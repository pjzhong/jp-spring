package jp.spring.web;

import org.apache.commons.lang3.StringUtils;

public enum MIME {
  NONE("none"),
  TEXT_PLAIN("text/plain"),
  MULTIPART_FORM_DATA("multipart/form-data"),
  APPLICATION_JSON("application/json"),
  APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded");

  MIME(String name) {
    this.name = name;
  }

  private String name;

  public String type() {
    return name;
  }

  public static MIME parse(String contentType) {
    if (StringUtils.isNotBlank(contentType)) {
      for (MIME f : MIME.values()) {
        if (contentType.startsWith(f.type())) {
          return f;
        }
      }
    }

    return NONE;
  }
}
