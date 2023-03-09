package com.csg.springframework.beans.factory.config;

import com.csg.springframework.context.event.ApplicationEventMulticaster;

/**
 * bean注册的抽象类
 */
public interface SingletonBeanRegistry {
    Object getSingleton(String beanName);

    void registerSingleton(String beanName, Object singletonBean);
}
