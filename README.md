# xdream-agent

一款创新的AI聊天问题解答智能体，致力于解决用户在AI聊天工具中遇到的情感理解、复杂问题解决、对话连贯性和信息检索效率等方面的痛点。

## 核心功能

### 1. 智能问答与知识库集成
- 多源信息整合（互联网、用户自定义知识库、历史对话记录）
- 实时信息更新（联网能力）
- 专业领域知识深度（用户导入、学习用户行为）

### 2. 高级对话管理
- 上下文感知与记忆
- 主动澄清与追问
- 情感识别与响应

### 3. 个性化与用户画像
- 用户偏好学习
- 个性化推荐
- 角色扮演与风格定制

### 4. 高效信息管理与协作
- 全局检索与历史记录
- 知识沉淀与分享
- 任务管理与提醒

## 技术架构

### 系统架构
- **前端**: React 18+ + TypeScript + Tailwind CSS
- **后端**: Spring Boot 3.x + Java 17+ + Gradle
- **数据库**: H2(开发) / PostgreSQL(生产) + Redis + 向量数据库 + 图数据库
- **AI服务**: 多Agent编排 + LLM集成 + 情感分析 + 工具调用

### 微服务模块
1. **核心服务层**
   - 认证授权服务 (AuthService)
   - 用户管理服务 (UserService)
   - 订阅管理服务 (SubscriptionService)
   - 知识管理服务 (KnowledgeService)
   - 任务管理服务 (TaskService)
   - 协作服务 (CollaborationService)

2. **AI服务层**
   - Agent编排服务 (AgentOrchestrationService)
   - 大语言模型集成服务 (LLMIntegrationService)
   - 情感分析服务 (EmotionAnalysisService)
   - 多模态感知服务 (MultimodalPerceptionService)
   - 工具调用服务 (ToolCallingService)

## 快速开始

### 环境要求
- Java 17+
- Node.js 18+
- Gradle 8+
- Docker (可选)

### 开发环境搭建
```bash
# 克隆项目
git clone <repository-url>
cd xdream-agent

# 启动后端服务
cd backend
./gradlew bootRun

# 启动前端应用
cd ../frontend
npm install
npm start
```

### 生产环境部署
```bash
# 构建项目
./gradlew build

# 运行测试
./gradlew test

# 打包Docker镜像
docker build -t xdream-agent .
```

## 项目结构
```
xdream-agent/
├── backend/                    # 后端服务
│   ├── gateway/               # API网关
│   ├── auth-service/          # 认证授权服务
│   ├── user-service/          # 用户管理服务
│   ├── chat-service/          # 对话管理服务
│   ├── knowledge-service/     # 知识管理服务
│   ├── task-service/          # 任务管理服务
│   ├── subscription-service/  # 订阅管理服务
│   ├── agent-service/         # Agent编排服务
│   ├── llm-service/           # LLM集成服务
│   ├── emotion-service/       # 情感分析服务
│   ├── multimodal-service/    # 多模态感知服务
│   └── tool-service/          # 工具调用服务
├── frontend/                  # 前端应用
│   ├── src/
│   │   ├── components/        # 通用组件
│   │   ├── pages/            # 页面组件
│   │   ├── services/         # API服务
│   │   ├── stores/           # 状态管理
│   │   └── utils/            # 工具函数
│   └── public/
├── docs/                      # 项目文档
├── scripts/                   # 构建脚本
└── docker/                    # Docker配置
```

## 贡献指南

1.  Fork 项目
2.  创建功能分支 (`git checkout -b feature/amazing-feature`)
3.  提交更改 (`git commit -m 'Add some amazing feature'`)
4.  推送到分支 (`git push origin feature/amazing-feature`)
5.  创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目维护者: xdream-team
- 邮箱: team@xdream-agent.com
- 官方网站: https://xdream-agent.com