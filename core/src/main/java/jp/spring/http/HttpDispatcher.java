package jp.spring.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import java.util.Map;
import jp.spring.ioc.beans.factory.BeanFactory;
import jp.spring.mvc.handler.Handler;
import jp.spring.mvc.handler.HandlerArgResolver;
import org.apache.commons.lang3.tuple.Pair;

/**
 * HttpDispatcher that invokes the appropriate http-handler method. The handler and the arguments
 * are read from the {@code Router} context.
 */
public class HttpDispatcher extends SimpleChannelInboundHandler<FullHttpRequest> {

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
    handler.invoke(object, resolver.getArgs());
    ctx.channel().writeAndFlush(resolver.getResponse());
  }
}