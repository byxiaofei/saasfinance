package com.sinosoft.service;

import com.sinosoft.domain.RoleInfo;
import org.springframework.data.domain.Page;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/18 17:53
 * @Description:
 */
public interface RoleInfoService {

    public static final String ROLE_ISEXISTE = "ROLE_ISEXIST";

    /**
     * 分页查询
     * @param page 起始页
     * @param rows 记录数
     * @param roleInfo
     * @return
     */
    Page<RoleInfo> qryRoleInfo(int page, int rows, RoleInfo roleInfo);

    /**
     * 创建角色
     * @param roleInfo
     * @return
     */
    String saveRoleInfo(RoleInfo roleInfo);

    /**
     * 删除角色
     * @param id 角色ID
     */
    void delRole(Long id);

    /**
     * 更新角色
     * @param roleInfo
     */
    void updateRoleInfo(RoleInfo roleInfo);

    /**
     * 角色菜单关联
     * @param menuId  菜单ID，1,2,3...
     * @param roleId  角色ID
     */
    void roleToMenu(String menuId, Long roleId);

    List<Map<String, Object>> getRoleByUserIdAndUserBAId(Long userId, BigInteger userBAId);
}
