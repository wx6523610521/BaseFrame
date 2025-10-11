package work.chncyl.base.global.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 全局ID生成器
 * 基于时间戳(35位) + 机器码(8位) + 序列号(12位) + 分组标识(4位)组合
 * 使用Base62编码压缩位数，支持分库分表和有序插入
 */
@Slf4j
public class GlobalIdGenerator {

    private static Long START_TIMESTAMP = 1735660800000L;// 默认：2025-01-01 00:00:00
    private static boolean CLOCK_BACKWARDS_PROTECTION = true;
    private static long MAX_CLOCK_BACKWARDS_MS = 1000;

    // 时间戳位数：35位（约35年）
    private static final long TIMESTAMP_BITS = 35L;

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
    private volatile long lastTimestamp = -1L;
    private long sequence = 0L;
    private final Lock lock = new ReentrantLock();


    /**
     * 构造函数
     *
     * @param workerId 机器ID (0-255)
     * @param groupId  分组ID (0-15)
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
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getGroupId() {
        return groupId;
    }

    public static void setStartTimestamp(Long startTimestamp) {
        if (START_TIMESTAMP != null && START_TIMESTAMP != 1735660800000L) {
            throw new IllegalArgumentException("Start timestamp has already been set");
        }
        if (startTimestamp == null) {
            // 默认起始时间戳：2025-01-01 00:00:00
            startTimestamp = 1735660800000L;
        }
        START_TIMESTAMP = startTimestamp;
    }

    public static void setMaxClockBackwardsMs(long maxClockBackwardsMs) {
        MAX_CLOCK_BACKWARDS_MS = maxClockBackwardsMs;
    }

    public static void setClockBackwardsProtection(boolean clockBackwardsProtection) {
        CLOCK_BACKWARDS_PROTECTION = clockBackwardsProtection;
    }

    /**
     * 生成下一个ID
     *
     * @return Base62编码的字符串ID
     */
    public String nextId() {
        try {
            lock.lock();
            long timestamp = timeGen();

            // 处理时钟回拨
            while (timestamp < lastTimestamp) {
                if (CLOCK_BACKWARDS_PROTECTION) {
                    long offset = lastTimestamp - timestamp;
                    if (offset <= MAX_CLOCK_BACKWARDS_MS) {
                        try {
                            Thread.sleep(offset);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Clock backwards protection interrupted", e);
                        }
                    } else {
                        throw new RuntimeException("Clock moved backwards by " + offset + "ms, exceeding maximum tolerance of " + MAX_CLOCK_BACKWARDS_MS + "ms");
                    }
                } else {
                    throw new RuntimeException("Clock moved backwards. Refusing to generate id");
                }
                timestamp = timeGen();
            }

            // 如果是同一毫秒生成的，则进行序列号递增
            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & MAX_SEQUENCE;
                // 同一毫秒内序列号用尽，等待下一毫秒
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0;
            }

            // 保存当前时间戳
            lastTimestamp = timestamp;
            long seq = sequence;

            // 组合各部分生成原始ID
            long rawId = ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                    | (workerId << WORKER_ID_SHIFT)
                    | (seq << SEQUENCE_SHIFT)
                    | groupId;

            return toBase62(rawId);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从ID解析时间戳
     *
     * @param id Base62编码的ID
     * @return 时间戳
     */
    public static long parseTimestamp(String id) {
        long rawId = fromBase62(id);
        return (rawId >>> TIMESTAMP_SHIFT) + START_TIMESTAMP;
    }

    /**
     * 从ID解析机器码
     *
     * @param id Base62编码的ID
     * @return 机器ID
     */
    public static long parseWorkerId(String id) {
        long rawId = fromBase62(id);
        return (rawId >>> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }

    /**
     * 从ID解析序列号
     *
     * @param id Base62编码的ID
     * @return 序列号
     */
    public static long parseSequence(String id) {
        long rawId = fromBase62(id);
        return (rawId >>> SEQUENCE_SHIFT) & MAX_SEQUENCE;
    }

    /**
     * 从ID解析分组标识
     *
     * @param id Base62编码的ID
     * @return 分组ID
     */
    public static long parseGroupId(String id) {
        long rawId = fromBase62(id);
        return rawId & MAX_GROUP_ID;
    }

    /**
     * 获取ID生成时间
     *
     * @param id Base62编码的ID
     * @return 生成时间
     */
    public static LocalDateTime getGenerateTime(String id) {
        long timestamp = parseTimestamp(id);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
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
     * 获取默认实例（单机模式）
     */
    public static GlobalIdGenerator getDefaultInstance() {
        try {
            // java9+ 使用ProcessHandle获取进程ID
            // long processId = ProcessHandle.current().pid();
            // java8 使用ManagementFactory获取进程ID
            long processId = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
            long workerId = (processId % MAX_WORKER_ID);
            return new GlobalIdGenerator(workerId, 0);
        } catch (SecurityException | NumberFormatException e) {
            // 如果无法获取进程ID，使用随机数
            return new GlobalIdGenerator(System.currentTimeMillis() % MAX_WORKER_ID, 0);
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) throws InterruptedException {
        final int THREAD_COUNT = 50;
        final int IDS_PER_THREAD = 10000;

        Set<String> ids = ConcurrentHashMap.newKeySet(THREAD_COUNT * IDS_PER_THREAD);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < IDS_PER_THREAD; j++) {
                        String id = IdGeneratorUtils.nextId();
//                        log.info(id);
                        ids.add(id);
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) + "ms");
        System.out.println("Expected IDs: " + (THREAD_COUNT * IDS_PER_THREAD));
        System.out.println("Actual IDs: " + ids.size());
        System.out.println("Average generation rate: " + (ids.size() * 1000.0 / (endTime - startTime)) + " ids/second");
    }
}