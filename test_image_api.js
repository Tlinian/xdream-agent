const http = require('http');

// API请求选项
const options = {
  hostname: 'localhost',
  port: 8084,
  path: '/api/llm/images/generate',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'X-User-Id': 'test-user'
  }
};

// 请求体数据
const postData = JSON.stringify({
  prompt: '测试图像生成',
  size: '512x512',
  n: 1,
  responseFormat: 'url',
  model: 'Qwen/Qwen-Image',
  user: 'test-user'
});

// 发送请求
const req = http.request(options, (res) => {
  console.log(`状态码: ${res.statusCode}`);
  console.log(`响应头: ${JSON.stringify(res.headers)}`);
  
  let responseBody = '';
  
  res.on('data', (chunk) => {
    responseBody += chunk;
  });
  
  res.on('end', () => {
    console.log('响应体:');
    try {
      const parsedBody = JSON.parse(responseBody);
      console.log(JSON.stringify(parsedBody, null, 2));
    } catch (e) {
      console.log(responseBody);
    }
  });
});

req.on('error', (e) => {
  console.error(`请求错误: ${e.message}`);
});

// 写入请求体并结束请求
req.write(postData);
req.end();