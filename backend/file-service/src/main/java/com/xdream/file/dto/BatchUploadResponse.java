package com.xdream.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量文件上传响应")
public class BatchUploadResponse {
    
    @Schema(description = "成功上传的文件列表")
    private List<FileUploadResponse> successfulFiles;
    
    @Schema(description = "上传失败的文件列表")
    private List<FileErrorResponse> failedFiles;
    
    @Schema(description = "总文件数量")
    private Integer totalFiles;
    
    @Schema(description = "成功数量")
    private Integer successCount;
    
    @Schema(description = "失败数量")
    private Integer failCount;
    
    @Schema(description = "总耗时（毫秒）")
    private Long duration;
    
    @Data
    @Schema(description = "文件错误响应")
    public static class FileErrorResponse {
        
        @Schema(description = "文件名称")
        private String fileName;
        
        @Schema(description = "错误信息")
        private String errorMessage;
    }
}