package com.common.toolkit.util;

import com.common.toolkit.config.apollo.GenericClass;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

public abstract class ClazzUtil {

  private static Logger logger = LoggerFactory.getLogger(ClazzUtil.class);

  /**
   * 指定基础包名及类名（不带路径）匹配模式，查找出匹配的类名（带路径）。
   */
  public static List<String> findClassName(String basePackage, String classPattern)
      throws IOException {
    ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    String basePackageWithResPath = ClassUtils.convertClassNameToResourcePath(basePackage);
    String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
        + basePackageWithResPath + "/**/" + classPattern;
    Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
    List<String> classNameList = new LinkedList<>();
    for (Resource res : resources) {
      logger.debug(res.getURL().getPath());
      int index = res.getURL().getPath().indexOf(basePackageWithResPath);
      if (index >= 0) {
        String classNamePath = res.getURL().getPath().substring(index);
        if ("jar".equals(res.getURL().getProtocol())) {
          int idx = classNamePath.indexOf("!");
          if (idx != -1) {
            classNamePath = classNamePath.substring(idx + 1);
          }
        }
        if (classNamePath.startsWith("/")) {
          classNamePath = classNamePath.substring(1);
        }
        String className = ClassUtils.convertResourcePathToClassName(classNamePath);
        if (className.endsWith(".class")) {
          className = className.substring(0, className.length() - 6);
        }
        logger.debug("find class: " + className);
        classNameList.add(className);
      }

    }

    return classNameList;
  }

  /**
   * 获取数组中对象类型
   */
  public static Class<?> getTypeOfObjInArray(Field arrField) {
    if (!arrField.getType().isArray()) {
      throw new IllegalArgumentException("The type of argument is not a Array type!!!");
    }
    Object[] arr;
    try {
      arr = (Object[]) arrField.get(null);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(
          "Can't not access field value. filedName=" + arrField.getName());
    }
    if (arr != null && arr.length > 0) {
      Object obj = arr[0];
      return obj.getClass();
    }
    return arr.getClass().getComponentType();
  }

  public static Class<?> getTypeOfList(Field listField) {
    if (!List.class.isAssignableFrom(listField.getType())) {
      throw new IllegalArgumentException("The type of argument is not a Collection type!!!");
    }
    List<?> list;
    try {
      list = (List) listField.get(null);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(
          "Can't not access field value. filedName=" + listField.getName());
    }
    if (list != null && !list.isEmpty()) {
      Object obj = list.iterator().next();
      return obj.getClass();
    }
    return getListClass(listField);
  }

  public static Class<?> getTypeOfKeyInMap(Field mapField) {
    if (!Map.class.isAssignableFrom(mapField.getType())) {
      throw new IllegalArgumentException("The type of argument is not a Collection type!!!");
    }
    Map<?, ?> map;
    try {
      map = (Map) mapField.get(null);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(
          "Can't not access field value. filedName=" + mapField.getName());
    }
    if (map != null && !map.isEmpty()) {
      Object obj = map.keySet().iterator().next();
      return obj.getClass();
    }
    return getMapKeyValueClass(mapField, true);
  }

  private static Class<?> getMapKeyValueClass(Field mapField, boolean isKey) {
    Type type = mapField.getGenericType();
    if (type instanceof Class) {
      //不存在泛型参数
      return Object.class;
    } else {
      GenericClass genericClass = new GenericClass();
      com.common.toolkit.util.ClassUtils.getGenericClass(type, genericClass);
      GenericClass[] genericClasses = genericClass.getGenericClasses();
      return genericClasses[isKey ? 0 : 1].getRawClass();
    }
  }

  private static Class<?> getListClass(Field listField) {
    Type type = listField.getGenericType();
    if (type instanceof Class) {
      //不存在泛型参数
      return Object.class;
    } else {
      GenericClass genericClass = new GenericClass();
      com.common.toolkit.util.ClassUtils.getGenericClass(type, genericClass);
      GenericClass[] genericClasses = genericClass.getGenericClasses();
      return genericClasses[0].getRawClass();
    }
  }


  public static Class<?> getTypeOfValueInMap(Field mapField) {
    if (!Map.class.isAssignableFrom(mapField.getType())) {
      throw new IllegalArgumentException("The type of argument is not a Collection type!!!");
    }
    Map<?, ?> map;
    try {
      map = (Map) mapField.get(null);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(
          "Can't not access field value. filedName=" + mapField.getName());
    }
    if (map != null && !map.isEmpty()) {
      Object obj = map.values().iterator().next();
      return obj.getClass();
    }
    return getMapKeyValueClass(mapField, false);
  }

  /**
   * 根据名称（格式为 枚举类名.成员名）获取枚举类中成员对象。
   */
  public static Object getObjFromEnum(Class<?> enumClass, String enumConstName) {
    //String[] enumClzConstName = enumConstName.split(".");
    int idx = enumConstName.indexOf('.');
    String constName;
    if (idx > 0) {
      String enumClassName = enumConstName.substring(0, idx);
      if (!enumClass.getName().endsWith("." + enumClassName)
          && !enumClass.getName().endsWith("$" + enumClassName)) {
        throw new IllegalArgumentException("Invalid enumConstName parameter!!!");
      }
      constName = enumConstName.substring(idx + 1);
    } else {
      constName = enumConstName;
    }

    Object[] enumConstants = enumClass.getEnumConstants();
    for (Object enumConstant : enumConstants) {
      if (enumConstant.toString().equals(constName)) {
        return enumConstant;
      }
    }
    return null;
  }

  public static Object cast(Object value, Class<?> clz) {
    if (value == null) {
      return null;
    }
    String strValue = String.valueOf(value);
    if (clz.equals(Integer.class)) {
      return Integer.valueOf(strValue);
    } else if (clz.equals(Long.class)) {
      return Long.valueOf(strValue);
    } else if (clz.equals(Short.class)) {
      return Short.valueOf(strValue);
    } else if (clz.equals(Double.class)) {
      return Double.valueOf(strValue);
    } else if (clz.equals(Float.class)) {
      return Float.valueOf(strValue);
    } else if (clz.equals(String.class)) {
      return strValue;
    } else {
      return value;
    }
  }
}
