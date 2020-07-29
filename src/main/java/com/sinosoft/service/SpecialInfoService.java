package com.sinosoft.service;

import com.sinosoft.domain.SpecialInfo;
import com.sinosoft.dto.SpecialInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface SpecialInfoService {
    public static final String SPECIAL_ISEXISTE = "SPECIAL_ISEXIST";
    public static final String ACCSUB_ISEXISTE="ACCSUB_ISEXISTE";

    /**
     * 查询全部的专项信息
     * @param page
     * @param rows
     * @param specialInfo
     */
    /*public Page<SpecialInfo> qrySpecialMessage(int page,int rows,SpecialInfo specialInfo ) ;*/

    /**
     * 按双击展示树状下拉框查，或手动输入询专项信息
     * @param page
     * @param rows
     * @param specialInfo
     * @param flag
     * @return
     */
    public Page<?> qrySpecial(int page,int rows,SpecialInfo specialInfo ,int flag) ;

    /**
     * 查询所有父级转向的id 和 对应的SpecialCode
     * @return
     */
    public List<?> qrySuperSpecialList();
    /**
     * 通过Id查询
     * @param id
     * @return
     */
    public SpecialInfo searchSpecial(long id) ;

    /**
     * 获取树状下拉菜单
     * @return
     */
    public List<?>qrySpecialCode(String value);

    /**
     * 保存新建的子项专项信息
     * @param dto
     * @param id
     * @return
     */
    public String saveSpecial(SpecialInfoDTO dto,long id);

    /**
     * 保存新建的一级专项信息
     * @param dto
     * @return
     */
    public String saveSpecial(SpecialInfoDTO dto);

    /**
     * 通过id查询查询子专项的个数
     * @return
     */
    public int countChildNum(long id);
    /**
     * 保存编辑的专项信息
     * @param dto
     * @param id
     * @return
     */
    public String editSpecial(SpecialInfoDTO dto,long id);

    /**
     * 删除专项信息
     * @param id
     */
    public String deleteSpecial(long id);

    /**
     * 将符合条件的数据导出至Excel中
     * @param request
     * @param response
     * @param name 导出文件名称
     * @param queryConditions 导出数据限制条件
     * @param cols 导出列
     */
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                                  String queryConditions, String cols);
}
