package com.common.toolkit.httpservice;


/**
 * 参数解析器或者包装
 */
public interface HandlerParameterResolver extends HandlerSupport {

  /**
   * 解析参数
   */
  Object resolveParameter(Object object, RequestFacade requestFacade);
}
