package com.csg.springframework.core.convert;


import com.sun.istack.Nullable;

/**
 * 类型转换服务
 */
public interface ConversionService {
    boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType);

    <T> T convert(Object source, Class<?> targetType);
}
