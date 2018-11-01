package com.common.toolkit.test.web;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * service test
 *
 * @author ewen
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class ServiceTest {

  /**
   * 添加一个新的bean,或替换已存在的bean,这个bean的方法返回空,对于不需要验证返回值的有用,可手动添加返回值，test执行完会重置
   */
  @MockBean
  private EchoService echoService;

  @Autowired
  private ApplicationContext applicationContext;

  /**
   * 间谍bean,在没有注册到容器的情况下,可以主动注册一个代理,有的话会wrapper
   */
  @SpyBean
  private HelloService helloService;

  @Test
  public void testEchoService() {
    String echo = echoService.echo();
    Assertions.assertThat(echo).isNull();

    //给echoService.echo()添加返回值
    BDDMockito.given(this.echoService.echo()).willReturn("mock");
    Assertions.assertThat(echoService.echo()).isEqualTo("mock");

    String say = helloService.say();
    Assertions.assertThat(say).isEqualTo("hello");

    HelloService helloService = applicationContext.getBean(HelloService.class);
    Assertions.assertThat(helloService).isNotNull();
  }
}

@Service
class EchoService {

  public String echo() {
    return "{'name':'daixw'}";
  }

}

class HelloService {

  public String say() {
    return "hello";
  }

}

