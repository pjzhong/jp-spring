package jp.spring.ioc.simleDemo.service;

import jp.spring.ioc.stereotype.Component;

@Component()
public class TestServiceImple implements TestService {
    @Override
    public void sayHello(String hi) {
        System.out.println("hello " + (hi != null ? hi : "World!"));
    }
}
