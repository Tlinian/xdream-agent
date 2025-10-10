package com.xdream.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationConfig {

  private Jwt jwt = new Jwt();
  private Cors cors = new Cors();

  @Data
  public static class Jwt {
    private String secret = "xdream-agent-secret-key-2024-xdream-agent-secret-key";
    private Long accessTokenExpiration = 3600L; // 1小时
    private Long refreshTokenExpiration = 86400L; // 24小时
  }

  @Data
  public static class Cors {
    private String[] allowedOrigins = {"*"};
    private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};
    private String[] allowedHeaders = {"*"};
    private boolean allowCredentials = true;
  }
}
