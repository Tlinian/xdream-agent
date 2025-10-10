package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分类列表响应")
public class CategoryListResponse {
    
    @Schema(description = "分类列表")
    private List<CategoryResponse> categories;
    
    @Schema(description = "总数量")
    private Long totalCount;
}