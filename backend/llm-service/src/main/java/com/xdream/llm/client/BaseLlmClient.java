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

  /** 璋冪敤SiliconFlow API杩涜闈炴祦寮忓璇?*/
  public ChatResponse chat(String userId, ChatRequest request) {
    // TODO锛氱洰鍓嶄粎鏀寔浠庨厤缃枃浠惰鍙栨ā鍨嬶紝鍚庣画寮勬垚浠庤姹傚弬鏁颁腑璇诲彇
    log.info("user: {}, model: {}", userId, llmProperties.getChat().getModel());

    try {
      // 鏋勫缓璇锋眰浣?
      Map<String, Object> requestBody =
          buildRequestBody(
              request,
              llmProperties.getChat().getModel(),
              llmProperties.getChat().getMaxTokens(),
              false);

      // 璁剧疆璇锋眰澶?
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("Authorization", "Bearer " + llmProperties.getChat().getApiKey());

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

      // 璋冪敤SiliconFlow API
      String apiUrl =
          llmProperties.getChat().getBaseUrl() + llmProperties.getChat().getInterfaceUrl();
      log.debug("API: {}", apiUrl);

      ResponseEntity<Map> response =
          restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        return parseResponse(response.getBody(), llmProperties.getChat().getModel());
      } else {
        throw new RuntimeException("API璋冪敤澶辫触锛岀姸鎬佺爜: " + response.getStatusCode());
      }

    } catch (Exception e) {
      log.error("API璋冪敤澶辫触", e);
      throw new RuntimeException("AI鏈嶅姟璋冪敤澶辫触: " + e.getMessage(), e);
    }
  }

  /** 璋冪敤SiliconFlow API杩涜娴佸紡瀵硅瘽 */
  public Flux<StreamResponse> streamChat(String userId, ChatRequest request) {
    String modelType = llmProperties.getChat().getModel();
    log.info("user: {}, model: {}", userId, modelType);

    Sinks.Many<StreamResponse> sink = Sinks.many().unicast().onBackpressureBuffer();
    String streamId = UUID.randomUUID().toString();

    // 鍦ㄥ崟鐙殑绾跨▼涓鐞咹TTP杩炴帴鍜屾祦寮忓搷搴?
    Schedulers.boundedElastic()
        .schedule(
            () -> {
              HttpURLConnection connection = null;
              BufferedReader reader = null;

              try {
                // 鏋勫缓璇锋眰浣?
                Map<String, Object> requestBody =
                    buildRequestBody(
                        request,
                        modelType,
                        llmProperties.getChat().getMaxTokens(),
                        true);

                String requestBodyJson = objectMapper.writeValueAsString(requestBody);
                log.info("Request body: {}", requestBodyJson);
                log.info("API URL: {}", llmProperties.getChat().getBaseUrl() + llmProperties.getChat().getInterfaceUrl());
                log.info("Model: {}", modelType);

                // 鍒涘缓杩炴帴
                String apiUrl =
                    llmProperties.getChat().getBaseUrl()
                        + llmProperties.getChat().getInterfaceUrl();
                log.info("Calling API URL: {} with model: {}", apiUrl, modelType);
                log.info("API Key: {}", llmProperties.getChat().getApiKey() != null ? "Present" : "Missing");
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();

                // 璁剧疆璇锋眰灞炴€?
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty(
                    "Authorization", "Bearer " + llmProperties.getChat().getApiKey());
                connection.setDoOutput(true);
                connection.setConnectTimeout(llmProperties.getChat().getTimeout());
                connection.setReadTimeout(llmProperties.getChat().getTimeout());

                // 鍙戦€佽姹備綋
                try (var outputStream = connection.getOutputStream()) {
                  byte[] input = requestBodyJson.getBytes(StandardCharsets.UTF_8);
                  outputStream.write(input, 0, input.length);
                }

                // 妫€鏌ュ搷搴旂姸鎬?
                int responseCode = connection.getResponseCode();
                log.info("Response code: {}", responseCode);
                if (responseCode != HttpURLConnection.HTTP_OK) {
                  // 璇诲彇閿欒鍝嶅簲鍐呭
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
                  log.error("API璋冪敤澶辫触锛岀姸鎬佺爜: {}, 閿欒鍝嶅簲: {}", responseCode, errorResponse);
                  throw new RuntimeException("API璋冪敤澶辫触锛岀姸鎬佺爜: " + responseCode + ", 閿欒: " + errorResponse);
                }

                // 璇诲彇娴佸紡鍝嶅簲
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
                      // 灏濊瘯瑙ｆ瀽JSON
                      Map<String, Object> chunk =
                          objectMapper.readValue(buffer.toString(), Map.class);

                      // 鎻愬彇鍐呭
                      if (chunk.containsKey("choices") && chunk.get("choices") instanceof List) {
                        List<Map<String, Object>> choices =
                            (List<Map<String, Object>>) chunk.get("choices");
                        if (!choices.isEmpty() && choices.get(0).containsKey("delta")) {
                          Map<String, Object> delta =
                              (Map<String, Object>) choices.get(0).get("delta");
                          if (delta.containsKey("content")) {
                            String content = (String) delta.get("content");

                            StreamResponse streamChunk = StreamResponse.builder()
                                    .streamId(streamId)
                                    .modelType(modelType)
                                    .content(content)
                                    .finished(false)
                                    .build();
                            sink.tryEmitNext(streamChunk);
                          }
                        }

                        // 妫€鏌ユ槸鍚﹀畬鎴?
                        if (!choices.isEmpty() && choices.get(0).containsKey("finish_reason")) {
                          String finishReason = (String) choices.get(0).get("finish_reason");
                          if (finishReason != null) {
                            StreamResponse finalChunk = StreamResponse.builder()
                                    .streamId(streamId)
                                    .modelType(modelType)
                                    .content("")
                                    .finished(true)
                                    .finishReason(finishReason)
                                    .tokenUsage(estimateTokenUsage(request.getMessage(), ""))
                                    .build();
                            sink.tryEmitNext(finalChunk);
                            break;
                          }
                        }
                      }

                      // 娓呯┖缂撳啿鍖?
                      buffer.setLength(0);
                    } catch (Exception e) {
                      // 濡傛灉瑙ｆ瀽澶辫触锛岀户缁疮绉暟鎹?
                      log.debug("Failed to parse chunk, continuing: {}", e.getMessage());
                    }
                  }
                }

                sink.tryEmitComplete();
                log.info("API streaming chat completed for user: {}", userId);

              } catch (Exception e) {
                log.error("API streaming call failed", e);
                sink.tryEmitError(new RuntimeException("AI鏈嶅姟娴佸紡璋冪敤澶辫触: " + e.getMessage(), e));
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

  /** 璋冪敤API鐢熸垚鍥剧墖 */
  public ImageGenerationResponse generateImage(String userId, ImageGenerationRequest request) {
    log.info(
        "Calling API for image generation: user={}, model={}",
        userId,
        llmProperties.getTextToImage().getModel());

    try {
      // 鏋勫缓璇锋眰浣?
      Map<String, Object> requestBody = new HashMap<>();
      requestBody.put("model", llmProperties.getTextToImage().getModel());
      requestBody.put("prompt", request.getPrompt());
      requestBody.put("n", request.getN());
      requestBody.put("size", request.getSize());
      requestBody.put("response_format", request.getResponseFormat());

      if (request.getUser() != null) {
        requestBody.put("user", request.getUser());
      }

      // 璁剧疆璇锋眰澶?
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      // 浣跨敤鐢ㄦ埛鎻愪緵鐨則oken
      headers.set("Authorization", "Bearer " + llmProperties.getTextToImage().getApiKey());

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

      // 璋冪敤鍥剧墖鐢熸垚API
      String apiUrl =
          llmProperties.getTextToImage().getBaseUrl()
              + llmProperties.getTextToImage().getInterfaceUrl();
      log.debug("Calling TextToImage image generation API: {}", apiUrl);

      ResponseEntity<Map> response =
          restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        return parseImageResponse(response.getBody(), llmProperties.getTextToImage().getModel());
      } else {
        throw new RuntimeException("TextToImage image API璋冪敤澶辫触锛岀姸鎬佺爜: " + response.getStatusCode());
      }

    } catch (Exception e) {
      log.error("TextToImage image API璋冪敤澶辫触", e);
      throw new RuntimeException("鍥剧墖鐢熸垚鏈嶅姟璋冪敤澶辫触: " + e.getMessage(), e);
    }
  }

  /** 瑙ｆ瀽鍥剧墖API鍝嶅簲 */
  private ImageGenerationResponse parseImageResponse(
      Map<String, Object> responseBody, String modelType) {
    ImageGenerationResponse response = new ImageGenerationResponse();
    response.setId(UUID.randomUUID().toString());
    response.setModelType(modelType);
    response.setCreatedAt(LocalDateTime.now());

    try {
      // 瑙ｆ瀽images鏁扮粍
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
      log.error("瑙ｆ瀽SiliconFlow鍥剧墖鍝嶅簲澶辫触", e);
      throw new RuntimeException("鍥剧墖鍝嶅簲瑙ｆ瀽澶辫触", e);
    }

    return response;
  }

  /** 浼扮畻token浣跨敤閲?*/
  private int estimateTokenUsage(String prompt, String completion) {
    // 绠€鍗曠殑token浼扮畻閫昏緫
    int promptTokens = prompt == null ? 0 : prompt.length() / 4;
    int completionTokens = completion == null ? 0 : completion.length() / 4;
    return promptTokens + completionTokens;
  }

  /** 鏋勫缓璇锋眰浣?*/
  private Map<String, Object> buildRequestBody(
      ChatRequest request, String modelType, int maxTokens, boolean stream) {
    Map<String, Object> requestBody = new HashMap<>();

    // 璁剧疆妯″瀷
    requestBody.put("model", modelType != null ? modelType : llmProperties.getChat().getModel());

    // 鏋勫缓娑堟伅鍒楄〃
    List<Map<String, String>> messages = new ArrayList<>();

    // 娣诲姞绯荤粺娑堟伅锛堝鏋滄湁锛?
    if (request.getSystemPrompt() != null && !request.getSystemPrompt().isEmpty()) {
      Map<String, String> systemMessage = new HashMap<>();
      systemMessage.put("role", "system");
      systemMessage.put("content", request.getSystemPrompt());
      messages.add(systemMessage);
    }

    // 娣诲姞鐢ㄦ埛娑堟伅
    Map<String, String> userMessage = new HashMap<>();
    userMessage.put("role", "user");
    userMessage.put("content", request.getMessage());
    messages.add(userMessage);

    requestBody.put("messages", messages);

    // 璁剧疆鍙傛暟
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

    // 璁剧疆娴佸紡鍝嶅簲
    requestBody.put("stream", stream);

    return requestBody;
  }

  /** 瑙ｆ瀽API鍝嶅簲 */
  private ChatResponse parseResponse(Map<String, Object> responseBody, String modelType) {
    ChatResponse response = new ChatResponse();
    response.setId(UUID.randomUUID().toString());
    response.setModelType(modelType);
    response.setCreatedAt(LocalDateTime.now());
    response.setResponseTime(LocalDateTime.now());

    try {
      // 瑙ｆ瀽choices
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

      // 瑙ｆ瀽usage
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
      log.error("瑙ｆ瀽鍝嶅簲澶辫触", e);
      throw new RuntimeException("AI鍝嶅簲瑙ｆ瀽澶辫触", e);
    }

    return response;
  
  public EmbeddingResponse generateEmbeddingsFromApi(String userId, EmbeddingRequest request) {
    LlmProperties.EmbeddingProperties config = llmProperties.getEmbedding();
    String model = request.getModelType() != null ? request.getModelType() : config.getModel();
    Map<String, Object> body = new HashMap<>();
    body.put("model", model);
    body.put("input", request.getText());
    if (request.getUser() != null) {
      body.put("user", request.getUser());
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + config.getApiKey());

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
    String url = config.getBaseUrl() + config.getInterfaceUrl();
    try {
      ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
      if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
        throw new RuntimeException("Embedding api call failed, status: " + response.getStatusCode());
      }
      return parseEmbeddingResponse(response.getBody(), model);
    } catch (Exception ex) {
      log.error("Failed to call embedding api: {}", ex.getMessage(), ex);
      throw new RuntimeException("Embedding service unavailable: " + ex.getMessage(), ex);
    }
  }

  public RerankResponse rerank(String userId, RerankRequest request) {
    LlmProperties.RerankProperties config = llmProperties.getRerank();
    if (config == null) {
      throw new RuntimeException("Rerank model not configured");
    }
    String model = request.getModelType() != null ? request.getModelType() : config.getModel();
    Map<String, Object> body = new HashMap<>();
    body.put("model", model);
    body.put("query", request.getQuery());
    body.put("documents", request.getDocuments());
    if (request.getTopK() != null) {
      body.put("top_n", request.getTopK());
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + config.getApiKey());

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
    String url = config.getBaseUrl() + config.getInterfaceUrl();
    try {
      ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
      if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
        throw new RuntimeException("Rerank api call failed, status: " + response.getStatusCode());
      }
      return parseRerankResponse(response.getBody());
    } catch (Exception ex) {
      log.error("Failed to call rerank api: {}", ex.getMessage(), ex);
      throw new RuntimeException("Rerank service unavailable: " + ex.getMessage(), ex);
    }
  }

  private EmbeddingResponse parseEmbeddingResponse(Map<String, Object> body, String model) {
    EmbeddingResponse response = new EmbeddingResponse();
    response.setId(UUID.randomUUID().toString());
    response.setModelType(model);
    response.setCreatedAt(LocalDateTime.now());

    Object dataObj = body.get("data");
    if (dataObj instanceof List<?> dataList && !dataList.isEmpty()) {
      Object first = dataList.get(0);
      if (first instanceof Map<?, ?> map) {
        Object embeddingObj = map.get("embedding");
        if (embeddingObj instanceof List<?> embeddingList) {
          List<Float> embedding = new ArrayList<>();
          for (Object value : embeddingList) {
            if (value instanceof Number number) {
              embedding.add(number.floatValue());
            }
          }
          response.setEmbedding(embedding);
          response.setDimensions(embedding.size());
        }
      }
    }

    Object usageObj = body.get("usage");
    if (usageObj instanceof Map<?, ?> usageMap) {
      Object totalTokens = usageMap.get("total_tokens");
      if (totalTokens instanceof Number number) {
        response.setTokenUsage(number.intValue());
      }
    }
    if (response.getEmbedding() == null) {
      throw new RuntimeException("Embedding vector missing in response");
    }
    return response;
  }

  private RerankResponse parseRerankResponse(Map<String, Object> body) {
    RerankResponse response = new RerankResponse();
    Object dataObj = body.get("data");
    if (dataObj instanceof List<?> dataList) {
      List<RerankResponse.Item> items = new ArrayList<>();
      for (Object element : dataList) {
        if (element instanceof Map<?, ?> map) {
          RerankResponse.Item item = new RerankResponse.Item();
          Object indexObj = map.get("index");
          Object scoreObj = map.get("score");
          if (indexObj instanceof Number number) {
            item.setIndex(number.intValue());
          }
          if (scoreObj instanceof Number number) {
            item.setScore(number.doubleValue());
          }
          items.add(item);
        }
      }
      response.setResults(items);
    }
    return response;
  }

}