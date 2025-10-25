package com.xdream.llm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.xdream.llm")
@EnableAsync
@ComponentScan(basePackages = {"com.xdream.llm", "com.xdream.common"})
public class LlmServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(LlmServiceApplication.class, args);
  }
}

