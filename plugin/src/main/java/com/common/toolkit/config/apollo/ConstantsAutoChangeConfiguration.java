package com.common.toolkit.config.apollo;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 属性自动变更配置
 *
 * @author ewen
 */
@Configuration
@EnableConfigurationProperties(ConstantsProperties.class)
public class ConstantsAutoChangeConfiguration {

  @Bean
  public ConstantsChangeListener constantsChangeListener(ConstantsProperties constantsProperties) {
    return new ConstantsChangeListener(constantsProperties);
  }

}
