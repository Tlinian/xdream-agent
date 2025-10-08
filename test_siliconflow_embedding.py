import requests
import json

def get_embedding():
    url = "https://api.siliconflow.cn/v1/embeddings"
    headers = {
        "Authorization": "Bearer sk-siajbtsbuxwcojdgmmilhfomowrqbskusznhxqqaemikvdvj",
        "Content-Type": "application/json"
    }
    data = {
        "model": "BAAI/bge-large-zh-v1.5",
        "input": "Silicon flow embedding online: fast, affordable, and high-quality embedding services. come try it out!"
    }
    
    try:
        response = requests.post(url, headers=headers, json=data)
        response.raise_for_status()  # 检查请求是否成功
        
        # 打印响应内容，格式化输出
        result = response.json()
        print("请求成功!")
        print("响应内容:")
        print(json.dumps(result, ensure_ascii=False, indent=2))
        
        return result
        
    except requests.exceptions.HTTPError as http_err:
        print(f"HTTP错误发生: {http_err}")
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
    except requests.exceptions.ConnectionError as conn_err:
        print(f"连接错误发生: {conn_err}")
    except requests.exceptions.Timeout as timeout_err:
        print(f"请求超时: {timeout_err}")
    except requests.exceptions.RequestException as req_err:
        print(f"请求异常: {req_err}")
    except json.JSONDecodeError:
        print("响应不是有效的JSON格式")
        print(f"响应内容: {response.text}")
    
    return None

if __name__ == "__main__":
    print("发送嵌入向量请求到Silicon Flow API...")
    get_embedding()