package jp.spring.ioc.cycle.service;

import jp.spring.ioc.annotation.Autowired;
import jp.spring.ioc.annotation.Service;

@Service
public class BImpl implements B {

  @Autowired
  A a;

  @Override
  public A getA() {
    return a;
  }
}
