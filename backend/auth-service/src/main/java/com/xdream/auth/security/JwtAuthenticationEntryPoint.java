package com.xdream.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdream.common.dto.ApiResponse;
import com.xdream.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    log.error("未授权访问 - {}", authException.getMessage());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    ApiResponse<?> apiResponse = ApiResponse.error(ErrorCode.UNAUTHORIZED.getCode(), "未授权访问");

    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
  }
}
