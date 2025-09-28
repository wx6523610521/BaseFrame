package work.chncyl.base.global.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import work.chncyl.base.global.redis.RedisUtils;
import work.chncyl.base.global.security.utils.CacheEventListener;
import work.chncyl.base.global.security.utils.PermissionCacheUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static work.chncyl.base.global.Constants.USER_INFO_KEY;

/**
 * 缓存管理工具类
 */
@Slf4j
@Component
public class CacheManagerUtil {

    private final ApplicationEventPublisher eventPublisher;

    public CacheManagerUtil(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 清理用户缓存
     */
    public void clearUserCache(String jwtId) {
        PermissionCacheUtil.clearUserCache(jwtId);
        log.info("用户缓存清理完成，jwtId: {}", jwtId);
    }

    /**
     * 清理所有用户缓存
     */
    public void clearAllUserCache() {
        Set<String> keys = RedisUtils.keys(USER_INFO_KEY + "*");
        int count = 0;
        for (String key : keys) {
            RedisUtils.delete(key);
            count++;
        }
        log.info("所有用户缓存清理完成，共清理 {} 个缓存", count);
    }

    /**
     * 刷新权限缓存
     */
    public void refreshPermissionCache() {
        PermissionCacheUtil.PermissionCache.refreshAll();
        PermissionCacheUtil.RoleCache.refreshAll();
        log.info("权限缓存刷新完成");
    }

    /**
     * 发布角色权限变更事件
     */
    public void publishRolePermissionChange(Integer roleId) {
        eventPublisher.publishEvent(new CacheEventListener.RolePermissionChangeEvent(roleId));
        log.info("发布角色权限变更事件，角色ID: {}", roleId);
    }

    /**
     * 发布用户权限变更事件
     */
    public void publishUserPermissionChange(String userId) {
        eventPublisher.publishEvent(new CacheEventListener.UserPermissionChangeEvent(userId));
        log.info("发布用户权限变更事件，用户ID: {}", userId);
    }

    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        // 用户缓存统计
        Set<String> userKeys = RedisUtils.keys(USER_INFO_KEY + "*");
        stats.put("userCacheCount", userKeys.size());

        // 权限缓存统计
        stats.put("permissionCacheCount", PermissionCacheUtil.PermissionCache.size());
        stats.put("roleCacheCount", PermissionCacheUtil.RoleCache.size());

        return stats;
    }
}
