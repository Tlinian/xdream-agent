package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * 重排序响应，包含得分与原始索引。
 */
@Data
@Schema(description = "重排序响应")
public class RerankResponseDto {

    @Schema(description = "候选结果")
    private List<Item> results;

    @Data
    public static class Item {
        @Schema(description = "原始下标")
        private Integer index;

        @Schema(description = "重排序分数")
        private Double score;
    }
}

