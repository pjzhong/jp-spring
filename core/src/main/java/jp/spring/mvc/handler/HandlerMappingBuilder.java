package jp.spring.mvc.handler;



import java.util.List;

/**
 * Created by Administrator on 1/10/2017.
 */
public interface HandlerMappingBuilder {

    List<Handler> buildHandler(String name, Class<?> controller);
}
