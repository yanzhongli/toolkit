package com.common.toolkit.logger;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import java.io.IOException;
import java.util.Arrays;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * logback
 *
 * @author ewen
 */
public class LogbackLevelUpdater implements DynamicLog {

  @Override
  public void setPackageLogLevel(String pks, String logLevel) {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    ch.qos.logback.classic.Level level = ch.qos.logback.classic.Level.toLevel(logLevel);
    String[] pkArray = StringUtils
        .tokenizeToStringArray(pks, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
    Arrays.stream(pkArray).forEach(pk -> loggerContext.getLogger(pk).setLevel(level));
  }

  @Override
  public void setGlobalLogLevel(String logLevel) {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    ch.qos.logback.classic.Level level = ch.qos.logback.classic.Level.toLevel(logLevel);
    loggerContext.getLogger("root").setLevel(level);
  }

  @Override
  public void reset() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    loggerContext.reset();
  }

  @Override
  public void configure(String logFile) throws Exception {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(loggerContext);
    loggerContext.reset();
    configurator.doConfigure(getFileLocation(logFile).openStream());
  }
}