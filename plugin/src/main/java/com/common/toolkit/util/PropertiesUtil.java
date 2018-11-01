package com.common.toolkit.util;

import com.common.toolkit.spring.SpringContextHolder;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

public class PropertiesUtil {

  /**
   * 获取属性配置,提供默认值
   */
  public static <T> T getProperty(String key, Class<T> type, T defaultValue) {
    return getEnv().getProperty(key, type, defaultValue);
  }

  /**
   * 获取属性配置
   */
  public static <T> T getProperty(String key, Class<T> type) {
    return getEnv().getProperty(key, type);
  }

  public static String getProperty(String key) {
    return getEnv().getProperty(key);
  }


  public static String findProperty(String key, String defaultValue) {
    String value = getSystemProperty(key);
    try {
      if (StringUtils.isEmpty(value)) {
        value = getProperty(key, String.class);
      }
    } catch (IllegalStateException e) {
      //ignore
    }
    return StringUtils.isEmpty(value) ? defaultValue : value;
  }

  /**
   * 获取系统属性
   */
  public static String getSystemProperty(String key, String defaultValue) {
    return System.getProperty(key, defaultValue);
  }

  public static String getSystemProperty(String key) {
    return System.getProperty(key);
  }

  /**
   * 优先加载外部属性配置,并放置于spring环境中
   */
  public static void loadProperties(String pattern) throws IOException {
    Resource[] resources = ResourceUtil.getResources(pattern);
    if (resources.length > 0) {
      Resource resource = resources[0];
      String filename = resource.getFilename();
      Properties properties = PropertiesLoaderUtils.loadProperties(resource);
      if (properties != null) {
        putKeyValue(filename, properties);
      }
    }
  }

  private static void putKeyValue(String sourceName, Properties properties) {
    MutablePropertySources ps = ((ConfigurableEnvironment) getEnv()).getPropertySources();
    Enumeration<String> keyEnumeration = (Enumeration<String>) properties.propertyNames();
    while (keyEnumeration.hasMoreElements()) {
      String key = keyEnumeration.nextElement();
      Object value = properties.get(key);
      if (!ps.contains(sourceName)) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        ps.addLast(new MapPropertySource(sourceName, map));
      } else {
        ((PropertiesPropertySource) ps.get(sourceName)).getSource().put(key, value);
      }
    }
  }

  private static Environment getEnv() {
    return getContext().getEnvironment();
  }

  private static ApplicationContext getContext() {
    return SpringContextHolder.getApplicationContext();
  }

}
