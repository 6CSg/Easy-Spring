package com.csg.springframework.beans.factory;

import com.csg.springframework.exception.BeanException;

public interface FactoryBean<T> {
    T getObject() throws BeanException;

    Class<?> getObjectType();

    boolean isSingleton();
}
