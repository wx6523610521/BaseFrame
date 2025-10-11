package work.chncyl.base.global.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import work.chncyl.base.global.utils.SpringUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtils implements CommandLineRunner {
    private static final Integer RELEASE_SUCCESS = 1;

    private static RedisTemplate<String, Object> redisTemplate;

    /**
     * ============================================== 基本操作 ==========================================================
     */
    public static void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public static void set(String key, Object value, long seconds) {
        set(key, value, seconds, TimeUnit.SECONDS);
    }


    public static void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 若不存在key时, 向redis中添加key-value, 返回成功/失败。
     * 若存在，则不作任何操作, 返回false。
     *
     * @param key   key
     * @param value key对应的value
     * @return set是否成功
     */
    public static boolean setIfAbsent(String key, String value) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value);
        if (result == null) {
            return false;
        }
        return result;
    }

    /**
     * 若不存在key时, 向redis中添加一个(具有超时时长的)key-value, 返回成功/失败。
     * 若存在，则不作任何操作, 返回false。
     *
     * @param key     key
     * @param value   key对应的value
     * @param seconds 过期时间（秒）
     * @return set是否成功
     */
    public static boolean setIfAbsent(String key, String value, Integer seconds) {
        return setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
    }

    public static boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        if (result == null) {
            return false;
        }
        return result;
    }

    public static <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    public static Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 获取key对应的key-value的过期时间
     * 若key-value永不过期， 那么返回的为-1。
     * 若不存在key对应的key-value， 那么返回的为-2
     * 若存在零碎时间不足1 SECONDS,则(大体上)四舍五入到SECONDS级别。
     *
     * @param key 定位key-value的key
     * @return 过期时间(单位s)
     */
    public static long getExpire(String key) {
        return getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 获取key对应的key-value的过期时间
     *
     * @param key 定位key-value的key
     * @return 过期时间(单位unit)
     */
    public static Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 重命名对应的oldKey为新的newKey
     * <p>
     * 注: 若oldKey不存在， 则会抛出异常.
     * 注: 若redis中已存在与newKey一样的key,
     * 那么原key-value会被丢弃，
     * 只留下新的key，以及原来的value
     * 示例说明: 假设redis中已有 (keyAlpha, valueAlpha) 和 (keyBeta, valueBeat),
     * 在使用rename(keyAlpha, keyBeta)替换后, redis中只会剩下(keyBeta, valueAlpha)
     *
     * @param oldKey 旧的key
     * @param newKey 新的key
     */
    public static void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 当redis中不存在newKey时, 重命名对应的oldKey为新的newKey。
     * 否者不进行重命名操作。
     * 注: 若oldKey不存在， 则会抛出异常.
     *
     * @param oldKey 旧的key
     * @param newKey 新的key
     */
    public static Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    public static Long increment(String key, int delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public static Long incre(String key) {
        return redisTemplate.opsForValue().increment(key, 1);
    }

    public static Long incre(String key, Integer expireSeconds) {
        Long serialNumber = redisTemplate.opsForValue().increment(key, 1);
        if (serialNumber != null && serialNumber == 1) {
            redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
        return serialNumber;
    }

    public static Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 为key设置过期时间，单位毫秒
     *
     * @param key  指定的key
     * @param time 过期时间（毫秒）
     */
    public static void expire(String key, long time) {
        redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
    }


    public static Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public static Long deleteObject(Collection<String> collection) {
        return redisTemplate.delete(collection);
    }

    public static Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * ============================================== set操作 ==========================================================
     * set中的元素，不可以重复。
     * set是无序的。
     */
    public static class SetOps {
        /**
         * 放入set
         */
        public static Long sSet(String key, Object... value) {
            return redisTemplate.opsForSet().add(key, value);
        }

        /**
         * 判断value是否为key的成员
         */
        public static Boolean isMember(String key, Object value) {
            return redisTemplate.opsForSet().isMember(key, value);
        }

        /**
         * 批量判断value是否为key的成员
         */
        public static <T> Map<T, Boolean> isMember(String key, T... value) {
            return (Map<T, Boolean>) redisTemplate.opsForSet().isMember(key, value);
        }

        /**
         * 获取set中元素数量
         */
        public static Long sSize(String key) {
            return redisTemplate.opsForSet().size(key);
        }

        /**
         * 对称差集
         */
        public static Set<Object> difference(String key, String otherKey) {
            return redisTemplate.opsForSet().difference(key, otherKey);
        }

        /**
         * 交集
         */
        public static Set<Object> intersect(String key, String otherKey) {
            return redisTemplate.opsForSet().intersect(key, otherKey);
        }

        /**
         * 并集
         */
        public static Set<Object> intersect(String... key) {
            return intersect(Arrays.asList(key));
        }

        /**
         * 并集
         */
        public static Set<Object> intersect(List<String> keys) {
            return redisTemplate.opsForSet().union(keys);
        }

        /**
         * 随机弹出并删除一个成员
         */
        public static <T> T sPop(String keys) {
            return (T) redisTemplate.opsForSet().pop(keys);
        }

        /**
         * 随机获取成员
         */
        public static <T> T sRandomMembers(String key) {
            List<Object> objects = sRandomMembers(key, 1);
            return objects.isEmpty() ? null : (T) objects.get(0);
        }

        /**
         * 随机获取指定<b>次数</b>成员，成员可能重复获取
         */
        public static List<Object> sRandomMembers(String key, long num) {
            return redisTemplate.opsForSet().randomMembers(key, num);
        }

        /**
         * 随机获取指定数量成员，获取的成员不会重复
         */
        public static Set<Object> sDistinctRandomMembers(String key, long num) {
            return redisTemplate.opsForSet().distinctRandomMembers(key, num);
        }
    }


    /**
     * ============================================== zset ==========================================================
     */
    public static class ZSetOps {
        /**
         * 放入zset
         */
        public static Long zSet(String key, Set<TypedTuple<Object>> value) {
            return redisTemplate.opsForZSet().add(key, value);
        }

        /**
         * 放入zset
         */
        public static Boolean zSet(String key, Object value, Double score) {
            return redisTemplate.opsForZSet().add(key, value, score);
        }

        /**
         * 不存在则放入zset
         */
        public static Long zSetIfAbsent(String key, Set<TypedTuple<Object>> value) {
            return redisTemplate.opsForZSet().addIfAbsent(key, value);
        }

        /**
         * 不存在则放入zset
         */
        public static Boolean zSetIfAbsent(String key, Object value, Double score) {
            return redisTemplate.opsForZSet().addIfAbsent(key, value, score);
        }

        /**
         * 成员数
         */
        public static Long zSize(String key) {
            return redisTemplate.opsForZSet().size(key);
        }

        /**
         * 获取成员分数
         */
        public static Double zScore(String key, TypedTuple<Object> value) {
            return redisTemplate.opsForZSet().score(key, value);
        }

        /**
         * 增加分数
         *
         * @return 增加后的元素分数
         */
        public static Double zIncrementScore(String key, Object value, Double score) {
            return redisTemplate.opsForZSet().incrementScore(key, value, score);
        }

        /**
         * key在分数范围内的元素数
         */
        public static Long zCount(String key, Double min, Double max) {
            return redisTemplate.opsForZSet().count(key, min, max);
        }

        /**
         * 移除元素
         */
        public static Long zRemove(String key, Object... values) {
            return redisTemplate.opsForZSet().remove(key, values);
        }


        /**
         * 获取分数排序（由小到大）后指定下标范围的元素
         */
        public static Set<Object> zRange(String key, Long min, Long max) {
            return redisTemplate.opsForZSet().range(key, min, max);
        }

        /**
         * 获取分数排序（由小到大）后指定分数范围的元素
         */
        public static Set<Object> zRangeByScore(String key, Double min, Double max) {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        }


        /**
         * 获取分数倒序排序（由大到小）后指定下标范围的元素
         */
        public static Set<Object> zReverseRange(String key, Long min, Long max) {
            return redisTemplate.opsForZSet().reverseRange(key, min, max);
        }

        /**
         * 获取分数倒序排序（由大到小）后指定分数范围的元素
         */
        public static Set<Object> zReverseRangeByScore(String key, Double min, Double max) {
            return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
        }

        /**
         * 弹出并删除分数最大的成员
         * 需要redis>=5.0
         */
        public static TypedTuple<Object> zPopMax(String key) {
            return redisTemplate.opsForZSet().popMax(key);
        }

        /**
         * 弹出并删除分数最小的成员
         * 需要redis>=5.0
         */
        public static TypedTuple<Object> zPopMin(String key) {
            return redisTemplate.opsForZSet().popMin(key);
        }
    }

    /**
     * ============================================== List ==========================================================
     */
    public static class ListOps {
        /**
         * 增加元素
         */
        public static void lSet(String key, long index, Object value) {
            redisTemplate.opsForList().set(key, index, value);
        }

        /**
         * 队列右侧增加元素
         */
        public static void lRightSet(String key, Object value) {
            redisTemplate.opsForList().rightPush(key, value);
        }

        /**
         * 队列左侧增加元素
         */
        public static void lLeftSet(String key, Object value) {
            redisTemplate.opsForList().leftPush(key, value);
        }

        /**
         * 队列右侧增加元素
         */
        public static void lRightSet(String key, Collection<Object> value) {
            redisTemplate.opsForList().rightPushAll(key, value);
        }

        /**
         * 队列左侧增加元素
         */
        public static void lLeftSet(String key, Collection<Object> value) {
            redisTemplate.opsForList().leftPushAll(key, value);
        }

        /**
         * 队列右侧增加元素
         */
        public static void lRightSet(String key, Object... value) {
            redisTemplate.opsForList().rightPushAll(key, value);
        }

        /**
         * 队列左侧增加元素
         */
        public static void lLeftSet(String key, Object... values) {
            redisTemplate.opsForList().leftPushAll(key, values);
        }

        /**
         * 队列右侧弹出并删除元素
         */
        public static <T> T lRightPop(String key) {
            return (T) redisTemplate.opsForList().rightPop(key);
        }

        /**
         * 队列左侧弹出并删除元素
         */
        public static <T> T lLeftPop(String key) {
            return (T) redisTemplate.opsForList().leftPop(key);
        }

        /**
         * 元素最开始出现的下标
         */
        public static Long lIndexOf(String key, Object value) {
            return redisTemplate.opsForList().indexOf(key, value);
        }

        /**
         * 元素最后出现的下标
         */
        public static Long lLastIndexOf(String key, Object value) {
            return redisTemplate.opsForList().lastIndexOf(key, value);
        }

        /**
         * 长度
         */
        public static Long lSize(String key) {
            return redisTemplate.opsForList().size(key);
        }
    }

    /**
     * ============================================== hash ==========================================================
     */
    public static class HashOps {
        /**
         * 向key对应的hash中，增加一个键值对hashKey-entryValue
         * <p>
         * 同一个hash里面，若已存在相同的hashKey， 那么此操作将丢弃原来的hashKey-entryValue，
         * 而使用新的hashKey-entryValue。
         *
         * @param key        定位hash的key
         * @param hashKey    要向hash中增加的键值对里的 键
         * @param entryValue 要向hash中增加的键值对里的 值
         */
        public static void hSet(String key, Object hashKey, Object entryValue) {
            redisTemplate.opsForHash().put(key, hashKey, entryValue);
        }

        /**
         * 向key对应的hash中，增加maps(即: 批量增加entry集)
         * <p>
         * 同一个hash里面，若已存在相同的hashKey， 那么此操作将丢弃原来的hashKey-entryValue，
         * 而使用新的hashKey-entryValue
         *
         * @param key  定位hash的key
         * @param maps 要向hash中增加的键值对集
         */
        public static void hPutAll(String key, Map<Object, Object> maps) {
            redisTemplate.opsForHash().putAll(key, maps);
        }

        /**
         * 当key对应的hash中,不存在hashKey时，才(向key对应的hash中，)增加hashKey-entryValue
         * 否者，不进行任何操作
         *
         * @param key        定位hash的key
         * @param hashKey    要向hash中增加的键值对里的 键
         * @param entryValue 要向hash中增加的键值对里的 值
         * @return 操作是否成功。
         */
        public static boolean hPutIfAbsent(String key, Object hashKey, Object entryValue) {
            return redisTemplate.opsForHash().putIfAbsent(key, hashKey, entryValue);
        }

        /**
         * 获取到key对应的hash里面的对应字段的值
         * <p>
         * 若redis中不存在对应的key, 则返回null。
         * 若key对应的hash中不存在对应的hashKey, 也会返回null。
         *
         * @param key     定位hash的key
         * @param hashKey 定位hash里面的entryValue的hashKey
         * @return key对应的hash里的hashKey对应的entryValue值
         */
        public static <T> T hGet(String key, Object hashKey) {
            return (T) redisTemplate.opsForHash().get(key, hashKey);
        }

        /**
         * 获取到key对应的hash(即: 获取到key对应的Map<HK, HV>)
         * <p>
         * 若redis中不存在对应的key, 则返回一个没有任何entry的空的Map(，而不是返回null)。
         *
         * @param key 定位hash的key
         * @return key对应的hash。
         */
        public static <K, V> Map<K, V> hGetAll(String key) {
            return (Map<K, V>) redisTemplate.opsForHash().entries(key);
        }

        /**
         * 批量获取(key对应的)hash中的hashKey的entryValue
         * <p>
         * 若hash中对应的hashKey不存在，那么返回的对应的entryValue值为null
         * redis中key不存在，那么返回的List中，每个元素都为null(这个List本身不为null, size也不为0， 只是每个list中的每个元素为null而已)
         *
         * @param key      定位hash的key
         * @param hashKeys 需要获取的hash中的字段集
         * @return hash中对应hashKeys的对应entryValue集
         */
        public static List<Object> hMultiGet(String key, Collection<Object> hashKeys) {
            return redisTemplate.opsForHash().multiGet(key, hashKeys);
        }

        /**
         * (批量)删除(key对应的)hash中的对应hashKey-entryValue
         * <p>
         * 1、若redis中不存在对应的key, 则返回0;
         * 2、若要删除的hashKey，在map中不存在，count不会+1, 如:
         * RedisUtil.HashOps.hPut("ds", "name", "邓沙利文");
         * RedisUtil.HashOps.hPut("ds", "birthday", "1994-02-05");
         * RedisUtil.HashOps.hPut("ds", "hobby", "女");
         * 则调用RedisUtil.HashOps.hDelete("ds", "name", "birthday", "hobby", "non-exist-hashKey")
         * 的返回结果为3
         * ！！！若(key对应的)hash中的所有entry都被删除了，那么该key也会被删除
         *
         * @param key      定位hash的key
         * @param hashKeys 定位要删除的hashKey-entryValue的hashKey
         * @return 删除了对应hash中多少个entry
         */
        public static long hDelete(String key, Object... hashKeys) {
            return redisTemplate.opsForHash().delete(key, hashKeys);
        }

        /**
         * 查看(key对应的)hash中，是否存在hashKey对应的entry
         * <p>
         * 若redis中不存在key,则返回false。
         * 若key对应的hash中不存在对应的hashKey, 也会返回false。
         *
         * @param key     定位hash的key
         * @param hashKey 定位hash中entry的hashKey
         * @return hash中是否存在hashKey对应的entry.
         */
        public static boolean hExists(String key, Object hashKey) {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        }

        /**
         * 增/减(hash中的某个entryValue值) 整数
         * <p>
         * 负数则为减。
         * 若key不存在，那么会自动创建对应的hash,并创建对应的hashKey、entryValue,entryValue的初始值为increment。
         * 若hashKey不存在，那么会自动创建对应的entryValue,entryValue的初始值为increment。
         * 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会抛出异常
         *
         * @param key       用于定位hash的key
         * @param hashKey   用于定位entryValue的hashKey
         * @param increment 增加多少
         * @return 增加后的总值。
         */
        public static long hIncrBy(String key, Object hashKey, long increment) {
            return redisTemplate.opsForHash().increment(key, hashKey, increment);
        }

        /**
         * 增/减(hash中的某个entryValue值) 浮点数
         * <p>
         * 负数则为减。
         * 若key不存在，那么会自动创建对应的hash,并创建对应的hashKey、entryValue,entryValue的初始值为increment。
         * 若hashKey不存在，那么会自动创建对应的entryValue,entryValue的初始值为increment。
         * 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会抛出异常
         * 因为是浮点数， 所以可能会出现精度问题。
         *
         * @param key       用于定位hash的key
         * @param hashKey   用于定位entryValue的hashKey
         * @param increment 增加多少
         * @return 增加后的总值。
         */
        public static double hIncrByFloat(String key, Object hashKey, double increment) {
            return redisTemplate.opsForHash().increment(key, hashKey, increment);
        }

        /**
         * 获取(key对应的)hash中的所有hashKey
         * <p>
         * 若key不存在，则返回的是一个空的Set(而不是返回null)
         *
         * @param key 定位hash的key
         * @return hash中的所有hashKey
         */
        public static Set<Object> hKeys(String key) {
            return redisTemplate.opsForHash().keys(key);
        }

        /**
         * 获取(key对应的)hash中的所有entryValue
         * <p>
         * 若key不存在，则返回的是一个空的List(而不是返回null)
         *
         * @param key 定位hash的key
         * @return hash中的所有entryValue
         */
        public static List<Object> hValues(String key) {
            return redisTemplate.opsForHash().values(key);
        }

        /**
         * 获取(key对应的)hash中的所有entry的数量
         * <p>
         * 若redis中不存在对应的key, 则返回值为0
         *
         * @param key 定位hash的key
         * @return (key对应的)hash中, entry的个数
         */
        public static long hSize(String key) {
            return redisTemplate.opsForHash().size(key);
        }

        /**
         * 根据options匹配到(key对应的)hash中的对应的hashKey, 并返回对应的entry集
         * <p>
         * ScanOptions实例的创建方式举例:
         * 1、ScanOptions.NONE
         * 2、ScanOptions.scanOptions().match("n??e").build()
         *
         * @param key     定位hash的key
         * @param options 匹配hashKey的条件
         *                ScanOptions.NONE表示全部匹配。
         *                ScanOptions.scanOptions().match(pattern).build()表示按照pattern匹配,
         *                其中pattern中可以使用通配符 * ? 等,
         *                * 表示>=0个字符
         *                ？ 表示有且只有一个字符
         * @return 匹配到的(key对应的)hash中的entry
         */
        public static Cursor<Map.Entry<Object, Object>> hScan(String key, ScanOptions options) {
            Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(key, options);
            return cursor;
        }
    }


    /**
     * ============================================== Redis锁 ==========================================================
     */
    public static class LockOps {
        /**
         * redis锁
         */
        public static Boolean tryGetDistributedLock(String lockKey, String requestId, long expireTime) {
            return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, Duration.ofSeconds(expireTime));
        }

        /**
         * 锁释放
         */
        public static boolean releaseDistributedLock(String lockKey, String requestId) {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Integer> redisScript = new DefaultRedisScript(script);
            Integer execute = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
            return RELEASE_SUCCESS.equals(execute);
        }
    }


    public void run(String... args) {
        redisTemplate = (RedisTemplate<String, Object>) SpringUtils.getBean("ObjectRedisTemplate", RedisTemplate.class);
    }
}
