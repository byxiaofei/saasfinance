package com.sinosoft.util;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class IPUtils {

    public static String getClientIp(HttpServletRequest request) {
        //X-Forwarded-For，不区分大小写
        String possibleIpStr = request.getHeader("X-Forwarded-For");
        if(!(StringUtils.isNotBlank(possibleIpStr) && !"unknown".equalsIgnoreCase(possibleIpStr))){
            possibleIpStr = request.getHeader("Proxy-Client-IP");
        }
        if(!(StringUtils.isNotBlank(possibleIpStr) && !"unknown".equalsIgnoreCase(possibleIpStr))){
            possibleIpStr = request.getHeader("WL-Proxy-Client-IP");
        }
        if(!(StringUtils.isNotBlank(possibleIpStr) && !"unknown".equalsIgnoreCase(possibleIpStr))){
            possibleIpStr = request.getHeader("HTTP_CLIENT_IP");
        }
        if(!(StringUtils.isNotBlank(possibleIpStr) && !"unknown".equalsIgnoreCase(possibleIpStr))){
            possibleIpStr = request.getHeader("X-Real-IP");
        }

        String remoteIp = request.getRemoteAddr();
        String clientIp;
        if (StringUtils.isNotBlank(possibleIpStr) && !"unknown".equalsIgnoreCase(possibleIpStr)) {
            //可能经过好几个转发流程，第一个是用户的真实ip，后面的是转发服务器的ip
            clientIp = possibleIpStr.split(",")[0].trim();
        } else {
            //如果转发头ip为空，说明是直接访问的，没有经过转发
            clientIp = remoteIp;
        }
        return clientIp.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":clientIp;
    }

    public static void main(String[] args) {
        Map<Integer,Object> bean = new HashMap<>();
        bean.put(1,"123");
        bean.put(2,"2323");
        System.out.println(bean.size());
    }
}
