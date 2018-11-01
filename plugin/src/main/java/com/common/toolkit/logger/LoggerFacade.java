package com.common.toolkit.logger;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 日志门户（运行时调用）
 *
 * @author ewen
 */
public class LoggerFacade extends LoggerSupport implements DynamicLog {

  private DynamicLog dynamicLog;
  private final AtomicBoolean configured = new AtomicBoolean(false);

  public LoggerFacade() {
    this(null);
  }

  public LoggerFacade(String logFile) {
    super();
    Map<String, DynamicLog> dynamicLogMap = mapUpdater();
    DynamicLog dynamicLog = dynamicLogMap.get(bindType);
    if (dynamicLog == null) {
      ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
      if (loggerFactory instanceof NOPLoggerFactory
          || loggerFactory instanceof SubstituteLoggerFactory) {
        // do nothing
        this.dynamicLog = new DynamicLog() {
          @Override
          public void setPackageLogLevel(String pk, String logLevel) {}

          @Override
          public void setGlobalLogLevel(String logLevel) {}

          @Override
          public void reset() {}
        };
      } else {
        throw new InitializationException("未适配到日志实现!");
      }
    }
    this.dynamicLog = dynamicLog;
    boolean isConfigured = true;
    if (!StringUtils.isEmpty(logFile)) {
      try {
        this.dynamicLog.configure(logFile);
      } catch (Exception e) {
        e.printStackTrace();
        isConfigured = false;
      }
      if (isConfigured) {
        configured.set(true);
      }
    }
  }

  @Override
  public void configure(String logFile) throws Exception {
    if (configured.compareAndSet(false, true)) {
      dynamicLog.configure(logFile);
    }
  }

  @Override
  public void setPackageLogLevel(String pks, String logLevel) {
    dynamicLog.setPackageLogLevel(pks, logLevel);
  }

  @Override
  public void setGlobalLogLevel(String logLevel) {
    dynamicLog.setGlobalLogLevel(logLevel);
  }

  @Override
  public void reset() {
    dynamicLog.reset();
  }

  public static Map<String, DynamicLog> mapUpdater() {
    Map<String, DynamicLog> all = Maps.newHashMap();
    EnumUtils.getEnumList(LogType.class).stream()
        .forEach(lt -> all.put(lt.bindType, lt.dynamicLog));
    return all;
  }

  public static LogType findByLogType(String logType) {
    Optional<LogType> any = EnumUtils.getEnumList(LogType.class).stream()
        .filter((type) -> type.bindType != null && type.bindType.equals(logType)).findAny();
    return any.isPresent() ? any.get() : LogType.NO_BIND;
  }

  enum LogType {
    LOG4J("org.apache.logging.slf4j.Log4jLoggerFactory", new Log4jLevelUpdater()),
    LOGBACK("ch.qos.logback.classic.util.ContextSelectorStaticBinder", new LogbackLevelUpdater()),
    NO_BIND(null, null);

    private String bindType;
    private DynamicLog dynamicLog;

    LogType(String bindType, DynamicLog dynamicLog) {
      this.bindType = bindType;
      this.dynamicLog = dynamicLog;
    }

    public boolean isBind() {
      return !StringUtils.isEmpty(bindType);
    }
  }
}