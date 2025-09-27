package com.xdream.context;

import com.xdream.config.YamlConfig;

public class LlmContext {
    // LLM配置上下文
    public static final String LLM_CONFIG_BASE_URL = YamlConfig.CONFIG_LLM_DEFAULT + "." + YamlConfig.CONFIG_BASE_URL;
    public static final String LLM_CONFIG_API_KEY = YamlConfig.CONFIG_LLM_DEFAULT + "." + YamlConfig.CONFIG_API_KEY;
    public static final String LLM_CONFIG_INTERFACE_URL = YamlConfig.CONFIG_LLM_DEFAULT + "." + YamlConfig.CONFIG_INTERFACE_URL;
    public static final String LLM_CONFIG_MODEL = YamlConfig.CONFIG_LLM_DEFAULT + "." + YamlConfig.CONFIG_MODEL;
    public static final String LLM_CONFIG_MAX_TOKENS = YamlConfig.CONFIG_LLM_DEFAULT + "." + YamlConfig.CONFIG_MAX_TOKENS;

    // LLM配置属性，将下面都变成final
    private final String baseUrl;
    private final String apiKey;
    private final String interfaceUrl;
    private final String model;
    private final Integer maxTokens;

    // 生成构造方法，初始化LLM配置
    public LlmContext() {
        // 初始化LLM配置
        baseUrl = YamlConfig.getLlmDefault(YamlConfig.CONFIG_BASE_URL);
        apiKey = YamlConfig.getLlmDefault(YamlConfig.CONFIG_API_KEY);
        interfaceUrl = YamlConfig.getLlmDefault(YamlConfig.CONFIG_INTERFACE_URL);
        model = YamlConfig.getLlmDefault(YamlConfig.CONFIG_MODEL);
        maxTokens = YamlConfig.getLlmDefault(YamlConfig.CONFIG_MAX_TOKENS);
    }

    // 生成getter方法，获取LLM配置属性
    public String getBaseUrl() {
        return baseUrl;
    }
    public String getApiKey() {
        return apiKey;
    }
    public String getInterfaceUrl() {
        return interfaceUrl;
    }
    public String getModel() {
        return model;
    }
    public Integer getMaxTokens() {
        return maxTokens;
    }
}
