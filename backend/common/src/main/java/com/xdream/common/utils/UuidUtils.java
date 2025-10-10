package com.xdream.common.utils;

import java.util.UUID;

public class UuidUtils {

  private UuidUtils() {}

  public static String generateUuid() {
    return UUID.randomUUID().toString();
  }

  public static String generateUuidWithoutDashes() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public static boolean isValidUuid(String uuid) {
    if (uuid == null || uuid.trim().isEmpty()) {
      return false;
    }
    try {
      UUID.fromString(uuid);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
