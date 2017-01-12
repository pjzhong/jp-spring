package com.jp.service;

import jp.spring.ioc.stereotype.Service;

/**
 * Created by Administrator on 1/12/2017.
 */
@Service
public class OutputServiceImple implements OutputService {

    @Override
    public <T> void output(T text) {
        System.out.println(text);
    }
}
