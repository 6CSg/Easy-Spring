package com.csg.springframework.beans.factory.surpport;

import com.csg.springframework.core.io.Resource;
import com.csg.springframework.core.io.ResourceLoader;
import com.csg.springframework.exception.BeanException;

public interface BeanDefinitionReader {
    // 获取注册接口
    BeanDefinitionRegistry getRegistry();

    // 获取资源加载器
    ResourceLoader getResourceLoader();

    /**
     *三个加载Bean定义的方法
     */
    void loadBeanDefinition(Resource resource) throws BeanException;

    void loadBeanDefinition(Resource... resource) throws BeanException;

    void loadBeanDefinition(String location) throws BeanException;

}
