package com.sinosoft.service.impl;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.BranchInfo;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.MenuInfoRepository;
import com.sinosoft.repository.RoleMenuRepository;
import com.sinosoft.repository.UserRoleRepository;
import com.sinosoft.dto.MenuInfoDTO;
import com.sinosoft.domain.UserInfo;
import com.sinosoft.domain.MenuInfo;
import com.sinosoft.service.MenuInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/10 19:49
 * @Description:
 */
@Service
public class MenuInfoServiceImpl implements MenuInfoService {

    @Resource
    private MenuInfoRepository menuInfoRepository;
    @Resource
    private UserRoleRepository userRoleRepository;
    @Resource
    private RoleMenuRepository roleMenuRepository;
    @Resource
    private BranchInfoRepository branchInfoRepository;

    @Override
    public List<?> initMenuInfo(UserInfo u, String accountId) {
        //当前登录机构ID
        Integer branchId = ((BranchInfo) branchInfoRepository.findByComCode(CurrentUser.getCurrentLoginManageBranch()).get(0)).getId();
        StringBuffer sql = new StringBuffer("select role_id from userrole where 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sql.append(" and user_id = ?" + paramsNo);
        params.put(paramsNo, u.getId());
        paramsNo++;
        sql.append(" and branch_id = ?" + paramsNo);
        params.put(paramsNo, branchId);
        paramsNo++;
        sql.append(" and account_id = ?" + paramsNo);
        params.put(paramsNo, accountId);
        paramsNo++;

        List<?> roleList=userRoleRepository.queryBySql(sql.toString(), params);
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<roleList.size();i++){
            //Map m=(Map) roleList.get(i);
            int roleid=Integer.parseInt(roleList.get(i).toString());

            params = new HashMap<>();
            params.put(1, roleid);

            List<?> menulist=roleMenuRepository.queryBySql("select menu_id from rolemenu where role_id = ?1", params);

            for(int j=0;j<menulist.size();j++){
                //Map m1=(Map) menulist.get(j);
                Long menuid=Long.parseLong( menulist.get(j).toString());
                sb.append(menuid);
                sb.append(",");
            }
        }

        List resultList=new ArrayList();
        for(int i=0;i<roleList.size();i++){
            //Map m=(Map) roleList.get(i);
            int roleid=Integer.parseInt(roleList.get(i).toString());
            //获取所有父菜单为空也就是最外层的父菜单
            List<Map<String, Object>> list = menuInfoRepository.findBySuperMenu();

            for (Object obj : list) {
                Map map = new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenForCheck((Integer) map.get("id"), sb);
               /* map.put("id",map.get("id"));
                map.put("text",map.get("text"));*/
                if(list2!=null){
                    map.put("children",list2);
                }
                if(!resultList.contains(map) && list2!=null && list2.size()>0){
                    resultList.add(map);
                }
                if (!resultList.contains(map) && !(list2!=null && list2.size()>0) && sb.toString().contains(map.get("id").toString())){
                    resultList.add(map);
                }

            }
        }
        return resultList;
    }

    @Override
    public List<?> initMenuInfo(UserInfo u, String branchId, String accountId) {
        StringBuffer sql = new StringBuffer();
        sql.append("select role_id from userrole where 1=1 and user_id = ?1 and branch_id = ?2 and account_id = ?3 ");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, u.getId());
        params.put(2, branchId);
        params.put(3, accountId);

        List<?> roleList=userRoleRepository.queryBySql(sql.toString(), params);
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<roleList.size();i++){
            //Map m=(Map) roleList.get(i);
            int roleid=Integer.parseInt(roleList.get(i).toString());

            params = new HashMap<>();
            params.put(1, roleid);

            List<?> menulist=roleMenuRepository.queryBySql("select menu_id from rolemenu where role_id = ?1", params);
            for(int j=0;j<menulist.size();j++){
                //Map m1=(Map) menulist.get(j);
                Long menuid=Long.parseLong( menulist.get(j).toString());
                sb.append(menuid);
                sb.append(",");
            }
        }

        List resultList=new ArrayList();
        for(int i=0;i<roleList.size();i++){
            //Map m=(Map) roleList.get(i);
            int roleid=Integer.parseInt(roleList.get(i).toString());
            //获取所有父菜单为空也就是最外层的父菜单
            List<Map<String, Object>> list = menuInfoRepository.findBySuperMenu();

            for (Object obj : list) {
                Map map = new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenForCheck((Integer) map.get("id"), sb);
               /* map.put("id",map.get("id"));
                map.put("text",map.get("text"));*/
                if(list2!=null){
                    map.put("children",list2);
                }
                if(!resultList.contains(map) && list2!=null && list2.size()>0){
                    resultList.add(map);
                }
                if (!resultList.contains(map) && !(list2!=null && list2.size()>0) && sb.toString().contains(map.get("id").toString())){
                    resultList.add(map);
                }

            }
        }
        return resultList;
    }

    @Override
    public List<?> initMenuTree(Integer id) {
        List resultList=new ArrayList();
        List<?> menulist=menuInfoRepository.findBySuperMenuIsNullAndRoleId(id);
        for (Object obj : menulist) {
            Map map = new HashMap();
            map.putAll((Map) obj);
            List list2=qryChildrenForCheck2((Integer) map.get("id"), id);
               /* map.put("id",map.get("id"));
                map.put("text",map.get("text"));*/
            if(list2!=null){
                map.put("children",list2);
            }
            if(!resultList.contains(map)){
                resultList.add(map);
            }

        }
        return resultList;
    }

    private List<MenuInfoDTO> qryChildrenForCheck(Integer id){
        List list1=new ArrayList();
        Map<String, Object> attributes = null;
        List<?> list= menuInfoRepository.findByHasMenuAndSuperMenu(id);
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                attributes = new HashMap<String, Object>();
                Map map= new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenForCheck((Integer) map.get("id"));
                if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                    map.put("id",map.get("id"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                    map.put("state","closed");
                }

                attributes.put("url", map.get("url"));
                map.put("id",map.get("id"));
                map.put("text",map.get("text"));
                map.put("attributes", attributes);
                list1.add(map);
            }
        }
        return list1;
    }

    private List<MenuInfoDTO> qryChildrenForCheck(Integer id,StringBuilder sb){
        List list1=new ArrayList();
        Map<String, Object> attributes = null;
        List<?> list= menuInfoRepository.findByHasMenuAndSuperMenu(id);
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                attributes = new HashMap<String, Object>();
                Map map= new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenForCheck((Integer) map.get("id"), sb);
                if(list2!=null&&list2.size()>0){
                    map.put("id",map.get("id"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                    map.put("state","closed");
                }

                attributes.put("url", map.get("url"));
                map.put("id",map.get("id"));
                map.put("text",map.get("text"));
                map.put("attributes", attributes);

                if (list2!=null&&list2.size()>0){
                    list1.add(map);
                }
                if (!(list2!=null&&list2.size()>0) && sb.toString().contains(map.get("id").toString())) {
                    list1.add(map);
                }
            }
        }
        return list1;
    }

    private List<MenuInfoDTO> qryChildrenForCheck2(Integer id, Integer roleId){
        List list1=new ArrayList();
        Map<String, Object> attributes = null;
        List<?> list= menuInfoRepository.findByHasMenuAndSuperMenu(roleId, id);
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                attributes = new HashMap<String, Object>();
                Map map= new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenForCheck2((Integer) map.get("id"), roleId);
                if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                    map.put("id",map.get("id"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                    map.put("state","closed");
                    map.put("checked", map.get("roleid")!=null&&!map.get("roleid").equals("")?true:false);
                }

                attributes.put("url", map.get("url"));
                map.put("id",map.get("id"));
                map.put("text",map.get("text"));
                map.put("attributes", attributes);
                map.put("checked", map.get("roleid")!=null&&!map.get("roleid").equals("")?true:false);
                list1.add(map);
            }
        }
        return list1;
    }

    @Override
    public List<?> qryMenuInfoAll() {
        List<Map<String, Object>> list = menuInfoRepository.findBySuperMenuAll();
        List resultList=new ArrayList();
        //遍历查询父菜单的子菜单
        for (Object obj : list) {
            Map map = new HashMap();
            map.putAll((Map) obj);
            List list2=qryChildren((Integer) map.get("id"));
            if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                map.put("children",list2);
                map.put("endFlag","1"); //非末级
                map.put("state","closed");
            }
            if(!resultList.contains(map)){
                map.put("operate",(Integer) map.get("id"));
                resultList.add(map);
            }
        }
        return resultList;
    }

    //递归查询子菜单
    private List<MenuInfoDTO> qryChildren(Integer id){
        List list1=new ArrayList();
        List<?> list= menuInfoRepository.findByHasMenuAndSuperMenuAll(id);
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map= new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildren((Integer) map.get("id"));
                if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                    map.put("children",list2);
                    map.put("endFlag","1"); //非末级
                    map.put("state","closed");
                }
                map.put("operate",(Integer) map.get("id"));
                list1.add(map);
            }
        }
        return list1;
    }

    @Override
    public List<?> qryMenuInfo() {
        List resultList=new ArrayList();
        //获取所有父菜单为空也就是最外层的父菜单
        List<Map<String, Object>> list = menuInfoRepository.findBySuperMenu();
        //遍历查询父菜单的子菜单
        for (Object obj : list) {
            Map map = new HashMap();
            map.putAll((Map) obj);
            List list2=qryChildrenForCheck((Integer) map.get("id"));
            if(list2!=null){
                map.put("children",list2);
            }
            resultList.add(map);
        }
        return resultList;
    }

    @Override
    public MenuInfo findMenuInfo(int id) {
        return menuInfoRepository.getOne(id);
    }

    @Override
    @Transactional
    public void saveMenuInfo(MenuInfoDTO mdt) {
        boolean flag = menuInfoRepository.existsById(mdt.getId());
        if(flag){
            throw new RuntimeException("exist");
        }
        MenuInfo m=new MenuInfo();
        m.setId(mdt.getId());
        m.setChildCount(mdt.getChildCount());
        m.setMenuCode(mdt.getMenuCode());
        m.setMenuIcon(mdt.getMenuIcon());
        m.setMenuName(mdt.getMenuName());
        m.setScript(mdt.getScript());
        if(!mdt.getSuperMenu().isEmpty() && !mdt.getSuperMenu().equals("undefined")){
            m.setSuperMenu(mdt.getSuperMenu());
        }
        m.setMenuOrder(mdt.getMenuOrder());
        m.setTemp(mdt.getTemp());
        UserInfo u = CurrentUser.getCurrentUser();
        m.setCreateoper(String.valueOf(u.getId()));
        m.setCreateTime(CurrentTime.getCurrentTime());
        menuInfoRepository.save(m);
    }

    @Override
    @Transactional
    public void updateMenuInfo(MenuInfoDTO mdt) {
        MenuInfo m = menuInfoRepository.findById(mdt.getId()).get();
        //m.setId(mdt.getId()); id不能修改
        m.setChildCount(mdt.getChildCount());
        m.setMenuCode(mdt.getMenuCode());
        m.setMenuIcon(mdt.getMenuIcon());
        m.setMenuName(mdt.getMenuName());
        m.setScript(mdt.getScript());
        m.setMenuOrder(mdt.getMenuOrder());
        m.setTemp(mdt.getTemp());
        UserInfo u = CurrentUser.getCurrentUser();
        m.setUpdateoper(String.valueOf(u.getId()));
        m.setUpdateTime(CurrentTime.getCurrentTime());
        if(!mdt.getSuperMenu().isEmpty() && !mdt.getSuperMenu().equals("undefined")){
            m.setSuperMenu(mdt.getSuperMenu());
        }
        menuInfoRepository.save(m);
    }

    @Override
    @Transactional
    public String deleteMenuInfo(int id) {
        //删除之前先判断（查询角色菜单配置表）是否允许删除

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, id);

        List<?> roleCode=userRoleRepository.queryBySql("select r.role_code from roleinfo r left join rolemenu ro on ro.role_id = r.id where ro.menu_id = ?1", params);
        if(roleCode!=null&&roleCode.size()>0){
            String str = "";
            for (int i=0; i<roleCode.size(); i++) {
                if(i>1){
                    break;
                }
                str +=  roleCode.get(i).toString() + ",";
            }
            if(roleCode.size()>2){
                return "请先修改"+str.substring(0,str.length()-1)+"等角色的菜单信息";
            }
            return "请先修改下列角色菜单信息："+str.substring(0,str.length()-1);
        }else{
            MenuInfo m = menuInfoRepository.findById(id).get();
            menuInfoRepository.delete(m);
        }
        return "";
    }


}
