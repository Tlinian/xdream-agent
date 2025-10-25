package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "宓屽叆璇锋眰")
public class EmbeddingRequest {

  @NotBlank(message = "鏂囨湰鍐呭涓嶈兘涓虹┖")
  @Size(max = 8192, message = "鏂囨湰鍐呭涓嶈兘瓒呰繃8192涓瓧绗?)
  @Schema(description = "鏂囨湰鍐呭", example = "杩欐槸涓€涓渶瑕佺敓鎴愬祵鍏ュ悜閲忕殑鏂囨湰")
  private String text;

  @Schema(description = "妯″瀷绫诲瀷", example = "text-embedding-ada-002")
  private String modelType = "text-embedding-ada-002";

  @Schema(description = "鐢ㄦ埛鏍囪瘑", example = "user123")
  private String user;
}
