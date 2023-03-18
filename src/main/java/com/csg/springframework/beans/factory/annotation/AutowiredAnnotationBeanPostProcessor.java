package com.csg.springframework.beans.factory.annotation;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.csg.springframework.beans.PropertyValues;
import com.csg.springframework.beans.factory.BeanFactory;
import com.csg.springframework.beans.factory.BeanFactoryAware;
import com.csg.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.csg.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.csg.springframework.exception.BeanException;
import com.csg.springframework.util.ClassUtils;

import java.lang.reflect.Field;

public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeanException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    /**
     * 扫描bean中的Autowired/Qualifier/Value注解
     * 注意：在引入AOP时，该方法入参的bean必须目标对象，否则是获取不到类上的注解的
     */
    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeanException {
        // 获取到了所有属性信息
        Class<?> clazz = bean.getClass();
        clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Value valueAnnotation = field.getAnnotation(Value.class);
            if (null != valueAnnotation) {
                // 现在value时${xxx}的形式
                String value = valueAnnotation.value();
                // 通过${xxx}去配置文件中解析出真正的属性值
                value = beanFactory.resolveEmbeddedValue(value);
                BeanUtil.setFieldValue(bean, field.getName(), value);
            }
        }
        // 处理Autowired
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            Object dependentBean = null;
            if (field.isAnnotationPresent(Resource.class) && field.isAnnotationPresent(Autowired.class)) {
                throw new BeanException("Resource & Autowired can't be used on a field at one time！");
            } else if (field.isAnnotationPresent(Resource.class)) {
                Resource resourceAnnotation = field.getAnnotation(Resource.class);
                // 默认为字段名
                String dependentBeanName = field.getName();
                if (StrUtil.isNotEmpty(resourceAnnotation.name())) {
                    // 根据用户定义的名称获取
                    dependentBeanName = resourceAnnotation.name();
                }
                // 给bean赋值
                dependentBean = beanFactory.getBean(dependentBeanName);
                // 设置属性
                BeanUtil.setFieldValue(bean, field.getName(), dependentBean);
            } else if (field.isAnnotationPresent(Autowired.class)) {
                String dependentBeanName = null;
                Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                if (null != qualifierAnnotation) {
                    dependentBeanName = qualifierAnnotation.value();
                    // 获取依赖的对象实例
                    dependentBean = beanFactory.getBean(dependentBeanName, fieldType);
                } else {
                    dependentBean = beanFactory.getBean(fieldType);
                }
                BeanUtil.setFieldValue(bean, field.getName(), dependentBean);
            }
        }
        return pvs;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeanException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeanException {
        return null;
    }

}
