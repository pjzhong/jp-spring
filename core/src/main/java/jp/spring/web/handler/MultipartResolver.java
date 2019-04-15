package jp.spring.web.handler;


import javax.servlet.http.HttpServletRequest;
import jp.spring.web.support.MultiPartRequest;

/**
 * Created by Administrator on 1/30/2017.
 */
public interface MultipartResolver {

    String DEFAULT_MULTI_PART_RESOLVER = MultipartResolver.class + ".root";

    boolean isMultiPart(HttpServletRequest request);

    MultiPartRequest resolveMultipart(HttpServletRequest request);
}
