package com.csg.springframework.beans.factory.config;
import com.csg.springframework.beans.PropertyValues;

/**
 *  负责定义spring容器中对象的类型
 *  Bean 的实例化操作放到容器中处理，而不是在定义时就处理
 */
public class BeanDefinition {
    // 单例Bean的标记
    String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

    // 默认为singleton
    private boolean singleton = true;

    // 原型Bean的标记
    String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

    private boolean prototype = false;

    // 默认为单例
    private String scope = SCOPE_SINGLETON;

    // 定义Bean的类型信息
    private Class beanClass;

    // 定义Bean的属性信息
    private PropertyValues propertyValues;

    // Bean初始化方法名
    private String initMethodName;

    // Bean的销毁方法名
    private String destroyMethodName;

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }



    public boolean isSingleton() {
        return singleton;
    }
    public BeanDefinition(Class beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        this.propertyValues = propertyValues;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
        this.propertyValues = new PropertyValues();
    }

    public PropertyValues getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }


    public String getSCOPE_SINGLETON() {
        return SCOPE_SINGLETON;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
        this.singleton = SCOPE_SINGLETON.equals(scope);
        this.prototype = SCOPE_PROTOTYPE.equals(scope);
    }

    public void setSCOPE_SINGLETON(String SCOPE_SINGLETON) {
        this.SCOPE_SINGLETON = SCOPE_SINGLETON;
    }



    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public String getSCOPE_PROTOTYPE() {
        return SCOPE_PROTOTYPE;
    }

    public void setSCOPE_PROTOTYPE(String SCOPE_PROTOTYPE) {
        this.SCOPE_PROTOTYPE = SCOPE_PROTOTYPE;
    }

    public boolean isPrototype() {
        return prototype;
    }

    public void setPrototype(boolean prototype) {
        this.prototype = prototype;
    }

}
