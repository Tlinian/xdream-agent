package com.xdream.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.registerModule(new JavaTimeModule());
    OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    OBJECT_MAPPER.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
  }

  private JsonUtils() {}

  public static String toJson(Object object) {
    try {
      return OBJECT_MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("对象转JSON失败", e);
      throw new RuntimeException("对象转JSON失败", e);
    }
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    try {
      return OBJECT_MAPPER.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      log.error("JSON转对象失败: {}", json, e);
      throw new RuntimeException("JSON转对象失败", e);
    }
  }

  public static <T> T fromJson(String json, TypeReference<T> typeReference) {
    try {
      return OBJECT_MAPPER.readValue(json, typeReference);
    } catch (JsonProcessingException e) {
      log.error("JSON转对象失败: {}", json, e);
      throw new RuntimeException("JSON转对象失败", e);
    }
  }

  public static ObjectMapper getObjectMapper() {
    return OBJECT_MAPPER;
  }
}
