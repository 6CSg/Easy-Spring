package com.csg.springframework.beans.factory;

import com.csg.springframework.exception.BeanException;

/**
 * 一个对象工厂
 */
public interface ObjectFactory<T> {
    // 获取对象的方法
    T getObject() throws BeanException;
}
