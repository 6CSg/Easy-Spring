package com.csg.springframework.core.convert.converter;

/**
 * 工厂接口，用于获取Converter，一对多类型的转换器，1个sourceType，多个targetType
 */
public interface ConverterFactory<S, R> {
    /**
     * Get the converter to convert from S to target type T,
      where T is also an instance of R.
     * @param targetType
     * @return a converter from S to T
     * @param <T>: The target type to convert to
     */
    <T extends R> Converter<S, T> getConverter(Class<T> targetType);

}
