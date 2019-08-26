package jp.spring.ioc.cycle.service;

import jp.spring.ioc.annotation.Autowired;
import jp.spring.ioc.annotation.Service;

@Service
public class AImpl implements A {

  @Autowired
  B b;

  @Override
  public B getB() {
    return b;
  }
}
