package com.common.toolkit.logger;


import java.util.Arrays;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * log4j日志级别变更,需要使用log4j1-2包，将1过渡到2,log4j2性能更6
 *
 * @author ewen
 */
public class Log4jLevelUpdater implements DynamicLog {

  @Override
  public void setPackageLogLevel(String pks, String logLevel) {
    Level level = Level.toLevel(logLevel);
    String[] pkArray = StringUtils
        .tokenizeToStringArray(pks, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
    Arrays.stream(pkArray).forEach(pk -> Configurator.setLevel(pk, level));
  }

  @Override
  public void setGlobalLogLevel(String logLevel) {
    Level level = Level.toLevel(logLevel);
    Configurator.setRootLevel(level);
  }

  @Override
  public void reset() {
    LoggerContext.getContext(false).reconfigure();
  }

  @Override
  public void configure(String logFile) throws Exception {
    ConfigurationSource source = new ConfigurationSource(getFileLocation(logFile).openStream());
    Configurator.initialize(null, source);
  }
}
