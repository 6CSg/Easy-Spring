package com.csg.springframework.test.bean;

import com.csg.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * @author VictorG
 * @date 2023/3/13 23:51
 */
public class UserServiceAfterAdvice implements AfterReturningAdvice {

    @Override
    public void after(Method method, Object[] args, Object target) throws Throwable {
        System.out.println(method.getName() + "__拦截后，做一些后序处理");
    }
}
