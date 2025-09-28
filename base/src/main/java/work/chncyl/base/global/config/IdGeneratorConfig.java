package work.chncyl.base.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import work.chncyl.base.global.utils.GlobalIdGenerator;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * ID生成器配置类
 * 支持全局配置起始时间戳
 */
@Configuration
public class IdGeneratorConfig {
    
    @Value("${id.generator.start-time:2025-01-01T00:00:00}")
    private String startTime;
    
    @Value("${id.generator.worker-id:0}")
    private long workerId;
    
    @Value("${id.generator.default-group:0}")
    private long defaultGroup;
    
    @Value("${id.generator.performance.clock-backwards-protection:true}")
    private boolean clockBackwardsProtection;
    
    @Value("${id.generator.performance.max-clock-backwards-ms:1000}")
    private long maxClockBackwardsMs;
    
    @Value("${id.generator.performance.sequence-prealloc-size:200}")
    private int sequencePreallocSize;
    
    private static long configuredStartTimestamp;
    private static long configuredWorkerId;
    private static long configuredDefaultGroup;
    private static boolean configuredClockBackwardsProtection;
    private static long configuredMaxClockBackwardsMs;
    private static int configuredSequencePreallocSize;
    
    @PostConstruct
    public void init() {
        // 解析起始时间
        LocalDateTime startDateTime = LocalDateTime.parse(startTime);
        configuredStartTimestamp = startDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        configuredWorkerId = workerId;
        configuredDefaultGroup = defaultGroup;
        configuredClockBackwardsProtection = clockBackwardsProtection;
        configuredMaxClockBackwardsMs = maxClockBackwardsMs;
        configuredSequencePreallocSize = sequencePreallocSize;
        
        // 设置全局起始时间戳和性能配置
        GlobalIdGenerator.setStartTimestamp(configuredStartTimestamp);
        GlobalIdGenerator.setClockBackwardsProtection(configuredClockBackwardsProtection);
        GlobalIdGenerator.setMaxClockBackwardsMs(configuredMaxClockBackwardsMs);
        GlobalIdGenerator.setSequencePreallocSize(configuredSequencePreallocSize);
        
        System.out.println("ID Generator configured with:");
        System.out.println("Start Time: " + startDateTime);
        System.out.println("Start Timestamp: " + configuredStartTimestamp);
        System.out.println("Worker ID: " + configuredWorkerId);
        System.out.println("Default Group: " + configuredDefaultGroup);
        System.out.println("Clock Backwards Protection: " + configuredClockBackwardsProtection);
        System.out.println("Max Clock Backwards Ms: " + configuredMaxClockBackwardsMs);
        System.out.println("Sequence Prealloc Size: " + configuredSequencePreallocSize);
    }
    
    public static long getStartTimestamp() {
        return configuredStartTimestamp;
    }
    
    public static long getWorkerId() {
        return configuredWorkerId;
    }
    
    public static long getDefaultGroup() {
        return configuredDefaultGroup;
    }
    
    public static boolean isClockBackwardsProtection() {
        return configuredClockBackwardsProtection;
    }
    
    public static long getMaxClockBackwardsMs() {
        return configuredMaxClockBackwardsMs;
    }
    
    public static int getSequencePreallocSize() {
        return configuredSequencePreallocSize;
    }
    
    /**
     * 获取配置的ID生成器实例
     */
    public static GlobalIdGenerator getConfiguredGenerator() {
        return new GlobalIdGenerator(configuredWorkerId, configuredDefaultGroup);
    }
    
    /**
     * 获取指定分组的ID生成器实例
     */
    public static GlobalIdGenerator getGeneratorForGroup(long groupId) {
        return new GlobalIdGenerator(configuredWorkerId, groupId);
    }
}
