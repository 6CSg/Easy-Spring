package com.csg.springframework.util;

import java.lang.reflect.Field;

public class BeanUtil {
    /**
     * 给bean的属性中名为fieldName的属性注入value对象
     * @param bean：
     * @param fieldName；
     * @param value
     */
    public static void setFieldValue(Object bean, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field targetField = bean.getClass().getDeclaredField(fieldName);
        // 注意，私有属性一定要设置可访问
        targetField.setAccessible(true);
        targetField.set(bean, value);
    }

}
