package com.csg.springframework.core.convert.support;

import com.csg.springframework.core.convert.ConversionService;
import com.csg.springframework.core.convert.converter.Converter;
import com.csg.springframework.core.convert.converter.ConverterFactory;
import com.csg.springframework.core.convert.converter.ConverterRegistry;
import com.csg.springframework.core.convert.converter.GenericConverter;
import com.csg.springframework.exception.BeanException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 实现了 ConfigurableConversionService 接口，
 * Spring 使用的 ConversionService 都是基于这个类的扩展。
 */
public class GenericConversionService implements ConversionService, ConverterRegistry {
    /**
     * 存放target&source pair 和 它们的转换器
     */
    private Map<GenericConverter.ConvertiblePair, GenericConverter> converters = new HashMap<>();

    @Override
    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        GenericConverter converter = getConverter(sourceType, targetType);
        return converter != null;
    }

    @Override
    public <T> T convert(Object source, Class<?> targetType) {
        Class<?> sourceType = source.getClass();
        GenericConverter converter = getConverter(sourceType, targetType);
        Object target = converter.convert(source, sourceType, targetType);
        return (T) target;
    }

    @Override
    public void addConverter(Converter<?, ?> converter) throws BeanException {
        // 获取ConvertiblePair对象
        GenericConverter.ConvertiblePair typeInfo = getRequiredTypeInfo(converter);
        // 创建适配器
        ConverterAdapter converterAdapter = new ConverterAdapter(typeInfo, converter);
        for (GenericConverter.ConvertiblePair convertibleType : converterAdapter.getConvertibleTypes()) {
            converters.put(convertibleType, converterAdapter);
        }
    }

    /**
     * 根据当前对象的泛型类型判断sourceType和 resourceType, 然后创建ConvertiblePair返回
     */
    private GenericConverter.ConvertiblePair getRequiredTypeInfo(Object obj) {
        Type[] types = obj.getClass().getGenericInterfaces();
        // 转为泛型类
        ParameterizedType parameterized = (ParameterizedType)types[0];
        // 获取泛型类的泛型参数类型，比如A<T>,A是一个ParameterizedType， 返回T
        Type[] actualTypeArguments = parameterized.getActualTypeArguments();
        Class sourceType = (Class) actualTypeArguments[0];
        Class targetSource = (Class) actualTypeArguments[1];
        return new GenericConverter.ConvertiblePair(sourceType, targetSource);
    }

    @Override
    public void addConverter(GenericConverter genericConverter) throws BeanException {
        for (GenericConverter.ConvertiblePair convertibleType : genericConverter.getConvertibleTypes()) {
            converters.put(convertibleType, genericConverter);
        }
    }

    @Override
    public void addConverterFactory(ConverterFactory<?, ?> converterFactory) throws BeanException {
        GenericConverter.ConvertiblePair typeInfo = getRequiredTypeInfo(converterFactory);
        // 将ConverterFactory对象做一个适配，然后一放进converters
        ConverterFactoryAdapter converterFactoryAdapter = new ConverterFactoryAdapter(typeInfo, converterFactory);
        for (GenericConverter.ConvertiblePair convertibleType : converterFactoryAdapter.getConvertibleTypes()) {
            converters.put(convertibleType, converterFactoryAdapter);
        }
    }

    /**
     * 从所有converters中找到匹配的converter
     */
    protected GenericConverter getConverter(Class<?> sourceType, Class<?> targetType) {
        List<Class<?>> sourceCandidates = getClassHierarchy(sourceType);
        List<Class<?>> targetCandidates = getClassHierarchy(targetType);
        for (Class<?> sourceCandidate : sourceCandidates) {
            for (Class<?> targetCandidate : targetCandidates) {
                GenericConverter.ConvertiblePair convertiblePair = new GenericConverter.ConvertiblePair(sourceCandidate, targetCandidate);
                GenericConverter converter = converters.get(convertiblePair);
                if (converter != null) {
                    return converter;
                }
            }
        }
        return null;
    }

    /**
     * hierarchy: 层级，等级制度
     * 获取当前类及其所有父类
     */
    private List<Class<?>> getClassHierarchy(Class<?> clazz) {
        List<Class<?>> hierarchy = new ArrayList<>();
        while (clazz != null) {
            hierarchy.add(clazz);
            clazz = clazz.getSuperclass();
        }
        return hierarchy;
    }
    private final class ConverterAdapter implements GenericConverter {
        /**
         * (target & source) pair
         */
        private final ConvertiblePair typeInfo;

        private final Converter<Object, Object> converter;

        public ConverterAdapter(GenericConverter.ConvertiblePair typeInfo, Converter<?, ?> converter) {
            this.typeInfo = typeInfo;
            this.converter = (Converter<Object, Object>) converter;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(typeInfo);
        }

        @Override
        public Object convert(Object source, Class<?> sourceType, Class<?> targetType) {
            return converter.convert(source);
        }
    }

    /**
     * ConverterFactory的适配器
     * 适配器：实现要适配的类/接口，聚合被适配对象
     */
    private final class ConverterFactoryAdapter implements GenericConverter {
        private final ConvertiblePair typeInfo;

        private final ConverterFactory<Object, Object> converterFactory;

        public ConverterFactoryAdapter(ConvertiblePair typeInfo, ConverterFactory<?, ?> converterFactory) {
            this.typeInfo = typeInfo;
            this.converterFactory = (ConverterFactory<Object, Object>) converterFactory;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(typeInfo);
        }

        @Override
        public Object convert(Object source, Class<?> sourceType, Class<?> targetType) {
            return converterFactory.getConverter(targetType).convert(source);
        }
    }
}
