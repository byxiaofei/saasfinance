package com.sinosoft.controller.account;

import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.account.QueryDetailAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/querydetailaccount")
public class QueryDetailAccountController {
    private Logger logger = LoggerFactory.getLogger(QueryDetailAccountController.class);
    @Resource
    private QueryDetailAccountService queryDetailAccountService;
    @Resource
    private VoucherService voucherService;

    /**
     * 跳转到联查明细账页面
     * @param directionIdx 科目方向段
     * @param itemName 科目名称
     * @param yearMonth 当前会计期间(开始时间)
     * @param endDate 制单日期(截止日期)
     * @param specialNameP 专项是否全称显示(0:否，1:是；可为空)
     * @param request
     * @return
     */
    @RequestMapping("/detai")
    public ModelAndView page(@RequestParam String directionIdx, @RequestParam String itemName, @RequestParam String yearMonth, @RequestParam String endDate, String specialNameP, HttpServletRequest request){
        String[] itemNames = itemName.split("/");
        /*request.getSession().setAttribute("detaiDirectionIdx", directionIdx);
        request.getSession().setAttribute("detaiItemName", itemNames[itemNames.length-1]);
        request.getSession().setAttribute("detaiYearMonth", yearMonth);
        request.getSession().setAttribute("detaiEndDate", endDate);
        request.getSession().setAttribute("detaiSpecialNameP", specialNameP);*/

        Map<String, Object> map = new HashMap<>();
        map.put("detaiDirectionIdx", directionIdx);//科目方向段
        map.put("detaiItemName", itemNames[itemNames.length-1]);//科目名称
        map.put("detaiYearMonth", yearMonth);//当前会计期间(开始时间)
        map.put("detaiEndDate", endDate);//制单日期(截止日期)
        map.put("detaiSpecialNameP", specialNameP);//专项是否全称显示(0:否，1:是；可为空)
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("account/jointdetailaccount");
        modelAndView.addObject("map",map);
        return modelAndView;
    }

    /**
     * 跳转到联查明细账打印页面
     * @param request
     * @return
     */
    @RequestMapping("/detaiprint")
    public ModelAndView pagePrint(@RequestParam String directionIdx, @RequestParam String itemName, @RequestParam String yearMonth, @RequestParam String endDate, String specialNameP, HttpServletRequest request){
        /*request.getSession().setAttribute("detaiPrintYearMonth", yearMonth);
        request.getSession().setAttribute("detaiPrintEndDate", endDate);*/

        Map<String, Object> map = new HashMap<>();
        map.put("detaiDirectionIdx", directionIdx);//科目方向段
        map.put("detaiItemName", itemName);//科目名称
        map.put("detaiYearMonth", yearMonth);//开始时间
        map.put("detaiEndDate", endDate);//截止日期
        map.put("detaiSpecialNameP", specialNameP);//专项是否全称显示(0:否，1:是；可为空)
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("account/jointdetailaccountprint");
        modelAndView.addObject("map",map);
        return modelAndView;
    }
    /**
     * 联查明细账导出页面
     * @param request
     * @return
     */
    @RequestMapping("/export")
    @ResponseBody
    public void exportData(HttpServletRequest request, HttpServletResponse response,String yearMonth, String beginDate, String endDate, String directionIdx, String specialNameP,String detaiItemName,String dateText){
        queryDetailAccountService.exportData(request,response,yearMonth, beginDate, endDate, directionIdx, specialNameP, detaiItemName, dateText);
    }
    /**
     * 联查辅助明细账导出页面
     * @param request
     * @return
     */
    @RequestMapping("/fzexport")
    @ResponseBody
    public void exportDatafz(HttpServletRequest request, HttpServletResponse response,String yearMonth, String beginDate, String endDate, String directionIdx,
                             String specialNameP,String specialSuperCodeS,String directionOther,String itemName,String dateText,String otherName,String directionOtherName,String directionOthers){
        queryDetailAccountService.exportDatafz(request,response,yearMonth, beginDate, endDate, directionIdx, specialNameP, specialSuperCodeS, directionOther, itemName, dateText, otherName, directionOtherName, directionOthers);
    }
    /**
     * 跳转到联查辅助明细账页面
     * @param directionIdx 科目方向段
     * @param itemName 科目名称
     * @param specialCode 专项代码
     * @param specialSuperCodeS 一级专项代码
     * @param specialName 专项名称
     * @param yearMonth 当前会计期间(开始时间)
     * @param endDate 制单日期(截止日期)
     * @param specialNameP 专项是否全称显示(0:否，1:是；可为空)
     * @param request
     * @return
     */
    @RequestMapping("/assist")
    public ModelAndView pageAssist(@RequestParam String directionIdx, @RequestParam String itemName, @RequestParam String specialCode, @RequestParam String specialSuperCodeS, @RequestParam String specialName, @RequestParam String yearMonth, @RequestParam String endDate, String specialNameP, HttpServletRequest request){
        String[] itemNames = itemName.split("/");
        //一级专项名称
        String specialSuperName = "";
        if (specialSuperCodeS !=null&&!"".equals(specialSuperCodeS)) {
            String[] code = specialSuperCodeS.split(",");
            for (String s : code) {
                VoucherDTO dto = voucherService.getSpecialDateBySpecialCode(s);
                if (dto!=null&&dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName())) {
                    specialSuperName += dto.getSpecialName() + ",";
                }
            }
            specialSuperName = specialSuperName.substring(0,specialSuperName.length()-1);
        }
        /*request.getSession().setAttribute("assistDirectionIdx", directionIdx);
        request.getSession().setAttribute("assistItemName", itemNames[itemNames.length-1]);
        request.getSession().setAttribute("assistSpecialCode", specialCode);
        request.getSession().setAttribute("assistSpecialSuperCodeS", specialSuperCodeS);
        request.getSession().setAttribute("assistSpecialSuperName", specialSuperName);
        request.getSession().setAttribute("assistSpecialName", specialName);
        request.getSession().setAttribute("assistYearMonth", yearMonth);
        request.getSession().setAttribute("assistEndDate", endDate);
        request.getSession().setAttribute("assistSpecialNameP", specialNameP);*/

        Map<String, Object> map = new HashMap<>();
        map.put("assistDirectionIdx", directionIdx);//科目方向段
        map.put("assistItemName", itemNames[itemNames.length-1]);//科目名称
        map.put("assistSpecialCode", specialCode);//专项代码
        map.put("assistSpecialSuperCodeS", specialSuperCodeS);//一级专项代码
        map.put("assistSpecialSuperName", specialSuperName);//一级专项名称
        map.put("assistSpecialName", specialName);//专项名称
        map.put("assistYearMonth", yearMonth);//当前会计期间(开始时间)
        map.put("assistEndDate", endDate);//制单日期(截止日期)
        map.put("assistSpecialNameP", specialNameP);//专项是否全称显示(0:否，1:是；可为空)
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("account/jointassistdetailaccount");
        modelAndView.addObject("map",map);
        return modelAndView;
    }

    /**
     * 跳转到联查辅助明细账打印页面
     * @param request
     * @return
     */
    @RequestMapping("/assistprint")
    public ModelAndView pageAssistPrint(@RequestParam String directionIdx, @RequestParam String itemName, @RequestParam String specialCode, @RequestParam String specialSuperCodeS, @RequestParam String specialSuperName, @RequestParam String specialName, @RequestParam String yearMonth, @RequestParam String endDate, String specialNameP, HttpServletRequest request){
        /*request.getSession().setAttribute("assistPrintYearMonth", yearMonth);
        request.getSession().setAttribute("assistPrintEndDate", endDate);*/

        Map<String, Object> map = new HashMap<>();
        map.put("assistDirectionIdx", directionIdx);//科目方向段
        map.put("assistItemName", itemName);//科目名称
        map.put("assistSpecialCode", specialCode);//专项代码
        map.put("assistSpecialSuperCodeS", specialSuperCodeS);//一级专项代码
        map.put("assistSpecialSuperName", specialSuperName);//一级专项名称
        map.put("assistSpecialName", specialName);//专项名称
        map.put("assistYearMonth", yearMonth);//开始时间
        map.put("assistEndDate", endDate);//截止日期
        map.put("assistSpecialNameP", specialNameP);//专项是否全称显示(0:否，1:是；可为空)
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("account/jointassistdetailaccountprint");
        modelAndView.addObject("map",map);
        return modelAndView;
    }

    /**
     * 联查明细账
     * @param yearMonth 当前会计期间
     * @param beginDate 开始时间
     * @param endDate 截止日期
     * @param directionIdx 科目方向段
     * @param specialNameP 专项是否全称显示(0:否，1:是；可为空)
     * @return
     */
    @RequestMapping("/detailist")
    @ResponseBody
    public List<?> queryDetaiList(String yearMonth, String beginDate, String endDate, String directionIdx, String specialNameP){
        System.out.println("yearMonth:"+yearMonth+"-"+"beginDate:"+beginDate+"-"+"endDate:"+endDate+"-"+"directionIdx:"+directionIdx+"-"+"specialNameP:"+specialNameP+"-");
        List<?> list = null;
        try {
            list = queryDetailAccountService.queryDetaiList(yearMonth, beginDate, endDate, directionIdx, specialNameP);
        } catch (Exception e) {
            logger.error("联查明细账异常",e);
        }
        return list;
    }

    /**
     * 联查辅助明细账
     * @param yearMonth 当前会计期间
     * @param beginDate 开始时间
     * @param endDate 截止日期
     * @param directionIdx 科目方向段
     * @param directionOther 专项方向段
     * @param specialSuperCodeS 一级专项代码
     * @param specialNameP 专项是否全称显示(0:否，1:是；可为空)
     * @return
     */
    @RequestMapping("/assistlist")
    @ResponseBody
    public List<?> queryAssistList(String yearMonth, String beginDate, String endDate, String directionIdx, String directionOther, String specialSuperCodeS, String specialNameP){
        System.out.println("yearMonth:"+yearMonth+"_"+"beginDate:"+beginDate+"_"+"endDate:"+endDate+"_"+"directionIdx:"+directionIdx+"_"+"directionOther:"+directionOther+"_"+"specialSuperCodeS:"+ specialSuperCodeS +"_"+"specialNameP:"+ specialNameP +"_");
        List<?> list = null;
        try {
            list = queryDetailAccountService.queryAssistList(yearMonth, beginDate, endDate, directionIdx, directionOther, specialSuperCodeS, specialNameP);
        } catch (Exception e) {
            logger.error("联查辅助明细账异常",e);
        }
        return list;
    }
    @RequestMapping("/begindatelist")
    @ResponseBody
    public List<?> getBeginDateList(){
        return queryDetailAccountService.getBeginDateList();
    }
}
