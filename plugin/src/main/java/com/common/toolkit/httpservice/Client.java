package com.common.toolkit.httpservice;

/**
 * 客户端
 *
 * @author ewen
 */
public interface Client {

  Object invoke(ServiceEntity serviceEntity, Object arg);
}
