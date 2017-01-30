package jp.spring.web.handler;


import jp.spring.web.support.MultiPartRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 1/30/2017.
 */
public interface MultipartResolver {

    String DEFAULT_MULTI_PART_RESOLVER = MultipartResolver.class + ".root";

    boolean isMultiPart(HttpServletRequest request);

    MultiPartRequest resolveMultipart(HttpServletRequest request);
}
