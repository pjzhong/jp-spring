package jp.spring.ioc.beans.scan.beans;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jdk.nashorn.internal.ir.annotations.Immutable;
import jp.spring.ioc.beans.scan.utils.ReflectionUtils;

/**
 * Created by Administrator on 10/28/2017.
 */
@Immutable
public class FieldInfo {
    /** Get the field modifiers as a string, e.g. "public static final". */
    public String getModifiers() {
        return ReflectionUtils.modifiersToString(modifiers, /* isMethod = */ false);
    }

    /** Returns true if this field is public. */
    public boolean isPublic() {
        return Modifier.isPublic(modifiers);
    }

    /** Returns true if this field is private. */
    public boolean isPrivate() {
        return Modifier.isPrivate(modifiers);
    }

    /** Returns true if this field is protected. */
    public boolean isProtected() {
        return Modifier.isProtected(modifiers);
    }

    /** Returns true if this field is package-private. */
    public boolean isPackagePrivate() {
        return !isPublic() && !isPrivate() && !isProtected();
    }

    /** Returns true if this field is static. */
    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }

    /** Returns true if this field is final. */
    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }

    /** Returns true if this field is a transient field. */
    public boolean isTransient() {
        return Modifier.isTransient(modifiers);
    }

    /** Returns the name of the field. */
    public String getFieldName() {
        return fieldName;
    }

    /** Returns the access flags of the field. */
    public int getAccessFlags() {
        return modifiers;
    }

    /** Returns the type of the field, in string representation (e.g. "int[][]"). */
    public String getTypeStr() {
        return typeStr;
    }

    /**maybe be null, if this field not modified by static and final or it isn't primitive type*/
    public Object getConstantValue() {
        return constantValue;
    }

    public Map<String, AnnotationInfo> getAnnotations() {
        return annotations == null ? Collections.emptyMap() : Collections.unmodifiableMap(annotations);
    }

    /** Returns the names of annotations on the field, or the empty list if none. */


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldInfo fieldInfo = (FieldInfo) o;

        if (!belongToClass.equals(fieldInfo.belongToClass)) return false;
        if (!fieldName.equals(fieldInfo.fieldName)) return false;
        return typeStr.equals(fieldInfo.typeStr);
    }

    @Override
    public int hashCode() {
        int result = belongToClass.hashCode();
        result = 31 * result + fieldName.hashCode();
        result = 31 * result + typeStr.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        getAnnotations().values().forEach(a -> buf.append(a).append(' '));
        buf.append(getModifiers()).append(' ').append(getTypeStr()).append(' ').append(fieldName);

        if(getConstantValue() != null) {
            buf.append(" = ").append(constantValue).append(';');
        }

        return buf.toString();
    }

    public static FieldInfoBuilder builder(String className, String fieldName, String typeDescriptor, int modifiers) {
        return new FieldInfoBuilder(className, fieldName, typeDescriptor, modifiers);
    }

    FieldInfo(String className, String fieldName, String typeDescriptor, int modifiers) {
        this.belongToClass = className;
        this.fieldName = fieldName;
        this.modifiers = modifiers;

        final List<String> typeNames = ReflectionUtils.parseTypeDescriptor(typeDescriptor);
        if (typeNames.size() != 1) {
            throw new IllegalArgumentException("Invalid type descriptor for field: " + typeDescriptor);
        }
        this.typeStr = typeNames.get(0);
    }


    void setAnnotations(Map<String, AnnotationInfo> annotations) {
        this.annotations = annotations;
    }

    void setConstantValue(Object constantValue) {
        this.constantValue = constantValue;
    }

    private final String belongToClass;
    private final String fieldName;
    private final int modifiers;
    private final String typeStr;

    private  Object constantValue = null;
    private Map<String, AnnotationInfo> annotations = Collections.emptyMap();
}
