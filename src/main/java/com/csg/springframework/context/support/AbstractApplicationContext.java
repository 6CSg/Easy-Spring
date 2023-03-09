package com.csg.springframework.context.support;

import com.csg.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.csg.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.csg.springframework.beans.factory.config.BeanPostProcessor;
import com.csg.springframework.context.ApplicationEvent;
import com.csg.springframework.context.ApplicationListener;
import com.csg.springframework.context.ConfigurableApplicationContext;
import com.csg.springframework.context.event.ApplicationEventMulticaster;
import com.csg.springframework.context.event.ContextClosedEvent;
import com.csg.springframework.context.event.ContextRefreshedEvent;
import com.csg.springframework.context.event.SimpleApplicationEventMulticaster;
import com.csg.springframework.core.convert.ConversionService;
import com.csg.springframework.core.io.DefaultResourceLoader;
import com.csg.springframework.exception.BeanException;
import com.sun.xml.internal.ws.api.client.WSPortInfo;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {
    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";

    private ApplicationEventMulticaster applicationEventMulticaster;

    @Override
    public void refresh() throws BeanException {
        // 1. 创建BeanFactory，加载BeanDefinition
        // 此时BeanDefinition已经被放入beanDefinitionMap中了
        refreshBeanFactory();
        // 2. 获取BeanFactory，这个BeanFactory是在refreshBeanFactory()过程中创建的
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        // 3.添加 ApplicationContextAwareProcessor，让继承自 ApplicationContextAware 的 Bean 对象都能感知所属的 ApplicationContext
        // 也就是将当前ApplicationContext封装为ApplicationContextAwareProcessor
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        // 4. 在Bean实例化之前，实例化BeanFactoryPostProcessor，并执行postProcessBeanFactory()方法，此时已经完成用户自定义Bean的属性修改
        invokeBeanFactoryProcessors(beanFactory);
        // 5. BeanPostProcessors需要提前于其它Bean对象实例化之前进行注册操作，也就是将BeanPostProcessor实例放入到List<BeanPostProcessor>中
        registerBeanPostProcessors(beanFactory);
        // 6. 初始化事件发布者，将发布者实例化并交给Spring容器
        initApplicationEventMulticaster();
        // 7. 注册事件监听器，将所有listener实例化，并放入multicaster中的listener集合中
        registerListeners();
        // 8. 设置类型转换器、提前实例化单例Bean对象
        // preInstantiateSingletons()在设置类型转换器后被调用
        finishBeanFactoryInitialization(beanFactory);
        // 9.发布容器刷新完成事件
        finishRefresh();
    }

    /**
     * 实例化事件广播器，将广播器交给Spring容器
     */
    private void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, applicationEventMulticaster);
    }

    /**
     * 将监听器加入到Set<ApplicationListener<ApplicationEvent>>中去
     */
    private void registerListeners() {
        Collection<ApplicationListener> applicationListeners = getBeansOfType(ApplicationListener.class).values();
        for (ApplicationListener applicationListener : applicationListeners) {
            applicationEventMulticaster.addApplicationListener(applicationListener);
        }
    }

    private void finishRefresh() {
        publishEvent(new ContextRefreshedEvent(this));
    }
    @Override
    public void publishEvent(ApplicationEvent event) {
        applicationEventMulticaster.multicastEvent(event);
    }

    protected abstract void refreshBeanFactory() throws BeanException;

    protected abstract ConfigurableListableBeanFactory getBeanFactory() throws BeanException;

    private void invokeBeanFactoryProcessors(ConfigurableListableBeanFactory beanFactory) {
        // 通过类型获取Bean，在getBeansOfType()时BeanFactoryPostProcessor被实例化为Bean，并封装进Map
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = getBeansOfType(BeanFactoryPostProcessor.class);
        // 如果容器中没有BeanFactoryPostProcessor，则不会进入循环
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
            // 依次执行postProcessBeanFactory()
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        // 实例化BeanPostProcessor，并封装为Map
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        if (beanFactory.containsBean("conversionService")) {
            Object conversionService = beanFactory.getBean("conversionService");
            if (conversionService instanceof ConversionService) {
                beanFactory.setConversionService((ConversionService) conversionService);
            }
        }
        beanFactory.preInstantiateSingletons();
    }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void close() {
        // 发布容器关闭事件
        publishEvent(new ContextClosedEvent(this));
        getBeanFactory().destroySingletons();
    }

    @Override
    public boolean containsBean(String beanName) {
        return getBeanFactory().containsBean(beanName);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeanException {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public Object getBean(String beanName) {
        return getBeanFactory().getBean(beanName);
    }
    @Override
    public Object getBean(String name, Object... args) throws Exception {
        return getBeanFactory().getBean(name, args);
    }

    public <T> T getBean(String name, Class<T> requiredType) {
        return getBeanFactory().getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeanException {
        return getBeanFactory().getBean(requiredType);
    }

}
