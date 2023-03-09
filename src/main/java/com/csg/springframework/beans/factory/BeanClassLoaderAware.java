package com.csg.springframework.beans.factory;

/**
 * 实现此接口，即可感知到所属的ClassLoader
 */
public interface BeanClassLoaderAware extends Aware {
    void setBeanClassLoader(ClassLoader classLoader);
}
