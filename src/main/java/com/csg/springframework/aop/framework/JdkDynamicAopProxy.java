package com.csg.springframework.aop.framework;

import com.csg.springframework.aop.AdvisedSupport;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Jdk动态代理类
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private final AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }


    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(getClass().getClassLoader(), advised.getTargetSource().getTargetClass(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取目标对象
        Object target = advised.getTargetSource().getTarget();
        Class<?> targetClass = target.getClass();
        Object retVal = null;
        // 获取拦截器链
        List<Object> chain = advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
        // 无拦截器链，执行原方法
        if (null == chain || chain.isEmpty()) {
            method.invoke(target, args);
        } else {
            // 将拦截器链封装为ReflectiveMethodInvocation
            ReflectiveMethodInvocation reflectiveMethodInvocation2 =
                    new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
            // 执行拦截器链
            retVal = reflectiveMethodInvocation2.proceed();
        }
        return retVal;
    }
}
