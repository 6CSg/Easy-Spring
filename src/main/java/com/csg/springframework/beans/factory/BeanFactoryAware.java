package com.csg.springframework.beans.factory;

import com.csg.springframework.exception.BeanException;

/**
 * 实现此接口，即能感知到所属的 BeanFactory
 */
public interface BeanFactoryAware extends Aware {
    void setBeanFactory(BeanFactory beanFactory) throws BeanException;
}
