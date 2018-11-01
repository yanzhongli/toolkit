package com.common.toolkit.httpservice.config;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.spring.boot.DubboConsumerAutoConfiguration;
import com.alibaba.dubbo.spring.boot.DubboProperties;
import com.common.toolkit.httpservice.DubboServiceContainer;
import com.common.toolkit.httpservice.HttpServiceExporter;
import com.common.toolkit.httpservice.ServiceContainer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ewen
 */
@Configuration
@AutoConfigureAfter(HttpServiceExportAutoConfiguration.class)
@ConditionalOnProperty(prefix = "http.service.", name = "exportEnable", havingValue = "true")
public class HttpServiceCommonAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public HttpServiceExporter httpServiceExporter(HttpServiceProperties httpServiceProperties) {
    HttpServiceExporter httpServiceExporter = new HttpServiceExporter();
    httpServiceExporter.setHttpServiceProperties(httpServiceProperties);
    return httpServiceExporter;
  }


  @Configuration
  @ConditionalOnClass(Service.class)
  @AutoConfigureAfter(DubboConsumerAutoConfiguration.class)
  @ConditionalOnProperty(prefix = "http.service.", name = "handleModel", havingValue = "remote")
  public class DubboServiceContainerConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public ServiceContainer serviceContainer(DubboProperties dubboProperties,
        HttpServiceProperties httpServiceProperties, ApplicationContext applicationContext) {
      DubboServiceContainer dubboServiceContainer = new DubboServiceContainer(
          (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory());
      dubboServiceContainer.setDubboProperties(dubboProperties);
      dubboServiceContainer.setScanPackage(httpServiceProperties.getScanPackages());
      return dubboServiceContainer;
    }
  }

}
