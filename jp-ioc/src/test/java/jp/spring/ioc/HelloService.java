package jp.spring.ioc;

import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.context.ApplicationContext;
import jp.spring.ioc.context.ApplicationContextAware;
import jp.spring.ioc.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 12/24/2016.
 */
@Service("helloService")
public class HelloService implements ApplicationContextAware{

    private String text;

    ApplicationContext applicationContext;

    @Autowired
    private OutputService outputService;

    public void helloWorld() {
       System.out.println("hi!");
    }

    public void helloWorld2(String test) {
        System.out.println("hi!");
    }


    public void helloWorlds(String fdsf) {
        outputService.output(text);
    }


    public void setOutputService(OutputServiceImpl outputService) {
        this.outputService = outputService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
