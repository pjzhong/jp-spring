package jp.spring.web.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 1/11/2017.
 */
public class ProcessContext {

    public final static String REQUEST = "request";
    public final static String RESPONSE = "response";
    public final static String SESSION = "session";
    public final static String REQUEST_URL = "requestUrl";

    private final static ThreadLocal<ProcessContext> context = new ThreadLocal<ProcessContext>();

    private Map<String, Object> objectMap = new HashMap<String, Object>();

    public ProcessContext() {}

    public static ProcessContext buildContext() {
        context.set(new ProcessContext());
        return  context.get();
    }

    public static ProcessContext getContext() {
        return context.get();
    }

    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) getContext().objectMap.get(REQUEST);
    }

    public static HttpServletResponse getResponse() {
        return (HttpServletResponse) getContext().objectMap.get(RESPONSE);
    }

    public static HttpSession getSeesion() {
        return getRequest().getSession();
    }

    public ProcessContext set(String key, Object value) {
        objectMap.put(key, value);
        return this;
    }

    public Object get(String key) {
        return objectMap.get(key);
    }

    public Object get(String key, Object defaultValue) {
        if (objectMap.containsKey(key))
            return objectMap.get(key);

        return defaultValue;
    }

    public String getString(String key) {
        return (String) objectMap.get(key);
    }

    public String getString(String key, String defaultValue) {
        return objectMap.containsKey(key) ? getString(key) : defaultValue;
    }

    public boolean getBoolean(String key) {
        return (Boolean) objectMap.get(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return objectMap.containsKey(key) ? getBoolean(key) : defaultValue;
    }

    public int getInt(String key) {
        return (Integer) objectMap.get(key);
    }

    public int getInt(String key, int defaultValue) {
        return objectMap.containsKey(key) ? getInt(key) : defaultValue;
    }
}
