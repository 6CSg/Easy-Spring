package com.csg.springframework.context;

/**
 * 事件发布者接口，所有事件都通过该接口发布出去
 */
public interface ApplicationEventPublisher {
    void publishEvent(ApplicationEvent event);
}
