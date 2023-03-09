package com.csg.springframework.context.event;

import com.csg.springframework.beans.factory.BeanFactory;
import com.csg.springframework.context.ApplicationEvent;
import com.csg.springframework.context.ApplicationListener;

public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {
    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }

    /**
     * 获取与event相匹配的监听器集合，依次调用这些listener的onApplicationEvent()方法，具体方法由用户实现
     * @param event
     */
    @Override
    public void multicastEvent(ApplicationEvent event) {
        for (ApplicationListener listener : getApplicationListeners(event)) {
            listener.onApplicationEvent(event);
        }
    }
}
