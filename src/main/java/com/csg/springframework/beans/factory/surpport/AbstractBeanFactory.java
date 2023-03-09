package com.csg.springframework.beans.factory.surpport;

import cn.hutool.core.util.StrUtil;
import com.csg.springframework.beans.factory.BeanFactory;
import com.csg.springframework.beans.factory.FactoryBean;
import com.csg.springframework.beans.factory.config.BeanDefinition;
import com.csg.springframework.beans.factory.config.BeanPostProcessor;
import com.csg.springframework.beans.factory.config.ConfigurableBeanFactory;
import com.csg.springframework.core.convert.ConversionService;
import com.csg.springframework.exception.BeanException;
import com.csg.springframework.util.ClassUtils;
import com.csg.springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板类，第一次获取和创建Bean的入口，定义了创建Bean和获取BeanDefinition的方法
 * 定义了两个钩子方法，如果抽象类中的一个实例方法A调用了某个钩子方法B，则调用A的子类必须实现方法B
 */
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {
    // 聚合BeanPostProcessor
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    private List<StringValueResolver> embeddedValueResolvers = new ArrayList<>();

    // 聚合ConversionService
    private ConversionService conversionService;
    /**
     * 实现类BeanFactory中的两个getBean()方法
     */
    @Override
    public Object getBean(String beanName) throws BeanException{
        return doGetBean(beanName, null);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeanException {
        return (T) getBean(name);
    }
    @Override
    public Object getBean(String beanName, Object... args) throws Exception {
        return doGetBean(beanName, args);
    }

    protected <T> T doGetBean(final String beanName, final Object[] args) {
        // 用户通过实现FactoryBean接口创建的对象不存在于singletonMap，
        // 所以getSingleton()获取到的对象成为sharedBean，共享的，意味这在spring的singletonMap这个大容器中的Bean

        Object sharedInstance = getSingleton(beanName);
        // 如果对象已经创建，则先进入getObjectForBeanInstance()判断一波是否是FactoryBean
        if (null != sharedInstance) {
            // 获取FactoryBean的入口
            return (T) getObjectForBeanInstance(sharedInstance, beanName);
        }
        // 如果为空，说明：对象未创建 or 非单例，则执行之后的createBean()
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        // 在创建对象的过程中执行了用户自定义BeanPostProcessor方法
        // createBean()封装了实例化、前置处理、init-method/InitializingBean中的方法、后置处理、注册销毁(非单例不销毁)
        Object bean = createBean(beanName, beanDefinition, args);
        // 创建完Bean后，也要判断一波是否为FactoryBean
        // getObjectForBeanInstance():判断是否未FactoryBean，如果不是，返回原来的Bean
        // 如果是FactoryBean，则返回用户自定义的代理对象
        return (T) getObjectForBeanInstance(bean, beanName);
    }

    private Object getObjectForBeanInstance(Object beanInstance, String beanName) {
        // 判断createBean()返回的Bean是否是FactoryBean
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }
        // 执行到这，说明该Bean是FactoryBean
        // 从factoryBeanCache中获取Bean
        Object object = getCachedObjectForFactoryBean(beanName);
        // 说明该FactoryBean<T>创建的T类型对象未被放入缓存中
        if (object == null) {
            FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
            // 去传创建对象
            object = getObjectFromFactoryBean(factoryBean, beanName);
        }
        return object;
    }

    /**
     * 钩子方法，延迟到子类执行，被AbstractAutoWireCapableBeanFactory实例调用
     * 在这里要新增一个Object[] args参数，用于之后创建对象时判断是有参构造还是午餐构造
     * @return
     */
    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeanException;

    /**
     * 钩子方法，延迟到子类执行，被DefaultListableBeanFactory实现并调用
     * @param beanName
     * @return
     */
    protected abstract BeanDefinition getBeanDefinition(String beanName);

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }

    /**
     * 将BeanPostProcessor实例放入list中
     * @param beanPostProcessor
     */
    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        // 先试图移除beanPostProcessor
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    /**
     * 将解析嵌入进来
     * @param valueResolver
     * @throws BeanException
     */
    @Override
    public void addEmbeddedValueResolver(StringValueResolver valueResolver) throws BeanException {
        this.embeddedValueResolvers.add(valueResolver);
    }

    @Override
    public boolean containsBean(String beanName) {
        return containsBeanDefinition(beanName);
    }

    @Override
    public String resolveEmbeddedValue(String value) {
        String res = value;
        for (StringValueResolver resolver : this.embeddedValueResolvers) {
            res = resolver.resolveStringValue(value);
        }
        return res;
    }
    protected abstract boolean containsBeanDefinition(String beanName);
    public ConversionService getConversionService() {
        return conversionService;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }
}
