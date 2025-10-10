package com.xdream.auth.service;

import com.xdream.auth.entity.User;

public interface JwtService {

  String generateAccessToken(User user);

  String generateRefreshToken(User user);

  boolean validateAccessToken(String token);

  boolean validateRefreshToken(String token);

  String getUserIdFromAccessToken(String token);

  String getUserIdFromRefreshToken(String token);

  String extractToken(String authorizationHeader);

  void blacklistToken(String token);

  Long getAccessTokenExpiration();

  Long getRefreshTokenExpiration();
}
