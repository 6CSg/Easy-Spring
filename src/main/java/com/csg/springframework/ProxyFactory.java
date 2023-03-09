package com.csg.springframework;

import com.csg.springframework.aop.AdvisedSupport;
import com.csg.springframework.aop.framework.AopProxy;
import com.csg.springframework.aop.framework.Cglib2AopProxy;
import com.csg.springframework.aop.framework.JdkDynamicAopProxy;

/**
 * 代理工厂，专门生成代理类
 */
public class ProxyFactory {
    private AdvisedSupport advised;

    public ProxyFactory(AdvisedSupport advised) {
        this.advised = advised;
    }

    public Object getProxy() {
        return createAopProxy().getProxy();
    }
    private AopProxy createAopProxy() {
        // 根据代理的配置信息创建代理对象，可用Jdk或Cglib，默认为Jdk
        if (advised.isProxyTargetClass()) {
            return new Cglib2AopProxy(advised);
        }
        return new JdkDynamicAopProxy(advised);
    }
}
