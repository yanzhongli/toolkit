package com.common.toolkit.spring;


import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * 针对不在spring容器里的类，可以使用{@link org.springframework.beans.factory.annotation.Autowired}注入bean.
 * 此类并不会被放入spring容器中，只需要继承此类即可。
 */
public abstract class AutowireBean {

  public AutowireBean() {
    ApplicationContext ctx = SpringContextHolder.getApplicationContext();
    if (ctx != null) {
      AutowireCapableBeanFactory autowireCapableBeanFactory = ctx.getAutowireCapableBeanFactory();
      autowireCapableBeanFactory.autowireBean(this);
    }
  }

}