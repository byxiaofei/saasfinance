package com.sinosoft.service.impl.fixedassets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.controller.VoucherController;
import com.sinosoft.domain.account.*;
import com.sinosoft.domain.fixedassets.*;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.repository.CodeSelectRepository;
import com.sinosoft.repository.account.AccMainVoucherRespository;
import com.sinosoft.repository.account.AccSubVoucherRespository;
import com.sinosoft.repository.account.AccVoucherNoRespository;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.repository.fixedassets.AccAssetInfoChangeRepository;
import com.sinosoft.repository.fixedassets.AccGCheckInfoRepository;
import com.sinosoft.repository.fixedassets.ChangeManageRepository;
import com.sinosoft.service.fixedassets.CategoryCodingService;
import com.sinosoft.service.fixedassets.ChangeManageService;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhangst
 * @Description
 * @create
 * 固定资产卡片变动管理
 */
@Service
public class ChangeManageServiceImpl implements ChangeManageService {
    private Logger logger = LoggerFactory.getLogger(ChangeManageServiceImpl.class);
    @Value("${voucher.currency}")
    private String currency;
    @Resource
    private ChangeManageRepository changeManageRepository;
    @Resource
    private AccAssetInfoChangeRepository accAssetInfoChangeRepository;
    @Resource
    private AccAssetCodeTypeRepository accAssetCodeTypeRepository;
    @Resource
    private AccMainVoucherRespository accMainVoucherRespository;
    @Resource
    private AccSubVoucherRespository accSubVoucherRespository;
    @Resource
    private com.sinosoft.repository.account.AccVoucherNoRespository accVoucherNoRespository;
    @Resource
    private VoucherController voucherController;
    @Resource
    private CategoryCodingService categoryCodingService ;
    @Resource
    private ChangeManageService changeManageService;
    @Resource
    private CodeSelectRepository codeSelectRepository;
    @Resource
    private AccGCheckInfoRepository accGCheckInfoRepository;

    @Override
    public Page<?> qrychangeList(int page,int rows,AccAssetInfoDTO acc){
        List<?> res=changeManageService.getChangeManageList(acc);
        Page<?> result = categoryCodingService.getPage(page,rows,res);
        return result;
    }
    @Override
    public List<?> getChangeManageList(AccAssetInfoDTO acc){
        StringBuffer sql=new StringBuffer();
        sql.append("select f.center_code as centerCode ,f.asset_type as assetType,f.unit_code as unitCode1,f.organization as organization1, f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                "f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName, f.metric_name as metricName,\n" +
                "f.manufactor as manufactor,f.specification as specification,f.serial_number as serialNumber,f.use_start_date as useStartDate,f.quantity,"+
                "f.use_flag as useFlag ,f.storage_way as storageWay,f.asset_origin_value as assetOriginValue,f.asset_net_value as assetNetValue, f.dep_years as depYears,f.dep_type as depType,"+
                "f.impairment as impairment, f.added_tax as addedTax,f.sum,f.input_tax as inputTax,f.pay_way as payWay, f.pay_code as payCode,f.remains_value as remainsValue,f.remains_rate as remainsRate," +
                "f.predict_clear_fee as predictClearFee,f.formula_code as formulaCode,f.init_depre_amount as initDepreAmount,f.init_depre_money as initDepreMoney, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "f.clear_flag as clearFlag , f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch, f.clear_operator_code as clearOperatorCode,"+
                "f.voucher_no as voucherNo,f.depre_from_date as depreFromDate,f.depre_to_date as depreToDate,f.depre_flag as  depreFlag,f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp,"+
                "(select c.code_name from codemanage c where c.code_type='organization' and c.code_code=f.organization ) as organization,"+
                " (select c.code_name from codemanage c where c.code_type='deprMethod' and c.code_code=f.dep_type ) as deprMethod,"+
                "(select i.month_depre_money from  AccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code ORDER BY i.year_month_data desc limit 1 ) as monthDepreMoney ,");
        sql.append("( select c.change_old_data from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.asset_type=f.asset_type and c.handle_date =(select max(j.handle_date) from accAssetInfoChange j where j.center_code=f.center_code and j.branch_code=f.branch_code and j.acc_book_code=f.acc_book_code and j.acc_book_type=f.acc_book_type and c.asset_type=f.asset_type and c.code_type=f.code_type and j.asset_code=f.asset_code and j.card_code=f.card_code)) as changeOldData,");
        sql.append("( select c.change_new_data from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.asset_type=f.asset_type and c.handle_date =(select max(j.handle_date) from accAssetInfoChange j where j.center_code=f.center_code and j.branch_code=f.branch_code and j.acc_book_code=f.acc_book_code and j.acc_book_type=f.acc_book_type and c.asset_type=f.asset_type and c.code_type=f.code_type and j.asset_code=f.asset_code and j.card_code=f.card_code)) as changeNewData,");
        sql.append("(select o.special_name from specialinfo o where o.id=f.unit_code ) as unitCode,");
        sql.append("(select a.asset_simple_name from accassetcodetype a where a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetComplexName, ");
        sql.append("(select e.code_name from codemanage e where e.code_type='sourceFlag' and e.code_code=f.source_flag) as sourceFlag from AccAssetInfo  f where 1=1 ");

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
        //折旧至日期---------------------------
        if(acc.getDepreToDate()!=null&&!acc.getDepreToDate().equals("")){
            sql.append(" and f.depre_to_date<=?" + paramsNo);
            params.put(paramsNo,acc.getDepreToDate());
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

        //变更信息
        if(acc.getChangeMessage()!=null&&!acc.getChangeMessage().equals("")){
            if(acc.getChangeMessage().equals("1")){
                //无变更信息 exists
                sql.append(" and not exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }
//            if(acc.getChangeMessage().equals("0")){
//                //查询全部
//                sql.append(" and (not exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
//                if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
//                    sql.append(" or  exists(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_date='"+acc.getChangeDate()+"'))");
//                }
//                if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
//                    sql.append(" or exists(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_type='"+acc.getChangeType()+"')");
//                }
//                sql.append(")");
//            }
            if(acc.getChangeMessage().equals("2")){
                //有变更信息
                sql.append(" and  exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
//                if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
//                    sql.append(" and exists(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_date='"+acc.getChangeDate()+"')");
//                }
//                if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
//                    sql.append(" and exists(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_type='"+acc.getChangeType()+"')");
//
//                }
            }
        }
        if ( (acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")) || (acc.getChangeType()!=null&&!acc.getChangeType().equals("")) ){
            sql.append(" and f.card_code IN (select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type ");
            if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
                sql.append(" and c.change_date<=?" + paramsNo);
                params.put(paramsNo,acc.getChangeDate());
                paramsNo++;
            }
            if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
                sql.append(" and c.change_type=?" + paramsNo );
                params.put(paramsNo,acc.getChangeType());
                paramsNo++;
            }
            sql.append(")");
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

        if(acc.getIsNotVoucher()!=null&&!acc.getIsNotVoucher().equals("")){
            if(acc.getIsNotVoucher().equals("0")){
                //没有生成凭证
                sql.append(" and (f.voucher_no='' or f.voucher_no  is  null)");
                //  sql.append(" and f.depre_flag='Y' ");
            }else{
                sql.append(" and (f.voucher_no !='' and f.voucher_no is not null) ");

            }
        }

        if(acc.getCleanCard()!=null&&!acc.getCleanCard().equals("")){
            if(acc.getCleanCard().equals("1")){
                //不包含已清理卡片 未清理
                sql.append(" and f.clear_flag='0'");
            }else if(acc.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag='1'");

            }
        }
        if(acc.getCreateOper()!=null && !acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from userinfo where user_name like ?" + paramsNo+")");
            params.put(paramsNo,"%"+acc.getCreateOper()+"%");
            paramsNo++;
        }
        sql.append(" order by f.card_code asc");
        List<?> res=changeManageRepository.queryBySqlSC(sql.toString(),params);
        return res;
    }

    @Transactional
    @Override
    public String depChange(AccAssetInfoDTO acc){
        String accBookType = CurrentUser.getCurrentLoginAccountType();     //账套类型
        String accBookCode = CurrentUser.getCurrentLoginAccount();     //账套编码
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        int ss=0;
        //清理生效年月
        String clearYearDate="";
        if(acc.getChangeType().equals("01")){
            //部门变更
            if (acc.getChangeOldData1() == null || "null:".equals(acc.getChangeOldData1())){
                acc.setChangeOldData1("#:#");
            }
            ss= changeManageRepository.departChange(acc.getUnitCode(),centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());

        }else if(acc.getChangeType().equals("03")){
            //地点变更
            if (acc.getChangeOldData1() == null || "null:".equals(acc.getChangeOldData1())){
                acc.setChangeOldData1("#:#");
            }
            ss= changeManageRepository.AddressChange(acc.getOrganization(),centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());

        }else if(acc.getChangeType().equals("04")){
            //使用变动
            if (acc.getChangeOldData1() == null || "null:".equals(acc.getChangeOldData1())){
                acc.setChangeOldData1("#:#");
            }
            ss= changeManageRepository.useChange(acc.getUseFlag(),centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());

        }else if(acc.getChangeType().equals("05")){
           /* //判断卡片清理生效日期 会计期间已折旧或已结转不能生成在该会计期间所在月份
            //查找未结转 //2019-01-01
            //先查找固定会计期间表，查找该会计期间是否折旧
            String clearDate=acc.getClearYearMonth().substring(0,4)+acc.getClearYearMonth().substring(5,7);
            StringBuffer sqls3=new StringBuffer("select * from accgcheckinfo where acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and year_month_date='"+clearDate+"'");
            List<AccGCheckInfo> accgcheckinfoClearDate=(List<AccGCheckInfo>)accAssetInfoChangeRepository.queryBySql(sqls3.toString(), AccGCheckInfo.class);
            if(accgcheckinfoClearDate!=null&&accgcheckinfoClearDate.size()>0){
                //有数据
                if(accgcheckinfoClearDate.get(0).getFlag().equals("0")){
                    //未计提
                    clearYearDate=clearDate;
                }else{
                    return "clearYearDateFail";//清理生效日期不符合
                }
            }else{
                return "clearYearDateFail";//清理生效日期不符合
            }*/
            //卡片清理
            //清理操作人
            if (acc.getChangeOldData1() == null || "null:".equals(acc.getChangeOldData1())){
                acc.setChangeOldData1("#:#");
            }
            String operator=CurrentUser.getCurrentUser().getId()+"";
            ss= changeManageRepository.clearChange(acc.getClearYearMonth(), acc.getClearCode(), acc.getClearIncome(),acc.getClearIncomeTallage(), acc.getClearfee(),acc.getClearReason(),CurrentUser.getCurrentLoginManageBranch(),operator,"1",centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());

        }
        if(ss<1){
            return "fail";
        }
        //生成卡片变动单
        AccAssetInfoChangeId assId=new AccAssetInfoChangeId();
        assId.setAccBookCode(accBookCode);
        assId.setAccBookType(accBookType);
        assId.setBranchCode(branchCode);
        assId.setCenterCode(centerCode);
        //---序号
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy");
        String date=df1.format(new Date());
        String code1="";
        try {
            code1= accAssetInfoChangeRepository.getChangeCode(accBookType,accBookCode,CurrentUser.getCurrentLoginManageBranch());
        }catch (Exception e){
            e.printStackTrace();
        }
        String dateCode="";
        if(code1==null||code1==""){
            dateCode=date+"00000000001";
        }else{
            BigInteger b = new BigInteger(date+code1.substring(4));
            dateCode=(b.add(new BigInteger("1"))).toString();
        }
        assId.setChangeCode(dateCode);
        AccAssetInfoChange accage=new AccAssetInfoChange();
        accage.setId(assId);
        accage.setCodeType(acc.getCodeType());
        accage.setCardCode(acc.getCardCode());
        //获取类别编码数据
        String  acctype1= changeManageRepository.getAssetType(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());
        List<AccAssetCodeType>  acctype= accAssetCodeTypeRepository.getAssetType(accBookType,accBookCode,acc.getCodeType(),acctype1);
        AccAssetCodeType accTypeCode=acctype.get(0);
        accage.setAssetType(accTypeCode.getId().getAssetType());
        accage.setAssetCode(acc.getAssetCode()+"");
        accage.setChangeDate(acc.getChangeDate());
        accage.setChangeType(acc.getChangeType());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(acc.getChangeType().equals("01")){
            //变动类型存序号 01 部门变动
            accage.setChangeOldData(acc.getChangeOldData1());
            String unitcode= changeManageRepository.getSpecialName(acc.getUnitCode());
            accage.setChangeNewData(unitcode+":"+acc.getUnitCode());
        }else if(acc.getChangeType().equals("03")){
            //地点变更
            accage.setChangeOldData(acc.getChangeOldData1());
            String s1=changeManageRepository.getOrganization(acc.getOrganization());
            accage.setChangeNewData(s1+":"+acc.getOrganization());
        }else if(acc.getChangeType().equals("04")){
            //使用变更
            if(acc.getUseFlag().equals("1")){
                accage.setChangeOldData("停用:0");
                accage.setChangeNewData("使用:1");
            }else{
                accage.setChangeOldData("使用:1");
                accage.setChangeNewData("停用:0");
            }
        }else if(acc.getChangeType().equals("05")){
            //清理卡片
            if(acc.getChangeOldData1().equals("0")){
                accage.setChangeOldData("未清理:0");
                accage.setChangeNewData("已清理:1");
            }else{
                accage.setChangeOldData("未清理:0");
                accage.setChangeNewData("已清理:1");
            }
            SimpleDateFormat df12 = new SimpleDateFormat("yyyy-MM-dd");
            accage.setChangeDate(df12.format(new Date()));
        }

        accage.setChangeReason(acc.getChangeReason());
        if(acc.getChangeType().equals("05")){
            String clearReason="";
            if(acc.getClearCode().equals("1")){
                clearReason=acc.getClearReason();
            }else{
//                List<?> clearCodeman=changeManageRepository.queryBySql("select code_name from codemanage where code_code='"+acc.getClearCode()+"' and code_type='clearCode' ");
                List<?> clearCodeman = codeSelectRepository.findCodeNameByCodeCode(acc.getClearCode());
                clearReason=String.valueOf(clearCodeman.get(0));
            }

            accage.setChangeReason(clearReason);
        }
        //操作员单位
        accage.setOperatorBranch(CurrentUser.getCurrentLoginManageBranch());
        accage.setOperatorCode(CurrentUser.getCurrentUser().getId()+"");

        accage.setHandleDate(df.format(new Date()));
        accage.setTemp("");
        accAssetInfoChangeRepository.save(accage);
        //------------------卡片清理结束，变动单生成结束-------
        //-------------------凭证录入开始-----------------------
/*
             //判断类别表末级标志0
             if(acc.getChangeType().equals("05")&&accTypeCode.getEndFlag().equals("0")&&accTypeCode.getItemCode5()!=null){
                 //凭证号
                 String voucherNo="";
                 //年月
                 String yearMonthDate="";
                 //获取基本信息表所有
                 List<AccAssetInfo> accAssetinfoList=changeManageRepository.getAccAssetInfo(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());
                 AccAssetInfo  accAssetinfo=accAssetinfoList.get(0);
                 //
                 //if(accAssetinfo.getVoucherNo()==null||accAssetinfo.getVoucherNo().equals("")){
                     //卡片清理生成凭证
                     //获取年月和凭证号
                     //String useStartDate=accAssetinfo.getUseStartDate(); //2019-03-12
                     //yearMonthDate=useStartDate.substring(2,4)+useStartDate.substring(5,7);
                     //yearMonthDate=useStartDate.substring(0,4)+useStartDate.substring(5,7);
                 //获取凭证最大号表最大凭证日期
//                     List<Map<String,String>> list= changeManageRepository.getYearMonthDate1(centerCode,accBookType,accBookCode,yearMonthDate);
//                      //yearMonthDate=list.get(0).get("yearMonthDate");
//                      voucherNo=list.get(0).get("voucher_no");
//                     StringBuffer sqls1=new StringBuffer("select * from accvoucherno where acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' ORDER BY year_month_date DESC");
////                     AccVoucherNo accvoucherno=(AccVoucherNo)changeManageRepository.queryBySql(sqls1.toString(),AccVoucherNo.class).get(0);
                     StringBuffer sqls1=new StringBuffer("select * from accvoucherno where acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and  year_month_date='"+clearYearDate+"'");
                     AccVoucherNo accvoucherno=(AccVoucherNo)changeManageRepository.queryBySql(sqls1.toString(),AccVoucherNo.class).get(0);
                     yearMonthDate=accvoucherno.getId().getYearMonthDate();
                     voucherNo=accvoucherno.getVoucherNo();//8
                     String start="";
                     for(int i=0;i<5-voucherNo.length();i++){
                         start=start+"0";
                     }
                     voucherNo= yearMonthDate.substring(2)+start+voucherNo;

//                       int isvoucherNo=changeManageRepository.isvoucherNo(centerCode, branchCode, accBookType, accBookCode,voucherNo,yearMonthDate);
//                       if(isvoucherNo<1){
                 //--------------生成凭证主表--------------
                         AccMainVoucherId accvId=new AccMainVoucherId();
                         accvId.setCenterCode(centerCode);
                         accvId.setBranchCode(branchCode);
                         accvId.setAccBookCode(accBookCode);
                         accvId.setAccBookType(accBookType);
                         accvId.setYearMonthDate(yearMonthDate);
                         accvId.setVoucherNo(voucherNo);
                         AccMainVoucher accv=new AccMainVoucher();
                         accv.setId(accvId);
                         accv.setVoucherType("3");
                         accv.setGenerateWay("1");
                         SimpleDateFormat dfs= new SimpleDateFormat("yyyyMM");
                         SimpleDateFormat df2= new SimpleDateFormat("yyyy-MM-dd");
                         if(yearMonthDate.equals(dfs.format(new Date()))){
                             //会计期间当前月
                             accv.setVoucherDate(df2.format(new Date()));
                         }else{
                             //凭证日期
                             Calendar cal = Calendar.getInstance();
                             cal.set(Calendar.YEAR, Integer.parseInt(yearMonthDate.substring(0,4)));
                             cal.set(Calendar.MONTH, Integer.parseInt(yearMonthDate.substring(4))-1);
                             int lastDay = cal.getActualMaximum(Calendar.DATE);
                             cal.set(Calendar.DAY_OF_MONTH, lastDay);
                             accv.setVoucherDate(df2.format(cal.getTime()));
                         }


                         accv.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//制单人
                         accv.setCreateBranchCode(CurrentUser.getCurrentLoginManageBranch());
                         //生成凭证标志
                         accv.setVoucherFlag("1");
                         //创建时间
                         accv.setCreateTime(df.format(new Date()));
                         accMainVoucherRespository.save(accv);
                         //凭证最大号管理表凭证+1
                         accVoucherNoRespository.updateAddVoucherNo(yearMonthDate,accBookType,accBookCode);

                  //   }
                     //资产基本信息表添加此凭证号
                    // changeManageRepository.updateVoucherNo(voucherNo,centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());

//                 }else{
//                     //存在凭证号 获取年月 accAssetinfo.getVoucherNo()
//                     voucherNo=accAssetinfo.getVoucherNo();
//                     yearMonthDate=changeManageRepository.getVoucherYear(centerCode,branchCode,accBookType,accBookCode,voucherNo);
//
//                 }

      //-----===-------生成凭证子表--------===----
                 ////联合主键
                 AccSubVoucherId accSubVoucherId =new AccSubVoucherId();
                 accSubVoucherId.setBranchCode(branchCode);
                 accSubVoucherId.setCenterCode(centerCode);
                 accSubVoucherId.setAccBookCode(accBookCode);
                 accSubVoucherId.setAccBookType(accBookType);
                 accSubVoucherId.setYearMonthDate(yearMonthDate);
                 accSubVoucherId.setVoucherNo(voucherNo);
                 //查询凭证子表中该凭证最大分录号
                 Integer suffixNo=changeManageRepository.getSuffixNo(centerCode, branchCode, accBookType, accBookCode,voucherNo);
                 if(suffixNo==null){suffixNo=0;}
                 //开始循环录入凭证子表
                 for(int j=0;j<9;j++){
                     AccSubVoucher accsub=new AccSubVoucher();

                     //科目方向段
                     String itemcode="";
                     //专项方向段
                     String articleCode="";
                     if(j==0){
                         if(accAssetinfo.getAssetOriginValue()==null){
                             accAssetinfo.setAssetOriginValue(new BigDecimal("0"));
                         }
                         if(accAssetinfo.getEndDepreMoney()==null){
                             accAssetinfo.setEndDepreMoney(new BigDecimal("0"));
                         }
                         //借方-清理科目代码
                         //清理科目代码 全部科目段
                         itemcode=accTypeCode.getItemCode5();
                         articleCode=accTypeCode.getArticleCode5();
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(accAssetinfo.getAssetOriginValue().subtract(accAssetinfo.getEndDepreMoney()));//原币借方金额 固定资产清理费用金额（原值-累计折旧）
                         accsub.setCreditSource(new BigDecimal(0));//原币贷方金额
                         accsub.setDebitDest(accAssetinfo.getAssetOriginValue().subtract(accAssetinfo.getEndDepreMoney()));//本位币借方金额 值同原币
                         accsub.setCreditDest(new BigDecimal(0));//本位币贷方金额

                     }else if(j==1){
                         //借方-折旧贷方科目代码
                         itemcode=accTypeCode.getItemCode2();
                         articleCode=accTypeCode.getArticleCode2();
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(accAssetinfo.getEndDepreMoney().subtract(accAssetinfo.getInitDepreMoney()));//原币借方金额 固定资产累计折旧金额
                         accsub.setCreditSource(new BigDecimal(0));//原币贷方金额
                         accsub.setDebitDest(accAssetinfo.getEndDepreMoney());//本位币借方金额 值同原币
                         accsub.setCreditDest(new BigDecimal(0));//本位币贷方金额

                     }else if(j==2){
                         if(accAssetinfo.getAssetOriginValue()==null){
                             accAssetinfo.setAssetOriginValue(new BigDecimal("0"));
                         }
                         if(accAssetinfo.getEndDepreMoney()==null){
                             accAssetinfo.setEndDepreMoney(new BigDecimal("0"));
                         }
                         //贷方-资产科目代码
                         itemcode=accTypeCode.getItemCode1();
                         articleCode=accTypeCode.getArticleCode1();
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(new BigDecimal(0));//原币借方金额 固定资产原值）
                         accsub.setCreditSource(accAssetinfo.getAssetOriginValue());//原币贷方金额
                         accsub.setDebitDest(new BigDecimal(0));//本位币借方金额 值同原币
                         accsub.setCreditDest(accAssetinfo.getAssetOriginValue());//本位币贷方金额
                     }else if(j==3){
                         if(accAssetinfo.getAssetOriginValue()==null){
                             accAssetinfo.setAssetOriginValue(new BigDecimal("0"));
                         }
                         if(accAssetinfo.getEndDepreMoney()==null){
                             accAssetinfo.setEndDepreMoney(new BigDecimal("0"));
                         }
                         //清理收入 借
                         itemcode=accAssetinfo.getPayWay();//付款方式
                         articleCode=accAssetinfo.getPayCode();//付款专项
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(accAssetinfo.getClearIncome());//原币借方金额 清理收入
                         accsub.setCreditSource(new BigDecimal(0));//原币贷方金额
                         accsub.setDebitDest(accAssetinfo.getClearIncome());//本位币借方金额 值同原币
                         accsub.setCreditDest(new BigDecimal(0));//本位币贷方金额

                     }
                     else if(j==4){
                         //清理收入 贷 固定资产清理
                         itemcode=accTypeCode.getItemCode5();
                         articleCode=accTypeCode.getArticleCode5();
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(new BigDecimal(0));//原币借方金额 清理收入
                         accsub.setCreditSource(accAssetinfo.getClearIncome());//原币贷方金额
                         accsub.setDebitDest(new BigDecimal(0));//本位币借方金额 值同原币
                         accsub.setCreditDest(accAssetinfo.getClearIncome());//本位币贷方金额
                     }
                     else if(j==5){
                         //清理费用 借 固定资产清理
                         itemcode=accTypeCode.getItemCode5();
                         articleCode=accTypeCode.getArticleCode5();
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(accAssetinfo.getClearfee());//原币借方金额 清理费用
                         accsub.setCreditSource(new BigDecimal(0));//原币贷方金额
                         accsub.setDebitDest(accAssetinfo.getClearfee());//本位币借方金额 值同原币
                         accsub.setCreditDest(new BigDecimal(0));//本位币贷方金额
                     }
                     else if(j==6){
                         //清理费用 贷 付款方式
                         itemcode=accAssetinfo.getPayWay();//付款方式
                         articleCode=accAssetinfo.getPayCode();//付款专项
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(new BigDecimal(0));//原币借方金额 清理收入
                         accsub.setCreditSource(accAssetinfo.getClearfee());//原币贷方金额
                         accsub.setDebitDest(new BigDecimal(0));//本位币借方金额 值同原币
                         accsub.setCreditDest(accAssetinfo.getClearfee());//本位币贷方金额
                     }
                     else if(j==7){
                         //清理收入税额 借 付款方式
                         itemcode=accAssetinfo.getPayWay();//付款方式
                         articleCode=accAssetinfo.getPayCode();//付款专项
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(accAssetinfo.getClearIncomeTallage());//原币借方金额 清理收入税额
                         accsub.setCreditSource(new BigDecimal(0));//原币贷方金额
                         accsub.setDebitDest(accAssetinfo.getClearIncomeTallage());//本位币借方金额 值同原币
                         accsub.setCreditDest(new BigDecimal(0));//本位币贷方金额

                     }
                     else if(j==8){
                         //清理收入税额 贷 科目名称--《应交增值税》
                         //根据科目名称查询科目代码--可能有问题--不确定科目名称是否唯一
                         List<Map<String,String>> listMap=changeManageRepository.getsubjectCode( accBookCode,"应交增值税");
                         itemcode=listMap.get(0).get("allSubject")+listMap.get(0).get("subjectCode") ;
                         if(!listMap.get(0).get("specialId").equals("")&&listMap.get(0).get("specialId")!=null){
                             //查找专项代码
                             articleCode=changeManageRepository.getSpecialCode(accBookCode,listMap.get(0).get("specialId"));
                             if(articleCode==null){
                                 articleCode="";
                             }
                         }
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(new BigDecimal(0));//原币借方金额 清理收入税额
                         accsub.setCreditSource(accAssetinfo.getClearIncomeTallage());//原币贷方金额
                         accsub.setDebitDest(new BigDecimal(0));//本位币借方金额 值同原币
                         accsub.setCreditDest(accAssetinfo.getClearIncomeTallage());//本位币贷方金额


                     }
                     //金额为0或者为空 跳过循环 不生成凭证
                     if((accsub.getDebitSource()==null&&accsub.getCreditSource()==null)||(accsub.getDebitSource().compareTo(new BigDecimal("0"))==0&&accsub.getCreditSource().compareTo(new BigDecimal("0"))==0)){
                         continue;
                     }
                     if(itemcode!=null){
                         if(itemcode.charAt(itemcode.length()-1)!='/'){
                             itemcode=itemcode+"/";
                         }
                     }
                     //  //凭证相同 科目专项相同 累加金额 摘要不知道
                   Integer count= changeManageRepository.getVoucherSame( centerCode, branchCode, accBookType, accBookCode, yearMonthDate, voucherNo,itemcode,articleCode);
                    if(count>=1&&count!=null){

                         //changeManageRepository.updateVoucher(accsub.getDebitSource(),accsub.getCreditSource(),accsub.getDebitDest(),accsub.getDebitDest(),centerCode, branchCode, accBookType, accBookCode, yearMonthDate, voucherNo,itemcode,articleCode);
                        // continue;
                     }
                     suffixNo=suffixNo+1;
                     System.out.println(suffixNo+"-----");
                     accSubVoucherId.setSuffixNo(suffixNo+"");
                     accsub.setId(accSubVoucherId);
                     //分割科目代码
                     String[] itemCodeArr=itemcode.split("/");
                     //一级科目代码
                     accsub.setItemCode(itemCodeArr[0]);
                     //科目方向段
                     accsub.setDirectionIdx(itemcode);
                     //科目名称
                     String subName="";
                     String itemcodeChild="";
                     for (int i=0;i<itemCodeArr.length;i++){
                          itemcodeChild=itemcodeChild+itemCodeArr[i]+"/";
                         String subjectName= changeManageRepository.getSubjectName(itemcodeChild,accBookCode);
                         subName=subName+subjectName+"/";
                     }
                     accsub.setDirectionIdxName(subName);
                     //输入科目代码段f01-f15
                     if(itemCodeArr.length>=1){
                         accsub.setItemCode(itemCodeArr[0]);//科目代码
                         accsub.setF01(itemCodeArr[0]);
                     }
                     if(itemCodeArr.length>=2){accsub.setF02(itemCodeArr[1]);}
                     if(itemCodeArr.length>=3){accsub.setF03(itemCodeArr[2]);}
                     if(itemCodeArr.length>=4){accsub.setF04(itemCodeArr[3]);}
                     if(itemCodeArr.length>=5){accsub.setF05(itemCodeArr[4]);}
                     if(itemCodeArr.length>=6){accsub.setF06(itemCodeArr[5]);}
                     if(itemCodeArr.length>=7){accsub.setF07(itemCodeArr[6]);}
                     if(itemCodeArr.length>=8){accsub.setF08(itemCodeArr[7]);}
                     if(itemCodeArr.length>=9){accsub.setF09(itemCodeArr[8]);}
                     if(itemCodeArr.length>=10){accsub.setF10(itemCodeArr[9]);}
                     if(itemCodeArr.length>=11){accsub.setF11(itemCodeArr[10]);}
                     if(itemCodeArr.length>=12){accsub.setF12(itemCodeArr[11]);}
                     if(itemCodeArr.length>=13){accsub.setF13(itemCodeArr[12]);}
                     if(itemCodeArr.length>=14){accsub.setF14(itemCodeArr[13]);}
                     if(itemCodeArr.length>=15){accsub.setF15(itemCodeArr[14]);}
                     //专项方向段
                     accsub.setDirectionOther(articleCode);
                     if(articleCode!=null&&!articleCode.equals("")&&articleCode.length()!=1){
                         String[] direcList=articleCode.split(",");
                         for(int i=0;i<direcList.length;i++){
                             String sub=direcList[i].substring(0,2);
                             String segmentFlag=changeManageRepository.getSegmentFlag(sub);
                             if(segmentFlag!=null&&!segmentFlag.equals("")){
                                 if(segmentFlag.equals("s01")){accsub.setS01(direcList[i]);}
                                 if(segmentFlag.equals("s02")){accsub.setS02(direcList[i]);}
                                 if(segmentFlag.equals("s03")){accsub.setS03(direcList[i]);}
                                 if(segmentFlag.equals("s04")){accsub.setS04(direcList[i]);}
                                 if(segmentFlag.equals("s05")){accsub.setS05(direcList[i]);}
                                 if(segmentFlag.equals("s06")){accsub.setS06(direcList[i]);}
                                 if(segmentFlag.equals("s07")){accsub.setS07(direcList[i]);}
                                 if(segmentFlag.equals("s08")){accsub.setS08(direcList[i]);}
                                 if(segmentFlag.equals("s09")){accsub.setS09(direcList[i]);}
                                 if(segmentFlag.equals("s10")){accsub.setS10(direcList[i]);}
                                 if(segmentFlag.equals("s11")){accsub.setS11(direcList[i]);}
                                 if(segmentFlag.equals("s12")){accsub.setS12(direcList[i]);}
                                 if(segmentFlag.equals("s13")){accsub.setS13(direcList[i]);}
                                 if(segmentFlag.equals("s14")){accsub.setS14(direcList[i]);}
                                 if(segmentFlag.equals("s15")){accsub.setS15(direcList[i]);}
                                 if(segmentFlag.equals("s16")){accsub.setS16(direcList[i]);}
                                 if(segmentFlag.equals("s17")){accsub.setS17(direcList[i]);}
                                 if(segmentFlag.equals("s18")){accsub.setS18(direcList[i]);}
                                 if(segmentFlag.equals("s19")){accsub.setS19(direcList[i]);}
                                 if(segmentFlag.equals("s20")){accsub.setS20(direcList[i]);}
                             }
                         }
                     }
                     //录入摘要 不确定
                    // Integer remark=changeManageRepository.getReamrk( centerCode, accBookType, accBookCode,itemcode);
                   //  accsub.setRemark(remark==null?"":remark+"");
                     accsub.setRemark("");
                     //注意：标注 支票号没有录入
                     accsub.setCurrency(currency);//原币别编码
                     accsub.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//制单人
                     accsub.setCreateTime(df.format(new Date()));//创建时间
                     accSubVoucherRespository.save(accsub);
                     accSubVoucherRespository.flush();


                 }

             }
*/
        return "success";

    }
    @Transactional
    @Override
    public String typeChange(AccAssetInfoDTO acc){
        System.out.println(acc);
        String accBookType = CurrentUser.getCurrentLoginAccountType();     //账套类型
        String accBookCode = CurrentUser.getCurrentLoginAccount();     //账套编码
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        List<AccAssetCodeType> acctypeList= accAssetCodeTypeRepository.getAssetType(accBookType,accBookCode,acc.getCodeType(),acc.getAssetType());
        AccAssetCodeType  acctype=acctypeList.get(0);
        System.out.println(acctype);
        //查找固定资产类别编码
        String assetType=acc.getAssetType();
        BigDecimal depYears;
        //注意：--要是类别变动后，变动后的使用年限为空怎么办
        //查找固定资产卡片使用年限
        depYears=acctype.getDepYears();
        if(depYears==null){
            depYears=new BigDecimal("0");
        }
        //查找固定资产卡片净残值率
        BigDecimal netSurplusRate=acctype.getNetSurplusRate();
        if(netSurplusRate==null){
            netSurplusRate=new BigDecimal("0");
        }
        //查找固定资产卡片折旧方法
        String depType=acctype.getDepType();
        //变动后预计残值
        BigDecimal bignum1 = new BigDecimal(1);
        BigDecimal remainsValue=acc.getAssetOriginValue().multiply(bignum1.subtract(netSurplusRate));
        List<AccAssetInfo> accAssetinfoList=changeManageRepository.getAccAssetInfo(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());
        AccAssetInfo  accAssetinfo=accAssetinfoList.get(0);
        accAssetinfo.setAssetType(assetType);
        accAssetinfo.setDepYears(depYears);
        accAssetinfo.setDepType(depType);
        accAssetinfo.setRemainsRate(netSurplusRate);
        accAssetinfo.setRemainsValue(remainsValue);

        changeManageRepository.save(accAssetinfo);
        //生成卡片变动单
        AccAssetInfoChangeId assId=new AccAssetInfoChangeId();
        assId.setAccBookCode(accBookCode);
        assId.setAccBookType(accBookType);
        assId.setBranchCode(branchCode);
        assId.setCenterCode(centerCode);
        //---序号
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy");
        String date=df1.format(new Date());
        String code1="";
        try {
            code1= accAssetInfoChangeRepository.getChangeCode(accBookType,accBookCode,CurrentUser.getCurrentLoginManageBranch());
        }catch (Exception e){

        }
        String dateCode="";
        if(code1==null||code1==""){
            dateCode=date+"00000000001";
        }else{
            BigInteger b = new BigInteger(date+code1.substring(4));
            dateCode=(b.add(new BigInteger("1"))).toString();
        }
        assId.setChangeCode(dateCode);
        AccAssetInfoChange accage=new AccAssetInfoChange();
        accage.setId(assId);
        accage.setCodeType(acc.getCodeType());
        accage.setCardCode(acc.getCardCode());
        //  String asset= changeManageRepository.getAssetType(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());
        accage.setAssetType(assetType);
        accage.setAssetCode(acc.getAssetCode()+"");
        accage.setChangeDate(acc.getChangeDate());
        //变动类型存序号 02 类别变动
        accage.setChangeType(acc.getChangeType());
        accage.setChangeOldData(acc.getChangeOldData1());
        String asset= changeManageRepository.getAssetTypeName(accBookType,accBookCode,acc.getCodeType(),acc.getAssetType());
        accage.setChangeNewData(asset+":"+acc.getAssetType());
        accage.setChangeReason(acc.getChangeReason());
        //操作员单位
        accage.setOperatorBranch(CurrentUser.getCurrentLoginManageBranch());
        accage.setOperatorCode(CurrentUser.getCurrentUser().getId()+"");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        accage.setHandleDate(df.format(new Date()));
        accage.setTemp("");
        accAssetInfoChangeRepository.save(accage);
        return "success";

    }
    public  String getChangeCode(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        String date=df.format(new Date());
        String code1="";
        try {
            code1= accAssetInfoChangeRepository.getChangeCode(CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount(),CurrentUser.getCurrentLoginManageBranch());
        }catch (Exception e){
            e.printStackTrace();
        }
        String dateCode="";
        if(code1==null||code1==""){
            dateCode=date+"00000000001";
        }else{
            dateCode=(Integer.valueOf(date+code1.substring(4))+1)+"";
        }
        return dateCode;

    }
    @Override
    public List<?> AssetType(AccAssetInfoDTO acc){
        List<?> list=  changeManageRepository.AssetType(  acc.getAccBookType(), acc.getAccBookCode(), acc.getCodeType());

        return list;
    }
    @Transactional
    @Override
    public String cleanCard(AccAssetInfoDTO acc) {
        //判断卡片清理生效日期 会计期间已折旧或已结转不能生成在该会计期间所在月份
        //查找未结转 //2019-01-01
        //先查找固定会计期间表，查找该会计期间是否折旧
        String clearDate=acc.getClearYearMonth().substring(0,4)+acc.getClearYearMonth().substring(5,7);
//        StringBuffer sqls3=new StringBuffer("select * from accgcheckinfo where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and year_month_date='"+clearDate+"'");
//        List<AccGCheckInfo> accgcheckinfoClearDate=(List<AccGCheckInfo>)accAssetInfoChangeRepository.queryBySql(sqls3.toString(), AccGCheckInfo.class);
        List<AccGCheckInfo> accgcheckinfoClearDate=accGCheckInfoRepository.findAccgcheckinfoByCenterCodeAndAccBookCodeAndYearMonthDate(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),clearDate);
        if(accgcheckinfoClearDate!=null&&accgcheckinfoClearDate.size()>0){
            //有数据
            if(accgcheckinfoClearDate.get(0).getFlag().equals("0")){
                //未计提
                // clearYearDate=clearDate;
            }else{
                return "clearYearDateFail";//清理生效日期不符合
            }
        }else{
            return "clearYearDateFail";//清理生效日期不符合
        }

        AccAssetInfoDTO accAssetInfoDTO=new AccAssetInfoDTO();
        accAssetInfoDTO.setClearYearMonth(acc.getClearYearMonth());
        accAssetInfoDTO.setClearCode(acc.getClearCode());
        accAssetInfoDTO.setClearReason(acc.getClearReason());
        accAssetInfoDTO.setClearIncomeTallage(acc.getClearIncomeTallage());
        accAssetInfoDTO.setClearIncome(acc.getClearIncome());
        accAssetInfoDTO.setClearfee(acc.getClearfee());
        accAssetInfoDTO.setChangeType(acc.getChangeType());

        String data1 = acc.getData1();
        List<AccAssetInfoDTO> list1=null;
        try{
            list1= voucherController.readJson(data1,List.class,AccAssetInfoDTO.class);
        }catch(Exception e){
            logger.error("",e);
        }

        for (AccAssetInfoDTO acc1 : list1) {
            accAssetInfoDTO.setChangeOldData1(acc1.getChangeOldData1());
            accAssetInfoDTO.setCodeType(acc1.getCodeType());
            accAssetInfoDTO.setCardCode(acc1.getCardCode());
            accAssetInfoDTO.setAssetType(acc1.getAssetType());
            accAssetInfoDTO.setAssetCode(acc1.getAssetCode());
            String  clearYearDateFail=depChange(accAssetInfoDTO);
            if(clearYearDateFail.equals("fail")){
                return "fail";
            }
              /*  if(clearYearDateFail.equals("clearYearDateFail")){
                    return "clearYearDateFail";
                }*/
        }

        return "success";
    }
    @Transactional
    @Override
    public String useChange(AccAssetInfoDTO acc) {
        if(acc.getCardCode()!=null&&!acc.getCardCode().equals("")){
            String [] cardcodes=acc.getCardCode().split("/");
            for(int i=0;i<cardcodes.length;i++){
//                StringBuffer sqls1=new StringBuffer("select * from accassetinfo where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardcodes[i]+"' ");
                List<AccAssetInfo> accassetinfo=changeManageRepository.findAccAssetInfosByCenterCodeAndAccBookCodeAndCardCode(CurrentUser.getCurrentLoginManageBranch(), CurrentUser.getCurrentLoginAccount(),cardcodes[i]);
                if(accassetinfo.size()>0){
                    acc.setCardCode(accassetinfo.get(0).getId().getCardCode());
                    acc.setAssetCode(accassetinfo.get(0).getAssetCode());
                    acc.setAssetType(accassetinfo.get(0).getAssetType());
                    acc.setCodeType(accassetinfo.get(0).getId().getCodeType());
                    String usefalg=depChange(acc);
                    if(usefalg.equals("fail")){
                        return "fail";
                    }
                }
            }
        }

        return "success";
    }


    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols) {
        ExcelUtil excelUtil=new ExcelUtil();
        StringBuffer sql= new StringBuffer();
        sql.append("select f.center_code as centerCode ,f.asset_type as assetType,f.unit_code as unitCode1,f.organization as organization1, f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                "f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName, f.metric_name as metricName,\n" +
                "f.manufactor as manufactor,f.specification as specification,f.serial_number as serialNumber,f.use_start_date as useStartDate,f.quantity,"+
                "c.code_name as useFlag ,f.storage_way as storageWay,f.asset_origin_value as assetOriginValue,f.asset_net_value as assetNetValue, f.dep_years as depYears,f.dep_type as depType,"+
                "f.impairment as impairment, f.added_tax as addedTax,f.sum,f.input_tax as inputTax,f.pay_way as payWay, f.pay_code as payCode,f.remains_value as remainsValue,f.remains_rate as remainsRate," +
                "f.predict_clear_fee as predictClearFee,f.formula_code as formulaCode,f.init_depre_amount as initDepreAmount,f.init_depre_money as initDepreMoney, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "f.clear_flag as clearFlag , f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch, f.clear_operator_code as clearOperatorCode,"+
                "f.voucher_no as voucherNo,f.depre_from_date as depreFromDate,f.depre_to_date as depreToDate,f.depre_flag as  depreFlag,f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp,"+
                "(select c.code_name from codemanage c where c.code_type='organization' and c.code_code=f.organization ) as organization,"+
                " (select c.code_name from codemanage c where c.code_type='deprMethod' and c.code_code=f.dep_type ) as deprMethod,"+
                "(select i.month_depre_money from  AccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code ORDER BY i.year_month_data desc limit 1 ) as monthDepreMoney ,");
        sql.append("( select c.change_old_data from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.asset_type=f.asset_type and c.handle_date =(select max(j.handle_date) from accAssetInfoChange j where j.center_code=f.center_code and j.branch_code=f.branch_code and j.acc_book_code=f.acc_book_code and j.acc_book_type=f.acc_book_type and c.asset_type=f.asset_type and c.code_type=f.code_type and j.asset_code=f.asset_code and j.card_code=f.card_code)) as changeOldData,");
        sql.append("( select c.change_new_data from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.asset_type=f.asset_type and c.handle_date =(select max(j.handle_date) from accAssetInfoChange j where j.center_code=f.center_code and j.branch_code=f.branch_code and j.acc_book_code=f.acc_book_code and j.acc_book_type=f.acc_book_type and c.asset_type=f.asset_type and c.code_type=f.code_type and j.asset_code=f.asset_code and j.card_code=f.card_code)) as changeNewData,");
        sql.append("(select o.special_name from specialinfo o where o.id=f.unit_code ) as unitCode,");
        sql.append("(select a.asset_simple_name from accassetcodetype a where a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetComplexName, ");
        sql.append("(select e.code_name from codemanage e where e.code_type='sourceFlag' and e.code_code=f.source_flag) as sourceFlag from AccAssetInfo  f LEFT JOIN codemanage c ON c.code_type = 'useFlag' AND c.code_code = f.use_flag  where 1=1   ");

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



        AccAssetInfoDTO acc = new AccAssetInfoDTO();
        try {
            acc = new ObjectMapper().readValue(queryConditions, AccAssetInfoDTO.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        /*if(acc.getCardCode()!=null&&!acc.getCardCode().equals("")){
            if(acc.getCardCode().length()<5){
                String cs=acc.getCardCode();
                for(int i=0;i<5-acc.getCardCode().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode(cs);
            }
            sql.append(" and f.card_code>='"+acc.getCardCode()+"'");
        }
        if(acc.getCardCode1()!=null&&!acc.getCardCode1().equals("")){
            if(acc.getCardCode1().length()<5){
                String cs=acc.getCardCode1();
                for(int i=0;i<5-acc.getCardCode1().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode1(cs);
            }
            sql.append(" and f.card_code<='"+acc.getCardCode1()+"'");
        }
        if(acc.getAssetCode1()!=null&&!acc.getAssetCode1().equals("")){
            sql.append(" and f.asset_code>='"+acc.getAssetCode1()+"'");
        }
        if(acc.getAssetCode2()!=null&&!acc.getAssetCode2().equals("")){
            sql.append(" and f.asset_code<='"+acc.getAssetCode2()+"'");
        }
        if(acc.getUseStartDate()!=null&&!acc.getUseStartDate().equals("")){
            sql.append(" and f.use_start_date>='"+acc.getUseStartDate()+"'");
        }
        if(acc.getUseStartDate1()!=null&&!acc.getUseStartDate1().equals("")){
            sql.append(" and f.use_start_date<='"+acc.getUseStartDate1()+"'");
        }

        if(acc.getAssetOriginValue()!=null&&!acc.getAssetOriginValue().equals("")){
            sql.append(" and f.asset_origin_value>='"+acc.getAssetOriginValue()+"'");
        }
        if(acc.getAssetOriginValue1()!=null&&!acc.getAssetOriginValue1().equals("")){
            sql.append(" and f.asset_origin_value<='"+acc.getAssetOriginValue1()+"'");
        }
        if(acc.getAssetNetValue()!=null&&!acc.getAssetNetValue().equals("")){
            sql.append(" and f.asset_net_value>='"+acc.getAssetNetValue()+"'");
        }
        if(acc.getAssetNetValue1()!=null&&!acc.getAssetNetValue1().equals("")){
            sql.append(" and f.asset_net_value<='"+acc.getAssetNetValue1()+"'");
        }
        if(acc.getEndDepreMoney()!=null&&!acc.getEndDepreMoney().equals("")){
            sql.append(" and f.end_depre_money>='"+acc.getEndDepreMoney()+"'");
        }
        if(acc.getEndDepreMoney1()!=null&&!acc.getEndDepreMoney1().equals("")){
            sql.append(" and f.end_depre_money<='"+acc.getEndDepreMoney1()+"'");
        }
        //折旧至日期---------------------------
        if(acc.getDepreToDate()!=null&&!acc.getDepreToDate().equals("")){
            sql.append(" and f.depre_to_date<='"+acc.getDepreToDate()+"'");
        }
        if(acc.getDepYears()!=null&&!acc.getDepYears().equals("")){
            sql.append(" and f.dep_years>='"+acc.getDepYears()+"'");
        }
        if(acc.getDepYears1()!=null&&!acc.getDepYears1().equals("")){
            sql.append(" and f.dep_years<='"+acc.getDepYears1()+"'");
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
                if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
                    sql.append(" and exists(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_date='"+acc.getChangeDate()+"')");
                }
                if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
                    sql.append(" and exists(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_type='"+acc.getChangeType()+"')");

                }
            }
        }
        //使用部门unitCode
        if(acc.getUnitCode()!=null&&!acc.getUnitCode().equals("")){
            sql.append(" and f.unit_code='"+acc.getUnitCode()+"'");
        }
        if(acc.getAssetType()!=null&&!acc.getAssetType().equals("")){
            sql.append(" and f.asset_type='"+acc.getAssetType()+"'");
        }
        //sourceFlag
        if(acc.getSourceFlag()!=null&&!acc.getSourceFlag().equals("")){
            sql.append(" and f.source_flag='"+acc.getSourceFlag()+"'");
        }
        if(acc.getStopCard()!=null&&!acc.getStopCard().equals("")){
            if(acc.getStopCard().equals("0")){
                //不包括已停用卡片
                sql.append(" and f.use_flag='1' ");
            }
        }

        if(acc.getIsNotVoucher()!=null&&!acc.getIsNotVoucher().equals("")){
            if(acc.getIsNotVoucher().equals("0")){
                //没有生成凭证
                sql.append(" and (f.voucher_no='' or f.voucher_no  is  null)");
                //  sql.append(" and f.depre_flag='Y' ");
            }else{
                sql.append(" and (f.voucher_no !='' and f.voucher_no is not null) ");

            }
        }

        if(acc.getCleanCard()!=null&&!acc.getCleanCard().equals("")){
            if(acc.getCleanCard().equals("1")){
                //不包含已清理卡片 未清理
                sql.append(" and f.clear_flag='0'");
            }else if(acc.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag='1'");

            }
        }*/
        if(acc.getCardCode()!=null&&!acc.getCardCode().equals("")){
            if(acc.getCardCode().length()<5){
                String cs=acc.getCardCode();
                for(int i=0;i<5-acc.getCardCode().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode(cs);
            }
            sql.append(" and f.card_code>=?"+paramsNo);
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
            sql.append(" and f.card_code<=?"+paramsNo);
            params.put(paramsNo,acc.getCardCode1());
            paramsNo++;
        }
        if(acc.getAssetCode1()!=null&&!acc.getAssetCode1().equals("")){
            sql.append(" and f.asset_code>=?"+paramsNo);
            params.put(paramsNo,acc.getAssetCode1());
            paramsNo++;
        }
        if(acc.getAssetCode2()!=null&&!acc.getAssetCode2().equals("")){
            sql.append(" and f.asset_code<=?"+paramsNo);
            params.put(paramsNo,acc.getAssetCode2());
            paramsNo++;
        }
        if(acc.getUseStartDate()!=null&&!acc.getUseStartDate().equals("")){
            sql.append(" and f.use_start_date>=?"+paramsNo);
            params.put(paramsNo,acc.getUseStartDate());
            paramsNo++;
        }
        if(acc.getUseStartDate1()!=null&&!acc.getUseStartDate1().equals("")){
            sql.append(" and f.use_start_date<=?"+paramsNo);
            params.put(paramsNo,acc.getUseStartDate1());
            paramsNo++;
        }

        if(acc.getAssetOriginValue()!=null&&!acc.getAssetOriginValue().equals("")){
            sql.append(" and f.asset_origin_value>=?"+paramsNo);
            params.put(paramsNo,acc.getAssetOriginValue());
            paramsNo++;
        }
        if(acc.getAssetOriginValue1()!=null&&!acc.getAssetOriginValue1().equals("")){
            sql.append(" and f.asset_origin_value<=?"+paramsNo);
            params.put(paramsNo,acc.getAssetOriginValue1());
            paramsNo++;
        }
        if(acc.getAssetNetValue()!=null&&!acc.getAssetNetValue().equals("")){
            sql.append(" and f.asset_net_value>=?"+paramsNo);
            params.put(paramsNo,acc.getAssetNetValue());
            paramsNo++;
        }
        if(acc.getAssetNetValue1()!=null&&!acc.getAssetNetValue1().equals("")){
            sql.append(" and f.asset_net_value<=?"+paramsNo);
            params.put(paramsNo,acc.getAssetNetValue1());
            paramsNo++;
        }
        if(acc.getEndDepreMoney()!=null&&!acc.getEndDepreMoney().equals("")){
            sql.append(" and f.end_depre_money>=?"+paramsNo);
            params.put(paramsNo,acc.getEndDepreMoney());
            paramsNo++;
        }
        if(acc.getEndDepreMoney1()!=null&&!acc.getEndDepreMoney1().equals("")){
            sql.append(" and f.end_depre_money<=?"+paramsNo);
            params.put(paramsNo,acc.getEndDepreMoney1());
            paramsNo++;
        }
        //折旧至日期---------------------------
        if(acc.getDepreToDate()!=null&&!acc.getDepreToDate().equals("")){
            sql.append(" and f.depre_to_date<=?"+paramsNo);
            params.put(paramsNo,acc.getDepreToDate());
            paramsNo++;
        }
        if(acc.getDepYears()!=null&&!acc.getDepYears().equals("")){
            sql.append(" and f.dep_years>=?"+paramsNo);
            params.put(paramsNo,acc.getDepYears());
            paramsNo++;
        }
        if(acc.getDepYears1()!=null&&!acc.getDepYears1().equals("")){
            sql.append(" and f.dep_years<=?"+paramsNo);
            params.put(paramsNo,acc.getDepYears1());
            paramsNo++;
        }

        //变更信息
        if(acc.getChangeMessage()!=null&&!acc.getChangeMessage().equals("")){
            if(acc.getChangeMessage().equals("1")){
                //无变更信息 exists
                sql.append(" and not exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }
//            if(acc.getChangeMessage().equals("0")){
//                //查询全部
//                sql.append(" and (not exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
//                if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
//                    sql.append(" or  exists(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_date='"+acc.getChangeDate()+"'))");
//                }
//                if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
//                    sql.append(" or exists(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_type='"+acc.getChangeType()+"')");
//                }
//                sql.append(")");
//            }
            if(acc.getChangeMessage().equals("2")){
                //有变更信息
                sql.append(" and  exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
//                if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
//                    sql.append(" and exists(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_date='"+acc.getChangeDate()+"')");
//                }
//                if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
//                    sql.append(" and exists(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_type='"+acc.getChangeType()+"')");
//
//                }
            }
        }
        if ( (acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")) || (acc.getChangeType()!=null&&!acc.getChangeType().equals("")) ){
            sql.append(" and f.card_code IN (select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type ");
            if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
                sql.append(" and c.change_date<=?"+paramsNo);
                params.put(paramsNo,acc.getChangeDate());
                paramsNo++;
            }
            if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
                sql.append(" and c.change_type=?"+paramsNo);
                params.put(paramsNo,acc.getChangeType());
                paramsNo++;
            }
            sql.append(")");
        }



        //使用部门unitCode
        if(acc.getUnitCode()!=null&&!acc.getUnitCode().equals("")){
            sql.append(" and f.unit_code=?"+paramsNo);
            params.put(paramsNo,acc.getUnitCode());
            paramsNo++;
        }
        if(acc.getAssetType()!=null&&!acc.getAssetType().equals("")){
            sql.append(" and f.asset_type=?"+paramsNo);
            params.put(paramsNo,acc.getAssetType());
            paramsNo++;
        }
        //sourceFlag
        if(acc.getSourceFlag()!=null&&!acc.getSourceFlag().equals("")){
            sql.append(" and f.source_flag=?"+paramsNo);
            params.put(paramsNo,acc.getSourceFlag());
            paramsNo++;
        }
        if(acc.getStopCard()!=null&&!acc.getStopCard().equals("")){
            if(acc.getStopCard().equals("0")){
                //不包括已停用卡片
                sql.append(" and f.use_flag='1' ");
            }
        }

        if(acc.getIsNotVoucher()!=null&&!acc.getIsNotVoucher().equals("")){
            if(acc.getIsNotVoucher().equals("0")){
                //没有生成凭证
                sql.append(" and (f.voucher_no='' or f.voucher_no  is  null)");
                //  sql.append(" and f.depre_flag='Y' ");
            }else{
                sql.append(" and (f.voucher_no !='' and f.voucher_no is not null) ");

            }
        }

        if(acc.getCleanCard()!=null&&!acc.getCleanCard().equals("")){
            if(acc.getCleanCard().equals("1")){
                //不包含已清理卡片 未清理
                sql.append(" and f.clear_flag='0'");
            }else if(acc.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag='1'");

            }
        }
        if(acc.getCreateOper()!=null && !acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from userinfo where user_name like ?"+paramsNo+")");
            params.put(paramsNo,"%"+acc.getCreateOper()+"%");
            paramsNo++;
        }
        sql.append(" order by f.card_code asc");
        if(acc.getCreateOper()!=null && !acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from roleinfo where role_name like ?"+paramsNo+")");
            params.put(paramsNo,"%"+acc.getCreateOper()+"%");
            paramsNo++;
        }
        try {
            // 根据条件查询导出数据集
            List<?> dataList = changeManageRepository.queryBySqlSC(sql.toString(),params);
            // 导出
            excelUtil.exportu_(request, response, name, cols, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
