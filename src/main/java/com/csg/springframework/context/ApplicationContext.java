package com.csg.springframework.context;

import com.csg.springframework.beans.factory.HierarchicalBeanFactory;
import com.csg.springframework.beans.factory.ListableBeanFactory;
import com.csg.springframework.core.io.ResourceLoader;

public interface ApplicationContext extends ListableBeanFactory, HierarchicalBeanFactory, ResourceLoader, ApplicationEventPublisher {

}
