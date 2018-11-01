package com.common.toolkit.httpservice;

/**
 * 日志记录
 */
public interface HandlerLogResolver extends HandlerSupport {

  /**
   * 处理日志
   */
  void resolveLog(ResultDecorator result, RequestFacade requestFacade);
}
