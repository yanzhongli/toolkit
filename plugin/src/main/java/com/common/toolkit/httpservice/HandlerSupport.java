package com.common.toolkit.httpservice;

/**
 * 判断是否支持操作接口
 */
public interface HandlerSupport {

  /**
   * 是否支持
   */
  boolean supports(String path);

}
