package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "浣跨敤鎯呭喌鍝嶅簲")
public class UsageResponse {

  @Schema(description = "鐢ㄦ埛ID")
  private String userId;

  @Schema(description = "缁熻寮€濮嬫棩鏈?)
  private LocalDate startDate;

  @Schema(description = "缁熻缁撴潫鏃ユ湡")
  private LocalDate endDate;

  @Schema(description = "鎬讳娇鐢ㄦ儏鍐?)
  private UsageSummary totalUsage;

  @Schema(description = "鍚勬ā鍨嬩娇鐢ㄦ儏鍐?)
  private Map<String, ModelUsage> modelUsage;

  @Schema(description = "姣忔棩浣跨敤鎯呭喌")
  private List<DailyUsage> dailyUsage;

  @Schema(description = "鍝嶅簲鏃堕棿")
  private LocalDateTime responseTime;

  @Data
  public static class UsageSummary {
    @Schema(description = "鎬昏姹傛暟", example = "100")
    private Integer totalRequests;

    @Schema(description = "鎬籺oken浣跨敤閲?, example = "50000")
    private Integer totalTokens;

    @Schema(description = "鎬昏緭鍏oken鏁?, example = "30000")
    private Integer totalInputTokens;

    @Schema(description = "鎬昏緭鍑簍oken鏁?, example = "20000")
    private Integer totalOutputTokens;

    @Schema(description = "鎬昏垂鐢紙USD锛?, example = "0.15")
    private Double totalCost;

    @Schema(description = "骞冲潎鍝嶅簲鏃堕棿锛堟绉掞級", example = "1200")
    private Double averageResponseTime;
  }

  @Data
  public static class ModelUsage {
    @Schema(description = "妯″瀷鍚嶇О", example = "gpt-3.5-turbo")
    private String modelName;

    @Schema(description = "璇锋眰鏁?, example = "50")
    private Integer requestCount;

    @Schema(description = "token浣跨敤閲?, example = "25000")
    private Integer tokenUsage;

    @Schema(description = "杈撳叆token鏁?, example = "15000")
    private Integer inputTokens;

    @Schema(description = "杈撳嚭token鏁?, example = "10000")
    private Integer outputTokens;

    @Schema(description = "璐圭敤锛圲SD锛?, example = "0.075")
    private Double cost;
  }

  @Data
  public static class DailyUsage {
    @Schema(description = "鏃ユ湡")
    private LocalDate date;

    @Schema(description = "璇锋眰鏁?, example = "10")
    private Integer requests;

    @Schema(description = "token浣跨敤閲?, example = "5000")
    private Integer tokens;

    @Schema(description = "璐圭敤锛圲SD锛?, example = "0.015")
    private Double cost;
  }
}
