# BaseFrame2

基于 **Spring Boot 2.6.15 + Shiro + MyBatis-Plus** 搭建的企业级基础框架，采用多模块分层架构设计，提供稳定、可扩展的技术底座。

## 项目架构

### 模块结构

```
baseProject/
├── base/           # 基础模块（核心工具、通用组件、安全框架）
├── service/        # 业务服务模块（用户、权限等业务逻辑）
├── system/         # 系统入口模块（主启动类、配置、接口文档）
└── upload/         # 文件上传模块
```

### 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.6.15 | 核心框架 |
| Shiro | 1.13.0 | 权限认证 |
| MyBatis-Plus | 3.5.7 | ORM框架 |
| MySQL | 8.2.0 | 数据库 |
| Redis | - | 缓存/会话存储 |
| Redisson | 3.34.0 | 分布式锁 |
| Knife4j | 2.0.9 | API文档 |
| Arthas | 3.6.7 | 在线诊断 |
| Fastjson2 | 2.0.52 | JSON处理 |
| Hutool | 5.8.31 | 工具库 |
| EasyExcel | 3.3.4 | Excel处理 |
| Java-JWT | 4.4.0 | Token生成 |

### 核心特性

- **权限认证**：Shiro + JWT Token，支持Redis分布式会话
- **多环境配置**：dev/prod环境隔离，配置文件动态加载
- **动态数据源**：支持主从配置，读写分离
- **API文档**：集成Knife4j，自动生成Swagger文档
- **监控诊断**：集成Arthas，支持在线调试
- **日志系统**：操作日志自动记录，支持审计
- **定时任务**：基于Spring Schedule，支持动态管理
- **文件处理**：支持PDF生成、Markdown解析、图片验证码

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+

### 配置数据库

修改 `system/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    dynamic:
      datasource:
        mysql:
          url: jdbc:mysql://localhost:3306/your_db?characterEncoding=UTF-8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai
          username: root
          password: your_password
```

### 启动应用

主启动类：`system/src/main/java/work/chncyl/system/SystemApplication.java`

```bash
# 开发环境（默认）
mvn spring-boot:run -pl system

# 或打包后运行
mvn clean package -Pdev
java -jar system/target/system-0.0.1-SNAPSHOT.jar
```

### 访问接口文档

启动后访问：`http://localhost:8665/api/doc.html`

## 核心功能模块

### 1. 认证授权（system/authentication）

- 登录/登出
- Token验证
- 用户信息管理
- 支持多设备登录配置

### 2. 权限管理（service/permission）

- 角色管理
- 权限（菜单）管理
- 角色-权限关联
- 用户-角色关联

### 3. 用户管理（service/user）

- 用户信息CRUD
- 账户状态管理
- 账户类型区分
- 密码安全策略

### 4. 字典管理（system/dictionary）

- 字典类型管理
- 字典数据树形结构
- 前端下拉框数据源

### 5. 系统日志（base/syslog）

- 操作日志自动记录（@AutoLog注解）
- 日志查询与导出
- 异常日志捕获

### 6. 定时任务（base/task）

- 任务调度管理
- 支持Cron表达式
- 任务启停控制
- 执行日志查看

## 常用注解

| 注解 | 说明 | 使用场景 |
|------|------|----------|
| `@AutoLog` | 自动记录操作日志 | Controller方法 |
| `@AllowAnonymous` | 允许匿名访问 | Controller类/方法 |
| `@Disabled` | 禁用接口（返回404） | Controller类/方法 |
| `@CurrentUser` | 注入当前登录用户 | Controller参数 |

## 项目特点

### 1. 分层清晰

- **base模块**：独立可复用，包含工具类、安全框架、通用实体
- **service模块**：纯业务逻辑，不依赖Web层
- **system模块**：Web入口，整合所有模块

### 2. 安全完善

- JWT Token无状态认证
- Shiro权限控制（URL级、方法级）
- Redis缓存权限数据，减少DB压力
- 密码加密存储（RSA + BCrypt）
- 密码错误锁定机制

### 3. 开发便捷

- 统一返回格式（`ApiResult<T>`）
- 分页查询封装（`PagedInputPojo`、`PagedResultPojo`）
- 通用CRUD接口（基于MyBatis-Plus）
- 自动填充字段（创建人、创建时间等）
- 逻辑删除支持

### 4. 配置灵活

- 多环境配置（dev/prod）
- 动态数据源切换
- 自定义ID生成器（雪花算法变体）
- 可配置的密码策略
- 可关闭的验证码

## 目录结构说明

### base模块核心包

```
work.chncyl.base.global/
├── Constants.java                    # 全局常量
├── enums/                            # 全局枚举
│   ├── AccountStatus.java
│   ├── CacheConstant.java
│   ├── ColumnType.java
│   ├── LogType.java
├── pojo/                             # 通用实体
│   ├── ColumnInfo.java
│   ├── EntityDto.java
│   ├── PagedInputPojo.java
│   ├── PagedResultPojo.java
│   ├── TableInfo.java
├── redis/                            # Redis配置与工具
│   ├── RedisConfig.java
│   ├── RedisUtils.java
├── security/                         # 安全框架
│   ├── CustomerRealm.java
│   ├── JwtFilter.java
│   ├── config/ShiroConfig.java
│   ├── dto/（JwtToken、JwtClaimDto等）
│   └── utils/（TokenUtil、PermissionCacheUtil）
├── result/                           # 统一返回
│   ├── ApiResult.java
│   └── ResultUtil.java
├── utils/                            # 工具类
│   ├── GlobalUtils.java
│   ├── EncryDecryUtils.java
│   ├── HtmlUtils.java
│   ├── IPUtils.java
│   └── ...
└── syslog/                           # 日志系统
    ├── SysLog.java
    ├── SysLogService.java
    └── mapper/SysLogMapper.java
```

## 数据库设计

项目使用MyBatis-Plus，建议遵循以下规范：

- 表名：小写+下划线（`sys_user`）
- 字段：小写+下划线（`create_time`）
- 主键：`id`（BIGINT，使用ID生成器）
- 逻辑删除：`isDeleted`（0未删除，1已删除）
- 时间字段：`createTime`、`updateTime`、`deleteTime`
- 审计字段：`createBy`、`updateBy`

### 初始SQL

参考根目录 `db.sql` 文件，包含基础表结构。

## 开发建议

1. **新增业务模块**：在 `service` 模块创建对应的 `entity`、`mapper`、`service`、`controller`
2. **权限配置**：在 `service/permission` 模块添加权限记录，关联角色
3. **接口文档**：Controller方法添加Swagger注解（`@Api`、`@ApiOperation`）
4. **日志记录**：需要记录的操作方法添加 `@AutoLog` 注解
5. **异常处理**：全局异常已在 `base` 模块处理，抛出自定义异常即可

## 部署说明

### 生产环境配置

1. 修改 `application-pro.yml`：
    - 数据库连接（建议使用连接池）
    - Redis配置（建议使用哨兵或集群）
    - 关闭Knife4j文档：`knife4j.enable: false`
    - 开启Arthas：`arthas.enabled: true`

2. 打包：
```bash
mvn clean package -Pprod -DskipTests
```

3. 运行：
```bash
java -jar system/target/system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Nginx反向代理配置示例

```nginx
location /api/ {
    proxy_pass http://localhost:8665/api/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
}
```

## 常见问题

### 1. 跨域问题

在 `system` 模块添加跨域配置类，或使用Nginx解决。

### 2. 文件上传大小限制

在 `application.yml` 中配置：
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 100MB
```

### 3. 验证码不显示

检查 `easy-captcha` 依赖是否正常加载，确认Redis可用（验证码存储在Redis）。

## 一些注解的使用

1. `@AutoLog`：自动记录日志注解，标注在Controller层的方法上，使用该注解会自动记录用户操作日志，并将日志存入数据库。
2. `@AllowAnonymous`：允许匿名访问注解，标注在Controller层的类或方法上，使用该注解会允许匿名用户访问该接口/该类下的所有接口。
3. `@Disabled`
   ：禁用接口注解，标注在Controller层的类或方法上，使用该注解会禁用该接口/该类下的所有接口,返回404错误,且会在swagger中隐藏接口,默认在`spring.profiles.active=dev`
   模式下<span style="color:red;font-weight:bold">
   失效</span>,可在`work.chncyl.base.global.security.JwtFilter#isAccessAllowed`中修改。
4. `@CurrentUser`：获取当前登录用户信息注解，标注在Controller层
   `JwtClaimDto`或`LoginedUserInfo`
   参数上，使用该注解会自动注入当前登录用户的JwtClaimDto或LoginedUserInfo对象，且不会在Swagger接口文档中显示该参数。（也可以使用`SessionUtil.getSession()`获取当前登录用户信息）

## 登录接口

1. 登录接口：/login，配合/Captcha验证码接口实现登录功能。
    1. 验证码接口：/Captcha，生成验证码图片，并返回验证码图片的字符数组。
2. 登录接口：/siginSkip，用于快速登录，无需输入验证码。
3. 登录接口（仅限测试使用）：/simpleSignAndLogin，更简单的登录接口，仅手机号即可登录，相同手机号会登录同一账号（仅限测试使用，或引入短信验证码）。

This project is maintained by [GitHub](https://github.com/wx6523610521).
