package jp.spring.web.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by Administrator on 1/3/2017.
 */
public abstract class HttpServletBean extends HttpServlet {

    @Override
    public final void init() throws ServletException {
        initServletBean();
    }

    protected abstract void initServletBean() throws ServletException;

    /**
     * servletConfig是默认存在的，我们可以看看servlet输出后，是什么样的结果
     * 1/为什么需要servletContext和servletName 为了能够根据servlet的运行环境
     * 来初始化咱们需要的webApplicationContext
     */
    @Override
    public final String getServletName() {
        return (getServletConfig() != null ? getServletConfig()
                .getServletName() : null);
    }

    @Override
    public final ServletContext getServletContext() {
        return (getServletConfig() != null ? getServletConfig()
                .getServletContext() : null);
    }
}
