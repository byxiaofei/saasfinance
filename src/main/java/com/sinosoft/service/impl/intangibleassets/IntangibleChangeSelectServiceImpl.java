package com.sinosoft.service.impl.intangibleassets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfo;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfoChange;
import com.sinosoft.domain.intangibleassets.IntangibleAccDepre;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoChangeDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetCodeTypeRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetInfoChangeRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetInfoRepository;
import com.sinosoft.repository.intangibleassets.IntangibleChangeManageRepository;
import com.sinosoft.service.fixedassets.CategoryCodingService;
import com.sinosoft.service.intangibleassets.IntangibleChangeSelectService;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangst
 * @Description
 * @create
 * 无形资产卡片变动管理
 */
@Service
public class IntangibleChangeSelectServiceImpl implements IntangibleChangeSelectService {
   private Logger logger = LoggerFactory.getLogger(IntangibleChangeSelectServiceImpl.class);
    @Value("${voucher.currency}")
    private String currency;

    @Resource
    private IntangibleChangeManageRepository intangibleChangeManageRepository;
    @Resource
    private IntangibleAccAssetInfoChangeRepository intangibleAccAssetInfoChangeRepository;
    @Resource
    private IntangibleAccAssetCodeTypeRepository intangibleAccAssetCodeTypeRepository;

    @Resource
    private CategoryCodingService categoryCodingService ;



    @Override
    public Page<?> qrychangeList(int page, int rows, IntangibleAccAssetInfoChangeDTO acc){
        StringBuffer sql=new StringBuffer();
        sql.append("select f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,f.change_code as changeCode,"+
                "f.card_code as cardCode, f.asset_code as assetCode,f.asset_type as assetType,f.change_date as changeDate, f.change_type as changeType,"+
                "f.change_old_data as changeOldData ,f.change_new_data as changeNewData,f.change_reason as changeReason,f.operator_branch as operatorBranch, f.operator_code as operatorCode,f.handle_date as handleDate,f.temp as temp,"+
                " (select c.code_name from codemanage c where c.code_type='changeType' and c.code_code=f.change_type) as changeTypeName,"+
                "(select a.use_flag from IntangibleAccAssetInfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code) as useFlag,"+
                "(select c.code_name from codemanage c where c.code_type='deprMethod' and c.code_code=(select a.dep_type from IntangibleAccAssetInfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code)) as depTypeName,"+
                "(select a.clear_flag from IntangibleAccAssetInfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code) as clearFlag,"+
                "(select a.use_start_date from IntangibleAccAssetInfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code) as useStartDate,"+
                "(select s.user_name from userinfo s where s.id=f.operator_code) as operator,"+
                "(select a.asset_name from IntangibleAccAssetInfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code) as assetName from IntangibleAccAssetInfoChange  f where 1=1 ");

        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        sql.append(" AND f.center_code = ?"+paramsNo );
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append(" AND f.acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
//                "(select c.code_name from codemanage c where c.code_type='organization' and c.code_code=(select a.organization from accassetinfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code)) as organization,"+
        //                "(select a.unit_code from IntangibleAccAssetInfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code) as unitCode,"+
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
        if(acc.getAssetType()!=null&&!acc.getAssetType().equals("")){
            sql.append(" and f.asset_type like ?"+ paramsNo);
            params.put(paramsNo,acc.getAssetType()+"%");
            paramsNo++;
        }
        if(acc.getAssetCode()!=null&&!acc.getAssetCode().equals("")){
            sql.append(" and f.asset_code>=?" + paramsNo);
            params.put(paramsNo,acc.getAssetCode());
            paramsNo++;
        }
        if(acc.getAssetCode1()!=null&&!acc.getAssetCode1().equals("")){
            sql.append(" and f.asset_code<=?" + paramsNo);
            params.put(paramsNo,acc.getAssetCode1());
            paramsNo++;
        }
        if(acc.getUseStartDate()!=null&&!acc.getUseStartDate().equals("")){
            sql.append(" and f.card_code in(select a.card_code from IntangibleAccAssetInfo a where a.center_code=?"+CurrentUser.getCurrentLoginManageBranch());
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append(" and  a.acc_book_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append(" and a.use_start_date>=?"+paramsNo+")");
            params.put(paramsNo,acc.getUseStartDate());
            paramsNo++;
        }
        if(acc.getUseStartDate1()!=null&&!acc.getUseStartDate1().equals("")){
            sql.append(" and f.card_code in(select a.card_code from IntangibleAccAssetInfo a where a.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  a.acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and a.use_start_date<='"+acc.getUseStartDate1()+"') ");
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append(" and  a.acc_book_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append(" and a.use_start_date<=?"+paramsNo+" )");
            params.put(paramsNo,acc.getUseStartDate1());
            paramsNo++;
        }
//        if(acc.getChangeCode()!=null&&!acc.getChangeCode().equals("")){
//            sql.append(" and f.change_code>='"+acc.getChangeCode()+"'");
//        }
//        if(acc.getChangeCode1()!=null&&!acc.getChangeCode1().equals("")){
//            sql.append(" and f.change_code<='"+acc.getChangeCode1()+"'");
//        }
        //变动单号正好15位不用拼接
        if(acc.getChangeCode()!=null&&!"".equals(acc.getChangeCode())&&acc.getChangeCode().length()==15){
//            sql.append(" and f.change_code>='"+acc.getChangeCode()+"'");
            sql.append(" and f.change_code>=?"+paramsNo);
            params.put(paramsNo,acc.getChangeCode());
            paramsNo++;
        }
        if(acc.getChangeCode1()!=null&&!"".equals(acc.getChangeCode1())&&acc.getChangeCode1().length()==15){
            sql.append(" and f.change_code<=?"+paramsNo);
            params.put(paramsNo,acc.getChangeCode1());
            paramsNo++;
        }
        //变动单号小于15位需要和查询年份拼接
        if(acc.getChangeCode()!=null&&!"".equals(acc.getChangeCode())&&acc.getChangeCode().length()<15){
            sql.append(" and f.change_code>=?"+paramsNo);
            params.put(paramsNo,acc.getSelectIntangibleYear()+String.format("%011d", Integer.parseInt(acc.getChangeCode())));
            paramsNo++;
        }
        if(acc.getChangeCode1()!=null&&!"".equals(acc.getChangeCode1())&&acc.getCardCode1().length()<15){
            sql.append(" and f.change_code<=?"+ paramsNo);
            params.put(paramsNo,acc.getSelectIntangibleYear()+String.format("%011d", Integer.parseInt(acc.getChangeCode1())));
            paramsNo++;
        }
        //当变动单号未输入时
        if(acc.getSelectIntangibleYear()!=null&&!"".equals(acc.getSelectIntangibleYear())){
            sql.append(" and f.change_code like ?"+paramsNo);
            params.put(paramsNo,"%"+acc.getSelectIntangibleYear()+"%");
            paramsNo++;
        }
        if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
            sql.append(" and f.change_date>=?"+paramsNo);
            params.put(paramsNo,acc.getChangeDate());
            paramsNo++;
        }
        if(acc.getChangeDate1()!=null&&!acc.getChangeDate1().equals("")){
            sql.append(" and f.change_date<=?"+paramsNo);
            params.put(paramsNo,acc.getChangeDate1());
            paramsNo++;
        }
/*
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
        }*/
       /* //使用部门unitCode
        if(acc.getUnitCode()!=null&&!acc.getUnitCode().equals("")){
            sql.append(" and f.card_code in(select a.card_code from AccAssetInfo a where a.unit_code='"+acc.getUnitCode()+"') ");

        }
        if(acc.getOrganization()!=null&&!acc.getOrganization().equals("")){
            sql.append(" and f.card_code in(select a.card_code from AccAssetInfo a where a.organization='"+acc.getOrganization()+"') ");

        }*/
        if(acc.getAssetType()!=null&&!acc.getAssetType().equals("")){
            sql.append(" and f.asset_type=?"+paramsNo);
            params.put(paramsNo,acc.getAssetType());
            paramsNo++;
        }

        if(acc.getOperatorCode()!=null&&!acc.getOperatorCode().equals("")){
          //  sql.append(" and f.operator_code='"+acc.getOperatorCode()+"'");
            sql.append(" and f.operator_code in (select id from roleinfo where role_name like ?"+acc.getOperatorCode()+" )");
            params.put(paramsNo,"%"+acc.getOperatorCode()+"%");
            paramsNo++;

        }

        if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
            sql.append(" and f.change_type=?"+paramsNo);
            params.put(paramsNo,acc.getChangeType());
            paramsNo++;
        }
     List<?> list=   intangibleAccAssetInfoChangeRepository.queryBySqlSC(sql.toString(),params);
        List list1=new ArrayList();
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map = new HashMap();
                map.putAll((Map) obj);
                String changOldDataName="";
                String changNewDataName="";
                if(map.get("changeType").equals("01")){
                    //类别changeTypeName
                    map.put("changeTypeName","摊销信息变动");
                    //变动前
                    String changeData=String.valueOf(map.get("changeOldData"));
                    String [] arr=changeData.split(",");
                    //变动后
                    String changeData1=String.valueOf(map.get("changeNewData"));
                    String [] arr1=changeData1.split(",");
                    if(!arr[0].equals(arr1[0])){
                        String assetTypeName= intangibleChangeManageRepository.getAssetName(String.valueOf(map.get("accBookType")),String.valueOf(map.get("accBookCode")),String.valueOf(map.get("codeType")),arr[0]);
                        changOldDataName="类别:"+assetTypeName;
                        String assetTypeName1= intangibleChangeManageRepository.getAssetName(String.valueOf(map.get("accBookType")),String.valueOf(map.get("accBookCode")),String.valueOf(map.get("codeType")),arr1[0]);
                        changNewDataName="类别:"+assetTypeName1;
                    }
                    if(!arr[1].equals(arr1[1])){
                        changOldDataName=changOldDataName+" 摊销年限:"+arr[1];
                        changNewDataName=changNewDataName+" 摊销年限:"+arr1[1];
                    }
                    if(!arr[2].equals(arr1[2])){
                        changOldDataName=changOldDataName+" 预计残值率:"+arr[2];
                        changNewDataName=changNewDataName+" 预计残值率:"+arr1[2];
                    }
                }else if(map.get("changeType").equals("02")){
                    //使用状态
                    map.put("changeTypeName","使用状态变动");
                    if(String.valueOf(map.get("changeOldData")).trim().equals("0")){
                        changOldDataName="停用";
                        changNewDataName="使用";
                    }else{
                        changOldDataName="使用";
                        changNewDataName="停用";
                    }

                }else if(map.get("changeType").equals("03")){
                    //清理
                    map.put("changeTypeName","卡片清理");
                    if(String.valueOf(map.get("changeOldData")).trim().equals("0")){
                        changOldDataName="未清理";
                        changNewDataName="已清理";
                    }else{
                        changOldDataName="已清理";
                        changNewDataName="未清理";
                    }
                }
                map.put("changeOldDataName",changOldDataName);
                map.put("changeNewDataName",changNewDataName);
                list1.add(map);

            }
        }
        Page<?> result = categoryCodingService.getPage(page,rows,list1);
        return result;

    }

   @Transactional
    @Override
    public InvokeResult revoke(String changeCodes){
        String accBookType = CurrentUser.getCurrentLoginAccountType();     //账套类型
        String accBookCode = CurrentUser.getCurrentLoginAccount();     //账套编码
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        int invitsNo = 1;
        Map<Integer,Object> invits = new HashMap<>();
        StringBuffer sqls=new StringBuffer();
        sqls.append(" select * from intangibleaccassetinfochange  where  1=1  and center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and  change_code in ("+changeCodes+") ");
        sqls.append(" and acc_book_code = ?"+invitsNo);
        invits.put(invitsNo,CurrentUser.getCurrentLoginAccount());
        invitsNo++;
        sqls.append(" and  change_code in ("+invitsNo+" )");
        invits.put(invitsNo,changeCodes);
        invitsNo++;
        List<IntangibleAccAssetInfoChange> accs= (List<IntangibleAccAssetInfoChange>)intangibleChangeManageRepository.queryBySql(sqls.toString(),invits ,IntangibleAccAssetInfoChange.class);
        for(IntangibleAccAssetInfoChange acca:accs){
            if(acca.getChangeType().equals("01")){
                String [] arr=acca.getChangeOldData().split(",");
                //类别
                String type=arr[0].trim();
                //摊销年限
                String depYears=arr[1].trim();
                //预计残值率
                String remainsRate=arr[2].trim();
                //类别变动
                List<IntangibleAccAssetInfo> accAssetinfoList=intangibleChangeManageRepository.getAccAssetInfo(centerCode,branchCode,accBookType,accBookCode,acca.getCodeType(),acca.getCardCode());
                IntangibleAccAssetInfo  accAssetinfo=accAssetinfoList.get(0);
                //摊销方法
                String depType=intangibleAccAssetInfoChangeRepository.getAccAssetInfo(accBookType,accBookCode,acca.getCodeType(),type);
                //预计残值
                BigDecimal originvalue=accAssetinfo.getAssetOriginValue().multiply(new BigDecimal(remainsRate));
                accAssetinfo.setDepYears(new BigDecimal(depYears));
                accAssetinfo.setDepType(depType);
                accAssetinfo.setAssetType(type);
                accAssetinfo.setRemainsRate(new BigDecimal(remainsRate));
                accAssetinfo.setRemainsValue(originvalue);
                intangibleChangeManageRepository.save(accAssetinfo);
            }else if(acca.getChangeType().equals("02")){
                //使用状态变动
                intangibleAccAssetInfoChangeRepository.updateUseFlag(acca.getChangeOldData(),centerCode,  branchCode,  accBookType,  accBookCode, acca.getCodeType(),acca.getCardCode());
            }
            //删除卡片变动单
            intangibleAccAssetInfoChangeRepository.deleteAccAssetInfoChange( centerCode,  branchCode,  accBookType,  accBookCode,  acca.getId().getChangeCode());
        }
//      else if(acca.getChangeType().equals("03")){
//           //卡片清理
//           //凭证不知道要不要删
//          List<IntangibleAccAssetInfo> accAssetinfoList=intangibleChangeManageRepository.getAccAssetInfo(centerCode,branchCode,accBookType,accBookCode,acca.getCodeType(),acca.getCardCode());
//          IntangibleAccAssetInfo  accAssetinfo=accAssetinfoList.get(0);
//          accAssetinfo.setClearFlag("0");
//          accAssetinfo.setClearCode("");
//          accAssetinfo.setClearYearMonth("");
//          accAssetinfo.setClearfee(null);
//          accAssetinfo.setClearReason("");
//          accAssetinfo.setClearDate("");
//          accAssetinfo.setClearOperatorBranch("");
//          accAssetinfo.setClearOperatorCode("");
//        //   accAssetInfoChangeRepository.clearCard(uni,centerCode,  branchCode,  accBookType,  accBookCode, acca.getCodeType(),acca.getCardCode());
//          intangibleChangeManageRepository.save(accAssetinfo);
//
//       }

         return InvokeResult.success();
    }
    @Override
   public List<?> intangibleselect(IntangibleAccAssetInfoChangeDTO accAssetInfoDTO){
        StringBuffer sql=new StringBuffer();
        int paramsNo = 1;
        Map<Integer,Object> params =  new HashMap<>();
        sql.append("select f.depre_from_date as depreFromDate, f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                " f.asset_type as  assetType, f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName,f.depre_to_date as depreToDate, \n" +
                "f.impairment as impairment, f.depre_flag as  depreFlag, f.added_tax as  addedTax, f.input_tax as inputTax, f.sum, f.remains_rate as remainsRate, f.init_depre_amount as initDepreAmount, f.init_depre_money as initDepreMoney,\n" +
                "f.use_flag as useFlag , f.asset_origin_value as assetOriginValue, f.asset_net_value as assetNetValue,\n" +
                " (select code_name from codemanage c where c.code_type = 'deprMethod' and c.code_code = f.dep_type) as depType, \n"+
                "f.pay_way as payWay, f.pay_code as payCode, f.dep_years as depYears, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney,\n" +
                "  f.use_start_date as useStartDate,f.voucher_no as voucherNo, f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee,\n" +
                "f.remains_value as remainsValue,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch, f.clear_operator_code as clearOperatorCode," +
                "(select a.asset_complex_name from IntangibleAccAssetCodeType a where  a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetTypeName,"+
                "(select a.special_name from specialinfo  a where a.account=f.acc_book_code and  a.special_code=(select e.article_code1 from intangibleaccassetcodetype e where  e.acc_book_code=f.acc_book_code and e.acc_book_type =f.acc_book_type and e.code_type=f.code_type and e.asset_type=f.asset_type)) as articleCode1,"+
                "( select c.change_new_data from IntangibleAccAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.asset_type=f.asset_type and c.handle_date =(select max(j.handle_date) from IntangibleAccAssetInfoChange j where j.center_code=f.center_code and j.branch_code=f.branch_code and j.acc_book_code=f.acc_book_code and j.acc_book_type=f.acc_book_type and c.asset_type=f.asset_type and c.code_type=f.code_type and j.asset_code=f.asset_code and j.card_code=f.card_code)) as changeNewData,"+
                "(select i.month_depre_money from intangibleAccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code ORDER BY i.year_month_data desc limit 1  ) as monthDepreMoney ,f.clear_flag as clearFlag, f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp from IntangibleAccAssetInfo  f where 1=1 ");
//        sql.append(" and center_code='"+accAssetInfoDTO.getCenterCode()+"' and branch_code='"+accAssetInfoDTO.getBranchCode()+"' and acc_book_type='"+accAssetInfoDTO.getAccBookType()+"' and acc_book_code='"+accAssetInfoDTO.getAccBookCode()+"' and code_type='"+accAssetInfoDTO.getCodeType()+"' and card_code='"+accAssetInfoDTO.getCardCode()+"'");
        sql.append(" and center_code=?"+paramsNo);
        params.put(paramsNo,accAssetInfoDTO.getCenterCode());
        paramsNo++;
        sql.append(" and branch_code=?"+paramsNo);
        params.put(paramsNo,accAssetInfoDTO.getBranchCode());
        paramsNo++;
        sql.append(" and acc_book_type=?"+paramsNo);
        params.put(paramsNo,accAssetInfoDTO.getAccBookType());
        paramsNo++;
        sql.append(" and acc_book_code=?"+paramsNo);
        params.put(paramsNo,accAssetInfoDTO.getAccBookCode());
        paramsNo++;
        sql.append(" and code_type=?"+paramsNo);
        params.put(paramsNo,accAssetInfoDTO.getCodeType());
        paramsNo++;
        sql.append(" and card_code=?"+paramsNo);
        params.put(paramsNo,accAssetInfoDTO.getCardCode());
        paramsNo++;

        return intangibleChangeManageRepository.queryBySqlSC(sql.toString(),params);
    }
    /**
     * 判断变动后当前卡片是否摊销过
     * @param dto
     * @return
     */
    @Override
    public InvokeResult depreciation(IntangibleAccAssetInfoChangeDTO dto) {
        try{
            //判断当前卡片是否进行过摊销
            StringBuffer adSql = new StringBuffer();
            int paramsNo = 1;
            Map<Integer,Object> params =  new HashMap<>();
            adSql.append("select * from IntangibleAccDepre a where 1=1 and a.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and a.acc_book_type = '"+ dto.getAccBookType() +"' " +
                    " and a.acc_book_code = '"+ dto.getAccBookCode() +"' and a.code_type = '"+dto.getCodeType()+"' " +
                    " and a.asset_code = '"+ dto.getAssetCode() +"' and a.handle_date >'"+dto.getHandleDate()+"'");
            adSql.append(" and a.center_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            adSql.append(" and a.acc_book_type = ?"+paramsNo);
            params.put(paramsNo,dto.getAccBookType());
            paramsNo++;
            adSql.append(" and a.acc_book_code = ?"+paramsNo);
            params.put(paramsNo,dto.getAccBookCode());
            paramsNo++;
            adSql.append(" and a.code_type = ?"+paramsNo);
            params.put(paramsNo,dto.getCodeType());
            paramsNo++;
            adSql.append(" and a.asset_code = ?"+paramsNo);
            params.put(paramsNo,dto.getAssetCode());
            paramsNo++;
            adSql.append(" and a.handle_date >?"+paramsNo);
            params.put(paramsNo,dto.getHandleDate());
            paramsNo++;
            List<?> adList = intangibleChangeManageRepository.queryBySql(adSql.toString(),params, IntangibleAccDepre.class);
            if(adList.size() > 0){//大于0 说明计提过摊销
                return InvokeResult.failure("当前卡片已进行过摊销，无法进行修改！");
            }
            return InvokeResult.success();
        }catch (Exception e){
            logger.error("无形资产卡片修改失败",e);
            return InvokeResult.failure("无形资产卡片修改失败，请联系管理员！");
        }
    }

    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols) {
        ExcelUtil excelUtil = new ExcelUtil();
        int paramsNo = 1;
        Map<Integer,Object> params =  new HashMap<>();
        StringBuffer sql =new StringBuffer("select f.depre_from_date as depreFromDate, f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                " f.asset_type as  assetType, f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName,f.depre_to_date as depreToDate, " +
                "f.impairment as impairment, f.depre_flag as  depreFlag, f.added_tax as  addedTax, f.input_tax as inputTax, f.sum, f.remains_rate as remainsRate, f.init_depre_amount as initDepreAmount, f.init_depre_money as initDepreMoney," +
                "(select c.code_name from codemanage c where c.code_code=f.use_flag and c.code_type='useFlag') as useFlag , f.asset_origin_value as assetOriginValue, f.asset_net_value as assetNetValue," +
                "f.dep_type as depType, "+
                "f.pay_way as payWay, f.pay_code as payCode, f.dep_years as depYears, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "  f.use_start_date as useStartDate,f.voucher_no as voucherNo, f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee," +
                "f.remains_value as remainsValue,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch,"+
                " f.clear_operator_code as clearOperatorCode," +
                "(select a.asset_simple_name from IntangibleAccAssetCodeType a where  a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetTypeName,"+
                "(select a.special_name from specialinfo  a where a.account=f.acc_book_code and  a.special_code=(select e.article_code1 from intangibleaccassetcodetype e where  e.acc_book_code=f.acc_book_code and e.acc_book_type =f.acc_book_type and e.code_type=f.code_type and e.asset_type=f.asset_type)) as articleCode1,"+
                "( select c.change_new_data from IntangibleAccAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.asset_type=f.asset_type and c.handle_date =(select max(j.handle_date) from IntangibleAccAssetInfoChange j where j.center_code=f.center_code and j.branch_code=f.branch_code and j.acc_book_code=f.acc_book_code and j.acc_book_type=f.acc_book_type and c.asset_type=f.asset_type and c.code_type=f.code_type and j.asset_code=f.asset_code and j.card_code=f.card_code)) as changeNewData,"+
                "(select i.month_depre_money from intangibleAccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code ORDER BY i.year_month_data desc limit 1  ) as monthDepreMoney  ,f.clear_flag as clearFlag, f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp from IntangibleAccAssetInfo  f where 1=1 ");
        sql.append(" AND f.center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append("  and f.acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        IntangibleAccAssetInfoDTO intang = new IntangibleAccAssetInfoDTO();
        try {
            intang = new ObjectMapper().readValue(queryConditions, IntangibleAccAssetInfoDTO.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(intang.getCardCode()!=null&&!intang.getCardCode().equals("")){
//            sql.append(" and f.card_code>='"+intang.getCardCode()+"'");
            sql.append(" and f.card_code>=?" + paramsNo );
            params.put(paramsNo,intang.getCardCode());
            paramsNo++;
        }
        if(intang.getCardCode()!=null&&!intang.getCardCode().equals("")){
            if(intang.getCardCode().length()<5){
                String cs=intang.getCardCode();
                for(int i=0;i<5-intang.getCardCode().length();i++){
                    cs="0"+cs;
                }
                intang.setCardCode(cs);
            }
//            sql.append(" and f.card_code>='"+intang.getCardCode()+"'");
            sql.append(" and f.card_code>=?" + paramsNo );
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
            sql.append(" and f.card_code<=?" + paramsNo);
            params.put(paramsNo,intang.getCardCode1());
            paramsNo++;
        }
        if(intang.getAssetType()!=null&&!intang.getAssetType().equals("")){
            sql.append(" and f.asset_type like ?"+paramsNo);
            params.put(paramsNo,intang.getAssetType()+"%");
            paramsNo++;
        }
        if(intang.getAssetCode()!=null&&!intang.getAssetCode().equals("")){
//            sql.append(" and f.asset_code>='"+intang.getAssetCode()+"'");
            sql.append(" and f.asset_code>=?"+paramsNo);
            params.put(paramsNo,intang.getAssetCode());
            paramsNo++;
        }
        if(intang.getAssetCode1()!=null&&!intang.getAssetCode1().equals("")){
//            sql.append(" and f.asset_code<='"+intang.getAssetCode1()+"'");
            sql.append(" and f.asset_code<=?"+paramsNo);
            params.put(paramsNo,intang.getAssetCode());
            paramsNo++;
        }
        if(intang.getUseStartDate()!=null&&!intang.getUseStartDate().equals("")){
//            sql.append(" and f.use_start_date>='"+intang.getUseStartDate()+"'");
            sql.append(" and f.use_start_date>=?"+paramsNo);
            params.put(paramsNo,intang.getUseStartDate());
            paramsNo++;
        }
        if(intang.getUseStartDate1()!=null&&!intang.getUseStartDate1().equals("")){
//            sql.append(" and f.use_start_date<='"+intang.getUseStartDate1()+"'");
            sql.append(" and f.use_start_date<=?"+paramsNo);
            params.put(paramsNo,intang.getUseStartDate1());
            paramsNo++;
        }

        if(intang.getAssetOriginValue()!=null&&!intang.getAssetOriginValue().equals("")){
//            sql.append(" and f.asset_origin_value>='"+intang.getAssetOriginValue()+"'");
            sql.append(" and f.asset_origin_value>=?"+paramsNo);
            params.put(paramsNo,intang.getAssetOriginValue());
            paramsNo++;
        }
        if(intang.getAssetOriginValue1()!=null&&!intang.getAssetOriginValue1().equals("")){
//            sql.append(" and f.asset_origin_value<='"+intang.getAssetOriginValue1()+"'");
            sql.append(" and f.asset_origin_value<=?"+paramsNo);
            params.put(paramsNo,intang.getAssetOriginValue1());
            paramsNo++;
        }
        if(intang.getAssetNetValue()!=null&&!intang.getAssetNetValue().equals("")){
//            sql.append(" and f.asset_net_value>='"+intang.getAssetNetValue()+"'");
            sql.append(" and f.asset_net_value>=?"+paramsNo);
            params.put(paramsNo,intang.getAssetNetValue());
            paramsNo++;
        }
        if(intang.getAssetNetValue1()!=null&&!intang.getAssetNetValue1().equals("")){
//            sql.append(" and f.asset_net_value<='"+intang.getAssetNetValue1()+"'");
            sql.append(" and f.asset_net_value<=?"+paramsNo);
            params.put(paramsNo,intang.getAssetNetValue1());
            paramsNo++;
        }
        if(intang.getEndDepreMoney()!=null&&!intang.getEndDepreMoney().equals("")){
//            sql.append(" and f.end_depre_money>='"+intang.getEndDepreMoney()+"'");
            sql.append(" and f.end_depre_money>=?"+paramsNo);
            params.put(paramsNo,intang.getEndDepreMoney());
            paramsNo++;
        }
        if(intang.getEndDepreMoney1()!=null&&!intang.getEndDepreMoney1().equals("")){
//            sql.append(" and f.end_depre_money<='"+intang.getEndDepreMoney1()+"'");
            sql.append(" and f.end_depre_money<=?"+paramsNo);
            params.put(paramsNo,intang.getEndDepreMoney1());
            paramsNo++;
        }
        if(intang.getDepYears()!=null&&!intang.getDepYears().equals("")){
//            sql.append(" and f.dep_years>='"+intang.getDepYears()+"'");
            sql.append(" and f.dep_years>=?"+paramsNo);
            params.put(paramsNo,intang.getDepYears());
            paramsNo++;
        }
        if(intang.getDepYears1()!=null&&!intang.getDepYears1().equals("")){
//            sql.append(" and f.dep_years<='"+intang.getDepYears1()+"'");
            sql.append(" and f.dep_years<=?"+paramsNo);
            params.put(paramsNo,intang.getDepYears1());
            paramsNo++;
        }
        //摊销至日期---------------------------
        if(intang.getDepreUtilDate()!=null&&!intang.getDepreUtilDate().equals("")){
//            sql.append(" and f.depre_to_date<='"+intang.getDepreUtilDate()+"' and f.use_start_date<='"+intang.getDepreUtilDate()+"'");
            sql.append(" and f.depre_to_date<=?"+paramsNo);
            params.put(paramsNo,intang.getDepreUtilDate());
            paramsNo++;
            sql.append(" and f.use_start_date<=?"+paramsNo);
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
//                sql.append(" and c.change_date<='"+intang.getChangeDate()+"'");
                sql.append(" and c.change_date<=?"+paramsNo);
                params.put(paramsNo,intang.getChangeDate());
                paramsNo++;
            }
            if(intang.getChangeType()!=null&&!intang.getChangeType().equals("")){
//                sql.append(" and  c.change_type='"+intang.getChangeType()+"'");
                sql.append(" and  c.change_type=?"+paramsNo);
                params.put(paramsNo,intang.getChangeType());
                paramsNo++;
            }
            sql.append(")");
        }

        if(intang.getCreateOper()!=null && !intang.getCreateOper().equals("")){
//            sql.append(" and f.create_oper in (select id from roleinfo where role_name like '%"+intang.getCreateOper()+"%')");
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
            excelUtil.exportu_intangcardselect(request, response, name, cols, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public InvokeResult revokejudge(String changeCodes){
        String [] changeCodeArr=changeCodes.split(",");
        ArrayList list =new ArrayList();
        //获取卡片数目
//        StringBuffer sql1=new StringBuffer("select * from intangibleaccassetinfochange  where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and  change_code in ("+changeCodes+") group by card_code");
//        List<IntangibleAccAssetInfoChange> cardsum=(List<IntangibleAccAssetInfoChange>)intangibleChangeManageRepository.queryBySql(sql1.toString(), IntangibleAccAssetInfoChange.class);
        List<IntangibleAccAssetInfoChange> cardsum=intangibleAccAssetInfoChangeRepository.queryIntangibleAccAssetInfoChangeCardCount(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),changeCodeArr);
        for(int i=0;i<cardsum.size();i++){
            //循环卡片
            //获取变动类型数目
//            StringBuffer sql2=new StringBuffer("select * from intangibleaccassetinfochange where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardsum.get(i).getCardCode()+"' and  change_code in ("+changeCodes+")  group by change_type");
//            List<IntangibleAccAssetInfoChange> changeTypesum=(List<IntangibleAccAssetInfoChange>)intangibleChangeManageRepository.queryBySql(sql2.toString(), IntangibleAccAssetInfoChange.class);
            List<IntangibleAccAssetInfoChange> changeTypesum=intangibleAccAssetInfoChangeRepository.queryIntangibleAccAssetInfoChangeMoveTypeCount(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),cardsum.get(i).getCardCode(),changeCodeArr);
            for(int j=0;j<changeTypesum.size();j++){
                //循环变动类型 从大到小
//                StringBuffer sql3 =new StringBuffer("select * from intangibleaccassetinfochange where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardsum.get(i).getCardCode()+"' and  change_code in ("+changeCodes+") and change_type='"+changeTypesum.get(j).getChangeType()+"' ORDER BY change_code desc");
//                List<IntangibleAccAssetInfoChange> getchangeType=(List<IntangibleAccAssetInfoChange>)intangibleChangeManageRepository.queryBySql(sql3.toString(), IntangibleAccAssetInfoChange.class);
                List<IntangibleAccAssetInfoChange> getchangeType=intangibleAccAssetInfoChangeRepository.queryIntangibleAccAssetInfoChangeMoveTypeCountBigToSmall(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),cardsum.get(i).getCardCode(),changeCodeArr,changeTypesum.get(j).getChangeType());
                //获取的最大变动单
                String maxChangeCode=getchangeType.get(0).getId().getChangeCode();
                //获取最小变动单
                String minChangeCode=getchangeType.get(getchangeType.size()-1).getId().getChangeCode();
                //向数据库中查询看是否是从最大变动单开始查询 或者选中是否连续
                //1.查询最大变动单
//                StringBuffer sql4 =new StringBuffer("select * from intangibleaccassetinfochange where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardsum.get(i).getCardCode()+"'  and change_type='"+changeTypesum.get(j).getChangeType()+"' ORDER BY change_code desc ");
//                List<IntangibleAccAssetInfoChange> getMaxchangeCode=(List<IntangibleAccAssetInfoChange>)intangibleChangeManageRepository.queryBySql(sql4.toString(), IntangibleAccAssetInfoChange.class);
                List<IntangibleAccAssetInfoChange> getMaxchangeCode=intangibleAccAssetInfoChangeRepository.queryIntangibleAccAssetInfoChangeMaxMoveTypeInfo(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),cardsum.get(i).getCardCode(),changeTypesum.get(j).getChangeType());
                String maxchangecode1=getMaxchangeCode.get(0).getId().getChangeCode();
                if(!maxChangeCode.equals(maxchangecode1)){
                    //不相等 不是从最大变动单号开始撤销
                    return InvokeResult.failure("卡片"+cardsum.get(i).getCardCode()+"请从该变动类型下最大变动单开始撤销");
                }
                //2.判断选中的数据是否连续
//                StringBuffer sql5 =new StringBuffer("select * from intangibleaccassetinfochange where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardsum.get(i).getCardCode()+"'  and change_type='"+changeTypesum.get(j).getChangeType()+"' and change_code >="+minChangeCode+" and change_code<="+maxChangeCode+" ORDER BY change_code desc");
//                List<IntangibleAccAssetInfoChange> getchangeTypes=(List<IntangibleAccAssetInfoChange>)intangibleChangeManageRepository.queryBySql(sql5.toString(), IntangibleAccAssetInfoChange.class);
                List<IntangibleAccAssetInfoChange> getchangeTypes=intangibleAccAssetInfoChangeRepository.queryIntangibleAccAssetInfoChangeContinuous(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),cardsum.get(i).getCardCode(),changeTypesum.get(j).getChangeType(),minChangeCode,maxChangeCode);
                //查询选中中数据是否连续
//                StringBuffer sql6 =new StringBuffer("select * from intangibleaccassetinfochange where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardsum.get(i).getCardCode()+"'  and change_type='"+changeTypesum.get(j).getChangeType()+"' and  change_code in ("+changeCodes+") and change_code >="+minChangeCode+" and change_code<="+maxChangeCode+" ORDER BY change_code desc");
//                List<IntangibleAccAssetInfoChange> getchangeTypes2=(List<IntangibleAccAssetInfoChange>)intangibleChangeManageRepository.queryBySql(sql6.toString(), IntangibleAccAssetInfoChange.class);
                List<IntangibleAccAssetInfoChange> getchangeTypes2=intangibleAccAssetInfoChangeRepository.queryIntangibleAccAssetInfoChangeContinuousANDChangeCode(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),cardsum.get(i).getCardCode(),changeTypesum.get(j).getChangeType(),minChangeCode,maxChangeCode,changeCodeArr);
                if(getchangeTypes2.size()!=getchangeTypes.size()){
                    //不连续
                    return InvokeResult.failure("卡片"+cardsum.get(i).getCardCode()+"在该变动类型下的变动单需连续撤销");
                }
                //将该卡片 该变动类型下 所有数据存入
                int seatsNo = 1;
                Map<Integer,Object> seats = new HashMap<>();
                StringBuffer sql7 =new StringBuffer("select  f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,f.change_code as changeCode," +
                        " f.card_code as cardCode, f.asset_code as assetCode,f.asset_type as assetType,f.change_date as changeDate, f.change_type as changeType," +
                        " f.change_old_data as changeOldData ,f.change_new_data as changeNewData,f.change_reason as changeReason,f.operator_branch as operatorBranch, f.operator_code as operatorCode,f.handle_date as handleDate,f.temp as temp from intangibleaccassetinfochange f where 1=1 " +
                        "and f.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and f.acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and f.card_code='"+cardsum.get(i).getCardCode()+"' and  f.change_code in ("+changeCodes+") and f.change_type='"+changeTypesum.get(j).getChangeType()+"'  ORDER BY f.change_code desc");

                sql7.append(" and f.center_code=?"+seatsNo);
                seats.put(seatsNo,CurrentUser.getCurrentLoginManageBranch());
                seatsNo++;
                sql7.append(" and f.acc_book_code=?"+seatsNo);
                seats.put(seatsNo,CurrentUser.getCurrentLoginAccount());
                seatsNo++;
                sql7.append(" and f.card_code=?"+seatsNo);
                seats.put(seatsNo,cardsum.get(i).getCardCode());
                seatsNo++;
                sql7.append(" and  f.change_code in ( ?"+seatsNo+")");
                seats.put(seatsNo,changeCodes);
                seatsNo++;
                sql7.append(" and f.change_type=?"+seatsNo);
                seats.put(seatsNo,changeTypesum.get(j).getChangeType());
                seatsNo++;
                sql7.append("  ORDER BY f.change_code desc");
                List<?> getinfochange=intangibleChangeManageRepository.queryBySqlSC(sql7.toString(),seats);
                for (Object obj:getinfochange) {
                    Map map=new HashMap();
                    map.putAll((Map) obj);
                    list.add(map);
                }

            }
        }

        return  InvokeResult.success(list);

    }
}
