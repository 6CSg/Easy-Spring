package com.csg.springframework.beans.factory;

import com.csg.springframework.exception.BeanException;

public interface BeanFactory {
    Object getBean(String beanName);

    // 新增一个获取Bean实例的方法
    Object getBean(String beanName, Object... args) throws Exception;

    <T> T getBean(String name, Class<T> requiredType) throws BeanException;

    <T> T getBean(Class<T> requiredType) throws BeanException;

    boolean containsBean(String name);

}
