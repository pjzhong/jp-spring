package jp.spring.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
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

    Handler handler = pair.getFirst();
    Map<String, String> groups = pair.getSecond();
    Object object = beanFactory.getBean(handler.getBeanName());
    Object result = handler.invoke(object, new Object[]{groups});
    ctx.channel().writeAndFlush(result);
  }
}
