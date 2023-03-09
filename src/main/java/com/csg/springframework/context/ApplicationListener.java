package com.csg.springframework.context;

import java.util.EventListener;

/**
 * 该接口由用户实现，用户决定实现类监听的事件
 * @param <E>
 */
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E event);
}
