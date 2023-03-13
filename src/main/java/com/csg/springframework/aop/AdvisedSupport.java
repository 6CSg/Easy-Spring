package com.csg.springframework.aop;

import com.csg.springframework.aop.framework.AdvisorChainFactory;
import com.csg.springframework.aop.framework.DefaultAdvisorChainFactory;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 包装切面的通知信息
 * 把代理、拦截、匹配的各项属性包装到一个类中，方便再Proxy实现类中直接使用
 */
public class AdvisedSupport {
    // 是否使用Cglib动态代理，默认为true
    private boolean proxyTargetClass = true;

    // 被代理的目标对象
    private TargetSource targetSource;

    // 切面集合
    private List<Advisor> advisors = new ArrayList<>();
    // 方法匹配器，查看目标方法是否符合通知条件
    private MethodMatcher methodMatcher;
    // 拦截器中方法缓存
    private transient Map<Integer, List<Object>> methodCache;
    // 拦截器链工厂
    AdvisorChainFactory advisorChainFactory = new DefaultAdvisorChainFactory();

    public AdvisedSupport() {
        this.methodCache = new ConcurrentHashMap<>(32);
    }

    public List<Advisor> getAdvisors() {
        return advisors;
    }
    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }
    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    public void addAdvisor(Advisor advisor) {
        advisors.add(advisor);
    }
    // 获取当前方法的所有拦截器
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) {
        Integer cacheKey = method.hashCode();
        List<Object> cached = this.methodCache.get(cacheKey);
        if (null == cached) {
            List<Object> interceptors = advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(this, method, targetClass);
            cached = interceptors;
            this.methodCache.put(cacheKey, cached);
        }
        return cached;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

}
