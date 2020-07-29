package com.sinosoft.repository.fixedassets;

import com.sinosoft.domain.fixedassets.AccDepre;
import com.sinosoft.domain.fixedassets.AccDepreId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-10 10:54
 */
@Repository
public interface AccDepreRepository extends BaseRepository<AccDepre, AccDepreId> {

    /**
     * 根据折旧年月删除折旧记录表中数据
     * @param yearMonthData
     */
    @Modifying
    @Query(value = "delete from AccDepre where year_month_data = ?1 and center_code = ?2 and acc_book_code=?3 ", nativeQuery = true)
    void delByYearMonthData(String yearMonthData,String centerCode,String accBookCode);

    /**
     * 根据凭证号 清空固定资产折旧记录表中的凭证号
     * @param voucherNo
     */
    @Modifying
    @Query(value = "update AccDepre set voucher_no = '' where voucher_no= ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code=?4", nativeQuery = true)
    void clearVoucherNo(String voucherNo ,String accBookType, String accBookCode,String centerCode);

    //判断变动后当前卡片是否进行过计提
    @Query(value = "select * from accdepre a where a.center_code= ?1 and a.acc_book_type = ?2 and a.acc_book_code = ?3 and  a.code_type = ?4  and a.asset_code = ?5 and a.handle_date > ?6  ",nativeQuery = true)
    List<AccDepre> queryAccDepreInfo(String centerCode ,String accBookType,String accBookCode ,String codeType ,String assetCode , String handleDate);

    //获取上个月的折旧信息
    @Query(value = "select sum(current_origin_value) as upcurrentoriginvalue,sum(month_depre_money) as upmonthdepremoney  from accdepre  where center_code= ?1 and  acc_book_code= ?2 and  asset_type like ?3%  and year_month_data=REPLACE(left(DATE_SUB(CONCAT(left(?4 ,4),'-',right(?5,2),'-01'),INTERVAL 1 MONTH),7),'-','')",nativeQuery = true)
    List<Map<String,BigDecimal>> queryAccDepreByYearMonthData(String centerCode,String accBookCode,String assetType,String yearMonthData,String yearMonthData1);

    //计算折旧记录表种的累计折旧金额 //当月折旧金额+折旧记录表中上月累计已折旧金额
    @Query(value = "select * from accdepre where center_code= ?1 and acc_book_code= ?2 and asset_type= ?3 and asset_code= ?4 order by year_month_data desc",nativeQuery = true)
    List<AccDepre> queryAccDepreByCenterCodeAndAccBookCodeAndAssetTypeAndAssetCodeOrderByYearMonthData(String centerCode,String accBookCode,String assetType ,String assetCode );

    @Query(value = "select * from accdepre where center_code= ?1 and acc_book_code= ?2  and asset_code= ?3 order by year_month_data desc",nativeQuery = true)
    List<AccDepre> queryAccDepreByCenterCodeAndAccBookCodeAndAssetCodeOrderByYearMonthData(String centerCode,String accBookCode ,String assetCode );

    //获取折旧表所有信息
    @Query(value = "select * from  accdepre where center_code= ?1 and  acc_book_code= ?2 and year_month_data= ?3 ",nativeQuery = true)
    List<AccDepre> queryAccDepreByChooseMessage(String centerCode,String accBookCode,String yearMonthData);

    //先从固定资产折旧记录表中获取所有信息
    @Query(value = "select * from  accdepre where center_code= ?1 and  acc_book_code= ?2 and year_month_data= ?3 and (voucher_no is null or voucher_no='')",nativeQuery = true)
    List<AccDepre> queryAccDepreByChooseMessage1(String centerCode,String accBookCode,String yearMonthData);

    @Query(value = "select * from accdepre where center_code= ?1 and  year_month_data = ?2 and acc_book_type = ?3 and acc_book_code = ?4 ",nativeQuery = true)
    List<AccDepre> queryAccDepreByCenterCodeAndYearMonthDataAndAccBookTypeAndAccBookCode(String centerCode,String yearMonthData,String accBookType,String accBookCode);
}
