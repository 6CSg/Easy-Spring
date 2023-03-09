package com.csg.springframework.beans.factory.surpport;

import com.csg.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.csg.springframework.beans.factory.config.BeanDefinition;
import com.csg.springframework.exception.BeanException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultListableBeanFactory extends AbstractAutoWireCapableBeanFactory implements BeanDefinitionRegistry, ConfigurableListableBeanFactory {
    Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeanException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) throw new BeanException("No bean named '" + beanName + "' is defined");
        return beanDefinition;
    }

    @Override
    public void preInstantiateSingletons() throws BeanException {
        // 依次实例化所有未实例化的Bean
        // 注意，如果beanDefinition中有FactoryBean的定义，实例化的将是代理对象
        beanDefinitionMap.keySet().forEach(this::getBean);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(final Class<T> type) throws BeanException {
        final Map<String, T> result = new HashMap<>();
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            Class beanClass = beanDefinition.getBeanClass();
            // 如果指定类型能和beanDefinitionMap中的类型匹配上，则实例化Bean并放入Map中
            if (type.isAssignableFrom(beanClass)) {
                result.put(beanName, (T) getBean(beanName));
            }
        });
        return result;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }
    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }
    /**
     * 根据类型获取对象实例
     */
    @Override
    public <T> T getBean(Class<T> requiredType) throws BeanException {
        List<String> beanNames = new ArrayList<>();
        // 获取所有BeanDefinition
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            // 获取类型
            Class beanClass = entry.getValue().getBeanClass();
            // 如果类型匹配上
            if (requiredType.isAssignableFrom(beanClass)) {
                beanNames.add(entry.getKey());
            }
        }
        if (1 == beanNames.size()) {
            return getBean(beanNames.get(0), requiredType);
        }

        throw new BeanException(requiredType + "expected single bean but found " + beanNames.size() + ": " + beanNames);
    }
}
