package com.sinosoft.service.impl.intangibleassets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.controller.VoucherController;
import com.sinosoft.domain.account.*;
import com.sinosoft.domain.fixedassets.AccAssetCodeType;
import com.sinosoft.domain.fixedassets.AccAssetInfo;
import com.sinosoft.domain.fixedassets.AccAssetInfoChange;
import com.sinosoft.domain.fixedassets.AccAssetInfoChangeId;
import com.sinosoft.domain.intangibleassets.*;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;
import com.sinosoft.repository.account.AccMainVoucherRespository;
import com.sinosoft.repository.account.AccSubVoucherRespository;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.repository.fixedassets.AccAssetInfoChangeRepository;
import com.sinosoft.repository.fixedassets.ChangeManageRepository;
import com.sinosoft.repository.intangibleassets.*;
import com.sinosoft.service.fixedassets.ChangeManageService;
import com.sinosoft.service.intangibleassets.IntangibleChangeManageService;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
 *//*
  * 无形资产卡片变动管理
 * */
@Service
public class IntangibleChangeManageServiceImpl implements IntangibleChangeManageService {
   private Logger logger = LoggerFactory.getLogger(IntangibleChangeManageServiceImpl.class);
    @Value("${voucher.currency}")
    private String currency;
    @Resource
    private IntangibleChangeManageRepository intangibleChangeManageRepository;
    @Resource
    private IntangibleAccAssetInfoChangeRepository intangibleAccAssetInfoChangeRepository;
    @Resource
    private IntangibleAccAssetCodeTypeRepository intangibleAccAssetCodeTypeRepository;
    @Resource
    private AccMainVoucherRespository accMainVoucherRespository;
    @Resource
    private AccSubVoucherRespository accSubVoucherRespository;

    @Resource
    private ChangeManageRepository changeManageRepository;
    @Resource
    private com.sinosoft.repository.account.AccVoucherNoRespository accVoucherNoRespository;
    @Resource
    private VoucherController voucherController;

    @Resource
    private AccWCheckInfoRepository accWCheckInfoRepository;
   /* @Override
    public List<?> qrychangeList(AccAssetInfoDTO acc){
        StringBuffer sql=new StringBuffer();
        sql.append("select f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                "f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName, f.metric_name as metricName,\n" +
                "f.manufactor as manufactor,f.specification as specification,f.serial_number as serialNumber,f.use_start_date as useStartDate,f.quantity,"+
                "f.use_flag as useFlag ,f.organization,f.storage_way as storageWay,f.asset_origin_value as assetOriginValue,f.asset_net_value as assetNetValue, f.dep_years as depYears,f.dep_type as depType,"+
                "f.impairment as impairment, f.added_tax as addedTax,f.sum,f.input_tax as inputTax,f.pay_way as payWay, f.pay_code as payCode,f.remains_value as remainsValue,f.remains_rate as remainsRate," +
                "f.predict_clear_fee as predictClearFee,f.formula_code as formulaCode,f.init_depre_amount as initDepreAmount,f.init_depre_money as initDepreMoney, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "f.clear_flag as clearFlag , f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch, f.clear_operator_code as clearOperatorCode,"+
                "f.voucher_no as voucherNo,f.depre_from_date as depreFromDate,f.depre_to_date as depreToDate,f.depre_flag as  depreFlag,f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp,"+
                "(select i.month_depre_money from  AccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code and i.year_month_data = (select max(t.year_month_data) from AccDepre t where left(t.year_month_data,7)=left( curdate(),7) and t.center_code=f.center_code and t.branch_code=f.branch_code and t.acc_book_type=f.acc_book_type and  t.acc_book_code=f.acc_book_code and t.asset_type=f.asset_type and t.code_type=f.code_type and f.asset_code=t.asset_code) ) as monthDepreMoney ,");
        sql.append("( select c.change_old_data from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.asset_type=f.asset_type and c.handle_date =(select max(j.handle_date) from accAssetInfoChange j where j.center_code=f.center_code and j.branch_code=f.branch_code and j.acc_book_code=f.acc_book_code and j.acc_book_type=f.acc_book_type and c.asset_type=f.asset_type and c.code_type=f.code_type and j.asset_code=f.asset_code and j.card_code=f.card_code)) as changeOldData,");
        sql.append("( select c.change_new_data from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.asset_type=f.asset_type and c.handle_date =(select max(j.handle_date) from accAssetInfoChange j where j.center_code=f.center_code and j.branch_code=f.branch_code and j.acc_book_code=f.acc_book_code and j.acc_book_type=f.acc_book_type and c.asset_type=f.asset_type and c.code_type=f.code_type and j.asset_code=f.asset_code and j.card_code=f.card_code)) as changeNewData,");
        sql.append("(select o.special_name from specialinfo o where o.id=f.unit_code ) as unitCode,");
     sql.append("(select a.asset_complex_name from accassetcodetype a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetType, ");
                sql.append("(select e.code_name from codemanage e where e.code_type='sourceFlag' and e.code_code=f.source_flag) as sourceFlag from AccAssetInfo  f where 1=1 ");


        if(acc.getCardCode()!=null&&!acc.getCardCode().equals("")){
            sql.append(" and f.card_code>='"+acc.getCardCode()+"'");
        }
        if(acc.getCardCode1()!=null&&!acc.getCardCode1().equals("")){
            sql.append(" and f.card_code<='"+acc.getCardCode1()+"'");
        }
        if(acc.getAssetCode1()!=null&&!acc.getAssetCode1().equals("")){
            sql.append(" and f.asset_code>='"+acc.getAssetCode()+"'");
        }
        if(acc.getAssetCode2()!=null&&!acc.getAssetCode2().equals("")){
            sql.append(" and f.asset_code<='"+acc.getAssetCode1()+"'");
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
        //只找计提过折旧或者生成凭证的
        if(acc.getIsNotVoucher()!=null&&!acc.getIsNotVoucher().equals("")){
            if(acc.getIsNotVoucher().equals("0")){
                //没有生成凭证
                sql.append(" and (f.voucher_no='' or f.voucher_no  is  null)");
                sql.append(" and f.depre_flag='Y' ");
            }else{
                sql.append(" and ((f.voucher_no !='' and f.voucher_no is not null) or f.depre_flag='Y') ");

            }
        }else{
            //凭证不知道
            sql.append(" and ((f.voucher_no !='' and f.voucher_no is not null) or f.depre_flag='Y') ");
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
        if(acc.getCreateOper()!=null&&!acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper='"+acc.getCreateOper()+"'");
        }
        return changeManageRepository.queryBySqlSC(sql.toString());
    }*/

    @Transactional
    @Override
    public String useflagChange(IntangibleAccAssetInfoDTO acc){
        String accBookType = CurrentUser.getCurrentLoginAccountType();     //账套类型
        String accBookCode = CurrentUser.getCurrentLoginAccount();     //账套编码
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        int ss=0;
        String clearYearDate="";
       if(acc.getChangeType().equals("02")){
            //使用变动
            ss= intangibleChangeManageRepository.useChange(acc.getUseFlag(),centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());

        }else if(acc.getChangeType().equals("03")){

            //卡片清理
           //清理操作人
           String operator=CurrentUser.getCurrentUser().getId()+"";
            ss= intangibleChangeManageRepository.clearChange(acc.getClearYearMonth(), acc.getClearCode(), acc.getClearfee(),acc.getClearReason(),CurrentUser.getCurrentLoginManageBranch(),operator,"1",centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());

        }
         if(ss<1){
              return "fail";
          }
        //生成卡片变动单
        IntangibleAccAssetInfoChangeId assId=new IntangibleAccAssetInfoChangeId();
        assId.setAccBookCode(accBookCode);
        assId.setAccBookType(accBookType);
        assId.setBranchCode(branchCode);
        assId.setCenterCode(centerCode);
        //---序号
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy");
        String date=df1.format(new Date());
        String code1="";
        try {
            code1= intangibleChangeManageRepository.getChangeCode(accBookType, accBookCode,CurrentUser.getCurrentLoginManageBranch());
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
        IntangibleAccAssetInfoChange accage=new IntangibleAccAssetInfoChange();
        accage.setId(assId);
        accage.setCodeType(acc.getCodeType());
        accage.setCardCode(acc.getCardCode());
        //获取类别编码数据
        String  acctype1= intangibleChangeManageRepository.getAssetType(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());
        List<IntangibleAccAssetCodeType>  acctype= intangibleAccAssetCodeTypeRepository.getAssetType(accBookType,accBookCode,acc.getCodeType(),acctype1);
        IntangibleAccAssetCodeType accTypeCode=acctype.get(0);
        accage.setAssetType(accTypeCode.getId().getAssetType());
        accage.setAssetCode(acc.getAssetCode()+"");
        accage.setChangeDate(acc.getChangeDate());
        accage.setChangeType(acc.getChangeType());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      /*  if(acc.getChangeType().equals("01")){
            //变动类型存序号 01 部门变动
            accage.setChangeOldData(acc.getChangeOldData1());
            String unitcode= changeManageRepository.getSpecialName(acc.getUnitCode());
            accage.setChangeNewData("部门变动："+unitcode);
        }else if(acc.getChangeType().equals("03")){
            //地点变更
            accage.setChangeOldData(acc.getChangeOldData1());
            accage.setChangeNewData("存放地点变动："+acc.getOrganization());
        }else */
      if(acc.getChangeType().equals("02")){
            //使用变更
            if(acc.getUseFlag().equals("1")){
                accage.setChangeOldData("0");
                accage.setChangeNewData("1");
            }else{
                accage.setChangeOldData("1");
                accage.setChangeNewData("0");
            }
        }else if(acc.getChangeType().equals("03")){
            //清理卡片
            accage.setChangeDate(acc.getClearYearMonth());
            accage.setChangeOldData("0");
            accage.setChangeNewData("1");
            SimpleDateFormat df12 = new SimpleDateFormat("yyyy-MM-dd");
            accage.setChangeDate(df12.format(new Date()));
        }
        accage.setChangeReason(acc.getChangeReason());
        if(acc.getChangeType().equals("03")){
            String clearReason="";
            if(acc.getClearCode().equals("1")){
                clearReason=acc.getClearReason();
            }else{
                StringBuffer sql = new StringBuffer();
                int invitsNo = 1;
                Map<Integer,Object> invits = new HashMap<>();
                sql.append("select code_name from codemanage where code_code=?"+invitsNo+" and code_type='clearCode' ");
                invits.put(invitsNo,acc.getClearCode());
//                List<?> clearCodeman=changeManageRepository.queryBySql("select code_name from codemanage where code_code='"+acc.getClearCode()+"' and code_type='clearCode' ");;
                List<?> clearCodeman=changeManageRepository.queryBySql(sql.toString(),invits);;
                clearReason=String.valueOf(clearCodeman.get(0));
            }

            accage.setChangeReason(clearReason);
        }
        //操作员单位
        accage.setOperatorBranch(CurrentUser.getCurrentLoginManageBranch());
        accage.setOperatorCode(CurrentUser.getCurrentUser().getId()+"");

        accage.setHandleDate(df.format(new Date()));
        accage.setTemp("");

        intangibleAccAssetInfoChangeRepository.save(accage);
 //------------------卡片清理结束，变动单生成结束-------
 //-------------------凭证录入开始-----------------------
/*
             //判断类别表末级标志0
             if(acc.getChangeType().equals("03")&&accTypeCode.getEndFlag().equals("0")&&accTypeCode.getItemCode5()!=null){
                 //凭证号
                 String voucherNo="";
                 //年月
                 String yearMonthDate="";
                 //获取基本信息表所有
                 List<IntangibleAccAssetInfo> accAssetinfoList=intangibleChangeManageRepository.getAccAssetInfo(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());
                 IntangibleAccAssetInfo  accAssetinfo=accAssetinfoList.get(0);
                // if(accAssetinfo.getVoucherNo()==null||accAssetinfo.getVoucherNo().equals("")){
                     //卡片清理生成凭证
                     //获取年月和凭证号
//                     String useStartDate=accAssetinfo.getUseStartDate(); //2019-03-12
//                     yearMonthDate=useStartDate.substring(0,4)+useStartDate.substring(5,7);
//                     List<Map<String,String>> list= intangibleChangeManageRepository.getYearMonthDate1(centerCode,accBookType,accBookCode,yearMonthDate);
//                     // yearMonthDate=list.get(0).get("yearMonthDate");
//                      voucherNo=list.get(0).get("voucher_no");
                 StringBuffer sqls1=new StringBuffer("select * from accvoucherno where acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' ORDER BY year_month_date DESC");
                 AccVoucherNo accvoucherno=(AccVoucherNo)changeManageRepository.queryBySql(sqls1.toString(),AccVoucherNo.class).get(0);
                 yearMonthDate=accvoucherno.getId().getYearMonthDate();
                 voucherNo=accvoucherno.getVoucherNo();//8
                     String start="";
                     for(int i=0;i<5-voucherNo.length();i++){
                         start=start+"0";
                     }
                 voucherNo= yearMonthDate.substring(2)+start+voucherNo;
                     //判断凭证主表是否已存在此凭证
//                     int isvoucherNo=intangibleChangeManageRepository.isvoucherNo(centerCode, branchCode, accBookType, accBookCode,voucherNo,yearMonthDate);
//                     if(isvoucherNo<1){
                         //生成凭证主表
                         AccMainVoucherId accvId=new AccMainVoucherId();
                         accvId.setCenterCode(centerCode);
                         accvId.setBranchCode(branchCode);
                         accvId.setAccBookCode(accBookCode);
                         accvId.setAccBookType(accBookType);
                         accvId.setYearMonthDate(yearMonthDate);
                         accvId.setVoucherNo(voucherNo);
                         AccMainVoucher accv=new AccMainVoucher();
                         accv.setId(accvId);
                         accv.setVoucherType("4");
                         accv.setGenerateWay("1");
                         //凭证日期
//                         SimpleDateFormat df2= new SimpleDateFormat("yyyy-MM-dd");
//                         accv.setVoucherDate(df2.format(new Date()));
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
                    // }
                     //资产基本信息表添加此凭证号
                    // intangibleChangeManageRepository.updateVoucherNo(voucherNo,centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());

//                 }else{
//                     //存在凭证号 获取年月 accAssetinfo.getVoucherNo()
//                     voucherNo=accAssetinfo.getVoucherNo();
//                     yearMonthDate=intangibleChangeManageRepository.getVoucherYear(centerCode,branchCode,accBookType,accBookCode,voucherNo);
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
                 Integer suffixNo=intangibleChangeManageRepository.getSuffixNo(centerCode, branchCode, accBookType, accBookCode,voucherNo);
              //  System.out.println(suffixNo+"-------------");
                 if(suffixNo==null){suffixNo=0;}
                 //开始循环录入凭证子表
                 for(int j=0;j<5;j++){
                     AccSubVoucher accsub=new AccSubVoucher();
//                     suffixNo=suffixNo+1;
//                  //   System.out.println(suffixNo+"=============");
//                     accSubVoucherId.setSuffixNo(suffixNo+"");
//                     accsub.setId(accSubVoucherId);
                     //科目方向段
                     String itemcode="";
                     //专项方向段
                     String articleCode="";
                     if(j==0){
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
                         accsub.setDebitSource(accAssetinfo.getEndDepreMoney());//原币借方金额 固定资产累计折旧金额
                         accsub.setCreditSource(new BigDecimal(0));//原币贷方金额
                         accsub.setDebitDest(accAssetinfo.getEndDepreMoney());//本位币借方金额 值同原币
                         accsub.setCreditDest(new BigDecimal(0));//本位币贷方金额

                     }else if(j==2){
                         //贷方-资产科目代码
                         itemcode=accTypeCode.getItemCode1();
                         articleCode=accTypeCode.getArticleCode1();
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(new BigDecimal(0));//原币借方金额 固定资产原值）
                         accsub.setCreditSource(accAssetinfo.getAssetOriginValue());//原币贷方金额
                         accsub.setDebitDest(new BigDecimal(0));//本位币借方金额 值同原币
                         accsub.setCreditDest(accAssetinfo.getAssetOriginValue());//本位币贷方金额
                     }
                     else if(j==3){
                         //清理费用 借 固定资产清理
                         itemcode=accTypeCode.getItemCode5();
                         articleCode=accTypeCode.getArticleCode5();
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(accAssetinfo.getClearfee());//原币借方金额 清理费用
                         accsub.setCreditSource(new BigDecimal(0));//原币贷方金额
                         accsub.setDebitDest(accAssetinfo.getClearfee());//本位币借方金额 值同原币
                         accsub.setCreditDest(new BigDecimal(0));//本位币贷方金额
                     }
                     else if(j==4){
                         //清理费用 贷 付款方式
                         itemcode=accAssetinfo.getPayWay();//付款方式
                         articleCode=accAssetinfo.getPayCode();//付款专项
                         accsub.setExchangeRate(new BigDecimal(1));//当前汇率 默认1
                         accsub.setDebitSource(new BigDecimal(0));//原币借方金额 清理收入
                         accsub.setCreditSource(accAssetinfo.getClearfee());//原币贷方金额
                         accsub.setDebitDest(new BigDecimal(0));//本位币借方金额 值同原币
                         accsub.setCreditDest(accAssetinfo.getClearfee());//本位币贷方金额
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
                     if(count!=null&&count>=1){
                         changeManageRepository.updateVoucher(accsub.getDebitSource(),accsub.getCreditSource(),accsub.getDebitDest(),accsub.getDebitDest(),centerCode, branchCode, accBookType, accBookCode, yearMonthDate, voucherNo,itemcode,articleCode);
                         continue;
                     }
                     suffixNo=suffixNo+1;
                    // System.out.println(suffixNo+"-----");
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
                         String subjectName= intangibleChangeManageRepository.getSubjectName(itemcodeChild,accBookCode);
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
                             String segmentFlag=intangibleChangeManageRepository.getSegmentFlag(sub);
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
                     //录入摘要
                     Integer remark=intangibleChangeManageRepository.getReamrk( centerCode, accBookType, accBookCode,itemcode);
                     accsub.setRemark(remark==null?"":remark+"");
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
    public String useChange(IntangibleAccAssetInfoDTO acc) {
        if(acc.getCardCode()!=null&&!acc.getCardCode().equals("")){
            String [] cardcodes=acc.getCardCode().split("/");
            for(int i=0;i<cardcodes.length;i++){
//                StringBuffer sqls1=new StringBuffer("select * from intangibleaccassetinfo where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardcodes[i]+"' ");
                StringBuffer sqls1 = new StringBuffer();
                int paramsNo = 1;
                Map<Integer,Object> params = new HashMap<>();
                sqls1.append("select * from intangibleaccassetinfo where center_code=?"+paramsNo);
                params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                paramsNo++;
                sqls1.append(" and acc_book_code=?"+paramsNo);
                params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                paramsNo++;
                sqls1.append(" and card_code=?"+paramsNo);
                params.put(paramsNo,cardcodes[i]);
                paramsNo++;
                List<IntangibleAccAssetInfo> accassetinfo=(List<IntangibleAccAssetInfo>)intangibleChangeManageRepository.queryBySql(sqls1.toString(),params,IntangibleAccAssetInfo.class);
                if(accassetinfo.size()>0){
                    acc.setCardCode(accassetinfo.get(0).getId().getCardCode());
                    acc.setAssetCode(accassetinfo.get(0).getAssetCode());
                    acc.setAssetType(accassetinfo.get(0).getAssetType());
                    acc.setCodeType(accassetinfo.get(0).getId().getCodeType());
                    String usefalg=useflagChange(acc);
                    if(usefalg.equals("fail")){
                        return "fail";
                    }
                }
            }
        }

        return "success";
    }

    @Transactional
    @Override
    public String cleanCard(IntangibleAccAssetInfoDTO inTangibleAcc) {
            //获取前台表单清理信息的数据
            IntangibleAccAssetInfoDTO intangibleAccAssetInfoDTO=new IntangibleAccAssetInfoDTO();
            intangibleAccAssetInfoDTO.setClearYearMonth(inTangibleAcc.getClearYearMonth());
            intangibleAccAssetInfoDTO.setClearCode(inTangibleAcc.getClearCode());
            intangibleAccAssetInfoDTO.setClearReason(inTangibleAcc.getClearReason());
            intangibleAccAssetInfoDTO.setClearfee(inTangibleAcc.getClearfee());
            intangibleAccAssetInfoDTO.setChangeType(inTangibleAcc.getChangeType());
            //获取清理勾选后前台卡片信息的字段
            String data1 = inTangibleAcc.getData1();
            //转为集合循环赋值，然后更新
            List<IntangibleAccAssetInfoDTO> list1=null;
        try{
            list1= voucherController.readJson(data1,List.class,IntangibleAccAssetInfoDTO.class);
        }catch(Exception e){
            logger.error("",e);
        }


        String clearDate=inTangibleAcc.getClearYearMonth().substring(0,4)+inTangibleAcc.getClearYearMonth().substring(5,7);
//        StringBuffer sqls3=new StringBuffer("select * from accwcheckinfo where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and year_month_date='"+clearDate+"'");
//        List<AccWCheckInfo> accgcheckinfoClearDate=(List<AccWCheckInfo>)intangibleChangeManageRepository.queryBySql(sqls3.toString(), AccWCheckInfo.class);
        List<AccWCheckInfo> accgcheckinfoClearDate=accWCheckInfoRepository.queryAccWCheckInfoByChooseMessage(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),clearDate);
        if(accgcheckinfoClearDate!=null&&accgcheckinfoClearDate.size()>0){
            //有数据
            if(accgcheckinfoClearDate.get(0).getFlag().equals("0")){
                //未计提
                //clearYearDate=clearDate;
            }else{
                return "clearYearDateFail";//清理生效日期不符合
            }
        }else{
            return "clearYearDateFail";//清理生效日期不符合
        }
        for (IntangibleAccAssetInfoDTO inTangibleAcc1 : list1) {
            intangibleAccAssetInfoDTO.setChangeOldData1(inTangibleAcc1.getChangeOldData1());
            intangibleAccAssetInfoDTO.setCodeType(inTangibleAcc1.getCodeType());
            intangibleAccAssetInfoDTO.setCardCode(inTangibleAcc1.getCardCode());
            intangibleAccAssetInfoDTO.setAssetType(inTangibleAcc1.getAssetType());
            intangibleAccAssetInfoDTO.setAssetCode(inTangibleAcc1.getAssetCode());
            String  clearYearDateFail=useflagChange(intangibleAccAssetInfoDTO);
            if(clearYearDateFail.equals("fail")){
                return "fail";
            }
               /* if(clearYearDateFail.equals("clearYearDateFail")){
                    return "clearYearDateFail";
                }*/
        }
        return "success";
    }

    @Transactional
    @Override
    public String typeChange(IntangibleAccAssetInfoDTO acc){
        String accBookType = CurrentUser.getCurrentLoginAccountType();     //账套类型
        String accBookCode = CurrentUser.getCurrentLoginAccount();     //账套编码
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        List<IntangibleAccAssetCodeType> acctypeList= intangibleAccAssetCodeTypeRepository.getAssetType(accBookType,accBookCode,acc.getCodeType(),acc.getAssetType());
        IntangibleAccAssetCodeType  acctype=acctypeList.get(0);
        //查找固定资产类别编码
        String assetType=acc.getAssetType();
        //查找固定资产卡片使用年限
        BigDecimal depYears=acc.getDepYears();
        if(depYears==null){
            depYears=new BigDecimal("0");
        }
        //查找固定资产卡片折旧方法
        String depType=acctype.getDepType();
        List<IntangibleAccAssetInfo> accAssetinfoList=intangibleChangeManageRepository.getAccAssetInfo(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());
        IntangibleAccAssetInfo  accAssetinfo=accAssetinfoList.get(0);
        //变动后预计残值
        BigDecimal remainsValue=accAssetinfo.getAssetOriginValue().multiply(acc.getRemainsRate());
          accAssetinfo.setAssetType(assetType);
          accAssetinfo.setDepYears(depYears);
          accAssetinfo.setDepType(depType);
          accAssetinfo.setRemainsRate(acc.getRemainsRate());
          accAssetinfo.setRemainsValue(remainsValue);

        intangibleChangeManageRepository.save(accAssetinfo);
        //生成卡片变动单
        IntangibleAccAssetInfoChangeId assId=new IntangibleAccAssetInfoChangeId();
        assId.setAccBookCode(accBookCode);
        assId.setAccBookType(accBookType);
        assId.setBranchCode(branchCode);
        assId.setCenterCode(centerCode);
        //---序号
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy");
        String date=df1.format(new Date());
        String code1="";
        try {
            code1= intangibleChangeManageRepository.getChangeCode(accBookType, accBookCode,CurrentUser.getCurrentLoginManageBranch());
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
        IntangibleAccAssetInfoChange accage=new IntangibleAccAssetInfoChange();
        accage.setId(assId);
        accage.setCodeType(acc.getCodeType());
        accage.setCardCode(acc.getCardCode());
       // String asset= changeManageRepository.getAssetType(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());
        accage.setAssetType(assetType);
        accage.setAssetCode(acc.getAssetCode()+"");
        accage.setChangeDate(acc.getChangeDate());
        //变动类型存序号 01 类别变动
        accage.setChangeType(acc.getChangeType());

        accage.setChangeOldData(acc.getChangeOldData1());
     //    acc.getDepYears() acc.getRemainsRate();
     //   StringBuffer sb=new StringBuffer();
       /* if(acc.getAssetType()!=acctype.getId().getAssetType()){
            sb.append("资产类别："+acc.getAssetType()+" ");
        }
       if(acc.getDepYears()!=acctype.getDepYears()){
            sb.append("摊销年限："+acc.getDepYears()+" ");
        }
        if(acc.getRemainsRate()!=accAssetinfo.getRemainsRate()){
            sb.append("预计残值率:"+acc.getRemainsRate());
        }*/
        accage.setChangeNewData(acc.getAssetType()+","+acc.getDepYears()+","+acc.getRemainsRate());
        accage.setChangeReason(acc.getChangeReason());
        //操作员单
        accage.setOperatorBranch(CurrentUser.getCurrentLoginManageBranch());
        accage.setOperatorCode(CurrentUser.getCurrentUser().getId()+"");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        accage.setHandleDate(df.format(new Date()));
        accage.setTemp("");
        intangibleAccAssetInfoChangeRepository.save(accage);
        return "success";

    }
   @Override
    public List<?> getAssetType(IntangibleAccAssetInfoDTO acc){
       List<?> list=  intangibleChangeManageRepository.AssetType( acc.getAccBookType(), acc.getAccBookCode(), acc.getCodeType());

       return list;
    }
    @Override
    public String getDepreYear(IntangibleAccAssetInfoDTO acc){
        String list=  intangibleChangeManageRepository.DepreYear( acc.getAccBookType(), acc.getAccBookCode(), acc.getCodeType(),acc.getAssetType());

        return list;
    }

    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols) {
        ExcelUtil excelUtil=new ExcelUtil();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql =new StringBuffer();
        sql.append("select f.depre_from_date as depreFromDate, f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                " f.asset_type as  assetType, f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName,f.depre_to_date as depreToDate, " +
                "f.impairment as impairment, f.depre_flag as  depreFlag, f.added_tax as  addedTax, f.input_tax as inputTax, f.sum, f.remains_rate as remainsRate, f.init_depre_amount as initDepreAmount, f.init_depre_money as initDepreMoney," +
                "(select c.code_name from codemanage c where c.code_code=f.use_flag and c.code_type='useFlag') as useFlag , f.asset_origin_value as assetOriginValue, f.asset_net_value as assetNetValue," +
                "f.dep_type as depType, "+
                "f.pay_way as payWay, f.pay_code as payCode, f.dep_years as depYears, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "  f.use_start_date as useStartDate,f.voucher_no as voucherNo, f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee," +
                "f.remains_value as remainsValue,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch,"+
                " f.clear_operator_code as clearOperatorCode," +
                "(select a.asset_simple_name from IntangibleAccAssetCodeType a where a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetTypeName,"+
                "(select a.special_name from specialinfo  a where a.account=f.acc_book_code and  a.special_code=(select e.article_code1 from intangibleaccassetcodetype e where  e.acc_book_code=f.acc_book_code and e.acc_book_type =f.acc_book_type and e.code_type=f.code_type and e.asset_type=f.asset_type)) as articleCode1,"+
                "( select c.change_new_data from IntangibleAccAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.asset_type=f.asset_type and c.handle_date =(select max(j.handle_date) from IntangibleAccAssetInfoChange j where j.center_code=f.center_code and j.branch_code=f.branch_code and j.acc_book_code=f.acc_book_code and j.acc_book_type=f.acc_book_type and c.asset_type=f.asset_type and c.code_type=f.code_type and j.asset_code=f.asset_code and j.card_code=f.card_code)) as changeNewData,"+
                "(select i.month_depre_money from intangibleAccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code ORDER BY i.year_month_data desc limit 1  ) as monthDepreMoney  ,f.clear_flag as clearFlag, f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp from IntangibleAccAssetInfo  f where 1=1 ");
        sql.append(" AND f.center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append(" AND f.acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        IntangibleAccAssetInfoDTO intang = new IntangibleAccAssetInfoDTO();
        try {
            intang = new ObjectMapper().readValue(queryConditions, IntangibleAccAssetInfoDTO.class);
        } catch (Exception e){
            e.printStackTrace();
        }
       /* if(intang.getCardCode()!=null&&!intang.getCardCode().equals("")){
            sql.append(" and f.card_code>='"+intang.getCardCode()+"'");
        }
        if(intang.getCardCode1()!=null&&!intang.getCardCode1().equals("")){
            sql.append(" and f.card_code<='"+intang.getCardCode1()+"'");
        }
        if(intang.getAssetCode()!=null&&!intang.getAssetCode().equals("")){
            sql.append(" and f.asset_code>='"+intang.getAssetCode()+"'");
        }
        if(intang.getAssetCode1()!=null&&!intang.getAssetCode1().equals("")){
            sql.append(" and f.asset_code<='"+intang.getAssetCode1()+"'");
        }
        if(intang.getUseStartDate()!=null&&!intang.getUseStartDate().equals("")){
            sql.append(" and f.use_start_date>='"+intang.getUseStartDate()+"'");
        }
        if(intang.getUseStartDate1()!=null&&!intang.getUseStartDate1().equals("")){
            sql.append(" and f.use_start_date<='"+intang.getUseStartDate1()+"'");
        }

        if(intang.getAssetOriginValue()!=null&&!intang.getAssetOriginValue().equals("")){
            sql.append(" and f.asset_origin_value>='"+intang.getAssetOriginValue()+"'");
        }
        if(intang.getAssetOriginValue1()!=null&&!intang.getAssetOriginValue1().equals("")){
            sql.append(" and f.asset_origin_value<='"+intang.getAssetOriginValue1()+"'");
        }
        if(intang.getAssetNetValue()!=null&&!intang.getAssetNetValue().equals("")){
            sql.append(" and f.asset_net_value>='"+intang.getAssetNetValue()+"'");
        }
        if(intang.getAssetNetValue1()!=null&&!intang.getAssetNetValue1().equals("")){
            sql.append(" and f.asset_net_value<='"+intang.getAssetNetValue1()+"'");
        }
        if(intang.getEndDepreMoney()!=null&&!intang.getEndDepreMoney().equals("")){
            sql.append(" and f.end_depre_money>='"+intang.getEndDepreMoney()+"'");
        }
        if(intang.getEndDepreMoney1()!=null&&!intang.getEndDepreMoney1().equals("")){
            sql.append(" and f.end_depre_money<='"+intang.getEndDepreMoney1()+"'");
        }
        if(intang.getDepYears()!=null&&!intang.getDepYears().equals("")){
            sql.append(" and f.dep_years>='"+intang.getDepYears()+"'");
        }
        if(intang.getDepYears1()!=null&&!intang.getDepYears1().equals("")){
            sql.append(" and f.dep_years<='"+intang.getDepYears1()+"'");
        }
        //摊销至日期------
        if(intang.getDepreUtilDate()!=null&&!intang.getDepreUtilDate().equals("")){
            sql.append(" and f.depre_to_date<='"+intang.getDepreUtilDate()+"' and f.use_start_date<='"+intang.getDepreToDate()+"'");
        }
        if(intang.getChangeMessage()!=null&&!intang.getChangeMessage().equals("")){
            if(intang.getChangeMessage().equals("0")){
                //全部 有变更信息判断
            }else if(intang.getChangeMessage().equals("1")){
                //无变更信息 exists
                sql.append(" and not exists(select c.change_code from intangibleaccassetinfochange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }else{
                //有变更信息
                sql.append(" and  exists(select c.change_code from intangibleaccassetinfochange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }
        }
        if(intang.getChangeDate()!=null&&!intang.getChangeDate().equals("")){
            sql.append(" and f.card_code in(select c.card_code from intangibleaccassetinfochange c where  c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_date<='"+intang.getChangeDate()+"')");
        }
        if(intang.getChangeType()!=null&&!intang.getChangeType().equals("")){
            sql.append(" and f.card_code in(select c.card_code from intangibleaccassetinfochange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_type='"+intang.getChangeType()+"')");

        }
        if(intang.getCreateOper()!=null && !intang.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from roleinfo where role_name like '%"+intang.getCreateOper()+"%')");
        }
        if(intang.getIsNotVoucher()!=null&&!intang.getIsNotVoucher().equals("")){
            if(intang.getIsNotVoucher().equals("0")){
                //没有生成凭证
                sql.append(" and (f.voucher_no='' or f.voucher_no  is  null)");
            }else{
                sql.append(" and f.voucher_no !='' and f.voucher_no is not null");
            }
        }

        if(intang.getStopCard()!=null&&!intang.getStopCard().equals("")){
            if(intang.getStopCard().equals("0")){
                //不包括已停用卡片
                sql.append(" and f.use_flag='1' ");
            }
        }

        if(intang.getCleanCard()!=null&&!intang.getCleanCard().equals("")){
            if(intang.getCleanCard().equals("1")){
                //不包含已清理卡片
                sql.append(" and f.clear_flag='0'");
            }else if(intang.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag='1'");
            }
        }*/
        if(intang.getCardCode()!=null&&!intang.getCardCode().equals("")){
            if(intang.getCardCode().length()<5){
                String cs=intang.getCardCode();
                for(int i=0;i<5-intang.getCardCode().length();i++){
                    cs="0"+cs;
                }
                intang.setCardCode(cs);
            }
            sql.append(" and f.card_code>=?"+paramsNo);
            params.put(paramsNo,intang.getCardCode());
            paramsNo++;
        }
        if(intang.getCardCode1()!=null&&!intang.getCardCode1().equals("")){
            if(intang.getCardCode1().length()<5){
                String cs=intang.getCardCode1();
                for(int i=0;i<5-intang.getCardCode1().length();i++){
                    cs="0"+cs;
                }
                intang.setCardCode1(cs);
            }
//            sql.append(" and f.card_code<='"+intang.getCardCode1()+"'");
            sql.append(" and f.card_code<=?"+paramsNo);
            params.put(paramsNo,intang.getCardCode1());
            paramsNo++;
        }
        if(intang.getAssetType()!=null&&!intang.getAssetType().equals("")){
            sql.append(" and f.asset_type like ?"+paramsNo);
            params.put(paramsNo,intang.getAssetType()+"%");
            paramsNo++;
        }
        if(intang.getAssetCode()!=null&&!intang.getAssetCode().equals("")){
            sql.append(" and f.asset_code>=?" + paramsNo);
            params.put(paramsNo,intang.getAssetCode());
            paramsNo++;
        }
        if(intang.getAssetCode1()!=null&&!intang.getAssetCode1().equals("")){
            sql.append(" and f.asset_code<=?" + paramsNo);
            params.put(paramsNo,intang.getAssetCode1());
            paramsNo++;
        }
        if(intang.getUseStartDate()!=null&&!intang.getUseStartDate().equals("")){
            sql.append(" and f.use_start_date>=?"+paramsNo);
            params.put(paramsNo,intang.getUseStartDate());
            paramsNo++;
        }
        if(intang.getUseStartDate1()!=null&&!intang.getUseStartDate1().equals("")){
            sql.append(" and f.use_start_date<=?"+paramsNo);
            params.put(paramsNo,intang.getUseStartDate1());
            paramsNo++;
        }

        if(intang.getAssetOriginValue()!=null&&!intang.getAssetOriginValue().equals("")){
            sql.append(" and f.asset_origin_value>=?"+paramsNo);
            params.put(paramsNo,intang.getAssetOriginValue());
            paramsNo++;
        }
        if(intang.getAssetOriginValue1()!=null&&!intang.getAssetOriginValue1().equals("")){
            sql.append(" and f.asset_origin_value<=?"+paramsNo);
            params.put(paramsNo,intang.getAssetOriginValue1());
            paramsNo++;
        }
        if(intang.getAssetNetValue()!=null&&!intang.getAssetNetValue().equals("")){
            sql.append(" and f.asset_net_value>=?"+paramsNo);
            params.put(paramsNo,intang.getAssetNetValue());
            paramsNo++;
        }
        if(intang.getAssetNetValue1()!=null&&!intang.getAssetNetValue1().equals("")){
            sql.append(" and f.asset_net_value<=?"+paramsNo);
            params.put(paramsNo,intang.getAssetNetValue1());
            paramsNo++;
        }
        if(intang.getEndDepreMoney()!=null&&!intang.getEndDepreMoney().equals("")){
            sql.append(" and f.end_depre_money>=?"+paramsNo);
            params.put(paramsNo,intang.getEndDepreMoney());
            paramsNo++;
        }
        if(intang.getEndDepreMoney1()!=null&&!intang.getEndDepreMoney1().equals("")){
            sql.append(" and f.end_depre_money<=?"+paramsNo);
            params.put(paramsNo,intang.getEndDepreMoney1());
            paramsNo++;
        }
        if(intang.getDepYears()!=null&&!intang.getDepYears().equals("")){
            sql.append(" and f.dep_years>=?"+paramsNo);
            params.put(paramsNo,intang.getDepYears());
            paramsNo++;
        }
        if(intang.getDepYears1()!=null&&!intang.getDepYears1().equals("")){
            sql.append(" and f.dep_years<=?"+paramsNo);
            params.put(paramsNo,intang.getDepYears1());
            paramsNo++;
        }
        //摊销至日期---------------------------
        if(intang.getDepreUtilDate()!=null&&!intang.getDepreUtilDate().equals("")){
            sql.append(" and f.depre_to_date<=?"+paramsNo);
            params.put(paramsNo,intang.getDepreUtilDate());
            paramsNo++;
            sql.append("  and f.use_start_date<=?"+paramsNo);
            params.put(paramsNo,intang.getDepreUtilDate());
            paramsNo++;
        }
        if(intang.getChangeMessage()!=null&&!intang.getChangeMessage().equals("")){
            if(intang.getChangeMessage().equals("0")){
                //全部 有变更信息判断
            }else if(intang.getChangeMessage().equals("1")){
                //无变更信息 exists
                sql.append(" and not exists(select c.change_code from intangibleaccassetinfochange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }else{
                //有变更信息
                sql.append(" and  exists(select c.change_code from intangibleaccassetinfochange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
            }
        }
        if ((intang.getChangeDate() != null&&!intang.getChangeDate().equals("")) || (intang.getChangeType()!=null&&!intang.getChangeType().equals("")) ){
            sql.append(" and f.card_code in (select c.card_code from intangibleaccassetinfochange c where  c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type ");
            if(intang.getChangeDate()!=null&&!intang.getChangeDate().equals("")){
                sql.append(" and c.change_date<=?"+paramsNo);
                params.put(paramsNo,intang.getChangeDate());
                paramsNo++;
            }
            if(intang.getChangeType()!=null&&!intang.getChangeType().equals("")){
                sql.append(" and  c.change_type=?"+paramsNo);
                params.put(paramsNo,intang.getChangeType());
                paramsNo++;
            }
            sql.append(")");
        }

        if(intang.getCreateOper()!=null && !intang.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from roleinfo where role_name like ?"+paramsNo+" )");
            params.put(paramsNo,"%"+intang.getCreateOper()+"%");
            paramsNo++;
        }
        if(intang.getIsNotVoucher()!=null&&!intang.getIsNotVoucher().equals("")){
            if(intang.getIsNotVoucher().equals("0")){
                //没有生成凭证
                sql.append(" and (f.voucher_no='' or f.voucher_no  is  null)");
            }else{
                sql.append(" and f.voucher_no !='' and f.voucher_no is not null");

            }
        }
        if(intang.getStopCard()!=null&&!intang.getStopCard().equals("")){
            if(intang.getStopCard().equals("0")){
                //不包括已停用卡片
                sql.append(" and f.use_flag='1' ");
            }
        }
        if(intang.getCleanCard()!=null&&!intang.getCleanCard().equals("")){
            if(intang.getCleanCard().equals("1")){
                //不包含已清理卡片
                sql.append(" and f.clear_flag='0'");
            }else if(intang.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag='1'");

            }
        }
        sql.append(" order by f.card_code asc");
        try {
             //根据条件查询导出数据集
            List<?> dataList = intangibleChangeManageRepository.queryBySqlSC(sql.toString(),params);
            // 导出
            excelUtil.exportu_intangcard(request, response, name, cols, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
