package com.xdream.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "文件分享请求")
public class FileShareRequest {
    
    @Min(value = 1, message = "有效期必须大于0")
    @Schema(description = "有效期（小时）")
    private Integer expireHours = 24;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "访问密码")
    private String password;
    
    @Schema(description = "最大下载次数")
    private Integer maxDownloads = 100;
    
    @Schema(description = "备注信息")
    private String remark;
}