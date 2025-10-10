const http = require('http');

const postData = JSON.stringify({
  message: '你是',
  modelType: 'deepseek-ai/DeepSeek-V3',
  temperature: 0.7,
  maxTokens: 256
});

const options = {
  hostname: 'localhost',
  port: 8085,
  path: '/api/test/chat/simple',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': Buffer.byteLength(postData)
  }
};

const req = http.request(options, (res) => {
  console.log(`状态码: ${res.statusCode}`);
  console.log(`响应头: ${JSON.stringify(res.headers)}`);
  
  res.on('data', (chunk) => {
    console.log(`响应体: ${chunk}`);
  });
  
  res.on('end', () => {
    console.log('响应已完成');
  });
});

req.on('error', (e) => {
  console.error(`请求错误: ${e.message}`);
});

// 发送请求体
req.write(postData);
req.end();