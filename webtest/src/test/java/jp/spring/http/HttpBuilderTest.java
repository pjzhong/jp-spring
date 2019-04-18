package jp.spring.http;

import java.util.Scanner;

public class HttpBuilderTest {

  public static void main(String[] args) throws Throwable {
    NettyHttpService service = NettyHttpService.builder("test").build();
    service.start();

    Scanner scanner = new Scanner(System.in);
    scanner.nextInt();
    service.stop();
    scanner.close();
  }
}
