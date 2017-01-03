package jp.spring.web.servlet.handler;

import jp.spring.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 1/3/2017.
 */
public class AbstractUrlHandlerMapping extends AbstractHandlerMapping {

    private final Map<String, Object> handlerMap = new LinkedHashMap<String, Object>();

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
        Object handler = lookupHandler(lookupPath, request);

        return handler;
    }

    protected Object lookupHandler(String urlPath, HttpServletRequest request) {
        Object handler = this.handlerMap.get(urlPath);

        if(handler != null) {
            return handler;
        }

        //No handler found
        return null;
    }

    public Map<String, Object> getHandlerMap() {
        return Collections.unmodifiableMap(handlerMap);
    }
}
