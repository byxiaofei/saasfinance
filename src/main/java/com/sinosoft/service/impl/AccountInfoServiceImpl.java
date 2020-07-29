package com.sinosoft.service.impl;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.domain.*;
import com.sinosoft.dto.AccountInfoDTO;
import com.sinosoft.dto.SpecialInfoDTO;
import com.sinosoft.dto.SubjectInfoDTO;
import com.sinosoft.repository.*;
import com.sinosoft.repository.account.AccMonthRespository;
import com.sinosoft.service.AccountInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountInfoServiceImpl implements AccountInfoService {
    @Resource
    private AccountInfoRepository accountInfoRepository;
    @Resource
    private UserInfoRepository userInfoRepository;
    @Resource
    private SubjectRepository subjectRepository;
    @Resource
    private SpecialInfoRepository specialInfoRepository;
    @Resource
    private CodeSelectRepository codeSelectRepository;
    @Resource
    private BranchAccountRepository branchAccountRepository;
    @Resource
    private BranchInfoRepository branchInfoRepository;
    @Resource
    private AccMonthRespository accMonthRespository;

    @Override
    public Page<AccountInfoDTO> qryAccountInfo(int page, int rows, AccountInfo accountInfo) {
        //return userInfoRepository.findAll(new PageRequest((page - 1), rows));
        Page<AccountInfo> pageInfo = accountInfoRepository.findAll(new CusSpecification<>().and(
                CusSpecification.Cnd.like("accountCode", accountInfo.getAccountCode()),
                CusSpecification.Cnd.like("accountName", accountInfo.getAccountName())
        ).asc("accountCode"),new PageRequest((page - 1), rows));

        List<AccountInfo> listInfo = pageInfo.getContent();
        List<AccountInfoDTO> listDto = new ArrayList<AccountInfoDTO>();
        if(listInfo!=null&&listInfo.size()>0) {
            for (AccountInfo info : listInfo) {
                //设置创建人、修改人名字
                AccountInfoDTO dto = AccountInfoDTO.toDTO(info);
                if (info.getCreateBy().equals(info.getLastModifyBy())) {
                    String name = getUserNameByUserId(Long.parseLong(info.getCreateBy()));
                    dto.setCreateByName(name);
                    dto.setLastModifyByName(name);
                } else {
                    dto.setCreateByName(getUserNameByUserId(Long.parseLong(info.getCreateBy())));
                    if (info.getLastModifyBy()!=null&&!"".equals(info.getLastModifyBy())&&!"null".equals(info.getLastModifyBy())) {
                        dto.setLastModifyByName(getUserNameByUserId(Long.parseLong(info.getLastModifyBy())));
                    }
                }
                //设置账套类型名称
                List list = codeSelectRepository.findCodeSelectInfo("accountType", dto.getAccountType());
                dto.setAccountTypeName(((Map<String,Object>)list.get(0)).get("codeName").toString());
                //设置不允许删除标志 Y：不允许删除
                if(!checkWhetherUseAccountByAccountCode(info.getAccountCode()).isEmpty()){
                    dto.setNotAllowDel("Y");
                }
                listDto.add(dto);
            }
        }
        Page<AccountInfoDTO> pageList = new PageImpl<>(listDto, pageInfo.getPageable(), pageInfo.getTotalElements());
        return pageList;
    }

    @Override
    @Transactional
    public void saveAccountInfo(AccountInfoDTO dto) {
        //先判断是否已经存在该账套编码
        List<?> list = accountInfoRepository.checkExistsAccountCode(dto.getAccountCode());
        if (list!=null&&list.size()>0) {
            throw new RuntimeException("exist");
        }
        list = accountInfoRepository.checkExistsAccountName(dto.getAccountName());
        if (list!=null&&list.size()>0) {
            throw new RuntimeException("existName");
        }
        AccountInfo accountInfo = AccountInfoDTO.toEntity(dto);
        UserInfo u = CurrentUser.getCurrentUser();
        accountInfo.setCreateBy(String.valueOf(u.getId()));
        accountInfo.setCreateTime(CurrentTime.getCurrentTime());
        if (!(dto.getUseFlag()!=null&&!"".equals(dto.getUseFlag()))) {
           accountInfo.setUseFlag("1"); //默认为使用
        }
        accountInfoRepository.save(accountInfo);
        accountInfoRepository.flush();

        //新增账套时判断是否初始化
        if ("1".equals(dto.getInitAccount())&&(dto.getReferToAccount()!=null&&!"".equals(dto.getReferToAccount()))) {
            AccountInfo initAccountInfo = accountInfoRepository.findById(Integer.valueOf(dto.getReferToAccount())).get();
            StringBuffer sql1 = new StringBuffer("SELECT * FROM subjectinfo s WHERE 1=1");
            sql1.append(" AND s.account='"+initAccountInfo.getAccountCode()+"'");
            sql1.append(" ORDER BY CONCAT(s.all_subject,s.subject_code) ASC");
            List<SubjectInfo> list1 = (List<SubjectInfo>) subjectRepository.queryBySql(sql1.toString(), SubjectInfo.class);

            StringBuffer sql2 = new StringBuffer("SELECT * FROM specialinfo s WHERE 1=1");
            sql2.append(" AND s.account='"+initAccountInfo.getAccountCode()+"'");
            sql2.append(" ORDER BY s.special_code");
            List<SpecialInfo> list2 = (List<SpecialInfo>) specialInfoRepository.queryBySql(sql2.toString(), SpecialInfo.class);

            Map<String, String> specialIdCodeMap = new HashMap<>();
            Map<String, Long> specialCodeIdMap = new HashMap<>();
            if (list2!=null&&list2.size()>0) {
                for (SpecialInfo s : list2) {
                    SpecialInfo specialInfo = SpecialInfoDTO.toNewEntity(s);
                    //所属账套
                    specialInfo.setAccount(accountInfo.getAccountCode());
                    //暂存原专项ID，以备后用
                    specialIdCodeMap.put(specialInfo.getId()+"", specialInfo.getSpecialCode());
                    specialInfo.setId(0);
                    SpecialInfo si = specialInfoRepository.save(specialInfo);
                    //暂存专项ID，以备后用
                    specialCodeIdMap.put(si.getSpecialCode(), si.getId());
                    specialInfoRepository.flush();
                    //修改当前专项的父级ID
                    if (si.getSuperSpecial()!=null&&!"".equals(si.getSuperSpecial())) {
                        si.setSuperSpecial(String.valueOf(specialCodeIdMap.get(specialIdCodeMap.get(si.getSuperSpecial()))));
                    }
                    specialInfoRepository.save(si);
                    specialInfoRepository.flush();
                }
            }

            if (list1!=null&&list1.size()>0) {
                Map<String, Long> subjectIdMap = new HashMap<>();
                for (SubjectInfo s : list1) {
                    SubjectInfo subjectInfo = SubjectInfoDTO.toNewEntity(s);
                    //所属账套
                    subjectInfo.setAccount(accountInfo.getAccountCode());
                    subjectInfo.setId(0);

                    SubjectInfo si = subjectRepository.save(subjectInfo);
                    //暂存科目ID，以备后用
                    subjectIdMap.put(si.getAllSubject()+si.getSubjectCode()+"/", si.getId());
                    subjectRepository.flush();
                    //修改当前科目的父级ID
                    if (si.getSuperSubject()!=null&&!"".equals(si.getSuperSubject())) {
                        si.setSuperSubject(String.valueOf(subjectIdMap.get(si.getAllSubject())));
                    }
                    //修改当前科目所挂专项的ID
                    if (si.getSpecialId()!=null&&!"".equals(si.getSpecialId())) {
                        String[] ids = si.getSpecialId().split(",");
                        String specialId = "";
                        for (String str : ids) {
                            specialId += specialCodeIdMap.get(specialIdCodeMap.get(str))+",";
                        }
                        if (!"".equals(specialId)) {
                            specialId = specialId.substring(0, specialId.length()-1);
                        }
                        si.setSpecialId(specialId);
                    }
                    subjectRepository.save(si);
                    subjectRepository.flush();
                }
            }
        }
    }

    @Override
    @Transactional
    public void updateAccountInfo(int id, AccountInfoDTO dto) {
        AccountInfo accountInfo = accountInfoRepository.findById(id).get();
        List<?> list = null;
        if (dto.getAccountName()!=null&&!"".equals(dto.getAccountName())&& !accountInfo.getAccountName().equals(dto.getAccountName())){
            //修改账套名称，需要校验
            list = accountInfoRepository.checkExistsAccountName(dto.getAccountName());
            if (list!=null&&list.size()>0) {
                throw new RuntimeException("existName");
            }
            accountInfo.setAccountName(dto.getAccountName());
        }

        /*if (dto.getAccountType()!=null&&!"".equals(dto.getAccountType())) {accountInfo.setAccountType(dto.getAccountType()); }
        if (dto.getAccountCode()!=null&&!"".equals(dto.getAccountCode())) {accountInfo.setAccountCode(dto.getAccountCode()); }*/

        if (dto.getUseFlag()!=null&&!"".equals(dto.getUseFlag())) {accountInfo.setUseFlag(dto.getUseFlag());}
        if (dto.getRemark()!=null&&!"".equals(dto.getRemark())) {accountInfo.setRemark(dto.getRemark()); }

        UserInfo u = CurrentUser.getCurrentUser();
        accountInfo.setLastModifyBy(String.valueOf(u.getId()));
        accountInfo.setLastModifyTime(CurrentTime.getCurrentTime());

        accountInfoRepository.save(accountInfo);
    }

    @Override
    @Transactional
    public String deleteAccountInfo(int id) {
        AccountInfo accountInfo = accountInfoRepository.findById(id).get();
        //删除之前先判断关联的科目和专项是否已经使用（无论科目和专项是否停用），即账套是否已使用过
        String checkResult = checkWhetherUseAccountByAccountCode(accountInfo.getAccountCode());
        if(!checkResult.isEmpty()){
            return "exist";
        }
        //先删除关联机构数据信息
        List<BranchAccount> list = branchAccountRepository.findAll(new CusSpecification<>().and(CusSpecification.Cnd.eq("accountInfo.id", accountInfo.getId())));
        if (list!=null&&list.size()>0) { for (BranchAccount b : list) { branchAccountRepository.delete(b); } }
        //再删除账套数据信息
        accountInfoRepository.delete(accountInfo);
        //同时删除该账套下的所有科目和专项
        List<SubjectInfo> list1 = subjectRepository.findAll(new CusSpecification<>().and(CusSpecification.Cnd.eq("account", accountInfo.getAccountCode())));
        List<SpecialInfo> list2 = specialInfoRepository.findAll(new CusSpecification<>().and(CusSpecification.Cnd.eq("account", accountInfo.getAccountCode())));
        if (list1!=null&&list1.size()>0) { for (SubjectInfo s : list1) { subjectRepository.delete(s); } }
        if (list2!=null&&list2.size()>0) { for (SpecialInfo s : list2) { specialInfoRepository.delete(s); } }
        return "";
    }

    @Override
    @Transactional
    public void saveBranchAccount(String branchId, Integer accountId) {
        String[] branch_ids = branchId.split(",");
        //去掉重复项
        List<String> addList = new ArrayList<String>();
        for(String b:branch_ids){
            if(!b.equals("") && !addList.contains(b)){
                addList.add(b);
            }
        }

        List<?> branchaccounts = branchAccountRepository.findBranchAccountByAccountId(accountId);
        for (Object obj: branchaccounts) {
            BranchAccount branchAccount = (BranchAccount) obj;
            String branchInfoId = String.valueOf(branchAccount.getBranchInfo().getId());
            if (addList.contains(branchInfoId)) {
                addList.remove(branchInfoId);
            } else {
                branchAccountRepository.delete(branchAccount);
            }
        }

        branchAccountRepository.flush();

        if(addList.size()>0){
            //添加角色菜单信息
            for(String bid : addList){
                BranchAccount branchAccount= new BranchAccount();
                branchAccount.setBranchInfo(branchInfoRepository.findById(Integer.valueOf(bid)).get());
                branchAccount.setAccountInfo(accountInfoRepository.findById(accountId).get());
                branchAccountRepository.save(branchAccount);
            }
        }
    }

    public Integer getAccountIdByAccountCode(String accountCode){
        List<AccountInfo> list = accountInfoRepository.findAll(new CusSpecification<>().and(CusSpecification.Cnd.eq("accountCode", accountCode)));
        if (list!=null&&list.size()>0) {
            return list.get(0).getId();
        }else {
            return null;
        }
    }

    private String checkWhetherUseAccountByAccountCode(String accountCode){
        //判断账套是否使用，只要会计期间表中存在即代表使用过
        List<?> list = accMonthRespository.findAll(new CusSpecification<>().and(CusSpecification.Cnd.eq("id.accBookCode", accountCode)));
        if (list!=null && list.size()>0) {
            return "exist";
        } else {
            return "";
        }
    }

    private String getUserNameByUserId(Long id){
        return userInfoRepository.findById(id).get().getUserName();
    }

    @Override
    public List<Map<String, Object>> getAccountByUserIdAndBranchId(Long userId, Integer branchId) {
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        List<?> list = accountInfoRepository.findByUserIdAndBranchId(userId, branchId);

        for(Object obj :  list){
            Map map = new HashMap();
            map.putAll((Map) obj);

            map.put("checked", map.get("aid")!=null&&!"".equals(map.get("aid"))?true:false);
            result.add(map);
        }
        return result;
    }
}
