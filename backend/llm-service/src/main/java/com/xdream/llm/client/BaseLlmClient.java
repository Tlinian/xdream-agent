package com.xdream.llm.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdream.llm.config.LlmProperties;
import com.xdream.llm.dto.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Slf4j
public class BaseLlmClient {
  private final LlmProperties llmProperties;
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  /** 调用SiliconFlow API进行非流式对话 */
  public ChatResponse chat(String userId, ChatRequest request) {
    // TODO：目前仅支持从配置文件读取模型，后续弄成从请求参数中读取
    log.info("user: {}, model: {}", userId, llmProperties.getChat().getModel());

    try {
      // 构建请求体
      Map<String, Object> requestBody =
          buildRequestBody(
              request, llmProperties.getChat().getModel(), llmProperties.getChat().getMaxTokens());

      // 设置请求头
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("Authorization", "Bearer " + llmProperties.getChat().getApiKey());

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

      // 调用SiliconFlow API
      String apiUrl =
          llmProperties.getChat().getBaseUrl() + llmProperties.getChat().getInterfaceUrl();
      log.debug("API: {}", apiUrl);

      ResponseEntity<Map> response =
          restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        return parseResponse(response.getBody(), llmProperties.getChat().getModel());
      } else {
        throw new RuntimeException("API调用失败，状态码: " + response.getStatusCode());
      }

    } catch (Exception e) {
      log.error("API调用失败", e);
      throw new RuntimeException("AI服务调用失败: " + e.getMessage(), e);
    }
  }

  /** 调用SiliconFlow API进行流式对话 */
  public Flux<StreamResponse> streamChat(String userId, ChatRequest request) {
    String modelType = request.getModelType() != null ? request.getModelType() : llmProperties.getChat().getModel();
    log.info("user: {}, model: {}", userId, modelType);

    Sinks.Many<StreamResponse> sink = Sinks.many().unicast().onBackpressureBuffer();
    String streamId = UUID.randomUUID().toString();

    // 在单独的线程中处理HTTP连接和流式响应
    Schedulers.boundedElastic()
        .schedule(
            () -> {
              HttpURLConnection connection = null;
              BufferedReader reader = null;

              try {
                // 构建请求体
                Map<String, Object> requestBody =
                    buildRequestBody(
                        request,
                        modelType,
                        llmProperties.getChat().getMaxTokens());
                requestBody.put("stream", true); // 启用流式响应

                String requestBodyJson = objectMapper.writeValueAsString(requestBody);
                log.info("Request body: {}", requestBodyJson);
                log.info("API URL: {}", llmProperties.getChat().getBaseUrl() + llmProperties.getChat().getInterfaceUrl());
                log.info("Model: {}", modelType);

                // 创建连接
                String apiUrl =
                    llmProperties.getChat().getBaseUrl()
                        + llmProperties.getChat().getInterfaceUrl();
                log.info("Calling API URL: {} with model: {}", apiUrl, modelType);
                log.info("API Key: {}", llmProperties.getChat().getApiKey() != null ? "Present" : "Missing");
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();

                // 设置请求属性
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty(
                    "Authorization", "Bearer " + llmProperties.getChat().getApiKey());
                connection.setDoOutput(true);
                connection.setConnectTimeout(llmProperties.getChat().getTimeout());
                connection.setReadTimeout(llmProperties.getChat().getTimeout());

                // 发送请求体
                try (var outputStream = connection.getOutputStream()) {
                  byte[] input = requestBodyJson.getBytes(StandardCharsets.UTF_8);
                  outputStream.write(input, 0, input.length);
                }

                // 检查响应状态
                int responseCode = connection.getResponseCode();
                log.info("Response code: {}", responseCode);
                if (responseCode != HttpURLConnection.HTTP_OK) {
                  // 读取错误响应内容
                  String errorResponse = "";
                  try (BufferedReader errorReader = new BufferedReader(
                      new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorBuffer = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                      errorBuffer.append(errorLine);
                    }
                    errorResponse = errorBuffer.toString();
                  } catch (Exception readError) {
                    log.error("Failed to read error response", readError);
                  }
                  log.error("API调用失败，状态码: {}, 错误响应: {}", responseCode, errorResponse);
                  throw new RuntimeException("API调用失败，状态码: " + responseCode + ", 错误: " + errorResponse);
                }

                // 读取流式响应
                reader =
                    new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

                String line;
                StringBuilder buffer = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                  if (line.isEmpty() || line.equals("data: [DONE]")) {
                    continue;
                  }

                  if (line.startsWith("data: ")) {
                    buffer.append(line.substring(6));

                    try {
                      // 尝试解析JSON
                      Map<String, Object> chunk =
                          objectMapper.readValue(buffer.toString(), Map.class);

                      // 提取内容
                      if (chunk.containsKey("choices") && chunk.get("choices") instanceof List) {
                        List<Map<String, Object>> choices =
                            (List<Map<String, Object>>) chunk.get("choices");
                        if (!choices.isEmpty() && choices.get(0).containsKey("delta")) {
                          Map<String, Object> delta =
                              (Map<String, Object>) choices.get(0).get("delta");
                          if (delta.containsKey("content")) {
                            String content = (String) delta.get("content");

                            StreamResponse streamChunk = new StreamResponse();
                            streamChunk.setStreamId(streamId);
                            streamChunk.setModelType(modelType);
                            streamChunk.setContent(content);
                            streamChunk.setFinished(false);

                            sink.tryEmitNext(streamChunk);
                          }
                        }

                        // 检查是否完成
                        if (!choices.isEmpty() && choices.get(0).containsKey("finish_reason")) {
                          String finishReason = (String) choices.get(0).get("finish_reason");
                          if (finishReason != null) {
                            StreamResponse finalChunk = new StreamResponse();
                            finalChunk.setStreamId(streamId);
                            finalChunk.setModelType(modelType);
                            finalChunk.setContent("");
                            finalChunk.setFinished(true);
                            finalChunk.setFinishReason(finishReason);
                            finalChunk.setTokenUsage(estimateTokenUsage(request.getMessage(), ""));

                            sink.tryEmitNext(finalChunk);
                            break;
                          }
                        }
                      }

                      // 清空缓冲区
                      buffer.setLength(0);
                    } catch (Exception e) {
                      // 如果解析失败，继续累积数据
                      log.debug("Failed to parse chunk, continuing: {}", e.getMessage());
                    }
                  }
                }

                sink.tryEmitComplete();
                log.info("API streaming chat completed for user: {}", userId);

              } catch (Exception e) {
                log.error("API streaming call failed", e);
                sink.tryEmitError(new RuntimeException("AI服务流式调用失败: " + e.getMessage(), e));
              } finally {
                if (reader != null) {
                  try {
                    reader.close();
                  } catch (Exception e) {
                  }
                }
                if (connection != null) {
                  connection.disconnect();
                }
              }
            });

    return sink.asFlux();
  }

  /** 调用API生成图片 */
  public ImageGenerationResponse generateImage(String userId, ImageGenerationRequest request) {
    log.info(
        "Calling API for image generation: user={}, model={}",
        userId,
        llmProperties.getTextToImage().getModel());

    try {
      // 构建请求体
      Map<String, Object> requestBody = new HashMap<>();
      requestBody.put("model", llmProperties.getTextToImage().getModel());
      requestBody.put("prompt", request.getPrompt());
      requestBody.put("n", request.getN());
      requestBody.put("size", request.getSize());
      requestBody.put("response_format", request.getResponseFormat());

      if (request.getUser() != null) {
        requestBody.put("user", request.getUser());
      }

      // 设置请求头
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      // 使用用户提供的token
      headers.set("Authorization", "Bearer " + llmProperties.getTextToImage().getApiKey());

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

      // 调用图片生成API
      String apiUrl =
          llmProperties.getTextToImage().getBaseUrl()
              + llmProperties.getTextToImage().getInterfaceUrl();
      log.debug("Calling TextToImage image generation API: {}", apiUrl);

      ResponseEntity<Map> response =
          restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        return parseImageResponse(response.getBody(), llmProperties.getTextToImage().getModel());
      } else {
        throw new RuntimeException("TextToImage image API调用失败，状态码: " + response.getStatusCode());
      }

    } catch (Exception e) {
      log.error("TextToImage image API调用失败", e);
      throw new RuntimeException("图片生成服务调用失败: " + e.getMessage(), e);
    }
  }

  /** 解析图片API响应 */
  private ImageGenerationResponse parseImageResponse(
      Map<String, Object> responseBody, String modelType) {
    ImageGenerationResponse response = new ImageGenerationResponse();
    response.setId(UUID.randomUUID().toString());
    response.setModelType(modelType);
    response.setCreatedAt(LocalDateTime.now());

    try {
      // 解析images数组
      List<Map<String, Object>> images = (List<Map<String, Object>>) responseBody.get("data");
      if (images != null && !images.isEmpty()) {
        List<ImageGenerationResponse.ImageData> imageDataList = new ArrayList<>();

        for (Map<String, Object> image : images) {
          ImageGenerationResponse.ImageData imageData = new ImageGenerationResponse.ImageData();

          if (image.containsKey("url")) {
            imageData.setUrl((String) image.get("url"));
          }

          if (image.containsKey("b64_json")) {
            imageData.setBase64((String) image.get("b64_json"));
          }

          if (image.containsKey("finish_reason")) {
            imageData.setFinishReason((String) image.get("finish_reason"));
          }

          imageDataList.add(imageData);
        }

        response.setImages(imageDataList);
      }

    } catch (Exception e) {
      log.error("解析SiliconFlow图片响应失败", e);
      throw new RuntimeException("图片响应解析失败", e);
    }

    return response;
  }

  /** 估算token使用量 */
  private int estimateTokenUsage(String prompt, String completion) {
    // 简单的token估算逻辑
    int promptTokens = prompt == null ? 0 : prompt.length() / 4;
    int completionTokens = completion == null ? 0 : completion.length() / 4;
    return promptTokens + completionTokens;
  }

  /** 构建请求体 */
  private Map<String, Object> buildRequestBody(
      ChatRequest request, String modelType, int maxTokens) {
    Map<String, Object> requestBody = new HashMap<>();

    // 设置模型
    requestBody.put("model", llmProperties.getChat().getModel());

    // 构建消息列表
    List<Map<String, String>> messages = new ArrayList<>();

    // 添加系统消息（如果有）
    if (request.getSystemPrompt() != null && !request.getSystemPrompt().isEmpty()) {
      Map<String, String> systemMessage = new HashMap<>();
      systemMessage.put("role", "system");
      systemMessage.put("content", request.getSystemPrompt());
      messages.add(systemMessage);
    }

    // 添加用户消息
    Map<String, String> userMessage = new HashMap<>();
    userMessage.put("role", "user");
    userMessage.put("content", request.getMessage());
    messages.add(userMessage);

    requestBody.put("messages", messages);

    // 设置参数
    requestBody.put(
        "max_tokens", request.getMaxTokens() != null ? request.getMaxTokens() : maxTokens);
    requestBody.put(
        "temperature", request.getTemperature() != null ? request.getTemperature() : 0.7);

    if (request.getTopP() != null) {
      requestBody.put("top_p", request.getTopP());
    }
    if (request.getFrequencyPenalty() != null) {
      requestBody.put("frequency_penalty", request.getFrequencyPenalty());
    }
    if (request.getPresencePenalty() != null) {
      requestBody.put("presence_penalty", request.getPresencePenalty());
    }

    // 设置流式响应
    requestBody.put("stream", true);

    return requestBody;
  }

  /** 解析API响应 */
  private ChatResponse parseResponse(Map<String, Object> responseBody, String modelType) {
    ChatResponse response = new ChatResponse();
    response.setId(UUID.randomUUID().toString());
    response.setModelType(modelType);
    response.setCreatedAt(LocalDateTime.now());
    response.setResponseTime(LocalDateTime.now());

    try {
      // 解析choices
      List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
      if (choices != null && !choices.isEmpty()) {
        Map<String, Object> choice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) choice.get("message");

        if (message != null) {
          String content = (String) message.get("content");
          response.setResponse(content);
        }

        String finishReason = (String) choice.get("finish_reason");
        response.setFinishReason(finishReason);
      }

      // 解析usage
      Map<String, Object> usage = (Map<String, Object>) responseBody.get("usage");
      if (usage != null) {
        Integer promptTokens = (Integer) usage.get("prompt_tokens");
        Integer completionTokens = (Integer) usage.get("completion_tokens");
        Integer totalTokens = (Integer) usage.get("total_tokens");

        response.setTokenUsage(totalTokens != null ? totalTokens : 0);
        response.setPromptTokens(promptTokens);
        response.setCompletionTokens(completionTokens);
      }

    } catch (Exception e) {
      log.error("解析响应失败", e);
      throw new RuntimeException("AI响应解析失败", e);
    }

    return response;
  }
}
