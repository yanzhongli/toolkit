package com.common.toolkit.httpservice;

/**
 * 身份认证
 */
public interface HandlerAuthenticationResolver extends HandlerSupport {

  /**
   * 校验身份
   */
  boolean resolveAuthentication(String serviceName, String serviceMethod, Object param,
      RequestFacade requestFacade);
}
