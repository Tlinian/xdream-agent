package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "使用情况响应")
public class UsageResponse {

  @Schema(description = "用户ID")
  private String userId;

  @Schema(description = "统计开始日期")
  private LocalDate startDate;

  @Schema(description = "统计结束日期")
  private LocalDate endDate;

  @Schema(description = "总使用情况")
  private UsageSummary totalUsage;

  @Schema(description = "各模型使用情况")
  private Map<String, ModelUsage> modelUsage;

  @Schema(description = "每日使用情况")
  private List<DailyUsage> dailyUsage;

  @Schema(description = "响应时间")
  private LocalDateTime responseTime;

  @Data
  public static class UsageSummary {
    @Schema(description = "总请求数", example = "100")
    private Integer totalRequests;

    @Schema(description = "总token使用量", example = "50000")
    private Integer totalTokens;

    @Schema(description = "总输入token数", example = "30000")
    private Integer totalInputTokens;

    @Schema(description = "总输出token数", example = "20000")
    private Integer totalOutputTokens;

    @Schema(description = "总费用（USD）", example = "0.15")
    private Double totalCost;

    @Schema(description = "平均响应时间（毫秒）", example = "1200")
    private Double averageResponseTime;
  }

  @Data
  public static class ModelUsage {
    @Schema(description = "模型名称", example = "gpt-3.5-turbo")
    private String modelName;

    @Schema(description = "请求数", example = "50")
    private Integer requestCount;

    @Schema(description = "token使用量", example = "25000")
    private Integer tokenUsage;

    @Schema(description = "输入token数", example = "15000")
    private Integer inputTokens;

    @Schema(description = "输出token数", example = "10000")
    private Integer outputTokens;

    @Schema(description = "费用（USD）", example = "0.075")
    private Double cost;
  }

  @Data
  public static class DailyUsage {
    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "请求数", example = "10")
    private Integer requests;

    @Schema(description = "token使用量", example = "5000")
    private Integer tokens;

    @Schema(description = "费用（USD）", example = "0.015")
    private Double cost;
  }
}
