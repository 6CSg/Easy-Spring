package com.csg.springframework.core.convert.converter;

import com.csg.springframework.exception.BeanException;

/**
 *  Converter接口，交给用户实现，用户在convert()中实现类型转换逻辑
 */
public interface Converter <S, T> {
    /** Convert the source object of type {@code S} to target type {@code T}. */
    T convert(S s) throws BeanException;
}
