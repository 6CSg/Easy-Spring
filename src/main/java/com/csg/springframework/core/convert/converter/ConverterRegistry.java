package com.csg.springframework.core.convert.converter;

import com.csg.springframework.exception.BeanException;

/**
 * Converter的注册接口，将ConvertiblePair和converter封装为<k,v></k,v>放入converters中
 * 三种类型的转换器的添加和删除
 */
public interface ConverterRegistry {
    /**
     * 一对一类型的转换器的增加
     */
    void addConverter(Converter<?, ?> converter) throws BeanException;

    /**
     * 多对多类型转换器的添加
     */
    void addConverter(GenericConverter genericConverter) throws BeanException;

    /**
     * 一对多类型转换器的添加
     */
    void addConverterFactory(ConverterFactory<?, ?> converterFactory) throws BeanException;
}
