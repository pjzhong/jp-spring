package jp.spring.mvc.annotation;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Created by Administrator on 1/10/2017.
 */
public enum RequestMethod {
  GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE, NONE;

  public static RequestMethod of(HttpMethod method) {
    for (RequestMethod m : values()) {
      if (method.toString().equals(m.toString())) {
        return m;
      }
    }
    return NONE;
  }
}
