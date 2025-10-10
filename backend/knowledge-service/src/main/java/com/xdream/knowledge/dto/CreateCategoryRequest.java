package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建分类请求")
public class CreateCategoryRequest {
    
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称不能超过100个字符")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Size(max = 500, message = "分类描述不能超过500个字符")
    @Schema(description = "分类描述")
    private String description;
    
    @Schema(description = "父分类ID")
    private String parentId;
    
    @Schema(description = "排序序号")
    private Integer sortOrder = 0;
}