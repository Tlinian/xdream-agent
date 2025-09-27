package com.xdream.agent;

import com.xdream.config.YamlConfig;

/**
 * 配置演示类
 * 用于展示如何使用YamlConfig类加载配置
 */
public class ConfigDemo {

    /**
     * 打印所有配置信息
     */
    public static void printAllConfigs() {
        System.out.println("===== 配置信息 =====");

        // 使用getDefault方法获取默认配置
        String baseUrl = YamlConfig.getLlmDefault("base_url", "未设置");
        String apiKey = YamlConfig.getLlmDefault("apikey", "未设置");
        String interfaceUrl = YamlConfig.getLlmDefault("interface_url", "未设置");
        String model = YamlConfig.getLlmDefault("model", "未设置");
        Integer maxTokens = YamlConfig.getLlmDefault("max_tokens", 0);
        
        System.out.println("Base URL: " + baseUrl);
        System.out.println("API Key: " + maskApiKey(apiKey));
        System.out.println("Interface URL: " + interfaceUrl);
        System.out.println("Model: " + model);
        System.out.println("Max Tokens: " + maxTokens);
        
        System.out.println("====================");
    }
    
    /**
     * 模糊处理API密钥，保护敏感信息
     */
    private static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return apiKey;
        }
        return apiKey.substring(0, 4) + "********" + apiKey.substring(apiKey.length() - 4);
    }
}