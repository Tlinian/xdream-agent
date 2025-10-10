package com.xdream.multimodal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/** 多模态服务启动类 提供图像、视频、音频等多模态内容的处理和分析服务 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MultimodalServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MultimodalServiceApplication.class, args);
  }
}
