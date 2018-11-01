package com.common.toolkit.spring.config;

/**
 * dubbo consumer reference annotation
 *
 * @author ewen
 */
public @interface DubboReference {

  String version() default "";

  String group() default "";

}
