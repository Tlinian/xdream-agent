package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文档上传请求体。
 */
@Data
@Schema(description = "文档上传请求")
public class UploadDocumentRequest {

    @NotBlank(message = "知识库ID不能为空")
    @Schema(description = "知识库ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String knowledgeBaseId;

    @NotBlank(message = "文档标题不能为空")
    @Size(max = 200, message = "文档标题不能超过200个字符")
    @Schema(description = "文档标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "文档内容不能为空")
    @Size(max = 50000, message = "文档内容不能超过50000个字符")
    @Schema(description = "文档内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Size(max = 500, message = "文档描述不能超过500个字符")
    @Schema(description = "文档描述")
    private String description;

    @Size(max = 100, message = "文档类型不能超过100个字符")
    @Schema(description = "文档类型")
    private String documentType;

    @Size(max = 50, message = "来源类型不能超过50个字符")
    @Schema(description = "文档来源类型", example = "manual")
    private String sourceType = "manual";

    @Size(max = 100, message = "分类名称不能超过100个字符")
    @Schema(description = "分类名称")
    private String category;

    @Size(max = 500, message = "标签总长度不能超过500个字符")
    @Schema(description = "标签，多个用逗号分隔")
    private String tags;

    @Schema(description = "是否上传后立即启动向量化处理")
    private Boolean autoProcess = true;
}

