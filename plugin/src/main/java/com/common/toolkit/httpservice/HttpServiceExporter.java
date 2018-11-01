package com.common.toolkit.httpservice;

import com.common.toolkit.httpservice.config.HttpServiceProperties;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

/**
 * http服务暴露
 *
 * @author ewen
 */
@Getter
@Setter
public class HttpServiceExporter extends AbstractUrlHandlerMapping implements HttpRequestHandler,
    Ordered {

  private ServiceHandler serviceHandler;

  private HandlerParameterGainer[] handlerParameterGainers;

  private HandlerResultWriter[] handlerResultWriters;

  private HttpServiceProperties httpServiceProperties;

  public HttpServiceExporter() {
    this(new DefHttpService());
  }

  public HttpServiceExporter(ServiceHandler serviceHandler) {
    this.serviceHandler = serviceHandler;
    handlerParameterGainers = new HandlerParameterGainer[]{new RequestInputParameterGainer()};
    handlerResultWriters = new HandlerResultWriter[]{new DefOutputResultWriter()};
  }

  @Override
  public void handleRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    RequestFacade requestFacade = new RequestFacade(request);
    requestFacade
        .setAttribute(HandleModel.class.getSimpleName(), httpServiceProperties.getHandleModel());

    String serviceName = requestFacade.getServiceName();
    String methodName = requestFacade.getMethodName();

    String path = serviceHandler
        .getServicePath(serviceName, methodName);

    Object param = getParameter(path, request, requestFacade);
    Object result = serviceHandler.handleService(serviceName, methodName, param, requestFacade);

    writeResult(path, result, request, response);
  }

  @Override
  protected void initApplicationContext() throws BeansException {
    super.initApplicationContext();
    Assert.notEmpty(httpServiceProperties.getServicePath(), "服务路径不能为空!");
    Arrays.stream(httpServiceProperties.getServicePath())
        .forEach(servicePath -> registerHandler(servicePath, this));
  }

  protected Object getParameter(String path, HttpServletRequest request,
      RequestFacade requestFacade) throws IOException {
    Object param = null;
    for (HandlerParameterGainer gainer : handlerParameterGainers) {
      if (isSupport(gainer, path)) {
        param = gainer.gainParameter(request, requestFacade);
        if (param != null) {
          break;
        }
      }
    }
    return param;
  }

  private boolean isSupport(HandlerSupport support, String path) {
    return support.supports(path);
  }

  protected void writeResult(String path, Object result, HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    for (HandlerResultWriter writer : handlerResultWriters) {
      if (isSupport(writer, path)) {
        writer.write(result, request, response);
        break;
      }
    }
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
