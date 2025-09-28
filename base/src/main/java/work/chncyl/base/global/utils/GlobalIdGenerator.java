package work.chncyl.base.global.utils;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 全局ID生成器
 * 基于时间戳(35位) + 机器码(8位) + 序列号(12位) + 分组标识(4位)组合
 * 使用Base62编码压缩位数，支持分库分表和有序插入
 */
public class GlobalIdGenerator {
    
    // 起始时间戳，可从配置中获取
    private static long START_TIMESTAMP = 1735660800000L; // 默认：2025-01-01 00:00:00
    
    // 性能优化配置
    private static boolean CLOCK_BACKWARDS_PROTECTION = true;
    private static long MAX_CLOCK_BACKWARDS_MS = 1000;
    private static int SEQUENCE_PREALLOC_SIZE = 200;
    
    // 时间戳位数：35位（约35年）
    private static final long TIMESTAMP_BITS = 35L;
    private static final long MAX_TIMESTAMP = (1L << TIMESTAMP_BITS) - 1;
    
    // 机器码位数：8位（256台机器）
    private static final long WORKER_ID_BITS = 8L;
    private static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1;
    
    // 序列号位数：12位（每毫秒4096个ID）
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
    
    // 分组标识位数：4位（16个分组）
    private static final long GROUP_ID_BITS = 4L;
    private static final long MAX_GROUP_ID = (1L << GROUP_ID_BITS) - 1;
    
    // 位偏移量
    private static final long TIMESTAMP_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS + GROUP_ID_BITS;
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS + GROUP_ID_BITS;
    private static final long SEQUENCE_SHIFT = GROUP_ID_BITS;
    
    // Base62字符集
    private static final char[] BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    
    private final long workerId;
    private final long groupId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;
    private long[] preallocatedSequences;
    private int preallocatedIndex = 0;
    

    /**
     * 构造函数
     * @param workerId 机器ID (0-255)
     * @param groupId 分组ID (0-15)
     */
    public GlobalIdGenerator(long workerId, long groupId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("Worker ID must be between 0 and " + MAX_WORKER_ID);
        }
        if (groupId > MAX_GROUP_ID || groupId < 0) {
            throw new IllegalArgumentException("Group ID must be between 0 and " + MAX_GROUP_ID);
        }
        this.workerId = workerId;
        this.groupId = groupId;
        this.preallocatedSequences = new long[SEQUENCE_PREALLOC_SIZE];
        this.preallocatedIndex = SEQUENCE_PREALLOC_SIZE; // 初始化为需要预分配
    }
    
    /**
     * 生成下一个ID
     * @return Base62编码的字符串ID
     */
    public synchronized String nextId() {
        long timestamp = timeGen();

        // 处理时钟回拨
        if (timestamp < lastTimestamp) {
            if (CLOCK_BACKWARDS_PROTECTION) {
                long offset = lastTimestamp - timestamp;
                if (offset <= MAX_CLOCK_BACKWARDS_MS) {
                    // 在容忍范围内，等待时钟追上
                    try {
                        Thread.sleep(offset);
                        timestamp = timeGen();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Clock backwards protection interrupted", e);
                    }
                } else {
                    // 超过容忍范围，抛出异常
                    throw new RuntimeException("Clock moved backwards by " + offset + "ms, exceeding maximum tolerance of " + MAX_CLOCK_BACKWARDS_MS + "ms");
                }
            } else {
                throw new RuntimeException("Clock moved backwards. Refusing to generate id");
            }
        }
        
        // 检查是否需要预分配序列号
        if (preallocatedIndex >= SEQUENCE_PREALLOC_SIZE) {
            preallocateSequences(timestamp);
        }
        
        // 从预分配缓存中获取序列号
        long sequenceValue = preallocatedSequences[preallocatedIndex++];
        
        // 组合各部分生成原始ID
        long rawId = ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | (sequenceValue << SEQUENCE_SHIFT)
                | groupId;
        
        return toBase62(rawId);
    }
    
    /**
     * 从ID解析时间戳
     * @param id Base62编码的ID
     * @return 时间戳
     */
    public static long parseTimestamp(String id) {
        long rawId = fromBase62(id);
        long timestamp = (rawId >>> TIMESTAMP_SHIFT) + START_TIMESTAMP;
        return timestamp;
    }
    
    /**
     * 从ID解析机器码
     * @param id Base62编码的ID
     * @return 机器ID
     */
    public static long parseWorkerId(String id) {
        long rawId = fromBase62(id);
        return (rawId >>> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }
    
    /**
     * 从ID解析序列号
     * @param id Base62编码的ID
     * @return 序列号
     */
    public static long parseSequence(String id) {
        long rawId = fromBase62(id);
        return (rawId >>> SEQUENCE_SHIFT) & MAX_SEQUENCE;
    }
    
    /**
     * 从ID解析分组标识
     * @param id Base62编码的ID
     * @return 分组ID
     */
    public static long parseGroupId(String id) {
        long rawId = fromBase62(id);
        return rawId & MAX_GROUP_ID;
    }
    
    /**
     * 获取ID生成时间
     * @param id Base62编码的ID
     * @return 生成时间
     */
    public static LocalDateTime getGenerateTime(String id) {
        long timestamp = parseTimestamp(id);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
    
    /**
     * 预分配序列号
     */
    private void preallocateSequences(long currentTimestamp) {
        if (currentTimestamp != lastTimestamp) {
            // 新的时间戳，重置序列号
            sequence = 0L;
            lastTimestamp = currentTimestamp;
        }
        
        // 预分配序列号
        for (int i = 0; i < SEQUENCE_PREALLOC_SIZE; i++) {
            preallocatedSequences[i] = sequence;
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 序列号用完，等待下一毫秒
                lastTimestamp = tilNextMillis(lastTimestamp);
            }
        }
        
        preallocatedIndex = 0;
    }
    
    /**
     * 等待下一毫秒
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
    
    /**
     * 获取当前时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
    
    /**
     * 转换为Base62编码
     */
    private String toBase62(long value) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.insert(0, BASE62_CHARS[(int) (value % 62)]);
            value /= 62;
        } while (value > 0);
        return sb.toString();
    }
    
    /**
     * 从Base62解码
     */
    private static long fromBase62(String str) {
        long result = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int digit;
            if (c >= '0' && c <= '9') {
                digit = c - '0';
            } else if (c >= 'A' && c <= 'Z') {
                digit = 10 + (c - 'A');
            } else if (c >= 'a' && c <= 'z') {
                digit = 36 + (c - 'a');
            } else {
                throw new IllegalArgumentException("Invalid Base62 character: " + c);
            }
            result = result * 62 + digit;
        }
        return result;
    }
    
    /**
     * 设置起始时间戳（支持全局配置）
     */
    public static void setStartTimestamp(long startTimestamp) {
        START_TIMESTAMP = startTimestamp;
    }
    
    /**
     * 设置时钟回拨保护
     */
    public static void setClockBackwardsProtection(boolean enabled) {
        CLOCK_BACKWARDS_PROTECTION = enabled;
    }
    
    /**
     * 设置最大时钟回拨容忍时间
     */
    public static void setMaxClockBackwardsMs(long maxMs) {
        MAX_CLOCK_BACKWARDS_MS = maxMs;
    }
    
    /**
     * 设置序列号预分配大小
     */
    public static void setSequencePreallocSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Preallocation size must be positive");
        }
        SEQUENCE_PREALLOC_SIZE = size;
    }
    
    /**
     * 获取起始时间戳
     */
    public static long getStartTimestamp() {
        return START_TIMESTAMP;
    }
    
    /**
     * 获取时钟回拨保护状态
     */
    public static boolean isClockBackwardsProtection() {
        return CLOCK_BACKWARDS_PROTECTION;
    }
    
    /**
     * 获取最大时钟回拨容忍时间
     */
    public static long getMaxClockBackwardsMs() {
        return MAX_CLOCK_BACKWARDS_MS;
    }
    
    /**
     * 获取序列号预分配大小
     */
    public static int getSequencePreallocSize() {
        return SEQUENCE_PREALLOC_SIZE;
    }
    
    /**
     * 获取机器ID
     */
    public long getWorkerId() {
        return workerId;
    }
    
    /**
     * 获取默认实例（单机模式）
     */
    public static GlobalIdGenerator getDefaultInstance() {
        // 使用进程ID和计数器生成机器码
        try {
            // java9+ 使用ProcessHandle获取进程ID
//            long processId = ProcessHandle.current().pid();
            // java8 使用ManagementFactory获取进程ID
            long processId = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
            long workerId = (processId % MAX_WORKER_ID);
            return new GlobalIdGenerator(workerId, 0);
        } catch (Exception e) {
            // 如果无法获取进程ID，使用随机数
            return new GlobalIdGenerator(System.currentTimeMillis() % MAX_WORKER_ID, 0);
        }
    }
    
    /**
     * 测试方法
     */
    public static void main(String[] args) {
        GlobalIdGenerator generator = new GlobalIdGenerator(1, 1);
        
        for (int i = 0; i < 10; i++) {
            String id = generator.nextId();
            System.out.println("Generated ID: " + id);
            System.out.println("Timestamp: " + parseTimestamp(id));
            System.out.println("Worker ID: " + parseWorkerId(id));
            System.out.println("Sequence: " + parseSequence(id));
            System.out.println("Group ID: " + parseGroupId(id));
            System.out.println("Generate Time: " + getGenerateTime(id));
            System.out.println("------------------------");
        }
    }
}
