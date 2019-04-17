package jp.spring.http;

import org.junit.Test;

public class HttpBuilderTest {

  @Test
  public void startTest() throws Throwable {
    NettyHttpService service = NettyHttpService.builder("test").build();
    service.start();
  }
}
