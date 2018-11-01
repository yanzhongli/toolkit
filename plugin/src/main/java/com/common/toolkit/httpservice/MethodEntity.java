package com.common.toolkit.httpservice;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述服务方法实体
 *
 * @author ewen
 */
@Setter
@Getter
public class MethodEntity {

  /**
   * 方法名
   */
  private String methodName;
  /**
   * 方法参数类型
   */
  private String[] parameterTypes;

}
