package com.csg.springframework;

import com.csg.springframework.aop.AdvisedSupport;
import com.csg.springframework.aop.framework.AopProxy;
import com.csg.springframework.aop.framework.CglibAopProxy;
import com.csg.springframework.aop.framework.JdkDynamicAopProxy;

/**
 * 代理工厂，专门生成代理类
 */
public class ProxyFactory extends AdvisedSupport {

    public ProxyFactory() {}

    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    private AopProxy createAopProxy() {
        // 根据代理的配置信息创建代理对象，可用Jdk或Cglib，默认为Jdk
        if (this.isProxyTargetClass() || this.getTargetSource().getTargetClass().length == 0) {
            return new CglibAopProxy(this);
        }
        return new JdkDynamicAopProxy(this);
    }
}
