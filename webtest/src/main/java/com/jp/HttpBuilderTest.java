package com.jp;

import java.util.Scanner;
import jp.spring.http.HttpService;

public class HttpBuilderTest {

  public static void main(String[] args) throws Throwable {
    HttpService service = HttpService.builder("test").build();
    service.start();

    Scanner scanner = new Scanner(System.in);
    scanner.nextInt();
    service.stop();
    scanner.close();
  }
}
