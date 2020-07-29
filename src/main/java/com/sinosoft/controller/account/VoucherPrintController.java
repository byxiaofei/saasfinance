package com.sinosoft.controller.account;

import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.account.VoucherPrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/voucherprint")
public class VoucherPrintController {
    private Logger logger = LoggerFactory.getLogger(VoucherPrintController.class);
    @Resource
    private VoucherPrintService voucherPrintService;

    @RequestMapping("/")
    public String Page(){
        return "account/voucherprint";
    }

    /**
     * 跳转到凭证打印页面
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping("/print")
    public ModelAndView page(VoucherDTO dto, HttpServletRequest request){
        /*if (dto.getCopyType()!=null && "4".equals(dto.getCopyType())) {
            request.getSession().setAttribute("printYearMonth", dto.getYearMonth());
            request.getSession().setAttribute("printYearMonthDate", dto.getYearMonthDate());
            request.getSession().setAttribute("printVoucherNoStart", dto.getVoucherNoStart());
            request.getSession().setAttribute("printVoucherNoEnd", dto.getVoucherNoEnd());
            request.getSession().setAttribute("printCreateBy", dto.getCreateBy());
            request.getSession().setAttribute("printVoucherNo", dto.getVoucherNo());
        } else {
            request.getSession().setAttribute("printYearMonth", dto.getYearMonth());
            request.getSession().setAttribute("printVoucherNo", dto.getVoucherNo());
        }
        request.getSession().setAttribute("printSpecialNameP", dto.getSpecialNameP());
        request.getSession().setAttribute("printCopyType", dto.getCopyType());*/

        Map<String, Object> map = new HashMap<>();
        map.put("printYearMonth", dto.getYearMonth());//会计期间1(copyType为4时),会计期间
        map.put("printYearMonthDate", dto.getYearMonthDate());//会计期间2
        map.put("printVoucherNoStart", dto.getVoucherNoStart());//开始凭证号
        map.put("printVoucherNoEnd", dto.getVoucherNoEnd());//结束凭证号
        map.put("printCreateBy", dto.getCreateBy());//制单人
        map.put("printVoucherNo", dto.getVoucherNo());//挑选凭证号(copyType为4时),凭证号

        map.put("printSpecialNameP", dto.getSpecialNameP());//专项全称全级显示
        map.put("printCopyType", dto.getCopyType());//为4时是凭证批量打印（凭证打印界面）

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("account/voucherprintdetail");
        modelAndView.addObject("map",map);
        return modelAndView;
    }

    @RequestMapping("/printlist")
    @ResponseBody
    public List<?> queryDetaiList(VoucherDTO voucherDTO){
        List<?> list = null;
        try {
            list = voucherPrintService.queryVoucherPrintList(voucherDTO);
        } catch (Exception e) {
            logger.error("凭证打印查询异常",e);
        }
        return list;
    }

    @RequestMapping("export")
    @ResponseBody
    public void voucherExport(HttpServletRequest request, HttpServletResponse response,VoucherDTO voucherDTO){
        voucherPrintService.voucherExport(request,response,voucherDTO);
    }
}
