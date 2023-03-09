package com.csg.springframework.aop;

import java.lang.reflect.Method;

/**
 * 后置通知
 */
public interface AfterReturningAdvice extends AfterAdvice {
    void after(Method method, Object[] args, Object target) throws Throwable;
}
