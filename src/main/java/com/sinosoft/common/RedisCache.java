package com.sinosoft.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {

    //失效时间 毫秒
    long time() default Constant.TIME_OUT;

    //redis 前缀
    String prefixKey() default "";


}