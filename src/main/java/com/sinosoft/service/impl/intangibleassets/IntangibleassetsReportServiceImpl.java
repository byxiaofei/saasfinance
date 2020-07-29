package com.sinosoft.service.impl.intangibleassets;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.repository.fixedassets.AccGCheckInfoRepository;
import com.sinosoft.repository.intangibleassets.AccWCheckInfoRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetCodeTypeRepository;
import com.sinosoft.service.impl.fixedassets.FixedassetsReportServiceImpl;
import com.sinosoft.service.intangibleassets.IntangibleassetsReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-22 19:08
 */
@Service
public class IntangibleassetsReportServiceImpl implements IntangibleassetsReportService {
    private Logger logger = LoggerFactory.getLogger(IntangibleassetsReportServiceImpl.class);

    @Value("${voucher.currency}")
    private String currency;
    @Resource
    private AccWCheckInfoRepository accWCheckInfoRepository;
    @Resource
    private IntangibleAccAssetCodeTypeRepository intangibleAccAssetCodeTypeRepository;
    /**
     * 无形资产报表查询
     * @param yearMonthDate
     * @return
     */
    @Override
    public List<?> qryReportInfo(String yearMonthDate, String startLevel, String endLevel) {//201906
        String[] strNo = new String[]{"一","二","三","四","五","六","七","八","九","十"};//序号
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        List<Object> result = new ArrayList<>();//
        int count = 0;//用来计算循环次数(序号获取)
        List<Object> xmhjList = new ArrayList<>();//用于存放项目合计
        try {
            //获取第一层级内容
            int invitsNo = 1;
            Map<Integer,Object> invits = new HashMap<>();
            StringBuffer level1Sql = new StringBuffer();
            level1Sql.append("select asset_type as assetType, asset_simple_name as assetSimpleName, " +
                    "level as level, end_flag as endFlag, super_code as superCode " +
                    "from intangibleaccassetcodetype where 1=1 and level = '1' and end_flag='1' " +
                    "and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"'");

            level1Sql.append(" and acc_book_type = ?"+invitsNo);
            invits.put(invitsNo,CurrentUser.getCurrentLoginAccountType());
            invitsNo++;
            level1Sql.append(" and acc_book_code = ?"+invitsNo);
            invits.put(invitsNo,CurrentUser.getCurrentLoginAccount());
            invitsNo++;
            List<?> level1List = intangibleAccAssetCodeTypeRepository.queryBySqlSC(level1Sql.toString(),invits);
            if(level1List.size()>0){
                for(Object level1Object : level1List){
                    Map level1Map = new HashMap();
                    level1Map.putAll((Map) level1Object);
                    List<Object> xmxjList = new ArrayList<>();//用于存放项目小计
                    String xmxjName = null;//项目小计名称

                    int paramsNo = 1;
                    Map<Integer,Object> params = new HashMap<>();
                    //获取第一层级下的第二层级内容
                    StringBuffer level2Sql = new StringBuffer();
                    level2Sql.append("select asset_type as assetType, asset_simple_name as assetSimpleName, " +
                            "level as level, end_flag as endFlag, super_code as superCode " +
                            "from intangibleaccassetcodetype where 1=1 and level = '2' and end_flag = '1'" );
//                            " and super_code = '"+ level1Map.get("assetType") +"' " +
//                            "and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"'");

                    level2Sql.append(" and super_code = ?"+paramsNo);
                    params.put(paramsNo,level1Map.get("assetType"));
                    paramsNo++;
                    level2Sql.append(" and acc_book_type = ?"+paramsNo);
                    params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
                    paramsNo++;
                    level2Sql.append(" and acc_book_code = ?"+paramsNo);
                    params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                    paramsNo++;
                    List<?> level2List = intangibleAccAssetCodeTypeRepository.queryBySqlSC(level2Sql.toString(),params);
                    if(level2List.size()>0){
                        for(Object level2Object : level2List){
                            Map level2Map = new HashMap();
                            level2Map.putAll((Map) level2Object);
                            String hjName = "合计";//合计名称
                            String hjAssetOriginValue = "0";//原值  合计
                            String hjMonthDepreMoney = "0";//月摊销金额  合计
                            String hjEndDepreAmount = "0";//累计摊销月份 合计
                            String hjEndDepreMoney = "0";//累计摊销金额  合计
                            String hjAssetNetValue = "0";//无形资产净值  合计
                            String flag = "false";//判断是否有合计项
                            xmxjName = level1Map.get("assetSimpleName").toString();//项目小计的大类名称 eg:信息化建设类

                            int number = 1;
                            Map<Integer,Object> maps = new HashMap<>();
                            //获取第二层级下的第三层级内容
                            StringBuffer level3Sql = new StringBuffer();
                            level3Sql.append("select asset_type as assetType, asset_simple_name as assetSimpleName, " +
                                    "level as level, end_flag as endFlag, super_code as superCode " +
                                    "from intangibleaccassetcodetype where 1=1 and level = '3' and end_flag = '0' " );
//                                    "and super_code = '"+ level2Map.get("assetType") +"' " +
//                                    "and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"'");
                            level3Sql.append(" and super_code = ?"+number);
                            maps.put(number,level2Map.get("assetType") );
                            number++;
                            level3Sql.append(" and acc_book_type = ?"+number);
                            maps.put(number,CurrentUser.getCurrentLoginAccountType());
                            number++;
                            level3Sql.append(" and acc_book_code = ?"+number);
                            maps.put(number,CurrentUser.getCurrentLoginAccount());
                            number++;
                            List<?> level3List = intangibleAccAssetCodeTypeRepository.queryBySqlSC(level3Sql.toString(),maps);
                            if(level3List.size()>0){
                                for(Object level3Object : level3List){
                                    Map level3Map = new HashMap();
                                    level3Map.putAll((Map) level3Object);
                                    String xjName = null;//小计名称
                                    String xjAssetOriginValue = "0";//原值  小计
                                    String xjMonthDepreMoney = "0";//月摊销金额  小计
                                    String xjEndDepreAmount = "0";//累计摊销月份 小计
                                    String xjEndDepreMoney = "0";//累计摊销金额  小计
                                    String xjAssetNetValue = "0";//无形资产净值  小计
                                    xjName = level3Map.get("assetSimpleName") + "小计";

                                    int paramsNo1 = 1;
                                    Map<Integer,Object> params1 = new HashMap<>();
                                    //无形资产基本信息获取
                                    StringBuffer iaSql = new StringBuffer();
                                    iaSql.append("select * from intangibleaccassetinfo where 1=1 ");
//                                            "and center_code = '"+CurrentUser.getCurrentLoginManageBranch()+"' and code_type = '31' and asset_type = '"+ level3Map.get("assetType") +"' " +
//                                            "and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"'");
                                    iaSql.append(" and center_code = ?"+paramsNo1);
                                    params1.put(paramsNo1,CurrentUser.getCurrentLoginManageBranch());
                                    paramsNo1++;
                                    iaSql.append("  and code_type = '31' and asset_type = ?"+paramsNo1);
                                    params1.put(paramsNo1,level3Map.get("assetType") );
                                    paramsNo1++;
                                    iaSql.append(" and acc_book_type = ?"+paramsNo1);
                                    params1.put(paramsNo1,CurrentUser.getCurrentLoginAccountType());
                                    paramsNo1++;
                                    iaSql.append(" and acc_book_code = ?"+paramsNo1);
                                    params1.put(paramsNo1,CurrentUser.getCurrentLoginAccount());
                                    paramsNo1++;
                                    List<?> iaList = intangibleAccAssetCodeTypeRepository.queryBySqlSC(iaSql.toString(),params1);
                                    if(iaList.size()>0){
                                        flag = "true";//有合计项
                                        for(Object iaObject : iaList){
                                            Map iaMap = new HashMap();
                                            iaMap.putAll((Map) iaObject);
                                            int seatsNo = 1;
                                            Map<Integer,Object> seats = new HashMap<>();
                                            //获取无形资产当前会计期间月摊销金额
                                            StringBuffer ytSql = new StringBuffer();
                                            ytSql.append("select * from IntangibleAccDepre where 1=1 " );
//                                                    "and center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and code_type = '31' and year_month_data = '"+ yearMonthDate +"' " +
//                                                    "and asset_type = '"+ iaMap.get("asset_type") +"' and asset_code = '"+ iaMap.get("asset_code") +"' " +
//                                                    "and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"'");

                                            ytSql.append(" and center_code=?"+seatsNo);
                                            seats.put(seatsNo,CurrentUser.getCurrentLoginManageBranch());
                                            seatsNo++;
                                            ytSql.append(" and code_type = '31' and year_month_data = ?"+seatsNo);
                                            seats.put(seatsNo,yearMonthDate);
                                            seatsNo++;
                                            ytSql.append(" and asset_type = ?"+seatsNo);
                                            seats.put(seatsNo,iaMap.get("asset_type"));
                                            seatsNo++;
                                            ytSql.append(" and asset_code = ?"+seatsNo);
                                            seats.put(seatsNo,iaMap.get("asset_code"));
                                            seatsNo++;
                                            ytSql.append(" and acc_book_type = ?"+seatsNo);
                                            seats.put(seatsNo,CurrentUser.getCurrentLoginAccountType());
                                            seatsNo++;
                                            ytSql.append(" and acc_book_code = ?"+seatsNo);
                                            seats.put(seatsNo,CurrentUser.getCurrentLoginAccount());
                                            seatsNo++;

                                            List<?> ytList = intangibleAccAssetCodeTypeRepository.queryBySqlSC(ytSql.toString(),seats);
                                            Map ytMap = new HashMap();
                                            for(Object ytObject : ytList){
                                                ytMap.putAll((Map) ytObject);//通过条件搜索后，该数据只有一条，所以循环只执行一次
                                            }

                                            Map map = new HashMap();
                                            map.put("centerCode",centerCode);//核算单位
                                            map.put("strNo",strNo[count]);//序号
                                            map.put("level1",level1Map.get("assetSimpleName"));//层级1名称
                                            map.put("level2",level2Map.get("assetSimpleName"));//层级2名称
                                            map.put("level3",level3Map.get("assetSimpleName"));//层级3名称
                                            map.put("nr", iaMap.get("asset_name"));//内容
                                            map.put("useStartDate",iaMap.get("use_start_date"));//开始使用日期
                                            map.put("depYears",iaMap.get("dep_years")==null || iaMap.get("dep_years").equals("")? 0 : iaMap.get("dep_years"));//摊销年限(月)
                                            map.put("assetOriginValue", iaMap.get("asset_origin_value")==null || iaMap.get("asset_origin_value").equals("")? "0.00" : new BigDecimal(iaMap.get("asset_origin_value").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//原值
                                            String endDepreMoney="";
                                            if(ytList.size()>0){
                                                map.put("monthDepreMoney", ytMap.get("month_depre_money")==null || ytMap.get("month_depre_money").equals("")? "0.00" : new BigDecimal(ytMap.get("month_depre_money").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//月摊销金额
                                                map.put("endDepreAmount", ytMap.get("all_depre_quantity")==null || ytMap.get("all_depre_quantity").equals("")? "0" : new BigDecimal(ytMap.get("all_depre_quantity").toString()).toString());//累计摊销月份
                                                String allDepreMoney=String.valueOf(ytMap.get("all_depre_money"));
                                                String initDepreMoney=String.valueOf(iaMap.get("init_depre_money"));
                                                if(allDepreMoney==null||allDepreMoney.equals("")||allDepreMoney.equals("null")){
                                                    allDepreMoney="0.00";
                                                }
                                                if(initDepreMoney==null||initDepreMoney.equals("")||initDepreMoney.equals("null")){
                                                    initDepreMoney="0.00";
                                                }
                                                 endDepreMoney=String.valueOf(new BigDecimal(allDepreMoney).add(new BigDecimal(initDepreMoney)));

                                                map.put("endDepreMoney", endDepreMoney== null || endDepreMoney.equals("")? "0.00" : new BigDecimal(endDepreMoney).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//累计摊销金额
                                                map.put("assetNetValue", ytMap.get("current_net_value")==null || ytMap.get("current_net_value").equals("")? "0.00" : new BigDecimal(ytMap.get("current_net_value").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//无形资产净值

                                            }else{
                                                map.put("monthDepreMoney", ytMap.get("month_depre_money")==null || ytMap.get("month_depre_money").equals("")? "0.00" : new BigDecimal(ytMap.get("month_depre_money").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//月摊销金额
                                                map.put("endDepreAmount", iaMap.get("end_depre_amount")==null || iaMap.get("end_depre_amount").equals("")? "0" : new BigDecimal(iaMap.get("end_depre_amount").toString()).toString());//累计摊销月份
                                                map.put("endDepreMoney", iaMap.get("end_depre_money")== null || iaMap.get("end_depre_money").equals("")? "0.00" : new BigDecimal(iaMap.get("end_depre_money").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//累计摊销金额
                                                map.put("assetNetValue", iaMap.get("asset_net_value")==null || iaMap.get("asset_net_value").equals("")? "0.00" : new BigDecimal(iaMap.get("asset_net_value").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//无形资产净值

                                            }
                                            map.put("temp", iaMap.get("temp"));//备注
                                            result.add(map);

                                            //小计计算
                                            xjAssetOriginValue = new BigDecimal(xjAssetOriginValue).add(new BigDecimal(iaMap.get("asset_origin_value")==null || iaMap.get("asset_origin_value").equals("")? "0.00" : iaMap.get("asset_origin_value").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString();//原值  小计
                                            xjMonthDepreMoney = new BigDecimal(xjMonthDepreMoney).add(new BigDecimal(ytMap.get("month_depre_money")== null || ytMap.get("month_depre_money").equals("")? "0.00" : ytMap.get("month_depre_money").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString();//月摊销金额  小计
                                            xjEndDepreAmount = new BigDecimal(xjEndDepreAmount).add(new BigDecimal(ytMap.get("all_depre_quantity")==null || ytMap.get("all_depre_quantity").equals("")? "0" : ytMap.get("all_depre_quantity").toString())).toString();//累计摊销月份 小计
                                            xjEndDepreMoney = new BigDecimal(xjEndDepreMoney).add(new BigDecimal(endDepreMoney==null || endDepreMoney.equals("")? "0.00" : endDepreMoney.toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString();//累计摊销金额  小计
                                            xjAssetNetValue = new BigDecimal(xjAssetNetValue).add(new BigDecimal(ytMap.get("current_net_value")==null || ytMap.get("current_net_value").equals("")? "0.00" : ytMap.get("current_net_value").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString();//无形资产净值  小计
                                        }
                                        //添加小计
                                        Map xjMap = new HashMap();
                                        xjMap.put("centerCode",centerCode);//核算单位
                                        xjMap.put("strNo",strNo[count]);//序号
                                        xjMap.put("level1","");//层级1名称
                                        xjMap.put("level2",level2Map.get("assetSimpleName"));//层级2名称
                                        xjMap.put("level3",xjName);//层级3名称
                                        xjMap.put("nr", "");//内容
                                        xjMap.put("useStartDate","");//开始使用日期
                                        xjMap.put("depYears","");//摊销年限(月)
                                        xjMap.put("assetOriginValue", xjAssetOriginValue);//原值
                                        xjMap.put("monthDepreMoney", xjMonthDepreMoney);//月摊销金额
                                        xjMap.put("endDepreAmount", xjEndDepreAmount);//累计摊销月份
                                        xjMap.put("endDepreMoney", xjEndDepreMoney);//累计摊销金额
                                        xjMap.put("assetNetValue", xjAssetNetValue);//无形资产净值
                                        xjMap.put("temp", "");//备注
                                        result.add(xjMap);
                                        Map xmxjMap = new HashMap();//作为中间map，为的是后面修改操作不会影响到原值
                                        xmxjMap.putAll(xjMap);
                                        xmxjList.add(xmxjMap);//将小计信息存入小计集合中

                                        //合计计算
                                        hjAssetOriginValue = new BigDecimal(hjAssetOriginValue).add(new BigDecimal(xjAssetOriginValue)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();//原值  小计
                                        hjMonthDepreMoney = new BigDecimal(hjMonthDepreMoney).add(new BigDecimal(xjMonthDepreMoney)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();//月摊销金额  小计
                                        hjEndDepreAmount = new BigDecimal(hjEndDepreAmount).add(new BigDecimal(xjEndDepreAmount)).toString();//累计摊销月份 小计
                                        hjEndDepreMoney = new BigDecimal(hjEndDepreMoney).add(new BigDecimal(xjEndDepreMoney)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();//累计摊销金额  小计
                                        hjAssetNetValue = new BigDecimal(hjAssetNetValue).add(new BigDecimal(xjAssetNetValue)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();//无形资产净值  小计
                                    }
                                }
                                if(flag.equals("true")){//有合计项
                                    //添加合计
                                    Map hjMap = new HashMap();
                                    hjMap.put("centerCode",centerCode);//核算单位
                                    hjMap.put("strNo",strNo[count]);//序号
                                    hjMap.put("level1","");//层级1名称
                                    hjMap.put("level2","");//层级2名称
                                    hjMap.put("level3","合计");//层级3名称
                                    hjMap.put("nr", "");//内容
                                    hjMap.put("useStartDate","");//开始使用日期
                                    hjMap.put("depYears","");//摊销年限(月)
                                    hjMap.put("assetOriginValue", hjAssetOriginValue);//原值
                                    hjMap.put("monthDepreMoney", hjMonthDepreMoney);//月摊销金额
                                    hjMap.put("endDepreAmount", hjEndDepreAmount);//累计摊销月份
                                    hjMap.put("endDepreMoney", hjEndDepreMoney);//累计摊销金额
                                    hjMap.put("assetNetValue", hjAssetNetValue);//无形资产净值
                                    hjMap.put("temp", "");//备注
                                    result.add(hjMap);
                                }
                            }
                        }
                        for(int i=0; i<xmxjList.size()-1; i++){//将相同小计合并
                            for(int j=1; j<xmxjList.size(); j++){
                                if(((Map) xmxjList.get(i)).get("level3").equals(((Map) xmxjList.get(j)).get("level3"))){
                                    ((Map) xmxjList.get(i)).put("assetOriginValue", new BigDecimal(((Map) xmxjList.get(i)).get("assetOriginValue").toString()).add(new BigDecimal(((Map) xmxjList.get(j)).get("assetOriginValue").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//原值
                                    ((Map) xmxjList.get(i)).put("monthDepreMoney", new BigDecimal(((Map) xmxjList.get(i)).get("monthDepreMoney").toString()).add(new BigDecimal(((Map) xmxjList.get(j)).get("monthDepreMoney").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//月摊销金额
                                    ((Map) xmxjList.get(i)).put("endDepreAmount", new BigDecimal(((Map) xmxjList.get(i)).get("endDepreAmount").toString()).add(new BigDecimal(((Map) xmxjList.get(j)).get("endDepreAmount").toString())).toString());//累计摊销月份
                                    ((Map) xmxjList.get(i)).put("endDepreMoney", new BigDecimal(((Map) xmxjList.get(i)).get("endDepreMoney").toString()).add(new BigDecimal(((Map) xmxjList.get(j)).get("endDepreMoney").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//累计摊销金额
                                    ((Map) xmxjList.get(i)).put("assetNetValue", new BigDecimal(((Map) xmxjList.get(i)).get("assetNetValue").toString()).add(new BigDecimal(((Map) xmxjList.get(j)).get("assetNetValue").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//无形资产净值
                                    xmxjList.remove(j);//删除相同小计
                                    j--;
                                }
                            }
                        }
                        Map xmhjMap = new HashMap();//项目合计
                        xmhjMap.put("centerCode",centerCode);//核算单位
                        xmhjMap.put("strNo",xmxjName);//序号
                        xmhjMap.put("level1","");//层级1名称
                        xmhjMap.put("level2","");//层级2名称
                        xmhjMap.put("level3","项目合计");//层级3名称
                        xmhjMap.put("nr", "");//内容
                        xmhjMap.put("useStartDate","");//开始使用日期
                        xmhjMap.put("depYears","");//摊销年限(月)
                        xmhjMap.put("assetOriginValue", "0.00");//原值
                        xmhjMap.put("monthDepreMoney", "0.00");//月摊销金额
                        xmhjMap.put("endDepreAmount", "0");//累计摊销月份
                        xmhjMap.put("endDepreMoney", "0.00");//累计摊销金额
                        xmhjMap.put("assetNetValue", "0.00");//无形资产净值
                        xmhjMap.put("temp", "");//备注
                        for(int i=0; i<xmxjList.size(); i++){
                            Map xmxjMap = new HashMap();
                            xmxjMap.putAll((Map) xmxjList.get(i));
                            xmhjMap.put("centerCode",centerCode);//核算单位
                            xmxjMap.put("strNo",xmxjName);
                            xmxjMap.put("level3",xmxjMap.get("level3").toString().replace("小计","项目小计"));//层级3名称
                            xmhjMap.put("level2","");//层级2名称

                            xmhjMap.put("assetOriginValue", new BigDecimal(xmhjMap.get("assetOriginValue").toString()).add(new BigDecimal(xmxjMap.get("assetOriginValue").toString())).setScale(2,BigDecimal.ROUND_HALF_UP).toString());//原值
                            xmhjMap.put("monthDepreMoney", new BigDecimal(xmhjMap.get("monthDepreMoney").toString()).add(new BigDecimal(xmxjMap.get("monthDepreMoney").toString())).setScale(2,BigDecimal.ROUND_HALF_UP).toString());//月摊销金额
                            xmhjMap.put("endDepreAmount", new BigDecimal(xmhjMap.get("endDepreAmount").toString()).add(new BigDecimal(xmxjMap.get("endDepreAmount").toString())).toString());//累计摊销月份
                            xmhjMap.put("endDepreMoney", new BigDecimal(xmhjMap.get("endDepreMoney").toString()).add(new BigDecimal(xmxjMap.get("endDepreMoney").toString())).setScale(2,BigDecimal.ROUND_HALF_UP).toString());//累计摊销金额
                            xmhjMap.put("assetNetValue", new BigDecimal(xmhjMap.get("assetNetValue").toString()).add(new BigDecimal(xmxjMap.get("assetNetValue").toString())).setScale(2,BigDecimal.ROUND_HALF_UP).toString());//无形资产净值
                            result.add(xmxjMap);

                            Map zhjMap = new HashMap();
                            zhjMap.putAll(xmxjMap);
                            xmhjList.add(zhjMap);
                        }
                        result.add(xmhjMap);//
                    }
                    count++;
                }
            }
            for(int i=0; i<xmhjList.size()-1; i++){//将相同小计合并
                for(int j=1; j<xmhjList.size(); j++){
                    if(((Map) xmhjList.get(i)).get("level3").equals(((Map) xmhjList.get(j)).get("level3"))){
                        ((Map) xmhjList.get(i)).put("assetOriginValue", new BigDecimal(((Map) xmhjList.get(i)).get("assetOriginValue").toString()).add(new BigDecimal(((Map) xmhjList.get(j)).get("assetOriginValue").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//原值
                        ((Map) xmhjList.get(i)).put("monthDepreMoney", new BigDecimal(((Map) xmhjList.get(i)).get("monthDepreMoney").toString()).add(new BigDecimal(((Map) xmhjList.get(j)).get("monthDepreMoney").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//月摊销金额
                        ((Map) xmhjList.get(i)).put("endDepreAmount", new BigDecimal(((Map) xmhjList.get(i)).get("endDepreAmount").toString()).add(new BigDecimal(((Map) xmhjList.get(j)).get("endDepreAmount").toString())).toString());//累计摊销月份
                        ((Map) xmhjList.get(i)).put("endDepreMoney", new BigDecimal(((Map) xmhjList.get(i)).get("endDepreMoney").toString()).add(new BigDecimal(((Map) xmhjList.get(j)).get("endDepreMoney").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//累计摊销金额
                        ((Map) xmhjList.get(i)).put("assetNetValue", new BigDecimal(((Map) xmhjList.get(i)).get("assetNetValue").toString()).add(new BigDecimal(((Map) xmhjList.get(j)).get("assetNetValue").toString())).setScale(2, BigDecimal.ROUND_HALF_UP).toString());//无形资产净值
                        xmhjList.remove(j);//删除相同小计
                        j--;
                    }
                }
            }
            Map xmhjMap = new HashMap();//项目合计
            xmhjMap.put("centerCode",centerCode);//核算单位
            xmhjMap.put("strNo","");//序号
            xmhjMap.put("level1","");//层级1名称
            xmhjMap.put("level2","");//层级2名称
            xmhjMap.put("level3","无形资产项目合计");//层级3名称
            xmhjMap.put("nr", "");//内容
            xmhjMap.put("useStartDate","");//开始使用日期
            xmhjMap.put("depYears","");//摊销年限(月)
            xmhjMap.put("assetOriginValue", "0.00");//原值
            xmhjMap.put("monthDepreMoney", "0.00");//月摊销金额
            xmhjMap.put("endDepreAmount", "0");//累计摊销月份
            xmhjMap.put("endDepreMoney", "0.00");//累计摊销金额
            xmhjMap.put("assetNetValue", "0.00");//无形资产净值
            xmhjMap.put("temp", "");//备注
            for(int i=0; i<xmhjList.size(); i++){
                Map zhjMap = new HashMap();
                zhjMap.putAll((Map) xmhjList.get(i));
                zhjMap.put("strNo","");
                zhjMap.put("level2","");//层级2名称
                zhjMap.put("level3",((Map) xmhjList.get(i)).get("level3").toString().replace("小计","合计"));//层级3名称
                result.add(zhjMap);

                xmhjMap.put("assetOriginValue", new BigDecimal(xmhjMap.get("assetOriginValue").toString()).add(new BigDecimal(zhjMap.get("assetOriginValue").toString())).setScale(2,BigDecimal.ROUND_HALF_UP).toString());//原值
                xmhjMap.put("monthDepreMoney", new BigDecimal(xmhjMap.get("monthDepreMoney").toString()).add(new BigDecimal(zhjMap.get("monthDepreMoney").toString())).setScale(2,BigDecimal.ROUND_HALF_UP).toString());//月摊销金额
                xmhjMap.put("endDepreAmount", new BigDecimal(xmhjMap.get("endDepreAmount").toString()).add(new BigDecimal(zhjMap.get("endDepreAmount").toString())).toString());//累计摊销月份
                xmhjMap.put("endDepreMoney", new BigDecimal(xmhjMap.get("endDepreMoney").toString()).add(new BigDecimal(zhjMap.get("endDepreMoney").toString())).setScale(2,BigDecimal.ROUND_HALF_UP).toString());//累计摊销金额
                xmhjMap.put("assetNetValue", new BigDecimal(xmhjMap.get("assetNetValue").toString()).add(new BigDecimal(zhjMap.get("assetNetValue").toString())).setScale(2,BigDecimal.ROUND_HALF_UP).toString());//无形资产净值
            }
            result.add(xmhjMap);//总合计

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
        List<?> yearMonthDateList = accWCheckInfoRepository.getYearMonthDate(CurrentUser.getCurrentLoginAccount(),CurrentUser.getCurrentLoginManageBranch());
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
        List<?> startLevelList = accWCheckInfoRepository.getStartLevel(CurrentUser.getCurrentLoginAccount());
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
        List<?> endLevelList = accWCheckInfoRepository.geteEndLevel(CurrentUser.getCurrentLoginAccount());
        if(endLevelList.size()>0){
            return endLevelList;
        }else{
            return null;
        }
    }
}
