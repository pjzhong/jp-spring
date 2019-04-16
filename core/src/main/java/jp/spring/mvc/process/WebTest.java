package jp.spring.mvc.process;

import jp.spring.ioc.stereotype.Component;

/**
 * Created by Administrator on 1/24/2017.
 */
@Component("webTest")
public class WebTest  {
    public WebTest() {
        System.out.println("I am WebTest");
    }
    
    public String toString() {
        return  "I am webTest";
    }
}
