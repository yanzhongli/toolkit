package com.common.toolkit.httpservice;


import lombok.Getter;
import lombok.Setter;

/**
 * 服务调用
 *
 * @author ewen
 */
@Getter
@Setter
public class ServiceInvoker<C extends Client> {

  private C client;

  public ServiceInvoker(C client) {
    this.client = client;
  }

  public Object invoke(ServiceEntity serviceEntity,Object args) {
    return client.invoke(serviceEntity, args);
  }


}
