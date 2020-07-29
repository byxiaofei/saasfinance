package com.sinosoft.controller;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.AccountInfo;
import com.sinosoft.dto.AccountInfoDTO;
import com.sinosoft.repository.AccountInfoRepository;
import com.sinosoft.service.AccountInfoService;
import org.apache.shiro.authc.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/accountinfo")
public class AccountInfoController {
    private Logger logger = LoggerFactory.getLogger(AccountInfoController.class);

    @Resource
    private AccountInfoService accountInfoService;

    //   为了看数据List<Map<String,Objecdt>> 格式，只能用于测试，不能直接暴露
//    @Resource
//    private AccountInfoRepository accountInfoRepository;

    @RequestMapping("/")
    public String page(){
        return "system/accountinfo";
    }

    @RequestMapping(path="/list")
    @ResponseBody
    public DataGrid qryAccountInfo(@RequestParam int page, @RequestParam int rows, AccountInfo accountInfo){
        Page<AccountInfoDTO> res = accountInfoService.qryAccountInfo(page, rows, accountInfo);
        return new DataGrid(res);
    }

    /**
     * 新增账套信息
     * @param accountInfoDTO
     * @return
     */
    @SysLog(value = "新增账套信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/add", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult saveAccountInfo(AccountInfoDTO accountInfoDTO){
        try {
            accountInfoService.saveAccountInfo(accountInfoDTO);
            return InvokeResult.success();
        } catch (Exception e) {
            if ("exist".equals(e.getMessage())) {
                return InvokeResult.failure("账套编码已存在");
            } else if ("existName".equals(e.getMessage())) {
                return InvokeResult.failure("账套名称已存在");
            } else {
                logger.error("新增账套异常", e);
                return InvokeResult.failure("操作失败");
            }
        }
    }

//    @SysLog(value = "测试返回的格式状态")   // 这里添加AOP的自定义注解
//    @RequestMapping(path = "/test",method = RequestMethod.POST)
//    @ResponseBody
//    public InvokeResult testListMapMessage(@RequestBody  AccountInfoDTO accountInfoDTO){
//
//        List<Map<String, Object>> maps = accountInfoRepository.checkExistsAccountCode(accountInfoDTO.getAccountCode());
//        System.out.println(maps);
//        return InvokeResult.success("success");
//    }

    /**
     * 修改账套信息
     * @param accountInfoDTO
     * @return
     */
    @SysLog(value = "编辑账套信息")
    @RequestMapping(path="/update")
    @ResponseBody
    public InvokeResult updateAccountInfo(int id, AccountInfoDTO accountInfoDTO){
        try {
            accountInfoService.updateAccountInfo(id, accountInfoDTO);
            return InvokeResult.success();
        } catch (Exception e) {
            if ("exist".equals(e.getMessage())) {
                return InvokeResult.failure("账套编码已存在");
            } else if ("existName".equals(e.getMessage())) {
                return InvokeResult.failure("账套名称已存在");
            } else {
                logger.error("编辑账套异常", e);
                return InvokeResult.failure("操作失败");
            }
        }
    }

    /**
     * 根据账套ID删除账套信息
     * @param id
     * @return
     */
    @SysLog(value = "删除账套信息")
    @RequestMapping(path="/delete")
    @ResponseBody
    public InvokeResult deleteAccountInfo(@RequestParam int id){
        try {
            String str =  accountInfoService.deleteAccountInfo(id);
            if(!str.isEmpty()&&"exist".equals(str)){
                return InvokeResult.failure("已经使用过的账套，不允许删除");
            }
            return InvokeResult.success();
        } catch (Exception e) {
            logger.error("删除账套异常", e);
            return InvokeResult.failure("操作失败");
        }
    }

    @SysLog(value = "账套关联机构")
    @PostMapping("/branchAccount")
    @ResponseBody
    public InvokeResult saveBranchAccount(String branchId, Integer accountId){
        try{
            accountInfoService.saveBranchAccount(branchId, accountId);
            return InvokeResult.success();
        }catch (Exception e){
            logger.error("账套关联机构异常", e);
            return InvokeResult.failure("账套关联机构失败！请联系系统管理员。");
        }
    }

    @RequestMapping("/getAccountByUserIdAndBranchId")
    @ResponseBody
    public List<?> getAccountByUserIdAndBranchId(Long userId, Integer branchId){
        return accountInfoService.getAccountByUserIdAndBranchId(userId, branchId);
    }
}
