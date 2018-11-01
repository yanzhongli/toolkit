package com.common.toolkit.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * springContext holder
 */
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

  private static ApplicationContext applicationContext = null;

  private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

  public void setApplicationContext(ApplicationContext applicationContext) {
    logger.debug("注入ApplicationContext到SpringContextHolder:" + applicationContext);

    if (SpringContextHolder.applicationContext != null) {
      logger.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:"
          + SpringContextHolder.applicationContext);
    }

    SpringContextHolder.applicationContext = applicationContext; //NOSONAR
  }

  public void destroy() throws Exception {
    SpringContextHolder.clear();
  }

  /**
   * 取得存储在静态变量中的ApplicationContext.
   */
  public static ApplicationContext getApplicationContext() {
    assertContextInjected();
    return applicationContext;
  }

  public static boolean containsBean(String id) {
    assertContextInjected();
    return applicationContext.containsBean(id);
  }

  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name) {
    return (T) getBean(name, null);
  }

  public static Object getBeanObject(String name) {
    return getBeanObject(name, null);
  }

  public static <T> T getBean(Class<T> requiredType) {
    assertContextInjected();
    return applicationContext.getBean(requiredType);
  }

  public static void clear() {
    logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
    applicationContext = null;
  }

  private static void assertContextInjected() {
    if (applicationContext == null) {
      throw new IllegalStateException(
          "applicationContext未注入,请定义SpringContextHolder为bean");
    }
  }

  public static Object getBeanObject(String name, Class<?> type) {
    assertContextInjected();
    return applicationContext.getBean(name, type);
  }

  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name, Class<?> type) {
    return (T) getBeanObject(name, type);
  }
}
