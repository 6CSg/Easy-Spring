package com.csg.springframework.beans.factory.surpport;

import com.csg.springframework.beans.factory.config.BeanDefinition;

/**
 * 定义了注册功能，让BeanFactory来实现
 */
public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String name, BeanDefinition beanDefinition);

    boolean containsBeanDefinition(String beanName);
}
