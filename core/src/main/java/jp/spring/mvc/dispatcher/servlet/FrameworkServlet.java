package jp.spring.mvc.dispatcher.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jp.spring.ioc.context.WebApplicationContext;

/**
 * Created by Administrator on 1/3/2017.
 */
public abstract class FrameworkServlet extends HttpServlet{

    private WebApplicationContext webApplicationContext;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doService(req, resp);
    }


    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doService(req, resp);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doService(req, resp);
    }

    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doService(req, resp);
    }

    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doService(req, resp);
    }

    protected abstract void doService(HttpServletRequest request, HttpServletResponse response);
}
