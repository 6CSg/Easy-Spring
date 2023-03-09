package com.csg.springframework.test.converter;

import com.csg.springframework.beans.factory.FactoryBean;
import com.csg.springframework.exception.BeanException;
import com.csg.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 用于获取所有Converters的FactoryBean
 */
@Component("converters")
public class ConvertersFactoryBean implements FactoryBean<Set<?>> {

    @Override
    public Set<?> getObject() throws BeanException {
        Set<Object> converters = new HashSet<>();
        converters.add(new String2LocalDateConverter("yyyy-MM-dd"));
        converters.add(new String2IntConverter());
        return converters;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
