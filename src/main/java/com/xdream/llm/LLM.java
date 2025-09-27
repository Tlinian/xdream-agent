package com.xdream.llm;

import com.xdream.context.LlmContext;
import com.xdream.respone.LlmChoices;
import com.xdream.respone.LlmRespone;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LLM {
    // 添加LLM配置上下文
    private final LlmContext llmContext;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 生成构造方法，初始化LLM配置上下文
    public LLM() {
        this.llmContext = new LlmContext();
    }

    // respone方法
    public LlmRespone response(LlmMessages llmMessages) throws ExecutionException, InterruptedException, JsonProcessingException {
        CompletableFuture<LlmRespone> future = new CompletableFuture<>();
        Map<String, Object> params = new HashMap<>();
        params.put("model", llmContext.getModel());
        params.put("max_tokens", llmContext.getMaxTokens());
        params.put("messages", llmMessages.getMessages());
        params.put("temperature", 0.0);
        params.put("stream", true);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                objectMapper.writeValueAsString(params)
        );
        Request.Builder requestBuilder = new Request.Builder()
                .url(llmContext.getBaseUrl() + llmContext.getInterfaceUrl())
                .post(body);
        requestBuilder.addHeader("Authorization", "Bearer " + llmContext.getApiKey());
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try  (ResponseBody responseBody = response.body()){
                    if (response.isSuccessful()) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(responseBody.byteStream())
                        );
                        String line;
                        boolean isFirstToken = true;
                        StringBuilder sb = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data: ")) {
                                String data = line.substring(6);
                                if (data.equals("[DONE]")) {
                                    break;
                                }
                                if (isFirstToken) {
                                    isFirstToken = false;
                                }
                                JsonNode chunk = objectMapper.readTree(data);
                                JsonNode choices = chunk.get("choices");

                                if (choices != null) {
                                    LlmChoices llmChoices = objectMapper.convertValue(choices.get(0), LlmChoices.class);
                                    if (llmChoices.getDelta() != null) {
                                        sb.append(llmChoices.getDelta().get("content"));
                                    }
                                }
                            }

                        }

                        future.complete(LlmRespone.builder().respone(sb.toString()).build());
                    } else {
                        future.completeExceptionally(new IOException("Unexpected code " + response));
                    }
                } catch (IOException e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future.get();
    }
}
