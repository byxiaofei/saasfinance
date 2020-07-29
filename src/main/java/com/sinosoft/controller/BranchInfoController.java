package com.sinosoft.controller;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.BranchInfo;
import com.sinosoft.dto.BranchInfoDTO;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.service.BranchInfoService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/11 19:41
 * @Description:
 */

@Controller
@RequestMapping("/branchinfo")
public class BranchInfoController {
    private Logger logger = LoggerFactory.getLogger(BranchInfoController.class);

    @Resource
    private BranchInfoService branchInfoService;

    @Resource
    private BranchInfoRepository branchInfoRepository;

    @RequestMapping("/")
    public String page(){
        return "system/branchinfo";
    }

    @GetMapping("/fillCombobox")
    @ResponseBody
    public List<?> fillCombobox(@Nullable String comId){

        if(StringUtils.isNotEmpty(comId)){
            return branchInfoRepository.findAccountByBranchId(Integer.valueOf(comId));
        }else{
            return branchInfoRepository.findByLevel();
        }
    }

    @RequestMapping(path="/list")
    @ResponseBody
    public DataGrid qryBranchInfo(@RequestParam int page, @RequestParam int rows, BranchInfo branchInfo){
        Page<BranchInfoDTO> res = branchInfoService.qryBranchInfo(page, rows, branchInfo);
        return new DataGrid(res);
    }

    /**
     * 新增机构信息
     * @param branchInfoDTO
     * @return
     */
    @SysLog(value = "新增机构信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/add", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult saveBranchInfo(BranchInfoDTO branchInfoDTO){
        try {
            branchInfoService.saveBranchInfo(branchInfoDTO);
            return InvokeResult.success();
        } catch (Exception e) {
            if ("exist".equals(e.getMessage())) {
                return InvokeResult.failure("机构编码已存在");
            } else {
                logger.error("新增机构异常", e);
                return InvokeResult.failure("操作失败");
            }
        }
    }

    /**
     * 修改机构信息
     * @param branchInfoDTO
     * @return
     */
    @SysLog(value = "编辑机构信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/update")
    @ResponseBody
    public InvokeResult updateBranchInfo(int id, BranchInfoDTO branchInfoDTO){
        try {
            branchInfoService.updateBranchInfo(id, branchInfoDTO);
            return InvokeResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("编辑机构异常", e);
            return InvokeResult.failure("操作失败");
        }
    }

    /**
     * 根据机构ID删除机构信息
     * @param id
     * @return
     */
    @SysLog(value = "删除机构信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/delete")
    @ResponseBody
    public InvokeResult deleteBranchInfo(@RequestParam int id){
        try {
            String str =  branchInfoService.deleteBranchInfo(id);
            if(!str.isEmpty()&&"exist".equals(str)){
                return InvokeResult.failure("存在下级机构，不允许删除");
            }else if (!str.isEmpty()&&"existUse".equals(str)) {
                return InvokeResult.failure("已经使用过的机构不允许删除");
            }
            return InvokeResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除机构异常", e);
            return InvokeResult.failure("操作失败");
        }
    }

    /**
     * 初始化账套关联机构
     * @param accountId 账套ID
     * @return
     */
    @RequestMapping(path="/getBranchTree")
    @ResponseBody
    public List<?> initBranchTree(Integer accountId){
        /*//递归查询父子级
        List<?> list = branchInfoService.initBranchTreeRecursion(accountId);*/
        //查询全部，非递归查询
        List<?> list = branchInfoService.initBranchTree(accountId);
        return list;
    }

    @RequestMapping(path="/getAccountByUserIdAndBranchId")
    @ResponseBody
    public List<?> getAccountByUserIdAndBranchId(Long userId, Integer branchId){
        return branchInfoService.getAccountByUserIdAndBranchId(userId, branchId);
    }

    @RequestMapping(path="/getBranchAccountByUserId")
    @ResponseBody
    public List<?> getBranchAccountByUserId(Long userId){
        return branchInfoService.getBranchAccountByUserId(userId);
    }

    @RequestMapping(path="/getManageBranchByUserId")
    @ResponseBody
    public List<BranchInfo> getManageBranchByUserId(Long userId){
        return branchInfoService.getManageBranchByUserId(userId);
    }
}
