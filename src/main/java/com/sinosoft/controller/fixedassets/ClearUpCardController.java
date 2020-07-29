package com.sinosoft.controller.fixedassets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clearupcard")
public class ClearUpCardController {
    private Logger logger = LoggerFactory.getLogger(ClearUpCardController.class);

    @RequestMapping("/")
    public String page(){ return "fixedassets/clearupcard"; }
}
