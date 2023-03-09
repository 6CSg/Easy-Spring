package com.csg.springframework.beans.factory.surpport;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.csg.springframework.beans.PropertyValue;
import com.csg.springframework.beans.PropertyValues;
import com.csg.springframework.beans.factory.*;
import com.csg.springframework.beans.factory.config.BeanDefinition;
import com.csg.springframework.beans.factory.config.BeanPostProcessor;
import com.csg.springframework.beans.factory.config.BeanReference;
import com.csg.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.csg.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.csg.springframework.core.convert.ConversionService;
import com.csg.springframework.exception.BeanException;
import net.sf.cglib.core.TypeUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public abstract class AbstractAutoWireCapableBeanFactory extends AbstractBeanFactory {

    // 聚合策略者，根据入参类型来实例化
    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();
    private InstantiationStrategy jdkInstantiationStrategy = new SimpleInstantiationStrategy();

    /**
     * 注意：属性填充（applyPropertyValues）时，如果ref(引用类型属性)是BeanFactory类型，那么填充的实例就不一定是Spring.xml中定义的类型的实例，
     * 填充的实例的类型完全依赖于用户重写的getObject()返回的实例，比如在demo中按道理来说是应该是填充ProxyBeanFactory类型对象，而实际上填充的是IUserDao的代理对象
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) {
        Object bean = null;
            // 1. 实例化当前Bean之前判断是否返回代理对象
            // 该方法内部封装了创建代理对象的逻辑
            // 由于要为代理对象设置属性注入，将创建代理对象的时机延后到initializeBean(),这里都返回null
            bean = resolveBeforeInstantiation(beanName, beanDefinition);
            if (null != bean) {
                return bean;
            }
            return doCreateBean(beanName, beanDefinition, args);

    }
    protected Object doCreateBean(String beanName, BeanDefinition beanDefinition, Object[] args) {
        Object bean = null;
        try {
            // 2.实例化bean
            bean = createBeanInstance(beanName, beanDefinition, args);
            // 处理循环依赖，将实例化后的Bean对象提前放入缓存中暴露出来
            if (beanDefinition.isSingleton()) {
                Object finalBean = bean;
                // 第二个参数：如果当前对象不需代理，返回null，如果需代理，返回代理对象
                // 将返回的对象放入三级缓存
                // 第二个参数: 以lambda表达式的形式使用匿名内部类, 实际上匿名内部类已被实例化(被new出来)，只是我们看不见，所以当getObject()被调用时，返回值就是该lambda表达式的返回值, 即lambda表达式被执行
                // 相当于实现了ObjectFactory()接口，重写getObject()方法，返回null或代理对象
                addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, finalBean));
            }
            // 实例化后判断, 是否继续进行
            boolean continueWithPropertyPopulation = applyBeanPostProcessorsAfterInstantiation(beanName, bean);
            if (!continueWithPropertyPopulation) {
                return bean;
            }
            // 3.在设置 Bean 初始化之前，允许 BeanPostProcessor 修改属性值
            applyBeanPostProcessorsBeforeApplyingPropertyValues(beanName, bean, beanDefinition);
            // 4.通过BeanDefinition填充bean的属性
            applyPropertyValues(beanName, bean, beanDefinition);
            // 5.执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw  new BeanException("Instantiation of bean failed " + e, e);
        }

        // 注册实现了DisableBean的对象
        registerDisposableBeanIfNecessary(bean, beanName, beanDefinition);

        // 判断 SCOPE_SINGLETON、SCOPE_PROTOTYPE，如果是单例，才放进singletonBeansMap
        Object exposedBean = bean;
        if (beanDefinition.isSingleton()) {
            exposedBean = getSingleton(beanName);
            registerSingleton(beanName, exposedBean);
         }
        return exposedBean;
    }

    protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
        Object exposedObject = bean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            if (processor instanceof InstantiationAwareBeanPostProcessor) {
                exposedObject = ((InstantiationAwareBeanPostProcessor)processor).getEarlyBeanReference(exposedObject, beanName);
                // 如果不许创建代理对象，返回null
                if (null == exposedObject) return exposedObject;
            }
        }
        return exposedObject;
    }

    private boolean applyBeanPostProcessorsAfterInstantiation(String beanName, Object bean) {
        boolean continueWithPropertyPopulation = true;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            if (processor instanceof InstantiationAwareBeanPostProcessor) {
                // 强转
                InstantiationAwareBeanPostProcessor instantiationAwareBeanPostProcessor = (InstantiationAwareBeanPostProcessor) processor;
                if (!instantiationAwareBeanPostProcessor.postProcessAfterInstantiation(bean, beanName)) {
                    continueWithPropertyPopulation = false;
                    break;
                }
            }
        }
        return continueWithPropertyPopulation;
    }

    private void applyBeanPostProcessorsBeforeApplyingPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        for (BeanPostProcessor processor: getBeanPostProcessors()) {
            if (processor instanceof AutowiredAnnotationBeanPostProcessor) {
                PropertyValues pvs = ((InstantiationAwareBeanPostProcessor) processor).postProcessPropertyValues(beanDefinition.getPropertyValues(), bean, beanName);
                if (pvs != null) {
                    for (PropertyValue pv : pvs.getPropertyValues()) {
                        beanDefinition.getPropertyValues().addPropertyValue(pv);
                    }
                }
            }
        }
    }

    /**
     * 创建代理对象与属性填充
     */
    protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) {
        // 尝试创建代理对象，这里目前缺乏代理对象属性的填充
        // 所以我们先进行属性的填充，之后再创建对象实例
        Object bean = applyBeanPostProcessorsBeforeInstantiation(beanDefinition.getBeanClass(), beanName);
        if (null != bean) {
            // 代理对象创建成功，执行初始化后方法
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        }
        return bean;
    }

    /**
     * 创建代理对象
     */
    protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            // 如果实现了InstantiationAwareBeanPostProcessor接口，使用该processor
            if (processor instanceof InstantiationAwareBeanPostProcessor) {
                Object result = ((InstantiationAwareBeanPostProcessor)processor).postProcessBeforeInstantiation(beanClass, beanName);
                if (null != result) {
                    return result;
                }
            }
        }
        return null;
    }


    private void registerDisposableBeanIfNecessary(Object bean, String beanName, BeanDefinition beanDefinition) {
        // 如果不是单例，则不执行销毁操作
        if (!beanDefinition.isSingleton()) return;

        // 实现了DisableBean或者在spring.xml配置过destroy-method
        if (bean instanceof DisableBean || StrUtil.isNotEmpty(beanDefinition.getDestroyMethodName())) {
            registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
        }
    }
    // 在Bean的初始化流程中实现感知调用
    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        // invokeAwareMethods
        if (bean instanceof Aware) {
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware)bean).setBeanFactory(this);
            }
            if (bean instanceof BeanClassLoaderAware) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
            }
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
        }
        // 执行BeanPostProcessor Before 处理，用户定义的前置方法已经处理完成
        Object wrappedBean = null;
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        // 执行在spring.xml中配置的initMethod
        try {
            // 执行初始化方法，方法签名可以来自spring.xml或者实现InitializingBean
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (Exception e) {
            throw new BeanException("Invocation of init method of bean[" + beanName + "] failed", e);
        }
        // 2. 执行 BeanPostProcessor After 处理，如果需要创建代理对象，则wrappedBean为代理对象
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        return wrappedBean;
    }

    private Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            // 这里是真正执行用户自定义的前置处理方法，处理器中的前置方法将会被多次执行，所以用户在实现的必须幂等
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }

    private Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }

    private void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
        // 实现接口 InitializingBean
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }
        // spring.xml里的配置信息 init-method，防止afterPropertiesSet被执行两次
        String initMethodName = beanDefinition.getInitMethodName();
        if (StrUtil.isNotEmpty(initMethodName) && !(bean instanceof InitializingBean && "afterPropertiesSet".equals(beanDefinition.getInitMethodName()))) {
            Method initMethod = beanDefinition.getBeanClass().getMethod(initMethodName);
            if (null == initMethod) {
                throw new BeanException("Couldn't find an initMethod named :" + initMethodName + "' on bean with name '" + beanName + "'");
            }
            // 执行initMethod方法
            initMethod.invoke(bean);
        }
    }

    protected Object createBeanInstance(String beanName, BeanDefinition beanDefinition, Object[] args) {
        Constructor constructorToUse = null;
        Class beanClass = beanDefinition.getBeanClass();
        // 遍历该类的所有构造器
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        for (Constructor<?> ctor : declaredConstructors) {
            // 如果构造器的参数个数等于用户传入的参入个数，则选择此构造器
            if (null != args && ctor.getParameterTypes().length == args.length) {
                constructorToUse = ctor;
                break;
            }
        }
        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUse, args);
    }

    private void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        if (null == beanDefinition.getPropertyValues()) return;

        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            // 获取属性名
            String fieldName = propertyValue.getName();
            // 获取属性值
            Object value = propertyValue.getValue();
            // 如果是引用类型，需要先将引用类型实例化
            if (value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                // 递归调用getBean方法，如果时第一次创建，则创建Bean
                // 现在value已被实例化为真正的bean
                value = getBean(beanReference.getBeanName());
            } else {
                /**
                 * 类型转换服务
                 */
                // 配置文件中的类型
                Class<?> sourceType = value.getClass();
                // 对象属性的类型
                Class<?> targetType = (Class<?>) TypeUtil.getFieldType(bean.getClass(), fieldName);
                ConversionService conversionService = getConversionService();
                if (null != conversionService) {
                    if (conversionService.canConvert(sourceType, targetType)) {
                        // 类型转换
                        conversionService.convert(value, targetType);
                    }
                }
            }
            // 设置属性
            BeanUtil.setFieldValue(bean, fieldName, value);
        }
    }
    /**
     * getter & setter
     * @return
     */
    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

    public InstantiationStrategy getJdkInstantiationStrategy() {
        return jdkInstantiationStrategy;
    }

    public void setJdkInstantiationStrategy(InstantiationStrategy jdkInstantiationStrategy) {
        this.jdkInstantiationStrategy = jdkInstantiationStrategy;
    }

}
