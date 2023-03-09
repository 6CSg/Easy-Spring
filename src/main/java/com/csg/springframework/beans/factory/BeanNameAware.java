package com.csg.springframework.beans.factory;

import com.csg.springframework.exception.BeanException;

/**
 * 实现此接口，即能感知到所属的 BeanName
 */
public interface BeanNameAware extends Aware {
    void setBeanName(String beanName) throws BeanException;
}
