package com.sinosoft.service.impl.fixedassets;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.fixedassets.AccDepre;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.repository.fixedassets.AccGCheckInfoRepository;
import com.sinosoft.service.fixedassets.FixedassetsReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-19 18:25
 */
@Service
public class FixedassetsReportServiceImpl implements FixedassetsReportService {
    private Logger logger = LoggerFactory.getLogger(FixedassetsReportServiceImpl.class);

    @Value("${voucher.currency}")
    private String currency;
    @Resource
    private AccGCheckInfoRepository accGCheckInfoRepository;
    @Resource
    private AccAssetCodeTypeRepository accAssetCodeTypeRepository;

    /**
     * 固定资产报表查询
     * @param yearMonthDate
     * @param startLevel
     * @param endLevel
     * @return
     */
    @Override
    public List<?> qryReportInfo(String yearMonthDate, String startLevel, String endLevel) {//201906
        List<Object> result = new ArrayList<>();//
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        try {
            //获取所有资产类别
            int invitNo = 1;
            Map<Integer,Object> invits = new HashMap<>();
            StringBuffer asset = new StringBuffer();
            asset.append("select asset_simple_name as assetComplexName,level,end_flag as endFlag,super_code as superCode, asset_type as assetType");
            asset.append(" from AccAssetCodeType where 1=1");

            asset.append(" and acc_book_type =?"+invitNo);
            invits.put(invitNo,CurrentUser.getCurrentLoginAccountType());
            invitNo++;
            asset.append(" and acc_book_code = ?"+invitNo);
            invits.put(invitNo,CurrentUser.getCurrentLoginAccount());
            invitNo++;

            if(startLevel != null && !startLevel.equals("")){
                asset.append(" and level >= ?"+ invitNo );
                invits.put(invitNo,startLevel);
                invitNo++;
            }
            if(endLevel != null && !endLevel.equals("")){
                asset.append(" and level <= ?"+ invitNo );
                invits.put(invitNo,endLevel);
                invitNo++;
            }

            asset.append(" order by asset_type,level");

            List<?> assetList = accAssetCodeTypeRepository.queryBySqlSC(asset.toString(),invits);
            if(assetList.size()>0){
                for(Object obj : assetList){
                    Map map = new HashMap();
                    map.putAll((Map) obj);
                    StringBuffer back = new StringBuffer();
                    if(Integer.parseInt(map.get("level").toString())>1){
                        for(int i=1; i<Integer.parseInt(map.get("level").toString()); i++){
                            back.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                        }
                    }
                    map.put("assetComplexName",back.append(map.get("assetComplexName")+"("+ map.get("assetType") +")"));//资产类别全称+资产类别

                    //计算
                    String yearStart = yearMonthDate.substring(0,4)+"-01-01";//年初
                    String yeaMonthDataStart = yearMonthDate.substring(0,4)+"01";//年初
                    BigDecimal ncAssetOriginValue = BigDecimal.ZERO;//年初数原值
                    BigDecimal ncEndDepreMoney = BigDecimal.ZERO;//年初数累计折旧
                    BigDecimal ncKHS = BigDecimal.ZERO;//年初数可回收市值

                    int paramsNo = 1;
                    Map<Integer,Object> params = new HashMap<>();
                    StringBuffer ncSql = new StringBuffer();
                    ncSql.append("SELECT SUM(asset_origin_value) AS assetOriginValue, SUM(init_depre_money) AS initDepreMoney FROM AccAssetInfo WHERE 1 = 1");

                    ncSql.append(" AND center_code = ?"+paramsNo );
                    params.put(paramsNo,centerCode);
                    paramsNo++;
                    ncSql.append(" AND branch_code = ?"+paramsNo );
                    params.put(paramsNo,branchCode);
                    paramsNo++;
                    ncSql.append(" AND acc_book_type = ?"+paramsNo );
                    params.put(paramsNo,accBookType);
                    paramsNo++;
                    ncSql.append(" AND acc_book_code = ?"+paramsNo );
                    params.put(paramsNo,accBookCode);
                    paramsNo++;
                    ncSql.append(" AND asset_type LIKE ?"+paramsNo );
                    params.put(paramsNo,map.get("assetType") );
                    paramsNo++;
                    ncSql.append(" AND use_start_date < ?"+paramsNo );
                    params.put(paramsNo,yearStart);
                    paramsNo++;
                    ncSql.append(" AND (clear_flag = ?"+paramsNo );
                    params.put(paramsNo,"0");
                    paramsNo++;
                    ncSql.append(" OR (clear_flag = ?"+paramsNo );
                    params.put(paramsNo,"1");
                    paramsNo++;
                    ncSql.append(" AND clear_year_month >= ?"+paramsNo + "))");
                    params.put(paramsNo,yearStart);

                    List<Map<String,BigDecimal>> ncSqlList= (List<Map<String,BigDecimal>>)accAssetCodeTypeRepository.queryBySqlSC(ncSql.toString(),params);
                    // 如果没有或者都为NULL，说明年初之前没有符合条件的资产数据，也就没有相应的折旧数据
                    if (ncSqlList!=null && ncSqlList.size()>0) {
                        Map<String, BigDecimal> data = ncSqlList.get(0);
                        BigDecimal assetOriginValue = data.get("assetOriginValue");
                        BigDecimal initDepreMoney = data.get("initDepreMoney");

                        if (assetOriginValue != null)
                            ncAssetOriginValue = assetOriginValue;
                        if (initDepreMoney != null)
                            ncEndDepreMoney = initDepreMoney;

                        // 再查询有无符合条件的系统内（期初折旧金额为系统之外的折旧）折旧数据
                        if (!(assetOriginValue==null && initDepreMoney==null)) {
                            paramsNo = 1;
                            params = new HashMap<>();
                            ncSql.setLength(0);
                            ncSql.append("SELECT SUM(ad.month_depre_money) as monthDepreMoney FROM AccDepre ad");
                            ncSql.append(" LEFT JOIN AccAssetInfo aai");
                            ncSql.append(" ON aai.center_code = ad.center_code AND aai.branch_code = ad.branch_code AND aai.acc_book_type = ad.acc_book_type AND aai.acc_book_code = ad.acc_book_code AND aai.code_type = ad.code_type AND aai.asset_type = ad.asset_type AND aai.asset_code = ad.asset_code");
                            ncSql.append(" WHERE 1 = 1");

                            ncSql.append(" AND ad.year_month_data <= ?"+paramsNo );
                            params.put(paramsNo,yearMonthDate);
                            paramsNo++;
                            ncSql.append(" AND aai.center_code = ?"+paramsNo );
                            params.put(paramsNo,centerCode);
                            paramsNo++;
                            ncSql.append(" AND aai.branch_code = ?"+paramsNo );
                            params.put(paramsNo,branchCode);
                            paramsNo++;
                            ncSql.append(" AND aai.acc_book_type = ?"+paramsNo );
                            params.put(paramsNo,accBookType);
                            paramsNo++;
                            ncSql.append(" AND aai.acc_book_code = ?"+paramsNo );
                            params.put(paramsNo,accBookCode);
                            paramsNo++;
                            ncSql.append(" AND aai.asset_type LIKE ?"+paramsNo );
                            params.put(paramsNo,map.get("assetType") );
                            paramsNo++;
                            ncSql.append(" AND use_start_date < ?"+paramsNo );
                            params.put(paramsNo,yearStart);
                            paramsNo++;
                            ncSql.append(" AND (clear_flag = ?"+paramsNo );
                            params.put(paramsNo,"0");
                            paramsNo++;
                            ncSql.append(" OR (clear_flag = ?"+paramsNo );
                            params.put(paramsNo,"1");
                            paramsNo++;
                            ncSql.append(" AND clear_year_month >=  ?"+paramsNo + "))");
                            params.put(paramsNo,yearStart);

                            List<Map<String,BigDecimal>> ncSqlList2 = (List<Map<String,BigDecimal>>)accAssetCodeTypeRepository.queryBySqlSC(ncSql.toString(),params);
                            if (ncSqlList2!=null && ncSqlList2.size()>0) {
                                Map<String, BigDecimal> data2 = ncSqlList2.get(0);
                                BigDecimal monthDepreMoney = data2.get("monthDepreMoney");

                                if (monthDepreMoney != null)
                                    ncEndDepreMoney = ncEndDepreMoney.add(monthDepreMoney);
                            }
                        }

                        // 年初数可回收市值=年初数原值-年初数累计折旧
                        ncKHS = ncAssetOriginValue.subtract(ncEndDepreMoney);
                    }

                    /*----------------------*/

                    BigDecimal qmAssetOriginValue = BigDecimal.ZERO;//期末数原值
                    BigDecimal qmEndDepreMoney = BigDecimal.ZERO;//期末数累计折旧
                    BigDecimal qmKHS = BigDecimal.ZERO;//期末数可回收市值

                    SimpleDateFormat df2= new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, Integer.parseInt(yearMonthDate.substring(0,4)));
                    cal.set(Calendar.MONTH, Integer.parseInt(yearMonthDate.substring(4))-1);
                    int lastDay = cal.getActualMaximum(Calendar.DATE);
                    cal.set(Calendar.DAY_OF_MONTH, lastDay);
                    String ymdEnd = df2.format(cal.getTime());// 月末

                    StringBuffer sql = new StringBuffer();
                    sql.append("SELECT SUM(asset_origin_value) AS assetOriginValue, SUM(init_depre_money) AS initDepreMoney FROM AccAssetInfo WHERE 1 = 1");
                    sql.append(" AND center_code = '" + centerCode + "'");
                    sql.append(" AND branch_code = '" + branchCode + "'");
                    sql.append(" AND acc_book_type = '" + accBookType + "'");
                    sql.append(" AND acc_book_code = '" + accBookCode + "'");
                    sql.append(" AND asset_type LIKE '" + map.get("assetType") + "%'");
                    sql.append(" AND use_start_date <= '" + ymdEnd + "' ");
                    sql.append(" AND (clear_flag = '0' OR (clear_flag = '1' AND clear_year_month > '" + ymdEnd + "'))");

                    List<Map<String,BigDecimal>> sqlList= (List<Map<String,BigDecimal>>)accAssetCodeTypeRepository.queryBySqlSC(sql.toString());
                    // 如果没有或者都为NULL，说明没有符合条件的资产数据，也就没有相应的折旧数据
                    if (sqlList!=null && sqlList.size()>0) {
                        Map<String, BigDecimal> data = sqlList.get(0);
                        BigDecimal assetOriginValue = data.get("assetOriginValue");
                        BigDecimal initDepreMoney = data.get("initDepreMoney");

                        if (assetOriginValue != null)
                            qmAssetOriginValue = assetOriginValue;
                        if (initDepreMoney != null)
                            qmEndDepreMoney = initDepreMoney;

                        // 再查询有无符合条件的系统内（期初折旧金额为系统之外的折旧）折旧数据
                        if (!(assetOriginValue==null && initDepreMoney==null)) {
                            sql.setLength(0);
                            sql.append("SELECT SUM(ad.month_depre_money) as monthDepreMoney FROM AccDepre ad");
                            sql.append(" LEFT JOIN AccAssetInfo aai");
                            sql.append(" ON aai.center_code = ad.center_code AND aai.branch_code = ad.branch_code AND aai.acc_book_type = ad.acc_book_type AND aai.acc_book_code = ad.acc_book_code AND aai.code_type = ad.code_type AND aai.asset_type = ad.asset_type AND aai.asset_code = ad.asset_code");
                            sql.append(" WHERE 1 = 1");
                            sql.append(" AND ad.year_month_data <= '" + yearMonthDate + "'");
                            sql.append(" AND aai.center_code = '" + centerCode + "'");
                            sql.append(" AND aai.branch_code = '" + branchCode + "'");
                            sql.append(" AND aai.acc_book_type = '" + accBookType + "'");
                            sql.append(" AND aai.acc_book_code = '" + accBookCode + "'");
                            sql.append(" AND aai.asset_type LIKE '" + map.get("assetType") + "%'");
                            sql.append(" AND aai.use_start_date <= '" + ymdEnd + "' ");
                            sql.append(" AND (aai.clear_flag = '0' OR (aai.clear_flag = '1' AND aai.clear_year_month > '" + ymdEnd + "'))");

                            List<Map<String,BigDecimal>> sqlList2 = (List<Map<String,BigDecimal>>)accAssetCodeTypeRepository.queryBySqlSC(sql.toString());
                            if (sqlList2!=null && sqlList2.size()>0) {
                                Map<String, BigDecimal> data2 = sqlList2.get(0);
                                BigDecimal monthDepreMoney = data2.get("monthDepreMoney");

                                if (monthDepreMoney != null)
                                    qmEndDepreMoney = qmEndDepreMoney.add(monthDepreMoney);
                            }
                        }

                        // 期末数可回收市值=期末数原值-期末数累计折旧
                        qmKHS = qmAssetOriginValue.subtract(qmEndDepreMoney);
                    }


                    BigDecimal monthDepreMoney = BigDecimal.ZERO;//本月计提折旧额

                    sql.setLength(0);
                    sql.append("SELECT SUM(ad.month_depre_money) as monthDepreMoney FROM AccDepre ad");
                    sql.append(" LEFT JOIN AccAssetInfo aai");
                    sql.append(" ON aai.center_code = ad.center_code AND aai.branch_code = ad.branch_code AND aai.acc_book_type = ad.acc_book_type AND aai.acc_book_code = ad.acc_book_code AND aai.code_type = ad.code_type AND aai.asset_type = ad.asset_type AND aai.asset_code = ad.asset_code");
                    sql.append(" WHERE 1 = 1");
                    sql.append(" AND ad.year_month_data = '" + yearMonthDate + "'");
                    sql.append(" AND aai.center_code = '" + centerCode + "'");
                    sql.append(" AND aai.branch_code = '" + branchCode + "'");
                    sql.append(" AND aai.acc_book_type = '" + accBookType + "'");
                    sql.append(" AND aai.acc_book_code = '" + accBookCode + "'");
                    sql.append(" AND aai.asset_type LIKE '" + map.get("assetType") + "%'");
                    sql.append(" AND aai.use_start_date <= '" + ymdEnd + "' ");
                    sql.append(" AND (aai.clear_flag = '0' OR (aai.clear_flag = '1' AND aai.clear_year_month > '" + ymdEnd + "'))");

                    List<Map<String,BigDecimal>> monthList = (List<Map<String,BigDecimal>>)accAssetCodeTypeRepository.queryBySqlSC(sql.toString());
                    if (monthList!=null && monthList.size()>0) {
                        BigDecimal data = monthList.get(0).get("monthDepreMoney");

                        if (data != null)
                            monthDepreMoney = data;
                    }


                    map.put("ncAssetOriginValue",ncAssetOriginValue);//年初数原值
                    map.put("ncEndDepreMoney",ncEndDepreMoney);//年初数累计折旧
                    map.put("ncKHS",ncKHS);//年初数可回收市值
                    map.put("qmAssetOriginValue",qmAssetOriginValue);//期末数原值
                    map.put("qmEndDepreMoney",qmEndDepreMoney);//期末数累计折旧
                    map.put("qmKHS",qmKHS);//期末数可回收市值
                    map.put("monthDepreMoney",monthDepreMoney);//本月计提折旧额
                    map.put("currency",currency);//单位
                    map.put("centerCode",centerCode);//核算单位
                    result.add(map);
                }
            }
            return result;
        }catch (Exception e){
            logger.error("固定资产报表查询异常",e);
            return null;
        }
    }


    /**
     * 获取会计期间
     * @return
     */
    @Override
    public List<?> getYearMonthDate() {
        List<?> yearMonthDateList = accGCheckInfoRepository.getYearMonthDate(CurrentUser.getCurrentLoginAccount(),CurrentUser.getCurrentLoginManageBranch());
        if(yearMonthDateList.size()>0){
            return yearMonthDateList;
        }else{
            return null;
        }
    }

    /**
     * 获取起始层级
     * @return
     */
    @Override
    public List<?> getStartLevel() {
        List<?> startLevelList = accGCheckInfoRepository.getStartLevel(CurrentUser.getCurrentLoginAccount());
        if(startLevelList.size()>0){
            return startLevelList;
        }else{
            return null;
        }
    }

    /**
     * 获取终止层级
     * @return
     */
    @Override
    public List<?> geteEndLevel() {
        List<?> endLevelList = accGCheckInfoRepository.geteEndLevel(CurrentUser.getCurrentLoginAccount());
        if(endLevelList.size()>0){
            return endLevelList;
        }else{
            return null;
        }
    }
}
