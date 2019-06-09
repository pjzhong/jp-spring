package jp.spring.ioc.cycle;

import jp.spring.ioc.factory.annotation.Autowired;
import jp.spring.ioc.cycle.service.A;
import jp.spring.ioc.cycle.service.B;
import jp.spring.ioc.stereotype.Component;

@Component
public class Both {

    @Autowired
    A a;

    @Autowired
    B b;



}
