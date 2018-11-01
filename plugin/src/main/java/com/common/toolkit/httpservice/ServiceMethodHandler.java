package com.common.toolkit.httpservice;

import java.lang.reflect.Method;
import org.springframework.core.MethodParameter;

/**
 * 服务方法执行器
 */
public interface ServiceMethodHandler {

  /**
   * 执行方法
   */
  ResultDecorator invoke(Object bean, Object obj) throws Exception;

  /**
   * 获取当前执行的方法
   */
  Method getMethod();

  /**
   * 被代理的原始方法
   */
  Method getTargetMethod();

  /**
   * 方法入参信息
   */
  MethodParameter[] getMethodParameters();


  void setTargetMethod(Method method);
}
