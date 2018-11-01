package com.common.toolkit.httpservice;

import java.lang.reflect.Method;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;

public class LocalServiceMethodHandler implements ServiceMethodHandler {

  private Method method;

  private Method targetMethod = null;

  private MethodParameter[] methodParameters = null;

  public LocalServiceMethodHandler(Method method) {
    this.method = method;
  }

  public ResultDecorator invoke(Object bean, Object obj) throws Exception {
    Object result;
    int num = method.getGenericParameterTypes().length;
    if (num > 0) {
      if (obj == null) {
        result = method.invoke(bean, new Object[num]);
      } else if (obj.getClass().isArray()) {
        result = method.invoke(bean, (Object[]) obj);
      } else {
        result = method.invoke(bean, obj);
      }
    } else {
      result = method.invoke(bean);
    }
    ResultDecorator resultDecorator = new ResultDecorator(true, result);
    return resultDecorator;
  }

  public Method getMethod() {
    return method;
  }

  public MethodParameter[] getMethodParameters() {
    if (methodParameters == null) {
      Method m = getTargetMethod();
      methodParameters = new MethodParameter[m.getParameterTypes().length];
      for (int i = 0; i < methodParameters.length; i++) {
        methodParameters[i] = new MethodParameter(m, i);
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        methodParameters[i].initParameterNameDiscovery(parameterNameDiscoverer);
      }
    }
    return methodParameters;
  }

  public Method getTargetMethod() {
    if (targetMethod == null) {
      return method;
    }
    return targetMethod;
  }

  public void setTargetMethod(Method targetMethod) {
    this.targetMethod = targetMethod;
  }
}
