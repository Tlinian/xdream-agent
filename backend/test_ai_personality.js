const http = require('http');

// 构建请求数据
const postData = JSON.stringify({
  message: '你是',
  modelType: 'default'
});

// 请求选项
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

// 发送请求
const req = http.request(options, (res) => {
  console.log(`状态码: ${res.statusCode}`);
  
  let responseData = '';
  res.on('data', (chunk) => {
    responseData += chunk;
  });
  
  res.on('end', () => {
    try {
      const data = JSON.parse(responseData);
      console.log('响应数据:');
      console.log(JSON.stringify(data, null, 2));
    } catch (e) {
      console.log('响应数据(非JSON):', responseData);
    }
  });
});

req.on('error', (e) => {
  console.error(`请求错误: ${e.message}`);
});

// 发送请求体
req.write(postData);
req.end();