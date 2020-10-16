package com.sinosoft.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.SysOperationLog;
import com.sinosoft.repository.SysOperationLogRepository;
import com.sinosoft.util.IPUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 系统操作日志：切面处理类
 */
@Aspect
@Component
public class SysLogServiceImpl {
    private Logger logger = LoggerFactory.getLogger(SysLogServiceImpl.class);
    @Autowired
    private SysOperationLogRepository sysOperationLogRepository;

    //定义切点 @Pointcut
    //在注解的位置切入代码
    @Pointcut("@annotation(com.sinosoft.common.SysLog)")
    public void logPoinCut() {

    }

    //切面 配置通知
    @AfterReturning("logPoinCut()")
    public void saveSysLog(JoinPoint joinPoint) {
        try {
            /*System.out.println("系统操作日志切面");*/
            //保存日志
            SysOperationLog sysOperationLog = new SysOperationLog();

            //从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取切入点所在的方法
            Method method = signature.getMethod();

            //获取操作
            SysLog sysLog = method.getAnnotation(com.sinosoft.common.SysLog.class);
            if (sysOperationLog != null) {
                String value = sysLog.value();
                sysOperationLog.setOperation(value);//保存获取的操作
            }
            //获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            //获取请求的方法名
            String methodName = method.getName();
            sysOperationLog.setMethod(className + "." + methodName);
            //请求的参数
            Object[] args = joinPoint.getArgs();
            //将参数所在的数组转换成json
            ObjectMapper mapper = new ObjectMapper();
            String params = mapper.writeValueAsString(args);
            /*String params = JSON.toJSONString(args);*/
            if(params.length() > 5000){
                params = params.substring(0,2000);
            }
            sysOperationLog.setParams(params);
            //获取用户Id
            sysOperationLog.setUserId(CurrentUser.getCurrentUser().getId());
            //获取用户ip地址
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            sysOperationLog.setIp(IPUtils.getClientIp(request));
            sysOperationLog.setCreateDate(CurrentTime.getCurrentTime());

            //调用service保存SysLog实体类到数据库
            sysOperationLogRepository.save(sysOperationLog);
        } catch (Exception e) {
            logger.error("系统操作日志记录异常", e);
        }
    }
}
