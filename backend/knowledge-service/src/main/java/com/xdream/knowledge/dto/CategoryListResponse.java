package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "鍒嗙被鍒楄〃鍝嶅簲")
public class CategoryListResponse {
    
    @Schema(description = "鍒嗙被鍒楄〃")
    private List<CategoryResponse> categories;
    
    @Schema(description = "鎬绘暟閲?)
    private Long totalCount;
}
