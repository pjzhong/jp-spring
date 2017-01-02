package jp.spring.ioc;

import java.util.List;

/**
 * Created by Administrator on 12/24/2016.
 */
public class HelloService {

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
}
