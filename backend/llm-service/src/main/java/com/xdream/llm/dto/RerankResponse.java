package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "重排序响应")
public class RerankResponse {

  @Schema(description = "结果集合")
  private List<Item> results;

  @Data
  public static class Item {
    @Schema(description = "原始下标")
    private Integer index;

    @Schema(description = "重排序得分")
    private Double score;
  }
}
