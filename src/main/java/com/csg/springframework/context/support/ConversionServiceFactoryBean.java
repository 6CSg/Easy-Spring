package com.csg.springframework.context.support;

import com.csg.springframework.beans.factory.FactoryBean;
import com.csg.springframework.beans.factory.InitializingBean;
import com.csg.springframework.core.convert.ConversionService;
import com.csg.springframework.core.convert.converter.Converter;
import com.csg.springframework.core.convert.converter.ConverterFactory;
import com.csg.springframework.core.convert.converter.ConverterRegistry;
import com.csg.springframework.core.convert.converter.GenericConverter;
import com.csg.springframework.core.convert.support.DefaultConversionService;
import com.csg.springframework.core.convert.support.GenericConversionService;
import com.csg.springframework.exception.BeanException;
import com.sun.istack.Nullable;

import java.util.Set;

/**
 * 用于获取ConversionService的工厂Bean
 */
public class ConversionServiceFactoryBean implements FactoryBean<ConversionService>, InitializingBean {
    @Nullable
    private Set<?> converters;
    /**
     * conversionService对象在调用initMethod()过程被实例化
     */
    @Nullable
    private GenericConversionService conversionService;

    @Override
    public ConversionService getObject() throws BeanException {
        return conversionService;
    }

    @Override
    public Class<?> getObjectType() {
        return conversionService.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private void registerConverters(Set<?> converters, ConverterRegistry registry) {
        if (converters != null) {
            for (Object converter : converters) {
                // 一对一型converter
                if (converter instanceof Converter<?, ?>) {
                    registry.addConverter((Converter<?, ?>) converter);
                    // 一对多型converter
                } else if (converter instanceof ConverterFactory<?, ?>) {
                    registry.addConverterFactory((ConverterFactory<?, ?>) converter);
                    // 多对多型converter
                } else if (converter instanceof GenericConverter) {
                    registry.addConverter((GenericConverter) converter);
                } else {
                    throw new IllegalArgumentException("Each converter object must implement one of the " +
                            "Converter, ConverterFactory, or GenericConverter interfaces");
                }
            }
        }
    }
    public void setConverters(Set<?> converters) {
        this.converters = converters;
    }

    @Override
    public void afterPropertiesSet() throws BeanException {
        // System.out.println(converters);
        this.conversionService = new DefaultConversionService();
        // 用户实现的所有Converter都被放如了converters
        registerConverters(converters, conversionService);
    }
}
