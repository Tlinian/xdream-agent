package com.xdream.auth.controller;

import com.xdream.auth.dto.LoginRequest;
import com.xdream.auth.dto.LoginResponse;
import com.xdream.auth.dto.RefreshTokenRequest;
import com.xdream.auth.dto.RegisterRequest;
import com.xdream.auth.service.AuthService;
import com.xdream.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证授权相关接口")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  @Operation(summary = "用户注册", description = "用户注册新账号")
  public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/login")
  @Operation(summary = "用户登录", description = "用户登录并获取JWT Token")
  public ResponseEntity<ApiResponse<LoginResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/refresh")
  @Operation(summary = "刷新Token", description = "刷新用户JWT Token")
  public ResponseEntity<ApiResponse<LoginResponse>> refresh(
      @Valid @RequestBody RefreshTokenRequest request) {
    LoginResponse response = authService.refreshToken(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/logout")
  @Operation(summary = "用户登出", description = "用户登出")
  public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String token) {
    authService.logout(token);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
