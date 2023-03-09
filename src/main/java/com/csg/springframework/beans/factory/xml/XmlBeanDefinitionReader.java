package com.csg.springframework.beans.factory.xml;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.csg.springframework.beans.PropertyValue;
import com.csg.springframework.beans.factory.config.BeanDefinition;
import com.csg.springframework.beans.factory.config.BeanReference;
import com.csg.springframework.beans.factory.surpport.AbstractBeanDefinitionReader;
import com.csg.springframework.beans.factory.surpport.BeanDefinitionRegistry;
import com.csg.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import com.csg.springframework.core.io.Resource;
import com.csg.springframework.core.io.ResourceLoader;
import com.csg.springframework.exception.BeanException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    // 因为父类会先实例化，但父类没有提供构造方法，所以必须调用super()让父类正常初始化
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    @Override
    public void loadBeanDefinition(Resource resource) throws BeanException {
        try {
            // 获取xml文件的输入流
            InputStream inputStream = resource.getInputStream();
            // 根据xml加载Bean
            doLoadBeanDefinition(inputStream);
        } catch (DocumentException | IOException | ClassNotFoundException e) {
            throw new BeanException("IOException parsing XML document from " + resource + " " + e, e);
        }
    }

    @Override
    public void loadBeanDefinition(Resource... resource) throws BeanException {
        for (Resource r : resource) {
            loadBeanDefinition(r);
        }
    }

    @Override
    public void loadBeanDefinition(String location) throws BeanException {
        // 获取资源加载器
        ResourceLoader resourceLoader = getResourceLoader();
        // 获取资源对象
        Resource resource = resourceLoader.getResource(location);
        // 真正执行解析
        loadBeanDefinition(resource);
    }

    /**
     * 通过xml文件的输入流加载BeanDefinition，这是真正执行bean的加载
     */
    protected void doLoadBeanDefinition(InputStream inputStream) throws ClassNotFoundException, DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        Element root = document.getRootElement();
        // 解析 context:component-scan 标签，扫描包中的类并提取相关信息，用于组装 BeanDefinition
        Element componentScan = root.element("component-scan");
        if (null != componentScan) {
            String scanPath = componentScan.attributeValue("base-package");
            if (StrUtil.isEmpty(scanPath)) {
                throw new BeanException("The value of base-package attribute can not be empty or null");
            }
            scanPackage(scanPath);
        }
        List<Element> beanList = root.elements("bean");
        for (Element bean : beanList) {

            String id = bean.attributeValue("id");
            String name = bean.attributeValue("name");
            String className = bean.attributeValue("class");
            String initMethod = bean.attributeValue("init-method");
            String destroyMethodName = bean.attributeValue("destroy-method");
            String beanScope = bean.attributeValue("scope");

            // 获取 Class，方便获取类中的名称
            Class<?> clazz = Class.forName(className);
            // 优先级 id > name
            String beanName = StrUtil.isNotEmpty(id) ? id : name;
            if (StrUtil.isEmpty(beanName)) {
                beanName = StrUtil.lowerFirst(clazz.getSimpleName());
            }

            // 定义Bean
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            beanDefinition.setInitMethodName(initMethod);
            beanDefinition.setDestroyMethodName(destroyMethodName);

            if (StrUtil.isNotEmpty(beanScope)) {
                beanDefinition.setScope(beanScope);
            }

            List<Element> propertyList = bean.elements("property");
            // 读取属性并填充
            for (Element property : propertyList) {
                // 解析标签：property
                String attrName = property.attributeValue("name");
                String attrValue = property.attributeValue("value");
                String attrRef = property.attributeValue("ref");
                // 获取属性值：引入对象、值对象
                Object value = StrUtil.isNotEmpty(attrRef) ? new BeanReference(attrRef) : attrValue;
                // 创建属性信息
                PropertyValue propertyValue = new PropertyValue(attrName, value);
                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
            }
            if (getRegistry().containsBeanDefinition(beanName)) {
                throw new BeanException("Duplicate beanName[" + beanName + "] is not allowed");
            }
            // 注册 BeanDefinition
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }
    private void scanPackage(String scanPath) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(getRegistry());
        String[] basePackages = StrUtil.splitToArray(scanPath, ',');
        scanner.doScan(basePackages);
    }
    public void loadBeanDefinitions(String[] configLocations) {
        // 依次加载配置文件
        for (String configLocation : configLocations) {
            loadBeanDefinition(configLocation);
        }
    }
}
