package com.xdream.llm.dto;

import lombok.Data;

@Data
public class ImageGenerationRequest {

  private String model = "Qwen/Qwen-Image-Edit-2509"; // 默认使用用户提供的模型

  private String prompt; // 图片生成的描述性提示词

  private Integer n = 1; // 生成的图片数量，默认为1

  private String size = "1024x1024"; // 图片尺寸，默认为1024x1024

  private String responseFormat = "url"; // 响应格式，可以是url或base64

  private String user; // 用户标识，可选
}
