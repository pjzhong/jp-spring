package jp.spring.mvc.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.factory.BeanFactory;
import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.beans.factory.annotation.Value;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.util.JpUtils;
import jp.spring.mvc.annotation.Intercept;
import jp.spring.mvc.handler.Handler;
import jp.spring.mvc.handler.HandlerMapping;
import jp.spring.mvc.handler.HandlerMappingBuilder;
import jp.spring.mvc.handler.PathRouter;
import jp.spring.mvc.handler.impl.DefaultHandlerMappingBuilder;
import jp.spring.mvc.interceptor.InterceptMatch;
import jp.spring.mvc.interceptor.Interceptor;

/**
 * Created by Administrator on 1/25/2017.
 */
@Component
public class WebBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

  private AbstractBeanFactory beanFactory;
  // Limit the number of parts of the path
  private static final int DEFAULT_MAX_PARTS = 25;

  @Value("page.extension")
  private String pageExtension = "";

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    if (beanFactory instanceof AbstractBeanFactory) {
      this.beanFactory = (AbstractBeanFactory) beanFactory;
    }
  }

  @Override
  public void postProcessBeforeInitialization() throws Exception {
    buildHandlerMapping(beanFactory);
  }

  private HandlerMapping buildHandlerMapping(AbstractBeanFactory beanFactory) throws Exception {
    List<String> controllerNames = beanFactory.getBeanNamByAnnotation(Controller.class);

    PathRouter<Handler> pathRouter = PathRouter.create(DEFAULT_MAX_PARTS);
    HandlerMappingBuilder builder = new DefaultHandlerMappingBuilder();
    for (String beanName : controllerNames) {
      List<Handler> handlers = builder.buildHandler(beanName, beanFactory.getType(beanName));

    }

    //TODO Assembling interceptors
    List<InterceptMatch> interceptMatches = buildInterceptMatch(beanFactory);
    Map<String, List<Handler>> handlerMap = Collections.emptyMap();
    for (String url : handlerMap.keySet()) {
      List<Interceptor> interceptors = new ArrayList<>();
      for (InterceptMatch interceptMatch : interceptMatches) {
        if (interceptMatch.match(url)) {
          interceptors.add(interceptMatch.getInterceptor());
        }
      }

      for (Handler handler : handlerMap.get(url)) {
        handler.addInterceptors(interceptors);
      }
    }

    return null;
  }

  /**
   * 为了每一个interceptor创建一个匹配器
   */
  private List<InterceptMatch> buildInterceptMatch(AbstractBeanFactory beanFactory)
      throws Exception {
    List<String> interceptorNames = beanFactory.getBeanNamByAnnotation(Intercept.class);
    List<InterceptMatch> interceptors = Collections.emptyList();
    if (!JpUtils.isEmpty(interceptorNames)) {
      interceptors = new ArrayList<>();
      InterceptMatch interceptMatch;
      Interceptor interceptor;
      String expression = null;
      for (String name : interceptorNames) {
        interceptor = (Interceptor) beanFactory.getBean(name);
        expression = beanFactory.getType(name).getAnnotation(Intercept.class).url();
        interceptMatch = new InterceptMatch(interceptor, expression);
        interceptors.add(interceptMatch);
      }
    }

    return interceptors;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
    return bean;
  }
}
