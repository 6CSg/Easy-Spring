package com.csg.springframework.beans.factory.config;

import com.csg.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.csg.springframework.exception.BeanException;

public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeanException;
}
