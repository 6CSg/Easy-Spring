package com.csg.springframework.beans.factory;

import com.csg.springframework.beans.factory.config.AutoWireCapableBeanFactory;
import com.csg.springframework.beans.factory.config.BeanDefinition;
import com.csg.springframework.beans.factory.config.ConfigurableBeanFactory;
import com.csg.springframework.exception.BeanException;

public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutoWireCapableBeanFactory, ConfigurableBeanFactory {
    BeanDefinition getBeanDefinition(String beanName) throws BeanException;

    void preInstantiateSingletons() throws BeanException;

}
