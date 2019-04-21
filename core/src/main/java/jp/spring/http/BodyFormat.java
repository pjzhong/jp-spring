package jp.spring.http;

import org.apache.commons.lang3.StringUtils;

public enum BodyFormat {
  NONE("none"),
  TEXT_LAIN("text/plain"),
  MULTIPART_FORM_DATA("multipart/form-data"),
  APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded");

  BodyFormat(String name) {
    this.name = name;
  }

  private String name;

  public String getName() {
    return name;
  }

  public static BodyFormat parse(String contentType) {
    if (StringUtils.isNotBlank(contentType)) {
      for (BodyFormat f : BodyFormat.values()) {
        if (contentType.startsWith(f.getName())) {
          return f;
        }
      }
    }

    return NONE;
  }
}
