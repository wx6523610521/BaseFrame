# 用户鉴权缓存优化方案

## 当前问题分析

### 1. 缓存管理不完善
- 用户信息缓存缺少空值检查
- 权限信息每次请求都重新加载，未充分利用缓存
- 缺少缓存一致性维护机制

### 2. Token刷新机制缺失
- JWT token刷新逻辑被注释掉
- 缺少自动续期机制

### 3. 缓存监控缺失
- 无法实时查看缓存状态
- 缺少缓存管理接口

## 优化方案实施

### 1. 缓存架构优化

#### 用户信息缓存 (Redis + LocalCache)
```
// 两级缓存：Redis(分布式) + LocalCache(本地)
USER_INFO_KEY + jwtId -> LoginedUserInfo (30分钟)
```

#### 权限信息缓存 (内存)
```
// 角色权限缓存
PermissionCacheUtil.PermissionCache -> Map<String, List<String>>
RoleCacheUtil.RoleCache -> Map<Integer, RoleInfo>
```

### 2. Token刷新机制

重新启用了JWT token自动刷新功能：
- AccessToken过期时间：配置的expire-second
- RefreshToken过期时间：AccessToken的2倍
- 自动续期：当AccessToken过期但RefreshToken有效时自动刷新

### 3. 缓存一致性维护

#### 事件驱动缓存清理
```java
// 角色权限变更事件
eventPublisher.publishEvent(new RolePermissionChangeEvent(roleId));

// 用户权限变更事件  
eventPublisher.publishEvent(new UserPermissionChangeEvent(userId));
```

#### 缓存监听器
- `CacheEventListener` 监听权限变更事件
- 自动刷新相关缓存

### 4. 缓存监控管理

#### REST接口
```
GET /api/cache/stats          # 获取缓存统计
POST /api/cache/refresh/permissions  # 刷新权限缓存
POST /api/cache/clear/user/{jwtId}  # 清理指定用户缓存
POST /api/cache/clear/all-users     # 清理所有用户缓存
POST /api/cache/event/role/{roleId} # 触发角色权限变更
POST /api/cache/event/user/{userId} # 触发用户权限变更
```

### 5. 关键改进点

#### TokenUtil.getLoginUser() 优化
- 添加了空值检查
- 优化了权限信息加载逻辑
- 避免重复加载权限信息

#### PermissionCacheUtil 增强
- 添加了缓存清理方法
- 添加了缓存统计方法
- 支持按角色刷新缓存

#### JwtFilter 完善
- 重新启用token刷新逻辑
- 添加必要的import语句

## 使用说明

### 1. 权限变更时清理缓存
```java
// 在权限服务中调用
cacheManagerUtil.publishRolePermissionChange(roleId);
cacheManagerUtil.publishUserPermissionChange(userId);
```

### 2. 手动管理缓存
```java
// 刷新所有权限缓存
cacheManagerUtil.refreshPermissionCache();

// 清理指定用户缓存
cacheManagerUtil.clearUserCache(jwtId);
```

### 3. 监控缓存状态
```bash
curl http://localhost:8080/api/cache/stats
```

## 性能提升

1. **减少数据库查询**：权限信息缓存后，每次请求减少2-3次DB查询
2. **降低Redis压力**：本地缓存减少Redis访问频率
3. **提升响应速度**：权限信息加载从每次请求变为首次加载

## 注意事项

1. 本地缓存适用于单机环境，集群环境需要额外处理缓存同步
2. 权限变更后需要及时触发缓存清理事件
3. 生产环境建议配置合适的缓存过期时间
