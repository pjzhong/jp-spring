package jp.spring.web.support;

import jp.spring.ioc.util.JpUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * Created by Administrator on 1/30/2017.
 */
public class MultiPartRequest extends HttpServletRequestWrapper {

    private Map<String, String[]> multipartParameterMap;
    private MultipartFiles multipartFiles;

    public MultiPartRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String[] values = (String[]) getMultipartParameterMap().get(name);
        if(!JpUtils.isEmpty(values)) {
            return values[0];
        }

        return super.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = getMultipartParameterMap().get(name);
        if(!JpUtils.isEmpty(values)) {
            return values;
        }

        return super.getParameterValues(name);
    }

    public Map<String, String[]> getMultipartParameterMap() {
        return multipartParameterMap;
    }

    public void setMultipartParameterMap(Map<String,String[]> multipartParameterMap) {
        this.multipartParameterMap = multipartParameterMap;
    }

    public MultipartFiles getMultipartFiles() {
        return multipartFiles;
    }

    public void setMultipartFiles(MultipartFiles multipartFiles) {
        this.multipartFiles = multipartFiles;
    }
}
