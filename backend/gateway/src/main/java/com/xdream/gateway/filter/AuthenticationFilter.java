package com.xdream.gateway.filter;

import com.xdream.gateway.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationFilter
    extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private final JwtService jwtService;
  private final RouteValidator validator;

  public AuthenticationFilter(JwtService jwtService, RouteValidator validator) {
    super(Config.class);
    this.jwtService = jwtService;
    this.validator = validator;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return ((exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();

      // 检查是否是公开路由
      if (validator.isSecured.test(request)) {
        // 检查是否有Authorization头
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
          return handleUnauthorized(exchange);
        }

        String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
          return handleUnauthorized(exchange);
        }

        String token = authHeader.substring(7);

        try {
          // 验证Token
          if (!jwtService.validateAccessToken(token)) {
            return handleUnauthorized(exchange);
          }

          String userId = jwtService.getUserIdFromAccessToken(token);
          String role = jwtService.getRoleFromAccessToken(token);

          // 添加用户信息到请求头
          ServerHttpRequest modifiedRequest =
              request.mutate().header("X-User-Id", userId).header("X-User-Role", role).build();

          return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
          log.error("Token验证失败: {}", e.getMessage());
          return handleUnauthorized(exchange);
        }
      }

      return chain.filter(exchange);
    });
  }

  private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    return response.setComplete();
  }

  public static class Config {
    // 配置类，可以添加一些配置参数
  }
}
