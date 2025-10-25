package com.xdream.llm.dto;

import lombok.Data;

@Data
public class ImageGenerationRequest {

  private String model = "Qwen/Qwen-Image-Edit-2509"; // 榛樿浣跨敤鐢ㄦ埛鎻愪緵鐨勬ā鍨?

  private String prompt; // 鍥剧墖鐢熸垚鐨勬弿杩版€ф彁绀鸿瘝

  private Integer n = 1; // 鐢熸垚鐨勫浘鐗囨暟閲忥紝榛樿涓?

  private String size = "1024x1024"; // 鍥剧墖灏哄锛岄粯璁や负1024x1024

  private String responseFormat = "url"; // 鍝嶅簲鏍煎紡锛屽彲浠ユ槸url鎴朾ase64

  private String user; // 鐢ㄦ埛鏍囪瘑锛屽彲閫?
}
