package com.common.toolkit.config.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Collections;
import java.util.Set;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 例子： 动态下发数据源配置，无需重启应用
 *
 * @author ewen
 */
@Configuration
@ConditionalOnBean(DataSourceProperties.class)
public class DataSourceConfiguration {

  Logger logger = LoggerFactory.getLogger(getClass());

  private final static String DATASOURCE_TAG = "db";

  @Autowired
  ApplicationContext context;

  @ApolloConfig
  Config config;

  @Bean("dataSource")
  public DynamicDataSource dynamicDataSource() {
    DynamicDataSource source = new DynamicDataSource();
    source.setTargetDataSources(Collections.singletonMap(DATASOURCE_TAG, dataSource()));
    return source;
  }

  @ApolloConfigChangeListener
  public void onChange(ConfigChangeEvent changeEvent) {
    Set changedKeys = changeEvent.changedKeys();
    if (changedKeys.contains("spring.datasource.url")) {
      DynamicDataSource source = context.getBean(DynamicDataSource.class);
      source.setTargetDataSources(Collections.singletonMap(DATASOURCE_TAG, dataSource()));
      source.afterPropertiesSet();
      logger.info("动态切换数据源为：{}", config.getProperty("spring.datasource.url", ""));
    }
  }

  public DataSource dataSource() {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(config.getProperty("spring.datasource.url", ""));
    dataSource.setUsername(config.getProperty("spring.datasource.username", ""));
    dataSource.setPassword(config.getProperty("spring.datasource.password", ""));
    return dataSource;
  }

  static class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
      return DATASOURCE_TAG;
    }
  }
}