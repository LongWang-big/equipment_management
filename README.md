# 设备管理系统 (Equipment Management System)

基于 Spring Boot + Vue 3 的设备管理系统，实现设备信息管理、库存管理、采购管理、出入库记录、设备报损报废等功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Vue Router |
| 后端 | Java 17 + Spring Boot 3.2.5 + Spring Security + JWT |
| ORM | MyBatis-Plus 3.5.6 |
| 数据库 | MySQL 8.x |
| API 文档 | Knife4j (Swagger/OpenAPI 3) |

## 项目结构

```
keshe/
├── pom.xml                              # Maven 父工程
├── equipment_management/                # 后端模块
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/xxx/equipment/
│       │   ├── EquipmentManagementApplication.java   # 启动类
│       │   │
│       │   ├── common/                                # 公共组件
│       │   │   ├── Result.java                        #   统一响应封装
│       │   │   ├── ResultCode.java                    #   响应状态码
│       │   │   ├── JwtUtil.java                       #   JWT 工具类
│       │   │   ├── annotation/
│       │   │   │   └── OperationLog.java              #   操作日志注解
│       │   │   └── aspect/
│       │   │       └── OperationLogAspect.java        #   操作日志切面
│       │   │
│       │   ├── config/                                # 配置类
│       │   │   ├── SecurityConfig.java                #   Spring Security 配置
│       │   │   ├── CorsConfig.java                    #   跨域配置
│       │   │   ├── MybatisPlusConfig.java             #   MyBatis-Plus 分页插件
│       │   │   └── Knife4jConfig.java                 #   API 文档配置
│       │   │
│       │   ├── entity/                                # 实体类（对应数据库表）
│       │   │   ├── SysUser.java                       #   用户表 sys_user
│       │   │   ├── Device.java                        #   设备表 device
│       │   │   ├── DeviceCategory.java                #   设备分类表 device_category
│       │   │   ├── Inventory.java                     #   库存表 inventory
│       │   │   ├── StockRecord.java                   #   出入库记录表 stock_record
│       │   │   ├── PurchaseOrder.java                 #   采购订单表 purchase_order
│       │   │   ├── InventoryCheck.java                #   库存盘点表 inventory_check
│       │   │   ├── DeviceScrap.java                   #   设备报损报废表 device_scrap
│       │   │   └── OperationLog.java                  #   操作日志表 operation_log
│       │   │
│       │   ├── dto/                                   # 数据传输对象（接收前端参数）
│       │   │   ├── LoginDTO.java                      #   登录请求
│       │   │   ├── RegisterDTO.java                   #   注册请求
│       │   │   ├── UserDTO.java                       #   用户新增/修改
│       │   │   ├── DeviceDTO.java                     #   设备新增/修改
│       │   │   ├── CategoryDTO.java                   #   设备分类新增/修改
│       │   │   ├── StockInDTO.java                    #   入库
│       │   │   ├── StockOutDTO.java                   #   出库
│       │   │   ├── StockReturnDTO.java                #   归还
│       │   │   ├── PurchaseOrderDTO.java              #   采购订单
│       │   │   ├── InventoryCheckDTO.java             #   库存盘点
│       │   │   └── ScrapDTO.java                      #   报损报废
│       │   │
│       │   ├── vo/                                    # 视图对象（返回前端数据）
│       │   │   ├── LoginVO.java                       #   登录响应（含 Token）
│       │   │   ├── UserVO.java                        #   用户信息
│       │   │   ├── DeviceVO.java                      #   设备信息
│       │   │   ├── CategoryVO.java                    #   设备分类
│       │   │   ├── InventoryVO.java                   #   库存信息
│       │   │   ├── StockRecordVO.java                 #   出入库记录
│       │   │   ├── PurchaseOrderVO.java               #   采购订单
│       │   │   ├── PurchaseReportVO.java              #   采购报表
│       │   │   ├── InventoryCheckVO.java              #   盘点记录
│       │   │   └── ScrapVO.java                       #   报损报废记录
│       │   │
│       │   ├── mapper/                                # MyBatis-Plus Mapper 接口
│       │   │   ├── SysUserMapper.java
│       │   │   ├── DeviceMapper.java
│       │   │   ├── DeviceCategoryMapper.java
│       │   │   ├── InventoryMapper.java
│       │   │   ├── StockRecordMapper.java
│       │   │   ├── PurchaseOrderMapper.java
│       │   │   ├── PurchaseReportMapper.java
│       │   │   ├── InventoryCheckMapper.java
│       │   │   ├── DeviceScrapMapper.java
│       │   │   └── OperationLogMapper.java
│       │   │
│       │   ├── service/                               # 业务逻辑层（接口）
│       │   │   ├── LoginService.java
│       │   │   ├── UserService.java
│       │   │   ├── DeviceService.java
│       │   │   ├── DeviceCategoryService.java
│       │   │   ├── InventoryService.java
│       │   │   ├── StockRecordService.java
│       │   │   ├── PurchaseOrderService.java
│       │   │   ├── PurchaseReportService.java
│       │   │   ├── DeviceScrapService.java
│       │   │   └── impl/                             #   实现类
│       │   │       ├── LoginServiceImpl.java
│       │   │       ├── UserServiceImpl.java
│       │   │       ├── DeviceServiceImpl.java
│       │   │       ├── DeviceCategoryServiceImpl.java
│       │   │       ├── InventoryServiceImpl.java
│       │   │       ├── StockRecordServiceImpl.java
│       │   │       ├── PurchaseOrderServiceImpl.java
│       │   │       ├── PurchaseReportServiceImpl.java
│       │   │       └── DeviceScrapServiceImpl.java
│       │   │
│       │   ├── controller/                            # 控制器层（API 接口）
│       │   │   ├── LoginController.java               #   POST /login, POST /register
│       │   │   ├── UserController.java                #   用户管理 CRUD
│       │   │   ├── DeviceController.java              #   设备管理 CRUD
│       │   │   ├── DeviceCategoryController.java      #   设备分类 CRUD
│       │   │   ├── InventoryController.java           #   库存管理
│       │   │   ├── StockRecordController.java         #   出入库记录
│       │   │   ├── PurchaseOrderController.java       #   采购订单
│       │   │   ├── PurchaseReportController.java      #   采购报表
│       │   │   └── DeviceScrapController.java         #   报损报废
│       │   │
│       │   ├── filter/
│       │   │   └── JwtAuthenticationFilter.java       # JWT 认证过滤器
│       │   │
│       │   └── exception/
│       │       ├── BusinessException.java             #   业务异常
│       │       └── GlobalExceptionHandler.java        #   全局异常处理
│       │
│       └── resources/
│           ├── application.yml                        # 主配置文件
│           ├── application-dev.yml                    # 开发环境配置
│           ├── application-prod.yml                   # 生产环境配置
│           ├── db/
│           │   └── schema.sql                         # 数据库建表 + 初始数据
│           └── mapper/                                # MyBatis XML 映射文件
│               ├── InventoryMapper.xml
│               ├── PurchaseOrderMapper.xml
│               └── PurchaseReportMapper.xml
│
└── frontend/                              # 前端模块
    ├── package.json
    ├── vite.config.js                     # Vite 配置（含代理）
    └── src/
        ├── main.js                        # 入口文件
        ├── App.vue                        # 根组件
        │
        ├── api/                           # API 请求封装
        │   ├── auth.js                    #   登录/注册
        │   ├── user.js                    #   用户管理
        │   ├── device.js                  #   设备管理
        │   ├── category.js                #   设备分类
        │   ├── inventory.js               #   库存管理
        │   ├── stockRecord.js             #   出入库记录
        │   ├── purchase.js                #   采购管理
        │   └── scrap.js                   #   报损报废
        │
        ├── router/
        │   └── index.js                   # 路由配置
        │
        ├── stores/                        # Pinia 状态管理
        │   ├── auth.js                    #   认证状态（Token/用户信息）
        │   └── app.js                     #   全局应用状态
        │
        ├── utils/
        │   └── request.js                 # Axios 封装（请求/响应拦截器）
        │
        ├── layouts/
        │   └── MainLayout.vue             # 主布局（侧边栏 + 顶栏 + 内容区）
        │
        ├── views/                         # 页面组件
        │   ├── login/
        │   │   ├── Login.vue              #   登录页
        │   │   └── Register.vue           #   注册页
        │   ├── dashboard/
        │   │   └── Dashboard.vue          #   首页仪表盘
        │   ├── device/
        │   │   └── Device.vue             #   设备管理
        │   ├── category/
        │   │   └── Category.vue           #   设备分类管理
        │   ├── inventory/
        │   │   └── Inventory.vue          #   库存管理
        │   ├── stock-record/
        │   │   └── StockRecord.vue        #   出入库记录
        │   ├── purchase/
        │   │   └── Purchase.vue           #   采购管理
        │   ├── scrap/
        │   │   └── Scrap.vue              #   报损报废管理
        │   └── user/
        │       └── User.vue               #   用户管理（仅管理员）
        │
        └── assets/
            └── styles/
                └── global.css             # 全局样式
```

## 数据库表结构

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表（admin/user 角色） |
| `device_category` | 设备分类表 |
| `device` | 设备信息表 |
| `inventory` | 库存表 |
| `stock_record` | 出入库记录表 |
| `purchase_order` | 采购订单表 |
| `inventory_check` | 库存盘点表 |
| `device_scrap` | 设备报损报废表 |
| `operation_log` | 操作日志表 |

## 角色权限

| 角色 | 权限 |
|------|------|
| `admin`（管理员） | 全部功能：设备管理、用户管理、删除操作、采购、报损报废审批等 |
| `user`（普通用户） | 查看设备/库存/记录，新增和修改设备信息，不能删除、不能管理用户 |

## 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | `admin123` | 管理员 |

## 快速启动

### 1. 数据库

```sql
-- 执行建表脚本
source equipment_management/src/main/resources/db/schema.sql
```

### 2. 后端

```bash
cd equipment_management
mvn spring-boot:run
# 启动后访问 API 文档: http://localhost:8080/doc.html
```

### 3. 前端

```bash
cd frontend
npm install
npm run dev
# 启动后访问: http://localhost:5173
```

## API 接口概览

| 模块 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| 认证 | POST | `/login` | 用户登录 | 公开 |
| 认证 | POST | `/register` | 用户注册 | 公开 |
| 用户 | GET | `/user/page` | 分页查询用户 | 已登录 |
| 用户 | POST | `/user` | 新增用户 | 管理员 |
| 用户 | PUT | `/user` | 修改用户 | 管理员 |
| 用户 | DELETE | `/user/{id}` | 删除用户 | 管理员 |
| 用户 | PUT | `/user/change-password` | 修改密码 | 已登录 |
| 设备 | GET | `/device/page` | 分页查询设备 | 已登录 |
| 设备 | GET | `/device/{id}` | 查询设备详情 | 已登录 |
| 设备 | POST | `/device` | 新增设备 | 已登录 |
| 设备 | PUT | `/device` | 修改设备 | 已登录 |
| 设备 | DELETE | `/device/{id}` | 删除设备 | 管理员 |
| 分类 | GET | `/category/page` | 分页查询分类 | 已登录 |
| 分类 | POST | `/category` | 新增分类 | 已登录 |
| 分类 | PUT | `/category` | 修改分类 | 已登录 |
| 分类 | DELETE | `/category/{id}` | 删除分类 | 管理员 |
| 库存 | GET | `/inventory/page` | 分页查询库存 | 已登录 |
| 出入库 | POST | `/stock-record/in` | 设备入库 | 已登录 |
| 出入库 | POST | `/stock-record/out` | 设备出库 | 已登录 |
| 出入库 | POST | `/stock-record/return` | 设备归还 | 已登录 |
| 出入库 | GET | `/stock-record/page` | 分页查询记录 | 已登录 |
| 采购 | GET | `/purchase/page` | 分页查询采购单 | 已登录 |
| 采购 | POST | `/purchase` | 新增采购单 | 已登录 |
| 采购 | PUT | `/purchase` | 修改采购单 | 已登录 |
| 采购报表 | GET | `/purchase-report/page` | 采购报表 | 已登录 |
| 报损报废 | GET | `/scrap/page` | 分页查询记录 | 已登录 |
| 报损报废 | POST | `/scrap` | 新增报损报废 | 已登录 |
| 报损报废 | PUT | `/scrap` | 修改报损报废 | 已登录 |
| 盘点 | POST | `/inventory/check` | 新增盘点 | 已登录 |
| 盘点 | GET | `/inventory/check/page` | 分页查询盘点 | 已登录 |
