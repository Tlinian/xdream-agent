package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "文档更新请求")
public class UpdateDocumentRequest {
    
    @Size(max = 200, message = "文档标题不能超过200个字符")
    @Schema(description = "文档标题")
    private String title;
    
    @Size(max = 50000, message = "文档内容不能超过50000个字符")
    @Schema(description = "文档内容")
    private String content;
    
    @Size(max = 500, message = "文档描述不能超过500个字符")
    @Schema(description = "文档描述")
    private String description;
    
    @Size(max = 100, message = "分类名称不能超过100个字符")
    @Schema(description = "分类名称")
    private String category;
    
    @Size(max = 500, message = "标签不能超过500个字符")
    @Schema(description = "标签，多个用逗号分隔")
    private String tags;
}