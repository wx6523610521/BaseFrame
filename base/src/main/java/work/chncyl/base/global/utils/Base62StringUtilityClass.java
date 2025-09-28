package work.chncyl.base.global.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import work.chncyl.base.global.enums.CacheConstant;
import work.chncyl.base.global.redis.RedisUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 62进制字符串工具类
 *
 * @author chncyl
 */
@Component
public class Base62StringUtilityClass {
    private static final String[] scale = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h",
            "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C",
            "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z"};
    private static final Map<Character, Integer> charToIndex = new HashMap<>();

    private static Integer START_YEAR = null;
    private static int startYear = 2025;

    @Value("${random-year-start:2025}")
    public void setStartYear(int year) {
        startYear = year;
    }

    static {
        for (int i = 0; i < scale.length; i++) {
            charToIndex.put(scale[i].charAt(0), i);
        }
    }

    /**
     * 时间精度枚举
     */
    public enum TimePrecision {
        DAY, // 精确到日
        HOUR, // 精确到时
        MINUTE, // 精确到分
        SECOND, // 精确到秒
        MILLISECOND // 精确到毫秒
    }

    /**
     * 生成全局唯一的字符串（根据精度自动设置长度和分组）
     *
     * @return 随机字符串
     */
    public static String generateGlobalStr() {
        return generateGlobalStr(null);
    }

    /**
     * 生成全局唯一的含创建日期信息的字符串（默认精确到分钟）
     *
     * @param size 字符串长度 至少6位，否则无法实现随机效果
     * @return 随机字符串
     */
    public static String generateStr(Integer size) {
        return generateStr(size, null, TimePrecision.MINUTE);
    }

    /**
     * 生成全局唯一的字符串（根据精度自动设置长度和分组）
     *
     * @param sequence 自定义序列组
     * @return 随机字符串
     */
    public static String generateGlobalStr(String sequence) {
        return generateGlobalStr(sequence, TimePrecision.MILLISECOND);
    }

    /**
     * 生成全局唯一的字符串（根据精度自动设置长度和分组）
     *
     * @param sequence  自定义序列组
     * @param precision 时间精度
     * @return 随机字符串
     */
    public static String generateGlobalStr(String sequence, TimePrecision precision) {
        int size = getRecommendedSize(precision);
        return generateStr(size, sequence, precision);
    }

    /**
     * 生成全局唯一的含创建日期信息的字符串
     *
     * @param size      字符串长度
     * @param precision 时间精度
     * @return 随机字符串
     */
    public static String generateStr(Integer size, TimePrecision precision) {
        return generateStr(size, null, precision);
    }


    public static String generateStr(Integer size, CharSequence sequence, TimePrecision precision) {
        if (START_YEAR == null) {
            START_YEAR = startYear;
        }
        LocalDateTime dateTime = LocalDateTime.now();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // 计算年月 - 修正年份计算逻辑
        int yearOffset = (dateTime.getYear() - START_YEAR) % 4;
        String yearBin = String.format("%2s", Integer.toString(yearOffset, 2)).replaceAll(" ", "0");
        String monthBin = String.format("%4s", Integer.toString(dateTime.getMonth().getValue() - 1, 2)).replaceAll(" ",
                "0");
        String ymBin = yearBin + monthBin;
        String ym = scale[Integer.valueOf(ymBin, 2)];

        // 计算日
        int dayIdx = dateTime.getDayOfMonth() - 1;
        boolean dayOffset = random.nextBoolean();
        if (dayOffset) {
            dayIdx += 31;
        }
        String d = scale[dayIdx];

        // 根据精度动态计算时间戳
        String timeStamp = "";
        int randomLength = 0;
        boolean hourOffset;
        int minute = 0;
        int second = 0;
        int millisecond = 0;
        int hour = 0;

        if (precision.compareTo(TimePrecision.DAY) > 0) {
            // 获取小时（带偏移）
            hour = dateTime.getHour();
            hourOffset = random.nextBoolean();
            if (hourOffset) {
                hour += 24;
            }
        }

        if (precision.compareTo(TimePrecision.HOUR) > 0) {
            minute = dateTime.getMinute();
            if (precision.compareTo(TimePrecision.MINUTE) > 0) {
                second = dateTime.getSecond();
                if (precision == TimePrecision.MILLISECOND) {
                    millisecond = dateTime.getNano() / 1_000_000; // 转换为毫秒
                }
            }
        }

        // 根据精度计算时间戳结构和随机部分长度
        switch (precision) {
            case DAY:
                timeStamp = ym + d;
                randomLength = size - 2;
                break;
            case HOUR:
                timeStamp = ym + d + scale[hour];
                randomLength = size - 3;
                break;
            case MINUTE:
                timeStamp = ym + d + scale[hour];
                randomLength = size - 4; // 分钟(1) + timeStamp(3) = 4
                break;
            case SECOND:
                timeStamp = ym + d + scale[hour] + scale[second];
                randomLength = size - 5; // 分钟(1) + timeStamp(4) = 5
                break;
            case MILLISECOND:
                // 毫秒需要3位62进制表示（62^3 = 238328，足够表示0-999毫秒）
                String millisStr = String.format("%3s", parse10To62(millisecond)).replaceAll(" ", "0");
                timeStamp = ym + d + scale[hour] + scale[second] + millisStr;
                randomLength = size - 8; // 分钟(1) + timeStamp(7) = 8
                break;
        }

        if (sequence == null) {
            StringBuilder randomPart = new StringBuilder(randomLength);
            for (int i = 0; i < randomLength; i++) {
                randomPart.append(scale[random.nextInt(62)]);
            }

            // 不同精度采用不同的拼接策略
            switch (precision) {
                case DAY:
                case HOUR:
                    return randomPart + timeStamp;
                case MINUTE:
                case SECOND:
                case MILLISECOND:
                    return scale[minute] + randomPart + timeStamp;
            }
        } else {
            long randomVal = RedisUtils.increment(CacheConstant.RANDOM_CHAR + (":" + sequence), 1);
            StringBuilder sb = new StringBuilder();
            while (randomVal != 0) {
                int digit = (int) randomVal % 62;
                sb.insert(0, scale[digit]);
                randomVal = randomVal / 62;
            }
            String randomStr = String.format("%" + randomLength + "s", sb).replaceAll(" ", "0");

            // 不同精度采用不同的拼接策略
            switch (precision) {
                case DAY:
                case HOUR:
                    return randomStr + timeStamp;
                case MINUTE:
                case SECOND:
                case MILLISECOND:
                    return scale[minute] + randomStr + timeStamp;
            }
        }
        return ""; // 永远不会执行但作为fallback
    }

    public static LocalDateTime getCreateTime(String s) {
        return getCreateTime(s, TimePrecision.MINUTE);
    }

    /**
     * 解析创建时间
     *
     * @param s         生成的字符串
     * @param precision 时间精度
     * @return 创建时间
     */
    public static LocalDateTime getCreateTime(String s, TimePrecision precision) {
        if (START_YEAR == null) {
            START_YEAR = startYear;
        }
        int len = s.length();
        int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0, millisecond = 0;

        // 根据精度从不同位置解析时间戳
        switch (precision) {
            case DAY:
                // 最后2位：年月(ym)+日(d)
                year = START_YEAR + (charToIndex.get(s.charAt(len - 2)) >> 4);
                month = (charToIndex.get(s.charAt(len - 2)) & 15) + 1;
                day = (charToIndex.get(s.charAt(len - 1)) % 31) + 1;
                break;
            case HOUR:
                // 最后3位：ym + d + hour
                year = START_YEAR + (charToIndex.get(s.charAt(len - 3)) >> 4);
                month = (charToIndex.get(s.charAt(len - 3)) & 15) + 1;
                day = (charToIndex.get(s.charAt(len - 2)) % 31) + 1;
                hour = charToIndex.get(s.charAt(len - 1)) % 24;
                break;
            case MINUTE:
                // 第一位: 分钟, 最后3位: ym+d+hour
                minute = charToIndex.get(s.charAt(0));
                year = START_YEAR + (charToIndex.get(s.charAt(len - 3)) >> 4);
                month = (charToIndex.get(s.charAt(len - 3)) & 15) + 1;
                day = (charToIndex.get(s.charAt(len - 2)) % 31) + 1;
                hour = charToIndex.get(s.charAt(len - 1)) % 24;
                break;
            case SECOND:
                // 第一位: 分钟, 最后4位: ym+d+hour+second
                minute = charToIndex.get(s.charAt(0));
                year = START_YEAR + (charToIndex.get(s.charAt(len - 4)) >> 4);
                month = (charToIndex.get(s.charAt(len - 4)) & 15) + 1;
                day = (charToIndex.get(s.charAt(len - 3)) % 31) + 1;
                hour = charToIndex.get(s.charAt(len - 2)) % 24;
                second = charToIndex.get(s.charAt(len - 1));
                break;
            case MILLISECOND:
                // 第一位: 分钟, 最后7位: ym+d+hour+second+millisecond(3位)
                minute = charToIndex.get(s.charAt(0));
                year = START_YEAR + (charToIndex.get(s.charAt(len - 7)) >> 4);
                month = (charToIndex.get(s.charAt(len - 7)) & 15) + 1;
                day = (charToIndex.get(s.charAt(len - 6)) % 31) + 1;
                hour = charToIndex.get(s.charAt(len - 5)) % 24;
                second = charToIndex.get(s.charAt(len - 4));
                // 解析毫秒部分（3位62进制）
                String millisStr = s.substring(len - 3, len);
                millisecond = (int) parse62To10(millisStr);
                break;
        }

        return LocalDateTime.of(year, month, day, hour, minute, second, millisecond * 1_000_000);
    }

    /**
     * 单字符62进制转10进制
     */
    private static int parse62To10(char c) {
        Integer index = charToIndex.get(c);
        if (index == null) {
            throw new IllegalArgumentException("Invalid character: " + c);
        }
        return index;
    }

    /**
     * 获取推荐的长度（根据精度自动计算）
     *
     * @param precision 时间精度
     * @return 推荐的长度
     */
    private static int getRecommendedSize(TimePrecision precision) {
        switch (precision) {
            case DAY:
                return 8; // 2位时间戳 + 6位随机数
            case HOUR:
                return 9; // 3位时间戳 + 6位随机数
            case MINUTE:
                return 10; // 4位时间戳 + 6位随机数
            case SECOND:
                return 11; // 5位时间戳 + 6位随机数
            case MILLISECOND:
                return 14; // 8位时间戳 + 6位随机数
            default:
                return 10;
        }
    }

    /**
     * 62进制字符串转10进制
     */
    private static long parse62To10(String c) {
        long result = 0;
        for (int i = 0; i < c.length(); i++) {
            result = result * 62 + parse62To10(c.charAt(i));
        }
        return result;
    }

    /**
     * 10进制转62进制
     */
    private static String parse10To62(long n) {
        if (n == 0)
            return "0";

        StringBuilder sb = new StringBuilder();
        while (n != 0) {
            int digit = (int) (n % 62);
            sb.insert(0, scale[digit]);
            n = n / 62;
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(Long.MAX_VALUE);
        System.out.println(Integer.MAX_VALUE);
        System.out.println(Math.pow(2, 2));
        System.out.println(System.currentTimeMillis());
        for (int i = 0; i < 10; i++) {
            System.out.println(generateStr(10, null, TimePrecision.SECOND));
        }

        // 测试新的generateGlobalStr方法
        System.out.println("\n测试generateGlobalStr方法:");
        System.out.println("毫秒级: " + generateGlobalStr());
        System.out.println("秒级: " + generateGlobalStr(null, TimePrecision.SECOND));
        System.out.println("分组毫秒级: " + generateGlobalStr(null));

        // 测试毫秒级精度
        String millisecondStr = generateStr(14, null, TimePrecision.MILLISECOND);
        System.out.println("\n测试毫秒级精度:");
        System.out.println("生成的字符串: " + millisecondStr);
        System.out.println("解析的时间: " + getCreateTime(millisecondStr, TimePrecision.MILLISECOND));
    }

}