package com.common.toolkit.httpservice.config;

import com.common.toolkit.httpservice.HandleModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ewen
 */
@Setter
@Getter
@ConfigurationProperties("http.service")
public class HttpServiceProperties {

  private boolean exportEnable;

  private HandleModel handleModel;

  private String[] servicePath;

  private String scanPackages;

}
