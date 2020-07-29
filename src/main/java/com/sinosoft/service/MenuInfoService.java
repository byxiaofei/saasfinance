package com.sinosoft.service;

import com.sinosoft.domain.UserInfo;
import com.sinosoft.domain.MenuInfo;
import com.sinosoft.dto.MenuInfoDTO;

import java.util.List;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/10 19:48
 * @Description:
 */
public interface MenuInfoService {
    /**
     * 初始化加载首页左侧个人菜单信息
     * @param u
     * @return
     */
    List<?> initMenuInfo(UserInfo u, String accountId);
    List<?> initMenuInfo(UserInfo u, String branchId, String accountId);
    /**
     * 按层级结构查询菜单信息（只查询菜单ID、菜单名称）
     * @return
     */
    List<?> qryMenuInfo();
    /**
     * 按层级结构查询菜单信息
     * @return
     */
    List<?> qryMenuInfoAll();
    /**
     * 根据菜单ID获取菜单信息
     * @param id
     * @return
     */
    MenuInfo findMenuInfo(int id);
    /**
     * 新增菜单信息
     * @param mdt
     */
    void saveMenuInfo(MenuInfoDTO mdt);
    /**
     * 修改菜单信息
     * @param c
     */
    void updateMenuInfo(MenuInfoDTO c);
    /**
     * 根据菜单ID删除菜单信息
     * @param id
     * @return 若角色菜单配置表中存在引用，则返回前两个角色代码信息，eg：“请先修改下列角色菜单信息：name1,name2” or “请先修改code1,code2等角色的菜单信息”
     */
    String deleteMenuInfo(int id);

    List<?> initMenuTree(Integer id);
}
