package jp.spring.ioc.scan.beans;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @link https://github.com/classgraph/classgraph/blob/master/src/main/java/io/github/classgraph/ClassInfo.java
 */
public class ClassInfo implements Comparable<ClassInfo> {

  private final Map<Relation, Set<ClassInfo>> relations = new HashMap<>();
  private final String name;

  boolean isInterface;
  boolean annotation;
  /**
   * True when a class has been scanned(i.e its classFile contents read), as opposed to only being
   * referenced by another class' classFile as a superclass/superInterface/annotation. If
   *
   * @code isScanned is true, then this also must be a whiteListed (and non-blacklisted) class in a
   * whiteListed(and non-blackListed) package
   */
  boolean scanned;

  public static ClassInfoBuilder builder(String className, int accessFlag) {
    return new ClassInfoBuilder(className, accessFlag);
  }

  ClassInfo(String name) {
    this.name = name;
  }

  private void related(Relation relation, ClassInfo info) {
    relations.computeIfAbsent(relation, k -> new HashSet<>()).add(info);
  }

  private Set<ClassInfo> getDirectlyRelated(Relation relation) {
    return relations.getOrDefault(relation, Collections.emptySet());
  }

  private Set<ClassInfo> getReachable(Relation relation) {
    final Set<ClassInfo> related = getDirectlyRelated(relation);
    if (related.isEmpty()) {
      return related;
    }

    Set<ClassInfo> reachable = new HashSet<>(related);
    LinkedList<ClassInfo> queue = new LinkedList<>(related);
    while (!queue.isEmpty()) {
      ClassInfo head = queue.poll();
      for (final ClassInfo info : head.getDirectlyRelated(relation)) {
        if (reachable.add(info)) {//don't get in cycle
          queue.add(info);
        }
      }
    }

    return reachable;
  }

  void addSuperclass(ClassInfo superClass) {
    superClass.related(Relation.SUBCLASSES, this);

    this.related(Relation.SUPERCLASSES, superClass);
  }

  void addImplementedInterface(ClassInfo inter) {
    inter.isInterface = true;
    inter.related(Relation.CLASSES_IMPLEMENTING, this);

    this.related(Relation.IMPLEMENTED_INTERFACE, inter);
  }

  void addAnnotation(ClassInfo anno) {
    anno.annotation = true;
    anno.related(Relation.CLASSES_ANNOTATING, this);

    this.related(Relation.ANNOTATIONS, anno);
  }

  Set<ClassInfo> getClassesWithAnnotation() {
    if (!isAnnotation()) {
      return Collections.emptySet();
    }

    Set<ClassInfo> classWithAnnotation = getReachable(Relation.CLASSES_ANNOTATING);

    //Is this annotation can be inherited
    boolean isInherited = getDirectlyRelated(Relation.ANNOTATIONS).stream()
        .anyMatch(c -> "java.lang.annotation.Inherited".equals(c.name));

    if (isInherited) {
      for (ClassInfo info : classWithAnnotation) {
        classWithAnnotation.addAll(info.getReachable(Relation.SUBCLASSES));
      }
    }

    return classWithAnnotation;
  }

  public Set<ClassInfo> getImplemented() {
    return getReachable(Relation.IMPLEMENTED_INTERFACE);
  }

  public Optional<ClassInfo> getSuperClass() {
    //Directly SUPERCLASSES has only one element(more cases)
    Set<ClassInfo> infos = getDirectlyRelated(Relation.SUPERCLASSES);
    return infos.stream().findFirst();
  }

  public Set<ClassInfo> getAnnotations() {
    return getReachable(Relation.ANNOTATIONS);
  }

  public boolean isScanned() {
    return scanned;
  }

  public String getName() {
    return name;
  }

  public boolean isInterface() {
    return isInterface && !annotation;
  }

  public boolean isAnnotation() {
    return annotation;
  }

  public boolean hasAnnotation(Class<? extends Annotation> clazz) {
    Set<ClassInfo> infos = getReachable(Relation.ANNOTATIONS);
    String name = clazz.getName();
    return infos.stream().anyMatch(i -> name.equals(i.getName()));
  }

  public boolean isStandardClass() {
    return !(annotation || isInterface);
  }

  @Override
  public int compareTo(ClassInfo o) {
    return this.name.compareTo(o.name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClassInfo classInfo = (ClassInfo) o;
    return Objects.equals(name, classInfo.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    String name = (isStandardClass() ? "class " : isInterface() ? "interface " : "annotation ")
        + this.name;

    if (!isScanned()) {
      name += " Not Scanned";
    }
    return name;
  }

  private enum Relation {
    SUPERCLASSES,
    SUBCLASSES,

    /**
     * Interfaces that this class implements, if this is a regular class, or superinterfaces, if
     * this is an interface.
     * <p>
     * (May also include annotations, since annotations are interfaces, so you can implement an
     * annotation.)
     */
    IMPLEMENTED_INTERFACE,

    /**
     * Classes that implement this interface (including sub-interfaces), if this is an interface.
     */
    CLASSES_IMPLEMENTING,

    /**
     * I am a class, and I have annotation(s)
     */
    ANNOTATIONS,

    /**
     * classes that have annotated with this class, if this class is an annotation
     */
    CLASSES_ANNOTATING,

    /**
     * This Annotation annotated on method(s) of some class
     */
    METHOD_ANNOTATIONS,

    /**
     * classes that have one or more method annotated with this annotation, if this is an
     * annotation
     */
    CLASSES_WITH_METHOD_ANNOTATED,

    /**
     * This Annotation annotated on field(s) of some class
     */
    FIELD_ANNOTATIONS,

    /**
     * classes that have one or more field annotated with this annotation, if this is an annotation
     */
    CLASSES_WITH_FIELD_ANNOTATED,
  }
}
