package com.csg.springframework.beans.factory.surpport;

import com.csg.springframework.beans.factory.FactoryBean;
import com.csg.springframework.exception.BeanException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现FactoryBean注册服务
 */
public class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {
    private final Map<String, Object> factoryBeanCache = new ConcurrentHashMap<>();

    /**
     * 从缓存(factoryBeanCache)中获取Bean
     * @param beanName
     * @return
     */
    public Object getCachedObjectForFactoryBean(String beanName) {
        Object object = factoryBeanCache.get(beanName);
        return (object != NULL_OBJECT ? object : null);
    }

    public Object getObjectFromFactoryBean(FactoryBean factory, String beanName) {
        // 调用用户实现的isSingleton()，由用户来决定它是否是单例
        if (factory.isSingleton()) {
            // 如果是单例，则先尝试从缓存中获取单例Bean
            Object object = factoryBeanCache.get(beanName);
            // 如果为null，说明该单例Bean还未被放到factoryBeanCache中
            if (null == object) {
                object = doGetObjectFromFactoryBean(factory, beanName);
                // 将调用用户getObject()的返回值放入factoryBeanCache，如过返回null，则用NULL_OBJECT占位
                factoryBeanCache.put(beanName, (object != null ? object : NULL_OBJECT));
            }
            return (object != NULL_OBJECT ? object : null);
        } else {
            return doGetObjectFromFactoryBean(factory, beanName);
        }
    }

    private Object doGetObjectFromFactoryBean(final FactoryBean factory, final String beanName) {
        try {
            // 调用用户实现类中的getObject()，一般返回FactoryBean<T>模板类中，T接口实例的代理对象
            return factory.getObject();
        } catch (BeanException e) {
            throw new BeanException("FactoryBean threw exception on object[" + beanName + "] creation", e);
        }
    }
}
