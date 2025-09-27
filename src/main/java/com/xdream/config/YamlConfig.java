package com.xdream.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * YAML配置加载类
 * 用于加载application.yml中的配置项
 */
public class YamlConfig {
    // 配置键静态常量
    public static final String CONFIG_LLM = "llm";
    public static final String CONFIG_DEFAULT = "default";
    public static final String CONFIG_LLM_DEFAULT = CONFIG_LLM + "." + CONFIG_DEFAULT;
    public static final String CONFIG_PROMPTS = "prompts";
    public static final String CONFIG_PROMPTS_DEFAULT = CONFIG_PROMPTS + "." + CONFIG_DEFAULT;
    public static final String CONFIG_SYSTEM_PROMPT = "system_prompt";
    public static final String CONFIG_BASE_URL = "base_url";
    public static final String CONFIG_API_KEY = "apikey";
    public static final String CONFIG_INTERFACE_URL = "interface_url";
    public static final String CONFIG_MODEL = "model";
    public static final String CONFIG_MAX_TOKENS = "max_tokens";
    
    private static final Logger logger = Logger.getLogger(YamlConfig.class.getName());
    private static final Yaml yaml = new Yaml();
    private static Map<String, Object> configMap;

    static {
        // 静态初始化块，加载application.yml配置
        try {
            logger.info("开始加载application.yml配置文件");
            
            // 获取资源路径
            String resourcePath = "application.yml";
            logger.info("尝试加载资源: " + resourcePath);
            
            // 使用类加载器获取资源
            ClassLoader classLoader = YamlConfig.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
            
            if (inputStream != null) {
                logger.info("成功找到application.yml配置文件");
                configMap = yaml.load(inputStream);
                logger.info("配置文件加载成功，配置项数量: " + (configMap != null ? configMap.size() : 0));
                
                // 打印配置内容的概要信息（不打印敏感信息）
                if (configMap != null && !configMap.isEmpty()) {
                    logger.info("配置文件包含的顶级键: " + String.join(", ", configMap.keySet()));
                    
                    // 打印configMap的类型
                    logger.info("configMap的类型: " + configMap.getClass().getName());
                    
                    // 打印default配置项的类型和值
                    for (String key : configMap.keySet()) {
                        Object value = configMap.get(key);
                        logger.info("配置项[" + key + "]的类型: " + (value != null ? value.getClass().getName() : "null"));
                        logger.info("配置项[" + key + "]的值: " + (value instanceof String && ((String)value).contains("sk-") ? "[API密钥已隐藏]" : value));
                    }
                }
                
                inputStream.close();
            } else {
                logger.warning("无法找到application.yml配置文件");
                // 检查资源路径是否正确
                java.net.URL url = classLoader.getResource(".");
                if (url != null) {
                    logger.info("当前类加载器的资源根路径: " + url.getPath());
                }
                configMap = Map.of();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "加载application.yml配置文件时出错", e);
            configMap = Map.of();
        }
    }

    /**
     * 获取配置项
     * @param key 配置键，支持点号分隔的嵌套路径，如"default.base_url"
     * @param defaultValue 默认值
     * @param <T> 返回值类型
     * @return 配置值或默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, T defaultValue) {
        try {
            if (key == null || key.isEmpty()) {
                logger.warning("获取配置时key为空");
                return defaultValue;
            }

            logger.info("尝试获取配置项: " + key);
            
            // 检查configMap是否为空
            if (configMap == null || configMap.isEmpty()) {
                logger.warning("配置映射为空，无法获取配置项: " + key);
                return defaultValue;
            }

            String[] keys = key.split("\\.");
            Map<String, Object> currentMap = configMap;
            Object value = null;

            for (int i = 0; i < keys.length; i++) {
                String currentKey = keys[i];
                logger.fine("当前处理键: " + currentKey);
                
                if (i == keys.length - 1) {
                    value = currentMap.get(currentKey);
                    logger.info("配置项[" + key + "]的值: " + (value instanceof String && ((String)value).contains("sk-") ? "[API密钥已隐藏]" : value));
                } else {
                    Object next = currentMap.get(currentKey);
                    if (next instanceof Map) {
                        currentMap = (Map<String, Object>) next;
                        logger.fine("进入嵌套配置: " + currentKey);
                    } else {
                        logger.warning("配置项[" + currentKey + "]不是嵌套映射，无法继续解析: " + key);
                        return defaultValue;
                    }
                }
            }

            return value != null ? (T) value : defaultValue;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "获取配置项[" + key + "]时出错", e);
            return defaultValue;
        }
    }

    /**
     * 获取配置项，不提供默认值
     * @param key 配置键
     * @param <T> 返回值类型
     * @return 配置值或null
     */
    public static <T> T get(String key) {
        return get(key, null);
    }

    /**
     * 获取默认配置项
     * @param subKey 子配置键
     * @param defaultValue 默认值
     * @param <T> 返回值类型
     * @return 配置值或默认值
     */
    public static <T> T getLlmDefault(String subKey, T defaultValue) {
        // 使用静态常量构建配置路径
        return get(CONFIG_LLM_DEFAULT + "." + subKey, defaultValue);
    }

    /**
     * 获取Prompts配置项
     * @param subKey 子配置键
     * @param defaultValue 默认值
     * @param <T> 返回值类型
     * @return 配置值或默认值
     */
    public static <T> T getPrompts(String subKey, T defaultValue) {
        return get(CONFIG_PROMPTS_DEFAULT + "." + subKey, defaultValue);
    }

    public static <T> T getPrompts(String subKey) {
        return getPrompts(subKey, null);
    }

    /**
     * 获取Prompts配置项
     * @param subKey 子配置键
     * @param <T> 返回值类型
     * @return 配置值或null
     */
    public static <T> T getLlmDefault(String subKey) {
        return getLlmDefault(subKey, null);
    }
    
    /**
     * 获取LLM配置项
     * @param subKey 子配置键
     * @param defaultValue 默认值
     * @param <T> 返回值类型
     * @return 配置值或默认值
     */
    public static <T> T getLlmConfig(String subKey, T defaultValue) {
        return get(CONFIG_LLM + "." + subKey, defaultValue);
    }
    
    /**
     * 获取LLM配置项
     * @param subKey 子配置键
     * @param <T> 返回值类型
     * @return 配置值或null
     */
    public static <T> T getLlmConfig(String subKey) {
        return getLlmConfig(subKey, null);
    }
}