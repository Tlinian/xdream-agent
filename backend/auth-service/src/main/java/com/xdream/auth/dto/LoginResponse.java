package com.xdream.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录响应")
public class LoginResponse {

  @Schema(description = "访问Token")
  private String accessToken;

  @Schema(description = "刷新Token")
  private String refreshToken;

  @Schema(description = "Token过期时间（秒）")
  private Long expiresIn;

  @Schema(description = "Token类型")
  private String tokenType;

  @Schema(description = "用户信息")
  private UserInfo userInfo;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "用户信息")
  public static class UserInfo {

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户角色")
    private String role;
  }
}
