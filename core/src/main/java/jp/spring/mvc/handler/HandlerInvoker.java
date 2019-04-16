package jp.spring.mvc.handler;

/**
 * Created by Administrator on 1/23/2017.
 */
public interface HandlerInvoker {

    String DEFAULT_HANDLER_INVOKER = HandlerInvoker.class + ".root";

    void invokeHandler(Handler handler) throws Exception;
}
