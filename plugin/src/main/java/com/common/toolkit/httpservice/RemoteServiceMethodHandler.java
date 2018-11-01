package com.common.toolkit.httpservice;

import java.lang.reflect.Method;
import org.springframework.core.MethodParameter;

/**
 * 远程方法调用
 *
 * @author ewen
 */
public class RemoteServiceMethodHandler implements ServiceMethodHandler {

  private String serviceName;

  private String methodName;

  public RemoteServiceMethodHandler(String serviceName, String methodName) {
    this.serviceName = serviceName;
    this.methodName = methodName;
  }

  @Override
  public ResultDecorator invoke(Object bean, Object obj) throws Exception {
    String serviceCode = ServiceContainer.getServiceCode(serviceName, methodName, (s, m) -> s + m);
    Object result = ServiceManager.INSTANCE.invoke(serviceCode, obj);
    ResultDecorator resultDecorator = new ResultDecorator(true, result);
    return resultDecorator;
  }

  @Override
  public Method getMethod() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Method getTargetMethod() {
    throw new UnsupportedOperationException();
  }

  @Override
  public MethodParameter[] getMethodParameters() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setTargetMethod(Method method) {
    throw new UnsupportedOperationException();
  }
}
