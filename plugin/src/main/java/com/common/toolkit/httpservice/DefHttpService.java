package com.common.toolkit.httpservice;

import com.common.toolkit.spring.SpringContextHolder;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;

@Setter
@Getter
public class DefHttpService implements ServiceHandler {

  /**
   * 参数解析器
   */
  private HandlerParameterResolver[] handlerParameterResolvers;

  /**
   * 服务方法缓存
   */
  private Map<String, ServiceMethodHandler> serviceMethodHandlers = new ConcurrentHashMap<>();

  /**
   * 结果对象解析器
   */
  private HandlerResultResolver[] handlerResultResolvers;

  /**
   * 异常解析器
   */
  private HandlerExceptionResolver[] handlerExceptionResolvers;

  private HandlerAuthenticationResolver[] handlerAuthenticationResolvers;

  private HandlerLogResolver[] handlerLogResolvers;

  private Set<String> supportMethods = new HashSet<String>() {{
    add("GET");
    add("POST");
    add("PUT");
    add("DELETE");
  }};

  private ApplicationContext applicationContext;

  public DefHttpService() {
    handlerParameterResolvers = new HandlerParameterResolver[0];
    handlerResultResolvers = new HandlerResultResolver[]{new JsonResultResolver()};
    handlerExceptionResolvers = new HandlerExceptionResolver[0];
    handlerAuthenticationResolvers = new HandlerAuthenticationResolver[0];
    handlerLogResolvers = new HandlerLogResolver[0];
  }

  public Object handleService(String serviceName, String serviceMethod,
      Object param, RequestFacade requestFacade) {

    RequestMethod method = requestFacade.getMethod();
    String path = getServicePath(serviceName, serviceMethod);
    //
    ResultDecorator resultDecorator = new ResultDecorator();
    try {
      if (!supportMethod(method.name())) {
        throw new IllegalStateException("不支持" + method + "请求，只支持" + supportMethods);
      }
      //判断身份认证
      if (!resolveAuthentication(path, serviceName, serviceMethod, param, requestFacade)) {
        throw new IllegalStateException("身份认证不通过:" + path);
      }
      //参数转换
      param = resolveParameter(path, param, requestFacade);

      ServiceMethodHandler serviceMethodHandler;
      Object bean = requestFacade.isLocal() ? getBean(serviceName) : null;

      //获取服务处理器
      serviceMethodHandler = serviceMethodHandlers
          .putIfAbsent(path, getServiceMethodHandler(bean, requestFacade));
      if (serviceMethodHandler == null) {
        serviceMethodHandler = serviceMethodHandlers.get(path);
      }
      if (serviceMethodHandler == null) {
        throw new IllegalStateException("服务方法" + path + "不存在");
      }
      //服务调用
      resultDecorator = serviceMethodHandler.invoke(bean, param);
    } catch (Exception ex) {
      //异常结果处理
      Object obj = resolveException(path, ex);
      resultDecorator = new ResultDecorator(false, obj);
    } finally {
      //结果处理
      resultDecorator.setParameter(param);
      resultDecorator = resolveResult(path, resultDecorator, param, requestFacade);
    }
    //写日志
    resolveLog(path, resultDecorator, requestFacade);

    return resultDecorator.getResult();
  }

  /**
   * 异常处理
   */
  protected Object resolveException(String path, Exception exception) {
    Object object = null;
    for (HandlerExceptionResolver resolver : handlerExceptionResolvers) {
      if (isSupport(resolver, path)) {
        object = resolver.resolveException(exception);
        if (object != null) {
          break;
        }
      }
    }
    if (object == null) {
      object = exception;
    }
    return object;
  }

  private boolean isSupport(HandlerSupport support, String path) {
    return support.supports(path);
  }


  /**
   * 参数转换
   */
  protected Object resolveParameter(String path, Object param, RequestFacade requestFacade) {
    Object object = param;
    for (HandlerParameterResolver resolver : handlerParameterResolvers) {
      if (isSupport(resolver, path)) {
        object = resolver.resolveParameter(param, requestFacade);
        if (object != null) {
          break;
        }
      }
    }
    return object;
  }

  /**
   * 结果转换
   */
  protected ResultDecorator resolveResult(String path, ResultDecorator resultVisitor, Object object,
      RequestFacade requestFacade) {
    ResultDecorator result = resultVisitor;
    for (HandlerResultResolver resolver : handlerResultResolvers) {
      if (isSupport(resolver, path)) {
        result = resolver.resolveResult(result, object, requestFacade);
        if (result != null) {
          break;
        }
      }
    }
    return result;
  }

  /**
   * 身份认证
   */
  protected boolean resolveAuthentication(String path, String serviceName, String serviceMethod,
      Object param, RequestFacade requestFacade) {
    boolean b = true;
    for (HandlerAuthenticationResolver resolver : handlerAuthenticationResolvers) {
      if (isSupport(resolver, path)) {
        b = resolver.resolveAuthentication(serviceName, serviceMethod, param, requestFacade);
        if (!b) {
          return b;
        }
      }
    }
    return b;
  }

  /**
   * 记录日志
   */
  protected void resolveLog(String path, ResultDecorator result, RequestFacade requestFacade) {
    for (HandlerLogResolver resolver : handlerLogResolvers) {
      if (isSupport(resolver, path)) {
        resolver.resolveLog(result, requestFacade);
        break;
      }
    }
  }

  private ServiceMethodHandler getServiceMethodHandler(Object bean,
      RequestFacade requestFacade) {
    String methodName = requestFacade.getMethodName();
    ServiceMethodHandler serviceMethodHandler = null;
    if (requestFacade.isLocal()) {
      Method[] methods = bean.getClass().getMethods();
      for (Method method : methods) {
        String name = method.getName();
        if (methodName.equals(name)) {
          serviceMethodHandler = new LocalServiceMethodHandler(method);
          break;
        }
      }
      if (AopUtils.isAopProxy(bean) && serviceMethodHandler != null) {
        try {
          Method[] methods2 = AopUtils.getTargetClass(bean).getMethods();
          for (Method method : methods2) {
            String name = method.getName();
            if (methodName.equals(name)) {
              serviceMethodHandler.setTargetMethod(method);
              break;
            }
          }
        } catch (Exception e) {
          //throw new RuntimeException(e);
        }
      }
    } else {
      serviceMethodHandler = new RemoteServiceMethodHandler(requestFacade.getServiceName(),
          methodName);
    }
    return serviceMethodHandler;
  }

  public void setApplicationContext(ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }

  protected Object getBean(String beanId) {
    if (applicationContext != null) {
      return applicationContext.getBean(beanId);
    } else {
      return SpringContextHolder.getBean(beanId);
    }
  }

  private boolean supportMethod(String method) {
    return supportMethods.contains(method);
  }

  public void setMethod(String method) {
    if (method != null) {
      String[] ms = method.split(",");
      if (ms.length > 0) {
        supportMethods.clear();
      }
      for (String m : ms) {
        m = m.trim();
        if (m.length() > 0) {
          supportMethods.add(m.toUpperCase());
        }
      }
    }
  }

  public String getServicePath(String serviceName, String serviceMethod) {
    if (serviceMethod == null) {
      serviceMethod = "";
    }
    return "/" + serviceName + "/" + serviceMethod;
  }


}
