package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONObject;
import com.sinosoft.httpclient.domain.Jstoken;
import com.sinosoft.httpclient.service.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "testHttp")
public class Test1Controller {
    @Autowired
    HttpClient httpClient ;
    /**
     * json转对象
     * @return
     */
    @RequestMapping(value = "1")
    public void getAccessToken(){

        String url = "https://api.weixin.qq.com/cgi-bin/token";
        //添加参数
        Map<String, String> uriMap = new HashMap<>(6);
        // uriMap.put("startTime","");
        // uriMap.put("endTime", new Date().toString());
        uriMap.put("appid","wx1d3b0f41812203d9&secret=fab2317a0f3786c9df4d71e519aaf82a");
        uriMap.put("grant_type","client_credential");
        String jstoken = httpClient.sendGet2(url,uriMap);

        //转json对象
        JSONObject jsonObject1 =JSONObject.parseObject(jstoken);

        // json 转对象
        Jstoken jstoken1 = (Jstoken)JSONObject.toJavaObject(jsonObject1, Jstoken.class);

        System.out.println(jstoken1);

    }
}

/**
 * 以下为list 的解析方式
 {"quesitons":
     [
        {
            "questionId": 0,
            "questionType": 0,
            "question": "string",
            "answer": "string",
            "choiceNum": 0,
            "choiceStr": "string",
            "questionareId": 0
        },
         {
         "questionId": 0,
         "questionType": 0,
         "question": "string",
         "answer": "string",
         "choiceNum": 0,
         "choiceStr": "string",
         "questionareId": 0
         }
     ]
 }

 代码如下：
 JSONArray questions_json = questionare_json.getJSONArray("questions");
 for(int i = 0; i < questions_json.size(); i++){
     JSONObject question_json = (JSONObject)questions_json.getJSONObject(i); //这里不能是get(i),get(i)只会得到键值对
     Question question = (Question)JSONObject.toJavaObject(question_json,Question.class);
 }

 **/