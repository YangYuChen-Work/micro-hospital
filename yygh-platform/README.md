# 智医通 - 预约挂号统一平台 (yygh-platform)

智慧医疗全栈解决方案，基于 Spring Cloud 微服务架构的在线预约挂号平台。

## 项目结构

```
yygh-platform/
├── backend/yygh_parent/              # 后端微服务
│   ├── model/                         # 数据模型（实体、VO、枚举）
│   ├── common/                        # 通用模块
│   │   ├── service_utils/             # 工具类（Result、JwtHelper、异常处理）
│   │   └── rabbit_util/               # RabbitMQ 常量与配置
│   ├── service/                       # 业务微服务
│   │   ├── service_hosp/              # 医院服务（排班、科室、医院CRUD）
│   │   ├── service_orders/            # 订单服务（下单、取消、查询、支付）
│   │   ├── service_user/              # 用户服务（就诊人管理、实名认证）
│   │   ├── service_msm/               # 短信服务（验证码、预约通知）
│   │   ├── service_oss/               # OSS 文件上传
│   │   ├── service_cmn/               # 字典管理
│   │   ├── service_task/              # 定时任务（预约提醒）
│   │   ├── service_statistics/        # 统计服务
│   │   └── service_help/              # 帮助中心
│   ├── service_client/                # OpenFeign 客户端
│   └── service_gateway/               # API 网关
└── frontend/yygh-sitedemo/            # Nuxt.js 前端
    ├── pages/                         # 页面
    │   ├── hospital/                  # 医院相关页面
    │   │   ├── _hoscode.vue           # 医院首页（科室选择）
    │   │   ├── schedule.vue           # 排班日期/号源
    │   │   ├── booking.vue            # 确认挂号
    │   │   ├── detail/_hoscode.vue    # 医院详情
    │   │   ├── notice/_hoscode.vue    # 预约须知
    │   │   ├── suspend/_hoscode.vue   # 停诊信息
    │   │   └── query/_hoscode.vue     # 查询/取消
    │   ├── order/                     # 订单页面
    │   │   └── show.vue               # 订单详情
    │   ├── patient/                   # 就诊人管理
    │   └── index.vue                  # 平台首页
    ├── api/                           # API 封装
    ├── components/                    # 通用组件
    └── assets/                        # 静态资源
```

## 核心功能模块

### 预约挂号流程
1. 首页选择医院 → 2. 选择科室 → 3. 选择日期和医生 → 4. 确认信息 → 5. 下单支付 → 6. 按时取号

### 预约通知 (Appointment Notification)
- **定时提醒**：`service_task` 每 10 秒扫描次日订单，通过 RabbitMQ 异步发送短信提醒就诊人
- **下单通知**：挂号成功后立即发送预约成功短信（含医院、科室、医生、日期、退号截止时间）
- **取消通知**：取消预约后发送取消确认短信
- **短信模板**：使用国阳云短信 API，支持模板参数化发送
- 关键文件：
  - `backend/.../service_task/.../ScheduleTask.java` — 定时任务入口
  - `backend/.../service_orders/.../impl/OrderInfoServiceImpl.java` — `patientTips()` 预约提醒
  - `backend/.../service_msm/.../impl/MsmServiceImpl.java` — 短信发送（验证码+模板双模式）

### 停诊信息 (Suspension Information)
- **停诊查询**：根据医院编号查询状态为"停诊"的排班列表
- **信息展示**：科室、医生、职称、日期、时段（上午/下午）
- **数据来源**：MongoDB `Schedule` 集合，`status = -1` 即为停诊
- 关键文件：
  - `backend/.../hosp/controller/api/HospitalApiController.java` — `GET /auth/getSuspendSchedule/{hoscode}`
  - `backend/.../hosp/service/impl/ScheduleServiceImpl.java` — `getSuspendScheduleList()`
  - `frontend/.../pages/hospital/suspend/_hoscode.vue` — 停诊信息页面

### 查询/取消 (Query & Cancellation)
- **订单查询**：输入手机号 + 就诊人姓名查询历史订单
- **在线取消**：在退号截止时间前可在线取消预约，取消后号源自动释放
- **订单状态**：待支付(0) → 已支付(1) → 已取号(2)，取消(-1)
- 关键文件：
  - `backend/.../orders/controller/OrderInfoController.java` — `POST /auth/searchOrders`
  - `backend/.../orders/service/impl/OrderInfoServiceImpl.java` — `searchOrders()`, `cancelOrder()`
  - `frontend/.../pages/hospital/query/_hoscode.vue` — 查询/取消页面

### 预约须知 (Appointment Notice)
- **预约规则**：展示医院预约周期、放号时间、停挂时间、退号规则
- **预约流程**：5 步可视化流程指引
- **注意事项**：取号、退号、实名制等重要提示
- 关键文件：
  - `frontend/.../pages/hospital/notice/_hoscode.vue` — 预约须知页面
  - 数据来自 `HospitalApiController.show()` 返回的 `BookingRule`

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot + Spring Cloud |
| 微服务组件 | Nacos（注册/配置）、OpenFeign（RPC）、Sentinel（限流） |
| 数据库 | MySQL（订单/用户）、MongoDB（医院/科室/排班） |
| 消息队列 | RabbitMQ（订单异步处理、短信发送） |
| 前端框架 | Nuxt.js (Vue 2) + Element UI |
| 短信服务 | 国阳云 SMS API |

## 快速启动

### 后端
```bash
cd backend/yygh_parent
mvn clean install -DskipTests
# 依次启动：Nacos → 各微服务模块
```

### 前端
```bash
cd frontend/yygh-sitedemo
npm install
npm run dev
```

## API 概览

| 模块 | 接口 | 说明 |
|------|------|------|
| 预约下单 | `POST /api/order/orderInfo/auth/submitOrder/{scheduleId}/{patientId}` | 提交预约 |
| 取消预约 | `GET /api/order/orderInfo/auth/cancelOrder/{orderId}` | 取消预约 |
| 订单查询 | `POST /api/order/orderInfo/auth/searchOrders` | 手机号+姓名查订单 |
| 订单详情 | `GET /api/order/orderInfo/auth/getOrders/{orderId}` | 单个订单详情 |
| 停诊查询 | `GET /api/hosp/hospital/auth/getSuspendSchedule/{hoscode}` | 医院停诊排班 |
| 排班查询 | `GET /api/hosp/hospital/auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}` | 可预约日期 |
| 号源查询 | `GET /api/hosp/hospital/auth/findScheduleList/{hoscode}/{depcode}/{workDate}` | 指定日期号源 |
