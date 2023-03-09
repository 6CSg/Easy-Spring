package com.csg.springframework.beans.factory;


import com.csg.springframework.exception.BeanException;

public interface InitializingBean {
    void afterPropertiesSet() throws BeanException;
}
