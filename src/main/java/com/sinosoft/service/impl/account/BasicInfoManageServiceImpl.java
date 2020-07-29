package com.sinosoft.service.impl.account;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.*;
import com.sinosoft.dto.CodeManageDTO;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.CodeSelectRepository;
import com.sinosoft.service.account.BasicInfoManageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BasicInfoManageServiceImpl implements BasicInfoManageService {
    @Resource
    private BranchInfoRepository branchInfoRepository;
    @Resource
    private CodeSelectRepository codeSelectRepository;

    @Override
    public List<?> qryBasicInfo(String codeType) {
        List<Map<String, Object>> list = codeSelectRepository.findCodeSelectInfoByCodeType(codeType);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (list!=null && list.size()>0) {
             for (Map<String, Object> map : list) {
                 String str = checkCodeManageIsUse(codeType, (String) map.get("codeCode"));
                 Map<String, Object> m = new HashMap<String, Object>();
                 m.putAll(map);
                 m.put("use", str);
                 result.add(m);
             }
        }
        return result;
    }

    @Override
    @Transactional
    public String addBasicInfo(CodeManageDTO dto) {
        List<?> list = codeSelectRepository.findCodeSelectInfo(dto.getCodeType(), dto.getCodeCode());
        if (list!=null && list.size()>0) {
            return "exist";
        }

        CodeSelectId id = new CodeSelectId();
        CodeSelect codeSelect = new CodeSelect();
        id.setCodeType(dto.getCodeType());
        id.setCodeCode(dto.getCodeCode());
        codeSelect.setId(id);
        codeSelect.setCodeName(dto.getCodeName());
        codeSelect.setTemp(dto.getTemp());

        if (dto.getOrderBy()!=null && !"".equals(dto.getOrderBy())) {
            //需要先判断是否已占用
            List<?> qList = codeSelectRepository.findCodeSelectInfoByCodeTypeAndOrderBy(dto.getCodeType(), dto.getOrderBy());
            if (qList!=null && qList.size()>0) {
                return "hasOccupy";
            }
            codeSelect.setOrderBy(dto.getOrderBy());
        } else {
            codeSelect.setOrderBy(null);
        }

        codeSelectRepository.save(codeSelect);

        return null;
    }

    @Override
    @Transactional
    public String updateBasicInfo(CodeManageDTO dto) {
        CodeSelect codeSelect = codeSelectRepository.findCodeSelect(dto.getCodeType(), dto.getCodeCode());
        if (codeSelect==null) {
            return "unExist";
        }

        // 校验是否已被使用，已被使用的是否允许编辑

        // 设置
        codeSelect.setCodeName(dto.getCodeName());
        codeSelect.setTemp(dto.getTemp());
        if (dto.getOrderBy()!=null && !"".equals(dto.getOrderBy())) {
            if (!dto.getOrderBy().equals(codeSelect.getOrderBy())) {
                //需要先判断是否已占用
                List<?> qList = codeSelectRepository.findCodeSelectInfoByCodeTypeAndOrderBy(dto.getCodeType(), dto.getOrderBy());
                if (qList!=null && qList.size()>0) {
                    return "hasOccupy";
                }
                codeSelect.setOrderBy(dto.getOrderBy());
            }
        } else {
            codeSelect.setOrderBy(null);
        }

        codeSelectRepository.save(codeSelect);
        return null;
    }

    @Override
    @Transactional
    public String deleteBasicInfo(CodeManageDTO dto) {
        CodeSelect codeSelect = codeSelectRepository.findCodeSelect(dto.getCodeType(), dto.getCodeCode());
        if (codeSelect==null) {
            return "unExist";
        }

        // 校验是否已被使用
        String result = checkCodeManageIsUse(dto.getCodeType(), dto.getCodeCode());
        if (result!=null && !"".equals(result)) {
            return result;
        }

        codeSelectRepository.delete(codeSelect);
        return null;
    }

    @Override
    @Transactional
    public String upOrDownBasicInfo(String type, CodeManageDTO dto) {
        CodeSelect codeSelect = codeSelectRepository.findCodeSelect(dto.getCodeType(), dto.getCodeCode());
        if (codeSelect==null) {
            return "unExist";
        }

        String orderBy = codeSelect.getOrderBy();

        List<CodeSelect> lastOrNextList = null;
        if ("up".equals(type)) {
            lastOrNextList = codeSelectRepository.lastCodeSelectByOrder(dto.getCodeType(), orderBy);
        } else if ("down".equals(type)) {
            lastOrNextList = codeSelectRepository.nextCodeSelectByOrder(dto.getCodeType(), orderBy);
        } else {
            return "paramError";
        }
        if (lastOrNextList==null || lastOrNextList.size()<1) {
            if ("up".equals(type)) {
                return "noLast";
            } else {
                return "noNext";
            }
        }
        CodeSelect lastOrNextCodeSelect = lastOrNextList.get(0);

        codeSelect.setOrderBy(lastOrNextCodeSelect.getOrderBy());
        lastOrNextCodeSelect.setOrderBy(orderBy);

        codeSelectRepository.save(codeSelect);
        codeSelectRepository.save(lastOrNextCodeSelect);
        return null;
    }

    private String checkCodeManageIsUse(String codeType, String codeCode){
        StringBuffer sql = new StringBuffer();

        if ("voucherType".equals(codeType)) {//凭证类型
            sql.append("select distinct a.voucher_type from accmainvoucher a where a.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and a.voucher_type = '" + codeCode + "'");

            String str = sql.toString();
            str = str.replaceAll("accmainvoucher", "accmainvoucherhis");

            sql.append(" union all ");
            sql.append(str);
        } else if ("systemSource".equals(codeType)) {//凭证来源（系统来源）
            sql.append("select distinct a.voucher_type from accmainvoucher a where  a.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and a.data_source = '" + codeCode + "'");

            String str = sql.toString();
            str = str.replaceAll("accmainvoucher", "accmainvoucherhis");

            sql.append(" union all ");
            sql.append(str);
        }

        if (sql.length()>0) {
            List<?> list = codeSelectRepository.queryBySql(sql.toString());
            if (list!=null && list.size()>0) {
                return "use";
            }
        }

        return "";
    }
}
