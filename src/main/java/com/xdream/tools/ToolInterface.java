package com.xdream.tools;

import com.alibaba.fastjson.JSON;

import java.util.*;

public interface ToolInterface {
    String getToolName();

    String run(List<Object> param);

    ToolDescription toolDescription();

    default String addFunctionNameParam(Map<String, Object> params) {
        Map<String, Object> functionParams = new HashMap<>();
        functionParams.put("name", getToolName());
        functionParams.put("description", toolDescription().getDescription());
        functionParams.put("parameters", addFunctionNameParam(params, getToolName()));
        return JSON.toJSONString(functionParams);
    }

    default Map<String, Object> addFunctionNameParam(Map<String, Object> parameters, String toolName) {
        Map<String, Object> newParameters = new HashMap<>();
        // 请求的入参名称
        ArrayList<String> newRequired = new ArrayList<>();
//        -.add("function_name");
        if (parameters.containsKey("required") && Objects.nonNull(parameters.get("required"))) {
            newRequired.addAll((List<String>) parameters.get("required"));
        }
        newParameters.put("required", newRequired);

        // properties
        Map<String, Object> newProperties = new HashMap<>();
        Map<String, Object> functionNameMap = new HashMap<>();
        functionNameMap.put("description", "默认值为工具名: " + toolName);
        functionNameMap.put("type", "string");
        newProperties.put("function_name", functionNameMap);
        if (parameters.containsKey("properties") && Objects.nonNull(parameters.get("properties"))) {
            newProperties.putAll((Map<String, Object>) parameters.get("properties"));
        }
        newParameters.put("properties", newProperties);
        return newParameters;
    }
}
