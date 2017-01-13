package jp.spring.web.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 1/7/2017.
 */
public interface Controller {

    void handelRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
