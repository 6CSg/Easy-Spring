package com.csg.springframework.aop.framework.adapter;

import com.csg.springframework.aop.BeforeAdvice;
import com.csg.springframework.aop.MethodBeforeAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 方法前置拦截器，封装了前置通知
 */
public class MethodBeforeAdviceInterceptor implements MethodInterceptor, BeforeAdvice {

    private MethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor() {}

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // void before(Method method, Object[] args, Object target)
        // 调用 MethodBeforeAdvice接口 中的 before 方法，MethodBeforeAdvice接口由用户实现
        this.advice.before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return invocation.proceed();
    }
}
