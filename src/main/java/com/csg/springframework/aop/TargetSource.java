package com.csg.springframework.aop;

import com.csg.springframework.util.ClassUtils;

/**
 * 封装被代理的目标对象
 */
public class TargetSource {
    private final Object target;

    public TargetSource(Object target) {
        this.target = target;
    }

    /**
     * 返回目标对象的类型，包括父类和接口类型
     */
    public Class<?>[] getTargetClass() {
        Class<?> clazz = this.target.getClass();
        // 由于ClassUtils.isCglibProxyClass(clazz)里有bug, 所以没有调用getSuperclass()
        clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;
        return clazz.getInterfaces();
    }

    public Object getTarget() {
        return this.target;
    }
}
