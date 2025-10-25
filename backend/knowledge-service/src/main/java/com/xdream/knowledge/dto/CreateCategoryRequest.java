package com.xdream.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "鍒涘缓鍒嗙被璇锋眰")
public class CreateCategoryRequest {
    
    @NotBlank(message = "鍒嗙被鍚嶇О涓嶈兘涓虹┖")
    @Size(max = 100, message = "鍒嗙被鍚嶇О涓嶈兘瓒呰繃100涓瓧绗?)
    @Schema(description = "鍒嗙被鍚嶇О", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Size(max = 500, message = "鍒嗙被鎻忚堪涓嶈兘瓒呰繃500涓瓧绗?)
    @Schema(description = "鍒嗙被鎻忚堪")
    private String description;
    
    @Schema(description = "鐖跺垎绫籌D")
    private String parentId;
    
    @Schema(description = "鎺掑簭搴忓彿")
    private Integer sortOrder = 0;
}
