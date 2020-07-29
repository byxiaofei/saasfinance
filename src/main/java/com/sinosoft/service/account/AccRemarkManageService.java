package com.sinosoft.service.account;

import com.sinosoft.domain.account.AccRemarkManage;
import com.sinosoft.dto.account.AccRemarkManageDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccRemarkManageService {

    public static final String ABSTRACT_ISEXISTE = "ABSTRACT_ISEXIST";
    public static final String ACCREMARKMANAGE_ISEXISTE = "ACCREMARKMANAGE_ISEXISTE" ;

    /**
     * 查询全部摘要信息
     * @param page
     * @param rows
     * @param accRemarkManageDTO
     * @return
     */
    public Page<AccRemarkManageDTO> qryAccRemarkManage(int page, int rows, AccRemarkManageDTO accRemarkManageDTO);

    /**
     * 根据摘要代码、摘要名称查询摘要信息
     * @param RemarkCode
     * @param RemarkName
     * @return
     */
    public List<?> qryByCodeAndName(String RemarkCode, String RemarkName);
    //摘要编码与摘要名称

    /**
     * 查询RemarkCode下拉框内容
     * @return
     */
    public List<?> qryCodeList(String type);

    /**
     * 新增摘要信息
     * @param accRemarkManageDTO
     * @return
     */
    public String saveAccRemarkManage(AccRemarkManageDTO accRemarkManageDTO);

    /**
     * 编辑摘要信息
     * @param id
     * @param accRemarkManageDTO
     * @return
     */
    public String editAccRemarkManage(long id,AccRemarkManageDTO accRemarkManageDTO);

    /**
     * 删除摘要信息
     * @param AccBookType
     * @param AccBookCode
     * @param RemarkCode
     * @return
     */
    public String deleteAccRemarkManage(String AccBookType,String AccBookCode,String RemarkCode);
    public List<?> qrySubjectCodeForCheck(String value);
}
