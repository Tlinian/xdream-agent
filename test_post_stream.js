const http = require('http');

// 构建请求参数
const requestBody = JSON.stringify({
  message: 'hello',
  modelType: 'deepseek-ai/DeepSeek-V3',
  temperature: 0.7,
  maxTokens: 2048,
  useReAct: false
});

// 配置请求选项
const options = {
  hostname: 'localhost',
  port: 8084,
  path: '/api/llm/chat/stream',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': Buffer.byteLength(requestBody),
    'X-User-Id': 'test-user-001'
  }
};

console.log('发送POST请求到后端API...');
console.log('请求路径:', options.path);
console.log('请求体:', requestBody);

// 发送请求
const req = http.request(options, (res) => {
  console.log(`状态码: ${res.statusCode}`);
  console.log('响应头:', res.headers);
  
  // 处理响应流
  res.on('data', (chunk) => {
    console.log('收到数据块:', chunk.toString());
  });
  
  res.on('end', () => {
    console.log('响应结束');
  });
});

// 处理错误
req.on('error', (e) => {
  console.error(`请求错误: ${e.message}`);
});

// 发送请求体
req.write(requestBody);
req.end();