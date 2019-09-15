package jp.spring.web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import jp.spring.DefaultApplicationContext;
import jp.spring.web.handler.HandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpService {

  private static final Logger LOG = LoggerFactory.getLogger(HttpService.class);

  private final String name;
  private int bossThreadSize;
  private int workerThreadSize;
  private int chunkLimit;

  private InetSocketAddress bindAddress;
  private ServerBootstrap bootstrap;
  private DefaultApplicationContext context;
  private HandlerMapping handlerMapping;

  private HttpService(String name) {
    this.name = name;
  }

  public static Builder builder(String name) {
    return new Builder(name);
  }

  public synchronized void start() throws Throwable {
    try {
      LOG.info("Starting HTTP Service {} at address {}", name, bindAddress);

      // Init ApplicationContext
      context = new DefaultApplicationContext();
      handlerMapping = HandlerMapping.build(context.getBeanFactory());

      bootstrap = createBootstrap();
      bootstrap.bind(bindAddress).sync();

      LOG.info("Started HTTP Service {} at address {}", name, bindAddress);
    } catch (Throwable t) {
      stop();
      throw t;
    }
  }

  public synchronized void stop() {
    bootstrap.config().group().shutdownGracefully().awaitUninterruptibly();
    bootstrap.config().childGroup().shutdownGracefully().awaitUninterruptibly();
    bootstrap = null;
    LOG.info("{} shutdown success", name);
  }

  private ServerBootstrap createBootstrap() {
    EventLoopGroup bossGroup = new NioEventLoopGroup(bossThreadSize,
        createNamedThreadFactory(name + "-boss-thread-%d"));
    EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreadSize,
        createNamedThreadFactory(name + "-worker-thread-%d"));
    ServerBootstrap bootstrap = new ServerBootstrap();
    HttpDispatcher dispatcher = new HttpDispatcher(context);
    bootstrap
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ch.pipeline()
                .addLast("codec", new HttpServerCodec())
                .addLast("compressor", new HttpContentCompressor())
                .addLast("keepAlive", new HttpServerKeepAliveHandler())
                .addLast("router", new RequestRouter(handlerMapping, chunkLimit))
                .addLast("dispatcher", dispatcher);
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
    private static final int DEFAULT_HTTP_CHUNK_LIMIT = 150 * 1024 * 1024;


    private final String name;
    private String host;
    private int bossThreadSize;
    private int workerThreadSize;
    private int port;
    private int chunkLimit;

    // Protected constructor to prevent instantiating Builder instance directly.
    protected Builder(String serviceName) {
      name = serviceName;
      bossThreadSize = DEFAULT_BOSS_THREAD_POOL_SIZE;
      workerThreadSize = DEFAULT_WORKER_THREAD_POOL_SIZE;
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

    public Builder setPort(int port) {
      this.port = port;
      return this;
    }

    public HttpService build() {
      InetSocketAddress bindAddress;
      bindAddress = new InetSocketAddress(host == null ? "localhost" : host, port);

      HttpService httpService = new HttpService(name);
      httpService.bindAddress = bindAddress;
      httpService.bossThreadSize = bossThreadSize;
      httpService.workerThreadSize = workerThreadSize;
      httpService.chunkLimit = chunkLimit;
      return httpService;
    }
  }

}
