package com.xdream.gateway.service;

public interface JwtService {

  boolean validateAccessToken(String token);

  String getUserIdFromAccessToken(String token);

  String getRoleFromAccessToken(String token);

  String extractToken(String authorizationHeader);
}
