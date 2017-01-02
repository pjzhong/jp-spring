package jp.spring.ioc.aop;

import jp.spring.ioc.aop.pointcut.AspectJExpressionPointcut;
import jp.spring.ioc.aop.pointcut.Pointcut;
import org.aopalliance.aop.Advice;

/**
 * Created by Administrator on 12/27/2016.
 * Container the PointCut and the advisor
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {

    private AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

    private Advice advice;

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    public void setExpression(String expression) {
        this.pointcut.setExpression(expression);
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }
}
