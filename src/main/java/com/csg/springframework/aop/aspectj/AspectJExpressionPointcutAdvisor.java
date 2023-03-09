package com.csg.springframework.aop.aspectj;

import com.csg.springframework.aop.AfterAdvice;
import com.csg.springframework.aop.BeforeAdvice;
import com.csg.springframework.aop.Pointcut;
import com.csg.springframework.aop.PointcutAdvisor;
import org.aopalliance.aop.Advice;

/**
 * AspectJ切点表达式定义切点的切面：通知，切点，切点表达式都可以算作他的属性
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {
    // 切点
    private AspectJExpressionPointcut pointcut;
    // 前置通知，当设置属性时，其实是MethodBeforeInterceptor，并非用户实现MethodBeforeAdvice
    // 用户的实现类已经被包装进MethodBeforeInterceptor
    private Advice beforeAdvice;

    // 后置通知
    private Advice afterAdvice;

    // 表达式
    private String expression;


    @Override
    public Advice getAdvice() {
        return this.beforeAdvice;
    }

    @Override
    public Pointcut getPointcut() {
        if (null == pointcut) {
            pointcut = new AspectJExpressionPointcut(expression);
        }
        return pointcut;
    }

    public void setPointcut(AspectJExpressionPointcut pointcut) {
        this.pointcut = pointcut;
    }


    public void setExpression(String expression) {
        this.expression = expression;
    }

}
