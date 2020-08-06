package com.sinosoft.httpclient.service;

import com.sinosoft.util.CommonUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class HttpClient {
    /**
     * 生成get参数请求url
     * 示例：https://0.0.0.0:80/api?u=u&o=o
     * 示例：https://0.0.0.0:80/api
     *
     * @param uri      请求的uri 示例: 0.0.0.0:80
     * @param params   请求参数
     * @return
     */
    public String generateRequestParameters( String uri, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(uri);
        if (!CommonUtil.isEmpty(params)) {
            sb.append("?");
            for (Map.Entry map : params.entrySet()) {
                sb.append(map.getKey())
                        .append("=")
                        .append(map.getValue())
                        .append("&");
            }
            uri = sb.substring(0, sb.length() - 1);
            return uri;
        }
        return sb.toString();
    }

    /**
     * get请求
     * @return
     */

    public String sendGet(String uri, Map<String, String> uriMap) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = restTemplate.getForEntity(generateRequestParameters(uri, uriMap), String.class);
        // 可以将下面的T 换成类名 可以直接将返回数据转对象
       //  xxxDto dto  = restTemplate.getForEntity(generateRequestParameters(uri, uriMap), String.class);
        return (String) responseEntity.getBody();
    }

}
