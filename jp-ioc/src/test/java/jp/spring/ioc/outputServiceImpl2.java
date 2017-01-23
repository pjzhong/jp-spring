package jp.spring.ioc;

import jp.spring.ioc.stereotype.Service;

/**
 * Created by Administrator on 1/23/2017.
 */
@Service("outService-2")
public class outputServiceImpl2  implements OutputService {

    @Override
    public void output(String text) {
        System.out.println("I am outService-2");
    }
}
