package jp.spring.ioc;

/**
 * Created by Administrator on 12/26/2016.
 */
public class OutputService {

    private HelloService helloService;

    public void output(String text) {
        if(helloService == null) {
            throw new RuntimeException("HelloService is null");
        }
        System.out.println(text);
    }

    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }
}
