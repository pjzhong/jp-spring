package jp.spring.ioc.cycle.service;

import jp.spring.ioc.factory.annotation.Autowired;
import jp.spring.ioc.stereotype.Service;

@Service
public class BImpl implements B {

  @Autowired
  A a;

  @Override
  public A getA() {
    return a;
  }
}
