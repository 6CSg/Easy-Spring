package com.csg.springframework.aop;

import org.aopalliance.aop.Advice;

/**
 * 顾问，封装了切点和通知，就是我们常用的@Aspect 注解标记的类。
 * 该接口的实现类都是切面
 */
public interface Advisor {
    Advice getAdvice();
}
