package com.csg.springframework.context.support;

import com.csg.springframework.beans.factory.ApplicationContextAware;
import com.csg.springframework.beans.factory.config.BeanPostProcessor;
import com.csg.springframework.context.ApplicationContext;
import com.csg.springframework.exception.BeanException;

/**
 * 由于ApplicationContext的获取并不能在创建bean时直接拿到，所以需要在 refresh 操作时，把 ApplicationContext 写入到一个包装的 BeanPostProcessor 中去，在applyBeanPostProcessorsBeforeInitialization()调用
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {
    private final ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    // 注意该方法会被反复执行，所以该方法一定要是幂等的
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeanException {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException {
        return bean;
    }
}
