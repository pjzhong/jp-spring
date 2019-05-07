package jp.spring.ioc.scan.scanner;


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
import java.util.concurrent.ConcurrentMap;
import jp.spring.ioc.scan.beans.AnnotationInfo;
import jp.spring.ioc.scan.beans.AnnotationInfo.AnnotationClassRef;
import jp.spring.ioc.scan.beans.AnnotationInfo.AnnotationEnumRef;
import jp.spring.ioc.scan.beans.AnnotationInfo.AnnotationParamValue;
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
public class ClassFileBinaryParser {

  public ClassFileBinaryParser() {
    internStringMap = new ConcurrentHashMap<>();
  }

  //缓存重复字符串对象
  private ConcurrentMap<String, String> internStringMap;

  public ClassInfoBuilder parse(final InputStream inputStream) throws IOException {
    try {
      final DataInputStream classInput = new DataInputStream(
          new BufferedInputStream(inputStream, inputStream.available()));

      //Magic number
      if (classInput.readInt() != 0xCAFEBABE) {
        throw new RuntimeException("Not a valid class File");
      }
      classInput.readUnsignedShort();//Minor version
      classInput.readUnsignedShort();//Major version

      final int constantCount = classInput.readUnsignedShort();//Constant pool count
      Object[] constantPool = new Object[constantCount];
      for (int i = 1; i < constantCount; i++) {
        final int tag = classInput.readUnsignedByte();
        switch (tag) {
          //Modified UTF8 - String
          case 1: {
            constantPool[i] = classInput.readUTF();
          }
          break;
          //byte, boolean, char, short, int are all represented by Constant_INTEGER
          case 3: {
            constantPool[i] = classInput.readInt();
          }
          break;
          // float
          case 4: {
            constantPool[i] = classInput.readFloat();
          }
          break;
          // long double-slot
          case 5: {
            constantPool[i] = classInput.readLong();
            i++;
          }
          break;
          // double double-slot
          case 6: {
            constantPool[i] = classInput.readDouble();
            i++;
          }
          break;
          // class String
          case 7:
          case 8: {
            constantPool[i] = classInput.readUnsignedShort();
          }
          break;
          //Modified UTF8 - String
          case 9:
          case 10:
          case 11:
          case 12: {
            classInput.skipBytes(4);
          }
          break;
          //method handler
          case 15: {
            classInput.skipBytes(3);
          }
          break;
          //method type
          case 16: {
            classInput.skipBytes(2);
          }
          break;
          //invoke dynamic
          case 18: {
            classInput.skipBytes(4);
          }
          break;
        }
      }
      return parse(constantPool, classInput);
    } finally {
      inputStream.close();
    }
  }

  private ClassInfoBuilder parse(Object[] constantPool, DataInputStream classInput)
      throws IOException {

    //Access flags
    final int accFlag = classInput.readUnsignedShort();
    final boolean isSynthetic = (accFlag & 0x1000) != 0;
    if (isSynthetic) {
      return null;
    }//skip class file generate by compiler

    final String className = intern(readRefString(classInput, constantPool));
    if (className.equals("java.lang.Object")) {
      //java.lang.Object doesn't have a superclass to be linked to, can simply return
      return null;
    }

    final String superclassName = intern(readRefString(classInput, constantPool));
    final ClassInfoBuilder infoBuilder = ClassInfo.builder(className, accFlag);
    infoBuilder.addSuperclass(superclassName);

    //Interfaces
    final int interfaceCount = classInput.readUnsignedShort();
    for (int i = 0; i < interfaceCount; i++) {
      infoBuilder.addImplementedInterface(intern(readRefString(classInput, constantPool)));
    }

    parseFields(classInput, constantPool, infoBuilder);
    parseMethods(classInput, constantPool, infoBuilder);

    //Attribute (including class annotations)
    final int attributesCount = classInput.readUnsignedShort();
    for (int i = 0; i < attributesCount; i++) {
      final String attributeName = readRefString(classInput, constantPool);
      final int attributeLength = classInput.readInt();
      if ("RuntimeVisibleAnnotations".equals(attributeName)) {
        final int annotationCount = classInput.readUnsignedShort();
        for (int j = 0; j < annotationCount; j++) {
          AnnotationInfo info = readAnnotation(classInput, constantPool);
          infoBuilder.addAnnotation(info);
        }
      } else {
        classInput.skipBytes(attributeLength);
      }
    }

    return infoBuilder;
  }

  private void parseFields(DataInputStream classInput, Object[] constantPool,
      ClassInfoBuilder infoBuilder) throws IOException {
    //Fields
    final int fieldCount = classInput.readUnsignedShort();
    for (int i = 0; i < fieldCount; i++) {
      final int accessFlags = classInput.readUnsignedShort();
      final String fieldName = readRefString(classInput, constantPool);
      final String descriptor = readRefString(classInput, constantPool);

      FieldInfoBuilder fieldBuilder = FieldInfo
          .builder(infoBuilder.getClassName(), fieldName, descriptor, accessFlags);

      final int attributeCount = classInput.readUnsignedShort();
      for (int j = 0; j < attributeCount; j++) {
        final String attributeName = readRefString(classInput, constantPool);
        final int attributeLength = classInput.readInt();
        switch (attributeName) {
          case "RuntimeVisibleAnnotations": {
            final int num_annotations = classInput.readUnsignedShort();
            for (int k = 0; k < num_annotations; k++) {
              AnnotationInfo info = readAnnotation(classInput, constantPool);
              infoBuilder.addFieldAnnotation(info);
              fieldBuilder.addAnnotationNames(info);
            }
          }
          break;
          case "ConstantValue": {
            final int valueIndex = classInput.readUnsignedShort();
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
            classInput.skipBytes(attributeLength);
            break;
        }
      }

      infoBuilder.addFieldInfo(fieldBuilder.build());
    }
  }

  private void parseMethods(DataInputStream classInput, Object[] constantPool,
      ClassInfoBuilder infoBuilder) throws IOException {
    //Methods
    final int methodCount = classInput.readUnsignedShort();
    for (int i = 0; i < methodCount; i++) {
      final int accessFlags = classInput.readUnsignedShort();
      final String methodName = readRefString(classInput, constantPool);
      final String descriptor = readRefString(classInput, constantPool);

      MethodInfoBuilder methodInfoBuilder = MethodInfo
          .builder(infoBuilder.getClassName(), methodName, descriptor, accessFlags);

      final int attributeCount = classInput.readUnsignedShort();
      for (int j = 0; j < attributeCount; j++) {
        final String attributeName = readRefString(classInput, constantPool);
        final int attributeLength = classInput.readInt();
        switch (attributeName) {
          case "RuntimeVisibleAnnotations": {
            final int annotationCount = classInput.readUnsignedShort();
            for (int k = 0; k < annotationCount; k++) {
              AnnotationInfo info = readAnnotation(classInput, constantPool);
              infoBuilder.addMethodAnnotation(info);
              methodInfoBuilder.addAnnotationName(info);
            }
          }
          break;
          case "AnnotationDefault": {

            Object defaultValue = parseElementValue(classInput, constantPool);
            methodInfoBuilder.setDefaultValue(defaultValue);
          }
          break;
          default:
            classInput.skipBytes(attributeLength);
        }
      }

      infoBuilder.addMethodInfo(methodInfoBuilder.build());
    }
  }

  /**
   * try to read a annotation and it's value  from this class, method or field, but ignore nested
   * annotations
   */
  private AnnotationInfo readAnnotation(final DataInputStream input, Object[] constantPool)
      throws IOException {
    final String annotationFieldDescriptor = readRefString(input, constantPool);
    String annotationClassName;
    List<String> names = ReflectionUtils.parseTypeDescriptor(annotationFieldDescriptor);
    if (names.isEmpty() || names.size() > 1) {
      throw new IllegalArgumentException(
          "Invalid typeDescriptor for annotation" + annotationFieldDescriptor);
    } else {
      annotationClassName = intern(names.get(0));
    }

    final int numElementValuePairs = input.readUnsignedShort();
    Map<String, AnnotationParamValue> paramValues =
        numElementValuePairs > 0 ? new HashMap<>(numElementValuePairs) : Collections.emptyMap();
    for (int i = 0; i < numElementValuePairs; i++) {
      final String elementName = readRefString(input, constantPool);//element_name_index
      Object value = parseElementValue(input, constantPool);
      paramValues.put(elementName, new AnnotationParamValue(elementName, value));
    }

    return new AnnotationInfo(annotationClassName, paramValues);
  }

  private Object parseElementValue(final DataInputStream input, Object[] constantPool)
      throws IOException {
    final int tag = input.readUnsignedByte();
    switch (tag) {
      case 'B':
        return ((Integer) constantPool[input.readUnsignedShort()]).byteValue();
      case 'C':
        return (char) ((Integer) constantPool[input.readUnsignedShort()]).intValue();
      case 'S':
        return ((Integer) constantPool[input.readUnsignedShort()]).shortValue();
      case 'Z':
        return ((Integer) constantPool[input.readUnsignedShort()]) != 0;
      case 'I'://int
      case 'J'://long
      case 'D'://double
      case 'F'://float
      case 's'://string
        return constantPool[input.readUnsignedShort()];//Already in correct type;
      case '@'://Complex(nested) annotation
        return readAnnotation(input, constantPool);
      case '[': {//array_value
        final int count = input.readUnsignedShort();
        Object[] arrayValues = new Object[count];
        for (int i = 0; i < count; i++) {
          //Nested annotation element value
          arrayValues[i] = parseElementValue(input, constantPool);
        }
        return arrayValues;
      }
      case 'c': { //class_info_index
        String typeDescriptor = readRefString(input, constantPool);
        return new AnnotationClassRef(typeDescriptor);
      }
      case 'e': {//enum_constant_index
        final String typeDescriptor = readRefString(input, constantPool);
        final String fieldName = readRefString(input, constantPool);
        List<String> type = ReflectionUtils.parseTypeDescriptor(typeDescriptor);
        if (type.isEmpty() || type.size() > 1) {
          throw new RuntimeException(
              "Illegal element_value enum_constant_index: " + typeDescriptor);
        }
        return new AnnotationEnumRef(/*className*/type.get(0), fieldName);
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
