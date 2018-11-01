package com.common.toolkit.httpservice;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 结果对象输出器
 */
public interface HandlerResultWriter extends HandlerSupport {


  /**
   * 输出对象
   */
  void write(Object result, HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException;
}
