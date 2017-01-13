package jp.spring.web.servlet;

import jp.spring.ioc.context.WebApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 1/3/2017.
 */
public abstract class FrameworkServlet extends HttpServlet{

    private WebApplicationContext webApplicationContext;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }


    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected  void processRequest(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        try {
            doService(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void doService(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
