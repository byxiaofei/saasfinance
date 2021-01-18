package com.sinosoft.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author hotcosmos
 */
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        redisUtil = this;
    }

    /**
     * 根据 key 数据
     * @param key 关键字
     * @return key 对应的缓存数据
     */
    public static Object get(String key) {
        return redisUtil.redisTemplate.opsForValue().get(key);
    }

    /**
     * 将 key value 数据保存到redis
     * @param key 关键字
     * @param value 数据
     */
    public static void set(String key, Object value) {
        redisUtil.redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 是否存在对应key的数据
     * @param key
     * @return 存在 true；
     */
    public static Boolean exists(String key) {
        return redisUtil.redisTemplate.opsForValue().getOperations().hasKey(key);
    }

    public static void expire(String key, long timeout) {
        redisUtil.redisTemplate.opsForValue().getOperations().expire(key, timeout, TimeUnit.SECONDS);
    }

    public static void set(String key, Object value, long timeout) {
        redisUtil.redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public static Object getSet(String key, Object value) {
        return redisUtil.redisTemplate.opsForValue().getAndSet(key, value);
    }

    public static Long incr(String key) {
        return redisUtil.redisTemplate.opsForValue().increment(key);
    }

    public static Set<String> keys(String pattern) {
        return redisUtil.redisTemplate.keys(pattern);
    }

    public static Boolean setNx(String key, Object value) {
        return redisUtil.redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public static Long ttl(String key) {
        return redisUtil.redisTemplate.opsForValue().getOperations().getExpire(key);
    }

    public static void del(String key) {
        redisUtil.redisTemplate.delete(key);
    }

    public static void hSet(String key, String field, Object value) {
        redisUtil.redisTemplate.opsForHash().put(key, field, value);
    }

    public static Object hGet(String key, String field) {
        return redisUtil.redisTemplate.opsForHash().get(key, field);
    }

    public static void hDel(String key, Object... field) {
        redisUtil.redisTemplate.opsForHash().delete(key, field);
    }

    public static void hDel(String key) {
        redisUtil.redisTemplate.opsForHash().getOperations().delete(key);
    }

    public static Boolean hExists(String key) {
        return redisUtil.redisTemplate.hasKey(key);
    }

    public static Boolean hExists(String key, String field) {
        return redisUtil.redisTemplate.opsForHash().hasKey(key, field);
    }

    public static void hExpire(String key, long timeout) {
        redisUtil.redisTemplate.opsForHash().getOperations().expire(key, timeout, TimeUnit.SECONDS);
    }
}
