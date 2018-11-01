package com.common.toolkit.httpservice;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述服务
 *
 * @author ewen
 */
@Getter
@Setter
public class ServiceEntity {

  /**
   * 服务编码,唯一
   */
  private String serviceCode;

  /**
   * 服务方法
   */
  private MethodEntity methodEntity;

  /**
   * 服务类型
   */
  private Class<?> serviceType;

}
