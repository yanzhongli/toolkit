package com.common.toolkit.httpservice;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.FileCopyUtils;

public class DefOutputResultWriter extends AbstractHandlerSupport implements HandlerResultWriter {

  public DefOutputResultWriter() {
    super();
  }

  public void write(Object result, HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    try {
      String characterEncoding = request.getCharacterEncoding();
      if (characterEncoding == null) {
        characterEncoding = "UTF-8";
      }
      response.setCharacterEncoding(characterEncoding);
      FileCopyUtils.copy(result.toString(), response.getWriter());
    } catch (IOException e) {
      // TODO Auto-generated catch block
    }
  }

}
