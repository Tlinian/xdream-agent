package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/** 批量处理请求DTO */
@Data
@Schema(description = "批量处理请求")
public class BatchProcessRequest {

  @Schema(description = "任务名称")
  private String taskName;

  @Schema(description = "处理类型")
  private String processType;

  @Schema(description = "文件URL列表")
  private List<String> fileUrls;

  @Schema(description = "处理参数")
  private String parameters;

  @Schema(description = "回调URL")
  private String callbackUrl;
}
