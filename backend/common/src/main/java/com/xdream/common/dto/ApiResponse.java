package com.xdream.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private boolean success;
  private String code;
  private String message;
  private T data;
  private LocalDateTime timestamp;
  private String path;

  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .code("SUCCESS")
        .message("操作成功")
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .code("SUCCESS")
        .message(message)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static <T> ApiResponse<T> error(String code, String message) {
    return ApiResponse.<T>builder()
        .success(false)
        .code(code)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static <T> ApiResponse<T> error(String code, String message, String path) {
    return ApiResponse.<T>builder()
        .success(false)
        .code(code)
        .message(message)
        .path(path)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
