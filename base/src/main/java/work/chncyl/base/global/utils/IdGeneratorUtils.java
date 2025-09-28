package work.chncyl.base.global.utils;

import work.chncyl.base.global.config.IdGeneratorConfig;

/**
 * ID生成器工具类
 * 提供简化的ID生成方法
 */
public class IdGeneratorUtils {
    
    private static final GlobalIdGenerator DEFAULT_GENERATOR = GlobalIdGenerator.getDefaultInstance();
    
    /**
     * 生成下一个ID（使用默认分组）
     * @return Base62编码的字符串ID
     */
    public static String nextId() {
        return DEFAULT_GENERATOR.nextId();
    }
    
    /**
     * 生成下一个指定分组的ID
     * @param groupId 分组ID (0-15)
     * @return Base62编码的字符串ID
     */
    public static String nextId(long groupId) {
        GlobalIdGenerator generator = new GlobalIdGenerator(DEFAULT_GENERATOR.getWorkerId(), groupId);
        return generator.nextId();
    }
    
    /**
     * 生成下一个配置分组的ID
     * @return Base62编码的字符串ID
     */
    public static String nextConfiguredId() {
        return IdGeneratorConfig.getConfiguredGenerator().nextId();
    }
    
    /**
     * 生成下一个指定配置分组的ID
     * @param groupId 分组ID (0-15)
     * @return Base62编码的字符串ID
     */
    public static String nextConfiguredId(long groupId) {
        return IdGeneratorConfig.getGeneratorForGroup(groupId).nextId();
    }
    
    /**
     * 从ID解析时间戳
     * @param id Base62编码的ID
     * @return 时间戳
     */
    public static long parseTimestamp(String id) {
        return GlobalIdGenerator.parseTimestamp(id);
    }
    
    /**
     * 从ID解析机器码
     * @param id Base62编码的ID
     * @return 机器ID
     */
    public static long parseWorkerId(String id) {
        return GlobalIdGenerator.parseWorkerId(id);
    }
    
    /**
     * 从ID解析序列号
     * @param id Base62编码的ID
     * @return 序列号
     */
    public static long parseSequence(String id) {
        return GlobalIdGenerator.parseSequence(id);
    }
    
    /**
     * 从ID解析分组标识
     * @param id Base62编码的ID
     * @return 分组ID
     */
    public static long parseGroupId(String id) {
        return GlobalIdGenerator.parseGroupId(id);
    }
    
    /**
     * 获取ID生成时间
     * @param id Base62编码的ID
     * @return 生成时间
     */
    public static java.time.LocalDateTime getGenerateTime(String id) {
        return GlobalIdGenerator.getGenerateTime(id);
    }
    
    /**
     * 获取默认的ID生成器实例
     */
    public static GlobalIdGenerator getDefaultGenerator() {
        return DEFAULT_GENERATOR;
    }
    
    /**
     * 获取指定分组的ID生成器实例
     */
    public static GlobalIdGenerator getGeneratorForGroup(long groupId) {
        return new GlobalIdGenerator(DEFAULT_GENERATOR.getWorkerId(), groupId);
    }
    
    /**
     * 获取当前机器ID
     */
    public static long getCurrentWorkerId() {
        return DEFAULT_GENERATOR.getWorkerId();
    }
}
