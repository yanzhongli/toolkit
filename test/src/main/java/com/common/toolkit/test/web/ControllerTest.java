package com.common.toolkit.test.web;

import javax.servlet.Filter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * {@link WebMvcTest} auto-configures the Spring MVC infrastructure and limits scanned beans to
 * {@link Controller}, {@link ControllerAdvice}, {@link JsonComponent}, {@link Converter}, {@link
 * GenericConverter}, {@link Filter}, {@link WebMvcConfigurer}, and {@link
 * HandlerMethodArgumentResolver}. Regular {@link Component} beans are not scanned when using this
 * annotation.@WebMvcTest is limited to a single controller and is used in combination with
 *
 * @author ewen
 * @MockBean to provide mock implementations for required collaborators. {@link WebMvcTest} also
 * auto-configures MockMvc. Mock MVC offers a powerful way to quickly test MVC controllers without
 * needing to start a full HTTP server.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(EchoController.class)
public class ControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void testExample() throws Exception {

    this.mvc.perform(MockMvcRequestBuilders.get("/echo").accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("{'name':'daixw'}"));
  }

}

@RestController
class EchoController {

  @GetMapping("/echo")
  public String echo() {
    return "{'name':'daixw'}";
  }

}
