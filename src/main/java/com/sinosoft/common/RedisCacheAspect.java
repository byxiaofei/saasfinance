package com.sinosoft.common;

import com.sinosoft.util.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author CHENG
 **/
@Aspect
@Component
public class RedisCacheAspect {
    @Around(value="@annotation(com.sinosoft.common.RedisCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RedisCache redisCache = method.getAnnotation(RedisCache.class);
        Object[] args = joinPoint.getArgs();
        String prefixKey= StringUtils.join(args, "_");
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String redisKey =redisCache.prefixKey().concat(accBookCode+"_"+prefixKey) ;
        Object obj = RedisUtil.get(redisKey);
        if(obj!=null){ return obj; }
        else{
            //调用在其上设置切入点的方法
            obj = joinPoint.proceed(); }
        if(obj!=null){
            RedisUtil.set(redisKey, obj,redisCache.time());
        }
        return obj;
    }
}
