# 积分商城服务 (mall-service)

## 项目介绍
积分商城服务是一个基于Spring Boot和MyBatis-Plus的微服务，提供积分管理、商品管理和订单管理等功能。

## 技术栈
- Spring Boot
- Spring Cloud Alibaba Nacos
- MyBatis-Plus
- MySQL
- Redis

## 核心功能

### 1. 积分管理
- 查询用户当前积分
- 查询用户积分记录
- 统计用户总积分获取和消耗

### 2. 商品管理
- 获取上架商品列表
- 根据ID获取商品详情
- 根据分类查询商品
- 搜索商品

### 3. 订单管理
- 创建订单（积分兑换商品）
- 获取用户订单列表
- 查询订单详情
- 取消订单
- 完成订单

## API接口文档

### 积分相关接口
```
GET /api/mall/points/current/{userId} - 获取用户当前积分
GET /api/mall/points/record/{userId} - 获取用户积分记录
GET /api/mall/points/total/gain/{userId} - 获取用户总积分获取
GET /api/mall/points/total/consume/{userId} - 获取用户总积分消耗
```

### 商品相关接口
```
GET /api/mall/products - 获取所有上架商品
GET /api/mall/products/{id} - 根据ID获取商品详情
GET /api/mall/products/category/{category} - 根据分类获取商品
GET /api/mall/products/search?keyword=xxx - 搜索商品
```

### 订单相关接口
```
POST /api/mall/orders - 创建订单
GET /api/mall/orders/user/{userId} - 获取用户订单列表
GET /api/mall/orders/{orderId}/user/{userId} - 获取订单详情
PUT /api/mall/orders/{orderId}/cancel - 取消订单
PUT /api/mall/orders/{orderId}/complete - 完成订单（管理员接口）
```

## 项目结构
```
src/main/java/com/agriculture/mall/
├── MallServiceApplication.java  // 启动类
├── controller/                  // 控制器
│   ├── PointsController.java    // 积分控制器
│   ├── ProductController.java   // 商品控制器
│   └── OrderController.java     // 订单控制器
├── service/                     // 服务层
│   ├── PointsRecordService.java // 积分记录服务接口
│   ├── MallProductService.java  // 商品服务接口
│   ├── MallOrderService.java    // 订单服务接口
│   └── impl/                    // 服务实现
├── mapper/                      // Mapper层
│   ├── PointsRecordMapper.java  // 积分记录Mapper
│   ├── MallProductMapper.java   // 商品Mapper
│   ├── MallOrderMapper.java     // 订单Mapper
│   └── UserMapper.java          // 用户Mapper（积分相关）
└── entity/                      // 实体类
    ├── PointsRecord.java        // 积分记录实体
    ├── MallProduct.java         // 商品实体
    └── MallOrder.java           // 订单实体
```

## 数据库表结构
服务依赖以下数据库表：
- sys_user - 用户表（包含points字段）
- points_record - 积分记录表
- mall_product - 商品表
- mall_order - 订单表

## 配置说明
主要配置文件：`src/main/resources/application.yml`
- 服务端口：8007
- Nacos注册中心配置
- MySQL数据库配置
- Redis配置

## 部署说明
1. 确保Nacos服务、MySQL和Redis已启动
2. 在项目根目录执行 `mvn clean package` 打包项目
3. 运行生成的jar包：`java -jar mall-service-1.0-SNAPSHOT.jar`

## 注意事项
1. 订单创建和取消操作涉及事务处理，确保积分和库存的一致性
2. 商品库存不足或用户积分不足时会返回相应的错误信息
3. 取消订单会自动返还用户积分和恢复商品库存