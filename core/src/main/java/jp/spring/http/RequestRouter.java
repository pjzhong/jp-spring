package jp.spring.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestRouter extends ChannelInboundHandlerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(RequestRouter.class);

  @Override
  public void channelRead(ChannelHandlerContext ctx,  Object msg) throws Exception {
    try {
      if (msg instanceof HttpRequest) {
      } else {//Something no interested
      }
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }
}
