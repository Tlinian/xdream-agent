package org.example;

import com.xdream.agent.ReActAgent;
import com.xdream.context.AgentContext;
import com.xdream.tools.ToolDescription;
import com.xdream.tools.ToolUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 主类，程序入口
 */
public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, JsonProcessingException {
        
        // 测试ToolUtil获取所有工具描述
        System.out.println("===== 测试获取所有工具描述 ====");
        ToolUtil toolUtil = new ToolUtil(); // 由于ToolUtil有@AllArgsConstructor，需要提供构造参数
        List<ToolDescription> toolDescriptions = toolUtil.getAllToolDescriptions();
        
        if (toolDescriptions.isEmpty()) {
            System.out.println("没有找到任何ToolInterface的实现类");
        } else {
            System.out.println("找到" + toolDescriptions.size() + "个工具:");
            for (ToolDescription description : toolDescriptions) {
                System.out.println("名称: " + description.getName() + ", 描述: " + description.getDescription());
            }
        }
        System.out.println("==============================");

        // 加载并打印配置信息
        AgentContext context = new AgentContext("现在是几月记号" +
                "", false);
        ReActAgent reactAgent = new ReActAgent();
        String run = reactAgent.run(context);
        System.out.printf("结束，运行结果：%s", run);

//        System.out.println(response);
    }
}