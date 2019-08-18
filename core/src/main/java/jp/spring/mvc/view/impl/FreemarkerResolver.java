package jp.spring.mvc.view.impl;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import jp.spring.ioc.stereotype.Component;
import jp.spring.mvc.context.ProcessContext;
import jp.spring.mvc.view.AbstractViewResolver;

/**
 * Created by Administrator on 2/12/2017.
 */
@Component
public class FreemarkerResolver extends AbstractViewResolver {

  private static final String contentType = "text/html; charset=" + DEFAULT_ENCODING;
  private static final Configuration config = new Configuration();
  private static boolean isInitialized = false;

  @Override
  public void toPage(String path) throws Exception {
    init();
    Map<String, Object> root = prepareData();

    ProcessContext.getResponse().setContentType(contentType);
    Writer writer = null;
    try {
      Template template = config.getTemplate(getPagePath(path));
      writer = ProcessContext.getResponse().getWriter();
      template.process(root, writer);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  private Map<String, Object> prepareData() {
    Map<String, Object> root = new HashMap<String, Object>();
    Enumeration<String> attrs = ProcessContext.getServletContext().getAttributeNames();
    while (attrs.hasMoreElements()) {
      String attrName = attrs.nextElement();
      root.put(attrName, ProcessContext.getServletContext().getAttribute(attrName));
    }

    attrs = ProcessContext.getRequest().getAttributeNames();
    while (attrs.hasMoreElements()) {
      String attrName = attrs.nextElement();
      root.put(attrName, ProcessContext.getRequest().getAttribute(attrName));
    }

    attrs = ProcessContext.getSession().getAttributeNames();
    while (attrs.hasMoreElements()) {
      String attrName = attrs.nextElement();
      root.put(attrName, ProcessContext.getSession().getAttribute(attrName));
    }

    return root;
  }

  public void init() {
    if (isInitialized) {
      return;
    }

    config.setServletContextForTemplateLoading(ProcessContext.getServletContext(), getFolder());
    config.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
    config.setDefaultEncoding(DEFAULT_ENCODING);
    config.setOutputEncoding(DEFAULT_ENCODING);

    isInitialized = true;
  }

  @Override
  public String getPagePath(String path) {
    return path + getExtension();
  }
}
