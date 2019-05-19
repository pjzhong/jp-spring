package jp.spring.ioc.scan.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @link https://github.com/classgraph/classgraph/blob/master/src/main/java/io/github/classgraph/ClassInfo.java
 */
public class ClassInfo implements Comparable<ClassInfo> {

  private final Map<Relation, Set<ClassInfo>> relations = new HashMap<>();
  private final String className;

  boolean isInterface;
  boolean isAnnotation;
  /**
   * True when a class has been scanned(i.e its classFile contents read), as opposed to only being
   * referenced by another class' classFile as a superclass/superInterface/annotation. If
   *
   * @code isScanned is true, then this also must be a whiteListed (and non-blacklisted) class in a
   * whiteListed(and non-blackListed) package
   */
  boolean isScanned;

  private List<FieldInfo> fieldInfos = Collections.emptyList();
  private List<MethodInfo> methodInfos = Collections.emptyList();
  private Map<String, AnnotationInfo> annotations = Collections.emptyMap();

  public static ClassInfoBuilder builder(String className, int accessFlag) {
    return new ClassInfoBuilder(className, accessFlag);
  }

  ClassInfo(String className) {
    this.className = className;
  }

  private void related(Relation relation, ClassInfo info) {
    relations.computeIfAbsent(relation, k -> new HashSet<>()).add(info);
  }

  private Set<ClassInfo> getDirectlyRelatedClass(Relation relation) {
    return relations.getOrDefault(relation, Collections.emptySet());
  }

  private Set<ClassInfo> getReachableClasses(Relation relation) {
    final Set<ClassInfo> related = getDirectlyRelatedClass(relation);
    if (related.isEmpty()) {
      return related;
    }

    Set<ClassInfo> reachableClasses = new HashSet<>(related);
    LinkedList<ClassInfo> queue = new LinkedList<>(related);
    while (!queue.isEmpty()) {
      ClassInfo head = queue.removeFirst();
      for (final ClassInfo info : head.getDirectlyRelatedClass(relation)) {
        if (reachableClasses.add(info)) {//don't get in cycle
          queue.add(info);
        }
      }
    }

    return reachableClasses;
  }

  void addSuperclass(String superclassName, ClassInfoBuilder builder) {
    ClassInfo superClass = builder.getClassInfo(superclassName);
    superClass.related(Relation.SUBCLASSES, this);

    this.related(Relation.SUPERCLASSES, superClass);
  }

  void addImplementedInterface(String interfaceName, ClassInfoBuilder builder) {
    ClassInfo interfaceClass = builder.getClassInfo(interfaceName);
    interfaceClass.isInterface = true;
    interfaceClass.related(Relation.CLASSES_IMPLEMENTING, this);

    this.related(Relation.IMPLEMENTED_INTERFACES, interfaceClass);
  }

  void addAnnotation(AnnotationInfo annotation, ClassInfoBuilder builder) {
    final ClassInfo annotationClass = builder.getClassInfo(annotation.getName());
    annotationClass.isAnnotation = true;
    annotationClass.related(Relation.ANNOTATED_CLASSES, this);

    this.related(Relation.ANNOTATIONS, annotationClass);
    if (annotations == Collections.EMPTY_MAP) {
      annotations = new HashMap<>();
    }
    annotations.put(annotation.getName(), annotation);
  }

  void addMethodAnnotation(String annotationName, ClassInfoBuilder builder) {
    ClassInfo annotationClass = builder.getClassInfo(annotationName);
    annotationClass.isAnnotation = true;

    annotationClass.related(Relation.CLASSES_WITH_METHOD_ANNOTATION, this);
    this.related(Relation.METHOD_ANNOTATIONS, annotationClass);
  }

  void addFieldAnnotation(String annotationName, ClassInfoBuilder builder) {
    ClassInfo annotationClass = builder.getClassInfo(annotationName);
    annotationClass.isAnnotation = true;
    annotationClass.related(Relation.CLASSES_WITH_FIELD_ANNOTATION, this);

    this.related(Relation.FIELD_ANNOTATIONS, annotationClass);
  }

  /**
   * Add field info.
   */
  void addFieldInfo(final List<FieldInfo> fieldInfoList) {
    if (this.fieldInfos == Collections.EMPTY_LIST) {
      this.fieldInfos = new ArrayList<>();
    }

    this.fieldInfos.addAll(fieldInfoList);
  }

  /**
   * Add method info.
   */
  void addMethodInfo(final List<MethodInfo> methodInfoList) {
    if (this.methodInfos == Collections.EMPTY_LIST) {
      this.methodInfos = new ArrayList<>();
    }

    this.methodInfos.addAll(methodInfoList);
  }

  Set<ClassInfo> getClassesWithFieldAnnotation() {
    return getDirectlyRelatedClass(Relation.CLASSES_WITH_FIELD_ANNOTATION);
  }

  Set<ClassInfo> getClassesWithMethodAnnotation() {
    return getDirectlyRelatedClass(Relation.CLASSES_WITH_METHOD_ANNOTATION);
  }

  Set<ClassInfo> getClassesWithAnnotation() {
    if (!isAnnotation()) {
      return Collections.emptySet();
    }

    Set<ClassInfo> classWithAnnotation = getReachableClasses(Relation.ANNOTATED_CLASSES);

    //Is this annotation can be inherited
    boolean isInherited = false;
    for (ClassInfo metaAnnotation : getDirectlyRelatedClass(Relation.ANNOTATIONS)) {
      if (metaAnnotation.className.equals("java.lang.annotation.Inherited")) {
        isInherited = true;
        break;
      }
    }

    if (isInherited) {
      for (ClassInfo info : classWithAnnotation) {
        classWithAnnotation.addAll(info.getSubClasses());
      }
    }

    return classWithAnnotation;
  }

  public Set<ClassInfo> getClassesImplementing() {
    if (!isInterface()) {
      return Collections.emptySet();
    }

    Set<ClassInfo> reachableClasses = getReachableClasses(Relation.CLASSES_IMPLEMENTING);

    final Set<ClassInfo> allImplementingClasses = new HashSet<>();
    for (ClassInfo implementingClass : reachableClasses) {
      allImplementingClasses.add(implementingClass);
      allImplementingClasses.addAll(implementingClass.getReachableClasses(Relation.SUBCLASSES));
    }
    return allImplementingClasses;
  }

  public Set<ClassInfo> getSuperClasses() {
    return getReachableClasses(Relation.SUBCLASSES);
  }

  public Set<ClassInfo> getSubClasses() {
    return getReachableClasses(Relation.SUBCLASSES);
  }

  boolean isScanned() {
    return isScanned;
  }

  public String getClassName() {
    return className;
  }

  public boolean isInterface() {
    return isInterface && !isAnnotation;
  }

  public boolean isAnnotation() {
    return isAnnotation;
  }

  public boolean isStandardClass() {
    return !(isAnnotation || isInterface);
  }

  public List<FieldInfo> getFieldInfos() {
    return Collections.unmodifiableList(fieldInfos);
  }

  public List<MethodInfo> getMethodInfos() {
    return Collections.unmodifiableList(methodInfos);
  }

  @Override
  public int compareTo(ClassInfo o) {
    return this.className.compareTo(o.className);
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
    return Objects.equals(className, classInfo.className);
  }

  @Override
  public int hashCode() {
    return Objects.hash(className);
  }

  @Override
  public String toString() {
    String name = (isStandardClass() ? "class " : isInterface() ? "interface " : "annotation ")
        + className;

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
     *
     * (May also include annotations, since annotations are interfaces, so you can implement an
     * annotation.)
     */
    IMPLEMENTED_INTERFACES,

    /**
     * Classes that implement this interface (including sub-interfaces), if this is an interface.
     */
    CLASSES_IMPLEMENTING,

    /**
     * I am a class, and I have a annotation
     */
    ANNOTATIONS,

    /**
     * classes that have annotated with this class, if this class is an annotation
     */
    ANNOTATED_CLASSES,

    /**
     * Annotations on one ore more methods of this class.
     */
    METHOD_ANNOTATIONS,

    /**
     * classes that have one or more method annotated with this annotation, if this is an
     * annotation
     */
    CLASSES_WITH_METHOD_ANNOTATION,

    /**
     * Annotations on one or more fields of this class
     */
    FIELD_ANNOTATIONS,

    /**
     * classes that have one or more field annotated with this annotation, if this is an annotation
     */
    CLASSES_WITH_FIELD_ANNOTATION,
  }
}
