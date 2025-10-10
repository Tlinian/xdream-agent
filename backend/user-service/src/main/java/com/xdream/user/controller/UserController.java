package com.xdream.user.controller;

import com.xdream.common.dto.ApiResponse;
import com.xdream.common.dto.PageResponse;
import com.xdream.user.dto.UserCreateRequest;
import com.xdream.user.dto.UserResponse;
import com.xdream.user.dto.UserUpdateRequest;
import com.xdream.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestHeader("X-User-Id") String userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/me")
    @Operation(summary = "更新当前用户信息", description = "更新当前登录用户的基本信息")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse user = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息（管理员权限）")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "用户ID") @PathVariable String userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取用户列表", description = "获取用户分页列表（管理员权限）")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
            @PageableDefault(size = 20) Pageable pageable,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        PageResponse<UserResponse> users = userService.getUsers(pageable, keyword);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建用户", description = "创建新用户（管理员权限）")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新用户信息", description = "更新用户信息（管理员权限）")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(description = "用户ID") @PathVariable String userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse user = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户", description = "删除用户（管理员权限）")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "用户ID") @PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新用户状态", description = "启用/禁用用户（管理员权限）")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable String userId,
            @Parameter(description = "状态") @RequestParam boolean enabled) {
        UserResponse user = userService.updateUserStatus(userId, enabled);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}