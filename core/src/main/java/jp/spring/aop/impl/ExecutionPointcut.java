package jp.spring.aop.impl;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.spring.aop.ClassFilter;
import jp.spring.aop.MethodMatcher;
import jp.spring.aop.Pointcut;

/**
 * Created by Administrator on 1/18/2017.
 */
public class ExecutionPointcut implements Pointcut {

    public static final Pattern RULE_PATTERN = Pattern.compile("([a-zA-Z.*]*)\\(\\)");

    private Pattern classNamePattern;
    private Pattern methodNamePattern;

    public ExecutionPointcut(String expression) {
        Matcher rule_matcher = RULE_PATTERN.matcher(expression);
        if(!rule_matcher.find()) {
            throw new IllegalArgumentException("Error raised when parsing expression");
        }

        String aspectExpression = rule_matcher.group(1);
        int index = aspectExpression.lastIndexOf(".");
        if(index < 1 || index == aspectExpression.length() - 1) {
            throw new IllegalArgumentException("Error raised when parsing expression");
        }
        String classExp, methodExp;
        classExp = aspectExpression.substring(0, index);
        methodExp = aspectExpression.substring(index + 1);

        classExp = classExp.replace("*", "([\\w[^\\.]]*)");
        classExp = classExp.replace("..", "[\\w\\.]*");
        methodExp = methodExp.replace("*", "[\\w]*");
        classNamePattern = Pattern.compile("^" + classExp + "$");
        methodNamePattern = Pattern.compile(methodExp);
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodFilter() {
        return this;
    }

    @Override
    public boolean match(Class<?> cls) {
        return classNamePattern.matcher(cls.getName()).find();
    }

    @Override
    public boolean match(Method method) {
        return methodNamePattern.matcher(method.getName()).find();
    }
}
