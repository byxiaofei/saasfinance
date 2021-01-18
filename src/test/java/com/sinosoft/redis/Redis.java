package com.sinosoft.redis;

import com.sinosoft.common.RedisConstant;
import com.sinosoft.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author CHENG
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Redis {
    @Autowired
    private RedisTemplate redisTemplate; //在MyRedisConfig文件中配置了redisTemplate的序列化之后， 客户端也能正确显示键值对了

   /* @Test
    public void testRedisSet(){
        redisTemplate.opsForValue().set(RedisConstant.SYSTEM_SUBJECT_ALL_KEY_PREFIX, "1234");
    }

    @Test
    public void testRedisGet(){
        String subject = (String)redisTemplate.opsForValue().get(RedisConstant.SYSTEM_SUBJECT_ALL_KEY_PREFIX);
        System.out.println(subject);
    }*/



   @Test
   public void testRedisSet(){
       RedisUtil.set(RedisConstant.SYSTEM_SUBJECT_ALL_KEY_PREFIX.concat("100100001"), "123456");
   }

    @Test
    public void testRedisExists(){
        Boolean exists = RedisUtil.exists(RedisConstant.SYSTEM_SUBJECT_ALL_KEY_PREFIX.concat("100100001"));
        System.out.println(exists);
    }

    @Test
    public void testRedisGet(){
        String subject = (String)RedisUtil.get(RedisConstant.SYSTEM_SUBJECT_ALL_KEY_PREFIX.concat("100100001"));
        System.out.println(subject);
    }
}
