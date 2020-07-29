package com.sinosoft.service.impl.fixedassets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.domain.account.*;
import com.sinosoft.domain.fixedassets.*;
import com.sinosoft.domain.intangibleassets.AccWCheckInfo;
import com.sinosoft.domain.intangibleassets.AccWCheckInfoId;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.repository.AccSegmentDefineRespository;
import com.sinosoft.repository.AgeSegmentDefineRepository;
import com.sinosoft.repository.SpecialInfoRepository;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.repository.account.AccMainVoucherRespository;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.repository.account.AccSubVoucherRespository;
import com.sinosoft.repository.account.AccVoucherNoRespository;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.repository.fixedassets.AccAssetInfoRepository;
import com.sinosoft.repository.fixedassets.AccDepreRepository;
import com.sinosoft.repository.fixedassets.AccGCheckInfoRepository;
import com.sinosoft.repository.intangibleassets.AccWCheckInfoRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetInfoRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccDepreRepository;
import com.sinosoft.service.account.VoucherManageService;
import com.sinosoft.service.fixedassets.FixedassetsCardVoucherService;
import com.sinosoft.util.ExcelUtil;
import org.omg.CORBA.Current;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-29 15:10
 */
@Service
public class FixedassetsCardVoucherServiceImpl implements FixedassetsCardVoucherService {
    private Logger logger = LoggerFactory.getLogger(FixedassetsCardVoucherServiceImpl.class);
    @Value("${voucher.currency}")
    private String currency;
    @Resource
    private VoucherManageService voucherManageService;
    @Resource
    private AccMainVoucherRespository accMainVoucherRespository;
    @Resource
    private AccSubVoucherRespository accSubVoucherRespository;
    @Resource
    private AccAssetCodeTypeRepository accAssetCodeTypeRepository;
    @Resource
    private AccVoucherNoRespository accVoucherNoRespository;
    @Resource
    private AccAssetInfoRepository accAssetInfoRepository;
    @Resource
    private IntangibleAccAssetInfoRepository intangibleAccAssetInfoRepository;
    @Resource
    private AccDepreRepository accDepreRepository;
    @Resource
    private IntangibleAccDepreRepository intangibleAccDepreRepository;
    @Resource
    private AccGCheckInfoRepository accGCheckInfoRepository;
    @Resource
    private AccWCheckInfoRepository accWCheckInfoRepository;
    @Resource
    private SpecialInfoRepository specialInfoRepository;

    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;
    @Resource
    private SubjectRepository subjectRepository;
    @Resource
    private AccSegmentDefineRespository accSegmentDefineRespository;


    /**
     * 固定资产卡片凭证生成
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult createVoucher(AccAssetInfoDTO dto) throws Exception {

        //先查询所以一级专项
        StringBuffer sql0 = new StringBuffer();
        int number = 1;
        Map<Integer,Object> maps = new HashMap<>();
        sql0.append("SELECT * FROM specialinfo s WHERE 1=1 AND s.endflag='1' AND s.useflag='1' AND (s.super_special IS NULL OR s.super_special='') AND s.account=?"+number);
        maps.put(number,CurrentUser.getCurrentLoginAccount());
        List<?> specialList = accAssetCodeTypeRepository.queryBySqlSC(sql0.toString(),maps);
//        List<?> specialList = specialInfoRepository.queryLevelOneSpecial(CurrentUser.getCurrentLoginAccount());
        Map specialMap1 = new HashMap();
        Map specialMap2 = new HashMap();
        for (Object specialObj : specialList) {
            specialMap1.putAll((Map)specialObj);
            specialMap2.put(specialMap1.get("id").toString(),specialMap1.get("special_code"));
        }
            //格式化创建时间
            String centerCode = CurrentUser.getCurrentLoginManageBranch();
            String branchCode = CurrentUser.getCurrentLoginManageBranch();
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df1.format(new Date());
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");

            String[] codeTypeArr = dto.getCodeType().split(",");
            String[] cardCodeArr = dto.getCardCode().split(",");
            String[] yearMonthDateArr = dto.getUseStartDate().split(",");

            //并判断是否跨会计期间
            for(int i=0; i<yearMonthDateArr.length; i++){
                if(!(yearMonthDateArr[0].substring(0,4)+yearMonthDateArr[0].substring(5,7)).equals(
                        yearMonthDateArr[i].substring(0,4)+yearMonthDateArr[i].substring(5,7))){
                    return InvokeResult.failure("不允许跨会计期间操作！");
                }
            }
            for(int i=0; i<yearMonthDateArr.length; i++){
                //判断制单日期即凭证日期必须在当前会计期间内
                if(!(dto.getHandleDate().substring(0,4)+dto.getHandleDate().substring(5,7)).equals(yearMonthDateArr[0].substring(0,4)+yearMonthDateArr[0].substring(5,7))){
                    return InvokeResult.failure("制单日期请选择当前会计期间所在月份！");
                }
            }
            String voucherlist="";
            for(int i=0;i<cardCodeArr.length;i++){
//                StringBuffer sqls1=new StringBuffer("select * from accassetinfo where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and card_code='"+cardCodeArr[i]+"' ");
//                List<AccAssetInfo> lsit=(List<AccAssetInfo>)accAssetInfoRepository.queryBySql(sqls1.toString(),AccAssetInfo.class);
                List<AccAssetInfo> lsit=accAssetInfoRepository.queryAccAssetInfoByCenterCodeAndAccBookCodeAndCardCode(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),cardCodeArr[i]);
                if(lsit.size()>0&&lsit.get(0).getVoucherNo()!=null&&!lsit.get(0).getVoucherNo().equals("")){
                    voucherlist=voucherlist+" "+cardCodeArr[i];
                }
            }
            if(!voucherlist.equals("")){
                return InvokeResult.failure("卡片'"+voucherlist+"'已生成凭证，请勿重复生成！");
            }
            //会计期间获取
            AccAssetInfoId aaid = new AccAssetInfoId();
            aaid.setCenterCode(centerCode);//核算单位
            aaid.setBranchCode(branchCode);//基层单位
            aaid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
            aaid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
            aaid.setCodeType(codeTypeArr[0]);//管理类别编码
            aaid.setCardCode(cardCodeArr[0]);//卡片编码
            AccAssetInfo aa = accAssetInfoRepository.findById(aaid).get();
            String yearMonthDate = aa.getUseStartDate().substring(0,4)+aa.getUseStartDate().substring(5,7);//当前会计期间2019-01-01
            for(int l=0;l<codeTypeArr.length;l++){
                AccAssetInfoId aaiid = new AccAssetInfoId();
                aaiid.setCenterCode(centerCode);//核算单位
                aaiid.setBranchCode(branchCode);//基层单位
                aaiid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
                aaiid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
                aaiid.setCodeType(codeTypeArr[l]);//管理类别编码
                aaiid.setCardCode(cardCodeArr[l]);//卡片编码
                AccAssetInfo aai = accAssetInfoRepository.findById(aaiid).get();
                String useStartDate = aai.getUseStartDate().substring(0,4)+aai.getUseStartDate().substring(5,7);//当前会计期间2019-01-01

            //判断当前会计期间是否结转，如为结转则不允许进行凭证生成操作
//            StringBuffer sql1 = new StringBuffer();
//            sql1.append("select * from accmonthtrace where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  year_month_date = '"+ useStartDate +"' and acc_month_stat > 2 " +
//                    " and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");//已结转
//            List<?> list1 = accAssetInfoRepository.queryBySqlSC(sql1.toString());
            List<?> list1 = accMonthTraceRespository.queryAccMonthTraceByChooseMessage(CurrentUser.getCurrentLoginManageBranch(),useStartDate,CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount());
            if(list1.size()>0){
                return InvokeResult.failure("当前会计期间已结算，不可以进行凭证生成操作！");
            }
        }
        for(int l=0;l<codeTypeArr.length;l++){
            AccAssetInfoId aaiid = new AccAssetInfoId();
            aaiid.setCenterCode(centerCode);//核算单位
            aaiid.setBranchCode(branchCode);//基层单位
            aaiid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
            aaiid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
            aaiid.setCodeType(codeTypeArr[l]);//管理类别编码
            aaiid.setCardCode(cardCodeArr[l]);//卡片编码
            AccAssetInfo aai = accAssetInfoRepository.findById(aaiid).get();
            String useStartDate = aai.getUseStartDate().substring(0,4)+aai.getUseStartDate().substring(5,7);//当前会计期间2019-01-01

                //判断当前会计期间是否结转，如为结转则不允许进行凭证生成操作
               /* StringBuffer sql1 = new StringBuffer();
                sql1.append("select * from accmonthtrace where year_month_date = '"+ useStartDate +"' and acc_month_stat > 2 " +
                        " and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");//已结转
                List<?> list1 = accAssetInfoRepository.queryBySqlSC(sql1.toString());
                if(list1.size()>0){
                    return InvokeResult.failure("当前会计期间已结算，不可以进行凭证生成操作！");
                }*/

//-------------------------凭证主表录入开始------------------------------
                AccMainVoucherId amvId = new AccMainVoucherId();
                amvId.setCenterCode(centerCode);//核算单位
                amvId.setBranchCode(branchCode);//基层单位
                amvId.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
                amvId.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码

                //从accvoucherno表中获取最大凭证号
//                StringBuffer maxVoucherNoSql = new StringBuffer();
//                maxVoucherNoSql.append("select * from accvoucherno where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  year_month_date = '"+ useStartDate +"'" +
//                        " and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");
//                List<?> maxVoucherNoList = accVoucherNoRespository.queryBySql(maxVoucherNoSql.toString(), AccVoucherNo.class);
                List<?> maxVoucherNoList = accVoucherNoRespository.queryAccVoucherNoByChooseMessage(CurrentUser.getCurrentLoginManageBranch(),useStartDate,CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount());
                accVoucherNoRespository.flush();
                //通过年月 生成凭证号
                String maxVoucherNo = ((AccVoucherNo)maxVoucherNoList.get(0)).getVoucherNo();
                int len = 6-maxVoucherNo.length();
                for(int i=0; i<len; i++){//格式：00001
                    maxVoucherNo = "0"+maxVoucherNo;
                }
                maxVoucherNo = centerCode + ((AccVoucherNo)maxVoucherNoList.get(0)).getId().getYearMonthDate().substring(2,6)+maxVoucherNo;
                amvId.setYearMonthDate(((AccVoucherNo)maxVoucherNoList.get(0)).getId().getYearMonthDate());//年月
                amvId.setVoucherNo(maxVoucherNo);//凭证号

                AccMainVoucher amv = new AccMainVoucher();
                amv.setId(amvId);//联合主键
                amv.setVoucherType("3");//凭证类型
                amv.setGenerateWay("1");//录入方式
                //未加制单日期之前--凭证日期 系统当前日期
               /* String voucherDate = df2.format(new Date());
                if ((voucherDate.substring(0,4)+voucherDate.substring(5,7)).equals(useStartDate)) {
                    amv.setVoucherDate(voucherDate);
                } else {
                    int year = Integer.parseInt(useStartDate.substring(0,4));
                    int month = Integer.parseInt(useStartDate.substring(4));
                    amv.setVoucherDate(getLastDayOfYearMonth(year, month));
                }*/
               /*加上制单日期之后--凭证日期*/
                amv.setVoucherDate(dto.getHandleDate());

//            amv.setAuxNumber();//附件张数
//            amv.setCreateBranchCode();//制单人单位
                amv.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//制单人
//            amv.setApproveBranchCode();//复核人单位
//            amv.setApproveBy();//复核人
//            amv.setApproveDate();//复核日期
//            amv.setGeneBranchCode();//记账人单位
//            amv.setGeneBy();//记账人
//            amv.setGeneDate();//记账日期
                amv.setVoucherFlag("1");//生成凭证标志 未复核
//            amv.setFlag();//标志
                amv.setCreateTime(time);//创建时间
//            amv.setModifyReason();//修改理由
//            amv.setLastModifyBy();//最后一次修改人
//            amv.setLastModifyTime();//最后一次修改时间
//            amv.setTemp();//备注
                accMainVoucherRespository.saveAndFlush(amv);//凭证主表信息录入
//-------------------------凭证主表录入结束------------------------------

//-------------------------凭证子表录入开始------------------------------
                AccSubVoucherId asvId = new AccSubVoucherId();
                asvId.setCenterCode(centerCode);//核算单位
                asvId.setBranchCode(branchCode);//基层单位
                String accBookType = CurrentUser.getCurrentLoginAccountType();//账套类型
                asvId.setAccBookType(accBookType);//账套类型
                String accBookCode = CurrentUser.getCurrentLoginAccount();//账套编码
                asvId.setAccBookCode(accBookCode);//账套编码
                asvId.setYearMonthDate(((AccVoucherNo)maxVoucherNoList.get(0)).getId().getYearMonthDate());//年月 (生成方式见上方主表)
                asvId.setVoucherNo(maxVoucherNo);//凭证号 (生成方式见上方主表)
                //获取卡片来源
//                StringBuffer sqlss1=new StringBuffer();
//                sqlss1.append("select code_name from codemanage where code_type='sourceFlag' and code_code='"+aa.getSourceFlag()+"'");
//                List<?> sourceFlag= accAssetInfoRepository.queryBySql(sqlss1.toString());
                List<?> sourceFlag= accAssetInfoRepository.queryCardInfoByCodeCode(aa.getSourceFlag());

                //通过管理类别编码去获取科目代码、科目段、专项段
//                StringBuffer sql = new StringBuffer();
//                sql.append("select * from accassetcodetype where 1=1 \n" +
//                        " and acc_book_type = '"+ accBookType +"' \n" +
//                        "and acc_book_code = '"+ accBookCode +"' and code_type = '21' and asset_type = '"+ aai.getAssetType() +"'");
//                List<?> aactList = accAssetCodeTypeRepository.queryBySql(sql.toString(), AccAssetCodeType.class);
                List<AccAssetCodeType> aactList = accAssetCodeTypeRepository.queryAccAssetCodeTypeByAccBookTypeAndAccBookCodeAndAssetType(accBookType,accBookCode,aai.getAssetType());
                //因为两个借方，一个贷方，所以循环执行三次
                for(int i=0; i<3; i++){
                    AccSubVoucher asv = new AccSubVoucher();
                    asv.setId(asvId);//联合主键
                    //先根据主键去 凭证子表中查询，如果信息存在则凭证号+1，若不存在，凭证号为1
//                    StringBuffer sql2 = new StringBuffer();
//                    sql2.append("select * from accsubvoucher where center_code = '"+ centerCode +"' \n" +
//                            "and branch_code = '"+ branchCode +"' and acc_book_type ='"+ accBookType +"'\n" +
//                            "and acc_book_code = '"+ accBookCode +"' and year_month_date = '"+ ((AccVoucherNo)maxVoucherNoList.get(0)).getId().getYearMonthDate() +"'\n" +
//                            "and voucher_no = '"+ maxVoucherNo +"' and suffix_no=\n" +
//                            "(select MAX(suffix_no) from accsubvoucher where voucher_no = '"+ maxVoucherNo +"')");
//                        List<?> list = accSubVoucherRespository.queryBySql(sql2.toString(), AccSubVoucher.class);
                    List<AccSubVoucher> list = accSubVoucherRespository.queryAccSubVoucherByAccId(centerCode,branchCode,accBookType,accBookCode,((AccVoucherNo)maxVoucherNoList.get(0)).getId().getYearMonthDate(),maxVoucherNo,maxVoucherNo);
                    if(list.size()!=0){//存在，需要分录，分类号+1
                        asvId.setSuffixNo(String.valueOf(Integer.parseInt(((AccSubVoucher)list.get(0)).getId().getSuffixNo())+1));//凭证分录号
                    }else{//不存在，不需要分录，分类号为1
                        asvId.setSuffixNo("1");//凭证分录号
                    }
                    String monthDepreMoneyss = String.valueOf(aai.getEndDepreMoney().subtract(aai.getInitDepreMoney()));
                    switch (i){
                        case 0://借方1 加载固定资产类别编码管理保存的固定资产科目代码和名称
                            if(aai.getSum().toString().equals("0.00")){
                                continue;//金额为0，则不记录该分录
                            }
                            String itemCode1 = ((AccAssetCodeType)aactList.get(0)).getItemCode1();
                            String[] itemCodeArr = itemCode1.split("/");
                            if(itemCodeArr.length>=1){
                                asv.setItemCode(itemCodeArr[0]);//科目代码
                                asv.setF01(itemCodeArr[0]);
                            }
                            if(itemCodeArr.length>=2){asv.setF02(itemCodeArr[1]);}
                            if(itemCodeArr.length>=3){asv.setF03(itemCodeArr[2]);}
                            if(itemCodeArr.length>=4){asv.setF04(itemCodeArr[3]);}
                            if(itemCodeArr.length>=5){asv.setF05(itemCodeArr[4]);}
                            if(itemCodeArr.length>=6){asv.setF06(itemCodeArr[5]);}
                            if(itemCodeArr.length>=7){asv.setF07(itemCodeArr[6]);}
                            if(itemCodeArr.length>=8){asv.setF08(itemCodeArr[7]);}
                            if(itemCodeArr.length>=9){asv.setF09(itemCodeArr[8]);}
                            if(itemCodeArr.length>=10){asv.setF10(itemCodeArr[9]);}
                            if(itemCodeArr.length>=11){asv.setF11(itemCodeArr[10]);}
                            if(itemCodeArr.length>=12){asv.setF12(itemCodeArr[11]);}
                            if(itemCodeArr.length>=13){asv.setF13(itemCodeArr[12]);}
                            if(itemCodeArr.length>=14){asv.setF14(itemCodeArr[13]);}
                            if(itemCodeArr.length>=15){asv.setF15(itemCodeArr[14]);}

                        //根据专项去判断科目去判断专项的存放位置
                        String specialCode = ((AccAssetCodeType)aactList.get(0)).getArticleCode1();
                        String flag = qrySegmentFlag(specialCode);//用来判断存放位置

                        if(flag!=null && !flag.equals("")){
                            if(flag.equals("s01")){asv.setS01(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s02")){asv.setS02(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s03")){asv.setS03(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s04")){asv.setS04(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s05")){asv.setS05(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s06")){asv.setS06(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s07")){asv.setS07(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s08")){asv.setS08(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s09")){asv.setS09(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s10")){asv.setS10(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s11")){asv.setS11(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s12")){asv.setS12(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s13")){asv.setS13(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s14")){asv.setS14(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s15")){asv.setS15(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s16")){asv.setS16(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s17")){asv.setS17(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s18")){asv.setS18(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s19")){asv.setS19(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                            if(flag.equals("s20")){asv.setS20(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}
                        }
                        String directionidx=((AccAssetCodeType)aactList.get(0)).getItemCode1();
                        if(directionidx.charAt(directionidx.length()-1)!='/'){directionidx=directionidx+"/";}
                        asv.setDirectionIdx(directionidx);//科目方向段

                        //根据科目代码去获取科目名称
                        StringBuffer directionIdxName = new StringBuffer();
                        String itemCodeStr="";
                        for(int j=0; j<itemCodeArr.length; j++){
//                            StringBuffer nameSql = new StringBuffer();
                            itemCodeStr=itemCodeStr+itemCodeArr[j]+"/";
//                            nameSql.append("select * from subjectinfo where CONCAT(all_subject,subject_code,'/')='"+itemCodeStr+" ' and account='"+CurrentUser.getCurrentLoginAccount()+"'\n");
//                            List<?> nameList = accAssetCodeTypeRepository.queryBySql(nameSql.toString(), SubjectInfo.class);
                            List<SubjectInfo> nameList = subjectRepository.querySubjectInfoByAccountAndAllsubject(CurrentUser.getCurrentLoginAccount(),itemCodeStr);
                            if(j==0){//第一次追加科目名称直接追加，非第一次追加在前面加"/"
                                directionIdxName.append(((SubjectInfo)nameList.get(0)).getSubjectName());
                            }else{
                                directionIdxName.append("/"+((SubjectInfo)nameList.get(0)).getSubjectName());
                            }
                        }
                        String directionidxNames=directionIdxName.toString();
                        if(directionidxNames.charAt(directionidxNames.length()-1)!='/'){directionidxNames=directionidxNames+"/";}
                        asv.setDirectionIdxName(directionidxNames);//科目方向段名称

                        asv.setDirectionOther(((AccAssetCodeType)aactList.get(0)).getArticleCode1());//专项方向段
                        asv.setRemark(String.valueOf(sourceFlag.get(0))+aa.getAssetName()+" "+aa.getQuantity()+" "+aa.getMetricName());//摘要 默认置空
                        asv.setCurrency(currency);//原币别编码
                        asv.setExchangeRate(new BigDecimal(0));//当前汇率 默认0
                        asv.setDebitSource(aai.getSum());//原币借方金额 同一固定资产类别编码下卡片“金额”字段值
                        asv.setCreditSource(new BigDecimal(0));//原币贷方金额
                        asv.setDebitDest(aai.getSum());//本位币借方金额 值同原币
                        asv.setCreditDest(new BigDecimal(0));//本位币贷方金额
                        asv.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//制单人
                        asv.setCreateTime(time);//创建时间
                        accSubVoucherRespository.saveAndFlush(asv);//凭证子表信息录入
                        break;
                    case 1://借方2
                        if(aai.getInputTax().toString().equals("0.00")){
                            continue;//金额为0不记录该分录
                        }
                        String itemCode4 = ((AccAssetCodeType)aactList.get(0)).getItemCode4();
                        String[] itemCodeArr2 = itemCode4.split("/");
                        if(itemCodeArr2.length>=1){
                            asv.setItemCode(itemCodeArr2[0]);//科目代码
                            asv.setF01(itemCodeArr2[0]);
                        }
                        if(itemCodeArr2.length>=2){asv.setF02(itemCodeArr2[1]);}
                        if(itemCodeArr2.length>=3){asv.setF03(itemCodeArr2[2]);}
                        if(itemCodeArr2.length>=4){asv.setF04(itemCodeArr2[3]);}
                        if(itemCodeArr2.length>=5){asv.setF05(itemCodeArr2[4]);}
                        if(itemCodeArr2.length>=6){asv.setF06(itemCodeArr2[5]);}
                        if(itemCodeArr2.length>=7){asv.setF07(itemCodeArr2[6]);}
                        if(itemCodeArr2.length>=8){asv.setF08(itemCodeArr2[7]);}
                        if(itemCodeArr2.length>=9){asv.setF09(itemCodeArr2[8]);}
                        if(itemCodeArr2.length>=10){asv.setF10(itemCodeArr2[9]);}
                        if(itemCodeArr2.length>=11){asv.setF11(itemCodeArr2[10]);}
                        if(itemCodeArr2.length>=12){asv.setF12(itemCodeArr2[11]);}
                        if(itemCodeArr2.length>=13){asv.setF13(itemCodeArr2[12]);}
                        if(itemCodeArr2.length>=14){asv.setF14(itemCodeArr2[13]);}
                        if(itemCodeArr2.length>=15){asv.setF15(itemCodeArr2[14]);}
//                            asv.setS09(((AccAssetCodeType)aactList.get(0)).getArticleCode4());//

                        //根据专项去判断科目去判断专项的存放位置
                        String specialCode2 = ((AccAssetCodeType)aactList.get(0)).getArticleCode4();
                        String flag2 = qrySegmentFlag(specialCode2);//用来判断存放位置

                        if(flag2!=null && !flag2.equals("")){
                            if(flag2.equals("s01")){asv.setS01(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s02")){asv.setS02(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s03")){asv.setS03(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s04")){asv.setS04(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s05")){asv.setS05(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s06")){asv.setS06(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s07")){asv.setS07(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s08")){asv.setS08(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s09")){asv.setS09(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s10")){asv.setS10(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s11")){asv.setS11(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s12")){asv.setS12(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s13")){asv.setS13(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s14")){asv.setS14(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s15")){asv.setS15(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s16")){asv.setS16(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s17")){asv.setS17(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s18")){asv.setS18(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s19")){asv.setS19(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                            if(flag2.equals("s20")){asv.setS20(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}
                        }
                        String directionidx2=((AccAssetCodeType)aactList.get(0)).getItemCode4();
                        if(directionidx2.charAt(directionidx2.length()-1)!='/'){directionidx2=directionidx2+"/";}
                        asv.setDirectionIdx(directionidx2);//科目方向段

                        //根据科目代码去获取科目名称
                        StringBuffer directionIdxName2 = new StringBuffer();
                        String itemCodeStr1="";
                        for(int j=0; j<itemCodeArr2.length; j++){
//                            StringBuffer nameSql2 = new StringBuffer();
                            itemCodeStr1=itemCodeStr1+itemCodeArr2[j]+"/";
//                            nameSql2.append("select * from subjectinfo where CONCAT(all_subject,subject_code,'/')='"+itemCodeStr1+" ' and account='"+CurrentUser.getCurrentLoginAccount()+"'\n");
//                            List<?> nameList = accAssetCodeTypeRepository.queryBySql(nameSql2.toString(), SubjectInfo.class);
                            List<SubjectInfo> nameList = subjectRepository.querySubjectInfoByAccountAndAllsubject(CurrentUser.getCurrentLoginAccount(),itemCodeStr1);
                            if(j==0){//第一次追加科目名称直接追加，非第一次追加在前面加","
                                directionIdxName2.append(((SubjectInfo)nameList.get(0)).getSubjectName());
                            }else{
                                directionIdxName2.append("/"+((SubjectInfo)nameList.get(0)).getSubjectName());
                            }
                        }
                        String directionidxNames2=directionIdxName2.toString();
                        if(directionidxNames2.charAt(directionidxNames2.length()-1)!='/'){directionidxNames2=directionidxNames2+"/";}
                        asv.setDirectionIdxName(directionidxNames2);//科目方向段名称

                        asv.setDirectionOther(((AccAssetCodeType)aactList.get(0)).getArticleCode4());//专项方向段
                        asv.setRemark(String.valueOf(sourceFlag.get(0))+aa.getAssetName()+" "+aa.getQuantity()+" "+aa.getMetricName());//摘要 默认置空
                        asv.setCurrency(currency);//原币别编码
                        asv.setExchangeRate(new BigDecimal(0));//当前汇率 默认0
                        asv.setDebitSource(aai.getInputTax());//原币借方金额 同一固定资产类别编码下卡片“进项税额”字段值
                        asv.setCreditSource(new BigDecimal(0));//原币贷方金额
                        asv.setDebitDest(aai.getInputTax());//本位币借方金额 值同原币
                        asv.setCreditDest(new BigDecimal(0));//本位币贷方金额
                        asv.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//制单人
                        asv.setCreateTime(time);//创建时间
                        accSubVoucherRespository.saveAndFlush(asv);//凭证子表信息录入
                        break;
                    case 2:
                        if(aai.getInputTax().add(aai.getSum()).toString().equals("0.00")){
                            continue;//金额为0不记录该分录
                        }
                        String payWay = aai.getPayWay();
                        String payCode = aai.getPayCode();
                        String[] payWayArr = payWay.split("/");
                        if(payWayArr.length>=1){
                            asv.setItemCode(payWayArr[0]);//科目代码
                            asv.setF01(payWayArr[0]);
                        }
                        if(payWayArr.length>=2){asv.setF02(payWayArr[1]);}
                        if(payWayArr.length>=3){asv.setF03(payWayArr[2]);}
                        if(payWayArr.length>=4){asv.setF04(payWayArr[3]);}
                        if(payWayArr.length>=5){asv.setF05(payWayArr[4]);}
                        if(payWayArr.length>=6){asv.setF06(payWayArr[5]);}
                        if(payWayArr.length>=7){asv.setF07(payWayArr[6]);}
                        if(payWayArr.length>=8){asv.setF08(payWayArr[7]);}
                        if(payWayArr.length>=9){asv.setF09(payWayArr[8]);}
                        if(payWayArr.length>=10){asv.setF10(payWayArr[9]);}
                        if(payWayArr.length>=11){asv.setF11(payWayArr[10]);}
                        if(payWayArr.length>=12){asv.setF12(payWayArr[11]);}
                        if(payWayArr.length>=13){asv.setF13(payWayArr[12]);}
                        if(payWayArr.length>=14){asv.setF14(payWayArr[13]);}
                        if(payWayArr.length>=15){asv.setF15(payWayArr[14]);}
//                            asv.setS09(aai.getPayCode());//
                        String flag3=null;
                        int invitNo = 1;
                        Map<Integer,Object> invits = new HashMap<>();

                        StringBuffer sql7 = new StringBuffer();
//                        sql7.append("SELECT * FROM subjectinfo WHERE account='"+CurrentUser.getCurrentLoginAccount()+"' AND CONCAT(all_subject,subject_code,'/')='"+payWay+"'");
                        sql7.append("SELECT * FROM subjectinfo WHERE ");
                        sql7.append(" account =?"+invitNo);
                        invits.put(invitNo,CurrentUser.getCurrentLoginAccount());
                        invitNo++;
                        sql7.append(" AND CONCAT(all_subject,subject_code,'/')=?"+invitNo);
                        invits.put(invitNo,payWay);
                        invitNo++;
                        List<?> specialCount2 = accSubVoucherRespository.queryBySqlSC(sql7.toString(),invits);
//                        List<SubjectInfo> specialCount2 = subjectRepository.querySubjectInfoByAccountAndAllsubject(CurrentUser.getCurrentLoginAccount(),payWay);
                        if(specialCount2.size()<= 0){
                            throw new Exception();
                        }
                        for (Object o : specialCount2) {
                            Map specialCountMap = new HashMap();
                            specialCountMap.putAll((Map)o);
                            String[] specialIds = specialCountMap.get("special_id").toString().split(",");
                            // 213,218
                            Set<Map.Entry<String,String>> entries = specialMap2.entrySet();
                            for (Map.Entry<String, String> entry : entries) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                if(specialIds.length==1&&specialIds[0].equals(key)){
                                    String specialCode4 = payCode;
                                    if(specialCode4==null||specialCode4.equals("")){
                                        //科目有专项 类别无专项 出错
                                        throw new Exception("false");
                                    }
                                    flag3 = qrySegmentFlag(specialCode4);//用来判断存放位置
                                    setValue2(flag3,asv,payCode,payWay);
                                    asv.setDirectionOther(payCode);//专项方向段

                                };
                                // 213,218
                                if(specialIds.length>1) {
                                    if (specialIds[0].equals(key)&&value.equals("ZJDB")){
                                        int paramsNo = 1 ;
                                        Map<Integer,Object> params = new HashMap<>();
                                        StringBuilder sql9 = new StringBuilder();
                                        sql9.append("SELECT s.special_code FROM specialinfo s INNER JOIN AccAssetInfo a ON s.special_code= a.pay_code where 1=1  " );
//                                                "AND a.asset_code='" + aai.getAssetCode() + "' AND a.acc_book_code='" + CurrentUser.getCurrentLoginAccount() + "' AND a.acc_book_type='" + CurrentUser.getCurrentLoginAccountType() + "' AND s.account = '"+CurrentUser.getCurrentLoginAccount() +"'");
                                        sql9.append(" and a.asset_code=?"+paramsNo);
                                        params.put(paramsNo,aai.getAssetCode());
                                        paramsNo++;
                                        sql9.append(" and a.acc_book_code = ?"+paramsNo);
                                        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                                        paramsNo++;
                                        sql9.append(" and a.acc_book_type=?"+paramsNo);
                                        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
                                        paramsNo++;
                                        sql9.append(" and s.account = ?"+paramsNo);
                                        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                                        paramsNo++;
                                        List<?> branchList = accGCheckInfoRepository.queryBySql(sql9.toString(),params);
                                        for (Object code : branchList) {
//                                                String branchCode = (String) code;
                                            String defaultValue = "ZJDBN";
                                            flag2 = qrySegmentFlag(defaultValue);//用来判断存放位置
                                            if (flag2 != null && !flag2.equals("")) {
                                                if (flag2.equals("s01")) {
                                                    asv.setS01(defaultValue);
                                                } else if (flag2.equals("s02")) {
                                                    asv.setS02(defaultValue);
                                                } else if (flag2.equals("s03")) {
                                                    asv.setS03(defaultValue);
                                                } else if (flag2.equals("s04")) {
                                                    asv.setS04(defaultValue);
                                                } else if (flag2.equals("s05")) {
                                                    asv.setS05(defaultValue);
                                                } else if (flag2.equals("s06")) {
                                                    asv.setS06(defaultValue);
                                                } else if (flag2.equals("s07")) {
                                                    asv.setS07(defaultValue);
                                                } else if (flag2.equals("s08")) {
                                                    asv.setS08(defaultValue);
                                                } else if (flag2.equals("s09")) {
                                                    asv.setS09(defaultValue);
                                                } else if (flag2.equals("s10")) {
                                                    asv.setS10(defaultValue);
                                                } else if (flag2.equals("s11")) {
                                                    asv.setS11(defaultValue);
                                                } else if (flag2.equals("s12")) {
                                                    asv.setS12(defaultValue);
                                                } else if (flag2.equals("s13")) {
                                                    asv.setS13(defaultValue);
                                                } else if (flag2.equals("s14")) {
                                                    asv.setS14(defaultValue);
                                                } else if (flag2.equals("s15")) {
                                                    asv.setS15(defaultValue);
                                                } else if (flag2.equals("s16")) {
                                                    asv.setS16(defaultValue);
                                                } else if (flag2.equals("s17")) {
                                                    asv.setS17(defaultValue);
                                                } else if (flag2.equals("s18")) {
                                                    asv.setS18(defaultValue);
                                                } else if (flag2.equals("s19")) {
                                                    asv.setS19(defaultValue);
                                                } else if (flag2.equals("s20")) {
                                                    asv.setS20(defaultValue);
                                                }
                                            }

                                            String directionOther=payCode;
                                            flag2 = qrySegmentFlag(directionOther);//用来判断存放位置
                                            setValue2(flag2,asv,payCode,payWay);
                                            if(directionOther==null||directionOther.equals("")){
                                                //科目有专项 类别无专项 出错
                                                throw new Exception("false");
                                            }
                                            if(defaultValue==null||defaultValue.equals("")){
                                                //卡片使用部门专项为空，科目不为null
                                                throw new Exception("unitfalse");
                                            }
                                            String directionOtherValue=defaultValue+","+directionOther;
                                            asv.setDirectionOther(directionOtherValue);
                                        };
                                    }
                                    if (specialIds[1].equals(key)&&value.equals("ZJDB")){
                                        int paramsNo = 1 ;
                                        Map<Integer,Object> params = new HashMap<>();

                                        StringBuilder sql9 = new StringBuilder();
                                        sql9.append("SELECT s.special_code FROM specialinfo s INNER JOIN AccAssetInfo a ON s.special_code= a.pay_code where 1=1  " );
//                                                "AND a.asset_code='" + aai.getAssetCode() + "' AND a.acc_book_code='" + CurrentUser.getCurrentLoginAccount() + "' AND a.acc_book_type='" + CurrentUser.getCurrentLoginAccountType() + "' AND s.account = '"+CurrentUser.getCurrentLoginAccount() +"'");
                                        sql9.append(" and a.asset_code=?"+paramsNo);
                                        params.put(paramsNo,aai.getAssetCode());
                                        paramsNo++;
                                        sql9.append(" and a.acc_book_code = ?"+paramsNo);
                                        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                                        paramsNo++;
                                        sql9.append(" and a.acc_book_type=?"+paramsNo);
                                        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
                                        paramsNo++;
                                        sql9.append(" and s.account = ?"+paramsNo);
                                        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                                        paramsNo++;
                                        List<?> branchList = accGCheckInfoRepository.queryBySql(sql9.toString(),params);
                                        for (Object code : branchList) {
//                                                String branchCode = (String) code;
                                            String defaultValue = "ZJDBN";
                                            flag2 = qrySegmentFlag(defaultValue);//用来判断存放位置
                                            if (flag2 != null && !flag2.equals("")) {
                                                if (flag2.equals("s01")) {
                                                    asv.setS01(defaultValue);
                                                } else if (flag2.equals("s02")) {
                                                    asv.setS02(defaultValue);
                                                } else if (flag2.equals("s03")) {
                                                    asv.setS03(defaultValue);
                                                } else if (flag2.equals("s04")) {
                                                    asv.setS04(defaultValue);
                                                } else if (flag2.equals("s05")) {
                                                    asv.setS05(defaultValue);
                                                } else if (flag2.equals("s06")) {
                                                    asv.setS06(defaultValue);
                                                } else if (flag2.equals("s07")) {
                                                    asv.setS07(defaultValue);
                                                } else if (flag2.equals("s08")) {
                                                    asv.setS08(defaultValue);
                                                } else if (flag2.equals("s09")) {
                                                    asv.setS09(defaultValue);
                                                } else if (flag2.equals("s10")) {
                                                    asv.setS10(defaultValue);
                                                } else if (flag2.equals("s11")) {
                                                    asv.setS11(defaultValue);
                                                } else if (flag2.equals("s12")) {
                                                    asv.setS12(defaultValue);
                                                } else if (flag2.equals("s13")) {
                                                    asv.setS13(defaultValue);
                                                } else if (flag2.equals("s14")) {
                                                    asv.setS14(defaultValue);
                                                } else if (flag2.equals("s15")) {
                                                    asv.setS15(defaultValue);
                                                } else if (flag2.equals("s16")) {
                                                    asv.setS16(defaultValue);
                                                } else if (flag2.equals("s17")) {
                                                    asv.setS17(defaultValue);
                                                } else if (flag2.equals("s18")) {
                                                    asv.setS18(defaultValue);
                                                } else if (flag2.equals("s19")) {
                                                    asv.setS19(defaultValue);
                                                } else if (flag2.equals("s20")) {
                                                    asv.setS20(defaultValue);
                                                }
                                            }

                                            String directionOther=payCode;
                                            flag2 = qrySegmentFlag(directionOther);//用来判断存放位置
                                            setValue2(flag2,asv,payCode,payWay);
                                            if(directionOther==null||directionOther.equals("")){
                                                //科目有专项 类别无专项 出错
                                                throw new Exception("false");
                                            }
                                            if(defaultValue==null||defaultValue.equals("")){
                                                //卡片使用部门专项为空，科目不为null
                                                throw new Exception("unitfalse");
                                            }
                                            String directionOtherValue=defaultValue+","+directionOther;
                                            asv.setDirectionOther(directionOtherValue);
                                        };
                                    }
                                }
                            }
                        }

//                        //根据专项去判断科目去判断专项的存放位置
//                        String specialCode3 = aai.getPayCode();
//                        String flag3 = qrySegmentFlag(specialCode3);//用来判断存放位置
//
//                        if(flag3!=null && !flag3.equals("")){
//                            if(flag3.equals("s01")){asv.setS01(aai.getPayCode());}else
//                            if(flag3.equals("s02")){asv.setS02(aai.getPayCode());}else
//                            if(flag3.equals("s03")){asv.setS03(aai.getPayCode());}else
//                            if(flag3.equals("s04")){asv.setS04(aai.getPayCode());}else
//                            if(flag3.equals("s05")){asv.setS05(aai.getPayCode());}else
//                            if(flag3.equals("s06")){asv.setS06(aai.getPayCode());}else
//                            if(flag3.equals("s07")){asv.setS07(aai.getPayCode());}else
//                            if(flag3.equals("s08")){asv.setS08(aai.getPayCode());}else
//                            if(flag3.equals("s09")){asv.setS09(aai.getPayCode());}else
//                            if(flag3.equals("s10")){asv.setS10(aai.getPayCode());}else
//                            if(flag3.equals("s11")){asv.setS11(aai.getPayCode());}else
//                            if(flag3.equals("s12")){asv.setS12(aai.getPayCode());}else
//                            if(flag3.equals("s13")){asv.setS13(aai.getPayCode());}else
//                            if(flag3.equals("s14")){asv.setS14(aai.getPayCode());}else
//                            if(flag3.equals("s15")){asv.setS15(aai.getPayCode());}else
//                            if(flag3.equals("s16")){asv.setS16(aai.getPayCode());}else
//                            if(flag3.equals("s17")){asv.setS17(aai.getPayCode());}else
//                            if(flag3.equals("s18")){asv.setS18(aai.getPayCode());}else
//                            if(flag3.equals("s19")){asv.setS19(aai.getPayCode());}else
//                            if(flag3.equals("s20")){asv.setS20(aai.getPayCode());}
//                        }
                        String directionidx3=aai.getPayWay();
                        if(directionidx3.charAt(directionidx3.length()-1)!='/'){directionidx3=directionidx3+"/";}
                        asv.setDirectionIdx(directionidx3);//科目方向段

                        //根据科目代码去获取科目名称
                        StringBuffer directionIdxName3 = new StringBuffer();
                        String payWayArr1="";
                        for(int j=0; j<payWayArr.length; j++){
//                            StringBuffer nameSql3 = new StringBuffer();
                            payWayArr1=payWayArr1+payWayArr[j]+"/";
//                            nameSql3.append("select * from subjectinfo where CONCAT(all_subject,subject_code,'/')='"+payWayArr1+" ' and account='"+CurrentUser.getCurrentLoginAccount()+"'\n");
//                            List<?> nameList = accAssetCodeTypeRepository.queryBySql(nameSql3.toString(), SubjectInfo.class);
                            List<SubjectInfo> nameList = subjectRepository.querySubjectInfoByAccountAndAllsubject(CurrentUser.getCurrentLoginAccount(),payWayArr1);
                            if(j==0){//第一次追加科目名称直接追加，非第一次追加在前面加","
                                directionIdxName3.append(((SubjectInfo)nameList.get(0)).getSubjectName());
                            }else{
                                directionIdxName3.append("/"+((SubjectInfo)nameList.get(0)).getSubjectName());
                            }
                        }
                        String directionidxNames3=directionIdxName3.toString();
                        if(directionidxNames3.charAt(directionidxNames3.length()-1)!='/'){directionidxNames3=directionidxNames3+"/";}
                        asv.setDirectionIdxName(directionidxNames3);//科目方向段名称
                        //判断科目专项是否相同，合并金额
//                        StringBuffer sqls4=new StringBuffer();
//                        sqls4.append("select * from accsubvoucher where acc_book_code='"+accBookCode+"' and voucher_no='"+maxVoucherNo+"' and direction_idx='"+asv.getDirectionIdx()+"' and direction_other='"+asv.getDirectionOther()+"'");
//                        List<AccSubVoucher> accsubList2=(List<AccSubVoucher>) accSubVoucherRespository.queryBySql(sqls4.toString(),AccSubVoucher.class);
                        List<AccSubVoucher> accsubList2=accSubVoucherRespository.queryAccSubVoucherByAccBookCodeAndVoucherNoAndDirectionIdxAndDirectionOther(accBookCode,maxVoucherNo,asv.getDirectionIdx(),asv.getDirectionOther());
                        if(accsubList2!=null&&accsubList2.size()>0){
                            AccSubVoucher accsub=accsubList2.get(0);
                            accsub.setDebitSource(new BigDecimal(0));//原币借方金额
                            //  asv.setCreditSource(new BigDecimal(aaiMap.get("end_depre_money").toString()));//原币贷方金额 固定资产计提折旧合计
                            accsub.setCreditSource(accsub.getCreditSource().add(new BigDecimal(monthDepreMoneyss)));//原币贷方金额 固定资产计提折旧合计
                            accsub.setDebitDest(new BigDecimal(0));//本位币借方金额
                            //  asv.setCreditDest(new BigDecimal(aaiMap.get("end_depre_money").toString()));//本位币贷方金额 固定资产计提折旧合计
                            accsub.setCreditDest(accsub.getCreditDest().add(new BigDecimal(monthDepreMoneyss)));//本位币贷方金额 固定资产计提折旧合计

                            accSubVoucherRespository.save(accsub);//凭证子表信息录入
                            break;
                        }
//                        asv.setDirectionOther(aai.getPayCode());//专项方向段
                        asv.setRemark(String.valueOf(sourceFlag.get(0))+aa.getAssetName()+" "+aa.getQuantity()+" "+aa.getMetricName());//摘要 默认置空
                        asv.setCurrency(currency);//原币别编码
                        asv.setExchangeRate(new BigDecimal(0));//当前汇率 默认0
                        asv.setDebitSource(new BigDecimal(0));//原币借方金额
                        asv.setCreditSource(aai.getInputTax().add(aai.getSum()));//原币贷方金额 借方合计
                        asv.setDebitDest(new BigDecimal(0));//本位币借方金额
                        asv.setCreditDest(aai.getInputTax().add(aai.getSum()));//本位币贷方金额 借方合计
                        asv.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//制单人
                        asv.setCreateTime(time);//创建时间
                        accSubVoucherRespository.saveAndFlush(asv);//凭证子表信息录入
                        break;
                }
            }
//-------------------------凭证子表录入结束------------------------------

            //固定资产基本信息表 卡片凭证号添加
            aai.setVoucherNo(maxVoucherNo);//设置凭证号
            accAssetInfoRepository.saveAndFlush(aai);//保存

        }
        //保存成功更新最大会计期间
        accVoucherNoRespository.updateAddVoucherNo(yearMonthDate, CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount(),CurrentUser.getCurrentLoginManageBranch());
        return InvokeResult.success();
    }

    /**
     * 固定资产卡片凭证回退
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult revokeVoucher(AccAssetInfoDTO dto) {
            String centerCode = CurrentUser.getCurrentLoginManageBranch();
            String branchCode = CurrentUser.getCurrentLoginManageBranch();
            String[] codeTypeArr = dto.getCodeType().split(",");
            String[] cardCodeArr = dto.getCardCode().split(",");
            String[] yearMonthDateArr = dto.getUseStartDate().split(",");

            //并判断是否跨会计期间
//            for(int i=0; i<yearMonthDateArr.length; i++){
//                if(!(yearMonthDateArr[0].substring(0,4)+yearMonthDateArr[0].substring(5,7)).equals(
//                        yearMonthDateArr[i].substring(0,4)+yearMonthDateArr[i].substring(5,7))){
//                    return InvokeResult.failure("不允许跨会计期间操作！");
//                }
//            }

        //判读该凭证是否复核记账
        List vouchersum=new ArrayList();
        String voucherstr="";
        for(int l=0;l<codeTypeArr.length;l++) {
            AccAssetInfoId aaiid = new AccAssetInfoId();
            aaiid.setCenterCode(centerCode);//核算单位
            aaiid.setBranchCode(branchCode);//基层单位
            aaiid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
            aaiid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
            aaiid.setCodeType(codeTypeArr[l]);//管理类别编码
            aaiid.setCardCode(cardCodeArr[l]);//卡片编码
            AccAssetInfo aai = accAssetInfoRepository.findById(aaiid).get();
            String voucherNo = aai.getVoucherNo();//凭证号
            int paramsNo = 1;
            Map<Integer,Object> params = new HashMap<>();
            StringBuffer sql = new StringBuffer();
            sql.append("select * from accmainvoucher a where 1=1 ");
            sql.append(" and a.center_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append(" and  a.acc_book_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append(" and a.voucher_no =?"+paramsNo);
            params.put(paramsNo,voucherNo);
            paramsNo++;
            sql.append(" UNION all select * from accmainvoucherhis b where");
            sql.append(" b.voucher_no = ?"+paramsNo);
            params.put(paramsNo,voucherNo);
            paramsNo++;
            sql.append(" and b.acc_book_code=?"+paramsNo);
            params.put(paramsNo, CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            List<?> list = accMainVoucherRespository.queryBySql(sql.toString(), params ,AccMainVoucher.class);
            if(!((AccMainVoucher)list.get(0)).getVoucherFlag().equals("1")){//非未复核状态的凭证，需要记账回退，撤销复核，才可以执行生帐回退操作
                int flag=0;
                if(vouchersum!=null&&vouchersum.size()>0){
                    for(int i=0;i<vouchersum.size();i++){
                        if(vouchersum.get(i).equals(voucherNo)){
                            flag=1;
                        }
                    }
                }
                if(flag==0){
                    vouchersum.add(voucherNo);
                    voucherstr=voucherstr+" "+voucherNo;
                }
            }
        }
        if(vouchersum!=null&&vouchersum.size()>0){
            return InvokeResult.failure("凭证"+ voucherstr +"已复核记账，不可回退该凭证");
        }
        List jiezhuanlist=new ArrayList();
        String jiezhuanstr="";
        List zhejiulist=new ArrayList();
        String zhejiustr="";
        for(int i=0; i<yearMonthDateArr.length; i++){
//            StringBuffer sql1 = new StringBuffer();
//            sql1.append("select * from accmonthtrace where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and year_month_date = '"+ yearMonthDateArr[i].substring(0,4)+yearMonthDateArr[i].substring(5,7) +"' and acc_month_stat > 2 " +
//                    " and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");//已结转
//            List<?> list1 = accAssetInfoRepository.queryBySqlSC(sql1.toString());
            List<?> list1 = accMonthTraceRespository.queryAccMonthTraceByChooseMessage(CurrentUser.getCurrentLoginManageBranch(),yearMonthDateArr[i].substring(0,4)+yearMonthDateArr[i].substring(5,7),CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount());
            String yearStartDate12=yearMonthDateArr[i].substring(0,4)+yearMonthDateArr[i].substring(5,7);
            if(list1.size()>0){
                if(!jiezhuanlist.contains(yearStartDate12)){
                    jiezhuanstr=jiezhuanstr+" "+yearStartDate12;
                }
                jiezhuanlist.add(yearStartDate12);
                // return InvokeResult.failure("当前会计期间 "+yearMonthDateArr[0].substring(0,4)+yearMonthDateArr[0].substring(5,7)+" 已结转，不可以进行凭证回退操作！");
            }
            //折旧状态的会计期间不能回退
//            StringBuffer sqls1=new StringBuffer("select * from AccGCheckInfo  where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and year_month_date='"+yearStartDate12+"'");
//            List<AccGCheckInfo> accGcheckinfo=(List<AccGCheckInfo>)accMainVoucherRespository.queryBySql(sqls1.toString(),AccGCheckInfo.class);
            List<AccGCheckInfo> accGcheckinfo=accGCheckInfoRepository.queryAccGCheckInfoByCenterCodeAndAccBookCodeAndYearMonthDate(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),yearStartDate12);
            if(accGcheckinfo!=null&&accGcheckinfo.size()>0){
                if(!accGcheckinfo.get(0).getFlag().equals("0")){
                    if(!zhejiulist.contains(yearStartDate12)){
                        zhejiustr=zhejiustr+" "+yearStartDate12;
                    }
                    zhejiulist.add(yearStartDate12);
                    // return InvokeResult.failure("会计期间 "+ yearMonthDateArr[0].substring(0,4)+yearMonthDateArr[0].substring(5,7) +" 已折旧，不可回退该凭证");
                }
            }
        }
        if(jiezhuanlist.size()>0){
            return InvokeResult.failure("当前会计期间"+jiezhuanstr+"已结转，不可以进行凭证回退操作！");
        }
        if(zhejiulist.size()>0){
            return InvokeResult.failure("会计期间 "+zhejiustr+" 已折旧，不可回退该凭证");
        }
        for(int l=0;l<codeTypeArr.length;l++) {
            AccAssetInfoId aaiid = new AccAssetInfoId();
            aaiid.setCenterCode(centerCode);//核算单位
            aaiid.setBranchCode(branchCode);//基层单位
            aaiid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
            aaiid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
            aaiid.setCodeType(codeTypeArr[l]);//管理类别编码
            aaiid.setCardCode(cardCodeArr[l]);//卡片编码
            AccAssetInfo aai = accAssetInfoRepository.findById(aaiid).get();
            String voucherNo = aai.getVoucherNo();//凭证号
            String useStartDate = aai.getUseStartDate().substring(0,4)+aai.getUseStartDate().substring(5,7);//当前会计期间
//                //判断当前卡片凭证是否已经符合记账
//                StringBuffer sql = new StringBuffer();
//                sql.append("select * from accmainvoucher a where a.voucher_no = '"+ voucherNo +"'");
//                List<?> list = accMainVoucherRespository.queryBySql(sql.toString(), AccMainVoucher.class);
//                if(list.size()==0){
//                    return InvokeResult.failure("凭证"+ voucherNo +"已为回退状态！");
//                }
//                if(!((AccMainVoucher)list.get(0)).getVoucherFlag().equals("1")){//非未复核状态的凭证，需要记账回退，撤销复核，才可以执行生帐回退操作
//                    return InvokeResult.failure("凭证"+ voucherNo +"已复核记账，不可回退该凭证");
//                }



                //固定资产会计期间状态修改
//                StringBuffer sql = new StringBuffer();
//                sql.append("select * from AccDepre where voucher_no = "+ voucherNo +"" +
//                        " and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");
//                List<?> adList = accDepreRepository.queryBySql(sql.toString(), AccDepre.class);
//                if(adList.size()>0){
//                    for(int i=0; i<adList.size(); i++){
//                        AccGCheckInfoId agid = new AccGCheckInfoId();//固定资产会计期间信息获取
//                        agid.setCenterCode(aai.getId().getCenterCode());//核算单位
//                        agid.setYearMonthDate(((AccDepre)adList.get(i)).getId().getYearMonthData());//凭证年月;
//                        agid.setAccBookType(aai.getId().getAccBookType());//账套类型
//                        agid.setAccBookCode(aai.getId().getAccBookCode());//账套编码
//                        AccGCheckInfo ag = accGCheckInfoRepository.findById(agid).get();//通过ID查找
//                        if(ag.getFlag().equals("3")){//已生成凭证
//                            ag.setFlag("1");//固定资产折旧状态 已计提
//                            ag.setCreateBy2(null);//固定资产折旧-生成凭证操作人
//                            ag.setCreateTime2(null);//固定资产折旧-生成凭证时间
//                            ag.setCreateBy4(String.valueOf(CurrentUser.getCurrentUser().getId()));//固定资产折旧-凭证回退操作人
//                            ag.setCreateTime4(CurrentTime.getCurrentTime());//固定资产折旧-凭证回退时间
//                            accGCheckInfoRepository.save(ag);//保存
//                        }
//
//                        AccWCheckInfoId awid = new AccWCheckInfoId();//无形资产会计期间信息获取
//                        awid.setCenterCode(aai.getId().getCenterCode());//核算单位
//                        awid.setYearMonthDate(((AccDepre)adList.get(i)).getId().getYearMonthData());//凭证年月;
//                        awid.setAccBookType(aai.getId().getAccBookType());//账套类型
//                        awid.setAccBookCode(aai.getId().getAccBookCode());//账套编码
//                        AccWCheckInfo aw = accWCheckInfoRepository.findById(awid).get();//通过ID查找
//                        if(aw.getFlag().equals("3")){
//                            aw.setFlag("1");//无形资产折旧状态 已生成凭证
//                            aw.setCreateBy2(null);//固定资产折旧-生成凭证操作人
//                            aw.setCreateTime2(null);//固定资产折旧-生成凭证时间
//                            aw.setCreateBy4(String.valueOf(CurrentUser.getCurrentUser().getId()));//固定资产折旧-凭证回退操作人
//                            aw.setCreateTime4(CurrentTime.getCurrentTime());//固定资产折旧-凭证回退时间
//                            accWCheckInfoRepository.save(aw);//保存
//                        }
//
//                    }
//                }

//                //凭证主表信息删除
//                accMainVoucherRespository.delById(voucherNo);
//                //凭证子表信息删除
//                accSubVoucherRespository.delById(voucherNo);

            //判断当前凭证号是否已经回退
            if(voucherNo == null || voucherNo.equals("")){//已经回退，则不执行下面的回退操作
                continue;
            }else{
                //固定资产基本信息表 卡片凭证号清空
                accAssetInfoRepository.clearVoucherNo(voucherNo,CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount(),CurrentUser.getCurrentLoginManageBranch());
                //凭证主表、子表删除
                voucherManageService.deleteVoucher(voucherNo, useStartDate);
            }


//                //最大凭证号回退
//                accVoucherNoRespository.updateSubVoucherNo(useStartDate, CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount());

        }
        return InvokeResult.success();

    }

    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols) {
        ExcelUtil excelUtil = new ExcelUtil();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql = new StringBuffer("select f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                " f.asset_type as  assetType, f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName, f.metric_name as metricName,\n" +
                "f.manufactor as manufactor,f.specification as specification,f.serial_number as serialNumber,f.use_start_date as useStartDate,f.quantity,f.source_flag as sourceFlag,"+
                "(select c.code_name from codemanage c where c.code_code=f.use_flag and c.code_type='useFlag') as useFlag ,f.storage_way as storageWay,f.asset_origin_value as assetOriginValue,f.asset_net_value as assetNetValue, f.dep_years as depYears,f.dep_type as depType,"+
                "f.impairment as impairment, f.added_tax as addedTax,f.sum,f.input_tax as inputTax,f.pay_way as payWay, f.pay_code as payCode,f.remains_value as remainsValue,f.remains_rate as remainsRate," +
                "f.predict_clear_fee as predictClearFee,f.formula_code as formulaCode,f.init_depre_amount as initDepreAmount,f.init_depre_money as initDepreMoney, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "f.clear_flag as clearFlag , f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch, f.clear_operator_code as clearOperatorCode,"+
                "f.voucher_no as voucherNo,f.depre_from_date as depreFromDate,f.depre_to_date as depreToDate,f.depre_flag as  depreFlag,f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp,"+
                "(select a.asset_simple_name from accassetcodetype a where  a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetTypeName ,"+
                " f.unit_code as unitCode,"+
                "(select o.special_name from specialinfo o where o.id=f.unit_code ) as unitCodeName,"+
                "(select c.code_name from codemanage c where c.code_type='organization' and c.code_code=f.organization ) as organization,"+
                " (select c.code_name from codemanage c where c.code_type='deprMethod' and c.code_code=f.dep_type ) as deprMethod,"+
                " (select special_name from specialinfo where f.unit_code = id) as specialName, " +
                "(select i.month_depre_money from  AccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code ORDER BY i.year_month_data desc limit 1 ) as monthDepreMoney   from AccAssetInfo  f where 1=1 " );
//                " and f.center_code = '"+CurrentUser.getCurrentLoginManageBranch()+"'  and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");

        sql.append("  and f.center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        AccAssetInfoDTO acc = new AccAssetInfoDTO();
        try {
            acc = new ObjectMapper().readValue(queryConditions, AccAssetInfoDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* if(acc.getCardCode()!=null&&!acc.getCardCode().equals("")){
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
        if(acc.getDepYears()!=null&&!acc.getDepYears().equals("")){
            sql.append(" and f.dep_years>='"+acc.getDepYears()+"'");
        }
        if(acc.getDepYears1()!=null&&!acc.getDepYears1().equals("")){
            sql.append(" and f.dep_years<='"+acc.getDepYears1()+"'");
        }
        if(acc.getCreateOper()!=null && !acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from roleinfo where role_name like '%"+acc.getCreateOper()+"%')");
        }
        if(acc.getDepreToDate()!=null&&!acc.getDepreToDate().equals("")){
            sql.append(" and f.depre_to_date<='"+acc.getDepreToDate()+"'  and f.use_start_date<='"+acc.getDepreToDate()+"'");
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
        if(acc.getCleanCard()!=null&&!acc.getCleanCard().equals("")){
            if(acc.getCleanCard().equals("1")){
                //不包含已清理卡片
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
//            sql.append(" and f.card_code>='"+acc.getCardCode()+"'");
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
            params.put(paramsNo,acc.getCardCode1());
            paramsNo++;
        }
        if(acc.getAssetCode2()!=null&&!acc.getAssetCode2().equals("")){
            sql.append(" and f.asset_code<=?"+paramsNo);
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
        if(acc.getCreateOper()!=null && !acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from userinfo where user_name like ?"+paramsNo+")");
            params.put(paramsNo,"%"+acc.getCreateOper()+"%");
            paramsNo++;
        }
        if(acc.getDepreToDate()!=null&&!acc.getDepreToDate().equals("")){
//            sql.append(" and f.depre_to_date<='"+acc.getDepreToDate()+"'  and f.use_start_date<='"+acc.getDepreToDate()+"'");
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
        //使用部门unitCode
        if(acc.getUnitCode()!=null&&!acc.getUnitCode().equals("")){
            sql.append(" and f.unit_code=?" + paramsNo );
            params.put(paramsNo,acc.getUnitCode());
            paramsNo++;
        }
        if(acc.getAssetType()!=null&&!acc.getAssetType().equals("")){
            sql.append(" and f.asset_type like ?"+paramsNo);
            params.put(paramsNo,acc.getAssetType()+"%");
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
        try {
            // 根据条件查询导出数据集
            List<?> dataList = accAssetInfoRepository.queryBySqlSC(sql.toString(),params);
            // 导出
            excelUtil.exportu_voucher(request, response, name, cols, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //根据专项查询专项存放位置
    public String qrySegmentFlag(String specialCode){
        int seatNo = 1;
        Map<Integer,Object> seats = new HashMap<>();
        String flag = null;
        StringBuffer siSql = new StringBuffer();
        siSql.append("select * from specialinfo where special_code = ?"+ seatNo );
        seats.put(seatNo,specialCode);
        seatNo++;
//        (CurrentUser.getCurrentLoginAccount())
        siSql.append(" and s.account = ?"+seatNo);
        seats.put(seatNo,CurrentUser.getCurrentLoginAccount());
        seatNo++;


        List<?> aiList = accAssetCodeTypeRepository.queryBySqlSC(siSql.toString(),seats);
//        List<?> aiList = specialInfoRepository.querySpecialInfoBySpecialCode(specialCode);
        if(aiList.size()>0){
            Map siMap = new HashMap();
            for(Object siObj : aiList){
                siMap.putAll((Map) siObj);
                break;
            }
            if((siMap.get("super_special")==null && siMap.get("endflag").equals("1")) ||
                    (siMap.get("super_special").equals("") && siMap.get("endflag").equals("1"))){
                specialCode = siMap.get("special_code").toString();
                StringBuffer agSql = new StringBuffer();
                int ssetNo = 1;
                Map<Integer,Object> ssets = new HashMap<>();
                agSql.append("select * from accsegmentdefine where segment_col = ?"+ ssetNo);
                ssets.put(ssetNo,specialCode);
                ssetNo++;
                List<?> agList = accAssetCodeTypeRepository.queryBySqlSC(agSql.toString(),ssets);
//                List<?> agList = accSegmentDefineRespository.queryAccSegmentDefine(specialCode);
                if(agList.size()>0){
                    Map agMap = new HashMap();
                    for(Object agObj : agList){
                        agMap.putAll((Map) agObj);
                        flag = agMap.get("segment_flag").toString();
                        break;
                    }
                }
            }else{
                specialCode = siMap.get("super_special").toString();
                for(;;){
                    StringBuffer siSql2 = new StringBuffer();
                    int ssetNo = 1;
                    Map<Integer,Object> ssets = new HashMap<>();
                    siSql2.append("select * from specialinfo where id = ?"+ ssetNo);
                    ssets.put(ssetNo,specialCode);
                    ssetNo++;
                    List<?> aiList2 = accAssetCodeTypeRepository.queryBySqlSC(siSql2.toString(),ssets);


//                    List<?> aiList2 = specialInfoRepository.querySpecialInfoById(specialCode);
                    if(aiList2.size()>0){
                        Map siMap2 = new HashMap();
                        for(Object siObj2 : aiList2){
                            siMap2.putAll((Map) siObj2);
                            break;
                        }
                        if(siMap2.get("super_special")==null && siMap2.get("endflag").equals("1") ||
                                siMap2.get("super_special").equals("") && siMap2.get("endflag").equals("1")){
                            specialCode = siMap2.get("special_code").toString();
                            StringBuffer agSql2 = new StringBuffer();
                            int number =1;
                            Map<Integer,Object> numbers = new HashMap<>();
                            agSql2.append("select * from accsegmentdefine where segment_col = ?"+ number);
                            numbers.put(number,specialCode);
                            number++;
                            List<?> agList2 = accAssetCodeTypeRepository.queryBySqlSC(agSql2.toString(),numbers);
                            if(agList2.size()>0){
                                Map agMap2 = new HashMap();
                                for(Object agObj2 : agList2){
                                    agMap2.putAll((Map) agObj2);
                                    flag = agMap2.get("segment_flag").toString();
                                    break;
                                }
                            }
                            break;
                        }else{
                            specialCode = siMap2.get("super_special").toString();
                        }
                    }

                }
            }
        }
        return flag;
    }

    /**
     * 获取指定年月的最后一天
     * @param year
     * @param month
     * @return 日期字符串（yyyy-MM-dd）
     */
    public String getLastDayOfYearMonth(int year, int month){
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }


    public void setValue(String flag,AccSubVoucher asv,List<?> aactList){

        if(flag!=null && !flag.equals("")){
            if(flag.equals("s01")){asv.setS01(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
            if(flag.equals("s02")){asv.setS02(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
            if(flag.equals("s03")){asv.setS03(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
            if(flag.equals("s04")){asv.setS04(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
            if(flag.equals("s05")){asv.setS05(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
            if(flag.equals("s06")){asv.setS06(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
            if(flag.equals("s07")){asv.setS07(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
//           if(flag.equals("s07")){asv.setS07("ZJDBN");}else
                if(flag.equals("s08")){asv.setS08(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s09")){asv.setS09(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s10")){asv.setS10(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s11")){asv.setS11(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s12")){asv.setS12(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s13")){asv.setS13(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s14")){asv.setS14(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s15")){asv.setS15(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s16")){asv.setS16(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s17")){asv.setS17(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s18")){asv.setS18(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s19")){asv.setS19(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}else
                if(flag.equals("s20")){asv.setS20(((AccAssetCodeType)aactList.get(0)).getArticleCode1());}
        }
        String directionidx=((AccAssetCodeType)aactList.get(0)).getItemCode1();
        if(directionidx.charAt(directionidx.length()-1)!='/'){directionidx=directionidx+"/";}
        asv.setDirectionIdx(directionidx);//科目方向段
    };

    public void setValue1(String flag,AccSubVoucher asv,List<?> aactList){

        if(flag!=null && !flag.equals("")){
            if(flag.equals("s01")){asv.setS01(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
            if(flag.equals("s02")){asv.setS02(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
            if(flag.equals("s03")){asv.setS03(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
            if(flag.equals("s04")){asv.setS04(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
            if(flag.equals("s05")){asv.setS05(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
            if(flag.equals("s06")){asv.setS06(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
            if(flag.equals("s07")){asv.setS07(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
//           if(flag.equals("s07")){asv.setS07("ZJDBN");}else
                if(flag.equals("s08")){asv.setS08(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s09")){asv.setS09(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s10")){asv.setS10(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s11")){asv.setS11(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s12")){asv.setS12(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s13")){asv.setS13(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s14")){asv.setS14(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s15")){asv.setS15(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s16")){asv.setS16(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s17")){asv.setS17(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s18")){asv.setS18(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s19")){asv.setS19(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}else
                if(flag.equals("s20")){asv.setS20(((AccAssetCodeType)aactList.get(0)).getArticleCode4());}
        }
        String directionidx=((AccAssetCodeType)aactList.get(0)).getItemCode4();
        if(directionidx.charAt(directionidx.length()-1)!='/'){directionidx=directionidx+"/";}
        asv.setDirectionIdx(directionidx);//科目方向段
    };

    public void setValue2(String flag,AccSubVoucher asv,String payCode,String payWay){

        if(flag!=null && !flag.equals("")){
            if(flag.equals("s01")){asv.setS01(payCode);}else
            if(flag.equals("s02")){asv.setS02(payCode);}else
            if(flag.equals("s03")){asv.setS03(payCode);}else
            if(flag.equals("s04")){asv.setS04(payCode);}else
            if(flag.equals("s05")){asv.setS05(payCode);}else
            if(flag.equals("s06")){asv.setS06(payCode);}else
            if(flag.equals("s07")){asv.setS07(payCode);}else
//           if(flag.equals("s07")){asv.setS07("ZJDBN");}else
                if(flag.equals("s08")){asv.setS08(payCode);}else
                if(flag.equals("s09")){asv.setS09(payCode);}else
                if(flag.equals("s10")){asv.setS10(payCode);}else
                if(flag.equals("s11")){asv.setS11(payCode);}else
                if(flag.equals("s12")){asv.setS12(payCode);}else
                if(flag.equals("s13")){asv.setS13(payCode);}else
                if(flag.equals("s14")){asv.setS14(payCode);}else
                if(flag.equals("s15")){asv.setS15(payCode);}else
                if(flag.equals("s16")){asv.setS16(payCode);}else
                if(flag.equals("s17")){asv.setS17(payCode);}else
                if(flag.equals("s18")){asv.setS18(payCode);}else
                if(flag.equals("s19")){asv.setS19(payCode);}else
                if(flag.equals("s20")){asv.setS20(payCode);}
        }
        String directionidx=payWay;
        if(directionidx.charAt(directionidx.length()-1)!='/'){directionidx=directionidx+"/";}
        asv.setDirectionIdx(directionidx);//科目方向段
    };

}
