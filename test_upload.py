import requests
import os

# 创建测试文件
with open('test_document.txt', 'w', encoding='utf-8') as f:
    f.write('这是一个测试文件内容')

# 测试文件上传
url = 'http://localhost:8086/file/api/files/upload'
headers = {'X-User-Id': 'anonymous'}

with open('test_document.txt', 'rb') as f:
    files = {'file': ('test_document.txt', f, 'text/plain')}
    data = {'type': 'document', 'description': '测试文档'}
    
    try:
        response = requests.post(url, headers=headers, files=files, data=data)
        print(f'状态码: {response.status_code}')
        print(f'响应内容: {response.text}')
    except Exception as e:
        print(f'错误: {e}')