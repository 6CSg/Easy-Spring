package com.csg.springframework.core.convert.support;

import com.csg.springframework.core.convert.converter.Converter;
import com.csg.springframework.core.convert.converter.ConverterFactory;
import com.csg.springframework.exception.BeanException;
import com.csg.springframework.util.NumberUtils;
import com.sun.istack.Nullable;

/**
 * 具体的ConverterFactory，将String转为Number具体的子类
 */
public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {
    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumber<>(targetType);
    }

    private static final class StringToNumber<T extends Number> implements Converter<String, T> {
        private final Class<T> targetType;

        private StringToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        @Nullable
        public T convert(String source) throws BeanException {
            if (null == source) {
                return null;
            }
            return NumberUtils.parseNumber(source, this.targetType);
        }
    }
}
