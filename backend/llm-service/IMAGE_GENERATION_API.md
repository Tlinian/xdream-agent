# 图片生成API使用指南

本文档介绍了xdream-agent项目中图片生成模块的API使用方法。

## 功能概述

图片生成模块允许用户通过提供文本描述（prompt）来生成相应的图片。目前支持的模型为`Qwen/Qwen-Image-Edit-2509`，该模型由通义千问提供，通过SiliconFlow API进行调用。

## API端点

```
POST /api/llm/images/generate
```

## 请求头

- `X-User-Id`: 用户标识（必填）
- `Content-Type`: application/json

## 请求体参数

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| model | String | 否 | Qwen/Qwen-Image-Edit-2509 | 图片生成模型 |
| prompt | String | 是 | 无 | 图片描述文本 |
| n | Integer | 否 | 1 | 生成的图片数量 |
| size | String | 否 | 1024x1024 | 图片尺寸 |
| response_format | String | 否 | url | 响应格式，可选值：url, base64 |
| user | String | 否 | 无 | 用户标识，用于追踪请求 |

## 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "string",
    "modelType": "string",
    "createdAt": "2024-07-14T10:30:00",
    "images": [
      {
        "url": "string",
        "base64": "string",
        "finishReason": "string"
      }
    ]
  }
}
```

## 使用示例

### curl命令示例

```bash
curl --request POST \
  --url http://localhost:8080/api/llm/images/generate \
  --header 'Authorization: Bearer <jwt-token>' \
  --header 'Content-Type: application/json' \
  --header 'X-User-Id: user-001' \
  --data '{
  "model": "Qwen/Qwen-Image-Edit-2509",
  "prompt": "an island near sea, with seagulls, moon shining over the sea, light house, boats int he background, fish flying over the sea",
  "n": 1,
  "size": "1024x1024",
  "response_format": "url"
}'
```

### JavaScript示例

```javascript
const axios = require('axios');

async function generateImage() {
  try {
    const response = await axios.post(
      'http://localhost:8080/api/llm/images/generate',
      {
        model: 'Qwen/Qwen-Image-Edit-2509',
        prompt: 'an island near sea, with seagulls, moon shining over the sea, light house, boats int he background, fish flying over the sea',
        n: 1,
        size: '1024x1024',
        response_format: 'url'
      },
      {
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': 'user-001',
          'Authorization': 'Bearer <jwt-token>'
        }
      }
    );
    
    console.log('图片生成成功:', response.data);
    // 处理返回的图片URL或base64数据
  } catch (error) {
    console.error('图片生成失败:', error);
  }
}

// 调用函数
generateImage();
```

## 注意事项

1. 图片生成可能需要一定的时间，请耐心等待响应。
2. 如果API调用失败，系统会返回模拟的图片URL作为备用方案。
3. 目前仅支持`Qwen/Qwen-Image-Edit-2509`模型进行图片生成。
4. 生成的图片URL通常有一定的有效期，请及时保存图片。
5. API调用受系统限流配置限制，请合理控制调用频率。