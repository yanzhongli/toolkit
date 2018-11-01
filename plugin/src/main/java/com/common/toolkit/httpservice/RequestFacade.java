package com.common.toolkit.httpservice;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

public class RequestFacade {

  private final String SERVICE_ID_KEY = "serviceId";

  private final String SERVICE_METHOD_KEY = "method";

  private RequestMethod method;

  private HttpServletRequest request;

  private Map<String, Object> attributes = Maps.newHashMap();

  public RequestFacade() {
    method = RequestMethod.GET;
  }

  public RequestFacade(HttpServletRequest request) {
    this.request = request;
    this.method = RequestMethod.valueOf(request.getMethod());
    this.setAttribute("RemoteHost", request.getRemoteHost());
    this.setAttribute("RemoteAddr", request.getRemoteAddr());
    this.setAttribute("RemoteHostIP", getRemoteAddr(request));
    this.setAttribute("RemotePort", request.getRemotePort());
    Map<String, String> attributes = (Map) request
        .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    this.setAttribute(SERVICE_ID_KEY, attributes.get(SERVICE_ID_KEY));
    this.setAttribute(SERVICE_METHOD_KEY, attributes.get(SERVICE_METHOD_KEY));
  }

  public String getServiceName() {
    return (String) getAttribute(SERVICE_ID_KEY);
  }

  public String getMethodName() {
    return (String) getAttribute(SERVICE_METHOD_KEY);
  }

  public RequestMethod getMethod() {
    return method;
  }

  public HttpServletRequest getRequest() {
    return request;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public Object getAttribute(String name) {
    return attributes.get(name);
  }

  public void setAttribute(String name, Object obj) {
    this.attributes.put(name, obj);
  }

  /**
   * 是否为本地调用
   */
  public boolean isLocal() {
    return HandleModel.LOCAL.equals(getAttribute(HandleModel.class.getSimpleName()));
  }

  private String getRemoteAddr(HttpServletRequest request) {

    String ip = request.getHeader("x-forwarded-for");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("http_client_ip");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    // 如果是多级代理，那么取第一个ip为客户ip
    if (ip != null && ip.indexOf(",") != -1) {
      ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
    }
    return ip;
  }
}
