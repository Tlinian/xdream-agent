package com.xdream.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.xdream.auth.dto.LoginRequest;
import com.xdream.auth.dto.LoginResponse;
import com.xdream.auth.dto.RegisterRequest;
import com.xdream.auth.entity.User;
import com.xdream.auth.repository.UserRepository;
import com.xdream.common.exception.BusinessException;
import com.xdream.common.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtService jwtService;

  @InjectMocks private AuthServiceImpl authService;

  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;
  private User testUser;

  @BeforeEach
  void setUp() {
    registerRequest = new RegisterRequest();
    registerRequest.setUsername("testuser");
    registerRequest.setEmail("test@example.com");
    registerRequest.setPassword("password123");

    loginRequest = new LoginRequest();
    loginRequest.setUsername("testuser");
    loginRequest.setPassword("password123");

    testUser =
        User.builder()
            .id("test-user-id")
            .username("testuser")
            .email("test@example.com")
            .passwordHash("encoded-password")
            .role(User.Role.USER)
            .enabled(true)
            .build();
  }

  @Test
  void register_Success() {
    // Given
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    assertDoesNotThrow(() -> authService.register(registerRequest));

    // Then
    verify(userRepository, times(1)).existsByUsername("testuser");
    verify(userRepository, times(1)).existsByEmail("test@example.com");
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void register_UsernameExists_ThrowsException() {
    // Given
    when(userRepository.existsByUsername(anyString())).thenReturn(true);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.register(registerRequest));

    assertEquals(ErrorCode.USERNAME_EXISTS.getCode(), exception.getCode());
    verify(userRepository, times(1)).existsByUsername("testuser");
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void register_EmailExists_ThrowsException() {
    // Given
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.register(registerRequest));

    assertEquals(ErrorCode.EMAIL_EXISTS.getCode(), exception.getCode());
    verify(userRepository, times(1)).existsByUsername("testuser");
    verify(userRepository, times(1)).existsByEmail("test@example.com");
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void login_Success() {
    // Given
    when(userRepository.findByUsernameOrEmail(anyString())).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token");
    when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");
    when(jwtService.getAccessTokenExpiration()).thenReturn(3600L);

    // When
    LoginResponse response = authService.login(loginRequest);

    // Then
    assertNotNull(response);
    assertEquals("access-token", response.getAccessToken());
    assertEquals("refresh-token", response.getRefreshToken());
    assertEquals("Bearer", response.getTokenType());
    assertEquals(3600L, response.getExpiresIn());
    assertNotNull(response.getUserInfo());
    assertEquals("testuser", response.getUserInfo().getUsername());
  }

  @Test
  void login_UserNotFound_ThrowsException() {
    // Given
    when(userRepository.findByUsernameOrEmail(anyString())).thenReturn(Optional.empty());

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginRequest));

    assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), exception.getCode());
    verify(jwtService, never()).generateAccessToken(any(User.class));
  }

  @Test
  void login_InvalidPassword_ThrowsException() {
    // Given
    when(userRepository.findByUsernameOrEmail(anyString())).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginRequest));

    assertEquals(ErrorCode.INVALID_CREDENTIALS.getCode(), exception.getCode());
    verify(jwtService, never()).generateAccessToken(any(User.class));
  }

  @Test
  void login_DisabledUser_ThrowsException() {
    // Given
    testUser.setEnabled(false);
    when(userRepository.findByUsernameOrEmail(anyString())).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

    // When & Then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginRequest));

    assertEquals(ErrorCode.UNAUTHORIZED.getCode(), exception.getCode());
    verify(jwtService, never()).generateAccessToken(any(User.class));
  }
}
