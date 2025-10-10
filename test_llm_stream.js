// 测试后端LLM流式API的脚本
const http = require('http');

// 构建请求选项 - 使用非DeepSeek模型以触发模拟响应
const options = {
  hostname: 'localhost',
  port: 8080,
  path: '/api/llm/chat/stream?message=' + encodeURIComponent('你好，请介绍一下自己') + '&modelType=deepseek-ai/DeepSeek-V3&temperature=0.7&maxTokens=2048&userId=test-user-001',
  method: 'GET',
  headers: {
    'X-User-Id': 'test-user-001',
    'Accept': 'text/event-stream'
  }
};

console.log('发送请求到:', options.path);

// 发送请求
const req = http.request(options, (res) => {
  console.log(`状态码: ${res.statusCode}`);
  console.log('响应头:', res.headers);
  
  // 监听数据流
  res.on('data', (chunk) => {
    console.log(`收到数据: ${chunk}`);
  });
  
  res.on('end', () => {
    console.log('响应结束');
  });
});

// 监听错误
req.on('error', (e) => {
  console.error(`请求错误: ${e.message}`);
});

// 结束请求
req.end();