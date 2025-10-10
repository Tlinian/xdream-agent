package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** 任务状态响应DTO */
@Data
@Schema(description = "任务状态响应")
public class TaskStatusResponse {

  @Schema(description = "任务ID")
  private String taskId;

  @Schema(description = "任务状态")
  private String status;

  @Schema(description = "任务类型")
  private String taskType;

  @Schema(description = "创建时间")
  private String createdTime;

  @Schema(description = "开始时间")
  private String startTime;

  @Schema(description = "完成时间")
  private String completionTime;

  @Schema(description = "进度百分比")
  private Integer progress;

  @Schema(description = "结果数据")
  private String resultData;

  @Schema(description = "错误信息")
  private String errorMessage;

  @Schema(description = "处理耗时（毫秒）")
  private Long processingTime;
}
