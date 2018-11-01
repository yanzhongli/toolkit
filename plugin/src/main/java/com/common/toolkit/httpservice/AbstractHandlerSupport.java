package com.common.toolkit.httpservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public abstract class AbstractHandlerSupport implements HandlerSupport {

  public final static String ALL_EXP = "/*/*";
  @Getter
  @Setter
  protected String expression;

  private PathMatcher pathMatcher = new AntPathMatcher();

  public AbstractHandlerSupport() {
    this(ALL_EXP);
  }

  public AbstractHandlerSupport(String expression) {
    this.expression = expression;
  }

  public boolean supports(String path) {
    if (path == null) {
      return false;
    }
    return pathMatcher.match(expression, path);
  }
}
