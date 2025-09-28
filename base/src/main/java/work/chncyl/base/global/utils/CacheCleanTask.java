package work.chncyl.base.global.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 缓存清理定时任务
 * 定期清理本地缓存中的过期条目
 */
@Component
public class CacheCleanTask {

    /**
     * 每5分钟清理一次过期缓存
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5分钟
    public void cleanExpiredCache() {
        LocalCacheUtil.cleanExpired();
    }
    
    /**
     * 每小时打印一次缓存统计信息（用于监控）
     */
    @Scheduled(fixedRate = 60 * 60 * 1000) // 1小时
    public void printCacheStats() {
        int cacheSize = LocalCacheUtil.size();
        if (cacheSize > 0) {
            System.out.println("本地缓存统计: " + cacheSize + " 个条目");
        }
    }
}
