package jp.spring.ioc.scan.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类信息构造者
 *
 * @author ZJP
 * @since 2019年05月26日 21:06:51
 **/
public class ClassData {

  private final String name;

  private final int accessFlag;
  private String superclassName;    // Superclass (can be null if no superclass, or if superclass is blacklisted)
  private List<String> implemented = Collections.emptyList();
  private List<String> annotations = Collections.emptyList();

  ClassData(final String name, final int accessFlag) {
    this.name = name;
    this.accessFlag = accessFlag;
  }

  public void addSuperclass(final String superclassName) {
    this.superclassName = superclassName;
  }

  public void addImplementedInterface(final String interfaceName) {
    if (implemented.isEmpty()) {
      implemented = new ArrayList<>();
    }
    implemented.add(interfaceName);
  }

  public void addAnnotation(String name) {
    if (annotations.isEmpty()) {
      annotations = new ArrayList<>();
    }
    annotations.add(name);
  }

  public String getName() {
    return name;
  }

  public boolean isInterface() {
    return (accessFlag & 0x0200) != 0;
  }

  public boolean isAnnotation() {
    return (accessFlag & 0x2000) != 0;
  }

  public String getSuperclassName() {
    return superclassName;
  }

  public List<String> getImplemented() {
    return implemented;
  }

  public List<String> getAnnotations() {
    return annotations;
  }

  public int getAccessFlag() {
    return accessFlag;
  }
}
