package com.jp;

import jp.spring.http.HttpService;

public class HttpServer {

  public static void main(String[] args) throws Throwable {
    HttpService service = HttpService.builder("test").build();
    service.start();

    Runtime.getRuntime().addShutdownHook(new Thread(service::stop));
  }
}
