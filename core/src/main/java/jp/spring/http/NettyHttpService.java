package jp.spring.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import jp.spring.mvc.context.WebApplicationContext;
import jp.spring.mvc.handler.HandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NettyHttpService {

  private static final Logger LOG = LoggerFactory.getLogger(NettyHttpService.class);

  private final String name;
  private int bossThreadSize;
  private int workerThreadSize;
  private int execThreadSize;
  private int chunkLimit;

  private InetSocketAddress bindAddress;
  private ServerBootstrap bootstrap;
  private ChannelGroup channels;
  private WebApplicationContext context;
  private HandlerMapping handlerMapping;

  private NettyHttpService(String name) {
    this.name = name;
  }

  public static Builder builder(String name) {
    return new Builder(name);
  }

  public synchronized void start() throws Throwable {
    try {
      LOG.info("Starting HTTP Service {} at address {}", name, bindAddress);
      channels = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
      bootstrap = createBootstrap(channels);
      Channel serverChannel = bootstrap.bind(bindAddress).sync().channel();
      channels.add(serverChannel);

      bindAddress = (InetSocketAddress) serverChannel.localAddress();

      // Init ApplicationContext
      context = new WebApplicationContext("/");
      handlerMapping = HandlerMapping.build(context.getBeanFactory());

      LOG.info("Started HTTP Service {} at address {}", name, bindAddress);
    } catch (Throwable t) {
      stop();
      throw t;
    }
  }

  public synchronized void stop() {
    channels.close().awaitUninterruptibly();
    bootstrap.config().group().shutdownGracefully().awaitUninterruptibly();
    bootstrap.config().childGroup().shutdownGracefully().awaitUninterruptibly();
    LOG.info("{} shutdown success", name);
  }

  private ServerBootstrap createBootstrap(final ChannelGroup channels) {
    EventLoopGroup bossGroup = new NioEventLoopGroup(bossThreadSize,
        createNamedThreadFactory(name + "-boss-thread-%d"));
    EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreadSize,
        createNamedThreadFactory(name + "-worker-thread-%d"));
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            channels.add(ch);
            ch.pipeline()
                .addLast("codec", new HttpServerCodec())
                .addLast("compressor", new HttpContentCompressor())
                .addLast("keepAlive", new HttpServerKeepAliveHandler())
                .addLast("router", new RequestRouter(handlerMapping, chunkLimit))
                .addLast("dispatcher", new HttpDispatcher(context.getBeanFactory()));
          }
        });
    return bootstrap;
  }

  private ThreadFactory createNamedThreadFactory(final String format) {
    return new ThreadFactory() {

      private final AtomicInteger count = new AtomicInteger();

      @Override
      public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(String.format(format, count.getAndIncrement()));
        return t;
      }
    };
  }

  public static class Builder {

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_BOSS_THREAD_POOL_SIZE = 1;
    private static final int DEFAULT_WORKER_THREAD_POOL_SIZE = 10;
    private static final int DEFAULT_CONNECTION_BACKLOG = 1000;
    private static final int DEFAULT_EXEC_HANDLER_THREAD_POOL_SIZE = 60;
    private static final long DEFAULT_EXEC_HANDLER_THREAD_KEEP_ALIVE_TIME_SECS = 60L;
    private static final int DEFAULT_HTTP_CHUNK_LIMIT = 150 * 1024 * 1024;


    private final String name;
    private String host;
    private int bossThreadSize;
    private int workerThreadSize;
    private int execThreadSize;
    private int port;
    private int chunkLimit;

    // Protected constructor to prevent instantiating Builder instance directly.
    protected Builder(String serviceName) {
      name = serviceName;
      bossThreadSize = DEFAULT_BOSS_THREAD_POOL_SIZE;
      workerThreadSize = DEFAULT_WORKER_THREAD_POOL_SIZE;
      execThreadSize = DEFAULT_EXEC_HANDLER_THREAD_POOL_SIZE;
      port = DEFAULT_PORT;
      chunkLimit = DEFAULT_HTTP_CHUNK_LIMIT;
    }

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setBossThreadSize(int bossThreadSize) {
      this.bossThreadSize = bossThreadSize;
      return this;
    }

    public Builder setWorkerThreadSize(int workerThreadSize) {
      this.workerThreadSize = workerThreadSize;
      return this;
    }

    public Builder setExecThreadSize(int execThreadSize) {
      this.execThreadSize = execThreadSize;
      return this;
    }

    public Builder setPort(int port) {
      this.port = port;
      return this;
    }

    public NettyHttpService build() {
      InetSocketAddress bindAddress;
      bindAddress = new InetSocketAddress(host == null ? "localhost" : host, port);

      NettyHttpService httpService = new NettyHttpService(name);
      httpService.bindAddress = bindAddress;
      httpService.bossThreadSize = bossThreadSize;
      httpService.workerThreadSize = workerThreadSize;
      httpService.execThreadSize = execThreadSize;
      httpService.chunkLimit = chunkLimit;
      return httpService;
    }
  }

}
