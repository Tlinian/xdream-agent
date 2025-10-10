@echo off
setlocal enabledelayedexpansion

:: 创建JSON请求文件
echo {
>> test_request.json
echo   "message": "你是",
>> test_request.json
echo   "modelType": "deepseek-ai/DeepSeek-V3",
>> test_request.json
echo   "temperature": 0.7,
>> test_request.json
echo   "maxTokens": 256
>> test_request.json
echo }

:: 使用curl命令发送POST请求
curl -X POST -H "Content-Type: application/json" -d @test_request.json http://localhost:8085/api/test/chat/simple

:: 删除临时文件
del test_request.json

endlocal