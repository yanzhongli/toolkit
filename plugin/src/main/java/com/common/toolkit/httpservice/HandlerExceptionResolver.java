package com.common.toolkit.httpservice;

/**
 * 异常对象解析器或者包装
 */
public interface HandlerExceptionResolver extends HandlerSupport {

  /**
   * 异常处理
   */
  Object resolveException(Exception exception);
}
