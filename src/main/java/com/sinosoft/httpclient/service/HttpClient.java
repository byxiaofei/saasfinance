package com.sinosoft.httpclient.service;

import com.sinosoft.util.CommonUtil;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


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
    public String generateRequestParameters( String uri, Map<String, Long> params) {
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

    public String sendGet(String url, Map<String, Long> uriMap) {

        RestTemplate restTemplate = new RestTemplate();
        //添加请求头
        HttpHeaders headers = new HttpHeaders();
       // headers.add("x-api-key", "c18f9f88-b85a-4585-9593-b0df09f05680");
        headers.add("x-api-key", "c9423f7e-0240-44d6-afe3-95bf29db2308");

        //添加请求的实体类，这里第一个参数是要发送的参数，第二个参数是请求头里的数据
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        url = generateRequestParameters(url, uriMap);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        if (HttpStatus.OK == responseEntity.getStatusCode()) {
            return (String) responseEntity.getBody();
        } else {
            // log.error("#method# 远程调用失败 httpCode = [{}]", responseEntity.getStatusCode());
            return (String) "接口调用失败";

        }

    }

}
