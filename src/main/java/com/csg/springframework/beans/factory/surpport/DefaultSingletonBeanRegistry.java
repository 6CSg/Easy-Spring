package com.csg.springframework.beans.factory.surpport;

import com.csg.springframework.beans.factory.DisableBean;
import com.csg.springframework.beans.factory.ObjectFactory;
import com.csg.springframework.beans.factory.config.SingletonBeanRegistry;
import com.csg.springframework.exception.BeanException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 该类就是用于缓存已经创建的对象的容器
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    protected static final Object NULL_OBJECT = new Object();
    /**
     * 一级缓存，存放普通对象
     */
    private Map<String, Object> singletonBeansMap = new ConcurrentHashMap<>();

    /**
     * 二级缓存，存放半成品对象，没有完全实例化的对象
     */
    protected final Map<String, Object> earlySingletonObjects = new HashMap<>();

    /**
     * 三级缓存，内部放一个可获取代理对象(如果需要创建代理对象，如果不需要，就是普通对象)函数
     *  当把对象从三级缓存移到二级缓存时(#getObject())，匿名函数被触发
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>();


    private Map<String, DisableBean> disableBeansMap = new HashMap<>();

    @Override
    public Object getSingleton(String beanName) {
        // 从一级缓存中获取普通对象
        Object singletonObject = singletonBeansMap.get(beanName);
        // 如果不存在，尝试从二级缓存中获取半成品对象
        if (null == singletonObject) {
            // 从二级缓存中获取半成品对象
            singletonObject = earlySingletonObjects.get(beanName);
            // 如果没有，从三级缓存中获取，三级缓存中的对象代理对象
            if (null == singletonObject) {
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (null != singletonFactory) {
                    // 把三级缓存中的代理对象中的真实对象取出来，放入二级缓存
                    // 当调用Object时，Lambda函数才被执行，类似于python里的闭包，返回值是函数内部的返回值
                    singletonObject = singletonFactory.getObject();
                    earlySingletonObjects.put(beanName, singletonObject);
                    // 将对象从三级缓存中移除
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    /**
     * 将成品对象放入singletonBeanMap
     * 将半成品对象和代理对象移除
     */
    @Override
    public void registerSingleton(String beanName, Object singletonBean) {
        singletonBeansMap.put(beanName, singletonBean);
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
    }

    // 用于缓存第一次创建的bean，即半成品对象
    protected void addSingleton(String beanName, Object singletonBean) {
        singletonBeansMap.put(beanName, singletonBean);
    }
    protected void registerDisposableBean(String beanName, DisableBean disabledBean) {
        disableBeansMap.put(beanName, disabledBean);
    }

    /**
     * 将代理对象加入三级缓存，并从二级缓存中移除
     *
     * @param singletonFactory : 一个lambda表达式标记，代表一个匿名内部类，即一个回调函数
     *  一个接口，该接口定义了一个函数：T getObject() throws BeanException，
     *  该函数有一个特点，无入参有返回值，
     *  所以在调用 addSingletonFactory()时，第二个入参可以是() -> getEarlyBeanReference(args...)的形式
     *  singletonFactories存放的是多个匿名类，这些匿名类是ObjectFactory的子类
     *  注意，只有当getObject()被调用时，封装在里面的lambda函数才会被调用
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        // 如果一级缓存中没有
        if (!singletonBeansMap.containsKey(beanName)) {
            // 放入三级缓存
            singletonFactories.put(beanName, singletonFactory);
            // 从二级缓存中移除
            earlySingletonObjects.remove(beanName);
        }
    }
    public void destroySingletons() {
        Set<String> keySet = this.disableBeansMap.keySet();
        Object[] disableBeanName = keySet.toArray();
        for (Object beanName : disableBeanName) {
            // System.out.println("--"+beanName);
            DisableBean disableBean = disableBeansMap.remove(beanName);
            if (null == disableBean) {
                throw new BeanException("you couldn't invoke registerShutdownHook method twice");
            }
            try {
                disableBean.destroy();
            } catch (Exception e) {
                throw new BeanException("Destroy method on bean with name '" + beanName + "' threw an exception", e);
            }
        }
    }
}
