package com.common.toolkit.logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.slf4j.helpers.Util;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author ewen
 */
public abstract class LoggerSupport {

  public static final String LOG_BIND_CLASS = "org.slf4j.impl.StaticLoggerBinder";
  public static final String FIND_LOG_TYPE_METHOD = "getLoggerFactoryClassStr";
  protected String bindType;

  public LoggerSupport() {
    findLogBindType();
  }

  private void findLogBindType() {
    if (ClassUtils
        .isPresent(LOG_BIND_CLASS, LoggerFacade.class.getClassLoader())) {
      Object instance = getInstance();
      if (instance != null) {
        Method method = ReflectionUtils
            .findMethod(instance.getClass(), FIND_LOG_TYPE_METHOD);
        bindType = (String) ReflectionUtils.invokeMethod(method, instance);
      }
    } else {
      Util.report("未找到绑定的日志系统！");
    }
  }

  private Object getInstance() {
    Object instance = null;
    try {
      Class<?> binderClass = Class.forName(LOG_BIND_CLASS);
      Field singleton = ReflectionUtils.findField(binderClass, "SINGLETON");
      singleton.setAccessible(true);
      instance = singleton.get(null);
    } catch (ClassNotFoundException e) {
      Util.report("未找到类！", e);
    } catch (IllegalAccessException e) {
      Util.report("没有访问权限！", e);
    }
    return instance;
  }
}
