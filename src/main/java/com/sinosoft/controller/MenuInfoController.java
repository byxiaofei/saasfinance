package com.sinosoft.controller;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.AccountInfo;
import com.sinosoft.domain.BranchInfo;
import com.sinosoft.domain.UserInfo;
import com.sinosoft.dto.MenuInfoDTO;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.repository.AccountInfoRepository;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.service.AccountInfoService;
import com.sinosoft.service.MenuInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/10 19:44
 * @Description:
 */
@Controller
@RequestMapping("/menuinfo")
public class MenuInfoController {
    private Logger logger = LoggerFactory.getLogger(MenuInfoController.class);

    @Resource
    private MenuInfoService menuInfoService;
    @Resource
    private AccountInfoService accountInfoService;
    @Resource
    private AccountInfoRepository accountInfoRepository;
    @Resource
    private BranchInfoRepository branchInfoRepository;

    @RequestMapping("/")
    public String page(){
        return "system/menuinfo";
    }

    /**
     * 初始化加载首页左侧个人菜单信息
     * @param req
     * @return
     */
    @RequestMapping(path="/initMenu")
    @ResponseBody
    public List<?> initMenuInfo(HttpServletRequest req){
        UserInfo u = (UserInfo) req.getSession().getAttribute("currentUser");
        String accountCode = CurrentUser.getCurrentLoginAccount();
        String accountId = String.valueOf(accountInfoService.getAccountIdByAccountCode(accountCode));
        List<?> list = menuInfoService.initMenuInfo(u, accountId);
        return list;
    }

    /**
     * 初始化加载首页左侧个人菜单信息（切换账套）
     * @param req
     * @return
     */
    @RequestMapping(path="/changeInitMenu")
    @ResponseBody
    public List<?> changeInitMenuInfo(HttpServletRequest req){
        UserInfo u= (UserInfo) req.getSession().getAttribute("currentUser");
        String accountId = req.getParameter("accountId");
        List<?> list = menuInfoService.initMenuInfo(u, accountId);

        AccountInfo accountInfo = accountInfoRepository.findById(Integer.valueOf(accountId)).get();

        //当前登录账套编码
        u.setCurrentLoginAccount(accountInfo.getAccountCode());
        //当前登录账套类型编码
        u.setCurrentLoginAccountType(accountInfo.getAccountType());
        //当前登录账套名称
        u.setCurrentAccountName(accountInfo.getAccountName());

        req.getSession().setAttribute("currentUser", u);

        return list;
    }

    /**
     * 初始化加载首页左侧个人菜单信息（切换机构）
     * @param req
     * @return
     */
    @RequestMapping(path="/changeInitMenuByBranch")
    @ResponseBody
    public List<?> changeInitMenuInfoByBranch(HttpServletRequest req){
        UserInfo u= (UserInfo) req.getSession().getAttribute("currentUser");
        String branchId = req.getParameter("branchId");
        String accountId = req.getParameter("accountId");
        List<?> list = menuInfoService.initMenuInfo(u, branchId, accountId);

        AccountInfo accountInfo = accountInfoRepository.findById(Integer.valueOf(accountId)).get();
        BranchInfo branchInfo = branchInfoRepository.findById(Integer.valueOf(branchId)).get();

        //当前登录账套编码
        u.setCurrentLoginAccount(accountInfo.getAccountCode());
        //当前登录账套类型编码
        u.setCurrentLoginAccountType(accountInfo.getAccountType());
        //当前登录账套名称
        u.setCurrentAccountName(accountInfo.getAccountName());
        //当前登录用户管理机构
        u.setCurrentLoginManageBranch(branchInfo.getComCode());
        //当前登录用户管理机构名称
        u.setCurrentManageBranchName(branchInfo.getComName());
        //当前登录用户管理机构状态
        u.setCurrentManageBranchFlag(branchInfo.getFlag());

        req.getSession().setAttribute("currentUser", u);

        return list;
    }

    /**
     * 初始化角色菜单
     * @param id 角色ID
     * @return
     */
    @RequestMapping(path="/getMenuTree")
    @ResponseBody
    public List<?> initMenuTree(Integer id){
        List<?> list = menuInfoService.initMenuTree(id);
        return list;
    }

    /**
     * 按层级结构查询菜单信息（只查询菜单ID、菜单名称）
     * @return
     */
    @RequestMapping(path="/list")
    @ResponseBody
    public List<?> qryMenuInfo(){
        return menuInfoService.qryMenuInfo();
    }

    /**
     * 按层级结构查询菜单信息
     * @return
     */
    @RequestMapping(path="/listall")
    @ResponseBody
    public List<?> qryMenuInfoAll(){
        return menuInfoService.qryMenuInfoAll();
    }

    /**
     * 根据菜单ID获取菜单信息
     * @param id
     * @return
     */
    @RequestMapping(path="/find")
    @ResponseBody
    public InvokeResult find(int id){
        try {
            return InvokeResult.success(menuInfoService.findMenuInfo(id));
        } catch (Exception e) {
            e.printStackTrace();
            return InvokeResult.failure(e.getMessage());
        }
    }

    /**
     * 新增菜单信息
     * @param m
     * @return
     */
    @SysLog(value = "新增菜单信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/add", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult saveMenuInfo(MenuInfoDTO m){
        try {
            menuInfoService.saveMenuInfo(m);
            return InvokeResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            if("exist".equals(e.getMessage())){
                return InvokeResult.failure("菜单ID重复，请重新输入");
            }
            logger.error("新增菜单异常", e);
            return InvokeResult.failure("操作失败");
        }
    }

    /**
     * 修改菜单信息
     * @param c
     * @return
     */
    @SysLog(value = "编辑菜单信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/update")
    @ResponseBody
    public InvokeResult updateMenuInfo(MenuInfoDTO c){
        try {
            menuInfoService.updateMenuInfo(c);
            return InvokeResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("编辑菜单异常", e);
            return InvokeResult.failure("操作失败");
        }
    }

    /**
     * 根据菜单ID删除菜单信息
     * @param id
     * @return 若角色菜单配置表中存在引用，则返回前两个角色代码信息，eg：“请先修改下列角色菜单信息：name1,name2” or “请先修改code1,code2等角色的菜单信息”
     */
    @SysLog(value = "删除菜单信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/delete")
    @ResponseBody
    public InvokeResult deleteMenuInfo(@RequestParam int id){
        try {
            String str =  menuInfoService.deleteMenuInfo(id);
            if(!str.isEmpty()){
                return InvokeResult.failure(str);
            }
            return InvokeResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除菜单异常", e);
            return InvokeResult.failure("操作失败");
        }
    }
}
