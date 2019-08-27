package jp.spring.ioc.factory.simleDemo.service;

import jp.spring.ioc.annotation.Component;

@Component()
public class TestServiceImple implements TestService {

  @Override
  public String say(String hi) {
    return hi;
  }
}