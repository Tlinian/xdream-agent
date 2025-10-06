import requests

# 测试获取模型列表的API
def test_get_models():
    try:
        # 根据Controller定义，可能的正确路径
        urls_to_test = [
            'http://localhost:8084/api/llm/models',
            'http://localhost:8084/llm/models'
        ]
        
        for url in urls_to_test:
            print(f"\n测试URL: {url}")
            headers = {'X-User-Id': 'test-user'}
            
            # 尝试GET请求
            print("尝试GET请求...")
            try:
                response = requests.get(url, headers=headers, timeout=5)
                print(f"GET状态码: {response.status_code}")
                print(f"GET响应内容: {response.text}")
            except Exception as e:
                print(f"GET请求出错: {str(e)}")
            
            # 尝试POST请求
            print("尝试POST请求...")
            try:
                response = requests.post(url, headers=headers, json={}, timeout=5)
                print(f"POST状态码: {response.status_code}")
                print(f"POST响应内容: {response.text}")
            except Exception as e:
                print(f"POST请求出错: {str(e)}")

    except Exception as e:
        print(f"❌ 测试过程出错: {str(e)}")

# 测试服务健康状态
def test_health():
    try:
        url = 'http://localhost:8084/actuator/health'
        response = requests.get(url, timeout=5)
        print(f"\n健康检查状态码: {response.status_code}")
        print(f"健康检查响应: {response.text}")
        if response.status_code == 200:
            print("✅ 服务健康状态正常")
    except Exception as e:
        print(f"❌ 健康检查失败: {str(e)}")

if __name__ == "__main__":
    print("开始测试llm-service API...")
    test_health()
    test_get_models()
    print("\n测试完成")