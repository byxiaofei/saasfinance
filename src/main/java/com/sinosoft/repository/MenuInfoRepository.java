package com.sinosoft.repository;

import com.sinosoft.domain.MenuInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/11 15:48
 * @Description:
 */
@Repository
public interface MenuInfoRepository extends BaseRepository<MenuInfo, Integer> {
    /**
     * 按层级结构查询一级菜单信息（只查询菜单ID(id)、菜单名称(text)）
     * @return
     */
    @Query(value = "select m.id AS id,m.menu_name as text  from menuinfo m  where super_menu is null order by id", nativeQuery = true)
    List<Map<String, Object>> findBySuperMenu();

    /**
     * 根据父菜单和角色ID查询
     * @param id  角色ID
     * @return
     */
    @Query(value = "select m.id AS id,m.menu_name as text  from menuinfo m left join rolemenu rm on m.id = rm.menu_id and rm.role_id = ?1 where super_menu is null order by id", nativeQuery = true)
    List<Map<String, Object>> findBySuperMenuIsNullAndRoleId(Integer id);

    /**
     * 按层级结构查询非一级菜单信息（只查询菜单ID(id)、菜单名称(text)、菜单URL(url)）
     * @param superMenu
     * @return
     */
    @Query(value = "select m.id as id ,m.menu_name as text ,m.script as url from menuinfo m where super_menu=?1 order by m.id", nativeQuery = true)
    List<Map<String, Object>> findByHasMenuAndSuperMenu(Integer superMenu);

    /**
     * 按层级结构查询非一级菜单信息（只查询菜单ID(id)、菜单名称(text)、菜单URL(url)）
     * @param superMenu
     * @param roleId
     * @return
     */
    @Query(value = "select m.id as id ,m.menu_name as text ,rm.role_id as roleid from menuinfo m left join rolemenu rm on m.id = rm.menu_id and rm.role_id =?1 where super_menu=?2 order by m.id", nativeQuery = true)
    List<Map<String, Object>> findByHasMenuAndSuperMenu(Integer roleId, Integer superMenu);
    /**
     * 按层级结构查询一级菜单信息
     * @return
     */
    @Query(value = "select id \"id\",child_count \"childCount\",menu_code \"menuCode\",menu_name \"menuName\",temp \"temp\",script \"script\",super_menu \"_parentId\",menu_order \"menuOrder\",menu_icon \"menuIcon\" from menuinfo where super_menu is null order by id asc", nativeQuery = true)
    List<Map<String, Object>> findBySuperMenuAll();
    /**
     * 按层级结构查询非一级菜单信息
     * @param superMenu
     * @return
     */
    @Query(value = "select id \"id\",child_count \"childCount\",menu_code \"menuCode\",menu_name \"menuName\",temp \"temp\",script \"script\",super_menu \"_parentId\",menu_order \"menuOrder\",menu_icon \"menuIcon\" from menuinfo where super_menu=?1 order by id asc", nativeQuery = true)
    List<Map<String, Object>> findByHasMenuAndSuperMenuAll(Integer superMenu);
}
