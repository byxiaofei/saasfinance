package com.sinosoft.repository.fixedassets;

import com.sinosoft.domain.fixedassets.AccAssetInfoChange;
import com.sinosoft.domain.fixedassets.AccAssetInfoChangeId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangst
 * @Description
 * @create
 * 变动表
 * 固定资产卡片变动管理
 */
@Repository
public interface AccAssetInfoChangeRepository extends BaseRepository<AccAssetInfoChange,AccAssetInfoChangeId> {
    @Query(value=" select max(change_code) from AccAssetInfoChange where acc_book_type = ?1 and acc_book_code = ?2 and center_code = ?3", nativeQuery = true)
    String getChangeCode(String accBookType, String accBookCode,String centerCode);
    @Query(value=" select * from accassetinfochange where center_code=?1 and branch_code=?2 and acc_book_type=?3 and acc_book_code=?4 and  change_code=?5", nativeQuery = true)
    List<AccAssetInfoChange> getAccAssetInfoChange(String centerCode, String branchCode, String accBookType, String accBookCode, String changeCode);
   @Modifying
    @Query(value=" update accassetinfo set unit_code=?1 where center_code=?2 and branch_code=?3 and acc_book_type=?4 and acc_book_code=?5  and code_type=?6 and card_code=?7", nativeQuery = true)
   Integer updateUpnitCode(String unitCode,String centerCode, String branchCode, String accBookType, String accBookCode, String codeType,String cardCode);
    @Modifying
    @Query(value="delete from accassetinfochange where center_code=?1 and branch_code=?2 and acc_book_type=?3 and acc_book_code=?4 and  change_code=?5", nativeQuery = true)
    Integer deleteAccAssetInfoChange(String centerCode, String branchCode, String accBookType, String accBookCode, String changeCode);
    @Modifying
    @Query(value=" update accassetinfo set organization=?1 where center_code=?2 and branch_code=?3 and acc_book_type=?4 and acc_book_code=?5  and code_type=?6 and card_code=?7", nativeQuery = true)
    Integer updateorgination(String unitCode,String centerCode, String branchCode, String accBookType, String accBookCode, String codeType,String cardCode);
    @Modifying
    @Query(value=" update accassetinfo set use_flag=?1 where center_code=?2 and branch_code=?3 and acc_book_type=?4 and acc_book_code=?5  and code_type=?6 and card_code=?7", nativeQuery = true)
    Integer updateUseFlag(String useFlag,String centerCode, String branchCode, String accBookType, String accBookCode, String codeType,String cardCode);
  /*  @Modifying
       @Query(value=" update accassetinfo set use_flag=?1 where center_code=?2 and branch_code=?3 and acc_book_type=?4 and acc_book_code=?5  and code_type=?6 and card_code=?7", nativeQuery = true)
    Integer clearCard(String clearFlag,String centerCode, String branchCode, String accBookType, String accBookCode, String codeType,String cardCode);

*/

    @Query(value = "select * from AccAssetInfoChange  where center_code=?1 and acc_book_code=?2 and  change_code = ?3  ",nativeQuery = true)
    List<AccAssetInfoChange> queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndChangeCode(String centerCode,String accBookCode,String changeCode);

    //获取卡片数目
    @Query(value = "select * from AccAssetInfoChange  where center_code= ?1 and acc_book_code= ?2 and  change_code = ?3 group by card_code" ,nativeQuery =  true)
    List<AccAssetInfoChange> queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndChangeCodeGroupByCardCode(String centerCode,String accBookCode,String changeCode);

    //获取变动类型数目
    @Query(value = "select * from AccAssetInfoChange  where center_code= ?1 and acc_book_code= ?2 and  card_code = ?3 and change_code = ?4 group by change_type" ,nativeQuery =  true)
    List<AccAssetInfoChange> queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndCardCodeAndChangeCodeGroupByChangeType(String centerCode,String accBookCode,String cardCode,String changeCode);

    //循环变动类型 从大到小
    @Query(value = "select * from AccAssetInfoChange  where center_code= ?1 and acc_book_code= ?2 and  card_code = ?3 and change_code = ?4 and change_type = ?5 ORDER BY change_code desc",nativeQuery = true)
    List<AccAssetInfoChange> queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndCardCodeAndChangeCodeAndChangeTypeOrderByChangeCode(String centerCode,String accBookCode,String cardCode,String changeCode, String changeType);

    //向数据库中查询看是否是从最大变动单开始查询 或者选中是否连续
    //1.查询最大变动单
    @Query(value = "select * from AccAssetInfoChange  where center_code= ?1 and acc_book_code= ?2 and  card_code = ?3 and change_type = ?4 ORDER BY change_code desc",nativeQuery = true)
    List<AccAssetInfoChange> queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndCardCodeAndChangeTypeOrderByChangeCode(String centerCode,String accBookCode,String cardCode, String changeType);

    //2.判断选中的数据是否连续
    @Query(value = "select * from AccAssetInfoChange  where center_code= ?1 and acc_book_code= ?2 and  card_code = ?3 and change_type = ?4  and change_code >= ?5 and change_code <= ?6 ORDER BY change_code desc",nativeQuery = true)
    List<AccAssetInfoChange> queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndCardCodeAndChangeTypeAndChangeCodeMinMaxOrderByChangeCode(String centerCode,String accBookCode,String cardCode, String changeType,String changeCodeMin,String changeCodeMax);

    //查询选中中数据是否连续
    @Query(value = "select * from AccAssetInfoChange  where center_code= ?1 and acc_book_code= ?2 and  card_code = ?3 and change_type = ?4 and change_code in(?5)  and change_code >= ?6 and change_code <= ?7 ORDER BY change_code desc",nativeQuery = true)
    List<AccAssetInfoChange> queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndCardCodeAndChangeTypeAndChangeCodeMinMaxOrderByChangeCode1(String centerCode,String accBookCode,String cardCode, String changeType,String changeCode,String changeCodeMin,String changeCodeMax);
}
