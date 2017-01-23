package jp.spring.ioc;

import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.stereotype.Service;

/**
 * Created by Administrator on 12/26/2016.
 */
@Service
public class OutputServiceImpl implements OutputService {

    @Autowired
    private HelloService helloService;

    public void output(String text) {
        System.out.println(text);
    }

    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }
}
