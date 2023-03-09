package com.csg.springframework.context.annotation;

import cn.hutool.core.util.ClassUtil;
import com.csg.springframework.beans.factory.config.BeanDefinition;
import com.csg.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 对象的扫描装配类
 */
public class ClassPathScanningCandidateComponentProvider {
    /**
     * 获取指定包下带有@Component注解的类的BeanDefinition，此时BeanDefinition只包含最基础的类型信息
     * @param basePackage
     * @return
     */
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        LinkedHashSet<BeanDefinition> candidates = new LinkedHashSet<>();
        // 扫描指定包下的所有带有Component注解的类
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);
        for (Class<?> clazz : classes) {
            candidates.add(new BeanDefinition(clazz));
        }
        return candidates;
    }

}
