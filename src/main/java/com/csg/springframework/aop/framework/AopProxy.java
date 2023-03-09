package com.csg.springframework.aop.framework;

/**
 * 代理类接口，由具体的代理方式来实现
 */
public interface AopProxy {
    Object getProxy();
}
