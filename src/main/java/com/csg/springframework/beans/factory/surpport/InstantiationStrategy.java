package com.csg.springframework.beans.factory.surpport;

import com.csg.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

/**
 * 实例化策略接口：运用策略者模式
 */

public interface InstantiationStrategy {
    /**
     *
     * @param beanDefinition：bean的类型信息
     * @param beanName：bean的名称
     * @param constructor：java.lang.reflect 包下的 Constructor 类，里面包含了一些必要的类信息，
     *                   有这个参数的目的就是为了拿到符合入参信息相对应的构造函数。
     * @param args：具体入参信息
     * @return
     */
    Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args);
}
