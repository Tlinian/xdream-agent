package com.xdream.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "文件分享响应")
public class FileShareResponse {
    
    @Schema(description = "分享ID")
    private String shareId;
    
    @Schema(description = "分享链接")
    private String shareUrl;
    
    @Schema(description = "访问密码")
    private String password;
    
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
    
    @Schema(description = "最大下载次数")
    private Integer maxDownloads;
    
    @Schema(description = "已下载次数")
    private Integer downloadCount;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}