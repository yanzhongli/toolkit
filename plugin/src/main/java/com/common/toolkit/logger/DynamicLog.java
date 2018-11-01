package com.common.toolkit.logger;

import java.io.IOException;
import java.net.URL;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public interface DynamicLog {

  /**
   * 设置包级日志级别
   */
  void setPackageLogLevel(String pks, String logLevel);

  /**
   * 设置全局日志级别
   */
  void setGlobalLogLevel(String logLevel);

  /**
   * 重置日志配置
   */
  void reset();

  /**
   * 配置日志
   */
  default void configure(String logFile) throws Exception {
    //
  }

  default URL getFileLocation(String logFile) throws Exception {
    String logFileLocal = logFile;
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    if (!logFile.startsWith("classpath:")) {
      logFileLocal = "classpath:" + logFile;
    }
    Resource resource = resourceLoader.getResource(logFileLocal);
    return resource.getURL();
  }

  class UnsupportedLogLevel extends RuntimeException {

    public UnsupportedLogLevel(String msg) {
      super(msg);
    }
  }

}