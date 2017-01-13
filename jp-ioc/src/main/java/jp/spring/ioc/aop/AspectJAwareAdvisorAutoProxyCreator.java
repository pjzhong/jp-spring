package jp.spring.ioc.aop;

import jp.spring.ioc.aop.proxy.ProxyFactory;
import jp.spring.ioc.beans.BeanPostProcessor;
import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.factory.BeanFactory;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.List;

/**
 * Created by Administrator on 12/27/2016.
 */
public class AspectJAwareAdvisorAutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {

    private AbstractBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws Exception {
        this.beanFactory = (AbstractBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        if(bean instanceof AspectJExpressionPointcutAdvisor) {
            return bean;
        }
        if(bean instanceof MethodInterceptor) {
            return bean;
        }

        List<AspectJExpressionPointcutAdvisor> PointcutAdvisors = beanFactory.getBeansForType(AspectJExpressionPointcutAdvisor.class);
        for(AspectJExpressionPointcutAdvisor pointcutAdvisor : PointcutAdvisors) {
            if (pointcutAdvisor.getPointcut().getClassFilter().matches(bean.getClass())) {
                ProxyFactory adviseSupport = new ProxyFactory();
                adviseSupport.setMethodInterceptor( (MethodInterceptor) pointcutAdvisor.getAdvice());
                adviseSupport.setMethodMatcher(pointcutAdvisor.getPointcut().getMethodMatcher());

                TargetSource  targetSource = new TargetSource(bean, bean.getClass(), bean.getClass().getInterfaces());
                adviseSupport.setTargetSource(targetSource);

                return adviseSupport.getProxy();
            }
        }
        return  bean;
    }
}
