package com.xdream.user.service.impl;

import com.xdream.common.dto.PageResponse;
import com.xdream.common.exception.BusinessException;
import com.xdream.common.exception.ErrorCode;
import com.xdream.user.dto.UserCreateRequest;
import com.xdream.user.dto.UserResponse;
import com.xdream.user.dto.UserUpdateRequest;
import com.xdream.user.entity.User;
import com.xdream.user.mapper.UserMapper;
import com.xdream.user.repository.UserRepository;
import com.xdream.user.service.UserService;
import com.xdream.common.utils.UuidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }
    
    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }
    
    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }
    
    @Override
    public PageResponse<UserResponse> getUsers(Pageable pageable, String keyword) {
        Page<User> userPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            userPage = userRepository.findByKeyword(keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        
        return PageResponse.of(userPage.map(userMapper::toResponse));
    }
    
    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        log.info("创建用户: {}", request.getUsername());
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }
        
        // 创建用户实体
        User user = User.builder()
                .id(UuidUtils.generateUuid())
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .avatar(request.getAvatar())
                .role(request.getRole() != null ? User.Role.valueOf(request.getRole()) : User.Role.USER)
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("用户创建成功: {}", savedUser.getUsername());
        
        return userMapper.toResponse(savedUser);
    }
    
    @Override
    @Transactional
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        log.info("更新用户: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        // 如果更新用户名，检查是否已存在
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BusinessException(ErrorCode.USERNAME_EXISTS);
            }
            user.setUsername(request.getUsername());
        }
        
        // 如果更新邮箱，检查是否已存在
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(ErrorCode.EMAIL_EXISTS);
            }
            user.setEmail(request.getEmail());
        }
        
        // 更新其他字段
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getRole() != null) {
            user.setRole(User.Role.valueOf(request.getRole()));
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        
        // 如果更新密码
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        log.info("用户更新成功: {}", updatedUser.getUsername());
        
        return userMapper.toResponse(updatedUser);
    }
    
    @Override
    @Transactional
    public UserResponse updateUserStatus(String userId, boolean enabled) {
        log.info("更新用户状态: {} -> {}", userId, enabled);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        user.setEnabled(enabled);
        User updatedUser = userRepository.save(user);
        
        log.info("用户状态更新成功: {} -> {}", updatedUser.getUsername(), enabled);
        return userMapper.toResponse(updatedUser);
    }
    
    @Override
    @Transactional
    public void deleteUser(String userId) {
        log.info("删除用户: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        userRepository.delete(user);
        log.info("用户删除成功: {}", user.getUsername());
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}