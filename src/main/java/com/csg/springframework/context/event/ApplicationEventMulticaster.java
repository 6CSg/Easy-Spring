package com.csg.springframework.context.event;

import com.csg.springframework.context.ApplicationEvent;
import com.csg.springframework.context.ApplicationListener;

/**
 * 事件广播器接口
 */
public interface ApplicationEventMulticaster {
    /**
     * 添加监听器
     * @param applicationListener
     */
    void addApplicationListener(ApplicationListener<?> applicationListener);

    /**
     * 移除监听器
     * @param applicationListener
     */
    void removeApplicationListener(ApplicationListener<?> applicationListener);

    /**
     * 将事件广播出去
     * @param event
     */
    void multicastEvent(ApplicationEvent event);
}
