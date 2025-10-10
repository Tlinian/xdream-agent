package com.xdream.auth.service.impl;

import com.xdream.auth.entity.User;
import com.xdream.auth.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

  @Value("${jwt.secret:xdream-agent-secret-key-2024-xdream-agent-secret-key}")
  private String secret;

  @Value("${jwt.access-token-expiration:3600}")
  private Long accessTokenExpiration;

  @Value("${jwt.refresh-token-expiration:86400}")
  private Long refreshTokenExpiration;

  private final Map<String, Boolean> tokenBlacklist = new ConcurrentHashMap<>();

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  @Override
  public String generateAccessToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId());
    claims.put("username", user.getUsername());
    claims.put("role", user.getRole().name());
    claims.put("type", "access");

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getId())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration * 1000))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public String generateRefreshToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId());
    claims.put("type", "refresh");

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getId())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration * 1000))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public boolean validateAccessToken(String token) {
    try {
      if (tokenBlacklist.containsKey(token)) {
        return false;
      }

      Claims claims = extractAllClaims(token);
      String tokenType = (String) claims.get("type");

      return "access".equals(tokenType) && !isTokenExpired(token);
    } catch (JwtException | IllegalArgumentException e) {
      log.error("访问Token验证失败: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public boolean validateRefreshToken(String token) {
    try {
      Claims claims = extractAllClaims(token);
      String tokenType = (String) claims.get("type");

      return "refresh".equals(tokenType) && !isTokenExpired(token);
    } catch (JwtException | IllegalArgumentException e) {
      log.error("刷新Token验证失败: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public String getUserIdFromAccessToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get("userId", String.class);
  }

  @Override
  public String getUserIdFromRefreshToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get("userId", String.class);
  }

  @Override
  public String extractToken(String authorizationHeader) {
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return authorizationHeader.substring(7);
    }
    return null;
  }

  @Override
  public void blacklistToken(String token) {
    tokenBlacklist.put(token, true);
    log.info("Token已加入黑名单");
  }

  @Override
  public Long getAccessTokenExpiration() {
    return accessTokenExpiration;
  }

  @Override
  public Long getRefreshTokenExpiration() {
    return refreshTokenExpiration;
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
  }

  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }
}
