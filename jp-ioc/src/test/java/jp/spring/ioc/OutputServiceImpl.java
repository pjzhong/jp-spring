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
 /*       if(helloService == null) {
            throw new RuntimeException("HelloService is null");
        }*/
        System.out.println(helloService);
        System.out.println(text);
    }

    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }
}
