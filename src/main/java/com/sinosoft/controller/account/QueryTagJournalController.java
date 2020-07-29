package com.sinosoft.controller.account;

import com.sinosoft.service.account.QueryTagJournalService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "querytagjournal")
public class QueryTagJournalController {
    @Resource
    QueryTagJournalService queryTagJournalService ;
    @RequestMapping("/")
    public String Page(){
        return "account/querytagjournalvoucher";
    }

    @RequestMapping(path = "VoucherTagList")
    @ResponseBody
    public List<?> qryVoucherTag(String yearMonthDateBegin , String yearMonthDateEnd , String DateStart , String DateStop , String tagCode){
        List<?> result = new ArrayList<>();
        result = queryTagJournalService.qryVoucherTag(yearMonthDateBegin,yearMonthDateEnd,DateStart,DateStop,tagCode);
        return result ;
    }

    @RequestMapping(path = "TagJouralList")
    @ResponseBody
    public List<?> queryTagJournal(String value){
        List result = new ArrayList();
        result = queryTagJournalService.queryTagJournal(value);
        return result ;
    }

}
