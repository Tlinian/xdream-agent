package com.xdream.test.controller;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

  @Value("${ai.personality.system-prompt:}")
  private String systemPrompt;

  @Value("${ai.personality.name:}")
  private String aiName;

  @Value("${ai.personality.greeting:}")
  private String greetingMessage;

  @Value("${ai.personality.help-message:}")
  private String helpMessage;

  @PostConstruct
  public void initAIPersonality() {
    if (systemPrompt == null || systemPrompt.trim().isEmpty()) {
      try {
        Yaml yaml = new Yaml();
        ClassPathResource resource = new ClassPathResource("ai-personality.yml");
        if (resource.exists()) {
          Map<String, Object> config = yaml.load(resource.getInputStream());
          Map<String, Object> personality =
              (Map<String, Object>) ((Map<String, Object>) config.get("ai")).get("personality");

          systemPrompt = (String) personality.get("system-prompt");
          aiName = (String) personality.get("name");
          greetingMessage = (String) personality.get("greeting");
          helpMessage = (String) personality.get("help-message");

          log.info("AI personality loaded from configuration file");
        }
      } catch (Exception e) {
        log.warn(
            "Failed to load AI personality from config file, using defaults: {}", e.getMessage());
        systemPrompt = "你是AI智能体小梦，你能帮助解答问题、提供建议、协助创作或处理各种任务。无论是学习、工作、生活还是娱乐相关的问题，你都会尽力提供清晰、有用的信息。";
        aiName = "小梦";
        greetingMessage = "你好！我是AI智能体小梦，很高兴为您服务。我能帮助解答问题、提供建议、协助创作或处理各种任务。有什么可以帮助您的吗？";
        helpMessage =
            "作为AI智能体小梦，我可以帮助您：\n1. 回答各种问题\n2. 提供建议和信息\n3. 协助文本创作\n4. 解释概念和原理\n5. 处理学习、工作、生活、娱乐相关的任务\n请告诉我您需要什么帮助！";
      }
    }
    log.info(
        "AI personality initialized - Name: {}, System prompt length: {}",
        aiName,
        systemPrompt != null ? systemPrompt.length() : 0);
  }

  @GetMapping("/ai-personality")
  public String getAIPersonalityInfo() {
    return String.format(
        "AI角色: %s\n系统提示: %s",
        aiName != null && !aiName.trim().isEmpty() ? aiName : "小梦",
        systemPrompt != null && !systemPrompt.trim().isEmpty() ? systemPrompt : "AI智能体助手");
  }

  @GetMapping("/greeting")
  public String getGreeting() {
    return greetingMessage != null && !greetingMessage.trim().isEmpty()
        ? greetingMessage
        : "你好！我是AI智能体小梦，很高兴为您服务。";
  }

  @GetMapping("/help")
  public String getHelp() {
    return helpMessage != null && !helpMessage.trim().isEmpty()
        ? helpMessage
        : "我可以帮助您回答各种问题、提供建议和信息。";
  }

  @GetMapping("/system-prompt")
  public String getSystemPrompt() {
    return systemPrompt != null && !systemPrompt.trim().isEmpty()
        ? systemPrompt
        : "你是AI智能体小梦，你能帮助解答问题、提供建议、协助创作或处理各种任务。";
  }
  
  @Data
  public static class ChatRequest {
    private String message;
    private String modelType;
    private Double temperature;
    private Integer maxTokens;
  }
  
  @Data
  public static class ChatResponse {
    private String id;
    private String response;
    private String modelType;
    private Integer tokenUsage;
    private String finishReason;
    private String responseTime;
  }
  
  @PostMapping("/chat/simple")
  public ChatResponse handleChatRequest(@RequestBody ChatRequest request) {
    log.info("Received chat request: {}", request.getMessage());
    
    ChatResponse response = new ChatResponse();
    response.setId(java.util.UUID.randomUUID().toString());
    response.setModelType(request.getModelType() != null ? request.getModelType() : "default");
    response.setTokenUsage(100);
    response.setFinishReason("stop");
    response.setResponseTime(java.time.LocalDateTime.now().toString());
    
    // 检查是否是"你是"相关的问题
    if (request.getMessage() != null && 
        (request.getMessage().contains("你是") || 
         request.getMessage().contains("你是谁") || 
         request.getMessage().contains("介绍一下你自己"))) {
      // 回复符合系统提示词设定的自我介绍
      response.setResponse(String.format("我是%s，%s", 
          aiName != null && !aiName.trim().isEmpty() ? aiName : "小梦",
          "一个友善、专业的AI助手，致力于为用户提供准确、有用的信息和帮助。"));
    } else {
      // 对于其他问题，返回模拟的回复
      response.setResponse("这是一个模拟的AI回复：" + request.getMessage());
    }
    
    return response;
  }
}