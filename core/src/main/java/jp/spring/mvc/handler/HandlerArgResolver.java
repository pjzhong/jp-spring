package jp.spring.mvc.handler;

/**
 * Created by Administrator on 2/19/2017.
 * 负责Controller/handler的参数绑定
 */
public interface HandlerArgResolver {

    Object[] resolve(Handler handler);
}
