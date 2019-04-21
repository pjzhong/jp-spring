package jp.spring.mvc.handler.impl;

import io.netty.handler.codec.http.FullHttpRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import jp.spring.ioc.util.IocUtil;
import jp.spring.mvc.annotation.CookieValue;
import jp.spring.mvc.annotation.PathVariable;
import jp.spring.mvc.annotation.RequestHeader;
import jp.spring.mvc.annotation.RequestParam;
import jp.spring.mvc.handler.Handler;
import jp.spring.mvc.handler.MethodParameter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by Administrator on 2/19/2017.
 */
public class DefaultHandlerArgResolver {

  public Object[] resolve(Pair<Handler, Map<String, String>> routed, FullHttpRequest request) {
    Handler handler = routed.getLeft();
    List<MethodParameter> parameters = handler.getParameters();
    if (ObjectUtils.isEmpty(parameters)) {
      return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }

    Map<String, String> paths = routed.getRight();
    Map<String, List<String>> queryParam = null;

    Object[] result = new Object[parameters.size()];
    int idx = 0;
    for (MethodParameter p : parameters) {

      if (p.hasAnnotation(RequestParam.class)) {
        if(queryParam == null) {
          queryParam =  parseQueryParam(request);
        }
        String name = p.getAnnotation(RequestParam.class).value();


      } else if (p.hasAnnotation(PathVariable.class)) {
        String name = p.getAnnotation(PathVariable.class).value();
        result[idx] = IocUtil.convert(routed.getRight().get(name), p.getType());
      } else if (p.hasAnnotation(RequestHeader.class)) {
        String name = p.getAnnotation(RequestHeader.class).value();
        String header = request.headers().get(name);
        result[idx] = IocUtil.convert(header, p.getType());
      } else if (p.hasAnnotation(CookieValue.class)) {
        //TODO parse CookieValue
      }
    }

    return ArrayUtils.EMPTY_OBJECT_ARRAY;
  }

  private  Map<String, List<String>> parseQueryParam(FullHttpRequest request) {

  }
}
