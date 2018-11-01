package com.common.toolkit.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.Assert;

/**
 * 反射工具类
 */
public abstract class ReflectionUtils {

  private static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

  /**
   * 获取默认的类加载器
   */
  public static final ClassLoader getDefaultClassLoader() {
    try {
      return Thread.currentThread().getContextClassLoader();
    } catch (Throwable e) {
      try {
        return ReflectionUtils.class.getClassLoader();
      } catch (SecurityException se) {
        try {
          return ClassLoader.getSystemClassLoader();
        } catch (SecurityException | IllegalStateException | Error error) {
          return null;
        }
      }
    }
  }

  /**
   * 根据名称获取Class对象
   */
  public static final Class<?> loadClass(String className) {
    try {
      return getDefaultClassLoader().loadClass(className);
    } catch (ClassNotFoundException e1) {
      try {
        return Class.forName(className);
      } catch (ClassNotFoundException e2) {
        throw new RuntimeException(String.format("类[%s]不存在", className), e2);
      }
    }
  }

  /**
   * 根据名称获取Class对象
   */
  @SuppressWarnings("unchecked")
  public static final <T> Class<T> loadClass(String className, Class<T> clazz) {
    Class<?> type = loadClass(className);
    if (clazz.isAssignableFrom(type)) {
      return (Class<T>) type;
    }
    throw new RuntimeException(String.format("类[%s]不可以转换为[%s]", className, clazz.getName()));
  }

  /**
   * 实例化指定的类
   */
  public static final <T> T newInstance(Class<T> clazz) {
    try {
      Constructor<?> constructor = findConstructor(clazz, new Class[0]);
      if (constructor == null) {
        throw new RuntimeException(String.format("无法实例化类[%s]，因为该类未定义默认构造函数", clazz));
      }
      makeAccessibleConstructor(constructor);
      return clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(String.format("实例化类[%s]失败", clazz), e);
    }
  }

  /**
   * 获取指定的属性对象
   */
  public static final Field getField(Class<?> clazz, String fieldName) {
    Field field = null;
    while (clazz != null && field == null) {
      try {
        field = clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
      }
      clazz = clazz.getSuperclass();
    }
    return field;
  }

  /**
   * 设置指定对象指定属性的值（只要属性存在不管是public还是private）
   */
  public static final void setFieldValue(Object object, String fieldName, Object value) {
    Assert.notNull(object);
    Class<?> objectClass = object.getClass();
    Field field = getField(objectClass, fieldName);
    if (field != null) {
      setFieldValue(object, field, conversion(field.getType(), value));
    } else {
      Method method = getWriteMethod(objectClass, fieldName);
      if (method != null) {
        Class<?>[] types = method.getParameterTypes();
        if (types.length == 1) {
          invokeMethod(object, method, conversion(types[0], value));
          return;
        }
      }
      throw new RuntimeException(String.format("类[%s]中不存在属性[%s]", object.getClass(), fieldName));
    }
  }

  /**
   * 获取指定对象指定属性的值（只要属性存在不管是public还是private）
   */
  @SuppressWarnings("unchecked")
  public static final <T> T getFieldValue(Object object, String fieldName, Class<T> fieldClass) {
    Assert.notNull(object);
    Class<?> objectClass = object.getClass();
    Field field = getField(objectClass, fieldName);
    if (field != null) {
      return getFieldValue(object, field, fieldClass);
    }
    Method method = getReadMethod(objectClass, fieldName);
    if (method == null) {
      throw new RuntimeException(String.format("类[%s]中不存在属性[%s]", object.getClass(), fieldName));
    }
    return (T) invokeMethod(object, method);
  }

  /**
   * 设置指定类的静态属性（只要静态属性存在不管是public还是private）
   */
  public static final void setStaticFieldValue(Class<?> objectClass, String fieldName,
      Object value) {
    if (null == objectClass) {
      throw new RuntimeException(String.format("请指定属性[%s]所在类", fieldName));
    }
    Field field = getField(objectClass, fieldName);
    if (null == field) {
      throw new RuntimeException(
          String.format("属性[%s]在类[%s]中不存在", fieldName, objectClass.getName()));
    }
    setFieldValue(null, field, value);
  }

  /**
   * 获取指定类的静态属性（只要静态属性存在不管是public还是private）
   */
  public static final <T> T getStaticFieldValue(Class<?> objectClass, String fieldName,
      Class<T> fieldClass) {
    if (null == objectClass) {
      throw new RuntimeException(String.format("请指定属性[%s]所在类", fieldName));
    }
    Field field = getField(objectClass, fieldName);
    if (null == field) {
      throw new RuntimeException(
          String.format("属性[%s]在类[%s]中不存在", fieldName, objectClass.getName()));
    }
    return getFieldValue(null, field, fieldClass);
  }

  /**
   * 设置指定类的静态常量属性
   */
  public static final void setStaticFinalFieldValue(Class<?> objectClass, String fieldName,
      Object value) {
    if (null == objectClass) {
      throw new RuntimeException(String.format("请指定属性[%s]所在类", fieldName));
    }
    Field field = getField(objectClass, fieldName);
    if (null == field) {
      throw new RuntimeException(
          String.format("属性[%s]在类[%s]中不存在", fieldName, objectClass.getName()));
    }
    setFieldValue(field, "modifiers", field.getModifiers() & ~Modifier.FINAL);
    setFieldValue(null, field, value);
  }

  /**
   * 获取构造函数对象
   */
  public static final Constructor<?> findConstructor(Class<?> clazz, Class<?>[] parameterTypes) {
    try {
      return clazz.getDeclaredConstructor(parameterTypes);
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  /**
   * 调用构造函数
   */
  public static final Object invokeConstructor(final Constructor<?> constructor, Object[] args,
      boolean makeAccessible) {
    boolean isAccessible = constructor.isAccessible();
    if (!isAccessible) {
      constructor.setAccessible(true);
    }
    try {
      return constructor.newInstance(args);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(
          String.format("使用参数为[%s]的构造函数[%s]进行实例化失败", Arrays.toString(args), constructor), e);
    } finally {
      if (!isAccessible) {
        constructor.setAccessible(false);
      }
    }
  }

  /**
   * 设置构造函数为可访问
   */
  public static void makeAccessibleConstructor(final Constructor<?> constructor) {
    if (!constructor.isAccessible()) {
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
        public Object run() {
          constructor.setAccessible(true);
          return null;
        }
      });
    }
  }

  /**
   * 获取对象实现的接口数组
   */
  public static final Class<?>[] findInterfaces(Object object) {
    return findInterfaceList(object).toArray(new Class<?>[0]);
  }

  /**
   * 获取对象实现的接口列表
   */
  public static final Vector<Class<?>> findInterfaceList(Object object) {
    Assert.notNull(object);
    Vector<Class<?>> interfaceList = new Vector<>();
    Class<?> clazz = object.getClass();
    while (clazz != null) {
      Class<?>[] interfaces = clazz.getInterfaces();
      interfaceList.addAll(Arrays.asList(interfaces));
      clazz = clazz.getSuperclass();
    }
    return interfaceList;
  }


  /**
   * 判断类型是否实现指定的接口
   */
  public static final boolean implementsInterface(Class<?> clazz, String interfaceName) {
    Assert.notNull(clazz);
    Assert.notNull(interfaceName);
    while (clazz != null) {
      Class<?>[] interfaces = clazz.getInterfaces();
      if (interfaces != null) {
        for (Class<?> iface : interfaces) {
          if (interfaceName.equals(iface.getName())) {
            return true;
          }
        }
      }
      clazz = clazz.getSuperclass();
    }
    return false;
  }

  /**
   * 判断类型是否为指定类型的子类
   */
  public static final boolean isSubclass(Class<?> clazz, String parentClassName) {
    Assert.notNull(clazz);
    Assert.notNull(parentClassName);
    while (clazz != null) {
      Class<?> superClass = clazz.getSuperclass();
      if (superClass != null && parentClassName.equals(superClass.getName())) {
        return true;
      }
      clazz = superClass;
    }
    return false;
  }


  /**
   * 调用方法
   */
  public static final Object invokeMethod(Object target, Method method, Object... args) {
    boolean isAccessible = method.isAccessible();
    if (!isAccessible) {
      method.setAccessible(true);
    }
    try {
      return method.invoke(target, args);
    } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
      throw new RuntimeException(
          String.format("无法执行参数为[%s]，的方法[%s]", Arrays.toString(args), method.getName()), e);
    } finally {
      if (!isAccessible) {
        method.setAccessible(false);
      }
    }
  }

  /**
   * 获取指定属性的写入方法
   */
  public static final Method getWriteMethod(Class<?> clazz, String name) {
    try {
      return getReadOrWriteMethods(clazz, name, false);
    } catch (IntrospectionException e) {
      throw new RuntimeException(String.format("在类[%s]中获取属性[%s]写入方法异常，原因：", clazz, name), e);
    }
  }

  /**
   * 获取指定属性的读取方法
   */
  public static final Method getReadMethod(Class<?> clazz, String name) {
    try {
      return getReadOrWriteMethods(clazz, name, false);
    } catch (IntrospectionException e) {
      throw new RuntimeException(String.format("在类[%s]中获取属性[%s]读取方法异常，原因：", clazz, name), e);
    }
  }

  /**
   * 获取所有存在set方法的属性和方法对应关系
   */
  public static final Map<String, Method> findWriteMethods(Class<?> clazz) {
    try {
      return findReadOrWriteMethods(clazz, false);
    } catch (IntrospectionException e) {
      throw new RuntimeException(String.format("在类[%s]中查找写入方法异常，原因：", clazz), e);
    }
  }


  public static Object conversion(Class<?> type, Object object) {
    if (type.equals(int.class) || type.equals(Integer.class)) {
      return Integer.valueOf(object.toString());
    } else if (type.equals(double.class) || type.equals(Double.class)) {
      return Double.valueOf(object.toString());
    } else if (type.equals(float.class) || type.equals(Float.class)) {
      return Float.valueOf(object.toString());
    } else if (type.equals(long.class) || type.equals(Long.class)) {
      return Long.valueOf(object.toString());
    } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
      return Boolean.valueOf(object.toString());
    } else {
      return object.toString();
    }
  }

  public static final Method getReadOrWriteMethods(Class<?> clazz, String name, boolean isRead)
      throws IntrospectionException {
    PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clazz)
        .getPropertyDescriptors();
    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
      if (propertyDescriptor.getName().equals(name)) {
        return isRead ? propertyDescriptor.getReadMethod() : propertyDescriptor.getWriteMethod();
      }
    }
    return null;
  }

  public static final Map<String, Method> findReadOrWriteMethods(Class<?> clazz, boolean isRead)
      throws IntrospectionException {
    Map<String, Method> result = new HashMap<String, Method>();
    PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clazz)
        .getPropertyDescriptors();
    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
      Method method =
          isRead ? propertyDescriptor.getReadMethod() : propertyDescriptor.getWriteMethod();
      if (null != method) {
        result.put(propertyDescriptor.getName(), method);
      }
    }
    return result;
  }

  public static void setFieldValue(Object object, Field field, Object value) {// 设置属性值
    boolean isAccessible = field.isAccessible();
    if (!isAccessible) {
      field.setAccessible(true);
    }
    try {
      field.set(object, value);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException(e);
    } finally {
      if (!isAccessible) {
        field.setAccessible(false);
      }
    }
  }


  public static <T> T getFieldValue(Object object, Field field, Class<T> clazz) {// 获取属性值
    boolean isAccessible = field.isAccessible();
    if (!isAccessible) {
      field.setAccessible(true);
    }
    try {
      return (T) ClazzUtil.cast(field.get(object), clazz);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException(e);
    } finally {
      if (!isAccessible) {
        field.setAccessible(false);
      }
    }
  }

  private static Field getDeclaredField(Object object, String filedName) {
    for (Class<?> superClass = object.getClass(); superClass != Object.class;
        superClass = superClass.getSuperclass()) {
      try {
        return superClass.getDeclaredField(filedName);
      } catch (NoSuchFieldException e) {
        //Field 不在当前类定义, 继续向上转型
      }
    }
    return null;
  }

  public static Object getFieldValue(Object object, String fieldName) {
    Field field = getDeclaredField(object, fieldName);
    if (field == null) {
      throw new IllegalArgumentException("Could not find field ["
          + fieldName + "] on target [" + object + "]");
    }

    boolean isAccessible = field.isAccessible();
    if (!isAccessible) {
      field.setAccessible(true);
    }

    Object result;
    try {
      result = field.get(object);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } finally {
      if (!isAccessible) {
        field.setAccessible(false);
      }
    }

    return result;
  }

  /**
   * 获取 目标对象
   *
   * @param proxy 代理对象
   */
  public static Object getTarget(Object proxy) throws Exception {

    if (!AopUtils.isAopProxy(proxy)) {
      return proxy;//不是代理对象
    }

    if (AopUtils.isJdkDynamicProxy(proxy)) {
      return getJdkDynamicProxyTargetObject(proxy);
    } else { //cglib
      return getCglibProxyTargetObject(proxy);
    }


  }


  private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
    Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
    h.setAccessible(true);
    Object dynamicAdvisedInterceptor = h.get(proxy);

    Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
    advised.setAccessible(true);

    Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource()
        .getTarget();

    return target;
  }


  private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
    Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
    h.setAccessible(true);
    AopProxy aopProxy = (AopProxy) h.get(proxy);

    Field advised = aopProxy.getClass().getDeclaredField("advised");
    advised.setAccessible(true);

    Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();

    return target;
  }


}