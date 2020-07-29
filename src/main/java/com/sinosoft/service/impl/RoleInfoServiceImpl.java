package com.sinosoft.service.impl;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.domain.RoleInfo;
import com.sinosoft.domain.RoleMenu;
import com.sinosoft.repository.MenuInfoRepository;
import com.sinosoft.repository.RoleInfoRepository;
import com.sinosoft.repository.RoleMenuRepository;
import com.sinosoft.service.RoleInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/18 17:55
 * @Description:
 */
@Service
public class RoleInfoServiceImpl implements RoleInfoService {

    @Resource
    private RoleInfoRepository roleInfoRepository;
    @Resource
    private RoleMenuRepository roleMenuRepository;
    @Resource
    private MenuInfoRepository menuInfoRepository;

    @Override
    public Page<RoleInfo> qryRoleInfo(int page, int rows, RoleInfo roleInfo) {
        return roleInfoRepository.findAll(new CusSpecification<RoleInfo>().and(
                CusSpecification.Cnd.like("roleName", roleInfo.getRoleName()),
                CusSpecification.Cnd.like("roleCode", roleInfo.getRoleCode())
        ).asc("id"),new PageRequest((page - 1), rows));
    }

    @Transactional
    public String saveRoleInfo(RoleInfo roleInfo) {
        if(roleInfoRepository.findByRoleCode(roleInfo.getRoleCode()).size() > 0){
            return ROLE_ISEXISTE;
        }else{

            roleInfo.setCreateBy(CurrentUser.getCurrentUser().getId() + "");
            roleInfo.setCreateTime(CurrentTime.getCurrentTime());

            roleInfoRepository.save(roleInfo);
            return "success";
        }
    }

    @Transactional
    public void delRole(Long id) {
        roleInfoRepository.deleteById(id);
    }

    @Transactional
    public void updateRoleInfo(RoleInfo roleInfo) {
        RoleInfo role = roleInfoRepository.findById(roleInfo.getId()).get();

        role.setRoleCode(roleInfo.getRoleCode());
        role.setRoleName(roleInfo.getRoleName());
        role.setRemark(roleInfo.getRemark());
        role.setLastModifyBy(CurrentUser.getCurrentUser().getId() + "");
        role.setLastModifyTime(CurrentTime.getCurrentTime());

        roleInfoRepository.save(role);
    }

    @Transactional
    public void roleToMenu(String menuId, Long roleId) {
        List<?> rolemenus = roleMenuRepository.findByRoleId(roleId);

        for (Object obj: rolemenus) {
            RoleMenu roleMenu = (RoleMenu) obj;
            roleMenuRepository.delete(roleMenu);
        }

        String[] menu_ids = menuId.split(",");
        //去掉重复项
        List<String> list = new ArrayList<String>();
        for(String a:menu_ids){
            if(!a.equals("") && !list.contains(a)){
                list.add(a);
            }
        }

        if(list.size()>0){
            //添加角色菜单信息
            for(String mid : list){
                RoleMenu rolemenu= new RoleMenu();
                rolemenu.setRoleInfo(roleInfoRepository.findById(roleId).get());
                rolemenu.setMenuInfo(menuInfoRepository.findById(Integer.valueOf(mid)).get());
                roleMenuRepository.save(rolemenu);
            }
        }
    }

    @Override
    public List<Map<String, Object>> getRoleByUserIdAndUserBAId(Long userId, BigInteger userBAId) {
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        List<?> list = roleInfoRepository.findByUserBAId(userBAId);

        for(Object obj :  list){
            Map map = new HashMap();
            map.putAll((Map) obj);

            map.put("checked", map.get("rid")!=null&&!map.get("rid").equals("")?true:false);
            result.add(map);
        }
        return result;
    }
}
