package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "鏂囨。鍝嶅簲")
public class DocumentResponse {
    
    @Schema(description = "鏂囨。ID")
    private String documentId;
    
    @Schema(description = "鏂囨。鏍囬")
    private String title;
    
    @Schema(description = "鏂囨。鍐呭")
    private String content;
    
    @Schema(description = "鏂囨。鎻忚堪")
    private String description;
    
    @Schema(description = "鏂囨。绫诲瀷")
    private String documentType;
    
    @Schema(description = "鍒嗙被")
    private String category;
    
    @Schema(description = "鏍囩鍒楄〃")
    private List<String> tags;
    
    @Schema(description = "鏂囦欢澶у皬锛堝瓧鑺傦級")
    private Long fileSize;
    
    @Schema(description = "鏂囨。鐘舵€?)
    private String status;
    
    @Schema(description = "澶勭悊鐘舵€?)
    private String processStatus;
    
    @Schema(description = "澶勭悊淇℃伅")
    private String processMessage;
    
    @Schema(description = "鍚戦噺鏁伴噺")
    private Integer vectorCount;
    
    @Schema(description = "鍒涘缓鑰匢D")
    private String createdBy;
    
    @Schema(description = "鍒涘缓鏃堕棿")
    private LocalDateTime createdAt;
    
    @Schema(description = "鏇存柊鏃堕棿")
    private LocalDateTime updatedAt;
    
    @Schema(description = "澶勭悊鏃堕棿")
    private LocalDateTime processedAt;
}

