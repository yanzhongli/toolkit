package com.common.toolkit.httpservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 服务暴露自动配置
 *
 * @author ewen
 */
@Configuration
@EnableConfigurationProperties(HttpServiceProperties.class)
public class HttpServiceExportAutoConfiguration {


}
