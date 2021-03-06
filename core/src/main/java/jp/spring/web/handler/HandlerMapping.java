package jp.spring.web.handler;

import io.netty.handler.codec.http.HttpRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import jp.spring.ioc.factory.DefaultBeanFactory;
import jp.spring.web.annotation.Controller;
import jp.spring.web.annotation.Intercept;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.handler.Router.Route;
import jp.spring.web.interceptor.InterceptMatch;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * create mapping between the url and the handler
 *
 * @author ZJP
 * @since 2019年04月18日 13:52:37
 **/
public class HandlerMapping {

  private static Logger LOG = LoggerFactory.getLogger(HandlerMapping.class);
  // Limit the number of parts of the path
  private static final int DEFAULT_MAX_PARTS = 25;

  private Router<Handler> router;

  private HandlerMapping() {
  }

  private HandlerMapping(Router<Handler> router) {
    this.router = router;
  }

  /**
   * get the handlers base on the given url
   *
   * @param request instant of {@code HttpRequest}
   * @since 2019年04月18日 14:02:24
   */
  public Route<Handler> getHandler(HttpRequest request) {
    String path = URI.create(request.uri()).normalize().getPath();
    List<Route<Handler>> routable = router.getDestinations(path);
    return getMatched(routable, RequestMethod.of(request.method()), path);
  }

  /**
   * Get handler which matches the RequestMethod of the request.
   *
   * @param routers List of Routable Handlers.
   * @param method HttpMethod
   * @param requestUri request URI
   * @since 2019年04月18日 16:31:30
   */
  private Route<Handler> getMatched(List<Route<Handler>> routers, RequestMethod method,
      String requestUri) {

    Iterable<String> reqIterator = splitAndOmitEmpty(requestUri);
    List<Route<Handler>> result = new ArrayList<>();

    long maxScore = 0;
    for (Route<Handler> p : routers) {
      Handler handler = p.getTarget();
      if (handler.hasMethod(method)) {
        long score = calcMatchScore(reqIterator, splitAndOmitEmpty(handler.getUrl()));
        if (maxScore < score) {
          maxScore = score;
          result.clear();
          result.add(p);
        } else if (score == maxScore) {
          result.add(p);
        }
      }
    }

    if (1 < result.size()) {
      StringBuilder sb = new StringBuilder();
      result.forEach(s -> sb.append(s.getTarget().getUrl()).append(','));
      throw new IllegalStateException(
          String.format("Multiple matched handlers found for request uri %s: %s",
              requestUri, sb.toString()));
    } else if (result.size() == 1) {
      return result.get(0);
    }
    return null;
  }

  private long calcMatchScore(Iterable<String> request, Iterable<String> destination) {
    long score = 0;
    Iterator<String> req = request.iterator(), dest = destination.iterator();
    while (req.hasNext() && dest.hasNext()) {
      String reqPart = req.next(), desPart = dest.next();
      if (reqPart.equals(desPart)) {
        score = (score * 5) + 4;
      } else if (Router.GROUP_PATTERN.matcher(desPart).matches()) {
        score = (score * 5) + 3;
      } else {
        score = (score * 5) + 2;
      }
    }
    return score;
  }

  private static Iterable<String> splitAndOmitEmpty(String str) {
    return () -> new Iterator<String>() {
      int startIdx = 0;
      String next = null;

      @Override
      public boolean hasNext() {
        while (next == null && startIdx < str.length()) {
          int idx = str.indexOf('/', startIdx);
          if (idx == startIdx) {
            startIdx++;
            continue;
          }

          if (0 <= idx) {
            next = str.substring(startIdx, idx);
            startIdx = idx;
          } else {
            next = str.substring(startIdx);
            startIdx = str.length();
            break;
          }
        }
        return next != null;
      }

      @Override
      public String next() {
        if (hasNext()) {
          String next = this.next;
          this.next = null;
          return next;
        }
        throw new NoSuchElementException("No more element");
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("Remove are not supported");
      }
    };
  }

  public static HandlerMapping build(DefaultBeanFactory beanFactory) {
    Router<Handler> router = buildHandlerMapping(beanFactory);
    return new HandlerMapping(router);
  }

  private static Router<Handler> buildHandlerMapping(DefaultBeanFactory beanFactory) {
    List<String> controllerNames = beanFactory.getNamesByAnnotation(Controller.class);

    Router<Handler> router = Router.create(DEFAULT_MAX_PARTS);

    List<InterceptMatch> intercepts = buildInterceptMatch(beanFactory);
    for (String beanName : controllerNames) {
      Class<?> type = beanFactory.getType(beanName);
      List<Handler> handlers = HandlerBuilder.buildHandler(beanName, type, intercepts);
      handlers.forEach(h -> {
        router.add(h.getUrl(), h);
        LOG.info("Mapping {} {} TO {}.{}", h.getUrl(), h.getHttpMethods(), type.getName(),
            h.getMethod().getName());
      });
    }

    return router;
  }

  /**
   * 为了每一个interceptor创建一个匹配器
   */
  private static List<InterceptMatch> buildInterceptMatch(DefaultBeanFactory beanFactory) {
    List<String> interceptorNames = beanFactory.getNamesByAnnotation(Intercept.class);
    if (ObjectUtils.isEmpty(interceptorNames)) {
      return Collections.emptyList();
    }

    List<InterceptMatch> interceptors = new ArrayList<>();
    for (String name : interceptorNames) {
      String expression = beanFactory.getType(name).getAnnotation(Intercept.class).value();
      InterceptMatch interceptMatch = new InterceptMatch(name, expression);
      interceptors.add(interceptMatch);
    }

    return interceptors;
  }
}
