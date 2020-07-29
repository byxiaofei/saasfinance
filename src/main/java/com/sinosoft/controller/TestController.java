package com.sinosoft.controller;

import com.sinosoft.common.InvokeResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: luodejun
 * @Date: 2020/3/17 15:49
 * @Description:
 */
@Controller
@RequestMapping(value = "/test/")
public class TestController {

    /**
     *
     * 功能描述:    测试
     *
     */
    @RequestMapping(value = "testOne")
    @ResponseBody
    public InvokeResult testInfo(){
        return InvokeResult.success("成功");
    }
}
