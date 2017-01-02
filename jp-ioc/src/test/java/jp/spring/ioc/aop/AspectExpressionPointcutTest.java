package jp.spring.ioc.aop;

import jp.spring.ioc.HelloService;
import jp.spring.ioc.aop.pointcut.AspectJExpressionPointcut;
import org.junit.Test;

/**
 * Created by Administrator on 12/27/2016.
 */
public class AspectExpressionPointcutTest {

    @Test
    public void testClassFilter() throws Exception {
        String expression = "execution(* com.GF.*.*(..))";
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(expression);
        boolean matches = aspectJExpressionPointcut.getClassFilter().matches(HelloService.class);
        System.out.println(matches);
    }

    @Test
    public void testMethodInterceptor() throws Exception {
        String expression = "execution(* jp.spring.ioc.*.*(..))";
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(expression);
        boolean matches = aspectJExpressionPointcut.getMethodMatcher().matches(HelloService.class.getDeclaredMethod("helloWorld"), HelloService.class);
        System.out.println(matches);
    }
}
