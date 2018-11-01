package com.common.toolkit.logger.spring;

import com.common.toolkit.logger.DynamicLog;
import java.lang.reflect.Method;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.logging.AbstractLoggingSystem;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * spring boot 日志系统，硬编码方式设置日志级别。 {@link LoggingApplicationListener}
 *
 * @author ewen
 */
public class SpringbootLoggingSystem implements DynamicLog {

  @Autowired
  @Qualifier(LoggingApplicationListener.LOGGING_SYSTEM_BEAN_NAME)
  private LoggingSystem loggingSystem;


  @Override
  public void setPackageLogLevel(String pks, String logLevel) {
    assertSupportedLevel(logLevel);
    loggingSystem.setLogLevel(pks, LogLevel.valueOf(logLevel.toUpperCase()));
  }

  @Override
  public void setGlobalLogLevel(String logLevel) {
    assertSupportedLevel(logLevel);
    loggingSystem
        .setLogLevel(LoggingSystem.ROOT_LOGGER_NAME, LogLevel.valueOf(logLevel.toUpperCase()));
  }

  private void assertSupportedLevel(String logLevel) {
    Set<LogLevel> supportedLogLevels = loggingSystem.getSupportedLogLevels();
    boolean isSupported;
    if (!StringUtils.isEmpty(logLevel)) {
      LogLevel ll = LogLevel.valueOf(logLevel.toUpperCase());
      if (ll == null) {
        isSupported = false;
      } else {
        isSupported = supportedLogLevels.contains(ll);
      }
    } else {
      isSupported = false;
    }
    if (!isSupported) {
      throw new UnsupportedLogLevel("不支持的日志级别");
    }
  }

  @Override
  public void reset() {
    if (loggingSystem instanceof AbstractLoggingSystem) {
      Method reinitialize = ReflectionUtils.findMethod(loggingSystem.getClass(), "reinitialize");
      ReflectionUtils.invokeMethod(reinitialize, loggingSystem, null);
    }
  }
}
