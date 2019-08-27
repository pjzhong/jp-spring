package jp.spring.ioc.factory.cycle;

import jp.spring.ioc.factory.cycle.service.A;
import jp.spring.ioc.factory.cycle.service.B;
import jp.spring.ioc.annotation.Autowired;
import jp.spring.ioc.annotation.Component;

@Component
public class Both {

  @Autowired
  A a;

  @Autowired
  B b;


}
