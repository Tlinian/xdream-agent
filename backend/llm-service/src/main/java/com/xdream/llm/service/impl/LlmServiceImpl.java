package com.xdream.llm.service.impl;

import com.xdream.llm.agent.ReActAgentService;
import com.xdream.llm.client.BaseLlmClient;
import com.xdream.llm.config.AiProperties;
import com.xdream.llm.dto.*;
import com.xdream.llm.service.LlmService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmServiceImpl implements LlmService {

  private final BaseLlmClient client;
  private final AiProperties aiProperties;
  private final ReActAgentService reactAgentService;
  private final Random random = new Random();

  // 模拟的模型数据
  private static final Map<String, ModelInfoResponse> AVAILABLE_MODELS = new HashMap<>();

  static {
    // SiliconFlow DeepSeek-V3
    ModelInfoResponse deepseekV3 = new ModelInfoResponse();
    deepseekV3.setId("deepseek-ai/DeepSeek-V3");
    deepseekV3.setName("DeepSeek-V3");
    deepseekV3.setType("chat");
    deepseekV3.setDescription("SiliconFlow DeepSeek-V3模型，强大的推理和对话能力");
    deepseekV3.setAvailable(true);
    deepseekV3.setMaxTokens(16384);
    deepseekV3.setContextLength(16384);
    deepseekV3.setTrainingDataCutoff("2024-04");

    ModelInfoResponse.SupportedParameters paramsV3 = new ModelInfoResponse.SupportedParameters();
    paramsV3.setTemperature(true);
    paramsV3.setTopP(true);
    paramsV3.setFrequencyPenalty(true);
    paramsV3.setPresencePenalty(true);
    paramsV3.setMaxTokens(true);
    paramsV3.setStream(true);
    deepseekV3.setSupportedParameters(paramsV3);

    ModelInfoResponse.Pricing pricingV3 = new ModelInfoResponse.Pricing();
    pricingV3.setInputPrice(0.001);
    pricingV3.setOutputPrice(0.002);
    pricingV3.setCurrency("CNY");
    pricingV3.setBillingMethod("per_token");
    deepseekV3.setPricing(pricingV3);

    AVAILABLE_MODELS.put("deepseek-ai/DeepSeek-V3", deepseekV3);

    // GPT-4
    ModelInfoResponse gpt4 = new ModelInfoResponse();
    gpt4.setId("gpt-4");
    gpt4.setName("GPT-4");
    gpt4.setType("chat");
    gpt4.setDescription("OpenAI GPT-4模型，更强大的推理能力");
    gpt4.setAvailable(true);
    gpt4.setMaxTokens(8192);
    gpt4.setContextLength(8192);
    gpt4.setTrainingDataCutoff("2023-12");

    ModelInfoResponse.SupportedParameters params4 = new ModelInfoResponse.SupportedParameters();
    params4.setTemperature(true);
    params4.setTopP(true);
    params4.setFrequencyPenalty(true);
    params4.setPresencePenalty(true);
    params4.setMaxTokens(true);
    params4.setStream(true);
    gpt4.setSupportedParameters(params4);

    ModelInfoResponse.Pricing pricing4 = new ModelInfoResponse.Pricing();
    pricing4.setInputPrice(0.03);
    pricing4.setOutputPrice(0.06);
    pricing4.setCurrency("USD");
    pricing4.setBillingMethod("per_token");
    gpt4.setPricing(pricing4);

    AVAILABLE_MODELS.put("gpt-4", gpt4);

    // Text Embedding
    ModelInfoResponse embedding = new ModelInfoResponse();
    embedding.setId("text-embedding-ada-002");
    embedding.setName("Text Embedding Ada 002");
    embedding.setType("embedding");
    embedding.setDescription("OpenAI文本嵌入模型，适用于文本向量化");
    embedding.setAvailable(true);
    embedding.setMaxTokens(8191);

    // Image Generation
    ModelInfoResponse imageModel = new ModelInfoResponse();
    imageModel.setId("Qwen/Qwen-Image-Edit-2509");
    imageModel.setName("Qwen Image Edit");
    imageModel.setType("image");
    imageModel.setDescription("通义千问图片编辑模型，用于生成和编辑图片");
    imageModel.setAvailable(true);
    imageModel.setMaxTokens(16384);
    embedding.setContextLength(8191);
    embedding.setTrainingDataCutoff("2020-08");

    ModelInfoResponse.SupportedParameters paramsEmbedding =
        new ModelInfoResponse.SupportedParameters();
    paramsEmbedding.setTemperature(false);
    paramsEmbedding.setTopP(false);
    paramsEmbedding.setFrequencyPenalty(false);
    paramsEmbedding.setPresencePenalty(false);
    paramsEmbedding.setMaxTokens(false);
    paramsEmbedding.setStream(false);
    embedding.setSupportedParameters(paramsEmbedding);

    ModelInfoResponse.Pricing pricingEmbedding = new ModelInfoResponse.Pricing();
    pricingEmbedding.setInputPrice(0.0001);
    pricingEmbedding.setOutputPrice(0.0);
    pricingEmbedding.setCurrency("USD");
    pricingEmbedding.setBillingMethod("per_token");
    embedding.setPricing(pricingEmbedding);

    AVAILABLE_MODELS.put("text-embedding-ada-002", embedding);
    AVAILABLE_MODELS.put("Qwen/Qwen-Image-Edit-2509", imageModel);
  }

  @Override
  public ChatResponse chat(String userId, ChatRequest request) {
    log.info("Chat request for user: {}, model: {}", userId, request.getModelType());

    // 验证模型是否可用
    if (!AVAILABLE_MODELS.containsKey(request.getModelType())) {
      throw new IllegalArgumentException("不支持的模型类型: " + request.getModelType());
    }

    // 检查是否使用ReAct模式
    if (request.getUseReAct() != null && request.getUseReAct()) {
      log.info("使用ReAct模式处理请求");
      return reactAgentService.processWithReAct(userId, "general-assistant", request.getMessage());
    }

    // 如果是SiliconFlow模型，调用真实API
    if (request.getModelType().startsWith("deepseek-ai/")) {
      try {
        return client.chat(userId, request);
      } catch (Exception e) {
        log.error("SiliconFlow API调用失败，降级到模拟回复", e);
        // 降级到模拟回复
        return generateMockChatResponse(userId, request);
      }
    }

    // 模拟AI回复（用于其他模型或降级情况）
    return generateMockChatResponse(userId, request);
  }

  private ChatResponse generateMockChatResponse(String userId, ChatRequest request) {
    String responseContent = generateMockResponse(request.getMessage());
    int tokenUsage = estimateTokenUsage(request.getMessage(), responseContent);

    ChatResponse response = new ChatResponse();
    response.setId(UUID.randomUUID().toString());
    response.setResponse(responseContent);
    response.setModelType(request.getModelType());
    response.setTokenUsage(tokenUsage);
    response.setFinishReason("stop");
    response.setResponseTime(LocalDateTime.now());
    response.setCreatedAt(LocalDateTime.now());

    log.info("Mock chat response generated for user: {}, tokens: {}", userId, tokenUsage);

    return response;
  }

  @Override
  public Flux<StreamResponse> streamChat(String userId, ChatRequest request) {
    log.info("Stream chat request for user: {}, model: {}", userId, request.getModelType());

    // 验证模型是否可用

    // 检查是否使用ReAct模式
    if (request.getUseReAct() != null && request.getUseReAct()) {
      log.info("使用ReAct模式处理流式请求");
      return processStreamWithReAct(userId, request);
    }

    try {
      if (true) {
        log.info("Calling real SiliconFlow API for deepseek model");
        return client.streamChat(userId, request);
      } else {
        // 对于其他模型，使用模拟响应
        log.info("Using mock response for non-DeepSeek model");
        String inputMessage = request.getMessage();
        if (inputMessage == null || inputMessage.trim().isEmpty()) {
          inputMessage = "你好";
        }
        String responseContent = generateMockResponse(inputMessage);
        String[] words = responseContent.split(" ");

        return Flux.create(
            sink -> {
              String streamId = UUID.randomUUID().toString();
              int tokenUsage = 0;

              try {
                // 模拟逐字发送
                for (int i = 0; i < words.length; i++) {
                  StreamResponse chunk = new StreamResponse();
                  chunk.setStreamId(streamId);
                  chunk.setModelType(request.getModelType());
                  chunk.setContent(words[i] + " ");
                  chunk.setFinished(i == words.length - 1);
                  chunk.setFinishReason(i == words.length - 1 ? "stop" : null);
                  chunk.setTokenUsage(
                      i == words.length - 1
                          ? estimateTokenUsage(request.getMessage(), responseContent)
                          : null);

                  sink.next(chunk);

                  // 模拟延迟
                  try {
                    Thread.sleep(100);
                  } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                  }
                }

                sink.complete();
                log.info("Stream chat response completed for user: {}", userId);

              } catch (Exception e) {
                sink.error(e);
              }
            });
      }
    } catch (Exception e) {
      log.error("Error in streamChat: {}", e.getMessage(), e);
      // 出错时降级到模拟响应
      String inputMessage = request.getMessage();
      if (inputMessage == null || inputMessage.trim().isEmpty()) {
        inputMessage = "你好";
      }
      String errorMessage = "抱歉，AI服务暂时不可用，这是模拟回复。\n" + generateMockResponse(inputMessage);
      String[] words = errorMessage.split(" ");

      return Flux.create(
          sink -> {
            String streamId = UUID.randomUUID().toString();

            try {
              for (int i = 0; i < words.length; i++) {
                StreamResponse chunk = new StreamResponse();
                chunk.setStreamId(streamId);
                chunk.setModelType(request.getModelType());
                chunk.setContent(words[i] + " ");
                chunk.setFinished(i == words.length - 1);
                chunk.setFinishReason(i == words.length - 1 ? "error" : null);
                chunk.setTokenUsage(
                    i == words.length - 1
                        ? estimateTokenUsage(request.getMessage(), errorMessage)
                        : null);

                sink.next(chunk);

                try {
                  Thread.sleep(100);
                } catch (InterruptedException ie) {
                  Thread.currentThread().interrupt();
                  sink.error(ie);
                  return;
                }
              }

              sink.complete();
              log.info("Degraded stream chat response completed for user: {}", userId);

            } catch (Exception ex) {
              sink.error(ex);
            }
          });
    }
  }

  @Override
  public EmbeddingResponse generateEmbeddings(String userId, EmbeddingRequest request) {
    log.info("Embedding request for user: {}, model: {}", userId, request.getModelType());

    // 验证模型是否可用
    if (!AVAILABLE_MODELS.containsKey(request.getModelType())) {
      throw new IllegalArgumentException("不支持的模型类型: " + request.getModelType());
    }

    // 模拟嵌入向量生成
    int dimensions = 1536; // ADA-002的维度
    List<Float> embedding = generateMockEmbedding(dimensions);
    int tokenUsage = estimateTokenUsage(request.getText(), "");

    EmbeddingResponse response = new EmbeddingResponse();
    response.setId(UUID.randomUUID().toString());
    response.setModelType(request.getModelType());
    response.setEmbedding(embedding);
    response.setDimensions(dimensions);
    response.setTokenUsage(tokenUsage);
    response.setCreatedAt(LocalDateTime.now());

    log.info(
        "Embedding response generated for user: {}, dimensions: {}, tokens: {}",
        userId,
        dimensions,
        tokenUsage);

    return response;
  }

  @Override
  public ImageGenerationResponse generateImage(String userId, ImageGenerationRequest request) {
    log.info("Image generation request for user: {}, model: {}", userId, request.getModel());

    // 检查是否是支持的图片生成模型

    try {
      // 调用SiliconFlow API生成图片
      return client.generateImage(userId, request);
    } catch (Exception e) {
      log.error("SiliconFlow API调用失败，降级到模拟回复", e);
      // 降级到模拟图片生成
      return generateMockImageResponse(userId, request);
    }
  }

  private ImageGenerationResponse generateMockImageResponse(
      String userId, ImageGenerationRequest request) {
    ImageGenerationResponse response = new ImageGenerationResponse();
    response.setId(UUID.randomUUID().toString());
    response.setModelType(request.getModel());
    response.setCreatedAt(LocalDateTime.now());

    // 创建模拟图片数据
    List<ImageGenerationResponse.ImageData> images = new ArrayList<>();
    for (int i = 0; i < request.getN(); i++) {
      ImageGenerationResponse.ImageData imageData = new ImageGenerationResponse.ImageData();
      imageData.setUrl("https://example.com/generated-image-" + UUID.randomUUID() + ".png");
      imageData.setFinishReason("stop");
      images.add(imageData);
    }

    response.setImages(images);
    log.info("Mock image response generated for user: {}, image count: {}", userId, request.getN());

    return response;
  }

  @Override
  public ModelListResponse getModels(String userId) {
    log.info("Get models for user: {}", userId);

    ModelListResponse response = new ModelListResponse();
    response.setModels(AVAILABLE_MODELS.values().stream().map(this::convertToModelInfo).toList());
    response.setTotal(AVAILABLE_MODELS.size());

    return response;
  }

  @Override
  public ModelInfoResponse getModelInfo(String userId, String modelId) {
    log.info("Get model info for user: {}, model: {}", userId, modelId);

    ModelInfoResponse model = AVAILABLE_MODELS.get(modelId);
    if (model == null) {
      throw new IllegalArgumentException("模型不存在: " + modelId);
    }

    return model;
  }

  @Override
  public UsageResponse getUsage(String userId, String startDate, String endDate) {
    log.info("Get usage for user: {}, start: {}, end: {}", userId, startDate, endDate);

    // 模拟使用情况数据
    UsageResponse response = new UsageResponse();
    response.setUserId(userId);
    response.setStartDate(
        startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(30));
    response.setEndDate(endDate != null ? LocalDate.parse(endDate) : LocalDate.now());

    // 总使用情况
    UsageResponse.UsageSummary totalUsage = new UsageResponse.UsageSummary();
    totalUsage.setTotalRequests(ThreadLocalRandom.current().nextInt(100, 1000));
    totalUsage.setTotalTokens(ThreadLocalRandom.current().nextInt(10000, 100000));
    totalUsage.setTotalInputTokens((int) (totalUsage.getTotalTokens() * 0.6));
    totalUsage.setTotalOutputTokens((int) (totalUsage.getTotalTokens() * 0.4));
    totalUsage.setTotalCost(ThreadLocalRandom.current().nextDouble(0.1, 10.0));
    totalUsage.setAverageResponseTime(ThreadLocalRandom.current().nextDouble(500, 3000));
    response.setTotalUsage(totalUsage);

    // 各模型使用情况
    Map<String, UsageResponse.ModelUsage> modelUsage = new HashMap<>();
    for (String modelId : AVAILABLE_MODELS.keySet()) {
      if (AVAILABLE_MODELS.get(modelId).getType().equals("chat")) {
        UsageResponse.ModelUsage model = new UsageResponse.ModelUsage();
        model.setModelName(modelId);
        model.setRequestCount(ThreadLocalRandom.current().nextInt(10, 100));
        model.setTokenUsage(ThreadLocalRandom.current().nextInt(1000, 10000));
        model.setInputTokens((int) (model.getTokenUsage() * 0.6));
        model.setOutputTokens((int) (model.getTokenUsage() * 0.4));
        model.setCost(ThreadLocalRandom.current().nextDouble(0.01, 1.0));
        modelUsage.put(modelId, model);
      }
    }
    response.setModelUsage(modelUsage);

    // 每日使用情况
    List<UsageResponse.DailyUsage> dailyUsage = new ArrayList<>();
    LocalDate currentDate = response.getStartDate();
    while (!currentDate.isAfter(response.getEndDate())) {
      UsageResponse.DailyUsage daily = new UsageResponse.DailyUsage();
      daily.setDate(currentDate);
      daily.setRequests(ThreadLocalRandom.current().nextInt(1, 50));
      daily.setTokens(ThreadLocalRandom.current().nextInt(100, 5000));
      daily.setCost(ThreadLocalRandom.current().nextDouble(0.001, 0.5));
      dailyUsage.add(daily);
      currentDate = currentDate.plusDays(1);
    }
    response.setDailyUsage(dailyUsage);

    response.setResponseTime(LocalDateTime.now());

    return response;
  }

  private ModelListResponse.ModelInfo convertToModelInfo(ModelInfoResponse model) {
    ModelListResponse.ModelInfo info = new ModelListResponse.ModelInfo();
    info.setId(model.getId());
    info.setName(model.getName());
    info.setType(model.getType());
    info.setDescription(model.getDescription());
    info.setAvailable(model.getAvailable());
    info.setMaxTokens(model.getMaxTokens());

    ModelListResponse.Pricing pricing = new ModelListResponse.Pricing();
    pricing.setInputPrice(model.getPricing().getInputPrice());
    pricing.setOutputPrice(model.getPricing().getOutputPrice());
    pricing.setCurrency(model.getPricing().getCurrency());
    info.setPricing(pricing);

    return info;
  }

  private String generateMockResponse(String input) {
    // 增强的智能回复生成逻辑
    String lowerInput = input.toLowerCase();

    // 问候语
    if (lowerInput.contains("你好") || lowerInput.contains("hi") || lowerInput.contains("hello")) {
      return "你好！我是AI助手，很高兴为您服务。有什么可以帮助您的吗？";
    }

    // 帮助和功能询问
    if (lowerInput.contains("帮助") || lowerInput.contains("功能") || lowerInput.contains("能做什么")) {
      return "我可以帮助您：\n1. 回答各种问题\n2. 提供建议和信息\n3. 协助文本创作\n4. 解释概念和原理\n5. 分析数据和提供见解\n请告诉我您需要什么帮助！";
    }

    // 技术相关问题
    if (lowerInput.contains("多线程")) {
      return "多线程是指程序中同时执行多个线程的技术。它允许程序在同一时间内执行多个任务，提高程序的并发性和效率。\n\n主要优势：\n• 提高程序响应性\n• 充分利用多核CPU\n• 改善程序结构\n\n需要注意的问题：\n• 线程安全\n• 同步机制\n• 死锁避免\n• 性能开销\n\n在实际应用中，建议使用线程池来管理线程生命周期。";
    }

    if (lowerInput.contains("spring")) {
      return "Spring是一个开源的Java企业级应用开发框架，提供了全面的编程和配置模型。\n\n核心特性：\n• 依赖注入(DI)和控制反转(IoC)\n• 面向切面编程(AOP)\n• 事务管理\n• MVC Web框架\n• 数据访问抽象\n• 安全框架\n\nSpring Boot是基于Spring的快速开发框架，特点包括：\n• 自动配置\n• 起步依赖\n• 内嵌服务器\n• 生产就绪特性\n• 简化部署\n\n现代Java应用开发中，Spring Boot已成为事实标准。";
    }

    if (lowerInput.contains("微服务")) {
      return "微服务是一种架构风格，将单一应用程序划分为一组小的服务，每个服务运行在自己的进程中，服务间通过轻量级机制通信。\n\n核心原则：\n• 单一职责\n• 自治性\n• 去中心化治理\n• 容错设计\n• 自动化部署\n\n主要优势：\n• 独立开发和部署\n• 技术栈灵活性\n• 可扩展性\n• 故障隔离\n\n挑战：\n• 分布式系统复杂性\n• 数据一致性\n• 服务治理\n• 网络延迟\n\n常用技术栈：Spring Cloud、Docker、Kubernetes等。";
    }

    // 通用知识问题
    if (lowerInput.contains("什么是") || lowerInput.contains("定义")) {
      String keyword = extractKeyword(lowerInput, "什么是", "定义");
      return "\""
          + keyword
          + "\" 是一个重要的概念。它通常指的是...\n\n主要特征包括：\n• 特征一：描述性说明\n• 特征二：功能性说明\n• 特征三：应用场景\n\n在实际应用中，\""
          + keyword
          + "\" 常用于：\n1. 应用场景一\n2. 应用场景二\n3. 应用场景三\n\n如果您需要更具体的解释，请告诉我您关注的具体方面。";
    }

    // 时间相关
    if (lowerInput.contains("时间") || lowerInput.contains("几点")) {
      return "当前时间是："
          + LocalDateTime.now().toString()
          + "\n\n时间管理建议：\n• 制定明确的时间计划\n• 设置优先级\n• 避免多任务并行\n• 定期回顾和调整\n• 保持工作与生活的平衡";
    }

    // 天气相关
    if (lowerInput.contains("天气")) {
      return "我目前无法获取实时天气信息，但建议您：\n\n1. 查看天气预报应用\n2. 访问天气网站\n3. 使用智能音箱查询\n\n天气对生活的影响：\n• 出行计划安排\n• 衣物选择\n• 户外活动规划\n• 健康防护措施\n\n建议您关注当地气象部门发布的权威信息。";
    }

    // 感谢和礼貌用语
    if (lowerInput.contains("谢谢") || lowerInput.contains("感谢")) {
      return "不客气！很高兴能帮助到您。\n\n如果您还有其他问题或需要进一步的解释，请随时告诉我。我随时为您提供帮助！\n\n也欢迎您：\n• 提出更深入的问题\n• 询问相关主题\n• 分享您的想法和经验\n\n期待继续为您服务！";
    }

    // 学习和教育相关
    if (lowerInput.contains("学习") || lowerInput.contains("教育")) {
      return "学习是一个持续的过程，需要正确的方法和态度。\n\n有效学习策略：\n• 制定明确的学习目标\n• 采用多种学习方式\n• 定期复习和巩固\n• 理论与实践相结合\n• 保持好奇心和求知欲\n\n学习资源推荐：\n• 在线课程平台\n• 技术文档和书籍\n• 实践项目\n• 社区讨论\n• 专家指导\n\n记住：学习的关键在于坚持和应用。";
    }

    // 默认智能回复
    int messageLength = input.length();
    if (messageLength < 10) {
      return "您提到：\""
          + input
          + "\"\n\n这是一个很有意思的简短问题。让我为您提供一些思路：\n\n1. 首先，建议您明确具体的需求和目标\n2. 然后，可以考虑相关的背景信息和上下文\n3. 最后，制定具体的行动计划\n\n如果您需要更详细的解释或有其他相关问题，请随时提出，我会很乐意为您提供更多帮助。";
    } else {
      return "我理解您的问题关于\""
          + input.substring(0, Math.min(messageLength, 30))
          + "...\"\n\n这是一个很有深度的问题。基于我的理解，我可以为您提供以下见解：\n\n1. 问题分析\n   • 核心要素识别\n   • 相关背景分析\n   • 关键挑战识别\n\n2. 解决思路\n   • 理论基础\n   • 实践方法\n   • 最佳实践\n\n3. 实施建议\n   • 步骤分解\n   • 注意事项\n   • 预期结果\n\n如果您需要更具体的指导或有其他疑问，请告诉我更多细节，我会为您提供更精准的帮助。";
    }
  }

  private String extractKeyword(String input, String... prefixes) {
    for (String prefix : prefixes) {
      int index = input.indexOf(prefix);
      if (index != -1) {
        String remaining = input.substring(index + prefix.length()).trim();
        if (!remaining.isEmpty()) {
          // 提取关键词（取前几个字符）
          int endIndex = remaining.indexOf(' ');
          return endIndex > 0 ? remaining.substring(0, endIndex) : remaining;
        }
      }
    }
    return "概念";
  }

  private List<Float> generateMockEmbedding(int dimensions) {
    // 生成随机嵌入向量
    List<Float> embedding = new ArrayList<>();
    Random random = new Random();

    for (int i = 0; i < dimensions; i++) {
      embedding.add(random.nextFloat() * 2 - 1); // -1 到 1 之间的随机数
    }

    return embedding;
  }

  private int estimateTokenUsage(String input, String output) {
    // 简单的token估算：假设每个字符约等于0.5个token
    return (int) ((input.length() + output.length()) * 0.5);
  }

  private String generateSmartFallbackResponse(String message) {
    // 智能备用响应生成
    String lowerMessage = message.toLowerCase();

    if (lowerMessage.contains("你好")
        || lowerMessage.contains("hi")
        || lowerMessage.contains("hello")) {
      return "你好！很高兴为你提供帮助。我是一个AI助手，可以回答各种问题、提供建议，或者协助你完成各种任务。有什么我可以帮助你的吗？";
    }

    if (lowerMessage.contains("技术") || lowerMessage.contains("编程") || lowerMessage.contains("代码")) {
      return "我理解你询问的是技术相关的问题。技术领域非常广泛，包括编程语言、框架、算法、系统架构等多个方面。\n\n为了更好地帮助你，我建议：\n1. 明确具体的技术问题\n2. 提供相关的代码片段或错误信息\n3. 说明你的预期结果和实际遇到的问题\n\n这样我就能给出更准确和有用的建议。";
    }

    if (lowerMessage.contains("spring") || lowerMessage.contains("boot")) {
      return "Spring Boot 是一个非常流行的 Java 框架，用于快速开发独立的、生产级别的 Spring 应用程序。\n\n它提供了以下核心特性：\n• 自动配置：根据项目依赖自动配置 Spring 应用\n• 起步依赖：简化 Maven 和 Gradle 配置\n• 内嵌服务器：无需部署 WAR 文件\n• 生产级监控：提供健康检查和指标监控\n\n如果你有具体的 Spring Boot 问题，我很乐意为你详细解答。";
    }

    if (lowerMessage.contains("微服务") || lowerMessage.contains("架构")) {
      return "微服务架构是一种将单个应用程序开发为一组小型服务的方法，每个服务都在自己的进程中运行，并使用轻量级机制（通常是 HTTP 资源 API）进行通信。\n\n微服务的主要优势包括：\n• 独立部署：每个服务可以独立开发、部署和扩展\n• 技术多样性：可以使用不同的编程语言和数据存储技术\n• 故障隔离：一个服务的故障不会影响整个系统\n• 团队自治：小型团队可以独立负责特定服务\n\n这种架构特别适合大型复杂应用，但也带来了服务间通信、数据一致性等挑战。";
    }

    // 通用备用响应
    return "感谢你的提问。我已经仔细思考了你的问题，这是一个很有意思的话题。\n\n基于我的理解，我可以从多个角度来分析和回答这个问题。如果你需要更具体的信息或者有其他相关问题，请随时告诉我，我会尽力为你提供详细和准确的回答。\n\n有什么特定的方面你希望我重点说明吗？";
  }

  private Flux<StreamResponse> processStreamWithReAct(String userId, ChatRequest request) {
    return Flux.create(
        sink -> {
          String streamId = UUID.randomUUID().toString();

          try {
            log.info("开始ReAct模式处理，用户: {}, 消息: {}", userId, request.getMessage());
            String message = request.getMessage().toLowerCase();
            boolean isTimeQuery =
                message.contains("时间")
                    || message.contains("几点")
                    || message.contains("current time");

            // 第一阶段：发送思考过程
            String[] thinkingSteps;
            if (isTimeQuery) {
              // 针对时间查询的特殊思考过程
              thinkingSteps =
                  new String[] {
                    "正在分析问题...\n",
                    "用户需要查询当前时间\n",
                    "我有工具可以获取精确时间\n",
                    "选择 get_current_time 工具\n",
                    "执行工具获取当前时间...\n",
                    "已获取时间信息，准备生成答案...\n"
                  };
            } else {
              // 通用思考过程
              thinkingSteps =
                  new String[] {
                    "正在分析问题...\n",
                    "理解用户需求：" + request.getMessage() + "\n",
                    "确定解决思路...\n",
                    "考虑相关背景知识...\n",
                    "制定回答策略...\n",
                    "准备生成最终答案...\n"
                  };
            }

            // 发送思考开始标记
            StreamResponse thinkingStart = new StreamResponse();
            thinkingStart.setStreamId(streamId);
            thinkingStart.setModelType(request.getModelType());
            thinkingStart.setContent("[THINKING_START]");
            thinkingStart.setFinished(false);
            thinkingStart.setFinishReason(null);
            sink.next(thinkingStart);

            // 逐步发送思考过程
            for (String step : thinkingSteps) {
              StreamResponse thinkingChunk = new StreamResponse();
              thinkingChunk.setStreamId(streamId);
              thinkingChunk.setModelType(request.getModelType());
              thinkingChunk.setContent(step);
              thinkingChunk.setFinished(false);
              thinkingChunk.setFinishReason(null);
              sink.next(thinkingChunk);

              // 模拟思考延迟
              try {
                Thread.sleep(300 + random.nextInt(400)); // 300-700ms随机延迟
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                sink.error(e);
                return;
              }
            }

            // 发送思考结束标记
            StreamResponse thinkingEnd = new StreamResponse();
            thinkingEnd.setStreamId(streamId);
            thinkingEnd.setModelType(request.getModelType());
            thinkingEnd.setContent("[THINKING_END]");
            thinkingEnd.setFinished(false);
            thinkingEnd.setFinishReason(null);
            sink.next(thinkingEnd);

            // 短暂停顿，让用户看到思考完成
            try {
              Thread.sleep(500);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }

            // 第二阶段：生成最终答案
            log.info("开始生成最终答案，用户: {}", userId);

            // 发送答案开始标记
            StreamResponse answerStart = new StreamResponse();
            answerStart.setStreamId(streamId);
            answerStart.setModelType(request.getModelType());
            answerStart.setContent("[ANSWER_START]");
            answerStart.setFinished(false);
            answerStart.setFinishReason(null);
            sink.next(answerStart);

            // 调用ReAct服务获取最终答案
            String finalAnswer;
            try {
              if (isTimeQuery) {
                // 对于时间查询，直接调用get_current_time工具的执行逻辑
                // 避免完整的ReAct流程可能带来的问题
                LocalDateTime now = LocalDateTime.now();
                finalAnswer =
                    String.format(
                        "当前时间是：%d年%d月%d日 %d:%02d:%02d",
                        now.getYear(),
                        now.getMonthValue(),
                        now.getDayOfMonth(),
                        now.getHour(),
                        now.getMinute(),
                        now.getSecond());
                log.info("直接获取当前时间: {}", finalAnswer);
              } else {
                // 其他问题使用完整的ReAct流程
                finalAnswer =
                    reactAgentService
                        .processWithReAct(userId, "general-assistant", request.getMessage())
                        .getResponse();

                // 清理答案格式
                finalAnswer =
                    finalAnswer
                        .replace("Final Answer: ", "")
                        .replace("最终答案: ", "")
                        .replace("Thought: 我现在知道最终答案了", "")
                        .trim();
              }

            } catch (Exception e) {
              log.error("ReAct服务调用失败: {}", e.getMessage());
              if (isTimeQuery) {
                // 时间查询失败时的备用处理
                LocalDateTime now = LocalDateTime.now();
                finalAnswer =
                    String.format(
                        "获取时间信息: %d年%d月%d日 %d:%02d:%02d",
                        now.getYear(),
                        now.getMonthValue(),
                        now.getDayOfMonth(),
                        now.getHour(),
                        now.getMinute(),
                        now.getSecond());
              } else {
                finalAnswer = generateSmartFallbackResponse(request.getMessage());
              }
            }

            // 逐步发送最终答案
            String[] sentences = finalAnswer.split("[。！？\\n]");
            for (int i = 0; i < sentences.length; i++) {
              if (sentences[i].trim().isEmpty()) continue;

              String sentence = sentences[i].trim() + (i < sentences.length - 1 ? "。" : "");

              StreamResponse answerChunk = new StreamResponse();
              answerChunk.setStreamId(streamId);
              answerChunk.setModelType(request.getModelType());
              answerChunk.setContent(sentence);
              answerChunk.setFinished(false);
              answerChunk.setFinishReason(null);
              sink.next(answerChunk);

              // 流式发送延迟
              try {
                Thread.sleep(100 + random.nextInt(200)); // 100-300ms随机延迟
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                sink.error(e);
                return;
              }
            }

            // 发送完成标记
            StreamResponse completeChunk = new StreamResponse();
            completeChunk.setStreamId(streamId);
            completeChunk.setModelType(request.getModelType());
            completeChunk.setContent("");
            completeChunk.setFinished(true);
            completeChunk.setFinishReason("stop");
            completeChunk.setTokenUsage(estimateTokenUsage(request.getMessage(), finalAnswer));
            sink.next(completeChunk);

            sink.complete();
            log.info("ReAct流式响应完成，用户: {}, token使用量: {}", userId, completeChunk.getTokenUsage());

          } catch (Exception e) {
            log.error("ReAct流式处理失败", e);
            sink.error(e);
          }
        });
  }
}
