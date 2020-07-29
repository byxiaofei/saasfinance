package com.sinosoft.common.interceptor;

import com.sinosoft.util.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class TokenInterceptor extends HandlerInterceptorAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenInterceptor.class);
	 
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Token annotation = method.getAnnotation(Token.class );
            if (annotation != null ) {
            	boolean flag = TokenHelper.validToken(request);
            	if (!flag) {
            		LOGGER.error("表单重复提交！");
            		return false ;
    			}
            }
            return true ;
        } else {
            return super.preHandle(request, response, handler);
        }
    }

}
