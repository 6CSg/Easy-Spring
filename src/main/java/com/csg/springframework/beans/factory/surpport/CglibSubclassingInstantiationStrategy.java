package com.csg.springframework.beans.factory.surpport;

import com.csg.springframework.beans.factory.config.BeanDefinition;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.Constructor;

public class CglibSubclassingInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanDefinition.getBeanClass());
        enhancer.setCallback(new NoOp() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        });
        // 构造器为空，调用无参构造创建对象
        if (null == constructor) {
            return enhancer.create();
        }
        // 构造器不为空，传入参数类型和参数，创建对象
        return enhancer.create(constructor.getParameterTypes(), args);
    }
}
