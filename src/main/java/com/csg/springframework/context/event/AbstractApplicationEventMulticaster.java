package com.csg.springframework.context.event;

import com.csg.springframework.beans.factory.BeanFactory;
import com.csg.springframework.beans.factory.BeanFactoryAware;
import com.csg.springframework.context.ApplicationEvent;
import com.csg.springframework.context.ApplicationListener;
import com.csg.springframework.exception.BeanException;
import com.csg.springframework.util.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware {
    // 用LinkedHashSet的原因
    public final Set<ApplicationListener<ApplicationEvent>> applicationListeners = new LinkedHashSet<>();

    public BeanFactory beanFactory;

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.add((ApplicationListener<ApplicationEvent>) listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.remove(listener);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeanException {
        this.beanFactory = beanFactory;
    }

    /**
     * 从applicationListeners中获取当前event相匹配的所有listener，
     * 也就是说event的类型要和listener的泛型类型相同
     */
    protected Collection<ApplicationListener> getApplicationListeners(ApplicationEvent event) {
        LinkedList<ApplicationListener> allListeners = new LinkedList<>();
        for (ApplicationListener<ApplicationEvent> listener : applicationListeners) {
            // 在supportEvent()中进行过滤
            if (supportsEvent(listener, event)) {
                allListeners.add(listener);
            }
        }
        return allListeners;
    }

    /**
     * 判断listener和event是否有关联关系
     */
    private boolean supportsEvent(ApplicationListener<ApplicationEvent> listener, ApplicationEvent event) {
        // 获取用户实现的监听器的类型，如果实例化策略是Cglib，则实际上是监听器的代理类
        Class<? extends ApplicationListener> listenerClass = listener.getClass();
        // 获取监听器的实际类型
        // 如果是JDK的代理类，则返回其父类
        Class<?> targetClass = ClassUtils.isCglibProxyClass(listenerClass) ? listenerClass.getSuperclass() : listenerClass;
        // 获取listener的接口类型
        Type genericInterface = targetClass.getGenericInterfaces()[0];
        // ParameterizedType：带有参数的类型，即泛型类
        // 将接口强转为泛型类，获取listener所监听事件的具体类型
        Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
        // 获取listener所监听事件的具体类型的className
        String className = actualTypeArgument.getTypeName();
        Class<?> eventClass;
        try {
            // 获取listener所监听事件的具体类型的Class对象
            eventClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BeanException("wrong event class name: " + className);
        }
        // isAssignableFrom是用来判断子类和父类或者接口与实现类关系的，
        // 如果A.isAssignableFrom(B)结果是true，证明B可以转换成为A
        // 如果event的class和ApplicationListener<T>中的T的class有关联关系，说明二者匹配上了
        return eventClass.isAssignableFrom(event.getClass());

    }
}
