package com.sinosoft.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 系统操作日志：切面处理类
 */
@Aspect
@Component
public class SysPringLogAspect{
    private Logger logger = LoggerFactory.getLogger(SysPringLogAspect.class);


    //定义切点 @Pointcut
    @Pointcut("@annotation(com.sinosoft.common.SysPringLog)")
    public void logPoinCut() {

    }

    //切面 配置通知
    @AfterReturning("logPoinCut()")
    public void saveSysLog(JoinPoint joinPoint) {
        try {

            StringBuilder pringtLog = new StringBuilder();
            //从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取切入点所在的方法
            Method method = signature.getMethod();

            //获取操作
            SysPringLog sysPringLog = method.getAnnotation(com.sinosoft.common.SysPringLog.class);
            String value = sysPringLog.value();
            pringtLog.append("\n操作描述："+value+"\n");
            //获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            //获取请求的方法名
            String methodName = method.getName();
            pringtLog.append("方法名："+ className + "." + methodName+"\n");
            //请求的参数
            Object[] args = joinPoint.getArgs();
            //将参数所在的数组转换成json
            ObjectMapper mapper = new ObjectMapper();
            String params = mapper.writeValueAsString(args);
            /*String params = JSON.toJSONString(args);*/
            if(params.length() > 5000){
                params = params.substring(0,2000);
            }
            pringtLog.append("请求参数："+params+"\n");
            //获取用户Id
            pringtLog.append("操作人："+CurrentUser.getCurrentUser().getId());
            logger.info(pringtLog.toString());

        } catch (Exception e) {
            logger.error("系统打印日志记录异常", e);
        }
    }
}
