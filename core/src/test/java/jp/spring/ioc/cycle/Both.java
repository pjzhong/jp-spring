package jp.spring.ioc.cycle;

import jp.spring.ioc.cycle.service.A;
import jp.spring.ioc.cycle.service.B;
import jp.spring.ioc.annotation.Autowired;
import jp.spring.ioc.annotation.Component;

@Component
public class Both {

  @Autowired
  A a;

  @Autowired
  B b;


}
