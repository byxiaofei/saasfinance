package com.sinosoft.service.account;

import com.sinosoft.domain.AccTagManage;
import com.sinosoft.dto.account.AccTagManageDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/2/19 17:47
 * @Description: 标注管理业务处理类
 */
public interface AccTagManageService {

    /**
     * 获取标注列表
     * @param page 起始页
     * @param rows 记录数
     * @param accTagManageDTO 查询条件
     * @return
     */
    Page<?> qryAccTagManage(int page, int rows, AccTagManageDTO accTagManageDTO);

    /**
     * 创建标注
     * @param accTagManageDTO
     * @return
     */
    String save(AccTagManageDTO accTagManageDTO);

    /**
     * 删除标注
     * @param id
     * @return
     */
    String delTag(long id);

    /**
     * 更新标注
     * @param accTagManageDTO
     * @return
     */
    String update(AccTagManageDTO accTagManageDTO);

    public List<?> qryTagCode(String value);
}
