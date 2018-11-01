package com.common.toolkit.httpservice;

import java.util.Map;

/**
 * 服务容器
 *
 * @author ewen
 */
public interface ServiceContainer {

  /**
   * 注册服务
   */
  void register(String serviceCode, ServiceEntity serviceEntity);

  /**
   * 根据服务编码查找服务
   */
  ServiceEntity getService(String serviceCode);

  /**
   * 服务销毁
   */
  void destroy();

  /**
   * 获取服务映射列表
   */
  Map<String, ServiceEntity> getServiceMapper();

  /**
   * 获取服务调用客户端
   */
  <C extends Client> ServiceInvoker<C> getInvoker();

  /**
   * 服务编码定义
   */
  static String getServiceCode(String serviceName, String methodName, Pattern pattern) {
    return pattern.get(serviceName, methodName);
  }

  interface Pattern {

    String get(String serviceName, String methodName);
  }
}
