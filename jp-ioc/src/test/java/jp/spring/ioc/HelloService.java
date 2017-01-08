package jp.spring.ioc;

import jp.spring.ioc.context.ApplicationContext;
import jp.spring.ioc.context.ApplicationContextAware;
import jp.spring.ioc.stereotype.Component;

import java.util.List;

/**
 * Created by Administrator on 12/24/2016.
 */
public class HelloService implements ApplicationContextAware{

    private String text;

    private OutputService outputService;

    private List<String> list;

    public void helloWorld() {
        outputService.output(text);
    }


    public void helloWorlds(String fdsf) {
        outputService.output(text);
    }


    public void setOutputService(OutputService outputService) {
        this.outputService = outputService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("ApplicationContextAware");
    }
}
