package com.csg.springframework.core.convert.support;

import com.csg.springframework.core.convert.converter.ConverterRegistry;

/**
 * 扩展 GenericConversionService，注册了一批默认的转换器。
 */
public class DefaultConversionService extends GenericConversionService {

    public DefaultConversionService() {
        addDefaultConverters(this);
    }

    public static void addDefaultConverters(ConverterRegistry converterRegistry) {
        // 添加各个类型的转换器工厂
        converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
    }

}
