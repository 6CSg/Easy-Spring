package com.csg.springframework.aop.framework.autoproxy;

import com.csg.springframework.ProxyFactory;
import com.csg.springframework.aop.*;
import com.csg.springframework.aop.aspectj.AspectJExpressionPointcut;
import com.csg.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import com.csg.springframework.beans.PropertyValues;
import com.csg.springframework.beans.factory.BeanFactory;
import com.csg.springframework.beans.factory.BeanFactoryAware;
import com.csg.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.csg.springframework.beans.factory.surpport.DefaultListableBeanFactory;
import com.csg.springframework.exception.BeanException;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 创建者：实现代理对象自动功能，本质上是一个后置处理器BeanPostProcessor，同时可以被感知
 */
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;
    // 实例化之前的后置处理，有几个对象，该方法就被调用几次

    private final Set<Object> earlyProxyReferences = Collections.synchronizedSet(new HashSet<>());

    /**
     * 方法已迁移
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeanException {
        return null;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeanException {
        return pvs;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }

    // 初始化之前的后置处理
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeanException {
        return bean;
    }

    /**
     * 将创建代理对象的逻辑迁移过来
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException {
        if (!earlyProxyReferences.contains(beanName)) {
            return wrapIfNecessary(bean, beanName);
        }
        return bean;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeanException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    /**
     * 判断该类是否是通知类型或者是切点类型
     */
    public boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass) ||
                Pointcut.class.isAssignableFrom(beanClass) ||
                Advisor.class.isAssignableFrom(beanClass);
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) {
        // 将beanName先缓存到earlyProxyReferences中，然后判断是否需要获取代理对象，如果需要，则创建
        earlyProxyReferences.add(beanName);
        return wrapIfNecessary(bean, beanName);

    }

    /**
     * 判断是否需要创建代理对象，如果需要则创建
     */
    protected Object wrapIfNecessary(Object bean, String beanName) {
        // 如果是Advice/Pointcut/Advisor，则不处理，否则StackOverflow，因为后面会递归调用getBeansOfType()
        // 一直爆栈，没把握好递归出口
        if (isInfrastructureClass(bean.getClass())) return bean;
        // 通过注册信息中实例化并获取所有切点表达式的访问者
        Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class)
                .values();
        // 让所有切点表达式访问者们去判断当前类是否需要创建代理对象
        try {
            ProxyFactory factory = new ProxyFactory();
            for (AspectJExpressionPointcutAdvisor advisor : advisors) {
                ClassFilter classFilter = advisor.getPointcut().getClassFilter();
                // 如果与当前类匹配不上，说明当前类中的方法不需要增强
                if (!classFilter.matches(bean.getClass())) continue;
//                AdvisedSupport advisedSupport = new AdvisedSupport();
                TargetSource targetSource = new TargetSource(bean);
//                advisedSupport.setTargetSource(targetSource);
//                advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
//                advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
                factory.setTargetSource(targetSource);
                factory.addAdvisor(advisor);
                factory.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
            }
            if (!factory.getAdvisors().isEmpty()) {
                return factory.getProxy();
            }
        } catch (Exception e) {
            throw new BeanException("Error create proxy bean for: " + beanName, e);
        }
        return bean;
    }
}