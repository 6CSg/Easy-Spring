package com.csg.springframework.beans.factory;

import com.csg.springframework.exception.BeanException;

import java.util.Map;

public interface ListableBeanFactory extends BeanFactory {
    /**
     * 按照类型返回 Bean 实例
     * @param type
     * @return
     * @param <T>
     * @throws BeanException
     */
    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeanException;

    /**
     * 返回注册表中所有Bean的名称（返回工厂中所有Bean的名字）
     * @return
     */
    String[] getBeanDefinitionNames();
}
