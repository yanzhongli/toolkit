package com.common.toolkit.httpservice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 结果对象
 */
@Getter
@Setter
@ToString
public class ResultDecorator {

  /**
   * 是否成功
   */
  private boolean success = true;

  /**
   * 返回对象
   */
  private Object result;
  private Object parameter;

  public ResultDecorator() {

  }

  public ResultDecorator(boolean success, Object result) {
    super();
    this.success = success;
    this.result = result;
  }

}
