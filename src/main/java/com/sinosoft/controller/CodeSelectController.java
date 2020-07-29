package com.sinosoft.controller;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.repository.CodeSelectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/codeSelect")
public class CodeSelectController {

    private Logger logger = LoggerFactory.getLogger(CodeSelectController.class);
    @Resource
    private CodeSelectRepository codeSelectRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<?> codeSelect(@RequestParam String type) {
        List<?> result = new ArrayList<Object>();
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        String sql = "";
        if (type == null) {
            logger.error("参数丢失！");
        } else {
            try {
                if ("".equals(type)) {

                } else if("yearMonth".equals(type)){
                    result = codeSelectRepository.findYearMonth(accBookCode, centerCode);
                } else if("yearMonthAll".equals(type)){
                    result = codeSelectRepository.findYearMonthAll(accBookCode, centerCode);
                } else if("yearMonthAllAndNotJS".equals(type)){
                    result = codeSelectRepository.findYearMonthAllAndNotJS(accBookCode, centerCode);
                } else if("yearsByAccMonthTrace".equals(type)){
                    result = codeSelectRepository.findYearsByAccMonthTrace(accBookCode, centerCode);
                } else if("yearMonthByCurrentYear".equals(type)){
                    result = codeSelectRepository.findYearMonthByCurrentYear(accBookCode, CurrentTime.getCurrentYear(), centerCode);
                } else if("JZYearMonth".equals(type)){
                    result = codeSelectRepository.findJZYearMonth(accBookCode, centerCode);
                } else if("version".equals(type)){
                    result = codeSelectRepository.findVersion();
                }else if("SDBBreportType".equals(type)){
                    result = codeSelectRepository.findSDBBreportTypeByAccount(accBookCode);
                } else if("selectYear".equals(type)){
                    result = codeSelectRepository.findChangeCodeByAccount(accBookCode, centerCode);
                }else if("selectIntangibleYear".equals(type)){
                    result = codeSelectRepository.findChangeIntangibleCodeByAccount(accBookCode, centerCode);
                } else {
                    System.out.println(type);
                    result = codeSelectRepository.findByComTypeAsc(type);
                }
            } catch (Exception e){
                logger.error("查询字典表数据异常,type="+type, e);
                e.printStackTrace();
            }
        }
        return result;
    }
    @RequestMapping(value = "/hasParam", method = RequestMethod.GET)
    @ResponseBody
    public List<?> codeSelect(@RequestParam String type, String param) {
        List<?> result = new ArrayList<Object>();
        String sql = "";
        if (type == null) {
            logger.error("参数丢失！");
        } else {
            try {
                if ("".equals(type)) {

                } else if("reportName".equals(type)){
                    if(param.equals("1")){
                        result = codeSelectRepository.findByComTypeAsc("JJreportType1");
                    }else if(param.equals("2")){
                        result = codeSelectRepository.findByComTypeAsc("JJreportType2");
                    }
                } else if("zdyReportName".equals(type)){
                    result = codeSelectRepository.findzdyReport(param);
                }
            } catch (Exception e){
                logger.error("查询字典表数据异常,type="+type, e);
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 下拉框查询（分组查询）
     * @param type
     * @return
     */
    @RequestMapping(path="/group", method = RequestMethod.GET)
    @ResponseBody
    public List<?> codeSelectGroup(@RequestParam String type) {
        List<Object> resultAll=new ArrayList<Object>();
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        if (type == null) {
            logger.error("参数丢失！");
        } else {
            try {
                if ("".equals(type)) {

                } else if("yearMonthGroup".equals(type)){
                    List<?> result = result = codeSelectRepository.findYearMonth1(accBookCode, centerCode);
                    if (result!=null&&result.size()>0) {
                        resultAll.addAll(result);
                    }
                    List<?> result1 = codeSelectRepository.findYearMonth2(accBookCode, centerCode);
                    if (result1!=null&&result1.size()>0) {
                        resultAll.addAll(result1);
                    }
                }
            } catch (Exception e){
                logger.error("查询字典表数据异常,type="+type, e);
                e.printStackTrace();
            }
        }
        return resultAll;
    }

    /**
     * 下拉框查询，将不限选项追加在第一项位置
     * @param type
     * @return
     */
    @RequestMapping(path="/unlimited", method = RequestMethod.GET)
    @ResponseBody
    public List<?> codeSelectUnlimited(@RequestParam String type) {
        List<Object> resultAll=new ArrayList<Object>();
        resultAll.addAll(codeSelect(type));
        Map map=new HashMap();
        map.put("text", "-不限-");
        map.put("value", null);
        resultAll.add(0,map);
        return resultAll;
    }

    /**
     * 初始化上级机构下拉框
     * @return
     */
    @RequestMapping(path="/initSuperCom", method = RequestMethod.GET)
    @ResponseBody
    public List<?> initSuperCom(){
        List<?> result = new ArrayList<Object>();
        try {
            result = codeSelectRepository.initSuperCom();
        } catch (Exception e){
            logger.error("初始化上级机构下拉框异常", e);
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 加载账套下拉框信息（账套信息id、accountName）
     * @return
     */
    @RequestMapping(path="/referToAccount", method = RequestMethod.GET)
    @ResponseBody
    public List<?> referToAccount(){
        List<?> result = new ArrayList<Object>();
        try {
            result = codeSelectRepository.referToAccount();
        } catch (Exception e){
            logger.error("初始化账套下拉框异常", e);
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 根据机构Id加载账套下拉框信息（账套信息id、accountName）
     * @return
     */
    @RequestMapping(path="/qryAccountByComComId", method = RequestMethod.GET)
    @ResponseBody
    public List<?> qryAccountByComComId(Integer id){
        List<?> result = new ArrayList<Object>();
        try {
            result = codeSelectRepository.findAccountByComComId(id);
        } catch (Exception e){
            logger.error("根据机构Id查询加载账套下拉信息异常", e);
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 登录页机构下拉框
     * @return
     */
    @RequestMapping(path="/loginPageFindComCom", method = RequestMethod.GET)
    @ResponseBody
    public List<?> loginPageFindComCom(){
        return initSuperCom();
    }
    /**
     * 登录页根据机构Id加载账套下拉框信息（账套信息id、comName）
     * @return
     */
    @RequestMapping(path="/loginPageFindAccountByComComId", method = RequestMethod.GET)
    @ResponseBody
    public List<?> loginPageFindAccountByComComId(Integer id){
        return qryAccountByComComId(id);
    }
}
