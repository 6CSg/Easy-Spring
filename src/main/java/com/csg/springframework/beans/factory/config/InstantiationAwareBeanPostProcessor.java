package com.csg.springframework.beans.factory.config;

import com.csg.springframework.beans.PropertyValues;
import com.csg.springframework.exception.BeanException;

/**
 * 该接口的实现类专门用于Bean实例化前的前置处理
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    /**
     * 实例化之前执行的
     */
    Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeanException;

    PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeanException;

    boolean postProcessAfterInstantiation(Object bean, String beanName);

    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }
}
