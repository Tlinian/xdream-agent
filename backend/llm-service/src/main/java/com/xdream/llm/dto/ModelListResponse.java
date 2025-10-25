package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "妯″瀷鍒楄〃鍝嶅簲")
public class ModelListResponse {

  @Schema(description = "鍙敤妯″瀷鍒楄〃")
  private List<ModelInfo> models;

  @Schema(description = "鎬绘暟閲?, example = "5")
  private Integer total;

  @Data
  public static class ModelInfo {
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

    @Schema(description = "浠锋牸淇℃伅")
    private Pricing pricing;
  }

  @Data
  public static class Pricing {
    @Schema(description = "杈撳叆浠锋牸锛堟瘡1K tokens锛?, example = "0.0015")
    private Double inputPrice;

    @Schema(description = "杈撳嚭浠锋牸锛堟瘡1K tokens锛?, example = "0.002")
    private Double outputPrice;

    @Schema(description = "璐у竵鍗曚綅", example = "USD")
    private String currency;
  }
}
