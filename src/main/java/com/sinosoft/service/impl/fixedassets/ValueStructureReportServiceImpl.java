package com.sinosoft.service.impl.fixedassets;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.repository.fixedassets.AccGCheckInfoRepository;
import com.sinosoft.service.fixedassets.ValueStructureReportService;
import org.hibernate.type.BigDecimalType;
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
 * @create 2019-04-22 20:41
 */
@Service
public class ValueStructureReportServiceImpl implements ValueStructureReportService {
    private Logger logger = LoggerFactory.getLogger(ValueStructureReportServiceImpl.class);

    @Value("${voucher.currency}")
    private String currency;
    @Resource
    private AccGCheckInfoRepository accGCheckInfoRepository;
    @Resource
    private AccAssetCodeTypeRepository accAssetCodeTypeRepository;

    /**
     * 固定资产价值结构分析表查询
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

                    BigDecimal qmAssetOriginValue = BigDecimal.ZERO;//期末数原值
                    BigDecimal qmEndDepreMoney = BigDecimal.ZERO;//期末数累计折旧
                    BigDecimal qmKHS = BigDecimal.ZERO;//期末净值
                    BigDecimal count = BigDecimal.ZERO;//数量
                    BigDecimal zb = BigDecimal.ZERO;//累计折旧占原值百分比
                    BigDecimal jz = BigDecimal.ZERO;//净值率

                    SimpleDateFormat df2= new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, Integer.parseInt(yearMonthDate.substring(0,4)));
                    cal.set(Calendar.MONTH, Integer.parseInt(yearMonthDate.substring(4))-1);
                    int lastDay = cal.getActualMaximum(Calendar.DATE);
                    cal.set(Calendar.DAY_OF_MONTH, lastDay);
                    String ymdEnd = df2.format(cal.getTime());// 月末

                    int paramsNo = 1;
                    Map<Integer,Object> params = new HashMap<>();
                    StringBuffer sql = new StringBuffer();
                    sql.append("SELECT SUM(CAST(quantity AS DECIMAL(5,2))) AS quantity, SUM(asset_origin_value) AS assetOriginValue, SUM(end_depre_money) AS initDepreMoney FROM AccAssetInfo WHERE 1 = 1");

                    sql.append(" AND center_code = ?"+paramsNo );
                    params.put(paramsNo,centerCode);
                    paramsNo++;
                    sql.append(" AND branch_code = ?"+paramsNo );
                    params.put(paramsNo,branchCode);
                    paramsNo++;
                    sql.append(" AND acc_book_type = ?"+paramsNo );
                    params.put(paramsNo,accBookType);
                    paramsNo++;
                    sql.append(" AND acc_book_code = ?"+paramsNo );
                    params.put(paramsNo,accBookCode);
                    paramsNo++;
                    sql.append(" AND asset_type LIKE ?"+paramsNo );
                    params.put(paramsNo,map.get("assetType") );
                    paramsNo++;
                    sql.append(" AND use_start_date <= ?"+paramsNo );
                    params.put(paramsNo,ymdEnd);
                    paramsNo++;
                    sql.append(" AND (clear_flag = ?"+paramsNo );
                    params.put(paramsNo,"0");
                    paramsNo++;
                    sql.append(" OR (clear_flag = ?"+paramsNo );
                    params.put(paramsNo,"1");
                    paramsNo++;
                    sql.append(" AND clear_year_month >  ?"+paramsNo + "))");
                    params.put(paramsNo,ymdEnd);
                    List<Map<String,BigDecimal>> sqlList= (List<Map<String,BigDecimal>>)accAssetCodeTypeRepository.queryBySqlSC(sql.toString(),params);
                    // 如果没有或者都为NULL，说明没有符合条件的资产数据，也就没有相应的折旧数据
                    if (sqlList!=null && sqlList.size()>0) {
                        Map<String, BigDecimal> data = sqlList.get(0);
                        BigDecimal quantity = data.get("quantity");
                        BigDecimal assetOriginValue = data.get("assetOriginValue");
                        BigDecimal initDepreMoney = data.get("initDepreMoney");

                        if (quantity != null)
                            count = quantity;
                        if (assetOriginValue != null)
                            qmAssetOriginValue = assetOriginValue;
                        if (initDepreMoney != null)
                            qmEndDepreMoney = initDepreMoney;

                        // 再查询有无符合条件的系统内（期初折旧金额为系统之外的折旧）折旧数据
                        if (!(assetOriginValue==null && initDepreMoney==null)) {
                            paramsNo = 1;
                            params = new HashMap<>();
                            sql.setLength(0);
                            sql.append("SELECT SUM(ad.month_depre_money) as monthDepreMoney FROM AccDepre ad");
                            sql.append(" LEFT JOIN AccAssetInfo aai");
                            sql.append(" ON aai.center_code = ad.center_code AND aai.branch_code = ad.branch_code AND aai.acc_book_type = ad.acc_book_type AND aai.acc_book_code = ad.acc_book_code AND aai.code_type = ad.code_type AND aai.asset_type = ad.asset_type AND aai.asset_code = ad.asset_code");
                            sql.append(" WHERE 1 = 1");

                            sql.append(" AND ad.year_month_data <= ?"+paramsNo );
                            params.put(paramsNo,yearMonthDate);
                            paramsNo++;
                            sql.append(" AND aai.center_code = ?"+paramsNo );
                            params.put(paramsNo,centerCode);
                            paramsNo++;
                            sql.append(" AND aai.branch_code = ?"+paramsNo );
                            params.put(paramsNo,branchCode);
                            paramsNo++;
                            sql.append(" AND aai.acc_book_type = ?"+paramsNo );
                            params.put(paramsNo,accBookType);
                            paramsNo++;
                            sql.append(" AND aai.acc_book_code = ?"+paramsNo );
                            params.put(paramsNo,accBookCode);
                            paramsNo++;
                            sql.append(" AND aai.asset_type LIKE ?"+paramsNo );
                            params.put(paramsNo,map.get("assetType") );
                            paramsNo++;
                            sql.append(" AND use_start_date <= ?"+paramsNo );
                            params.put(paramsNo,ymdEnd);
                            paramsNo++;
                            sql.append(" AND (clear_flag = ?"+paramsNo );
                            params.put(paramsNo,"0");
                            paramsNo++;
                            sql.append(" OR (clear_flag = ?"+paramsNo );
                            params.put(paramsNo,"1");
                            paramsNo++;
                            sql.append(" AND clear_year_month >  ?"+paramsNo + "))");
                            params.put(paramsNo,ymdEnd);

                            List<Map<String,BigDecimal>> sqlList2 = (List<Map<String,BigDecimal>>)accAssetCodeTypeRepository.queryBySqlSC(sql.toString(),params);
                            if (sqlList2!=null && sqlList2.size()>0) {
                                Map<String, BigDecimal> data2 = sqlList2.get(0);
                                BigDecimal monthDepreMoney = data2.get("monthDepreMoney");

                                if (monthDepreMoney != null)
                                    qmEndDepreMoney = qmEndDepreMoney.add(monthDepreMoney);
                            }
                        }

                        // 期末净值=期末原值-期末累计折旧
                        qmKHS = qmAssetOriginValue.subtract(qmEndDepreMoney);

                        if (qmAssetOriginValue.compareTo(BigDecimal.ZERO) != 0) {
                            // 累计折旧占原值百分比%=(期末累计折旧/期末原值)*100
                            zb = qmEndDepreMoney.divide(qmAssetOriginValue, 5, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                            // 净值率%=(期末净值/期末原值)*100
                            jz = qmKHS.divide(qmAssetOriginValue, 5, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        }
                    }

                    map.put("count",count);//数量
                    map.put("qmAssetOriginValue",qmAssetOriginValue);//期末数原值
                    map.put("qmEndDepreMoney",qmEndDepreMoney);//期末数累计折旧
                    map.put("qmKHS",qmKHS);//期末净值
                    map.put("zb",zb);//累计折旧占原值百分比%
                    map.put("jz",jz);//净值率%=(期末净值/期末原值)*100
                    map.put("currency",currency);//单位
                    map.put("centerCode",centerCode);//核算单位
                    result.add(map);
                }
            }
            return result;
        }catch (Exception e){
            logger.error("固定资产价值结构分析表查询异常",e);
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
