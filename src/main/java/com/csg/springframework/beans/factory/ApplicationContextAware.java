package com.csg.springframework.beans.factory;

import com.csg.springframework.context.ApplicationContext;
import com.csg.springframework.exception.BeanException;

/**
 * 实现此接口，即能感知到所属的 ApplicationContext
 */
public interface ApplicationContextAware extends Aware {
    void setApplicationContext(ApplicationContext applicationContext) throws BeanException;
}
