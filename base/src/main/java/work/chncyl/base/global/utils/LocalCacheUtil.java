package work.chncyl.base.global.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存工具类
 * 用于缓存用户基本信息，减少Redis访问
 */
public class LocalCacheUtil {
    
    private static final Map<String, CacheEntry> localCache = new ConcurrentHashMap<>();
    private static final long DEFAULT_EXPIRE_TIME = TimeUnit.MINUTES.toMillis(30); // 默认30分钟
    
    /**
     * 缓存条目
     */
    private static class CacheEntry {
        private final Object value;
        private final long expireTime;
        
        public CacheEntry(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }
    
    /**
     * 放入缓存
     * @param key 缓存键
     * @param value 缓存值
     * @param expireTime 过期时间（毫秒）
     */
    public static void put(String key, Object value, long expireTime) {
        localCache.put(key, new CacheEntry(value, System.currentTimeMillis() + expireTime));
    }
    
    /**
     * 放入缓存（使用默认过期时间）
     * @param key 缓存键
     * @param value 缓存值
     */
    public static void put(String key, Object value) {
        put(key, value, DEFAULT_EXPIRE_TIME);
    }
    
    /**
     * 获取缓存
     * @param key 缓存键
     * @param <T> 返回值类型
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        CacheEntry entry = localCache.get(key);
        if (entry == null) {
            return null;
        }
        
        if (entry.isExpired()) {
            localCache.remove(key);
            return null;
        }
        
        return (T) entry.value;
    }
    
    /**
     * 移除缓存
     * @param key 缓存键
     */
    public static void remove(String key) {
        localCache.remove(key);
    }
    
    /**
     * 清除所有缓存
     */
    public static void clear() {
        localCache.clear();
    }
    
    /**
     * 获取缓存大小
     * @return 缓存条目数量
     */
    public static int size() {
        return localCache.size();
    }
    
    /**
     * 清理过期缓存
     */
    public static void cleanExpired() {
        localCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
