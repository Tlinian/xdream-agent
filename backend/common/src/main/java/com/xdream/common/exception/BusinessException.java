package com.xdream.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final String code;
  private final Object[] args;

  public BusinessException(String code, String message) {
    super(message);
    this.code = code;
    this.args = new Object[0];
  }

  public BusinessException(String code, String message, Object... args) {
    super(message);
    this.code = code;
    this.args = args;
  }

  public BusinessException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
    this.args = new Object[0];
  }

  public BusinessException(String code, String message, Throwable cause, Object... args) {
    super(message, cause);
    this.code = code;
    this.args = args;
  }

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
    this.args = new Object[0];
  }

  public BusinessException(ErrorCode errorCode, Object... args) {
    super(String.format(errorCode.getMessage(), args));
    this.code = errorCode.getCode();
    this.args = args;
  }
}
