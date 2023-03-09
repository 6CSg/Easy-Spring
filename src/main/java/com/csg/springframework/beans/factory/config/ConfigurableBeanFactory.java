package com.csg.springframework.beans.factory.config;

import com.csg.springframework.beans.factory.BeanFactory;
import com.csg.springframework.beans.factory.HierarchicalBeanFactory;
import com.csg.springframework.beans.factory.PropertyPlaceholderConfigurer;
import com.csg.springframework.core.convert.ConversionService;
import com.csg.springframework.exception.BeanException;
import com.csg.springframework.util.StringValueResolver;
import com.sun.istack.internal.Nullable;

public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {
    String SCOPE_SINGLETON = "singleton";

    String SCOPE_PROTOTYPE = "prototype";

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    void destroySingletons();

    void addEmbeddedValueResolver(StringValueResolver valueResolver) throws BeanException;

    String resolveEmbeddedValue(String value);

    void setConversionService(ConversionService conversionService);

    /**
     * Return the associated ConversionService, if any.
     * @since 3.0
     */
    @Nullable
    ConversionService getConversionService();
}
