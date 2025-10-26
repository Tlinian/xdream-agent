package com.xdream.gateway.filter;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

  public static final List<String> openApiEndpoints =
      List.of(
          "/api/auth/register",
          "/api/auth/login",
          "/api/auth/refresh",
          "/api/llm/chat/stream",
          "/api/knowledge",
          "/actuator/health",
          "/eureka");

  public Predicate<ServerHttpRequest> isSecured =
      request ->
          openApiEndpoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));
}
