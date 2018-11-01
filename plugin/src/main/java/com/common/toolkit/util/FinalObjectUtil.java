package com.common.toolkit.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinalObjectUtil {

  private static Logger log = LoggerFactory.getLogger(FinalObjectUtil.class);

  public static void setStringValue(String oldValue, String newValue) {
    char[] value = newValue.toCharArray();
    Field field;
    try {
      field = String.class.getDeclaredField("value");
      field.setAccessible(true);
      field.set(oldValue, value);
      field = String.class.getDeclaredField("offset");
      field.setAccessible(true);
      field.set(oldValue, 0);
      field = String.class.getDeclaredField("count");
      field.setAccessible(true);
      field.set(oldValue, value.length);
      field = String.class.getDeclaredField("hash");
      field.setAccessible(true);
      field.set(oldValue, 0);
    } catch (Exception e) {
      String errMsg = String.format("Can't set final String [%s] to new value!", oldValue);
      log.error(errMsg, e);
      throw new IllegalArgumentException(errMsg);
    }
  }

  public static void setIntegerValue(Integer oldValue, Integer newValue) {
    Field field;
    try {
      field = Integer.class.getDeclaredField("value");
      field.setAccessible(true);
      field.set(oldValue, newValue);
    } catch (Exception e) {
      String errMsg = String.format("Can't set final Integer [%d] to new value!", oldValue);
      log.error(errMsg, e);
      throw new IllegalArgumentException(errMsg);
    }
  }

  public static void setLongValue(Long oldValue, Long newValue) {
    Field field;
    try {
      field = Long.class.getDeclaredField("value");
      field.setAccessible(true);
      field.set(oldValue, newValue);
    } catch (Exception e) {
      String errMsg = String.format("Can't set final Integer [%d] to new value!", oldValue);
      log.error(errMsg, e);
      throw new IllegalArgumentException(errMsg);
    }
  }

  public static void setShortValue(Short oldValue, Short newValue) {
    Field field;
    try {
      field = Short.class.getDeclaredField("value");
      field.setAccessible(true);
      field.set(oldValue, newValue);
    } catch (Exception e) {
      String errMsg = String.format("Can't set final Integer [%d] to new value!", oldValue);
      log.error(errMsg, e);
      throw new IllegalArgumentException(errMsg);
    }
  }

  public static void setDoubleValue(Double oldValue, Double newValue) {
    Field field;
    try {
      field = Double.class.getDeclaredField("value");
      field.setAccessible(true);
      field.set(oldValue, newValue);
    } catch (Exception e) {
      String errMsg = String.format("Can't set final Integer [%f] to new value!", oldValue);
      log.error(errMsg, e);
      throw new IllegalArgumentException(errMsg);
    }
  }

  public static void setFloatValue(Float oldValue, Float newValue) {
    Field field;
    try {
      field = Float.class.getDeclaredField("value");
      field.setAccessible(true);
      field.set(oldValue, newValue);
    } catch (Exception e) {
      String errMsg = String.format("Can't set final Integer [%f] to new value!", oldValue);
      log.error(errMsg, e);
      throw new IllegalArgumentException(errMsg);
    }
  }

  public static void setValue(Object oldValue, Object newValue) {
    if (oldValue instanceof Integer) {
      setIntegerValue((Integer) oldValue, (Integer) newValue);
    } else if (oldValue instanceof Long) {
      setLongValue((Long) oldValue, (Long) newValue);
    } else if (oldValue instanceof Short) {
      setShortValue((Short) oldValue, (Short) newValue);
    } else if (oldValue instanceof Double) {
      setDoubleValue((Double) oldValue, (Double) newValue);
    } else if (oldValue instanceof Float) {
      setFloatValue((Float) oldValue, (Float) newValue);
    } else if (oldValue instanceof String) {
      setStringValue((String) oldValue, (String) newValue);
    }
  }

  /**
   * 反射情况下设值
   */
  public static void setValue(Field targetField, Object newValue) {
    int modBak = targetField.getModifiers();
    Field modifiersField = null;
    try {
      targetField.setAccessible(true);
      modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      if (Modifier.isFinal(targetField.getModifiers())) {
        //去掉final修饰符
        modifiersField.setInt(targetField, targetField.getModifiers() - Modifier.FINAL);
      }
      targetField.set(null, newValue);
    } catch (Exception e) {
      String errMsg = String.format("Can't set field [%s] to new value!", targetField.toString());
      log.error(errMsg, e);
      throw new IllegalArgumentException(errMsg);
    } finally {
      if (targetField.getModifiers() != modBak && modifiersField != null) {
        //恢复去掉的final修饰符
        try {
          modifiersField.setInt(targetField, modBak);
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
      }
    }
  }

}
