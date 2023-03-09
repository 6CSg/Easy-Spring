package com.csg.springframework.context;

import com.csg.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.csg.springframework.exception.BeanException;

public interface ConfigurableApplicationContext extends ApplicationContext {
    /**
     * 刷新容器的方法
     * @throws BeanException
     */
    void refresh() throws BeanException;

    /**
     * 注册虚拟机关闭时的钩子方法
     */
    void registerShutdownHook();

    /**
     * 手动执行关闭的方法
     */
    void close();
}
