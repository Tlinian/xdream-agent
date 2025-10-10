package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "分类响应")
public class CategoryResponse {
    
    @Schema(description = "分类ID")
    private String categoryId;
    
    @Schema(description = "分类名称")
    private String name;
    
    @Schema(description = "分类描述")
    private String description;
    
    @Schema(description = "父分类ID")
    private String parentId;
    
    @Schema(description = "排序序号")
    private Integer sortOrder;
    
    @Schema(description = "文档数量")
    private Integer documentCount;
    
    @Schema(description = "子分类列表")
    private List<CategoryResponse> children;
    
    @Schema(description = "创建者ID")
    private String createdBy;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}