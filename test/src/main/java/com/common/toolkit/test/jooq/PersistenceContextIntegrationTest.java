package com.common.toolkit.test.jooq;

import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.jooq.ExecuteContext;
import org.jooq.SQLDialect;
import org.jooq.TransactionProvider;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.SpringTransactionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan({"com.common.toolkit.database.jooq.db.public_.tables"})
@EnableTransactionManagement
@PropertySource("classpath:intro_config.properties")
public class PersistenceContextIntegrationTest {

  @Autowired
  private Environment environment;

  @Bean
  public DataSource dataSource() {
    JdbcDataSource dataSource = new JdbcDataSource();

    dataSource.setUrl(environment.getRequiredProperty("db.url"));
    dataSource.setUser(environment.getRequiredProperty("db.username"));
    dataSource.setPassword(environment.getRequiredProperty("db.password"));

    return dataSource;
  }

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public TransactionAwareDataSourceProxy transactionAwareDataSource() {
    return new TransactionAwareDataSourceProxy(dataSource());
  }

  @Bean
  public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource());
  }

  @Bean
  public DataSourceConnectionProvider connectionProvider() {
    return new DataSourceConnectionProvider(transactionAwareDataSource());
  }

  @Bean
  public SpringTransactionProvider transactionProvider(
      PlatformTransactionManager txManager) {
    return new SpringTransactionProvider(txManager);
  }

  @Bean
  public ExceptionTranslator exceptionTransformer() {
    return new ExceptionTranslator();
  }

  @Bean
  public DefaultDSLContext dsl(org.jooq.Configuration configuration) {
    return new DefaultDSLContext(configuration);
  }

  @Bean
  public DefaultConfiguration configuration(TransactionProvider transactionProvider) {
    DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
    jooqConfiguration.set(connectionProvider());
    jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));
    jooqConfiguration.set(transactionProvider);
    String sqlDialectName = environment.getRequiredProperty("jooq.sql.dialect");
    SQLDialect dialect = SQLDialect.valueOf(sqlDialectName);
    jooqConfiguration.set(dialect);

    return jooqConfiguration;
  }

  public static class ExceptionTranslator extends DefaultExecuteListener {

    @Override
    public void exception(ExecuteContext context) {
      SQLDialect dialect = context.configuration().dialect();
      SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dialect.name());

      context.exception(translator
          .translate("Access database using jOOQ", context.sql(), context.sqlException()));
    }
  }
}