package com.common.toolkit.util;

import com.common.toolkit.config.apollo.GenericClass;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

public class ClassUtils extends org.springframework.util.ClassUtils {

  public static void getGenericClass(Type type, GenericClass genericClass) {
    if (type == null) {
      return;
    }
    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;

      genericClass.setRawClass((Class<?>) parameterizedType.getRawType());

      Type[] args = parameterizedType.getActualTypeArguments();
      if (args != null && args.length > 0) {
        GenericClass[] genericClasses = new GenericClass[args.length];
        for (int i = 0; i < args.length; i++) {
          Type t = args[i];
          GenericClass gc = new GenericClass();
          if (t instanceof ParameterizedType) {
            getGenericClass(t, gc);
          } else {
            if (t instanceof WildcardType) {
              throw new RuntimeException("不确定的泛型参数'?'");
            }
            Class<?> clazz = (Class<?>) t;
            gc.setRawClass(clazz);
          }
          genericClasses[i] = gc;
        }
        genericClass.setGenericClasses(genericClasses);
      }
    }
  }

  public static Class<?>[] getGenericClass(Type type) {
    if (type == null) {
      return null;
    } else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      if (parameterizedType.getOwnerType() != null) {
      }
      Type[] args = parameterizedType.getActualTypeArguments();
      if (args != null && args.length > 0) {
        Class<?>[] clazzs = new Class<?>[args.length];
				/*
				Class<?> genericClazz = (Class<?>)args[0]; //得到泛型里的class类型对象。
				if(genericClazz!=null)
					return genericClazz;
					*/
        for (int i = 0; i < args.length; i++) {
          Type t = args[i];
          if (t instanceof ParameterizedType) {
            //return getGenericClass(t);
            ParameterizedType subType = (ParameterizedType) t;
            //Type[] args = parameterizedType.getActualTypeArguments();
            Class<?> rawClass = (Class<?>) parameterizedType.getRawType();
            if (Map.class.isAssignableFrom(rawClass)) {
              return getGenericClass(t);
            } else {
              clazzs[i] = (Class<?>) subType.getRawType();
            }
          } else {
            clazzs[i] = (Class<?>) args[i];
          }
        }
        return clazzs;
      }
    }
    return null;
  }

  public static String[] convertClass2String(Class<?>[] types) {
    if (ArrayUtils.isEmpty(types)) {
      return null;
    }
    String[] typeString = new String[types.length];
    for (int i = 0; i < types.length; i++) {
      typeString[i] = types[i].getName();
    }
    return typeString;
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

  private static void makeAccessible(Field field) {
    if (!Modifier.isPublic(field.getModifiers())) {
      field.setAccessible(true);
    }
  }


}
