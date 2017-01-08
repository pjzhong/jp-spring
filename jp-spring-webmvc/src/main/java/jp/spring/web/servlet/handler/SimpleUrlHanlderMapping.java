package jp.spring.web.servlet.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 1/7/2017.
 * 最原始的XML配置url
 */
public class SimpleUrlHanlderMapping  extends AbstractUrlHandlerMapping{

    private final Map<String, Object> urlMap = new HashMap<>();

    protected void registerHanlders(Map<String, Object> urlMap) throws Exception {
        if(urlMap.isEmpty()) {

        } else {
            for(Map.Entry<String, Object> entry : urlMap.entrySet()) {
                String url = entry.getKey();
                Object hanlder = entry.getValue();

                if(!url.startsWith("/")) {
                    url = "/" + url;
                }

                if(hanlder instanceof String) {
                    hanlder = ((String) hanlder).trim();
                }

                registerHanlder(url, hanlder);
            }
        }
    }

    public Map<String, ?> getUrlMap() {
        return Collections.unmodifiableMap(this.urlMap);
    }

}
