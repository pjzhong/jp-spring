package jp.spring.mvc.interceptor;


import java.util.regex.Pattern;
import jp.spring.ioc.factory.BeanFactory;
import jp.spring.mvc.handler.Router;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 1/27/2017. 对 Interceptor 进行封装。处理拦截表达式，为之后的运行做准备
 */
public class InterceptMatch {

  private Pattern pattern;

  private String name;

  public InterceptMatch(String name, String expression) {
    this.name = name;
    init(expression);
  }

  private void init(String expression) {
    if (StringUtils.isBlank(expression)) {
      throw new IllegalArgumentException("Illegal Intercept Expression");
    }

    expression = Router.CLEAN_PATH.matcher(expression).replaceAll("/");
    expression = Router.WILD_CARD_PATTERN.matcher(expression).replaceAll(".*?");
    pattern = Pattern.compile(expression);
  }

  public String getName() {
    return name;
  }

  public Interceptor getInterceptor(BeanFactory beanFactory) {
    return (Interceptor) beanFactory.getBean(name);
  }

  public boolean match(String str) {
    return pattern.matcher(str).find();
  }
}
