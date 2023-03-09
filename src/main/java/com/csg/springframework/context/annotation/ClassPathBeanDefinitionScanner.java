package com.csg.springframework.context.annotation;

import cn.hutool.core.util.StrUtil;
import com.csg.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.csg.springframework.beans.factory.config.BeanDefinition;
import com.csg.springframework.beans.factory.surpport.BeanDefinitionRegistry;
import com.csg.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider{
    private BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }
    public void doScan(String... basePackages) {
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : candidates) {
                // 解析属性
                String beanScope = resolveBeanScope(beanDefinition);
                if (StrUtil.isNotEmpty(beanScope)) {
                    beanDefinition.setScope(beanScope);
                }
                registry.registerBeanDefinition(determineBeanName(beanDefinition), beanDefinition);
            }
        }
        registry.registerBeanDefinition("com.csg.springframework.context.annotation.internalAutowiredAnnotationProcessor", new BeanDefinition(AutowiredAnnotationBeanPostProcessor.class));
    }

    private String determineBeanName(BeanDefinition beanDefinition) {
        Class beanClass = beanDefinition.getBeanClass();
        Component component = (Component) beanClass.getAnnotation(Component.class);
        String val = component.value();
        // 如果注解中没有定义beanName, 那么直接取类名首字母小写
        if (StrUtil.isEmpty(val)) {
            val = StrUtil.lowerFirst(beanClass.getSimpleName());
        }
        return val;
    }

    private String resolveBeanScope(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 获取该类的Scope注解
        Scope scope = beanClass.getAnnotation(Scope.class);
        if (scope != null) {
            return scope.value();
        }
        return StrUtil.EMPTY;
    }
}
