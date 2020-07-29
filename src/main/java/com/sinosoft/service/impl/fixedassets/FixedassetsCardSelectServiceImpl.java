package com.sinosoft.service.impl.fixedassets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.fixedassets.AccDepre;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.repository.fixedassets.AccAssetInfoRepository;
import com.sinosoft.repository.fixedassets.AccAssetSelectRepository;
import com.sinosoft.repository.fixedassets.AccDepreRepository;
import com.sinosoft.service.fixedassets.CategoryCodingService;
import com.sinosoft.service.fixedassets.FixedassetsCardSelectService;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-29 15:10
 */
@Service
public class FixedassetsCardSelectServiceImpl implements FixedassetsCardSelectService {
    private Logger logger = LoggerFactory.getLogger(FixedassetsCardSelectServiceImpl.class);
    @Resource
    private AccAssetInfoRepository accAssetInfoRepository;
    @Resource
    private AccAssetCodeTypeRepository accAssetCodeTypeRepository;
    @Resource
    private AccAssetSelectRepository accAssetSelectRepository;
    @Resource
    private AccDepreRepository accDepreRepository;
    @Resource
    private CategoryCodingService categoryCodingService ;
    /**
     *加载固定资产类别编码中维护的类别
     * @return
     */
   /* @Override
    public List<?> qryAssetType() {
        List<?> result = accAssetInfoRepository.qryAssetType();
        return result;
    }*/

    /**
     * 加载专项中维护的BM类专项
     * @return
     */
  /*  @Override
    public List<?> qryuUnitCode() {
        List<?> result = accAssetInfoRepository.qryuUnitCode();
        return result;
    }*/

    /**
     * 通过固定资产编码类别名称查询 使用年限()
     * @param assetType
     * @return
     */
  /*  @Override
    public String getDepYears(String assetType) {
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("select * from accassetcodetype a where a.asset_type = '"+ assetType +"'");
            List<?> list = accAssetCodeTypeRepository.queryBySql(sql.toString(), AccAssetCodeType.class);
            if(list.size()>0){
                return ((AccAssetCodeType)list.get(0)).getDepYears().toString();
            }else{
                return "";
            }
        }catch (Exception e){
            logger.error("查询异常", e);
            return "";
        }
    }*/

   /* *//**
     * 通过固定资产编码类别名称查询 折旧方法
     * @param
     * @return
     *//*
    @Override
    public String getDepType(String assetType) {
        StringBuffer sql = new StringBuffer();
        sql.append("select code_name from codemanage where 1=1 and code_type = 'deprMethod' and code_code =\n" +
                "(select a.dep_type from accassetcodetype a where a.asset_type = '"+ assetType +"')");
        List<?> list = accAssetCodeTypeRepository.queryBySql(sql.toString());
        if(list.size()>0){
            return list.get(0).toString();
//            return ((AccAssetCodeType)list.get(0)).getDepYears().toString();
        }else{
            return null;
        }
    }*/
/*
* 固定资产卡片查询搜索
* */
    @Override
   public Page<?> qryAccAssetInfo(int page, int rows, AccAssetInfoDTO acc){

        Page<?> result = categoryCodingService.getPage(page,rows,getAssetsCardSelect(acc));
        return result;
    }
    @Override
    public  List<?> getAssetsCardSelect(AccAssetInfoDTO acc){
        StringBuffer sql=new StringBuffer();
        sql.append("select f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                " f.asset_type as  assetType, f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName, f.metric_name as metricName," +
                "f.manufactor as manufactor,f.specification as specification,f.serial_number as serialNumber,f.use_start_date as useStartDate,f.quantity,f.source_flag as sourceFlag,"+
                "(select c.code_name from codemanage c where c.code_code=f.use_flag and c.code_type='useFlag') as useFlag ,f.storage_way as storageWay,f.asset_origin_value as assetOriginValue,f.asset_net_value as assetNetValue, f.dep_years as depYears,f.dep_type as depType,"+
                "f.impairment as impairment, f.added_tax as addedTax,f.sum,f.input_tax as inputTax,f.pay_way as payWay, f.pay_code as payCode,f.remains_value as remainsValue,f.remains_rate as remainsRate," +
                "f.predict_clear_fee as predictClearFee,f.formula_code as formulaCode,f.init_depre_amount as initDepreAmount,f.init_depre_money as initDepreMoney, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "f.clear_flag as clearFlag , f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch, f.clear_operator_code as clearOperatorCode,"+
                "f.voucher_no as voucherNo,f.depre_from_date as depreFromDate,f.depre_to_date as depreToDate,f.depre_flag as  depreFlag,f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp,"+
                "(select a.asset_simple_name from accassetcodetype a where  a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetTypeName ,"+
                "(select o.special_name from specialinfo o where o.id=f.unit_code ) as unitCode,"+
                "(select c.code_name from codemanage c where c.code_type='organization' and c.code_code=f.organization ) as organization,"+
                " (select c.code_name from codemanage c where c.code_type='deprMethod' and c.code_code=f.dep_type ) as deprMethod,"+
                "(select i.month_depre_money from  AccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code ORDER BY i.year_month_data desc limit 1 ) as monthDepreMoney   from AccAssetInfo  f where 1=1 ");

        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();

        sql.append(" and center_code = ?"+ paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append(" and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append(" and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;



        if(acc.getCardCode()!=null&&!acc.getCardCode().equals("")){
            if(acc.getCardCode().length()<5){
                String cs=acc.getCardCode();
                for(int i=0;i<5-acc.getCardCode().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode(cs);
            }
            sql.append(" and f.card_code>=?" + paramsNo );
            params.put(paramsNo,acc.getCardCode());
            paramsNo++;
        }
        if(acc.getCardCode1()!=null&&!acc.getCardCode1().equals("")){
            if(acc.getCardCode1().length()<5){
                String cs=acc.getCardCode1();
                for(int i=0;i<5-acc.getCardCode1().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode1(cs);
            }
            sql.append(" and f.card_code<=?" + paramsNo);
            params.put(paramsNo,acc.getCardCode1());
            paramsNo++;
        }
        if(acc.getAssetCode1()!=null&&!acc.getAssetCode1().equals("")){
            sql.append(" and f.asset_code>=?" + paramsNo);
            params.put(paramsNo,acc.getAssetCode1());
            paramsNo++;
        }
        if(acc.getAssetCode2()!=null&&!acc.getAssetCode2().equals("")){
            sql.append(" and f.asset_code<=?" + paramsNo);
            params.put(paramsNo,acc.getAssetCode2());
            paramsNo++;
        }
        if(acc.getUseStartDate()!=null&&!acc.getUseStartDate().equals("")){
            sql.append(" and f.use_start_date>=?" + paramsNo);
            params.put(paramsNo,acc.getUseStartDate());
            paramsNo++;
        }
        if(acc.getUseStartDate1()!=null&&!acc.getUseStartDate1().equals("")){
            sql.append(" and f.use_start_date<=?" + paramsNo);
            params.put(paramsNo,acc.getUseStartDate1());
            paramsNo++;
        }

        if(acc.getAssetOriginValue()!=null&&!acc.getAssetOriginValue().equals("")){
            sql.append(" and f.asset_origin_value>=?" + paramsNo);
            params.put(paramsNo,acc.getAssetOriginValue());
            paramsNo++;
        }
        if(acc.getAssetOriginValue1()!=null&&!acc.getAssetOriginValue1().equals("")){
            sql.append(" and f.asset_origin_value<=?" + paramsNo);
            params.put(paramsNo,acc.getAssetOriginValue1());
            paramsNo++;
        }
        if(acc.getAssetNetValue()!=null&&!acc.getAssetNetValue().equals("")){
            sql.append(" and f.asset_net_value>=?" + paramsNo);
            params.put(paramsNo,acc.getAssetNetValue());
            paramsNo++;
        }
        if(acc.getAssetNetValue1()!=null&&!acc.getAssetNetValue1().equals("")){
            sql.append(" and f.asset_net_value<=?" + paramsNo);
            params.put(paramsNo,acc.getAssetNetValue1());
            paramsNo++;
        }
        if(acc.getEndDepreMoney()!=null&&!acc.getEndDepreMoney().equals("")){
            sql.append(" and f.end_depre_money>=?" + paramsNo);
            params.put(paramsNo,acc.getEndDepreMoney());
            paramsNo++;
        }
        if(acc.getEndDepreMoney1()!=null&&!acc.getEndDepreMoney1().equals("")){
            sql.append(" and f.end_depre_money<=?" + paramsNo);
            params.put(paramsNo,acc.getEndDepreMoney1());
            paramsNo++;
        }
        if(acc.getDepYears()!=null&&!acc.getDepYears().equals("")){
            sql.append(" and f.dep_years>=?" + paramsNo);
            params.put(paramsNo,acc.getDepYears());
            paramsNo++;

        }
        if(acc.getDepYears1()!=null&&!acc.getDepYears1().equals("")){
            sql.append(" and f.dep_years<=?" + paramsNo);
            params.put(paramsNo,acc.getDepYears1());
            paramsNo++;
        }
        if(acc.getDepreToDate()!=null&&!acc.getDepreToDate().equals("")){
            sql.append(" and f.depre_to_date<=?" + paramsNo);
            params.put(paramsNo,acc.getDepreToDate());
            paramsNo++;
            sql.append(" and f.use_start_data<=?"+paramsNo);
            params.put(paramsNo,acc.getDepreToDate());
            paramsNo++;
        }
        //变更信息
        if(acc.getChangeMessage()!=null&&!acc.getChangeMessage().equals("")){
            if(acc.getChangeMessage().equals("1")){
                //无变更信息 exists
                sql.append(" and not exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }
            if(acc.getChangeMessage().equals("2")){
                //有变更信息
                sql.append(" and  exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
            }
        }
        if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
            sql.append(" and f.card_code in(select c.card_code from accAssetInfoChange c where  c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_date<=?"+paramsNo+")");
            params.put(paramsNo,acc.getChangeDate());
            paramsNo++;
        }
        if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
            sql.append(" and f.card_code in(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_type=?"+paramsNo+")");
            params.put(paramsNo,acc.getChangeType());
            paramsNo++;
        }
        //使用部门unitCode
        if(acc.getUnitCode()!=null&&!acc.getUnitCode().equals("")){
            sql.append(" and f.unit_code=?" + paramsNo );
            params.put(paramsNo,acc.getUnitCode());
            paramsNo++;
        }
        if(acc.getAssetType()!=null&&!acc.getAssetType().equals("")){
            sql.append(" and f.asset_type=?" + paramsNo );
            params.put(paramsNo,acc.getAssetType());
            paramsNo++;
        }
        //sourceFlag
        if(acc.getSourceFlag()!=null&&!acc.getSourceFlag().equals("")){
            sql.append(" and f.source_flag=?" + paramsNo);
            params.put(paramsNo,acc.getSourceFlag());
            paramsNo++;
        }
        if(acc.getStopCard()!=null&&!acc.getStopCard().equals("")){
            if(acc.getStopCard().equals("0")){
                //不包括已停用卡片
                sql.append(" and f.use_flag='1' ");
            }
        }
        if(acc.getCleanCard()!=null&&!acc.getCleanCard().equals("")){
            if(acc.getCleanCard().equals("1")){
                //不包含已清理卡片
                sql.append(" and f.clear_flag='0'");
            }else if(acc.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag='1'");

            }
        }
        if(acc.getIsNotVoucher()!=null&&!acc.getIsNotVoucher().equals("")){
            if(acc.getIsNotVoucher().equals("0")){
                //没有生成凭证
                sql.append(" and (f.voucher_no='' or f.voucher_no  is  null)");
            }else{
                sql.append(" and f.voucher_no !='' and f.voucher_no is not null");

            }
        }
      /*  if(acc.getCreateOper()!=null&&!acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper='"+acc.getCreateOper()+"'");
        }*/
        if(acc.getCreateOper()!=null && !acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from roleinfo where role_name like ?" + paramsNo+")");
            params.put(paramsNo,"%"+acc.getCreateOper()+"%");
            paramsNo++;
        }
        sql.append(" order by f.card_code asc");
        return accAssetInfoRepository.queryBySqlSC(sql.toString(),params);
    }
    /*
    * 固定资产卡片查询 台账查询
    * */
    @Override
    public List<?> getAssetDDepre(AccAssetInfoDTO acc){
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
       List<?> list= accAssetSelectRepository.getAccDepre(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getAssetType(),String.valueOf(acc.getAssetCode()));
        List<Map<String,Object>> assetValue=accAssetSelectRepository.getAccAssetValue(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());
        Map<String,Object> info=assetValue.get(0);
          List list1=new ArrayList();
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map = new HashMap();
                map.putAll((Map) obj);
               /* //本月累计折旧月数和累计金额
               Map<String,BigDecimal> count= accAssetSelectRepository.getCount(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getAssetType(),String.valueOf(acc.getAssetCode()),String.valueOf(map.get("year_month_data")));

                //上月折旧日期
                String month=String.valueOf(map.get("year_month_data"));
                if(month.substring(4).equals("01")){
                    month=(Integer.parseInt(month.substring(0,4))-1)+"12";
                }else{
                    if(Integer.parseInt(month.substring(4))>10){
                        month=month.substring(0,4)+(Integer.parseInt(month.substring(4))-1);
                    }else{
                        month=month.substring(0,4)+"0"+(Integer.parseInt(month.substring(5))-1);
                    }
                }
                //上月累计折旧月数和累计金额
                Map<String,BigDecimal> countEnd= accAssetSelectRepository.getCount(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getAssetType(),String.valueOf(acc.getAssetCode()),month);
                if(countEnd.get("allDepreQuantity")==null){
                    countEnd.put("allDepreQuantity",new BigDecimal("0"));
                }
                if(countEnd.get("allDepreMoney")==null){
                    countEnd.put("allDepreMoney",new BigDecimal("0"));
                }
                if(count.get("allDepreQuantity")==null){
                    count.put("allDepreQuantity",new BigDecimal("0"));
                }
                if(count.get("allDepreMoney")==null){
                    count.put("allDepreMoney",new BigDecimal("0"));
                }*/
                //本月计提月数
                map.put("thisMonthDrepre", map.get("month_depre_quantity")==null?"0":map.get("month_depre_quantity"));
                //本月计提折旧金额
                map.put("month_depre_money", map.get("month_depre_money")==null?"0.00":map.get("month_depre_money"));

                //期末累计折旧月份
                map.put("endDepreAmount",map.get("all_depre_quantity")==null?"0":map.get("all_depre_quantity"));
                map.put("assetOriginValue",info.get("assetOriginValue")==null?"0.00":info.get("assetOriginValue"));
                map.put("assetNetValue",map.get("current_net_value")==null?"0.00":map.get("current_net_value"));
                map.put("impairment",info.get("impairment")==null?"0.00":info.get("impairment"));
                BigDecimal endDepreMoney=new BigDecimal(String.valueOf(map.get("all_depre_money"))).add(new BigDecimal(String.valueOf(info.get("initDepreMoney"))));
                map.put("endDepreMoney",endDepreMoney);
                list1.add(map);

            }
        }
        return list1;
    }
    /**
     * 固定资产折旧查询搜索
     * @param
     * @return
     */
    @Override
    public List<?> getAssetDepreSelect(String centerCode1,String  branchCode1,String  levelstart,String  levelend,String  yearMonthData){
//        StringBuffer sql =new StringBuffer();
//        sql.append("select a.level as level, a.center_code as centerCode,a.branch_code as branchCode,a.acc_book_type as accBookType,a.acc_book_code as accBookCode, a.code_type as codeType ,a.asset_type as assetType,a.asset_complex_name as assetComplexName,c.current_origin_value as currentOriginValue,\n" +
//                "\n" +
//                "(select b.current_origin_value  from accdepre b where b.center_code=c.center_code and b.branch_code=c.branch_code and b.acc_book_type=c.acc_book_type and b.acc_book_code=c.acc_book_code and b.code_type=c.code_type and b.asset_type=c.asset_type \n" +
//                " and b.asset_code=c.asset_code and b.year_month_data=REPLACE(left(DATE_SUB(CONCAT(left(c.year_month_data ,4),'-',right(c.year_month_data,2),'-01'),INTERVAL 1 MONTH),7),'-','')) as upcurrentoriginvalue ,\n" +
//                "   \n" +
//                "(select b.month_depre_money  from accdepre b where b.center_code=c.center_code\n" +
//                "     and b.branch_code=c.branch_code and b.acc_book_type=c.acc_book_type and b.acc_book_code=c.acc_book_code and b.code_type=c.code_type and b.asset_type=c.asset_type \n" +
//                "     and b.asset_code=c.asset_code and b.year_month_data=REPLACE(left(DATE_SUB(CONCAT(left(c.year_month_data ,4),'-',right(c.year_month_data,2),'-01'),INTERVAL 1 MONTH),7),'-','')) as upmonthdepremoney ,\n" +
//                "    \n" +
//                "(select u.user_name from  userinfo u where u.id='"+CurrentUser.getCurrentUser().getId()+"' ) as operator," +
//
//                "     month_depre_money as monthDepreMoney from AccAssetCodeType a,AccDepre c where a.center_code=c.center_code and a.branch_code=c.branch_code and a.acc_book_type=c.acc_book_type and a.acc_book_code=c.acc_book_code\n" +
//                " and a.acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and a.acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' "+
//                "     and a.code_type=c.code_type and a.asset_type=c.asset_type ");
//        if(centerCode1!=null&&!centerCode1.equals("")){
//            sql.append(" and a.center_code="+centerCode1);
//        }
//        if(branchCode1!=null&&!branchCode1.equals("")){
//            sql.append(" and a.branch_code="+branchCode1);
//        }
//        if(levelstart!=null&&!levelstart.equals("")){
//            sql.append(" and a.level>="+levelstart);
//        }
//        if(levelend!=null&&!levelend.equals("")){
//            sql.append(" and a.level<="+levelend);
//        }
//        if(yearMonthData!=null&&!yearMonthData.equals("")){
//            sql.append(" and c.year_month_data="+yearMonthData);
//        }

     //   return  accAssetSelectRepository.queryBySqlSC(sql.toString());

        try{
            int paramsNo = 1;
            Map<Integer,Object> params = new HashMap<>();
            List<Object> result = new ArrayList<>();
            //获取所有资产类别
            StringBuffer sql = new StringBuffer();
            sql.append("select asset_simple_name as assetSimpleName,asset_complex_name as assetComplexName,level,end_flag as endFlag,super_code as superCode, asset_type as assetType,code_type as codeType,acc_book_code as accBookCode " +
                    " from AccAssetCodeType where 1=1 ");
            sql.append(" and acc_book_type =?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
            paramsNo++;
            sql.append(" and acc_book_code= ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;

            if(levelstart!=null&&!levelstart.equals("")){
                sql.append(" and level>=?"+paramsNo);
                params.put(paramsNo,levelstart);
                paramsNo++;
            }
            if(levelend!=null&&!levelend.equals("")){
                sql.append(" and level<=?"+paramsNo);
                params.put(paramsNo,levelend);
                paramsNo++;
            }
            sql.append(" order by asset_type asc");
            List<?> assetList = accAssetCodeTypeRepository.queryBySqlSC(sql.toString(),params);

            if(assetList.size()>0) {
                for (Object obj : assetList) {
                    Map map = new HashMap();
                    map.putAll((Map) obj);
                    StringBuffer back = new StringBuffer();
                    if (Integer.parseInt(map.get("level").toString()) > 1) {
                        for (int i = 1; i < Integer.parseInt(map.get("level").toString()); i++) {
                            back.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                        }
                    }
                    if(map.get("assetComplexName")==null||map.get("assetComplexName").equals("")){
                        map.put("assetComplexName", back.append(map.get("assetSimpleName") + "(" + map.get("assetType") + ")"));
                    }else{
                        map.put("assetComplexName", back.append(map.get("assetComplexName") + "(" + map.get("assetType") + ")"));
                    }
                    StringBuffer sqls=new StringBuffer();
                    int paramNumber = 1;
                    Map<Integer,Object> param = new HashMap<>();
                    sqls.append("select c.center_code as centerCode, c.acc_book_code as accBookCode, c.code_type as codeType ,c.asset_type as assetType, sum(c.current_origin_value) as currentOriginValue,sum(c.month_depre_money) as monthDepreMoney \n" +
                            "  from AccDepre c where 1=1  ");

                    sqls.append(" and c.center_code = ?"+paramNumber);
                    param.put(paramNumber,CurrentUser.getCurrentLoginManageBranch());
                    paramNumber++;
                    sqls.append(" and c.acc_book_type = ?"+paramNumber);
                    param.put(paramNumber,CurrentUser.getCurrentLoginAccountType());
                    paramNumber++;
                    sqls.append(" and c.acc_book_code=?"+paramNumber);
                    param.put(paramNumber,CurrentUser.getCurrentLoginAccount());
                    paramNumber++;
                    sqls.append(" and c.code_type=?"+paramNumber);
                    param.put(paramNumber,map.get("codeType"));
                    paramNumber++;
                    sqls.append(" and c.asset_type like ?"+paramNumber);
                    param.put(paramNumber,map.get("assetType")+"%");
                    paramNumber++;

                    //(select u.user_name from  userinfo u where u.id='1' ) as operator
                    if(centerCode1!=null&&!centerCode1.equals("")){
                        sqls.append(" and c.center_code=?"+paramNumber);
                        param.put(paramNumber,centerCode1);
                        paramNumber++;
                    }
                    if(branchCode1!=null&&!branchCode1.equals("")){
                        sqls.append(" and c.branch_code=?"+paramNumber);
                        param.put(paramNumber,branchCode1);
                        paramNumber++;
                    }

                    if(yearMonthData!=null&&!yearMonthData.equals("")){
                        sqls.append(" and c.year_month_data=?"+paramNumber);
                        param.put(paramNumber,yearMonthData);
                        paramNumber++;
                    }

                    List<?> accDepreList=accDepreRepository.queryBySqlSC(sqls.toString(),param );
                    for (Object objs : accDepreList) {
                        Map maps = new HashMap();
                        maps.putAll((Map) objs);
//                        map.put("accBookCode",CurrentUser.getCurrentLoginAccount());
//                        map.put("codeType",maps.get("codeType"));
//                        map.put("assetType",maps.get("assetType"));
//                        map.put("centerCode",centerCode);
                        //获取上个月的折旧信息
//                        StringBuffer fsql=new StringBuffer("select sum(current_origin_value) as upcurrentoriginvalue,sum(month_depre_money) as upmonthdepremoney  from accdepre  where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and  asset_type like '"+map.get("assetType")+"%'"+
//                                "  and year_month_data=REPLACE(left(DATE_SUB(CONCAT(left('"+yearMonthData+"' ,4),'-',right('"+yearMonthData+"',2),'-01'),INTERVAL 1 MONTH),7),'-','')\n");
//                        List<Map<String,BigDecimal>> accdeup=(List<Map<String,BigDecimal>>)accDepreRepository.queryBySqlSC(fsql.toString());
                        List<Map<String,BigDecimal>> accdeup=accDepreRepository.queryAccDepreByYearMonthData(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(), (String) map.get("assetType"),yearMonthData,yearMonthData);
                        BigDecimal  upcurrentoriginvalue=null;
                        BigDecimal  upmonthdepremoney=null;
                        if(accdeup!=null&&accdeup.size()>0){
                            upcurrentoriginvalue=accdeup.get(0).get("upcurrentoriginvalue");
                            upmonthdepremoney=accdeup.get(0).get("upmonthdepremoney");
                        }
                        map.put("currentOriginValue", maps.get("currentOriginValue")==null?0:maps.get("currentOriginValue"));
                        map.put("monthDepreMoney", maps.get("monthDepreMoney")==null?0:maps.get("monthDepreMoney"));
                        map.put("upcurrentoriginvalue", upcurrentoriginvalue==null?0:upcurrentoriginvalue);
                        map.put("upmonthdepremoney", upmonthdepremoney==null?0:upmonthdepremoney);
                    }
                    result.add(map);

                }
            }
                return result;
                }catch (Exception e){
            logger.error("固定资产折旧查询异常",e);
            return null;
        }

    }
    /**
     * 固定资产折旧查询 登记信息
     * @param
     * @return
     */
   @Override
   public List<?>  getAssetDepreMessage(AccAssetInfoDTO accAssetInfoDTO ){
       String centerCode = CurrentUser.getCurrentLoginManageBranch();
       String branchCode = CurrentUser.getCurrentLoginManageBranch();
       int paramsNo = 1;
       Map<Integer,Object> params = new HashMap<>();
       StringBuffer sql=new StringBuffer();
       sql.append(" select f.center_code as centerCode,f.branch_code as branchCode,f.acc_book_type as accBookType,f.acc_book_code as accBookCode,f.code_type as codeType,f.card_code as cardCode ,f.asset_type as assetType, " +
               "f.asset_code as assetCode ,f.asset_name as assetName,f.specification as specification,f.use_start_date as useStartDate,f.quantity as quantity,f.asset_origin_value as assetOriginValue,f.end_depre_money as endDepreMoney," +
               "( select o.special_name from specialinfo o where o.id=f.unit_code) as unitName," +
               "(select t.asset_simple_name from AccAssetCodeType t where  t.acc_book_type =f.acc_book_type  and t.acc_book_code =f.acc_book_code and t.code_type=f.code_type and t.asset_type=f.asset_type  ) as assetTypeName"+
               " from AccAssetInfo f where 1=1");

       sql.append(" and f.center_code=?"+paramsNo);
       params.put(paramsNo,centerCode);
       paramsNo++;
       sql.append(" and f.branch_code=?"+paramsNo);
       params.put(paramsNo,branchCode);
       paramsNo++;
       sql.append(" and f.acc_book_code=?"+paramsNo);
       params.put(paramsNo,accAssetInfoDTO.getAccBookCode());
       paramsNo++;
       sql.append(" and f.code_type=?"+paramsNo);
       params.put(paramsNo,accAssetInfoDTO.getCodeType());
       paramsNo++;
       sql.append(" and f.asset_type like ?"+paramsNo);
       params.put(paramsNo,accAssetInfoDTO.getAssetType()+"%");
       paramsNo++;
       sql.append(" limit ?"+paramsNo+" , ");
       params.put(paramsNo,accAssetInfoDTO.getPagestart());
       paramsNo++;
       sql.append(" ?"+paramsNo);
       params.put(paramsNo,accAssetInfoDTO.getPagerow());
       return  accAssetSelectRepository.queryBySqlSC(sql.toString(),params);
   }
    /**
     * 固定资产折旧查询 打印登记信息
     * @param
     * @return
     */
    @Override
    public List<?>  getAssetDepreMessageprint(AccAssetInfoDTO accAssetInfoDTO ){

        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        StringBuffer sql=new StringBuffer();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        sql.append(" select f.center_code as centerCode,f.branch_code as branchCode,f.acc_book_type as accBookType,f.acc_book_code as accBookCode,f.code_type as codeType,f.card_code as cardCode ,f.asset_type as assetType, " +
                "f.asset_code as assetCode ,f.asset_name as assetName,f.specification as specification,f.use_start_date as useStartDate,f.quantity as quantity,f.asset_origin_value as assetOriginValue,f.end_depre_money as endDepreMoney," +
                "( select o.special_name from specialinfo o where o.id=f.unit_code) as unitName," +
                "(select t.asset_simple_name from AccAssetCodeType t where t.center_code=f.center_code and t.branch_code =f.branch_code  and t.acc_book_type =f.acc_book_type  and t.acc_book_code =f.acc_book_code and t.code_type=f.code_type and t.asset_type=f.asset_type  ) as assetTypeName"+
                " from AccAssetInfo f where 1=1");

        sql.append(" and f.center_code = ?"+paramsNo);
        params.put(paramsNo,centerCode);
        paramsNo++;
        sql.append(" and f.branch_code = ?"+paramsNo);
        params.put(paramsNo,branchCode);
        paramsNo++;
        sql.append(" and f.acc_book_code=?"+paramsNo);
        params.put(paramsNo,accAssetInfoDTO.getAccBookCode());
        paramsNo++;
        sql.append(" and f.code_type=?"+paramsNo);
        params.put(paramsNo,accAssetInfoDTO.getCodeType());
        paramsNo++;
        sql.append(" and f.asset_type like ?"+paramsNo);
        params.put(paramsNo,accAssetInfoDTO.getAssetType()+"%");
        paramsNo++;

        return  accAssetSelectRepository.queryBySqlSC(sql.toString());
    }
    /**
     * 固定资产折旧查询 登记信息 获取分页
     * @param
     * @return
     */
    @Override
    public Integer  getAssetDepreMessagePage(AccAssetInfoDTO accAssetInfoDTO){
          String centerCode = CurrentUser.getCurrentLoginManageBranch();
          String branchCode = CurrentUser.getCurrentLoginManageBranch();
          return  accAssetSelectRepository.getAccAssetDepreCount(centerCode,branchCode,accAssetInfoDTO.getAccBookCode(),accAssetInfoDTO.getCodeType(),accAssetInfoDTO.getAssetType());
    }

    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols) {
        ExcelUtil excelUtil = new ExcelUtil();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql=new StringBuffer("select f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                " f.asset_type as  assetType, f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName, f.metric_name as metricName," +
                "f.manufactor as manufactor,f.specification as specification,f.serial_number as serialNumber,f.use_start_date as useStartDate,f.quantity,f.source_flag as sourceFlag,"+
                "(select c.code_name from codemanage c where c.code_code=f.use_flag and c.code_type='useFlag') as useFlag ,f.storage_way as storageWay,f.asset_origin_value as assetOriginValue,f.asset_net_value as assetNetValue, f.dep_years as depYears,f.dep_type as depType,"+
                "f.impairment as impairment, f.added_tax as addedTax,f.sum,f.input_tax as inputTax,f.pay_way as payWay, f.pay_code as payCode,f.remains_value as remainsValue,f.remains_rate as remainsRate," +
                "f.predict_clear_fee as predictClearFee,f.formula_code as formulaCode,f.init_depre_amount as initDepreAmount,f.init_depre_money as initDepreMoney, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "f.clear_flag as clearFlag , f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch, f.clear_operator_code as clearOperatorCode,"+
                "f.voucher_no as voucherNo,f.depre_from_date as depreFromDate,f.depre_to_date as depreToDate,f.depre_flag as  depreFlag,f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp,"+
                "(select a.asset_simple_name from accassetcodetype a where  a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetTypeName ,"+
                "(select o.special_name from specialinfo o where o.id=f.unit_code ) as unitCode,"+
                "(select c.code_name from codemanage c where c.code_type='organization' and c.code_code=f.organization ) as organization,"+
                " (select c.code_name from codemanage c where c.code_type='deprMethod' and c.code_code=f.dep_type ) as deprMethod,"+

                "(select i.month_depre_money from  AccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code ORDER BY i.year_month_data desc limit 1 ) as monthDepreMoney   from AccAssetInfo  f where 1=1 ");
        sql.append(" AND f.center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        AccAssetInfoDTO acc = new AccAssetInfoDTO();
        try {
            acc = new ObjectMapper().readValue(queryConditions, AccAssetInfoDTO.class);
        } catch (Exception e){
            e.printStackTrace();
        }

//        sql.append(" AND f.center_code = '"+CurrentUser.getCurrentLoginManageBranch()+"'");
        sql.append(" AND f.acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        if(acc.getCardCode()!=null&&!acc.getCardCode().equals("")){
            if(acc.getCardCode().length()<5){
                String cs=acc.getCardCode();
                for(int i=0;i<5-acc.getCardCode().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode(cs);
            }
            sql.append(" and f.card_code>=?" + paramsNo );
            params.put(paramsNo,acc.getCardCode());
            paramsNo++;
        }
        if(acc.getCardCode1()!=null&&!acc.getCardCode1().equals("")){
            if(acc.getCardCode1().length()<5){
                String cs=acc.getCardCode1();
                for(int i=0;i<5-acc.getCardCode1().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode1(cs);
            }
            sql.append(" and f.card_code<=?" + paramsNo);
            params.put(paramsNo,acc.getCardCode1());
            paramsNo++;
        }
        if(acc.getAssetCode1()!=null&&!acc.getAssetCode1().equals("")){
            sql.append(" and f.asset_code>=?" + paramsNo);
            params.put(paramsNo,acc.getAssetCode1());
            paramsNo++;
        }
        if(acc.getAssetCode2()!=null&&!acc.getAssetCode2().equals("")){
            sql.append(" and f.asset_code<=?" + paramsNo);
            params.put(paramsNo,acc.getAssetCode2());
            paramsNo++;
        }
        if(acc.getUseStartDate()!=null&&!acc.getUseStartDate().equals("")){
            sql.append(" and f.use_start_date>=?" + paramsNo);
            params.put(paramsNo,acc.getUseStartDate());
            paramsNo++;
        }
        if(acc.getUseStartDate1()!=null&&!acc.getUseStartDate1().equals("")){
            sql.append(" and f.use_start_date<=?" + paramsNo);
            params.put(paramsNo,acc.getUseStartDate1());
            paramsNo++;
        }

        if(acc.getAssetOriginValue()!=null&&!acc.getAssetOriginValue().equals("")){
            sql.append(" and f.asset_origin_value>=?" + paramsNo);
            params.put(paramsNo,acc.getAssetOriginValue());
            paramsNo++;
        }
        if(acc.getAssetOriginValue1()!=null&&!acc.getAssetOriginValue1().equals("")){
            sql.append(" and f.asset_origin_value<=?" + paramsNo);
            params.put(paramsNo,acc.getAssetOriginValue1());
            paramsNo++;
        }
        if(acc.getAssetNetValue()!=null&&!acc.getAssetNetValue().equals("")){
            sql.append(" and f.asset_net_value>=?" + paramsNo);
            params.put(paramsNo,acc.getAssetNetValue());
            paramsNo++;
        }
        if(acc.getAssetNetValue1()!=null&&!acc.getAssetNetValue1().equals("")){
            sql.append(" and f.asset_net_value<=?" + paramsNo);
            params.put(paramsNo,acc.getAssetNetValue1());
            paramsNo++;
        }
        if(acc.getEndDepreMoney()!=null&&!acc.getEndDepreMoney().equals("")){
            sql.append(" and f.end_depre_money>=?" + paramsNo);
            params.put(paramsNo,acc.getEndDepreMoney());
            paramsNo++;
        }
        if(acc.getEndDepreMoney1()!=null&&!acc.getEndDepreMoney1().equals("")){
            sql.append(" and f.end_depre_money<=?" + paramsNo);
            params.put(paramsNo,acc.getEndDepreMoney1());
            paramsNo++;
        }
        if(acc.getDepYears()!=null&&!acc.getDepYears().equals("")){
            sql.append(" and f.dep_years>=?" + paramsNo);
            params.put(paramsNo,acc.getDepYears());
            paramsNo++;
        }
        if(acc.getDepYears1()!=null&&!acc.getDepYears1().equals("")){
            sql.append(" and f.dep_years<=?" + paramsNo);
            params.put(paramsNo,acc.getDepYears1());
            paramsNo++;
        }
        if(acc.getDepreToDate()!=null&&!acc.getDepreToDate().equals("")){
            sql.append(" and f.depre_to_date<=?"+paramsNo);
            params.put(paramsNo,acc.getDepreToDate());
            paramsNo++;
            sql.append(" and f.use_start_date <=?"+paramsNo);
            params.put(paramsNo,acc.getDepreToDate());
            paramsNo++;
        }
        //变更信息
        if(acc.getChangeMessage()!=null&&!acc.getChangeMessage().equals("")){
            if(acc.getChangeMessage().equals("1")){
                //无变更信息 exists
                sql.append(" and not exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }
            if(acc.getChangeMessage().equals("2")){
                //有变更信息
                sql.append(" and  exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
            }
        }
        if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
            sql.append(" and f.card_code in(select c.card_code from accAssetInfoChange c where  c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_date<=?"+paramsNo+")");
            params.put(paramsNo,acc.getChangeDate());
            paramsNo++;
        }
        if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
            sql.append(" and f.card_code in(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_type=?"+paramsNo+")");
            params.put(paramsNo,acc.getChangeType());
            paramsNo++;
        }
        //使用部门unitCode
        if(acc.getUnitCode()!=null&&!acc.getUnitCode().equals("")){
            sql.append(" and f.unit_code=?" + paramsNo );
            params.put(paramsNo,acc.getUnitCode());
            paramsNo++;
        }
        if(acc.getAssetType()!=null&&!acc.getAssetType().equals("")){
            sql.append(" and f.asset_type=?" + paramsNo );
            params.put(paramsNo,acc.getAssetType());
            paramsNo++;
        }
        //sourceFlag
        if(acc.getSourceFlag()!=null&&!acc.getSourceFlag().equals("")){
            sql.append(" and f.source_flag=?" + paramsNo);
            params.put(paramsNo,acc.getSourceFlag());
            paramsNo++;
        }
        if(acc.getStopCard()!=null&&!acc.getStopCard().equals("")){
            if(acc.getStopCard().equals("0")){
                //不包括已停用卡片
                sql.append(" and f.use_flag='1' ");
            }
        }
        if(acc.getCleanCard()!=null&&!acc.getCleanCard().equals("")){
            if(acc.getCleanCard().equals("1")){
                //不包含已清理卡片
                sql.append(" and f.clear_flag='0'");
            }else if(acc.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag='1'");

            }
        }
        if(acc.getIsNotVoucher()!=null&&!acc.getIsNotVoucher().equals("")){
            if(acc.getIsNotVoucher().equals("0")){
                //没有生成凭证
                sql.append(" and (f.voucher_no='' or f.voucher_no  is  null)");
            }else{
                sql.append(" and f.voucher_no !='' and f.voucher_no is not null");

            }
        }
      /*  if(acc.getCreateOper()!=null&&!acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper='"+acc.getCreateOper()+"'");
        }*/
        if(acc.getCreateOper()!=null && !acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from roleinfo where role_name like ?" + paramsNo+")");
            params.put(paramsNo,"%"+acc.getCreateOper()+"%");
            paramsNo++;
        }
        sql.append(" order by f.card_code asc");
        try {
            // 根据条件查询导出数据集
            List<?> dataList = accAssetInfoRepository.queryBySqlSC(sql.toString(),params);
            // 导出
            excelUtil.exportu_cardselect(request, response, name, cols, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<?> getAssetVoucherSelect(String date1,String date2){
//       StringBuffer sql=new StringBuffer();
//       sql.append(" select f.center_code as centerCode,f.branch_code as branchCode,f.acc_book_type as accBookType,f.acc_book_code as accBookCode,f.year_month_date as yearMonthDate,f.voucher_no as voucherNo,f.voucher_date as voucherDate,f.aux_number as auxNumber," +
//               "f.create_branch_code as createBranchCode ,f.create_by as createBy,f.approve_branch_code as approveBranchCode,f.approve_by as approveBy ,f.approve_date as approveDate,f.gene_branch_code as geneBranchCode," +
//               "f.gene_by as geneBy ,f.gene_date as geneDate ,f.voucher_flag  as voucherFlag , f.flag as flag, f.voucher_date as voucher_date," +
//               "(select u.user_name from  userinfo u where u.id=f.create_by ) as createByName," +
//               "(select u.user_name from  userinfo u where u.id=f.approve_by ) as approveByName," +
//               "(select u.user_name from  userinfo u where u.id=f.gene_by ) as geneByName," +
//               "(select c.code_name from codemanage c where c.code_type='voucherFlag' and c.code_code=f.voucher_flag ) as voucherFlagName,"+
//               "(select (select r.remark_name from AccRemarkManage r where r.id=g.remark )  from AccSubVoucher g where g.center_code=f.center_code and g.branch_code=f.branch_code and g.acc_book_type=f.acc_book_type and g.acc_book_code=f.acc_book_code and g.year_month_date=f.year_month_date and  g.voucher_no=f.voucher_no and g.suffix_no='1'\n) as remarkName,"+
//               "(select sum(g.debit_source)  from AccSubVoucher g where g.center_code=f.center_code and g.branch_code=f.branch_code and g.acc_book_type=f.acc_book_type and g.acc_book_code=f.acc_book_code and g.year_month_date=f.year_month_date and  g.voucher_no=f.voucher_no GROUP BY g.voucher_no) as debitSourceSum,"+
//               "(select sum(g.credit_source)  from AccSubVoucher g where g.center_code=f.center_code and g.branch_code=f.branch_code and g.acc_book_type=f.acc_book_type and g.acc_book_code=f.acc_book_code and g.year_month_date=f.year_month_date and  g.voucher_no=f.voucher_no GROUP BY g.voucher_no) as creditSourceSum"+
//               " from  AccMainVoucher f where 1=1   and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"'  ");
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql=new StringBuffer();
        sql.append("select a.voucher_date as voucherDate,a.voucher_no as voucherNo,a.year_month_date as yearMonthDate,a.generate_way as generateWay,a.voucher_type as voucherType,");
        sql.append("(select u.user_name from userinfo u where u.id=a.create_by) as createBy,");
        sql.append("(select u.user_name from userinfo u where u.id=a.approve_by) as approveBy, ");
        sql.append("(select u.user_name from userinfo u where u.id=a.gene_by) as geneBy, ");
        sql.append("a.aux_number as auxNumber,a.approve_date as approveDate,  ");
        sql.append("(SELECT s.remark FROM accsubvoucher s where s.center_code=a.center_code and  s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no limit 1) as remarkName, ");
        sql.append("(SELECT sum(s.debit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) as debit, ");
        sql.append("(SELECT sum(s.credit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) as credit,  ");
        sql.append("(select c.code_name from codemanage c where c.code_type='voucherFlag' and c.code_code = a.voucher_flag) as voucherFlag  ");
        //凭证类型voucher_type:1决算凭证、2记账凭证、3固资自转、4无资自转。   录入方式generate_way:1-自动 2-手工
        sql.append("from AccMainVoucher a  where 1=1 and a.voucher_type='3' and a.generate_way='1' ");

        sql.append(" and a.acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        sql.append(" AND a.center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;

        if(date1!=null&&!"".equals(date1)){
          sql.append(" and a.voucher_date >= ?"+paramsNo);
          params.put(paramsNo,date1);
          paramsNo++;
       }
        if(date2!=null&&!"".equals(date2)){
            sql.append(" and a.voucher_date <= ?"+paramsNo );
            params.put(paramsNo,date2);
            paramsNo++;
        }
        sql.append(" AND a.voucher_no IN(\n" +
                "  SELECT DISTINCT  s.voucher_no FROM accsubvoucher s WHERE s.center_code=a.center_code and s.acc_book_code = a.acc_book_code \n" +
                "  AND s.year_month_date = a.year_month_date AND s.center_code=?"+paramsNo+" and  s.remark = CONCAT('提取',LEFT(s.year_month_date,4),'年',RIGHT(s.year_month_date,2),'月固定资产折旧'))");
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;

        //联查历史表数据
        String hisSql = sql.toString();
        hisSql = hisSql.replaceAll("accsubvoucher","accsubvoucherhis");
        hisSql = hisSql.replaceAll("s\\.","sh\\.");
        hisSql = hisSql.replaceAll(" s "," sh ");
        hisSql = hisSql.replaceAll("AccMainVoucher","AccMainVoucherHis");
        hisSql = hisSql.replaceAll("a\\.","ah\\.");
        hisSql = hisSql.replaceAll(" a "," ah ");

        sql.append(" union all ");
        sql.append(hisSql);

        sql.append(" ORDER BY yearMonthDate,voucherNo ");

        System.out.println(sql);
        List<?> list= accAssetSelectRepository.queryBySqlSC(sql.toString(),params);
        return list;
    }

    /**
     * 固定资产类别
     * @param value
     * @return
     */
   /* @Override
    public List<?> qryAssetTypeTree(String value){
        List resultListAll = new ArrayList();

        StringBuffer assetTypeSql = new StringBuffer("select asset_type as id, asset_complex_name as text, asset_type as mid from accassetcodetype where end_flag = '1'");
//        if(value!=null && !("").equals(value)){
//            assetTypeSql.append(" and asset_complex_name like '%"+value+"%'");
//        }
        List<?> assetTypeList = accAssetCodeTypeRepository.queryBySqlSC(assetTypeSql.toString());
        if(assetTypeList!=null&&assetTypeList.size()>0){
            for(Object o : assetTypeList){
                List resultList = new ArrayList();
                Map assetTypeMap = new HashMap();
                assetTypeMap.putAll((Map) o);
                List list = qryChildrenForTree((String)assetTypeMap.get("id"),value);
                if(!("").equals(value) && value!=null){
                    if(list.size() == 0 && !(assetTypeMap.get("text").toString().contains(value))){
                        continue;
                    }
                }
                assetTypeMap.put("id",assetTypeMap.get("mid"));
                assetTypeMap.put("text",assetTypeMap.get("text"));
                if(list.size() != 0){
                    assetTypeMap.put("children",list);
                    assetTypeMap.put("state","closed");
                }
                resultList.add(assetTypeMap);
                resultListAll.add(assetTypeMap);
            }
        }
        return resultListAll;
    }
*/
    /**
     * 判断当前卡片是否计提过
     * @param dto
     * @return
     */
    @Override
    public InvokeResult depreciation(AccAssetInfoDTO dto) {
        try{
            //判断当前卡片是否进行过计提
//            StringBuffer adSql = new StringBuffer();
//            adSql.append("select * from accdepre a where a.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  a.acc_book_type = '"+ dto.getAccBookType() +"' " +
//                    " and a.acc_book_code = '"+ dto.getAccBookCode() +"' and a.code_type = '"+dto.getCodeType()+"' " +
//                    " and a.asset_code = '"+ dto.getAssetCode() +"'");
//            List<?> adList = accAssetCodeTypeRepository.queryBySql(adSql.toString(), AccDepre.class);
            List<?> adList = accDepreRepository.queryAccDepreInfo(CurrentUser.getCurrentLoginManageBranch(),dto.getAccBookType(),dto.getAccBookCode(),dto.getCodeType(),dto.getAssetCode(),null);
            if(adList.size() > 0){//大于0 说明计提过折旧
                return InvokeResult.failure("当前卡片已进行过折旧，无法进行修改！");
            }
            return InvokeResult.success();
        }catch (Exception e){
            logger.error("固定资产卡片修改失败",e);
            return InvokeResult.failure("固定资产卡片修改失败，请联系管理员！");
        }
    }

    /*
     *//**
     * 固定资产编号生成
     * @param assetType
     * @return
     *//*
    @Override
    public String fCode(String assetType) {
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("select * from AccAssetInfo a where a.asset_type = '"+ assetType +"' and a.asset_code = " +
                    "(select MAX(asset_code) from AccAssetInfo where asset_type = '"+ assetType +"')");
            List<?> list = accAssetInfoRepository.queryBySql(sql.toString(), AccAssetInfo.class);
            String assetCode;//固定资产编号
            if(list.size()>0){//基本信息表中有当前类别
                assetCode = String.valueOf(Integer.parseInt(String.valueOf(((AccAssetInfo)list.get(0)).getAssetCode()))+1);//最大固定资产编号+1
            }else{//基本信息表中没有当前类别
                assetCode = assetType + "00001";
            }
            return assetCode;
        }catch (Exception e){
            logger.error("固定资产编号生成异常",e);
            return null;
        }
    }

    public List<MenuInfoDTO> qryChildrenForTree(String id, String value){
        List list1 = new ArrayList();
        StringBuffer sql = new StringBuffer("select asset_type as id, asset_complex_name as text, asset_type as mid from accassetcodetype where super_code = '"+ id +"'");
        if(value!=null && !("").equals(value)){
            sql.append(" and asset_complex_name like '%"+value+"%'");
        }
        List<?> list =  accAssetCodeTypeRepository.queryBySqlSC(sql.toString());
        if(list!=null && list.size()>0 && !list.isEmpty()){
            for(Object obj : list){
                Map map = new HashMap();
                map.putAll((Map) obj);

                List list2 = qryChildrenForTree((String)map.get("id"),value);
                if(list2!=null && list2.size()>0 && !list2.isEmpty()){
                    map.put("id",map.get("mid"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                    map.put("state","closed");
                }else{
                    map.put("id",map.get("mid"));
                    map.put("text",map.get("text"));
                }
                list1.add(map);
            }
        }
        return list1;
    }*/


}
