package jp.spring.http;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jp.spring.ioc.factory.BeanFactory;
import jp.spring.mvc.handler.Handler;
import jp.spring.mvc.handler.HandlerArgResolver;
import jp.spring.mvc.interceptor.Interceptor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpDispatcher that invokes the appropriate http-handler method. The handler and the arguments
 * are read from the {@code Router} context.
 */
public class HttpDispatcher extends SimpleChannelInboundHandler<FullHttpRequest> {

  private static final Logger LOG = LoggerFactory.getLogger(RequestRouter.class);

  private BeanFactory beanFactory;

  HttpDispatcher(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  static final AttributeKey<Pair<Handler, Map<String, String>>> METHOD_INFO_KEY = AttributeKey
      .newInstance("methodInfo");

  @Override
  public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    Pair<Handler, Map<String, String>> pair = ctx.channel().attr(METHOD_INFO_KEY).get();

    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK);

    Handler handler = pair.getLeft();
    HandlerArgResolver resolver = HandlerArgResolver.resolve(pair, request, response);
    Object object = beanFactory.getBean(handler.getBeanName());

    List<Interceptor> intercepts = handler.getInterceptors().stream()
        .map(p -> p.getInterceptor(beanFactory))
        .collect(Collectors.toList());

    boolean go = intercepts.stream().allMatch(i -> i.beforeHandle(request, response, handler));
    if (go) {
      Object result = handler.invoke(object, resolver.getArgs());
      if (handler.isResponseBody()) {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, MIME.APPLICATION_JSON.type());
        response.content().writeBytes(JSON.toJSONString(result).getBytes(CharsetUtil.UTF_8));
      }
    }
    intercepts.forEach(i -> i.afterHandle(request, response, handler));
    ctx.channel().writeAndFlush(resolver.getResponse());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    //Simply close the connection
    LOG.error("Controller Exception:{}", cause);
    HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
        HttpResponseStatus.INTERNAL_SERVER_ERROR);
    HttpUtil.setContentLength(response, 0);
    HttpUtil.setKeepAlive(response, false);
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }
}
