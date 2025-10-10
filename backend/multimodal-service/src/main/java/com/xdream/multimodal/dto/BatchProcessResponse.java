package com.xdream.multimodal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** 批量处理响应DTO */
@Data
@Schema(description = "批量处理响应")
public class BatchProcessResponse {

  @Schema(description = "批次ID")
  private String batchId;

  @Schema(description = "任务ID列表")
  private java.util.List<String> taskIds;

  @Schema(description = "处理状态")
  private String status;

  @Schema(description = "创建时间")
  private String createdTime;

  @Schema(description = "预计完成时间")
  private String estimatedCompletionTime;

  @Schema(description = "总任务数")
  private Integer totalTasks;

  @Schema(description = "完成任务数")
  private Integer completedTasks;

  @Schema(description = "失败任务数")
  private Integer failedTasks;
}
