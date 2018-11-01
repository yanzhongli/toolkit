package com.common.toolkit.httpservice;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

/**
 * 服务参数数据获取器<br/>
 * 不同的服务接口参数的获取方式有可能不一样，这与服务调用客户端有关，<br/>
 * 如有些参数是通过request.getParameter的方式取得,<br/>
 * 有些参数有可能是通过输入流的方式取得,<br/>
 */
public interface HandlerParameterGainer extends HandlerSupport {

	/**
	 * 获取参数数据
	 */
	Object gainParameter(HttpServletRequest request, RequestFacade requestFacade)throws IOException;
}
