package com.sinosoft.controller.account;

import com.sinosoft.common.*;
import com.sinosoft.dto.CodeManageDTO;
import com.sinosoft.service.account.BasicInfoManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/basicInfoManage")
public class BasicInfoManageController {
    private Logger logger = LoggerFactory.getLogger(BasicInfoManageController.class);

    @Resource
    private BasicInfoManageService basicInfoManageService;

    @RequestMapping("/")
    public String page(){
        return "account/basicInfoManage";
    }

    @RequestMapping(path="/list")
    @ResponseBody
    public List<?> qryBasicInfo(String codeType){
        return basicInfoManageService.qryBasicInfo(codeType);
    }

    /**
     * 新增字典表信息
     * @param dto
     * @return
     */
    @SysLog(value = "新增字典表信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/add", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult addBasicInfo(CodeManageDTO dto){
        try {
            String result = basicInfoManageService.addBasicInfo(dto);

            if (result!=null && "exist".equals(result)) {
                return InvokeResult.failure("当前类型中已存在类别为：" + dto.getCodeCode() + " 的数据！");
            } else if (result!=null && "hasOccupy".equals(result)) {
                return InvokeResult.failure("排序序号已被占用！");
            }

            return InvokeResult.success();
        } catch (Exception e) {
            logger.error("新增字典表数据异常", e);
            return InvokeResult.failure("操作失败！");
        }
    }

    /**
     * 编辑字典表信息
     * @param dto
     * @return
     */
    @SysLog(value = "编辑字典表信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/update")
    @ResponseBody
    public InvokeResult updateBasicInfo(CodeManageDTO dto){
        try {
            String result = basicInfoManageService.updateBasicInfo(dto);

            if (result!=null && "unExist".equals(result)) {
                return InvokeResult.failure("未查询到对应的数据！");
            } else if (result!=null && "hasOccupy".equals(result)) {
                return InvokeResult.failure("排序序号已被占用！");
            }

            return InvokeResult.success();
        } catch (Exception e) {
            logger.error("编辑字典表信息异常", e);
            return InvokeResult.failure("操作失败！");
        }
    }

    /**
     * 删除字典表信息
     * @param dto
     * @return
     */
    @SysLog(value = "删除字典表信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/delete")
    @ResponseBody
    public InvokeResult deleteBasicInfo(CodeManageDTO dto){
        try {
            String result = basicInfoManageService.deleteBasicInfo(dto);

            if (result!=null && "unExist".equals(result)) {
                return InvokeResult.failure("未查询到对应的数据！");
            } else if (result!=null && "use".equals(result)) {
                return InvokeResult.failure("已被使用，不允许删除！");
            }

            return InvokeResult.success();
        } catch (Exception e) {
            logger.error("删除字典表信息异常", e);
            return InvokeResult.failure("操作失败！");
        }
    }

    /**
     * 删除字典表信息
     * @param dto
     * @return
     */
    @SysLog(value = "上移或下移字典表信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/upOrDown")
    @ResponseBody
    public InvokeResult upOrDownBasicInfo(String type, CodeManageDTO dto){
        try {
            String result = basicInfoManageService.upOrDownBasicInfo(type, dto);

            if (result!=null && "unExist".equals(result)) {
                return InvokeResult.failure("未查询到对应的数据！");
            } else if (result!=null && "noLast".equals(result)) {
                return InvokeResult.failure("没找到符合条件的上一条数据，无法进行上移操作！");
            } else if (result!=null && "noNext".equals(result)) {
                return InvokeResult.failure("没找到符合条件的下一条数据，无法进行下移操作！");
            } else if (result!=null && "paramError".equals(result)) {
                return InvokeResult.failure("参数错误！");
            }

            return InvokeResult.success();
        } catch (Exception e) {
            logger.error("上移或下移字典表信息异常", e);
            return InvokeResult.failure("操作失败！");
        }
    }
}
