package com.sinosoft.common;

import java.lang.annotation.*;

/**
 * 自定义注解类 SQL打印参数值
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysPringLog {
    String value() default "";
}
