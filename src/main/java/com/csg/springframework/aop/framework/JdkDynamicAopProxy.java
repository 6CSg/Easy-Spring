package com.csg.springframework.aop.framework;

import com.csg.springframework.aop.AdvisedSupport;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
        /**
         * newProxyInstance的三个参数：
         *     1.ClassLoader loader：代理对象的类加载器
         *     2.Class<?>[] interfaces：被代理对象的接口
         *     3.InvocationHandler：代理对象实例
         */

        /**
         *  advised.getTargetSource().getTargetClass()深层有个bug，所以没有获取到接口，导致生成的代理对象无法与cast to IUserService
         */
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), advised.getTargetSource().getTargetClass(), this);
    }

    /**
     *  客户端获取代理对象，用代理对象调用被代理对象的方法后进入invoke()方法
     * @param proxy 真实的代理对象$Proxy0
     * @param method 被代理对象的被代理方法的Method对象
     * @param args 方法传递过来的参数
     */

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 如果被代理对象的方法能和切点表达式匹配上
        if (advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
            // 获取过滤器
            MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
            // 通过interceptor去调用方法，给invoke()方法传一个MethodInvocation，
            // 当执行了interceptor的增强逻辑之后，在invoke()方法内部会调用methodInvocation.proceed()，表示执行被增强的方法
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(), method, args));
        }
        // 没有切点表达式匹配，即不需要被代理，直接执行原方法
        return method.invoke(advised.getTargetSource().getTarget(), args);
    }
}
