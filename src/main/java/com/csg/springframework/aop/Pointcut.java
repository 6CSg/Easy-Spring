package com.csg.springframework.aop;

/**
 * 切点的一个顶层抽象。
 * 切点运用于类和方法，当前切点具体作用于哪些类和方法，由ClassFilter和MethodMatcher来判断
 */
public interface Pointcut {
    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}
