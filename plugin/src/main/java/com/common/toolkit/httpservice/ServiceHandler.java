package com.common.toolkit.httpservice;

/**
 * 服务 handler
 *
 * @author ewen
 */
public interface ServiceHandler {

  /**
   * 服务处理
   */
  Object handleService(String serviceName, String serviceMethod, Object param,
      RequestFacade requestFacade);

  /**
   * 根据条件获取到数据请求地址
   */
  String getServicePath(String serviceName, String serviceMethod);


  /**
   * 设置需要支持的request方法
   */
  void setMethod(String method);

}
