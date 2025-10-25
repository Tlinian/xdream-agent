package com.xdream.llm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "瀵硅瘽鍝嶅簲")
public class ChatResponse {

  @Schema(description = "鍝嶅簲ID")
  private String id;

  @Schema(description = "AI鍥炲鍐呭", example = "澶氱嚎绋嬫槸鎸囩▼搴忎腑鍚屾椂鎵ц澶氫釜绾跨▼...")
  private String response;

  @Schema(description = "浣跨敤鐨勬ā鍨?, example = "gpt-3.5-turbo")
  private String modelType;

  @Schema(description = "token浣跨敤閲?, example = "150")
  private Integer tokenUsage;

  @Schema(description = "鎻愮ずtoken鏁?, example = "100")
  private Integer promptTokens;

  @Schema(description = "瀹屾垚token鏁?, example = "50")
  private Integer completionTokens;

  @Schema(description = "瀹屾垚鍘熷洜", example = "stop")
  private String finishReason;

  @Schema(description = "鍝嶅簲鏃堕棿")
  private LocalDateTime responseTime;

  @Schema(description = "鍒涘缓鏃堕棿")
  private LocalDateTime createdAt;
}
