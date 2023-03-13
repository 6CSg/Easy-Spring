package com.csg.springframework.aop.framework;
import com.csg.springframework.aop.AdvisedSupport;
import com.csg.springframework.aop.Advisor;
import com.csg.springframework.aop.MethodMatcher;
import com.csg.springframework.aop.PointcutAdvisor;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VictorG
 * @date 2023/3/13 23:26
 */


/**
 * 拦截器链工厂具体实现类：用于获取拦截器链
 */
public class DefaultAdvisorChainFactory implements AdvisorChainFactory {
    /**
     *
     * @param config：切面
     * @param method：方法
     * @param targetClass：代理类
     * @return：目标类的拦截器（通知）集合
     */
    @Override
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(AdvisedSupport config, Method method, Class<?> targetClass) {
        Advisor[] advisors = config.getAdvisors().toArray(new Advisor[0]);
        // 过滤器集合
        List<Object> interceptorList = new ArrayList<>(advisors.length);
        // 真实的目标类
        Class<?> actualClass = (targetClass != null ? targetClass :method.getDeclaringClass());
        for (Advisor advisor : advisors) {
            if (advisor instanceof PointcutAdvisor) {
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor)advisor;
                // 如果当前切面能匹配当前类
                if (pointcutAdvisor.getPointcut().getClassFilter().matches(targetClass)) {
                    // 获取当前切面的方法匹配器
                    MethodMatcher matcher = pointcutAdvisor.getPointcut().getMethodMatcher();
                    // 如果能匹配当前方法
                    if (matcher.matches(method, actualClass)) {
                        MethodInterceptor interceptor = (MethodInterceptor) advisor.getAdvice();
                        // 将拦截器放入到拦截器集合中
                        interceptorList.add(interceptor);
                    }
                }
            }
        }
        return interceptorList;
    }
}
