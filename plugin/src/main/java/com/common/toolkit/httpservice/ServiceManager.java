package com.common.toolkit.httpservice;

import com.common.toolkit.spring.SpringContextHolder;
import java.util.Map;

/**
 * 服务管理
 *
 * @author ewen
 */
public enum ServiceManager {

  INSTANCE;

  private ServiceContainer serviceContainer;

  ServiceManager() {
    this.serviceContainer = SpringContextHolder.getBean(ServiceContainer.class);
  }

  public Map<String, ServiceEntity> listService() {
    return this.serviceContainer.getServiceMapper();
  }

  public void destory() {
    this.serviceContainer.destroy();
  }

  public Object invoke(String serviceCode, Object args) {
    ServiceEntity serviceEntity = this.serviceContainer.getService(serviceCode);
    return this.serviceContainer.getInvoker().invoke(serviceEntity, args);
  }
}
