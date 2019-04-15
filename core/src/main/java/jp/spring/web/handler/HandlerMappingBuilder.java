package jp.spring.web.handler;



import java.util.List;

/**
 * Created by Administrator on 1/10/2017.
 */
public interface HandlerMappingBuilder {

    public List<Handler> buildHandler(String name, Class<?> controller);
}
