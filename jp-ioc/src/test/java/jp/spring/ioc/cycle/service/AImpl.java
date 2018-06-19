package jp.spring.ioc.cycle.service;

import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.cycle.service.A;
import jp.spring.ioc.cycle.service.B;
import jp.spring.ioc.stereotype.Service;

@Service
public class AImpl implements A {

    @Autowired
    private B b;
}
