package com.sinosoft.service.impl;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.domain.*;
import com.sinosoft.dto.BranchInfoDTO;
import com.sinosoft.repository.*;
import com.sinosoft.service.BranchInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class BranchInfoServiceImpl implements BranchInfoService {
    @Resource
    private BranchInfoRepository branchInfoRepository;
    @Resource
    private UserInfoRepository userInfoRepository;
    @Resource
    private BranchAccountRepository branchAccountRepository;

    @Override
    public Page<BranchInfoDTO> qryBranchInfo(int page, int rows, BranchInfo branchInfo) {
        //return userInfoRepository.findAll(new PageRequest((page - 1), rows));
        Page<BranchInfo> pageInfo = branchInfoRepository.findAll(new CusSpecification<>().and(
                CusSpecification.Cnd.like("comCode", branchInfo.getComCode()),
                CusSpecification.Cnd.like("comName", branchInfo.getComName())
        ).asc("level").asc("comCode"),new PageRequest((page - 1), rows));

        //设置创建人、修改人名字
        List<BranchInfo> listInfo = pageInfo.getContent();
        List<BranchInfoDTO> listDto = new ArrayList<BranchInfoDTO>();
        if(listInfo!=null&&listInfo.size()>0) {
            for (BranchInfo info : listInfo) {
                BranchInfoDTO dto = BranchInfoDTO.toDTO(info);
                if (info.getCreateBy().equals(info.getLastModifyBy())) {
                    String name = userInfoRepository.findById(Long.parseLong(info.getCreateBy())).get().getUserName();
                    dto.setCreateByName(name);
                    dto.setLastModifyByName(name);
                } else {
                    dto.setCreateByName(userInfoRepository.findById(Long.parseLong(info.getCreateBy())).get().getUserName());
                    if (info.getLastModifyBy()!=null&&!"".equals(info.getLastModifyBy())&&!"null".equals(info.getLastModifyBy())) {
                        dto.setLastModifyByName(userInfoRepository.findById(Long.parseLong(info.getLastModifyBy())).get().getUserName());
                    }
                }

                //替换（添加）上级机构编码
                if (info.getSuperCom()!=null&&!"".equals(info.getSuperCom())) {
                    BranchInfo b = branchInfoRepository.findById(Integer.valueOf(info.getSuperCom())).get();
                    if (b!=null) {
                        dto.setSuperComCode(b.getComCode());
                    }
                }
                listDto.add(dto);
            }
        }
        Page<BranchInfoDTO> pageList = new PageImpl<>(listDto, pageInfo.getPageable(), pageInfo.getTotalElements());
        return pageList;
    }

    @Override
    @Transactional
    public void saveBranchInfo(BranchInfoDTO dto) {
        //先判断是否已经存在该机构编码
        List<?> list = branchInfoRepository.checkExistsComCode(dto.getComCode());
        if (list!=null&&list.size()>0) {
            throw new RuntimeException("exist");
        }
        BranchInfo branchInfo = BranchInfoDTO.toEntity(dto);
        UserInfo u = CurrentUser.getCurrentUser();
        branchInfo.setCreateBy(String.valueOf(u.getId()));
        branchInfo.setCreateTime(CurrentTime.getCurrentTime());

        branchInfoRepository.save(branchInfo);
    }

    @Override
    @Transactional
    public void updateBranchInfo(int id, BranchInfoDTO dto) {
        BranchInfo branchInfo = branchInfoRepository.findById(id).get();

        //设置不允许修改的机构信息
        dto.setId(branchInfo.getId());
        dto.setComCode(branchInfo.getComCode());
        dto.setCreateBy(branchInfo.getCreateBy());
        dto.setCreateTime(branchInfo.getCreateTime());

        UserInfo u = CurrentUser.getCurrentUser();
        dto.setLastModifyBy(String.valueOf(u.getId()));
        dto.setLastModifyTime(CurrentTime.getCurrentTime());
        dto.toEntity(branchInfo);
        branchInfoRepository.save(branchInfo);
    }

    @Override
    @Transactional
    public String deleteBranchInfo(int id) {
        BranchInfo branchInfo = branchInfoRepository.findById(id).get();

        //如果是机构，删除之前先判断，本机构是否允许删除（本机构是否存在下级机构，本机构是否已经使用）
        String checkResult = checkWhetherExistsSubordinateOrUseCom(id);
        if(!checkResult.isEmpty()){
            return checkResult;
        }
        //先删除关联账套数据信息
        List<BranchAccount> list = branchAccountRepository.findAll(new CusSpecification<>().and(CusSpecification.Cnd.eq("branchInfo.id", branchInfo.getId())));
        if (list!=null&&list.size()>0) { for (BranchAccount b : list) { branchAccountRepository.delete(b); } }
        //再删除机构数据信息
        branchInfoRepository.delete(branchInfo);
        return "";
    }

    @Override
    public List<?> initBranchTreeRecursion(Integer accountId) {
        List resultList=new ArrayList();
        List<?> branchlist=branchInfoRepository.findBySuperComIsNullAndAccountId(accountId);
        for (Object obj : branchlist) {
            Map map = new HashMap();
            map.putAll((Map) obj);
            List list2=qryChildrenForCheck((Integer) map.get("id"), accountId);
            if(list2!=null){
                map.put("children",list2);
            }
            if(!resultList.contains(map)){
                map.put("checked", map.get("accountId")!=null&&!map.get("accountId").equals("")?true:false);
                resultList.add(map);
            }
        }
        return resultList;
    }

    @Override
    public List<?> initBranchTree(Integer accountId) {
        List resultList=new ArrayList();
        List<?> branchlist=branchInfoRepository.findBranchTreeByAccountId(accountId);
        for (Object obj : branchlist) {
            Map map = new HashMap();
            map.putAll((Map) obj);
            if(!resultList.contains(map)){
                map.put("checked", map.get("accountId")!=null&&!map.get("accountId").equals("")?true:false);
                resultList.add(map);
            }
        }
        return resultList;
    }

    private List<BranchInfoDTO> qryChildrenForCheck(Integer id, Integer accountId){
        List list1=new ArrayList();
        List<?> list= branchInfoRepository.findByHasBranchAndSuperCom(accountId, id);
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map= new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenForCheck((Integer) map.get("id"), accountId);
                if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                    map.put("id",map.get("id"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                    map.put("state","closed");
                    map.put("checked", map.get("accountId")!=null&&!map.get("accountId").equals("")?true:false);
                }

                map.put("id",map.get("id"));
                map.put("text",map.get("text"));
                map.put("checked", map.get("accountId")!=null&&!map.get("accountId").equals("")?true:false);
                list1.add(map);
            }
        }
        return list1;
    }

    @Override
    public List<?> getAccountByUserIdAndBranchId(Long userId, Integer branchId) {
        if (!(userId!=null&&userId!=0)) {
            userId = CurrentUser.getCurrentUser().getId();
        }
        if (!(branchId!=null&&branchId!=0)) {
            //当前登录机构编码
            String currentLoginManageBranch = CurrentUser.getCurrentLoginManageBranch();
            List<?> branchList = branchInfoRepository.findByComCode(currentLoginManageBranch);
            branchId = ((BranchInfo) branchList.get(0)).getId();
        }

        UserInfo user = userInfoRepository.findById(userId).get();
        List<?> list = null;
        if (user!=null && user.getManageCode()!=null && !"".equals(user.getManageCode()) && Arrays.asList(user.getManageCode().split(",")).contains(String.valueOf(branchId))) {
            StringBuffer sql=new StringBuffer("select a.id as id,a.account_code as accountCode,a.account_name as accountName from userbranchaccount uba left join accountinfo a on a.id = uba.account_id where 1=1");

            int paramsNo = 1;
            Map<Integer, Object> params = new HashMap<>();

            sql.append(" and uba.user_id = ?" + paramsNo);
            params.put(paramsNo, userId);
            paramsNo++;
            sql.append(" and uba.branch_id = ?" + paramsNo);
            params.put(paramsNo, branchId);
            paramsNo++;

            sql.append(" order by a.id");
            list = branchInfoRepository.queryBySqlSC(sql.toString(), params);
        }
        return list;
    }

    @Override
    public List<?> getBranchAccountByUserId(Long userId) {
        if (!(userId!=null&&userId!=0)) {
            userId = CurrentUser.getCurrentUser().getId();
        }

        StringBuffer sql=new StringBuffer("select uba.id as id,b.com_name as comName,a.account_name as accountName from userbranchaccount uba left join branchinfo b on b.id = uba.branch_id left join accountinfo a on a.id = uba.account_id where 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sql.append(" and uba.user_id = ?" + paramsNo);
        params.put(paramsNo, userId);
        paramsNo++;

        sql.append(" order by b.id,a.id");
        List<?> list = branchInfoRepository.queryBySqlSC(sql.toString(), params);
        return list;
    }

    @Override
    public List<BranchInfo> getManageBranchByUserId(Long userId) {
        if (!(userId!=null&&userId!=0)) {
            userId = CurrentUser.getCurrentUser().getId();
        }

        UserInfo user = userInfoRepository.findById(userId).get();
        List<BranchInfo> list = null;
        if (user!=null&&user.getManageCode()!=null&&!"".equals(user.getManageCode())) {
            StringBuffer sql=new StringBuffer("select * from branchinfo b where b.id in ( ?1 ) order by id ");

            Map<Integer, Object> params = new HashMap<>();
            params.put(1, Arrays.asList(user.getManageCode().split(",")));

            list = (List<BranchInfo>)branchInfoRepository.queryBySql(sql.toString(), params, BranchInfo.class);
        }
        return list;
    }

    private String checkWhetherExistsSubordinateOrUseCom(int id){
        List<?> list = branchInfoRepository.checkExistsSubordinate(String.valueOf(id));
        if(list!=null&&list.size()>0){
            return "exist";
        }
        //existUse
        return "";
    }
}
