package com.csg.springframework.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectiveMethodInvocation implements MethodInvocation {
    protected final Object proxy;

    protected Object target;

    protected Method method;

    protected Object[] args;

    protected final Class<?> targetClass;
    // 方法过滤器集合
    protected final List<Object> interceptorsAndDynamicMethodMatchers;
    // 当前拦截器链的下标, 初始化为-1
    private int currentInterceptorIndex = -1;

    public ReflectiveMethodInvocation(Object proxy,Object target, Method method, Object[] arguments,Class<?> targetClass,List<Object> chain) {
        this.proxy=proxy;
        this.target = target;
        this.method = method;
        this.args = arguments;
        this.targetClass=targetClass;
        this.interceptorsAndDynamicMethodMatchers = chain;
    }

    public Object proceed() throws Throwable {
        // 当调用拦截器调用次数与拦截器个数相等时，执行目标方法
        if (this.currentInterceptorIndex == interceptorsAndDynamicMethodMatchers.size() - 1) {
            return method.invoke(this.target, this.args);
        }
        // 每调用一次proceed就把currentInterceptorIndex+1
        Object interceptorOrInterceptionAdvice =
                this.interceptorsAndDynamicMethodMatchers.get(++currentInterceptorIndex);
        return ((MethodInterceptor)interceptorOrInterceptionAdvice).invoke(this);
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return method;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return args;
    }
}
