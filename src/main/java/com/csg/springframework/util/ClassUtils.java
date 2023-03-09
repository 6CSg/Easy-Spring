package com.csg.springframework.util;

public class ClassUtils {
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (null == cl) {
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    public static boolean isCglibProxyClass(Class<?> clazz) {
        // isCglibProxyClassName里有个bug, 返回false
        return (clazz != null && isCglibProxyClassName(clazz.getName()));
    }

    private static boolean isCglibProxyClassName(String name) {
        return (null != name && name.contains("$$"));
        // 超级大BUG 操你妈，返回false
        // return (null != name && name.startsWith("$$"));
    }
}

