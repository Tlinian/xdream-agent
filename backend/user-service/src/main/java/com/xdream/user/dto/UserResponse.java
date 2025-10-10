package com.xdream.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户响应")
public class UserResponse {
    
    @Schema(description = "用户ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;
    
    @Schema(description = "用户名", example = "john_doe")
    private String username;
    
    @Schema(description = "邮箱地址", example = "john@example.com")
    private String email;
    
    @Schema(description = "昵称", example = "John Doe")
    private String nickname;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @Schema(description = "个人简介", example = "我是一个AI爱好者")
    private String bio;
    
    @Schema(description = "角色", example = "USER")
    private String role;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
    
    @Schema(description = "账户是否过期", example = "false")
    private Boolean accountNonExpired;
    
    @Schema(description = "凭证是否过期", example = "false")
    private Boolean credentialsNonExpired;
    
    @Schema(description = "账户是否锁定", example = "false")
    private Boolean accountNonLocked;
    
    @Schema(description = "创建时间", example = "2024-01-01T00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间", example = "2024-01-01T00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}