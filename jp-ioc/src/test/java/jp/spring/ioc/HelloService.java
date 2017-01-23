package jp.spring.ioc;

import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.beans.factory.annotation.Qualifier;
import jp.spring.ioc.beans.factory.annotation.Value;
import jp.spring.ioc.context.ApplicationContext;
import jp.spring.ioc.context.ApplicationContextAware;
import jp.spring.ioc.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 12/24/2016.
 */
@Service("helloService")
public class HelloService {

    @Value(value = "jdbc.driver", required = false)
    private String text;

    ApplicationContext applicationContext;

    @Autowired
    private OutputService outputService;

    @Autowired
    @Qualifier("outService-2")
    private OutputService outputService2;

    public void helloWorld() {
        outputService.output(text);
    }

    public void outPutHello(String text) {
        outputService2.output(text);
    }
}
