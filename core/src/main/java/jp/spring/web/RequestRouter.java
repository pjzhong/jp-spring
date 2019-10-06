package jp.spring.web;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerMapping;
import jp.spring.web.handler.Router.Route;

public class RequestRouter extends ChannelInboundHandlerAdapter {

  private final HandlerMapping handlerMapping;
  private final int chunkLimit;
  private Route<Handler> handler;

  public RequestRouter(HandlerMapping handlerMapping, int chunkLimit) {
    this.handlerMapping = handlerMapping;
    this.chunkLimit = chunkLimit;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    try {
      if (msg instanceof HttpRequest) {
        HttpRequest request = (HttpRequest) msg;
        handler = prepareHandleMethod(request, ctx);

        if (handler != null) {
          ReferenceCountUtil.retain(msg);
          ctx.fireChannelRead(msg);
        } else {//Not Found, you can do something here.
          // Now just simply return 404
          HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
              HttpResponseStatus.NOT_FOUND);
          HttpUtil.setContentLength(response, 0);
          HttpUtil.setKeepAlive(response, false);
          ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
      } else {
        if (handler != null) {
          ReferenceCountUtil.retain(msg);
          ctx.fireChannelRead(msg);
        }
      }
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

  private Route<Handler> prepareHandleMethod(HttpRequest request,
      ChannelHandlerContext ctx) {
    Route<Handler> handles = handlerMapping.getHandler(request);
    if (handles == null) {
      return null;
    }

    ChannelPipeline pipeline = ctx.channel().pipeline();
    if (pipeline.get("continue") != null) {
      pipeline.remove("continue");
    }
    if (pipeline.get("aggregator") == null) {
      pipeline.addAfter("router", "aggregator", new HttpObjectAggregator(chunkLimit));
    }
    ctx.channel().attr(HttpDispatcher.METHOD_INFO_KEY).set(handles);
    return handles;
  }
}
