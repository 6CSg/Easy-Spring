package com.csg.springframework.aop;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * 包装切面的通知信息
 * 把代理、拦截、匹配的各项属性包装到一个类中，方便再Proxy实现类中直接使用
 */
public class AdvisedSupport {
    // 被代理的目标对象
    private TargetSource targetSource;
    // 方法拦截器
    private MethodInterceptor methodInterceptor;

    // 方法匹配器，查看目标方法是否符合通知条件
    private MethodMatcher methodMatcher;

    // 代理配置信息
    private boolean proxyTargetClass = false;

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }


    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }
}
