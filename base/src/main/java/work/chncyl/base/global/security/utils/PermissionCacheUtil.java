package work.chncyl.base.global.security.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import work.chncyl.base.global.redis.RedisUtils;
import work.chncyl.base.global.security.dto.RoleInfo;
import work.chncyl.base.global.security.dto.RolePermissionsSearch;
import work.chncyl.base.global.security.dto.RoleSearchDto;
import work.chncyl.base.global.service.GlobalService;
import work.chncyl.base.global.utils.LocalCacheUtil;
import work.chncyl.base.global.utils.SpringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static work.chncyl.base.global.Constants.REFRESH_TOKEN_KEY;
import static work.chncyl.base.global.Constants.USER_INFO_KEY;

@Slf4j
@Component
public class PermissionCacheUtil implements CommandLineRunner {

    public static class PermissionCache {
        /**
         * 角色ID-权限列表
         */
        private static final Map<String, List<String>> PERMISSION_CACHE = new ConcurrentHashMap<>();

        public static void put(String roleId, List<String> permissions) {
            PERMISSION_CACHE.put(roleId, permissions);
        }

        public static List<String> get(String roleId) {
            if (PERMISSION_CACHE.isEmpty()) {
                refreshAll();
            }
            return PERMISSION_CACHE.get(roleId);
        }

        public static List<String> get(List<String> roleIds) {
            Set<String> permissions = new HashSet<>();
            for (String roleId : roleIds) {
                if (PERMISSION_CACHE.containsKey(roleId)) {
                    permissions.addAll(PERMISSION_CACHE.get(roleId));
                }
            }
            return new ArrayList<>(permissions);
        }

        public static void remove(String roleId) {
            PERMISSION_CACHE.remove(roleId);
        }

        public static void clear() {
            PERMISSION_CACHE.clear();
        }

        public static void refreshAll() {
            PERMISSION_CACHE.clear();
            Map<String, List<String>> allRolePermissions = SpringUtils.getBean(GlobalService.class).getRolePermissions(RolePermissionsSearch.builder().build());

            PERMISSION_CACHE.putAll(allRolePermissions);
            log.info("PermissionCache refreshAll success, size: " + PERMISSION_CACHE.size());
        }

        public static void refreshByRoleId(Integer roleId) {
            Map<String, List<String>> allRolePermissions = SpringUtils.getBean(GlobalService.class).getRolePermissions(RolePermissionsSearch.builder().roleIds(Collections.singletonList(roleId)).build());

            PERMISSION_CACHE.putAll(allRolePermissions);
        }

        public static int size() {
            return PERMISSION_CACHE.size();
        }
    }

    public static class RoleCache {
        private static final Map<Integer, RoleInfo> ROLE_CACHE = new ConcurrentHashMap<>();

        public static void put(Integer roleId, RoleInfo permissions) {
            ROLE_CACHE.put(roleId, permissions);
        }

        public static RoleInfo get(Integer roleId) {
            if (ROLE_CACHE.isEmpty()) {
                refreshAll();
            }
            return ROLE_CACHE.get(roleId);
        }

        public static List<RoleInfo> simpleGet(List<String> roleIds) {
            List<Integer> collect = roleIds.stream().map(Integer::valueOf).collect(Collectors.toList());
            return get(collect);
        }

        public static List<RoleInfo> get(List<Integer> roleIds) {
            Set<RoleInfo> permissions = new HashSet<>();
            for (Integer roleId : roleIds) {
                if (ROLE_CACHE.containsKey(roleId)) {
                    permissions.add(ROLE_CACHE.get(roleId));
                }
            }
            return new ArrayList<>(permissions);
        }

        public static void remove(Integer key) {
            ROLE_CACHE.remove(key);
        }

        public static void clear() {
            ROLE_CACHE.clear();
        }

        public static void refreshAll() {
            ROLE_CACHE.clear();
            List<RoleInfo> allRolePermissions = SpringUtils.getBean(GlobalService.class).getRoles(null);

            ROLE_CACHE.putAll(allRolePermissions.stream().collect(Collectors.toMap(RoleInfo::getId, roleInfo -> roleInfo)));
        }

        public static void refreshById(Integer roleId) {
            remove(roleId);
            List<RoleInfo> allRolePermissions = SpringUtils.getBean(GlobalService.class).getRoles(RoleSearchDto.builder().roleIds(Collections.singletonList(roleId)).build());

            ROLE_CACHE.putAll(allRolePermissions.stream().collect(Collectors.toMap(RoleInfo::getId, roleInfo -> roleInfo)));
        }

        public static int size() {
            return ROLE_CACHE.size();
        }
    }

    @Override
    public void run(String... args) {
        PermissionCache.refreshAll();
        RoleCache.refreshAll();
    }

    /**
     * 清理用户相关的缓存
     */
    public static void clearUserCache(String jwtId) {
        if (jwtId != null) {
            RedisUtils.delete(USER_INFO_KEY + jwtId);
            RedisUtils.delete(REFRESH_TOKEN_KEY + jwtId);
            LocalCacheUtil.remove(USER_INFO_KEY + jwtId);
        }
    }

    /**
     * 当用户权限变更时调用此方法清理缓存
     */
    public static void clearUserPermissionCache(String userId) {
        // 这里可以根据业务需求实现更精确的缓存清理逻辑
        PermissionCache.refreshAll();
        RoleCache.refreshAll();
    }
}
