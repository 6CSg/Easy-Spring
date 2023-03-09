package com.csg.springframework.aop;

import java.lang.reflect.Method;

/**
 * 前置通知接口，通知具体内容由用户实现，该接口定义的返回值为void，所以前置通知无法修改方法的返回值
 */
public interface MethodBeforeAdvice extends BeforeAdvice {
    void before(Method method, Object[] args, Object target) throws Throwable;
}
