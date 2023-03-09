package com.csg.springframework.beans.factory;

import com.csg.springframework.beans.PropertyValue;
import com.csg.springframework.beans.PropertyValues;
import com.csg.springframework.beans.factory.config.BeanDefinition;
import com.csg.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.csg.springframework.core.io.DefaultResourceLoader;
import com.csg.springframework.core.io.Resource;
import com.csg.springframework.exception.BeanException;
import com.csg.springframework.util.StringValueResolver;

import java.io.IOException;
import java.util.Properties;

/**
 * 在实例化之前执行，用于处理配spring.xml${xxx}的属性填充
 * 占位符处理配置类，BeanFactoryPostProcessor拓展接口的实现类，要在Xml中配置
 */
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    private String location;
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeanException {
        try {
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(location);
            // 包装配置信息
            Properties properties = new Properties();
            properties.load(resource.getInputStream());
            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanDefinitionNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                PropertyValues propertyValues = beanDefinition.getPropertyValues();
                for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                    Object value = propertyValue.getValue();
                    if (!(value instanceof String)) continue;
                    // 解析占位符，如果该属性值不存在占位符，则说明不需要填充
                    value = resolvePlaceholder(properties, (String) value);
                    propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), value));
                }
            }
            // 如果是写在配置文件内的，那么之前的逻辑已经处理完了，之后就是处理@Value了
            // 向容器里添加字段解析器，用于解析@Value
            PlaceholderResolvingStringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(properties);
            beanFactory.addEmbeddedValueResolver(valueResolver);

        } catch (IOException e) {
            throw new BeanException("Could not load properties", e);
        }
    }

    /**
     * 解析占位符的方法
     */
    private String resolvePlaceholder(Properties properties, String value) {
        String strVal = value;
        StringBuilder builder = new StringBuilder(strVal);
        int startIdx = builder.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
        int endIdx = builder.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);
        if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
            String propKey = builder.substring(startIdx + 2, endIdx);
            // 根据key去配置文件中找到具体的值
            String propVal = properties.getProperty(propKey);
            // 替换为具体的值
            builder.replace(startIdx, endIdx + 1, propVal);
        }
        return builder.toString();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 占位符解析器，用于解析@Value
     * 被嵌入在PropertyPlaceholderConfigurer中，可与beanPostProcessor共用resolvePlaceholder()
     */
    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {
        private final Properties properties;

        private PlaceholderResolvingStringValueResolver(Properties properties) {
            this.properties = properties;
        }

        @Override
        public String resolveStringValue(String strVal) {
            return PropertyPlaceholderConfigurer.this.resolvePlaceholder(properties, strVal);
        }
    }
}
