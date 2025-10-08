import requests
import os

# 创建测试文件
with open('test_document.txt', 'w', encoding='utf-8') as f:
    f.write('这是一个测试文件内容')

# 测试通过网关的文件上传，添加更多调试信息
url = 'http://localhost:8080/api/files/upload'  # 网关端口
headers = {
    'X-User-Id': 'anonymous',
    'User-Agent': 'Test-Client'
}

print(f"测试URL: {url}")
print(f"请求头: {headers}")

with open('test_document.txt', 'rb') as f:
    files = {'file': ('test_document.txt', f, 'text/plain')}
    data = {'type': 'document', 'description': '测试文档'}
    
    try:
        response = requests.post(url, headers=headers, files=files, data=data, allow_redirects=False)
        print(f'状态码: {response.status_code}')
        print(f'响应头: {response.headers}')
        print(f'响应内容: {response.text}')
        
        # 检查是否有重定向
        if response.status_code in [301, 302, 307, 308]:
            print(f"重定向到: {response.headers.get('Location')}")
            
    except Exception as e:
        print(f'错误: {e}')

# 同时测试直接访问文件服务作为对比
direct_url = 'http://localhost:8086/file/api/files/upload'
print(f"\n对比测试直接访问文件服务: {direct_url}")

with open('test_document.txt', 'rb') as f:
    files = {'file': ('test_document.txt', f, 'text/plain')}
    data = {'type': 'document', 'description': '测试文档'}
    
    try:
        response = requests.post(direct_url, headers=headers, files=files, data=data)
        print(f'直接访问状态码: {response.status_code}')
        if response.status_code == 200:
            print('直接访问成功！')
        else:
            print(f'直接访问响应: {response.text}')
    except Exception as e:
        print(f'直接访问错误: {e}')