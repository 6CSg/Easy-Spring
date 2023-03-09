package com.csg.springframework.context.support;

import com.csg.springframework.beans.factory.surpport.DefaultListableBeanFactory;
import com.csg.springframework.beans.factory.xml.XmlBeanDefinitionReader;

public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        // 获取bean资源解析器：XmlBeanDefinitionReader
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        // 调用钩子方法，获取文件路径
        String[] configLocations = getConfigLocations();
        if (null != configLocations) {
            // 开始解析配置文件
            xmlBeanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }
    protected abstract String[] getConfigLocations();
}
