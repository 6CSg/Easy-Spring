package com.csg.springframework.aop.framework.adapter;

import com.csg.springframework.aop.AfterAdvice;
import com.csg.springframework.aop.AfterReturningAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 方法拦截器，拦截方法后执行后置通知
 */
public class AfterReturningAdviceInterceptor implements MethodInterceptor, AfterAdvice {
    private AfterReturningAdvice advice;

    public AfterReturningAdviceInterceptor(AfterReturningAdvice advice) {
        this.advice = advice;
    }

    public AfterReturningAdviceInterceptor() {
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 先执行原方法
        Object ret = invocation.proceed();
        advice.after(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return ret;
    }
}
