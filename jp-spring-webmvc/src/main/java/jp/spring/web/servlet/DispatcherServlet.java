package jp.spring.web.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.annotation.*;
import jp.spring.web.context.ProcessContext;
import jp.spring.web.exception.NotFoundException;
import jp.spring.web.servlet.handler.RequestMethodParameter;
import jp.spring.web.servlet.handler.UrlHandlerMapping;
import jp.spring.web.servlet.handler.UrlMapping;
import jp.spring.web.util.FileUtils;
import jp.spring.web.util.UrlPathHelper;
import jp.spring.web.view.ViewResolver;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 1/3/2017.
 */
public class DispatcherServlet extends FrameworkServlet {

    private static WebApplicationContext webApplicationContext;

    private static UrlHandlerMapping urlHandlerMapping;

    private static ViewResolver viewResolver;

    private UrlPathHelper urlPathHelper = new UrlPathHelper();


    @Override
    public void init() {
        try {
            webApplicationContext = (WebApplicationContext) getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            urlHandlerMapping = (UrlHandlerMapping) webApplicationContext.getBean(UrlHandlerMapping.URL_HANDLER_MAPPING);
            viewResolver = (ViewResolver) webApplicationContext.getBean(ViewResolver.RESOLVER_NAME);
        } catch (Exception e) {
            System.out.println("DispatcherServlet init failed");
            e.printStackTrace();
        }
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = urlPathHelper.getLookupPathForRequest(request);

        if(isStaticResource(response, path)) {
            return;
        }

        UrlMapping urlMapping = urlHandlerMapping.getUrlMapping(request);
        if(urlMapping == null) {
            response.setStatus(response.SC_NOT_FOUND);
            throw new ServletException("Request of " + path + "Not Found");
        }

        //Build context
        ProcessContext
                .buildContext()
                .set(ProcessContext.REQUEST, request)
                .set(ProcessContext.RESPONSE, response)
                .set(ProcessContext.REQUEST_URL, path);

        Object controller = webApplicationContext.getBean(urlMapping.getBeanName());
        Object[] paras = autowireParameters(urlMapping);
        Object result = urlMapping.getMethod().invoke(controller, paras);

        if(result instanceof String ) {
            String pagePath = (String) result;
            if(!StringUtils.isEmpty(pagePath)) {
               String[] pagePaths = pagePath.split(":");
                if(pagePaths[0].equals("redirect")) {
                    response.sendRedirect(pagePaths[1]);
                } else {
                    viewResolver.toPage(pagePath);
                }
            }
        }
    }

    protected static Object[] autowireParameters(UrlMapping urlMapping) throws Exception {
        List<RequestMethodParameter> methodParameters = urlMapping.getRequestMethodParameters();
        Object[] paras = new Object[methodParameters.size()];

        for(int i = 0; i < methodParameters.size(); i++) {
            paras[i] = autowireParameter(urlMapping, methodParameters.get(i));
        }
        return paras;
    }

    protected static Object autowireParameter(UrlMapping urlMapping, RequestMethodParameter parameter)
            throws Exception {
        if(HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
            return ProcessContext.getRequest();
        }
        if(HttpServletResponse.class.isAssignableFrom(parameter.getType())) {
            return ProcessContext.getResponse();
        }
        if(HttpSession.class.isAssignableFrom(parameter.getType())) {
            return ProcessContext.getSession();
        }

        if(parameter.isPrimitiveType() && parameter.isHasAnnotation()) {
            String name = null, value = null;
            //因为UrlMapping都是UrlMappingBuilder创造的，所以确保了有value()这个方法.....
            Method method =  parameter.getValueMethod();
            name = (String)method.invoke(parameter.getAnnotation(), null);
            if(name.isEmpty()) {
                return null;
            }

            Class<?> annotationType = parameter.getAnnotation().annotationType();
            if(PathVariable.class.equals(annotationType)) {
                String url = ProcessContext.getContext().getString(ProcessContext.REQUEST_URL);
                value = urlMapping.getPathVariable(url, name);

            } else if(RequestParam.class.equals(annotationType)) {
                value = ProcessContext.getRequest().getParameter(name);

            } else if(RequestHeader.class.equals(annotationType)) {
                value = ProcessContext.getRequest().getHeader(name);

            } else if(CookieValue.class.equals(annotationType)) {
                Cookie[] cookies = ProcessContext.getRequest().getCookies();
                if(!JpUtils.isEmpty(cookies)) {
                    for(int i = 0; i < cookies.length; i++) {
                        if(name.equals(cookies[i].getName())) {
                            value = cookies[i].getValue();
                            break;
                        }
                    }
                }
            }

            Object targetValue = JpUtils.convert(value, parameter.getType());
            return targetValue;
        } else {
            return autowireParameter(parameter.getType());
        }
    }

    /**
     *  paramClass is a method parameter and user didn't annotated it. If it is a  primitive type
     * I have no way  to obtain the name of it, but other things LIKE POJO
     * I would use fasterJson to inject it to method
     * */
    private static Object autowireParameter(Class<?> paramClass)  throws Exception {
        HttpServletRequest request = ProcessContext.getRequest();
        Object dto = null;
        if(RequestMethod.GET.name().equals(request.getMethod())
                || "application/x-www-form-urlencoded".equals(request.getContentType())) {
            JSONObject json = new JSONObject();
            format(paramClass, json);
            dto = JSON.toJavaObject(json, paramClass);//使用FastJson，通过键值对的形式进行构造目标对象然后注入...
            //如果对象没有对应的Getter和Setter，属性将无法注入。
            //有需要的话请使用反射来赋值....
        } else if(request.getContentType().startsWith("application/json")) {
            String content = readText(request);
            if(!StringUtils.isEmpty(content)) {
                JSONObject json = JSON.parseObject(content);
                dto = JSON.toJavaObject(json, paramClass);
            }
        }
        return dto;
    }

    /**
     * 进行一些简单的数据变化，确保转换的格式正确
     * */
    private static void format(Class<?> paramClass, JSONObject json) {
        json.putAll(ProcessContext.getRequest().getParameterMap());
        String[] values = null;
        Field field;

        for(Map.Entry<String, Object> entry : json.entrySet()) { //这个循环就是把转变成合适的格式
            values = (String[]) entry.getValue();
            if(values != null && values.length == 1) {
                try {
                    field = paramClass.getDeclaredField(entry.getKey());
                    if(null != field) {
                        if(field.getType().isArray()) {
                            // pass
                        } else if(Collection.class.isAssignableFrom(field.getType())) {
                            entry.setValue(Arrays.asList(values));
                        } else {
                            entry.setValue(values[0]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String readText(HttpServletRequest request) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(request.getInputStream(), "UTF-8");
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[256];
            int read = 0;
            while((read = reader.read(buffer)) != -1) { //the reader.reader() maybe block, pay a attention
                sb.append(buffer, 0, read);
            }
            return sb.toString();
        } catch (IOException e) {

        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    protected boolean isStaticResource(HttpServletResponse response, String path) {
        int index = path.lastIndexOf(".");
        if(index > -1 && index < path.length() - 1) {
            String ext = path.substring(index + 1).toLowerCase();

            if(FileUtils.ALLOWED_EXTENSION.contains(ext)) {
                response.setHeader("Content-type", FileUtils.getMimeType(ext) + ";charset=UTF-8");
                String fileLocation = this.getServletContext().getRealPath(path);
                try {
                    FileUtils.copy(fileLocation, response.getOutputStream());
                } catch (IOException e) {
                    response.setStatus(response.SC_NOT_FOUND);
                }
                return true;
            }
        }
        return false;
    }
}
