package com.common.toolkit.test.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * spring boot mvc mock
 *
 * @author ewen
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class WebMvcMock {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void echo() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/echo")
        .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("daixw"));
  }

}
