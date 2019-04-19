package jp.spring.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.spring.ioc.beans.factory.BeanFactory;
import jp.spring.mvc.handler.Handler;
import jp.spring.mvc.handler.Pair;

/**
 * HttpDispatcher that invokes the appropriate http-handler method. The handler and the arguments
 * are read from the {@code Router} context.
 */
public class HttpDispatcher extends SimpleChannelInboundHandler<FullHttpRequest> {

  private BeanFactory beanFactory;

  public HttpDispatcher(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  public static final AttributeKey<Pair<Handler, Map<String, String>>> METHOD_INFO_KEY = AttributeKey
      .newInstance("methodInfo");

  @Override
  public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
    Pair<Handler, Map<String, String>> pair = ctx.channel().attr(METHOD_INFO_KEY).get();

    Map<String, List<String>> params = parseParams(msg);
    System.out.println(params);

    Handler handler = pair.getFirst();
    Map<String, String> groups = pair.getSecond();
    Object object = beanFactory.getBean(handler.getBeanName());
    Object result = handler.invoke(object, new Object[]{groups});
    ctx.channel().writeAndFlush(result);
  }

  /**
   * parse the request parameters
   *
   * @param request instance of @code FullHttpRequest
   * @since 2019年04月19日 20:30:51
   */
  private Map<String, List<String>> parseParams(FullHttpRequest request) {
    Map<String, List<String>> parameters = new HashMap<>(
        new QueryStringDecoder(request.uri()).parameters());

    BodyFormat format = parseBodyFormat(request);
    if (format == BodyFormat.APPLICATION_X_WWW_FORM_URLENCODED) {
      String s = request.content().toString(CharsetUtil.UTF_8);
      parameters.putAll(new QueryStringDecoder(s, false).parameters());
    }

    return parameters;
  }

  private BodyFormat parseBodyFormat(FullHttpRequest request) {
    String type = request.headers().get(HttpHeaderNames.CONTENT_TYPE, "").toLowerCase();

    BodyFormat res = BodyFormat.NONE;
    for (BodyFormat f : BodyFormat.values()) {
      if (type.startsWith(f.getName())) {
        res = f;
        break;
      }
    }

    return res;
  }
}
