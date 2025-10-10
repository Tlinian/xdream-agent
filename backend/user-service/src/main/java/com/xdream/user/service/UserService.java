package com.xdream.user.service;

import com.xdream.common.dto.PageResponse;
import com.xdream.user.dto.UserCreateRequest;
import com.xdream.user.dto.UserResponse;
import com.xdream.user.dto.UserUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface UserService {
    
    UserResponse getUserById(String userId);
    
    UserResponse getUserByUsername(String username);
    
    UserResponse getUserByEmail(String email);
    
    PageResponse<UserResponse> getUsers(Pageable pageable, String keyword);
    
    UserResponse createUser(UserCreateRequest request);
    
    UserResponse updateUser(String userId, UserUpdateRequest request);
    
    UserResponse updateUserStatus(String userId, boolean enabled);
    
    void deleteUser(String userId);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}