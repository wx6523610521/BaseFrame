package work.chncyl.base.global.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.chncyl.base.global.result.ApiResult;
import work.chncyl.base.global.utils.CacheManagerUtil;

import java.util.Map;

/**
 * 缓存监控控制器
 */
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheMonitorController {

    private final CacheManagerUtil cacheManagerUtil;

    /**
     * 获取缓存统计信息
     */
    @GetMapping("/stats")
    public ApiResult<Map<String, Object>> getCacheStats() {
        return ApiResult.success(cacheManagerUtil.getCacheStats());
    }

    /**
     * 刷新权限缓存
     */
    @PostMapping("/refresh/permissions")
    public ApiResult<String> refreshPermissionCache() {
        cacheManagerUtil.refreshPermissionCache();
        return ApiResult.success("权限缓存刷新成功");
    }

    /**
     * 清理用户缓存
     */
    @PostMapping("/clear/user/{jwtId}")
    public ApiResult<String> clearUserCache(@PathVariable String jwtId) {
        cacheManagerUtil.clearUserCache(jwtId);
        return ApiResult.success("用户缓存清理成功");
    }

    /**
     * 清理所有用户缓存
     */
    @PostMapping("/clear/all-users")
    public ApiResult<String> clearAllUserCache() {
        cacheManagerUtil.clearAllUserCache();
        return ApiResult.success("所有用户缓存清理成功");
    }

    /**
     * 触发角色权限变更
     */
    @PostMapping("/event/role/{roleId}")
    public ApiResult<String> triggerRolePermissionChange(@PathVariable Integer roleId) {
        cacheManagerUtil.publishRolePermissionChange(roleId);
        return ApiResult.success("角色权限变更事件触发成功");
    }

    /**
     * 触发用户权限变更
     */
    @PostMapping("/event/user/{userId}")
    public ApiResult<String> triggerUserPermissionChange(@PathVariable String userId) {
        cacheManagerUtil.publishUserPermissionChange(userId);
        return ApiResult.success("用户权限变更事件触发成功");
    }
}
