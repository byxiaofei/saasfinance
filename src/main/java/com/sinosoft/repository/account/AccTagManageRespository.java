package com.sinosoft.repository.account;

import com.sinosoft.domain.AccTagManage;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/2/19 18:48
 * @Description:
 */
@Repository
public interface AccTagManageRespository extends BaseRepository<AccTagManage, Long> {

    List<?> findByCenterCodeAndAccBookCodeAndAccBookTypeAndTagCode(String centerCode,String accBookCode, String accBookType, String tagCode);
    List<?> findByCenterCodeAndAccBookCodeAndAccBookTypeAndUpperTag(String centerCode,String accBookCode, String accBookType, String upperTag);

    @Query(value = "select * from acctagmanage a where 1=1 and a.center_code = ?1 and a.acc_book_type = ?2 and a.acc_book_code = ?3 and a.tag_name like %?4% and a.end_flag = '0' and a.flag = '1'",nativeQuery = true)
    List<AccTagManage> findByCenterCodeAndAccBookTypeAndAccBookCodeAndLikeTagName(String centerCode, String accBookType, String accBookCode, String tagName);
}
