package com.csg.springframework.aop;

/**
 * 具有切点的切面
 */
public interface PointcutAdvisor extends Advisor {
    Pointcut getPointcut();
}
