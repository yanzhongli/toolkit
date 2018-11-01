package com.common.toolkit.httpservice;

import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.FileCopyUtils;

/**
 * 从request的流里获取数据
 */
public class RequestInputParameterGainer extends AbstractHandlerSupport implements
    HandlerParameterGainer {

  public RequestInputParameterGainer() {
    super();
  }

  public Object gainParameter(HttpServletRequest request, RequestFacade requestFacade)
      throws IOException {
    InputStreamReader reader = new InputStreamReader(request.getInputStream(), "UTF-8");
    return FileCopyUtils.copyToString(reader);
  }

}
