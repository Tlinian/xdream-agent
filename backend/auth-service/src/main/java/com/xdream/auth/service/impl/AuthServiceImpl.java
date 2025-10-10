package com.xdream.auth.service.impl;

import com.xdream.auth.dto.LoginRequest;
import com.xdream.auth.dto.LoginResponse;
import com.xdream.auth.dto.RefreshTokenRequest;
import com.xdream.auth.dto.RegisterRequest;
import com.xdream.auth.entity.User;
import com.xdream.auth.repository.UserRepository;
import com.xdream.auth.service.AuthService;
import com.xdream.auth.service.JwtService;
import com.xdream.common.exception.BusinessException;
import com.xdream.common.exception.ErrorCode;
import com.xdream.common.utils.UuidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Override
  @Transactional
  public void register(RegisterRequest request) {
    log.info("用户注册请求: {}", request.getUsername());

    // 检查用户名是否已存在
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new BusinessException(ErrorCode.USERNAME_EXISTS);
    }

    // 检查邮箱是否已存在
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException(ErrorCode.EMAIL_EXISTS);
    }

    // 创建新用户
    User user =
        User.builder()
            .id(UuidUtils.generateUuid())
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role(User.Role.USER)
            .enabled(true)
            .accountNonExpired(true)
            .credentialsNonExpired(true)
            .accountNonLocked(true)
            .build();

    userRepository.save(user);
    log.info("用户注册成功: {}", user.getUsername());
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    log.info("用户登录请求: {}", request.getUsername());

    // 查找用户
    User user =
        userRepository
            .findByUsernameOrEmail(request.getUsername())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 验证密码
    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
    }

    // 检查用户状态
    if (!user.getEnabled()) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "账户已被禁用");
    }

    // 生成Token
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    LoginResponse.UserInfo userInfo =
        LoginResponse.UserInfo.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();

    LoginResponse response =
        LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtService.getAccessTokenExpiration())
            .tokenType("Bearer")
            .userInfo(userInfo)
            .build();

    log.info("用户登录成功: {}", user.getUsername());
    return response;
  }

  @Override
  public LoginResponse refreshToken(RefreshTokenRequest request) {
    log.info("刷新Token请求");

    String refreshToken = request.getRefreshToken();

    // 验证刷新Token
    if (!jwtService.validateRefreshToken(refreshToken)) {
      throw new BusinessException(ErrorCode.TOKEN_INVALID);
    }

    // 获取用户ID
    String userId = jwtService.getUserIdFromRefreshToken(refreshToken);

    // 查找用户
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 检查用户状态
    if (!user.getEnabled()) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "账户已被禁用");
    }

    // 生成新的访问Token
    String newAccessToken = jwtService.generateAccessToken(user);
    String newRefreshToken = jwtService.generateRefreshToken(user);

    LoginResponse.UserInfo userInfo =
        LoginResponse.UserInfo.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();

    LoginResponse response =
        LoginResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .expiresIn(jwtService.getAccessTokenExpiration())
            .tokenType("Bearer")
            .userInfo(userInfo)
            .build();

    log.info("Token刷新成功: {}", user.getUsername());
    return response;
  }

  @Override
  public void logout(String token) {
    log.info("用户登出请求");

    // 提取Token
    String accessToken = jwtService.extractToken(token);

    // 验证Token
    if (jwtService.validateAccessToken(accessToken)) {
      // 将Token加入黑名单（可选）
      jwtService.blacklistToken(accessToken);
    }

    log.info("用户登出成功");
  }
}
