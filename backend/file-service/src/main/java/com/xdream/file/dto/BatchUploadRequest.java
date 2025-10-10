package com.xdream.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "批量文件上传请求")
public class BatchUploadRequest {
    
    @NotNull(message = "文件列表不能为空")
    @Schema(description = "文件列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile[] files;
    
    @Schema(description = "文件类型")
    private String fileType;
    
    @Schema(description = "文件描述")
    private String description;
}