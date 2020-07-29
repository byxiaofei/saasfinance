package com.sinosoft.service.impl.fixedassets;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.account.AccMainVoucher;
import com.sinosoft.domain.account.AccMainVoucherId;
import com.sinosoft.domain.account.AccSubVoucher;
import com.sinosoft.domain.account.AccSubVoucherId;
import com.sinosoft.domain.fixedassets.*;
import com.sinosoft.dto.fixedassets.AccAssetInfoChangeDTO;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.repository.account.AccMainVoucherRespository;
import com.sinosoft.repository.account.AccSubVoucherRespository;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.repository.fixedassets.AccAssetInfoChangeRepository;
import com.sinosoft.repository.fixedassets.AccDepreRepository;
import com.sinosoft.repository.fixedassets.ChangeManageRepository;
import com.sinosoft.service.fixedassets.CategoryCodingService;
import com.sinosoft.service.fixedassets.ChangeManageService;
import com.sinosoft.service.fixedassets.ChangeSelectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class ChangeSelectServiceImpl implements ChangeSelectService {
   private Logger logger = LoggerFactory.getLogger(ChangeSelectServiceImpl.class);
    @Value("${voucher.currency}")
    private String currency;

  @Resource
    private ChangeManageRepository changeManageRepository;
    @Resource
    private AccAssetInfoChangeRepository accAssetInfoChangeRepository;
   @Resource
    private AccAssetCodeTypeRepository accAssetCodeTypeRepository;
   @Resource
   private CategoryCodingService categoryCodingService ;
  /*   @Resource
    private AccMainVoucherRespository accMainVoucherRespository;
    @Resource
    private AccSubVoucherRespository accSubVoucherRespository;*/
    @Resource
    private AccDepreRepository accDepreRepository;


    @Override
    public Page<?> qrychangeList(int page, int rows, AccAssetInfoChangeDTO acc){
        StringBuffer sql=new StringBuffer();
        sql.append("select f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,f.change_code as changeCode,"+
                "f.card_code as cardCode, f.asset_code as assetCode,f.asset_type as assetType,f.change_date as changeDate, f.change_type as changeType,"+
                "f.change_old_data as changeOldData ,f.change_new_data as changeNewData,f.change_reason as changeReason,f.operator_branch as operatorBranch, f.operator_code as operatorCode,f.handle_date as handleDate,f.temp as temp,"+
                " (select c.code_name from codemanage c where c.code_type='fixChangeType' and c.code_code=f.change_type) as changeTypeName,"+
                "(select a.use_flag from accassetinfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code) as useFlag,"+
                "(select a.specification from accassetinfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code) as specification,"+
                "(select c.code_name from codemanage c where c.code_type='deprMethod' and c.code_code=(select a.dep_type from accassetinfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code)) as depTypeName,"+
                "(select a.clear_flag from accassetinfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code) as clearFlag,"+
                "(select a.unit_code from accassetinfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code) as unitCode,"+
                "(select c.code_name from codemanage c where c.code_type='organization' and c.code_code=(select a.organization from accassetinfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code)) as organization,"+
                "(select a.use_start_date from accassetinfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code) as useStartDate,"+
                 "(CASE WHEN SUBSTRING_INDEX(change_old_data, ':', 1)  LIKE '%#%' THEN '' ELSE SUBSTRING_INDEX(change_old_data, ':', 1) END) as changeOldDataRight ,"+
                "(SUBSTRING_INDEX(change_new_data,':',1)) as changeNewDataRight ,"+
                "(select u.user_name from userinfo u where u.id=f.operator_code) as operatorCodeName,"+
                "(select a.asset_name from accassetinfo a where a.center_code=f.center_code and a.branch_code=f.branch_code and a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.card_code=f.card_code) as assetName from accassetinfochange  f where 1=1 ");

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
            sql.append(" and f.card_code in(select a.card_code from AccAssetInfo a where a.center_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append(" and  a.acc_book_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append(" and a.use_start_date>=?"+paramsNo+") ");
            params.put(paramsNo,acc.getUseStartDate());
            paramsNo++;
        }
        if(acc.getUseStartDate1()!=null&&!acc.getUseStartDate1().equals("")){
            sql.append(" and f.card_code in(select a.card_code from AccAssetInfo a where a.center_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append(" and  a.acc_book_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append(" and a.use_start_date>=?"+paramsNo+") ");
            params.put(paramsNo,acc.getUseStartDate1());
            paramsNo++;

        }
//        if(acc.getChangeCode()!=null&&!acc.getChangeCode().equals("")){
//            sql.append(" and f.change_code>='"+acc.getChangeCode()+"'");
//        }
//        if(acc.getChangeCode1()!=null&&!acc.getChangeCode1().equals("")) {
//            sql.append(" and f.change_code<='" + acc.getChangeCode1() + "'");
//        }
        //如果变动单号正好15位那么不用拼接年份
        if(acc.getChangeCode()!=null&&!"".equals(acc.getChangeCode())&&acc.getChangeCode1().length()==15){
            sql.append(" and f.change_code>=?"+ paramsNo);
            params.put(paramsNo,acc.getChangeCode());
            paramsNo++;
        }
        if(acc.getChangeCode1()!=null&&!"".equals(acc.getChangeCode1())&&acc.getChangeCode1().length()==15){
            sql.append(" and f.change_code<=?"+ paramsNo);
            params.put(paramsNo,acc.getChangeCode1());
            paramsNo++;
        }
        //变动单号changeCode是由年份加零和变动单号拼接而成的
        if(acc.getChangeCode()!=null&&!"".equals(acc.getChangeCode())&&acc.getChangeCode().length()<15){
            sql.append(" and f.change_code>=?"+ paramsNo);
            params.put(paramsNo,acc.getSelectYear()+String.format("%011d", Integer.parseInt(acc.getChangeCode())));
            paramsNo++;
        }
        //当变动单号未输入时
        if(acc.getSelectYear()!=null&&!"".equals(acc.getSelectYear())){
            sql.append(" and f.change_code like ?"+ paramsNo);
            params.put(paramsNo,"%"+acc.getSelectYear()+"%");
            paramsNo++;
        }
        if(acc.getChangeCode1()!=null&&!"".equals(acc.getChangeCode1())&&acc.getChangeCode1().length()<15){
            sql.append(" and f.change_code<=?"+ paramsNo);
            params.put(paramsNo,acc.getSelectYear()+String.format("%011d", Integer.parseInt(acc.getChangeCode1())));
            paramsNo++;
        }
        if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
            sql.append(" and f.change_date>=?"+ paramsNo);
            params.put(paramsNo,acc.getChangeDate());
            paramsNo++;
        }
        if(acc.getChangeDate1()!=null&&!acc.getChangeDate1().equals("")){
            sql.append(" and f.change_date<=?"+ paramsNo);
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
        //使用部门unitCode
        if(acc.getUnitCode()!=null&&!acc.getUnitCode().equals("")){
            sql.append(" and f.card_code in(select a.card_code from AccAssetInfo a where a.center_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append(" and  a.acc_book_code= ?"+ paramsNo);
            params.put(paramsNo, CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append(" and a.unit_code =?"+paramsNo+")");
            params.put(paramsNo, acc.getUnitCode());
            paramsNo++;
        }
        if(acc.getOrganization()!=null&&!acc.getOrganization().equals("")){
            sql.append(" and f.card_code in(select a.card_code from AccAssetInfo a where a.center_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append(" and  a.acc_book_code =?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append(" and a.organization =?"+paramsNo+")");
            params.put(paramsNo,acc.getOrganization());
            paramsNo++;
        }
        if(acc.getAssetType()!=null&&!acc.getAssetType().equals("")){
            sql.append(" and f.asset_type=?"+paramsNo);
            params.put(paramsNo,acc.getAssetType());
            paramsNo++;
        }

        if(acc.getOperatorCode()!=null&&!acc.getOperatorCode().equals("")){
           // sql.append(" and f.operator_code='"+acc.getOperatorCode()+"'");
            sql.append(" and f.operator_code in (select id from roleinfo where role_name like ?"+paramsNo+")");
            params.put(paramsNo,"%"+acc.getOperatorCode()+"%");
            paramsNo++;

        }

        if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
            sql.append(" and f.change_type=?"+paramsNo);
            params.put(paramsNo,acc.getChangeType());
            paramsNo++;
        }

        Page<?> result = categoryCodingService.getPage(page,rows,accAssetInfoChangeRepository.queryBySqlSC(sql.toString(),params));
        return result;
    }

    @Transactional
    @Override
    public InvokeResult revoke(String changeCodes){
        String accBookType = CurrentUser.getCurrentLoginAccountType();     //账套类型
        String accBookCode = CurrentUser.getCurrentLoginAccount();     //账套编码
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();

      //判断当前所撤销的变动单是否为最大变动单
//      String maxChangeCode = accAssetInfoChangeRepository.getChangeCode(accBookType,accBookCode);
//      if(!acca.getChangeCode().equals(maxChangeCode)){
//          return InvokeResult.failure("请从最大变动单开始撤销！");
//      }
//        StringBuffer sqls=new StringBuffer();
//        sqls.append("select * from AccAssetInfoChange  where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and  change_code in ("+changeCodes+") ");
//       List<AccAssetInfoChange> accs= (List<AccAssetInfoChange>)accAssetInfoChangeRepository.queryBySql(sqls.toString(),AccAssetInfoChange.class);
        List<AccAssetInfoChange> accs= accAssetInfoChangeRepository.queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndChangeCode(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),changeCodes);
       for(AccAssetInfoChange acca:accs){
           String [] arr=acca.getChangeOldData().split(":");
           String uni=arr[1].trim();
           if(acca.getChangeType().equals("01")){
               //   accAssetInfoChangeRepository.getAccAssetInfoChange( centerCode,  branchCode,  accBookType,  accBookCode,  acca.getChangeCode());
               //部门变动
               accAssetInfoChangeRepository.updateUpnitCode(uni,centerCode,  branchCode,  accBookType,  accBookCode, acca.getCodeType(),acca.getCardCode());

           }else if(acca.getChangeType().equals("02")){
               //类别变动
               List<AccAssetCodeType> acctypeList= accAssetCodeTypeRepository.getAssetType(accBookType,accBookCode,acca.getCodeType(),uni);
               AccAssetCodeType  acctype=acctypeList.get(0);
               List<AccAssetInfo> accAssetinfoList=changeManageRepository.getAccAssetInfo(centerCode,branchCode,accBookType,accBookCode,acca.getCodeType(),acca.getCardCode());
               AccAssetInfo  accAsset=accAssetinfoList.get(0);
               //预计残值
               if(acctype.getNetSurplusRate()==null){
                   acctype.setNetSurplusRate(new BigDecimal("0"));
               }
               BigDecimal originvalue=accAsset.getAssetOriginValue().multiply(new BigDecimal("1").subtract(acctype.getNetSurplusRate()));
               accAsset.setDepYears(acctype.getDepYears());
               accAsset.setDepType(acctype.getDepType());
               accAsset.setAssetType(uni);
               accAsset.setRemainsRate( acctype.getNetSurplusRate());
               accAsset.setRemainsValue(originvalue);
               changeManageRepository.save(accAsset);
           }else if(acca.getChangeType().equals("03")){
               //存放地点变动
               //修改基本信息表
               accAssetInfoChangeRepository.updateorgination(uni,centerCode,  branchCode,  accBookType,  accBookCode, acca.getCodeType(),acca.getCardCode());

           }else if(acca.getChangeType().equals("04")){
               //使用状态变动
               accAssetInfoChangeRepository.updateUseFlag(uni,centerCode,  branchCode,  accBookType,  accBookCode, acca.getCodeType(),acca.getCardCode());

           }
           accAssetInfoChangeRepository.deleteAccAssetInfoChange( centerCode,  branchCode,  accBookType,  accBookCode,  acca.getId().getChangeCode());

       }
      /*else if(acca.getChangeType().equals("05")){
           //卡片清理
           //凭证不知道要不要删
           List<AccAssetInfo> accAssetinfoList=changeManageRepository.getAccAssetInfo(centerCode,branchCode,accBookType,accBookCode,acca.getCodeType(),acca.getCardCode());
           AccAssetInfo  accAsset=accAssetinfoList.get(0);
           accAsset.setClearFlag("0");
           accAsset.setClearCode("");
           accAsset.setClearYearMonth("");
           accAsset.setClearIncome(null);
           accAsset.setClearIncomeTallage(null);
           accAsset.setClearfee(null);
           accAsset.setClearReason("");
           accAsset.setClearDate("");
           accAsset.setClearOperatorBranch("");
           accAsset.setClearOperatorCode("");
        //   accAssetInfoChangeRepository.clearCard(uni,centerCode,  branchCode,  accBookType,  accBookCode, acca.getCodeType(),acca.getCardCode());
           changeManageRepository.save(accAsset);

       }*/
       //删除卡片变动单
         return InvokeResult.success();
    }
    @Override
    public InvokeResult revokejudge(String changeCodes){
        String [] changeCodeArr=changeCodes.split(",");
        ArrayList list =new ArrayList();
        //获取卡片数目
//        StringBuffer sql1=new StringBuffer("select * from AccAssetInfoChange  where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and  change_code in ("+changeCodes+") group by card_code");
//        List<AccAssetInfoChange> cardsum=(List<AccAssetInfoChange>)accAssetInfoChangeRepository.queryBySql(sql1.toString(), AccAssetInfoChange.class);
        List<AccAssetInfoChange> cardsum=(List<AccAssetInfoChange>)accAssetInfoChangeRepository.queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndChangeCodeGroupByCardCode(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),changeCodes);
        for(int i=0;i<cardsum.size();i++){
            //循环卡片
            //获取变动类型数目
//            StringBuffer sql2=new StringBuffer("select * from AccAssetInfoChange where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardsum.get(i).getCardCode()+"' and  change_code in ("+changeCodes+")  group by change_type");
//            List<AccAssetInfoChange> changeTypesum=(List<AccAssetInfoChange>)accAssetInfoChangeRepository.queryBySql(sql2.toString(), AccAssetInfoChange.class);
            List<AccAssetInfoChange> changeTypesum=accAssetInfoChangeRepository.queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndCardCodeAndChangeCodeGroupByChangeType(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),cardsum.get(i).getCardCode(),changeCodes);
            for(int j=0;j<changeTypesum.size();j++){
                //循环变动类型 从大到小
//                StringBuffer sql3 =new StringBuffer("select * from AccAssetInfoChange where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardsum.get(i).getCardCode()+"' and  change_code in ("+changeCodes+") and change_type='"+changeTypesum.get(j).getChangeType()+"' ORDER BY change_code desc");
//                List<AccAssetInfoChange> getchangeType=(List<AccAssetInfoChange>)accAssetInfoChangeRepository.queryBySql(sql3.toString(), AccAssetInfoChange.class);
                List<AccAssetInfoChange> getchangeType=accAssetInfoChangeRepository.queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndCardCodeAndChangeCodeAndChangeTypeOrderByChangeCode(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),cardsum.get(i).getCardCode(),changeCodes,changeTypesum.get(j).getChangeType());
                //获取的最大变动单
                String maxChangeCode=getchangeType.get(0).getId().getChangeCode();
                //获取最小变动单
                String minChangeCode=getchangeType.get(getchangeType.size()-1).getId().getChangeCode();
                //向数据库中查询看是否是从最大变动单开始查询 或者选中是否连续
                //1.查询最大变动单
//                StringBuffer sql4 =new StringBuffer("select * from AccAssetInfoChange where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardsum.get(i).getCardCode()+"'  and change_type='"+changeTypesum.get(j).getChangeType()+"' ORDER BY change_code desc ");
//                List<AccAssetInfoChange> getMaxchangeCode=(List<AccAssetInfoChange>)accAssetInfoChangeRepository.queryBySql(sql4.toString(), AccAssetInfoChange.class);
                List<AccAssetInfoChange> getMaxchangeCode=accAssetInfoChangeRepository.queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndCardCodeAndChangeTypeOrderByChangeCode(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),cardsum.get(i).getCardCode(),changeTypesum.get(j).getChangeType());
                String maxchangecode1=getMaxchangeCode.get(0).getId().getChangeCode();
                if(!maxChangeCode.equals(maxchangecode1)){
                    //不相等 不是从最大变动单号开始撤销
                    return InvokeResult.failure("卡片"+cardsum.get(i).getCardCode()+"请从该变动类型下最大变动单开始撤销");
                }
                //2.判断选中的数据是否连续
//                StringBuffer sql5 =new StringBuffer("select * from AccAssetInfoChange where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardsum.get(i).getCardCode()+"'  and change_type='"+changeTypesum.get(j).getChangeType()+"' and change_code >="+minChangeCode+" and change_code<="+maxChangeCode+" ORDER BY change_code desc");
//                List<AccAssetInfoChange> getchangeTypes=(List<AccAssetInfoChange>)accAssetInfoChangeRepository.queryBySql(sql5.toString(), AccAssetInfoChange.class);
                List<AccAssetInfoChange> getchangeTypes=accAssetInfoChangeRepository.queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndCardCodeAndChangeTypeAndChangeCodeMinMaxOrderByChangeCode(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),cardsum.get(i).getCardCode(),changeTypesum.get(j).getChangeType(),minChangeCode,maxChangeCode);
                //查询选中中数据是否连续
//                StringBuffer sql6 =new StringBuffer("select * from AccAssetInfoChange where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardsum.get(i).getCardCode()+"'  and change_type='"+changeTypesum.get(j).getChangeType()+"' and  change_code in ("+changeCodes+") and change_code >="+minChangeCode+" and change_code<="+maxChangeCode+" ORDER BY change_code desc");
//                List<AccAssetInfoChange> getchangeTypes2=(List<AccAssetInfoChange>)accAssetInfoChangeRepository.queryBySql(sql6.toString(), AccAssetInfoChange.class);
                List<AccAssetInfoChange> getchangeTypes2=accAssetInfoChangeRepository.queryAccAssetInfoChangeByCenterCodeAndAccBookCodeAndCardCodeAndChangeTypeAndChangeCodeMinMaxOrderByChangeCode1(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),cardsum.get(i).getCardCode(),changeTypesum.get(j).getChangeType(),changeCodes,minChangeCode,maxChangeCode);
                if(getchangeTypes2.size()!=getchangeTypes.size()){
                    //不连续
                    return InvokeResult.failure("卡片"+cardsum.get(i).getCardCode()+"在该变动类型下的变动单需连续撤销");
                }
                //将该卡片 该变动类型下 所有数据存入
                StringBuffer sql7 =new StringBuffer("select  f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,f.change_code as changeCode," +
                        " f.card_code as cardCode, f.asset_code as assetCode,f.asset_type as assetType,f.change_date as changeDate, f.change_type as changeType," +
                        " f.change_old_data as changeOldData ,f.change_new_data as changeNewData,f.change_reason as changeReason,f.operator_branch as operatorBranch, f.operator_code as operatorCode,f.handle_date as handleDate,f.temp as temp from AccAssetInfoChange f where 1=1 ");

                int paramsNo = 1;
                Map<Integer,Object> params = new HashMap<>();
                sql7.append(" and f.center_code=?"+paramsNo);
                params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                paramsNo++;
                sql7.append(" and f.acc_book_code=?"+paramsNo);
                params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                paramsNo++;
                sql7.append(" and f.card_code=?"+paramsNo);
                params.put(paramsNo,cardsum.get(i).getCardCode());
                paramsNo++;
                sql7.append(" and f.change_code in ("+"?"+paramsNo+")");
                params.put(paramsNo,changeCodes);
                paramsNo++;
                sql7.append(" and f.change_type=?"+paramsNo+"  ORDER BY f.change_code desc");
                params.put(paramsNo,changeTypesum.get(j).getChangeType());
                paramsNo++;
                List<?> getinfochange=accAssetInfoChangeRepository.queryBySqlSC(sql7.toString(),params);
                for (Object obj:getinfochange) {
                    Map map=new HashMap();
                    map.putAll((Map) obj);
                    list.add(map);
                }

            }
        }

        return  InvokeResult.success(list);

    }





    @Override
    public List<?> cardselect(AccAssetInfoDTO accAssetInfoDTO){
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
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
        sql.append("(select a.asset_complex_name from accassetcodetype a where a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetComplexName, ");
        sql.append("(select e.code_name from codemanage e where e.code_type='sourceFlag' and e.code_code=f.source_flag) as sourceFlag from AccAssetInfo  f where 1=1 ");
        sql.append(" and center_code=?"+paramsNo);
        params.put(paramsNo,accAssetInfoDTO.getCenterCode());
        paramsNo++;
        sql.append(" and branch_code =?"+paramsNo);
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
        return changeManageRepository.queryBySqlSC(sql.toString(),params);
    }
   /*  public String isDepre(AccAssetInfoChangeDTO accAssetInfoChange){
        StringBuffer sql=new StringBuffer();
        sql.append("select count(year_month_data) from accdepre where center_code='"+accAssetInfoChange.getCenterCode()+"' and branch_code='"+accAssetInfoChange.getBranchCode()+"' and acc_book_type='"+accAssetInfoChange.getAccBookType()+"' and acc_book_code='"+accAssetInfoChange.getAccBookCode()+"' and code_type='"+accAssetInfoChange.getCodeType()+"' and asset_type='"+accAssetInfoChange.getAssetType()+"' and asset_code='"+accAssetInfoChange.getAssetCode()+"' and  year_month_data>='"+accAssetInfoChange.getChangeDate()+"'  or handle_date>= ");
     }*/
    /**
     * 判断当前卡片是否计提过
     * @param dto
     * @return
     */
    @Override
    public InvokeResult Isdepreciation(AccAssetInfoChangeDTO dto) {
        try{
            //判断变动后当前卡片是否进行过计提
//            StringBuffer adSql = new StringBuffer();
//            adSql.append("select * from accdepre a where a.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  a.acc_book_type = '"+ dto.getAccBookType() +"' " +
//                    " and a.acc_book_code = '"+ dto.getAccBookCode() +"' and a.code_type = '"+dto.getCodeType()+"' " +
//                    " and a.asset_code = '"+ dto.getAssetCode() +"' and a.handle_date > '"+dto.getHandleDate()+" '");
//            List<?> adList = accAssetCodeTypeRepository.queryBySql(adSql.toString(), AccDepre.class);
            List<AccDepre> adList =  accDepreRepository.queryAccDepreInfo(CurrentUser.getCurrentLoginManageBranch(),dto.getAccBookType(),dto.getAccBookCode(),dto.getCodeType(),dto.getAssetCode(),dto.getHandleDate());
            if(adList.size() > 0){//大于0 说明计提过折旧
                return InvokeResult.failure("当前卡片已进行过折旧，无法进行修改！");
            }
            return InvokeResult.success();
        }catch (Exception e){
            logger.error("固定资产卡片修改失败",e);
            return InvokeResult.failure("固定资产卡片修改失败，请联系管理员！");
        }
    }

}
