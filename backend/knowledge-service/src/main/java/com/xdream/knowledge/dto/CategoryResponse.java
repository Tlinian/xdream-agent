package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "鍒嗙被鍝嶅簲")
public class CategoryResponse {
    
    @Schema(description = "鍒嗙被ID")
    private String categoryId;
    
    @Schema(description = "鍒嗙被鍚嶇О")
    private String name;
    
    @Schema(description = "鍒嗙被鎻忚堪")
    private String description;
    
    @Schema(description = "鐖跺垎绫籌D")
    private String parentId;
    
    @Schema(description = "鎺掑簭搴忓彿")
    private Integer sortOrder;
    
    @Schema(description = "鏂囨。鏁伴噺")
    private Integer documentCount;
    
    @Schema(description = "瀛愬垎绫诲垪琛?)
    private List<CategoryResponse> children;
    
    @Schema(description = "鍒涘缓鑰匢D")
    private String createdBy;
    
    @Schema(description = "鍒涘缓鏃堕棿")
    private LocalDateTime createdAt;
    
    @Schema(description = "鏇存柊鏃堕棿")
    private LocalDateTime updatedAt;
}
