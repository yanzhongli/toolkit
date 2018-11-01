package com.common.toolkit.actuator;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAdminServer
@SpringBootApplication
public class AdminBootstrap {

  public static void main(String[] args) {
    SpringApplication.run(AdminBootstrap.class, args);
  }

}
