package com.csg.springframework.aop.aspectj;

import com.csg.springframework.aop.ClassFilter;
import com.csg.springframework.aop.MethodMatcher;
import com.csg.springframework.aop.Pointcut;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 切点表达式类
 */
public class AspectJExpressionPointcut implements Pointcut, MethodMatcher, ClassFilter {
    //Set中放的是该切点支持的切点表达式类型
    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    static {
        // 支持execution表达式
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
    }
    private final PointcutExpression pointcutExpression;

    public AspectJExpressionPointcut(String expression) {
        // 获取切点表达式的解析器，该解析器只有能解析在SUPPORTED_PRIMITIVES中的切点表达式类型
        PointcutParser pointcutParser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(SUPPORTED_PRIMITIVES, this.getClass().getClassLoader());
        // 用切点解析器解析表达式，返回PointcutExpression对象
        this.pointcutExpression = pointcutParser.parsePointcutExpression(expression);
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return pointcutExpression.matchesMethodExecution(method).alwaysMatches();
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }
}
