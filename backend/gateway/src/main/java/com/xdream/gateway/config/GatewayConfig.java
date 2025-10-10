package com.xdream.gateway.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "gateway")
public class GatewayConfig {

  private Jwt jwt = new Jwt();
  private RateLimit rateLimit = new RateLimit();
  private Cors cors = new Cors();

  @Data
  public static class Jwt {
    private String secret = "xdream-agent-secret-key-2024-xdream-agent-secret-key";
    private Long accessTokenExpiration = 3600L;
    private Long refreshTokenExpiration = 86400L;
  }

  @Data
  public static class RateLimit {
    private boolean enabled = true;
    private int requestsPerMinute = 100;
    private int burstCapacity = 150;
  }

  @Data
  public static class Cors {
    private List<String> allowedOrigins = new ArrayList<>();
    private List<String> allowedMethods = new ArrayList<>();
    private List<String> allowedHeaders = new ArrayList<>();
    private boolean allowCredentials = true;

    public Cors() {
      allowedOrigins.add("*");
      allowedMethods.add("GET");
      allowedMethods.add("POST");
      allowedMethods.add("PUT");
      allowedMethods.add("DELETE");
      allowedMethods.add("OPTIONS");
      allowedHeaders.add("*");
    }
  }
}
