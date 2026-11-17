# 山野同行 · 户外运动伴游预约平台

基于 Spring Boot + Vue 3 的前后端分离毕业设计项目，面向徒步、骑行、登山、露营、攀岩、溯溪等户外场景，提供“活动发布 - 预约下单 - 在线支付 - 行程完成 - 评价反馈”的完整闭环。

## 功能模块

- 用户端：注册登录、活动浏览与筛选、活动详情、在线预约、模拟支付、取消订单、完成评价、消息通知、个人资料。
- 领队端：领队申请、资质审核状态、活动发布/编辑/删除、预约订单查看、确认行程完成、领队资料维护。
- 管理端：运营数据概览、订单状态分布、活动类型分布、领队审核、用户启用/禁用、活动上下架、全量订单查看。
- 通用能力：JWT 登录认证、角色权限校验、统一响应格式、全局异常处理、分页查询、站内消息、OpenAPI 接口文档。

## 技术栈

| 层次 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 2.7、MyBatis-Plus 3.5、JWT、Hutool、Lombok |
| 前端 | Vue 3、Vite 5、Vue Router 4、Element Plus、Axios |
| 数据库 | MySQL 8.0 |
| 构建 | Maven、npm |

## 项目结构

```text
outdoor-companion-platform/
├── outdoor-server/          # Spring Boot 后端
│   └── src/main/java/com/outdoor/server/
│       ├── controller/      # REST 接口
│       ├── service/         # 业务逻辑
│       ├── mapper/          # MyBatis-Plus Mapper
│       ├── entity/          # 数据库实体
│       ├── dto/ vo/         # 请求与响应模型
│       ├── config/          # 鉴权、跨域、异常处理
│       └── util/            # JWT 工具
├── outdoor-web/             # Vue 3 前端
│   └── src/
│       ├── views/           # 页面
│       ├── components/      # 布局与卡片组件
│       ├── api/             # 接口封装
│       ├── stores/          # 登录状态
│       └── utils/           # 格式化工具
└── docs/
    ├── schema.sql           # 建库建表
    └── data.sql             # 演示数据
```

## 快速启动

### 1. 初始化数据库

确保本地 MySQL 8.0 已启动，然后依次执行：

```bash
mysql -uroot -p < docs/schema.sql
mysql -uroot -p outdoor_db < docs/data.sql
```

如果使用 Navicat / DataGrip，也可以直接打开并运行 `docs/schema.sql`、`docs/data.sql`。

### 2. 配置数据库连接（密钥不进版本库）

敏感配置已全部改为环境变量占位，仓库里**不含**真实密码：

```yaml
# application.yml（已提交）
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/outdoor_db?...}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}
jwt:
  secret: ${JWT_SECRET:...}
```

本地开发推荐用 profile 文件，不影响别人、也不会被提交：

```bash
cd outdoor-server/src/main/resources
copy application-local.yml.example application-local.yml   # Windows
cp    application-local.yml.example application-local.yml  # macOS/Linux
```

然后把 `application-local.yml` 里的数据库密码、JWT 密钥改成你自己的（该文件已被 `.gitignore` 忽略）。

也可以直接用环境变量启动：

```bash
set DB_PASSWORD=你的密码
set JWT_SECRET=至少32位的随机字符串
```

### 3. 启动后端

```bash
cd outdoor-server
mvn clean package -DskipTests
java -jar target/outdoor-server-1.0.0.jar
```

后端默认运行在 `http://localhost:8080`。

Swagger 接口文档：`http://localhost:8080/swagger-ui/index.html`

### 4. 启动前端

```bash
cd outdoor-web
npm install
npm run dev
```

浏览器访问 `http://localhost:5173`。Vite 已配置 `/api` 代理到后端 8080 端口。

## 演示账号

所有演示账号密码均为 `123456`。

| 角色 | 账号 | 说明 |
| --- | --- | --- |
| 管理员 | `admin` | 审核领队、管理用户/活动/订单 |
| 认证领队 | `guide_lin` | 林川，杭州，徒步/露营/登山 |
| 认证领队 | `guide_chen` | 陈野，上海，骑行 |
| 认证领队 | `guide_zhou` | 周岚，广州，攀岩/溯溪 |
| 普通用户 | `user_xia` | 已有历史订单与评价 |
| 普通用户 | `user_ming` | 可体验完整预约流程 |

## 加分项

- OpenAPI/Swagger 在线接口文档，支持 JWT Bearer 授权调试。
- 管理端运营分析：订单状态分布、活动类型分布可视化。
- JWT 工具单元测试，覆盖 Token 生成与用户信息解析。
- 统一接口错误页与前端启动引导，改善演示时的异常访问体验。

## 说明

- 支付为毕业设计演示用的模拟支付，调用 `/api/booking/pay/{id}` 即视为支付成功，不接入真实支付渠道。
- 活动封面使用 Unsplash 在线图片；无网络时会显示内置渐变背景，不影响功能。
- JWT 密钥、数据库密码等敏感配置仅用于本地演示，正式部署请改为环境变量。
- 领队审核通过后，建议重新登录一次，使前端菜单与角色信息同步。
