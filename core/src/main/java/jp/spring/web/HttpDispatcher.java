package jp.spring.web;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
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
import jp.spring.ioc.factory.BeanFactory;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerContext;
import jp.spring.web.handler.Router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpDispatcher that invokes the appropriate http-handler method. The handler and the arguments
 * are read from the {@code Router} context.
 */
@Sharable
public class HttpDispatcher extends SimpleChannelInboundHandler<FullHttpRequest> {

  private final Logger LOG = LoggerFactory.getLogger(RequestRouter.class);

  static final AttributeKey<Route<Handler>> METHOD_INFO_KEY = AttributeKey
      .newInstance("methodInfo");

  private BeanFactory beanFactory;

  HttpDispatcher(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }


  @Override
  public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    Route<Handler> route = ctx.channel().attr(METHOD_INFO_KEY).get();

    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK);

    HandlerContext context = HandlerContext.build(route, request, response);

    Object result = context.invoke(beanFactory);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, MIME.APPLICATION_JSON.type());
    response.content().writeBytes(JSON.toJSONString(result).getBytes(CharsetUtil.UTF_8));
    ctx.channel().writeAndFlush(context.getResponse());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    //Simply close the connection
    LOG.error("Controller Exception", cause);
    HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
        HttpResponseStatus.INTERNAL_SERVER_ERROR);
    HttpUtil.setContentLength(response, 0);
    HttpUtil.setKeepAlive(response, false);
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }
}
