package com.csg.springframework.beans.factory.config;

import com.csg.springframework.exception.BeanException;

public interface BeanPostProcessor {
    /**
     * 在Bean对象初始化之前执行此方法
     * @param bean
     * @param beanName
     * @return
     * @throws BeanException
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeanException;

    /**
     * 在Bean对象初始化之后执行此方法
     * @param bean
     * @param beanName
     * @return
     * @throws BeanException
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException;

}
