package com.csg.springframework.beans.factory.surpport;

import cn.hutool.core.util.StrUtil;
import com.csg.springframework.beans.factory.DisableBean;
import com.csg.springframework.beans.factory.config.BeanDefinition;
import com.csg.springframework.exception.BeanException;

import java.lang.reflect.Method;

/**
 * 实现了DisableBean接口的对象 or 在spring.xml中定义了destroy-method的对象都将被封装为DisposableBeanAdapter
 */
public class DisposableBeanAdapter implements DisableBean {

    private final Object bean;
    private final Object beanName;
    private String destroyMethodName;

    public DisposableBeanAdapter(Object bean, Object beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = beanDefinition.getDestroyMethodName();
    }

    @Override
    public void destroy() throws Exception {
        // 如果实现接口DisableBean，直接调用用户自定义的destroy方法
        if (bean instanceof DisableBean) {
            ((DisableBean)bean).destroy();
        }

        // 如果通过配置信息 destroy-method，则通过反射调用执行，
        // 后面这个判断是防止二次销毁(既实现DisableBean又在spring.xml中定义了destroy-method，spring.xml中的destroy-method也叫destroy，那就与DisableBean接口中的destroy同名，相当于destroy()被调用两次)
        if (StrUtil.isNotEmpty(destroyMethodName) && !(bean instanceof DisableBean && "destroy".equals(this.destroyMethodName))) {
            Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
            if (null == destroyMethod) {
                throw new BeanException("couldn't find a destroyMethod named" + destroyMethodName);
            }
            destroyMethod.invoke(bean);
        }
    }
}
