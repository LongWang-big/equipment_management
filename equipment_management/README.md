# 设备管理系统

基于 Spring Boot 3 + MyBatis-Plus 的设备管理系统，提供设备管理、库存管理、出入库操作、库存盘点、采购订单管理及采购报表统计等功能。

## 技术栈

| 层次 | 技术 |
|------|------|
| 框架 | Spring Boot 3.2.5 |
| ORM | MyBatis-Plus 3.5.6 |
| 数据库 | MySQL 8.x |
| 认证 | Spring Security + JWT |
| 文档 | Knife4j (OpenAPI 3) |
| 构建 | Maven |

## 项目结构

```
src/main/java/com/xxx/equipment/
├── common/            # 通用类（Result、JwtUtil、注解、AOP）
├── config/            # 配置类（Security、CORS、MyBatis-Plus、Knife4j）
├── controller/        # 控制器层
├── dto/               # 数据传输对象（入参校验）
├── entity/            # 实体类（对应数据库表）
├── exception/         # 异常处理
├── filter/            # JWT 认证过滤器
├── mapper/            # MyBatis-Plus Mapper 接口
├── service/           # 服务层接口
│   └── impl/          # 服务层实现
└── vo/                # 视图对象（出参）
```

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.x
- Maven 3.8+

### 2. 初始化数据库

在 MySQL 中执行建表脚本：

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

### 3. 修改配置

开发环境配置文件：`src/main/resources/application-dev.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/equipment_management
    username: root
    password: 123456
```

### 4. 启动项目

```bash
mvn spring-boot:run
```

### 5. 访问接口文档

启动成功后访问 Knife4j 文档：

```
http://localhost:8080/doc.html
```

## 功能模块

### 用户认证
- `POST /login` — 用户登录，返回 JWT Token

### 设备管理
- `POST /device` — 新增设备
- `PUT /device` — 修改设备
- `DELETE /device/{id}` — 删除设备（有关联数据时禁止删除）
- `GET /device/{id}` — 查询设备详情
- `GET /device/page` — 分页查询设备

### 库存管理
- `GET /inventory/page` — 分页查询库存
- `GET /inventory/warning` — 分页查询库存预警
- `POST /inventory/in` — 设备入库
- `POST /inventory/out` — 设备出库
- `POST /inventory/return` — 设备退库
- `POST /inventory/check` — 设备盘点（自动修正库存）

### 采购订单
- `POST /purchase-order` — 新增采购订单（自动计算总金额）
- `PUT /purchase-order` — 修改采购订单
- `DELETE /purchase-order/{id}` — 删除采购订单
- `GET /purchase-order/{id}` — 查询采购订单详情
- `GET /purchase-order/page` — 分页查询采购订单

### 采购报表
- `GET /report/purchase/month?year=2026` — 按月统计采购金额
- `GET /report/purchase/year?year=2026` — 按年统计采购金额

## 认证说明

除登录接口和 Knife4j 文档外，所有接口均需携带 JWT Token：

```
Authorization: Bearer <token>
```
