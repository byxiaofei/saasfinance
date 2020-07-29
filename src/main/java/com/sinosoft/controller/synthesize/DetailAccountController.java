package com.sinosoft.controller.synthesize;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.service.synthesize.DetailAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/detailaccount")
public class DetailAccountController {
    private Logger logger = LoggerFactory.getLogger(DetailAccountController.class);
    @Resource
    private DetailAccountService detailAccountService;
    @Resource
    BranchInfoRepository branchInfoRepository;

    @RequestMapping("/")
    public String page(){ return "synthesize/detailaccount"; }

    /**
     * 明细账查询
     * @param dto
     * @param synthDetailAccount 综合明细账（0-否 1-是）
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public List<?> queryDetailAccount(VoucherDTO dto, String synthDetailAccount){
        List list = null;
        try {
            List<String> subBranch = new ArrayList<>();
            String centerCode1 = CurrentUser.getCurrentLoginManageBranch();
            //判断是否为汇总机构
            List<String>  summaryBranch = new ArrayList();
            summaryBranch = branchInfoRepository.findByLevel("1");
            if(summaryBranch.contains(centerCode1)){
                subBranch = branchInfoRepository.findBySuperCom(centerCode1);
            }else{
                subBranch.add(centerCode1);
            }
            list = detailAccountService.queryDetailAccount(dto, synthDetailAccount,subBranch);
        } catch (Exception e) {
            logger.error("明细账查询异常",e);
        }
        return list;
    }
    /**
     * 跳转到明细账打印页面
     * @param request
     * @return
     */
    @RequestMapping("/print")
    public ModelAndView pagePrint( VoucherDTO dto,String synthDetailAccount,String centerCode,  HttpServletRequest request){
//        Map<String, Object> map = new HashMap<>();
////        map.put("yearMonth", dto);
//        map.put("yearMonth", yearMonth);//会计期间开始时间
//        map.put("yearMonthDate", yearMonthDate);//
//        map.put("voucherDateStart", voucherDateStart);//凭证日期
//        map.put("voucherDateEnd", voucherDateEnd);//
//        map.put("centerCode", centerCode);//
//        map.put("voucherGene", voucherGene);//包含未记账凭证(0:否，1:是；可为空)
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("synthesize/detailaccountprint");
        modelAndView.addObject("map",dto);
        modelAndView.addObject("synthDetailAccount",synthDetailAccount);
        modelAndView.addObject("centerCode",centerCode);
        return modelAndView;
    }

    /**
     * 明细账查询
     * @param page
     * @param rows
     * @param dto
     * @param synthDetailAccount 综合明细账（0-否 1-是）
     * @return
     */
    @RequestMapping("/listPage")
    @ResponseBody
    public DataGrid queryDetailAccountByPage(@RequestParam int page, @RequestParam int rows, VoucherDTO dto, String synthDetailAccount){
        Page<?> res = null;
        try {
            res = detailAccountService.queryDetailAccountByPage(page, rows, dto, synthDetailAccount);
        } catch (Exception e) {
            logger.error("明细账查询异常",e);
        }
        return new DataGrid(res);
    }

    @RequestMapping("/ishasdata")
    @ResponseBody
    public InvokeResult isHasData(VoucherDTO dto, String synthDetailAccount){

        String msg = detailAccountService.isHasData(dto,synthDetailAccount);
        if ("NOTEXIST".equals(msg)){
            return InvokeResult.failure("当前条件下，暂无数据可导出！");
        }else {
            return InvokeResult.success();
        }
    }

    @RequestMapping("/export")
    @ResponseBody
    public void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO dto, String synthDetailAccount,String Date1,String Date2){
        detailAccountService.exportData(request,response,dto,synthDetailAccount,Date1,Date2);
    }
}
