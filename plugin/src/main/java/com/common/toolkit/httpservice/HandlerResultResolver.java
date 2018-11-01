package com.common.toolkit.httpservice;


/**
 * 结果对象解析器或者包装
 */
public interface HandlerResultResolver extends HandlerSupport {

  /**
   * 解析结果对象
   */
  ResultDecorator resolveResult(ResultDecorator resultVisitor, Object paramObj,
      RequestFacade requestFacade);
}
