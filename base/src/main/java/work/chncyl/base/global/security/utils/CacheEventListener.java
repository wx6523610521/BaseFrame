package work.chncyl.base.global.security.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import work.chncyl.base.global.redis.RedisUtils;

import java.util.Set;

import static work.chncyl.base.global.Constants.USER_INFO_KEY;

/**
 * 缓存事件监听器，用于处理权限变更时的缓存清理
 */
@Slf4j
@Component
public class CacheEventListener {

    /**
     * 处理角色权限变更事件
     */
    @EventListener
    public void handleRolePermissionChangeEvent(RolePermissionChangeEvent event) {
        log.info("接收到角色权限变更事件，角色ID: {}", event.getRoleId());

        // 刷新权限缓存
        PermissionCacheUtil.PermissionCache.refreshByRoleId(event.getRoleId());
        PermissionCacheUtil.RoleCache.refreshById(event.getRoleId());

        log.info("角色权限缓存刷新完成");
    }

    /**
     * 处理用户权限变更事件
     */
    @EventListener
    public void handleUserPermissionChangeEvent(UserPermissionChangeEvent event) {
        log.info("接收到用户权限变更事件，用户ID: {}", event.getUserId());

        // 清理该用户相关的所有缓存
        Set<String> keys = RedisUtils.keys(USER_INFO_KEY + "*");
        for (String key : keys) {
            RedisUtils.delete(key);
        }

        log.info("用户权限相关缓存清理完成");
    }

    /**
     * 角色权限变更事件
     */
    public static class RolePermissionChangeEvent {
        private final Integer roleId;

        public RolePermissionChangeEvent(Integer roleId) {
            this.roleId = roleId;
        }

        public Integer getRoleId() {
            return roleId;
        }
    }

    /**
     * 用户权限变更事件
     */
    public static class UserPermissionChangeEvent {
        private final String userId;

        public UserPermissionChangeEvent(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }
    }
}
