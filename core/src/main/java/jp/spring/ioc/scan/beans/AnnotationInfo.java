package jp.spring.ioc.scan.beans;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class AnnotationInfo {
    private final String name;
    private Map<String, AnnotationParamValue> annotationParamValues = Collections.emptyMap();

    public AnnotationInfo(String annotationName, Map<String, AnnotationParamValue> annotationParamValues) {
        this.name = annotationName;
        this.annotationParamValues = annotationParamValues;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(name);
        sb.append("(");
        Iterator<AnnotationParamValue> iterator = annotationParamValues.values().iterator();
        while(iterator.hasNext()) {
            sb.append(iterator.next());
            if(iterator.hasNext()) {
                sb.append(",  ");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    public static class AnnotationParamValue {
        private final String paramName;
        private final Object paramValue;

        public AnnotationParamValue(String paramName, Object paramValue) {
            this.paramName = paramName;
            this.paramValue = paramValue;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(paramName).append("=").append(paramValue);

            return sb.toString();
        }
    }

    public static class AnnotationClassRef {
        private final String typeDescriptor;

        public AnnotationClassRef(String typeDescriptor) {
            this.typeDescriptor = typeDescriptor;
        }

        @Override
        public String toString() {
           return typeDescriptor;
        }
    }

    public static class AnnotationEnumRef {
        private final String className;
        private final String fieldName;

        public AnnotationEnumRef(String className , String fieldName ) {
            this.fieldName = fieldName;
            this.className = className;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(className).append(".").append(fieldName);
            return sb.toString();
        }
    }
}
