package com.xdream.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "文件上传响应")
public class FileUploadResponse {
    
    @Schema(description = "文件ID")
    private String fileId;
    
    @Schema(description = "文件名称")
    private String fileName;
    
    @Schema(description = "文件大小（字节）")
    private Long fileSize;
    
    @Schema(description = "文件类型")
    private String fileType;
    
    @Schema(description = "文件MIME类型")
    private String mimeType;
    
    @Schema(description = "文件URL")
    private String fileUrl;
    
    @Schema(description = "文件MD5")
    private String md5;
    
    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;
    
    @Schema(description = "过期时间（如果有）")
    private LocalDateTime expireTime;
}