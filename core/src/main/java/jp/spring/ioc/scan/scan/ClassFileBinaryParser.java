package jp.spring.ioc.scan.scan;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import jp.spring.ioc.scan.beans.AnnotationInfo;
import jp.spring.ioc.scan.beans.AnnotationInfo.ParamValue;
import jp.spring.ioc.scan.beans.AnnotationInfo.classRef;
import jp.spring.ioc.scan.beans.AnnotationInfo.enumRef;
import jp.spring.ioc.scan.beans.ClassInfo;
import jp.spring.ioc.scan.beans.ClassInfoBuilder;
import jp.spring.ioc.scan.beans.FieldInfo;
import jp.spring.ioc.scan.beans.FieldInfoBuilder;
import jp.spring.ioc.scan.beans.MethodInfo;
import jp.spring.ioc.scan.beans.MethodInfoBuilder;
import jp.spring.ioc.scan.utils.ReflectionUtils;

/**
 * Created by Administrator on 10/15/2017.
 */
class ClassFileBinaryParser {

  //缓存重复字符串对象
  private Map<String, String> internStringMap;

  ClassFileBinaryParser() {
    internStringMap = new ConcurrentHashMap<>();
  }

  ClassInfoBuilder parse(InputStream inputStream) throws IOException {
    try {
      final DataInputStream stream = new DataInputStream(new BufferedInputStream(inputStream));

      //Magic number
      if (stream.readInt() != 0xCAFEBABE) {
        throw new RuntimeException("Not a valid class File");
      }
      stream.readUnsignedShort();//Minor version
      stream.readUnsignedShort();//Major version

      final int constantCount = stream.readUnsignedShort();//Constant pool count
      Object[] constantPool = new Object[constantCount];
      for (int i = 1; i < constantCount; i++) {
        final int tag = stream.readUnsignedByte();
        switch (tag) {
          //Modified UTF8 - String
          case 1: {
            constantPool[i] = stream.readUTF();
          }
          break;
          //byte, boolean, char, short, int are all represented by Constant_INTEGER
          case 3: {
            constantPool[i] = stream.readInt();
          }
          break;
          // float
          case 4: {
            constantPool[i] = stream.readFloat();
          }
          break;
          // long double-slot
          case 5: {
            constantPool[i] = stream.readLong();
            i++;
          }
          break;
          // double double-slot
          case 6: {
            constantPool[i] = stream.readDouble();
            i++;
          }
          break;
          // class String
          case 7:
          case 8: {
            constantPool[i] = stream.readUnsignedShort();
          }
          break;
          //Modified UTF8 - String
          case 9:
          case 10:
          case 11:
          case 12: {
            stream.skipBytes(4);
          }
          break;
          //method handler
          case 15: {
            stream.skipBytes(3);
          }
          break;
          //method type
          case 16: {
            stream.skipBytes(2);
          }
          break;
          //invoke dynamic
          case 18: {
            stream.skipBytes(4);
          }
          break;
        }
      }
      return parse(constantPool, stream);
    } finally {
      inputStream.close();
    }
  }

  private ClassInfoBuilder parse(Object[] constantPool, DataInputStream stream)
      throws IOException {

    //Access flags
    final int accFlag = stream.readUnsignedShort();
    final boolean isSynthetic = (accFlag & 0x1000) != 0;
    if (isSynthetic) {//skip class file generate by compiler
      return null;
    }

    final String className = intern(readRefString(stream, constantPool));
    if (className.equals("java.lang.Object")) {
      //java.lang.Object doesn't have a superclass to be linked to, can simply return
      return null;
    }

    final String superclassName = intern(readRefString(stream, constantPool));
    final ClassInfoBuilder infoBuilder = ClassInfo.builder(className, accFlag);
    infoBuilder.addSuperclass(superclassName);

    //Interfaces
    final int interfaceCount = stream.readUnsignedShort();
    for (int i = 0; i < interfaceCount; i++) {
      infoBuilder.addImplementedInterface(intern(readRefString(stream, constantPool)));
    }

    parseFields(stream, constantPool, infoBuilder);
    parseMethods(stream, constantPool, infoBuilder);

    //Attribute (including class annotations)
    final int attributesCount = stream.readUnsignedShort();
    for (int i = 0; i < attributesCount; i++) {
      final String attributeName = readRefString(stream, constantPool);
      final int attributeLength = stream.readInt();
      if ("RuntimeVisibleAnnotations".equals(attributeName)) {
        final int annotationCount = stream.readUnsignedShort();
        for (int j = 0; j < annotationCount; j++) {
          AnnotationInfo info = readAnnotation(stream, constantPool);
          infoBuilder.addAnnotation(info);
        }
      } else {
        stream.skipBytes(attributeLength);
      }
    }

    return infoBuilder;
  }

  private void parseFields(DataInputStream stream, Object[] constantPool,
      ClassInfoBuilder infoBuilder) throws IOException {
    //Fields
    final int fieldCount = stream.readUnsignedShort();
    for (int i = 0; i < fieldCount; i++) {
      final int accessFlags = stream.readUnsignedShort();
      final String fieldName = readRefString(stream, constantPool);
      final String descriptor = readRefString(stream, constantPool);

      FieldInfoBuilder fieldBuilder = FieldInfo
          .builder(infoBuilder.getClassName(), fieldName, descriptor, accessFlags);

      final int attributeCount = stream.readUnsignedShort();
      for (int j = 0; j < attributeCount; j++) {
        final String attributeName = readRefString(stream, constantPool);
        final int attributeLength = stream.readInt();
        switch (attributeName) {
          case "RuntimeVisibleAnnotations": {
            final int num_annotations = stream.readUnsignedShort();
            for (int k = 0; k < num_annotations; k++) {
              AnnotationInfo info = readAnnotation(stream, constantPool);
              infoBuilder.addFieldAnnotation(info);
              fieldBuilder.addAnnotationNames(info);
            }
          }
          break;
          case "ConstantValue": {
            final int valueIndex = stream.readUnsignedShort();
            Object constantValue;
            final char firstChar = descriptor.charAt(0);
            switch (firstChar) {
              case 'B':
                constantValue = ((Integer) constantPool[valueIndex]).byteValue();
                break;
              case 'C':
                constantValue = ((char) ((Integer) constantPool[valueIndex]).intValue());
                break;
              case 'S':
                constantValue = ((Integer) constantPool[valueIndex]).shortValue();
                break;
              case 'Z':
                constantValue = ((Integer) constantPool[valueIndex]) != 0;
                break;
              case 'I':
              case 'J':
              case 'F'://Integer, Long, Float, Double already in correct type
              case 'D':
                constantValue = constantPool[valueIndex];
                break;
              default: {
                if (descriptor.equals("Ljava/lang/String;")) {
                  constantValue = constantPool[(int) constantPool[valueIndex]];
                } else {
                  throw new RuntimeException("unknown Constant type:" + descriptor);
                }
              }
              break;
            }
            fieldBuilder.setConstantValue(constantValue);
          }
          break;
          default:
            stream.skipBytes(attributeLength);
            break;
        }
      }

      infoBuilder.addFieldInfo(fieldBuilder.build());
    }
  }

  private void parseMethods(DataInputStream stream, Object[] constantPool,
      ClassInfoBuilder infoBuilder) throws IOException {
    //Methods
    final int methodCount = stream.readUnsignedShort();
    for (int i = 0; i < methodCount; i++) {
      final int accessFlags = stream.readUnsignedShort();
      final String methodName = readRefString(stream, constantPool);
      final String descriptor = readRefString(stream, constantPool);

      MethodInfoBuilder methodInfoBuilder = MethodInfo
          .builder(infoBuilder.getClassName(), methodName, descriptor, accessFlags);

      final int attributeCount = stream.readUnsignedShort();
      for (int j = 0; j < attributeCount; j++) {
        final String attributeName = readRefString(stream, constantPool);
        final int attributeLength = stream.readInt();
        switch (attributeName) {
          case "RuntimeVisibleAnnotations": {
            final int annotationCount = stream.readUnsignedShort();
            for (int k = 0; k < annotationCount; k++) {
              AnnotationInfo info = readAnnotation(stream, constantPool);
              infoBuilder.addMethodAnnotation(info);
              methodInfoBuilder.addAnnotationName(info);
            }
          }
          break;
          case "AnnotationDefault": {

            Object defaultValue = parseElementValue(stream, constantPool);
            methodInfoBuilder.setDefaultValue(defaultValue);
          }
          break;
          default:
            stream.skipBytes(attributeLength);
        }
      }

      infoBuilder.addMethodInfo(methodInfoBuilder.build());
    }
  }

  /**
   * try to read a annotation and it's value  from this class, method or field, but ignore nested
   * annotations
   */
  private AnnotationInfo readAnnotation(final DataInputStream stream, Object[] constantPool)
      throws IOException {
    final String descriptor = readRefString(stream, constantPool);
    String name;
    List<String> names = ReflectionUtils.parseTypeDescriptor(descriptor);
    if (names.isEmpty() || names.size() > 1) {
      throw new IllegalArgumentException(
          "Invalid typeDescriptor for annotation" + descriptor);
    } else {
      name = intern(names.get(0));
    }

    int size = stream.readUnsignedShort();
    Map<String, ParamValue> values = new HashMap<>();
    for (int i = 0; i < size; i++) {
      String elementName = readRefString(stream, constantPool);//element_name_index
      Object value = parseElementValue(stream, constantPool);
      values.put(elementName, new ParamValue(elementName, value));
    }

    return new AnnotationInfo(name,
        values.isEmpty() ? Collections.emptyMap() : values);
  }

  private Object parseElementValue(final DataInputStream stream, Object[] constantPool)
      throws IOException {
    final int tag = stream.readUnsignedByte();
    switch (tag) {
      case 'B':
        return ((Integer) constantPool[stream.readUnsignedShort()]).byteValue();
      case 'C':
        return (char) ((Integer) constantPool[stream.readUnsignedShort()]).intValue();
      case 'S':
        return ((Integer) constantPool[stream.readUnsignedShort()]).shortValue();
      case 'Z':
        return ((Integer) constantPool[stream.readUnsignedShort()]) != 0;
      case 'I'://int
      case 'J'://long
      case 'D'://double
      case 'F'://float
      case 's'://string
        return constantPool[stream.readUnsignedShort()];//Already in correct type;
      case '@'://Complex(nested) annotation
        return readAnnotation(stream, constantPool);
      case '[': {//array_value
        final int count = stream.readUnsignedShort();
        Object[] values = new Object[count];
        for (int i = 0; i < count; i++) {
          //Nested annotation element value
          values[i] = parseElementValue(stream, constantPool);
        }
        return values;
      }
      case 'c': { //class_info_index
        String typeDescriptor = readRefString(stream, constantPool);
        return new classRef(typeDescriptor);
      }
      case 'e': {//enum_constant_index
        final String typeDescriptor = readRefString(stream, constantPool);
        final String fieldName = readRefString(stream, constantPool);
        List<String> type = ReflectionUtils.parseTypeDescriptor(typeDescriptor);
        if (type.isEmpty() || type.size() > 1) {
          throw new RuntimeException(
              "Illegal element_value enum_constant_index: " + typeDescriptor);
        }
        return new enumRef(/*className*/type.get(0), fieldName);
      }
      default:
        throw new RuntimeException("Unknown annotation elementValue type" + tag);
    }
  }

  private String readRefString(final DataInputStream input, final Object[] constantPool)
      throws IOException {
    final int index = input.readUnsignedShort();
    if (constantPool[index] instanceof Integer) {//indirect reference, like CONSTANT_Class,CONSTANT_String
      return (String) constantPool[(int) constantPool[index]];
    } else {
      return (String) constantPool[index];
    }
  }

  /**
   * 复用String对象
   */
  private String intern(final String string) {
    Objects.requireNonNull(string);
    return internStringMap.computeIfAbsent(string, k -> k.replace('/', '.'));
  }
}
