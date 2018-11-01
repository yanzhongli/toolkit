package com.common.toolkit.httpservice;


import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonResultResolver extends AbstractHandlerSupport implements HandlerResultResolver {

  private ObjectMapper objectMapper = new ObjectMapper();

  public JsonResultResolver() {
    super();
  }


  public ResultDecorator resolveResult(ResultDecorator resultVisitor,
      Object paramObj, RequestFacade requestFacade) {

    try {
			/*
			String characterEncoding = request.getCharacterEncoding();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			OutputStreamWriter writer = new OutputStreamWriter(baos,characterEncoding);
			JsonGenerator jsonGenerator =
				this.objectMapper.getJsonFactory().createJsonGenerator(writer);
			this.objectMapper.writeValue(jsonGenerator, resultVisitor.getResult());
			resultVisitor.setResult(baos.toString());
			*/
      Object result = resultVisitor.getResult();
      String data;
      if (result instanceof String) {
        data = (String) result;
      } else {
        data = objectMapper.writeValueAsString(result);
      }
      resultVisitor.setResult(data);
    } catch (Exception ex) {
      resultVisitor.setSuccess(false);
      resultVisitor.setResult(ex);
    }
    return resultVisitor;
  }


}
