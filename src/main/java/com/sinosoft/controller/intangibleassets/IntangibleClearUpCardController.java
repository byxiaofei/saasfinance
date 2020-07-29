package com.sinosoft.controller.intangibleassets;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/intangibleclearupcard")
public class IntangibleClearUpCardController {
    @RequestMapping("/")
    public String page(){ return "intangibleassets/clearupcard"; }
}
