package com.xdream.auth.service;

import com.xdream.auth.dto.LoginRequest;
import com.xdream.auth.dto.LoginResponse;
import com.xdream.auth.dto.RefreshTokenRequest;
import com.xdream.auth.dto.RegisterRequest;

public interface AuthService {

  void register(RegisterRequest request);

  LoginResponse login(LoginRequest request);

  LoginResponse refreshToken(RefreshTokenRequest request);

  void logout(String token);
}
