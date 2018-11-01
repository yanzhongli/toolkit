package com.common.toolkit.config.apollo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ewen
 */
@Getter
@Setter
@ConfigurationProperties("constants.properties")
public class ConstantsProperties {

  private String basePackage;

  private String classPattern;

}