package com.xdream.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@Schema(description = "文件下载响应")
public class FileDownloadResponse {
    
    @Schema(description = "文件资源")
    private Resource resource;
    
    @Schema(description = "文件名称")
    private String fileName;
    
    @Schema(description = "文件MIME类型")
    private String contentType;
    
    @Schema(description = "文件大小（字节）")
    private Long fileSize;
}