package com.csg.springframework.context.support;

import com.csg.springframework.beans.factory.surpport.DefaultListableBeanFactory;
import com.csg.springframework.exception.BeanException;

public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {
    // 聚合DefaultListableBeanFactory，相当于做一层封装
    // 临时感悟：要对class A抽象为class B，则在B中聚合A，B就有了A的大部分功能
    private DefaultListableBeanFactory beanFactory;

    @Override
    protected void refreshBeanFactory() throws BeanException {
        // 创建BeanFactory
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        // 加载BeanDefinition，此时配置文件已读取完毕，Bean的类型，属性信息已被放入HashMap中
        // 配置文件中包含了用户自定义的BeanPostProcessor和BeanFactoryPostProcessor
        loadBeanDefinitions(beanFactory);
        //
        this.beanFactory = beanFactory;
    }

    private DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }

    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory);

    @Override
    public DefaultListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
