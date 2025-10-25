package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "妯″瀷淇℃伅鍝嶅簲")
public class ModelInfoResponse {

  @Schema(description = "妯″瀷ID", example = "gpt-3.5-turbo")
  private String id;

  @Schema(description = "妯″瀷鍚嶇О", example = "GPT-3.5 Turbo")
  private String name;

  @Schema(description = "妯″瀷绫诲瀷", example = "chat")
  private String type;

  @Schema(description = "妯″瀷鎻忚堪", example = "OpenAI GPT-3.5 Turbo妯″瀷")
  private String description;

  @Schema(description = "鏄惁鍙敤", example = "true")
  private Boolean available;

  @Schema(description = "鏈€澶oken鏁?, example = "4096")
  private Integer maxTokens;

  @Schema(description = "涓婁笅鏂囬暱搴?, example = "4096")
  private Integer contextLength;

  @Schema(description = "璁粌鏁版嵁鎴鏃堕棿", example = "2023-09")
  private String trainingDataCutoff;

  @Schema(description = "鏀寔鐨勫弬鏁?)
  private SupportedParameters supportedParameters;

  @Schema(description = "浠锋牸淇℃伅")
  private Pricing pricing;

  @Schema(description = "鍒涘缓鏃堕棿")
  private LocalDateTime createdAt;

  @Schema(description = "鏇存柊鏃堕棿")
  private LocalDateTime updatedAt;

  @Data
  public static class SupportedParameters {
    @Schema(description = "鏀寔娓╁害鍙傛暟", example = "true")
    private Boolean temperature;

    @Schema(description = "鏀寔top_p鍙傛暟", example = "true")
    private Boolean topP;

    @Schema(description = "鏀寔棰戠巼鎯╃綒", example = "true")
    private Boolean frequencyPenalty;

    @Schema(description = "鏀寔瀛樺湪鎯╃綒", example = "true")
    private Boolean presencePenalty;

    @Schema(description = "鏀寔鏈€澶oken鏁?, example = "true")
    private Boolean maxTokens;

    @Schema(description = "鏀寔娴佸紡鍝嶅簲", example = "true")
    private Boolean stream;
  }

  @Data
  public static class Pricing {
    @Schema(description = "杈撳叆浠锋牸锛堟瘡1K tokens锛?, example = "0.0015")
    private Double inputPrice;

    @Schema(description = "杈撳嚭浠锋牸锛堟瘡1K tokens锛?, example = "0.002")
    private Double outputPrice;

    @Schema(description = "璐у竵鍗曚綅", example = "USD")
    private String currency;

    @Schema(description = "璁¤垂鏂瑰紡", example = "per_token")
    private String billingMethod;
  }
}
