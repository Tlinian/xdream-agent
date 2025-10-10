# xdream-agent 程序设计说明书

## 1. 引言

本程序设计说明书（Program Design Specification, PDS）旨在详细描述 xdream-agent 智能体的技术实现方案，包括功能模块、系统架构、API接口、数据库设计、前端模块设计和后端模块设计。xdream-agent 是一款创新的 AI 聊天问题解答智能体，致力于解决用户在 AI 聊天工具中遇到的情感理解、复杂问题解决、对话连贯性和信息检索效率等方面的痛点。本 PDS 将作为开发团队实现产品目标的技术指导文档。

## 2. 功能模块提炼

根据产品需求说明书（PRD），xdream-agent 的核心功能模块可以提炼如下：

### 2.1 核心功能

1.  **智能问答与知识库集成**
    *   多源信息整合（互联网、用户自定义知识库、历史对话记录）
    *   实时信息更新（联网能力）
    *   专业领域知识深度（用户导入、学习用户行为）

2.  **高级对话管理**
    *   上下文感知与记忆
    *   主动澄清与追问
    *   情感识别与响应

3.  **个性化与用户画像**
    *   用户偏好学习
    *   个性化推荐
    *   角色扮演与风格定制

4.  **高效信息管理与协作**
    *   全局检索与历史记录
    *   知识沉淀与分享（保存为结构化知识、团队共享）
    *   任务管理与提醒

### 2.2 差异化策略（技术实现层面）

1.  **“情感共鸣”的智能体**
    *   深度情感理解：通过情感分析模型和用户行为分析实现。
    *   “人性化”的对话体验：通过自然语言生成（NLG）和对话管理优化。

2.  **“问题终结者”的Agent工作流**
    *   多Agent协作模式：设计Agent调度和协作框架。
    *   工具调用与自动化：实现外部工具（代码解释器、数据分析工具、文档生成器、浏览器等）的集成和调用。
    *   可解释性与透明度：记录并展示Agent的思考路径和工具调用过程。

3.  **“知识炼金术士”的个人/团队知识管理**
    *   智能知识图谱构建：从对话和文档中提取信息，构建知识图谱。
    *   主动知识推荐与洞察：基于知识图谱和用户画像进行推荐。
    *   无缝集成与跨平台：提供API接口，支持与其他系统集成。

### 2.3 其他功能

*   用户认证与授权
*   订阅与付费管理
*   系统管理与监控
*   日志与审计




## 3. 系统架构设计

xdream-agent 将采用微服务架构，以确保系统的模块化、可扩展性和高可用性。整体架构将分为前端层、API网关层、核心服务层、AI服务层、数据服务层和外部服务集成层。

### 3.1 总体架构图

```mermaid
graph TD
    User[用户] --> |Web/Mobile/Desktop| Frontend[前端应用 (React)]
    Frontend --> APIGateway[API 网关]

    APIGateway --> |认证/授权/路由| CoreServices[核心服务层]
    APIGateway --> |AI相关请求| AIServices[AI 服务层]

    subgraph CoreServices
        AuthService[认证授权服务]
        UserService[用户管理服务]
        SubscriptionService[订阅管理服务]
        KnowledgeService[知识管理服务]
        TaskService[任务管理服务]
        CollaborationService[协作服务]
    end

    subgraph AIServices
        AgentOrchestrationService[Agent 编排服务]
        LLMIntegrationService[大语言模型集成服务]
        EmotionAnalysisService[情感分析服务]
        MultimodalPerceptionService[多模态感知服务]
        ToolCallingService[工具调用服务]
    end

    CoreServices --> DataServices[数据服务层]
    AIServices --> DataServices
    AIServices --> ExternalServices[外部服务集成层]

    subgraph DataServices
        Database[关系型数据库 (H2/PostgreSQL)]
        VectorDB[向量数据库 (Pinecone/Milvus)]
        GraphDB[图数据库 (Neo4j)]
        Cache[缓存服务 (Redis)]
    end

    subgraph ExternalServices
        InternetSearch[互联网搜索 API]
        CodeInterpreter[代码解释器]
        DataAnalysisTools[数据分析工具]
        DocumentGenerators[文档生成器]
        BrowserAutomation[浏览器自动化]
        ThirdPartyAPIs[第三方服务 API]
    end

    AuthService --> Database
    UserService --> Database
    SubscriptionService --> Database
    KnowledgeService --> Database & VectorDB & GraphDB
    TaskService --> Database
    CollaborationService --> Database

    AgentOrchestrationService --> LLMIntegrationService & EmotionAnalysisService & MultimodalPerceptionService & ToolCallingService & KnowledgeService
    LLMIntegrationService --> ExternalServices
    ToolCallingService --> ExternalServices

    DataServices --> Cache
```

### 3.2 各层职责

1.  **前端应用层 (Frontend Application Layer)**：
    *   **技术栈**：React
    *   **职责**：提供用户界面，处理用户交互，通过API网关与后端服务通信。

2.  **API 网关层 (API Gateway Layer)**：
    *   **技术栈**：Spring Cloud Gateway 或 Nginx + Spring Boot
    *   **职责**：统一入口，负责请求路由、负载均衡、认证授权、限流熔断等。

3.  **核心服务层 (Core Services Layer)**：
    *   **技术栈**：Java Spring Boot Gradle
    *   **职责**：
        *   **认证授权服务 (AuthService)**：用户注册、登录、会话管理、权限控制。
        *   **用户管理服务 (UserService)**：用户资料、偏好设置、用户画像管理。
        *   **订阅管理服务 (SubscriptionService)**：处理用户订阅、付费、套餐管理。
        *   **知识管理服务 (KnowledgeService)**：知识的创建、存储、检索、分享、权限控制。
        *   **任务管理服务 (TaskService)**：任务的创建、分配、状态更新、提醒。
        *   **协作服务 (CollaborationService)**：团队空间、成员管理、知识共享。

4.  **AI 服务层 (AI Services Layer)**：
    *   **技术栈**：Java Spring Boot Gradle (集成Python微服务进行AI模型调用)
    *   **职责**：
        *   **Agent 编排服务 (AgentOrchestrationService)**：根据用户意图，调度内部Agent和外部工具，管理Agent工作流。
        *   **大语言模型集成服务 (LLMIntegrationService)**：封装对各类大语言模型（如OpenAI GPT, Google Gemini）的调用，提供统一接口。
        *   **情感分析服务 (EmotionAnalysisService)**：识别用户输入中的情感，为Agent提供情感上下文。
        *   **多模态感知服务 (MultimodalPerceptionService)**：处理文本、语音、图像等多模态输入，将其转化为Agent可理解的格式。
        *   **工具调用服务 (ToolCallingService)**：管理和执行外部工具的调用，如代码解释器、数据分析工具、浏览器自动化等。

5.  **数据服务层 (Data Services Layer)**：
    *   **技术栈**：H2 (开发环境), PostgreSQL (生产环境), Pinecone/Milvus, Neo4j, Redis
    *   **职责**：
        *   **关系型数据库 (Database)**：存储用户、订阅、任务、知识元数据等结构化数据。
        *   **向量数据库 (VectorDB)**：存储知识嵌入向量，支持高效的语义检索 (RAG)。
        *   **图数据库 (GraphDB)**：存储知识图谱，支持复杂知识关联和推理。
        *   **缓存服务 (Cache)**：提高数据访问速度，减轻数据库压力。

6.  **外部服务集成层 (External Services Integration Layer)**：
    *   **技术栈**：根据外部服务API要求
    *   **职责**：集成第三方服务，如互联网搜索API、代码解释器、数据分析工具、文档生成器、浏览器自动化工具等，为AI服务层提供能力扩展。

### 3.3 技术选型说明

*   **后端框架**：选择 **Java Spring Boot Gradle**，因其成熟稳定、生态丰富、性能优越，适合构建微服务架构。
*   **数据库**：开发环境使用 **H2** 内存数据库，方便快速启动和测试；生产环境推荐使用 **PostgreSQL**，提供更强大的数据管理和并发处理能力。
*   **AI模型集成**：通过 **LLMIntegrationService** 封装，实现大模型的灵活切换和管理，降低对特定模型的依赖。
*   **Agent编排**：将是核心技术挑战，需要设计灵活的Agent调度策略和工作流引擎。




## 4. API 接口定义

xdream-agent 的 API 接口将遵循 RESTful 风格，采用 JSON 格式进行数据交换，并使用 OAuth2 或 JWT 进行认证授权。以下是主要功能模块的 API 接口设计。

### 4.1 用户认证与授权 (AuthService)

| 接口名称 | HTTP 方法 | 路径 | 描述 | 请求体示例 | 响应体示例 | 认证 | 备注 |
|---|---|---|---|---|---|---|---|
| 用户注册 | `POST` | `/api/auth/register` | 用户注册新账号 | `{"username": "testuser", "password": "password123", "email": "test@example.com"}` | `{"message": "User registered successfully"}` | 无 | |
| 用户登录 | `POST` | `/api/auth/login` | 用户登录并获取 JWT Token | `{"username": "testuser", "password": "password123"}` | `{"token": "eyJhbGciOi...", "expiresIn": 3600}` | 无 | |
| 刷新Token | `POST` | `/api/auth/refresh` | 刷新用户 JWT Token | `{"refreshToken": "some_refresh_token"}` | `{"token": "eyJhbGciOi...", "expiresIn": 3600}` | JWT | |
| 登出 | `POST` | `/api/auth/logout` | 用户登出 | 无 | `{"message": "Logged out successfully"}` | JWT | |

### 4.2 用户管理 (UserService)

| 接口名称 | HTTP 方法 | 路径 | 描述 | 请求体示例 | 响应体示例 | 认证 | 备注 |
|---|---|---|---|---|---|---|---|
| 获取用户信息 | `GET` | `/api/users/{userId}` | 获取指定用户的信息 | 无 | `{"id": "uuid", "username": "testuser", "email": "test@example.com", "preferences": {}}` | JWT | 仅限本人或管理员 |
| 更新用户信息 | `PUT` | `/api/users/{userId}` | 更新指定用户的信息 | `{"preferences": {"language": "zh-CN"}}` | `{"message": "User updated successfully"}` | JWT | 仅限本人或管理员 |
| 删除用户 | `DELETE` | | `/api/users/{userId}` | 删除指定用户 | 无 | `{"message": "User deleted successfully"}` | JWT | 仅限本人或管理员 |

### 4.3 对话管理 (ChatService)

| 接口名称 | HTTP 方法 | 路径 | 描述 | 请求体示例 | 响应体示例 | 认证 | 备注 |
|---|---|---|---|---|---|---|---|
| 开始新对话 | `POST` | `/api/chats` | 创建一个新的对话会话 | `{"agentId": "default", "initialMessage": "你好"}` | `{"chatId": "uuid", "messages": []}` | JWT | `agentId` 可选，默认为通用Agent |
| 发送消息 | `POST` | `/api/chats/{chatId}/messages` | 向指定对话发送消息 | `{"content": "我的问题是...", "type": "text"}` | `{"messageId": "uuid", "sender": "user", "content": "...", "timestamp": "..."}` | JWT | 支持文本、语音、文件等类型 |
| 获取对话历史 | `GET` | `/api/chats/{chatId}/messages` | 获取指定对话的历史消息 | 无 | `[{"messageId": "uuid", "sender": "user", "content": "...", "timestamp": "..."}]` | JWT | 支持分页和时间范围过滤 |
| 更新消息 | `PUT` | `/api/chats/{chatId}/messages/{messageId}` | 更新对话中的某条消息 | `{"content": "更新后的内容"}` | `{"message": "Message updated"}` | JWT | 仅限用户本人修改自己的消息 |
| 删除消息 | `DELETE` | `/api/chats/{chatId}/messages/{messageId}` | 删除对话中的某条消息 | 无 | `{"message": "Message deleted"}` | JWT | 仅限用户本人删除自己的消息 |
| 搜索对话 | `GET` | `/api/chats/search` | 搜索用户的历史对话 | `?query=关键词&agentId=...` | `[{"chatId": "uuid", "title": "...", "lastMessage": "..."}]` | JWT | |

### 4.4 知识库管理 (KnowledgeService)

| 接口名称 | HTTP 方法 | 路径 | 描述 | 请求体示例 | 响应体示例 | 认证 | 备注 |
|---|---|---|---|---|---|---|---|
| 创建知识条目 | `POST` | `/api/knowledge` | 创建新的知识条目 | `{"title": "产品发布策略", "content": "...", "tags": ["产品", "策略"]}` | `{"knowledgeId": "uuid", "title": "..."}` | JWT | |
| 获取知识条目 | `GET` | `/api/knowledge/{knowledgeId}` | 获取指定知识条目详情 | 无 | `{"knowledgeId": "uuid", "title": "...", "content": "..."}` | JWT | |
| 更新知识条目 | `PUT` | `/api/knowledge/{knowledgeId}` | 更新指定知识条目 | `{"content": "更新后的内容"}` | `{"message": "Knowledge updated"}` | JWT | |
| 删除知识条目 | `DELETE` | `/api/knowledge/{knowledgeId}` | 删除指定知识条目 | 无 | `{"message": "Knowledge deleted"}` | JWT | |
| 搜索知识库 | `GET` | `/api/knowledge/search` | 搜索知识库中的条目 | `?query=关键词&tag=...` | `[{"knowledgeId": "uuid", "title": "...", "snippet": "..."}]` | JWT | 支持全文搜索和标签过滤 |
| 上传知识文件 | `POST` | `/api/knowledge/upload` | 上传文件到知识库 | `(multipart/form-data)` | `{"knowledgeId": "uuid", "fileName": "..."}` | JWT | 支持PDF, DOCX, TXT等 |

### 4.5 任务管理 (TaskService)

| 接口名称 | HTTP 方法 | 路径 | 描述 | 请求体示例 | 响应体示例 | 认证 | 备注 |
|---|---|---|---|---|---|---|---|
| 创建任务 | `POST` | `/api/tasks` | 创建新任务 | `{"title": "完成报告", "dueDate": "2025-10-30", "priority": "high"}` | `{"taskId": "uuid", "title": "..."}` | JWT | |
| 获取任务列表 | `GET` | `/api/tasks` | 获取用户任务列表 | `?status=pending&priority=high` | `[{"taskId": "uuid", "title": "...", "status": "pending"}]` | JWT | 支持状态、优先级过滤 |
| 更新任务状态 | `PUT` | `/api/tasks/{taskId}/status` | 更新任务状态 | `{"status": "completed"}` | `{"message": "Task status updated"}` | JWT | |
| 删除任务 | `DELETE` | `/api/tasks/{taskId}` | 删除任务 | 无 | `{"message": "Task deleted"}` | JWT | |

### 4.6 订阅与付费 (SubscriptionService)

| 接口名称 | HTTP 方法 | 路径 | 描述 | 请求体示例 | 响应体示例 | 认证 | 备注 |
|---|---|---|---|---|---|---|---|
| 获取订阅计划 | `GET` | `/api/subscriptions/plans` | 获取所有可用的订阅计划 | 无 | `[{"planId": "free", "name": "免费版", "price": 0}]` | 无 | |
| 获取用户订阅 | `GET` | `/api/subscriptions/my` | 获取当前用户的订阅信息 | 无 | `{"planId": "premium", "status": "active", "expiresAt": "..."}` | JWT | |
| 创建订阅订单 | `POST` | `/api/subscriptions/order` | 创建新的订阅订单 | `{"planId": "premium", "paymentMethod": "alipay"}` | `{"orderId": "uuid", "paymentUrl": "..."}` | JWT | 返回支付链接 |
| 确认支付 | `POST` | `/api/subscriptions/confirm-payment` | 确认支付结果 | `{"orderId": "uuid", "paymentStatus": "success"}` | `{"message": "Payment confirmed"}` | JWT | 支付回调接口 |

### 4.7 AI Agent 服务 (AgentOrchestrationService)

| 接口名称 | HTTP 方法 | 路径 | 描述 | 请求体示例 | 响应体示例 | 认证 | 备注 |
|---|---|---|---|---|---|---|---|
| 获取可用Agent | `GET` | `/api/agents` | 获取所有可用的AI Agent列表 | 无 | `[{"agentId": "default", "name": "通用助手", "description": "..."}]` | JWT | |
| 切换Agent | `POST` | `/api/chats/{chatId}/agent` | 在对话中切换使用的Agent | `{"agentId": "product_manager_agent"}` | `{"message": "Agent switched"}` | JWT | |
| 获取Agent思考路径 | `GET` | `/api/chats/{chatId}/messages/{messageId}/thought-process` | 获取AI Agent解决问题的思考路径和工具调用记录 | 无 | `{"steps": [{"action": "search", "query": "..."}]}` | JWT | 可解释性AI功能 |

### 4.8 文件上传服务 (FileService)

| 接口名称 | HTTP 方法 | 路径 | 描述 | 请求体示例 | 响应体示例 | 认证 | 备注 |
|---|---|---|---|---|---|---|---|
| 上传文件 | `POST` | `/api/files/upload` | 上传文件到服务器 | `(multipart/form-data)` | `{"fileId": "uuid", "url": "..."}` | JWT | 用于聊天附件、知识库文件等 |
| 下载文件 | `GET` | `/api/files/{fileId}` | 下载文件 | 无 | 文件流 | JWT | |

**通用响应结构**：

*   **成功响应**：`HTTP 200 OK`，`{"data": {...}}` 或 `{"message": "..."}`
*   **错误响应**：`HTTP 4xx/5xx`，`{"code": "ERROR_CODE", "message": "错误描述"}`





## 5. 数据库设计

xdream-agent 的数据库设计将基于关系型数据库（开发环境使用 H2，生产环境推荐 PostgreSQL），并结合向量数据库和图数据库来支持高级的 AI 功能。以下是主要实体及其表结构设计。

### 5.1 关系型数据库 (H2/PostgreSQL)

#### 5.1.1 `users` 表 (用户表)

存储用户基本信息、认证凭证和个性化偏好。

| 字段名 | 数据类型 | 约束 | 描述 |
|---|---|---|---|
| `id` | `VARCHAR(36)` | `PRIMARY KEY` | 用户唯一标识符 (UUID) |
| `username` | `VARCHAR(50)` | `UNIQUE NOT NULL` | 用户名 |
| `password_hash` | `VARCHAR(255)` | `NOT NULL` | 密码哈希值 |
| `email` | `VARCHAR(100)` | `UNIQUE NOT NULL` | 用户邮箱 |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 注册时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 最后更新时间 |
| `preferences` | `TEXT` | `NULL` | 用户偏好设置 (JSON 格式) |
| `role` | `VARCHAR(20)` | `DEFAULT 'USER'` | 用户角色 (USER, ADMIN) |

#### 5.1.2 `subscriptions` 表 (订阅表)

存储用户的订阅信息。

| 字段名 | 数据类型 | 约束 | 描述 |
|---|---|---|---|
| `id` | `VARCHAR(36)` | `PRIMARY KEY` | 订阅唯一标识符 (UUID) |
| `user_id` | `VARCHAR(36)` | `NOT NULL, FOREIGN KEY (user_id) REFERENCES users(id)` | 关联用户 ID |
| `plan_id` | `VARCHAR(50)` | `NOT NULL` | 订阅计划 ID (FREE, PREMIUM, TEAM) |
| `status` | `VARCHAR(20)` | `NOT NULL` | 订阅状态 (ACTIVE, EXPIRED, CANCELED) |
| `start_date` | `TIMESTAMP` | `NOT NULL` | 订阅开始时间 |
| `end_date` | `TIMESTAMP` | `NULL` | 订阅结束时间 |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 最后更新时间 |

#### 5.1.3 `chats` 表 (对话表)

存储对话会话的基本信息。

| 字段名 | 数据类型 | 约束 | 描述 |
|---|---|---|---|
| `id` | `VARCHAR(36)` | `PRIMARY KEY` | 对话唯一标识符 (UUID) |
| `user_id` | `VARCHAR(36)` | `NOT NULL, FOREIGN KEY (user_id) REFERENCES users(id)` | 关联用户 ID |
| `title` | `VARCHAR(255)` | `NULL` | 对话标题 (可由AI生成或用户编辑) |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 最后更新时间 |
| `agent_id` | `VARCHAR(50)` | `NULL` | 当前对话使用的 Agent ID |

#### 5.1.4 `messages` 表 (消息表)

存储对话中的每条消息。

| 字段名 | 数据类型 | 约束 | 描述 |
|---|---|---|---|
| `id` | `VARCHAR(36)` | `PRIMARY KEY` | 消息唯一标识符 (UUID) |
| `chat_id` | `VARCHAR(36)` | `NOT NULL, FOREIGN KEY (chat_id) REFERENCES chats(id)` | 关联对话 ID |
| `sender` | `VARCHAR(20)` | `NOT NULL` | 发送者 (USER, AI) |
| `content` | `TEXT` | `NOT NULL` | 消息内容 |
| `type` | `VARCHAR(20)` | `DEFAULT 'TEXT'` | 消息类型 (TEXT, IMAGE, AUDIO, FILE) |
| `timestamp` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 消息发送时间 |
| `emotion_score` | `DECIMAL(5,2)` | `NULL` | 情感分数 (AI分析) |
| `thought_process` | `TEXT` | `NULL` | AI的思考过程 (JSON 格式) |

#### 5.1.5 `knowledge_items` 表 (知识条目表)

存储用户创建或AI生成的知识内容。

| 字段名 | 数据类型 | `约束` | 描述 |
|---|---|---|---|
| `id` | `VARCHAR(36)` | `PRIMARY KEY` | 知识条目唯一标识符 (UUID) |
| `user_id` | `VARCHAR(36)` | `NOT NULL, FOREIGN KEY (user_id) REFERENCES users(id)` | 关联用户 ID |
| `title` | `VARCHAR(255)` | `NOT NULL` | 知识条目标题 |
| `content` | `LONGTEXT` | `NULL` | 知识内容 (富文本或Markdown) |
| `type` | `VARCHAR(50)` | `NOT NULL` | 知识类型 (DOCUMENT, CODE, CONVERSATION_SUMMARY) |
| `tags` | `TEXT` | `NULL` | 标签 (逗号分隔) |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 最后更新时间 |
| `source_chat_id` | `VARCHAR(36)` | `NULL, FOREIGN KEY (source_chat_id) REFERENCES chats(id)` | 来源对话 ID |

#### 5.1.6 `tasks` 表 (任务表)

存储用户创建或AI识别的任务。

| 字段名 | 数据类型 | 约束 | 描述 |
|---|---|---|---|
| `id` | `VARCHAR(36)` | `PRIMARY KEY` | 任务唯一标识符 (UUID) |
| `user_id` | `VARCHAR(36)` | `NOT NULL, FOREIGN KEY (user_id) REFERENCES users(id)` | 关联用户 ID |
| `title` | `VARCHAR(255)` | `NOT NULL` | 任务标题 |
| `description` | `TEXT` | `NULL` | 任务描述 |
| `due_date` | `DATE` | `NULL` | 截止日期 |
| `priority` | `VARCHAR(20)` | `DEFAULT 'MEDIUM'` | 优先级 (LOW, MEDIUM, HIGH) |
| `status` | `VARCHAR(20)` | `DEFAULT 'PENDING'` | 任务状态 (PENDING, COMPLETED, CANCELED) |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 最后更新时间 |

#### 5.1.7 `files` 表 (文件表)

存储用户上传的文件信息。

| 字段名 | 数据类型 | 约束 | 描述 |
|---|---|---|---|
| `id` | `VARCHAR(36)` | `PRIMARY KEY` | 文件唯一标识符 (UUID) |
| `user_id` | `VARCHAR(36)` | `NOT NULL, FOREIGN KEY (user_id) REFERENCES users(id)` | 上传用户 ID |
| `file_name` | `VARCHAR(255)` | `NOT NULL` | 文件名 |
| `file_path` | `VARCHAR(500)` | `NOT NULL` | 文件存储路径/URL |
| `file_type` | `VARCHAR(50)` | `NOT NULL` | 文件类型 (e.g., image/png, application/pdf) |
| `file_size` | `BIGINT` | `NOT NULL` | 文件大小 (字节) |
| `uploaded_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 上传时间 |

### 5.2 向量数据库 (VectorDB)

*   **目的**：存储 `knowledge_items` 和 `messages` 的文本嵌入向量，用于实现高效的语义搜索和 RAG (Retrieval-Augmented Generation)。
*   **选型**：Pinecone 或 Milvus (生产环境)，HNSWlib (本地测试/小型部署)。
*   **数据结构**：每个向量条目包含 `id` (关联 `knowledge_items.id` 或 `messages.id`)、`embedding` (高维向量) 和 `metadata` (如 `user_id`, `chat_id`, `tags` 等用于过滤)。

### 5.3 图数据库 (GraphDB)

*   **目的**：构建和管理用户个性化知识图谱，关联知识点、用户偏好、Agent行为等，支持复杂知识推理和主动推荐。
*   **选型**：Neo4j 或 ArangoDB。
*   **数据结构**：
    *   **节点 (Nodes)**：用户、知识条目、Agent、概念、实体等。
    *   **关系 (Relationships)**：`HAS_PREFERENCE` (用户-概念)、`RELATED_TO` (知识-知识)、`USED_BY` (知识-Agent)、`GENERATED_BY` (消息-Agent) 等。

### 5.4 缓存 (Cache)

*   **目的**：缓存常用数据（如用户偏好、热门知识条目、LLM响应），减少数据库负载，提高响应速度。
*   **选型**：Redis。
*   **缓存策略**：根据业务需求采用 LRU、LFU 等策略，并设置合理的过期时间。

### 5.5 数据库迁移与管理

*   **工具**：Flyway 或 Liquibase 用于数据库版本管理和迁移。
*   **原则**：所有数据库变更都应通过脚本进行管理，确保开发、测试、生产环境的一致性。




## 6. 前端模块设计 (React)

xdream-agent 的前端将采用 React 框架进行开发，以提供高性能、响应式和用户友好的界面。我们将遵循组件化、模块化的设计原则，确保代码的可维护性和可扩展性。

### 6.1 技术栈

*   **框架**：React 18+
*   **语言**：TypeScript
*   **状态管理**：Zustand / React Query (用于数据获取和缓存)
*   **UI 组件库**：Ant Design / Material-UI / Shadcn UI (根据具体需求选择，倾向于轻量级和可定制性强的)
*   **样式**：Tailwind CSS / Styled Components (倾向于 Tailwind CSS，提高开发效率和一致性)
*   **路由**：React Router DOM
*   **构建工具**：Vite / Webpack
*   **HTTP 客户端**：Axios / Fetch API

### 6.2 模块划分

前端应用将根据功能和页面进行模块划分，主要包括以下模块：

1.  **认证模块 (Auth)**：
    *   **功能**：用户注册、登录、登出、密码找回。
    *   **组件**：`LoginPage`, `RegisterPage`, `AuthForm`。
    *   **交互**：与后端认证授权服务 (AuthService) 交互，获取和管理 JWT Token。

2.  **主页/对话模块 (Chat)**：
    *   **功能**：显示历史对话列表、创建新对话、实时聊天界面、消息发送/接收、Agent切换、工具调用结果展示。
    *   **组件**：`ChatLayout`, `ChatList`, `ChatMessage`, `ChatInput`, `AgentSelector`, `ToolOutputDisplay`。
    *   **交互**：与后端对话管理服务 (ChatService) 和 AI Agent 服务 (AgentOrchestrationService) 交互，实现实时消息通信 (WebSocket)。

3.  **知识库模块 (Knowledge Base)**：
    *   **功能**：知识条目列表、搜索、筛选、创建、编辑、删除、文件上传、知识图谱可视化。
    *   **组件**：`KnowledgeLayout`, `KnowledgeList`, `KnowledgeEditor`, `KnowledgeSearch`, `KnowledgeGraphView`。
    *   **交互**：与后端知识管理服务 (KnowledgeService) 交互，支持富文本编辑和文件上传。

4.  **任务管理模块 (Task)**：
    *   **功能**：任务列表、创建、编辑、状态更新、提醒设置。
    *   **组件**：`TaskList`, `TaskItem`, `TaskForm`。
    *   **交互**：与后端任务管理服务 (TaskService) 交互。

5.  **个人设置/账户管理模块 (Settings)**：
    *   **功能**：个人资料编辑、偏好设置、订阅管理、API Key 管理。
    *   **组件**：`SettingsLayout`, `ProfileSettings`, `PreferenceSettings`, `SubscriptionManager`, `ApiKeysManager`。
    *   **交互**：与后端用户管理服务 (UserService) 和订阅管理服务 (SubscriptionService) 交互。

6.  **通用组件库 (Common UI)**：
    *   **功能**：按钮、输入框、加载动画、消息提示、模态框等可复用 UI 组件。
    *   **组件**：`Button`, `Input`, `LoadingSpinner`, `Toast`, `Modal`。
    *   **目的**：确保 UI 一致性和开发效率。

### 6.3 关键技术点与实现考量

1.  **实时通信**：使用 WebSocket 实现聊天消息的实时发送和接收，确保流畅的对话体验。后端将通过 Spring WebSocket 或类似的方案提供支持。
2.  **状态管理**：对于全局状态（如用户认证信息、当前对话ID），使用 Zustand 进行轻量级管理。对于服务器端数据状态（如对话列表、知识库数据），使用 React Query 进行管理，利用其缓存、去重、重试等功能，优化数据获取体验。
3.  **响应式设计**：采用 Tailwind CSS 或其他 CSS-in-JS 方案，结合媒体查询，实现网页在不同设备（PC、平板、手机）上的自适应布局和优化显示。
4.  **性能优化**：
    *   **代码分割 (Code Splitting)**：按需加载模块，减少初始加载时间。
    *   **懒加载 (Lazy Loading)**：对非关键组件和路由进行懒加载。
    *   **虚拟列表 (Virtualization)**：对于长列表（如聊天记录、知识库列表），使用虚拟列表技术优化渲染性能。
    *   **图片优化**：使用 WebP 格式，按需加载，CDN 加速。
5.  **错误处理与用户反馈**：统一的错误处理机制，通过 Toast 或 Modal 及时向用户反馈操作结果和错误信息。
6.  **国际化 (i18n)**：预留国际化支持，方便未来扩展多语言版本。
7.  **可访问性 (Accessibility)**：遵循 WCAG 指南，确保应用对所有用户都可用。
8.  **测试**：采用 Jest/React Testing Library 进行单元测试和集成测试，确保组件质量和功能稳定性。

### 6.4 页面布局示例 (以主页/对话页为例)

```mermaid
graph TD
    A[顶部导航栏] --> B[左侧对话列表/功能侧边栏]
    A --> C[主内容区 (聊天窗口)]
    B --> C
    C --> D[底部输入框/工具栏]

    subgraph 顶部导航栏
        Logo & 产品名称
        用户头像 & 设置入口
        新对话按钮
    end

    subgraph 左侧对话列表/功能侧边栏
        历史对话列表 (可搜索/筛选)
        Agent 切换/管理入口
        知识库/任务管理快捷入口
    end

    subgraph 主内容区 (聊天窗口)
        对话消息展示 (用户/AI)
        AI 思考过程/工具调用展示
        知识引用/建议
    end

    subgraph 底部输入框/工具栏
        文本输入框
        语音输入按钮
        文件上传按钮
        表情/附件按钮
        发送按钮
        快捷提示词/Agent 动作按钮
    end
```

通过上述设计，前端应用将能够提供一个高效、美观且功能强大的用户界面，充分发挥 xdream-agent 的各项能力。



## 7. 后端模块设计 (Spring Boot)

xdream-agent 的后端将采用 Java Spring Boot 框架，并使用 Gradle 作为构建工具。我们将遵循领域驱动设计 (DDD) 和微服务架构原则，确保后端服务的健壮性、可扩展性和高性能。

### 7.1 技术栈

*   **框架**：Spring Boot 3.x
*   **语言**：Java 17+
*   **构建工具**：Gradle
*   **依赖管理**：Maven Central
*   **Web 框架**：Spring WebFlux (响应式编程) 或 Spring MVC (传统同步)
*   **数据访问**：Spring Data JPA / MyBatis (根据具体服务需求选择)
*   **数据库**：H2 (开发/测试), PostgreSQL (生产)
*   **缓存**：Spring Cache + Redis
*   **消息队列**：Kafka / RabbitMQ (用于 Agent 间通信和异步任务)
*   **认证授权**：Spring Security + JWT
*   **API 文档**：Springdoc OpenAPI (Swagger UI)
*   **监控**：Spring Boot Actuator + Prometheus + Grafana
*   **日志**：SLF4J + Logback

### 7.2 模块划分与职责

后端服务将根据系统架构设计中的核心服务层和 AI 服务层进行模块划分，每个模块对应一个独立的 Spring Boot 微服务。

1.  **认证授权服务 (AuthService)**：
    *   **职责**：用户注册、登录、登出、JWT Token 的生成、验证和刷新、权限管理。
    *   **核心实体**：User, Role。
    *   **关键技术**：Spring Security, JWT。

2.  **用户管理服务 (UserService)**：
    *   **职责**：管理用户资料、偏好设置、用户画像的存储和更新。
    *   **核心实体**：User, UserProfile。
    *   **关键技术**：Spring Data JPA。

3.  **订阅管理服务 (SubscriptionService)**：
    *   **职责**：管理订阅计划、用户订阅状态、支付流程集成（如支付宝、微信支付回调）。
    *   **核心实体**：Subscription, Plan, PaymentTransaction。
    *   **关键技术**：Spring Data JPA, 外部支付 SDK 集成。

4.  **对话管理服务 (ChatService)**：
    *   **职责**：管理对话会话的创建、消息的存储和检索、对话上下文的维护。
    *   **核心实体**：Chat, Message。
    *   **关键技术**：Spring Data JPA, WebSocket (用于实时消息)。

5.  **知识管理服务 (KnowledgeService)**：
    *   **职责**：管理知识条目的创建、存储、检索、更新、删除、文件上传、知识图谱构建和维护。
    *   **核心实体**：KnowledgeItem, Tag, FileMetadata。
    *   **关键技术**：Spring Data JPA, Spring Data Redis (缓存), 向量数据库客户端 (Pinecone/Milvus SDK), 图数据库客户端 (Neo4j Driver)。

6.  **任务管理服务 (TaskService)**：
    *   **职责**：管理任务的创建、列表、状态更新、提醒设置。
    *   **核心实体**：Task。
    *   **关键技术**：Spring Data JPA。

7.  **Agent 编排服务 (AgentOrchestrationService)**：
    *   **职责**：xdream-agent 的核心大脑，负责解析用户意图，调度内部 Agent 和外部工具，管理 Agent 工作流，生成最终响应。
    *   **核心实体**：Agent, Tool, Workflow。
    *   **关键技术**：自定义 Agent 调度框架，状态机模式，事件驱动架构 (Spring Event / Kafka)。

8.  **大语言模型集成服务 (LLMIntegrationService)**：
    *   **职责**：封装对各类大语言模型（如 OpenAI GPT, Google Gemini, 文心一言）的 API 调用，提供统一的抽象接口，支持模型切换和负载均衡。
    *   **核心实体**：LLMProvider, LLMRequest, LLMResponse。
    *   **关键技术**：HTTP 客户端 (WebClient / RestTemplate), 各种 LLM SDK。

9.  **情感分析服务 (EmotionAnalysisService)**：
    *   **职责**：接收文本或语音输入，进行情感分析，返回情感分数和类别。
    *   **核心实体**：EmotionAnalysisResult。
    *   **关键技术**：集成第三方情感分析 API 或自研模型 (可能通过 Python 微服务提供)。

10. **多模态感知服务 (MultimodalPerceptionService)**：
    *   **职责**：处理文本、语音、图像等多模态输入，将其转化为 Agent 可理解的统一格式。
    *   **核心实体**：MultimodalInput。
    *   **关键技术**：集成语音识别 (ASR)、图像识别 (OCR/CV) 服务。

11. **工具调用服务 (ToolCallingService)**：
    *   **职责**：管理和执行外部工具的调用，如代码解释器、数据分析工具、文档生成器、浏览器自动化等。负责工具的注册、发现和执行。
    *   **核心实体**：ToolDefinition, ToolExecutionResult。
    *   **关键技术**：动态代理，反射，外部 API 调用。

### 7.3 关键技术点与实现考量

1.  **微服务通信**：服务间通信将主要采用 RESTful API 调用，对于高吞吐量或异步场景，可引入消息队列 (Kafka/RabbitMQ)。
2.  **数据一致性**：在微服务架构下，通过最终一致性（如 Saga 模式）来处理跨服务的事务。
3.  **高可用与负载均衡**：利用 Spring Cloud Eureka/Consul 进行服务注册与发现，结合 Ribbon/LoadBalancer 实现客户端负载均衡。部署在 Kubernetes 等容器编排平台，实现服务的自动伸缩和故障恢复。
4.  **安全性**：所有对外 API 接口通过 API 网关进行统一认证授权。服务间通信采用 mTLS 或其他加密方式。敏感数据加密存储。
5.  **可观测性**：集成 Prometheus 和 Grafana 进行指标监控，使用 ELK Stack (Elasticsearch, Logstash, Kibana) 进行日志管理和分析，Zipkin/Sleuth 进行分布式链路追踪。
6.  **异步处理**：利用 Spring 的 `@Async` 注解、CompletableFuture 或响应式编程 (WebFlux) 处理耗时操作，避免阻塞主线程，提高系统吞吐量。
7.  **Agent 工作流引擎**：设计一个灵活的规则引擎或状态机来编排 Agent 的行为，支持动态配置和扩展新的 Agent 能力。
8.  **Prompt Engineering**：在 LLM 集成服务中，对 Prompt 进行精细化管理和优化，以提高 AI 回答的质量和准确性。
9.  **RAG (Retrieval-Augmented Generation)**：通过向量数据库和知识管理服务，实现基于检索增强的生成，确保 AI 回答的知识性和时效性。

### 7.4 开发流程

1.  **环境搭建**：Java SDK, Gradle, IDE (IntelliJ IDEA)。
2.  **项目初始化**：使用 Spring Initializr 创建 Spring Boot 项目。
3.  **依赖管理**：配置 `build.gradle` 文件，引入所需依赖。
4.  **代码开发**：按照模块划分进行开发，遵循编码规范。
5.  **单元测试与集成测试**：使用 JUnit, Mockito, Spring Boot Test 进行测试。
6.  **部署**：构建 Docker 镜像，部署到 Kubernetes 或其他云平台。

通过上述后端设计，xdream-agent 将构建一个强大、稳定、可扩展的后端系统，为前端应用和 AI 核心功能提供坚实支撑。



## 8. 结论

本程序设计说明书详细阐述了 xdream-agent 智能体的技术实现方案，涵盖了功能模块、系统架构、API 接口、数据库设计、前端和后端模块设计。通过采用微服务架构、React 前端、Spring Boot 后端、H2/PostgreSQL 数据库以及向量/图数据库的组合，xdream-agent 将能够构建一个高性能、可扩展、智能化的 AI 聊天问题解答平台。我们相信，遵循本 PDS 进行开发，将有助于高效地实现产品目标，并为用户提供卓越的 AI 交互体验。

## 9. 附录

### 9.1 术语表

*   **PDS (Program Design Specification)**：程序设计说明书，详细描述软件系统的技术实现方案。
*   **PRD (Product Requirements Document)**：产品需求说明书，详细描述产品的功能、目的和行为。
*   **RESTful API**：一种设计风格，用于网络应用程序的接口设计。
*   **JWT (JSON Web Token)**：一种开放标准，用于在各方之间安全地传输信息。
*   **UUID (Universally Unique Identifier)**：通用唯一标识符。
*   **RAG (Retrieval-Augmented Generation)**：检索增强生成，结合信息检索和文本生成的技术。
*   **DDD (Domain-Driven Design)**：领域驱动设计，一种软件开发方法，强调将业务领域模型与软件设计紧密结合。
*   **WebSocket**：一种在单个 TCP 连接上进行全双工通信的协议。
*   **H2**：一个用 Java 编写的开源关系型数据库，可以嵌入到应用程序中或作为独立的数据库服务器运行。
*   **PostgreSQL**：一个功能强大的开源对象关系型数据库系统。
*   **Pinecone/Milvus**：向量数据库，用于存储和检索高维向量数据。
*   **Neo4j/ArangoDB**：图数据库，用于存储和查询图结构数据。
*   **Redis**：一个开源的内存数据结构存储，可用作数据库、缓存和消息代理。
*   **Spring Boot**：一个用于简化 Spring 应用程序开发的框架。
*   **React**：一个用于构建用户界面的 JavaScript 库。
*   **Gradle**：一个自动化构建工具，用于多语言项目构建。

### 9.2 参考资料

*   [xdream-agent 产品需求说明书](xdream_agent_prs.md)
*   [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
*   [React 官方文档](https://react.dev/)
*   [H2 Database 官方网站](https://www.h2database.com/html/main.html)
*   [PostgreSQL 官方网站](https://www.postgresql.org/)
*   [JWT 官方网站](https://jwt.io/)
*   [RESTful API 设计指南](https://restfulapi.net/)


