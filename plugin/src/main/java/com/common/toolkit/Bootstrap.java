package com.common.toolkit;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.common.toolkit.spring.SpringContextHolder;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author ewen
 */
@EnableDubboConfiguration
@SpringBootApplication(scanBasePackages = {"com.common.toolkit"})
public class Bootstrap {

  public static void main(String[] args) {

    new SpringApplicationBuilder(Bootstrap.class)
        .web(WebApplicationType.SERVLET).run(args);
  }

  @Bean
  public SpringContextHolder springContextHolder() {
    return new SpringContextHolder();
  }

}
