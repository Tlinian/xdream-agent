package com.xdream.gateway.service.impl;

import com.xdream.gateway.config.GatewayConfig;
import com.xdream.gateway.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

  private final GatewayConfig gatewayConfig;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(gatewayConfig.getJwt().getSecret().getBytes());
  }

  @Override
  public boolean validateAccessToken(String token) {
    try {
      Claims claims = extractAllClaims(token);
      String tokenType = (String) claims.get("type");

      return "access".equals(tokenType) && !isTokenExpired(token);
    } catch (JwtException | IllegalArgumentException e) {
      log.error("访问Token验证失败: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public String getUserIdFromAccessToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get("userId", String.class);
  }

  @Override
  public String getRoleFromAccessToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get("role", String.class);
  }

  @Override
  public String extractToken(String authorizationHeader) {
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return authorizationHeader.substring(7);
    }
    return null;
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }
}
