package com.sinosoft.service.impl.account;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.account.*;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.AccDetailBalanceRepository;
import com.sinosoft.repository.AccArticleBalanceRepository;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.repository.account.AccCheckInfoRespository;
import com.sinosoft.repository.account.AccMainVoucherRespository;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.repository.account.AccSubVoucherRespository;
import com.sinosoft.repository.fixedassets.AccGCheckInfoRepository;
import com.sinosoft.repository.intangibleassets.AccWCheckInfoRepository;
import com.sinosoft.service.account.VoucherAccountingService;
import com.sinosoft.service.fixedassets.FixedassetsCardVoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@SpringBootApplication
public class VoucherAccountingServiceImpl implements VoucherAccountingService {
    private Logger logger = LoggerFactory.getLogger(VoucherApproveServiceImpl.class);

    @Resource
    private VoucherRepository voucherRepository ;
    @Resource
    private AccArticleBalanceRepository accArticleBalanceRepository ;
    @Resource
    private AccDetailBalanceRepository accDetailBalanceRepository;
    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;
    @Resource
    private AccSubVoucherRespository accSubVoucherRespository;
    @Resource
    private AccCheckInfoRespository accCheckInfoRespository;
    @Resource
    private AccGCheckInfoRepository accGCheckInfoRepository;
    @Resource
    private AccWCheckInfoRepository accWCheckInfoRepository;
    @Resource
    private AccMainVoucherRespository accMainVoucherRespository;
    @Resource
    private FixedassetsCardVoucherService fixedassetsCardVoucherService;

    @Override
    public List<?> qryVoucherList(VoucherDTO dto) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        StringBuffer sql=new StringBuffer();
        sql.append("select a.voucher_date as voucherDate,a.voucher_no as voucherNo,a.year_month_date as yearMonthDate,");
        sql.append("(select u.user_name from userinfo u where u.id=a.create_by) as createBy,");
        sql.append("(select u.user_name from userinfo u where u.id=a.approve_by) as approveBy, ");
        sql.append("(select u.user_name from userinfo u where u.id=a.gene_by) as geneBy, ");
        sql.append("a.aux_number as auxNumber,a.approve_date as approveDate,  ");
        sql.append("(SELECT s.remark FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no limit 1) as remarkName, ");
        sql.append("(SELECT sum(s.debit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) as debit, ");
        sql.append("(SELECT sum(s.credit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) as credit,  ");
        sql.append("(select c.code_name from codemanage c where c.code_type='voucherFlag' and c.code_code = a.voucher_flag) as voucherFlag  ");
        sql.append("from AccMainVoucher a  where 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sql.append(" and a.center_code = ?" + paramsNo);
        params.put(paramsNo, centerCode);
        paramsNo++;
        sql.append(" and a.branch_code = ?" + paramsNo);
        params.put(paramsNo, branchCode);
        paramsNo++;
        sql.append(" and acc_book_type = ?" + paramsNo);
        params.put(paramsNo, accBookType);
        paramsNo++;
        sql.append(" and a.acc_book_code = ?" + paramsNo);
        params.put(paramsNo, accBookCode);
        paramsNo++;

        if(dto.getVoucherType()!=null&&!dto.getVoucherType().equals("")){
            sql.append(" and a.voucher_type = ?" + paramsNo);
            params.put(paramsNo, dto.getVoucherType());
            paramsNo++;
        }
        if(dto.getVoucherFlag()!=null&&"1".equals(dto.getVoucherFlag())){
            //已记账
            sql.append(" and a.voucher_flag='3'");
        } else if(dto.getVoucherFlag()!=null&&"2".equals(dto.getVoucherFlag())){
            /*//未记账（未复核、已复核）
            sql.append(" and a.voucher_flag in ('1','2')");*/
            //未记账（已复核）
            sql.append(" and a.voucher_flag in ('2')");
        } else {
            sql.append(" and a.voucher_flag in ('2','3')");
        }
        if(dto.getYearMonth()!=null&&!dto.getYearMonth().equals("")){
            sql.append(" and a.year_month_date = ?" + paramsNo);
            params.put(paramsNo, dto.getYearMonth());
            paramsNo++;
        } else {
            return new ArrayList<>();
        }
        if(dto.getVoucherNoStart()!=null&&!dto.getVoucherNoStart().equals("")){
            String voucherNo="";
            if(dto.getVoucherNoStart().length()!=(10+centerCode.length())){
                int i =  Integer.parseInt(dto.getVoucherNoStart());
                voucherNo = centerCode + dto.getYearMonth().substring(2,6)+String.format("%06d", i);
            }else{
                voucherNo=dto.getVoucherNoStart();
            }
            sql.append(" and a.voucher_no >= ?" + paramsNo);
            params.put(paramsNo, voucherNo);
            paramsNo++;
        }
        if(dto.getVoucherNoEnd()!=null&&!dto.getVoucherNoEnd().equals("")){
            String voucherNo="";
            if(dto.getVoucherNoEnd().length()!=(10+centerCode.length())){
                int i =  Integer.parseInt(dto.getVoucherNoEnd());
                voucherNo = centerCode + dto.getYearMonth().substring(2,6)+String.format("%06d", i);
            }else{
                voucherNo=dto.getVoucherNoEnd();
            }
            sql.append(" and a.voucher_no <= ?" + paramsNo);
            params.put(paramsNo, voucherNo);
            paramsNo++;
        }

        sql.append(" ORDER BY a.year_month_date,a.voucher_no ");
        return voucherRepository.queryBySqlSC(sql.toString(), params);
    }

    /**
     * 记账(单条凭证走这个方法，数据量大的走上面的记账方法)
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public InvokeResult accounting(VoucherDTO dto) {
        synchronized(this){
            String centerCode = CurrentUser.getCurrentLoginManageBranch();
            String branchCode = CurrentUser.getCurrentLoginManageBranch();
            String accBookType = CurrentUser.getCurrentLoginAccountType();
            String accBookCode = CurrentUser.getCurrentLoginAccount();
            //获取该机构当月全部科目余额数据
            List<AccDetailBalance> accDetailBalancelList = accDetailBalanceRepository.qryAccDetailBalanceByYearMonthDateAndDirectionIdx(centerCode, branchCode,accBookType, accBookCode, dto.getYearMonthDate());
            //获取该机构当月全部专项余额数据
            List<AccArticleBalance> accArticleBalanceList = accArticleBalanceRepository.qryAccArticleBalanceByYearMonthDateAndDirectionIdxAndDirectionOther(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate());


            String[] voucherNo=dto.getVoucherNo().split(",");
            //先判断本月会计期间是否结转
            AccMonthTrace accMonthTrace = accMonthTraceRespository.findAccMonthTraceByYearMonthDate(centerCode, accBookType, accBookCode, dto.getYearMonthDate());

            if(accMonthTrace.getAccMonthStat().equals("3")){
                return InvokeResult.failure("记账失败，会计期间已结转！");
            }

            //判断上月会计期间是否结转
            String lastYmd = dto.getYearMonthDate();//上月会计日期
            if(lastYmd.contains("JS")){
                lastYmd = lastYmd.substring(0,4)+"12";
            }else if(lastYmd.substring(4).equals("01")){
                lastYmd = (Integer.parseInt(lastYmd.substring(0,4))-1)+"14";
            }else{
                lastYmd = (Integer.parseInt(lastYmd)-1) + "";
            }

            AccMonthTrace lastAM = accMonthTraceRespository.findAccMonthTraceByYearMonthDate(centerCode, accBookType, accBookCode, lastYmd);
            if(lastAM != null){
                if(!lastAM.getAccMonthStat().equals("3") && !lastAM.getAccMonthStat().equals("5")){
                    return InvokeResult.failure("记账失败，上月会计期间未结转！");
                }
            }

            if ("12".equals(accMonthTrace.getId().getYearMonthDate().substring(4))) {
                AccMainVoucher finanAccMainVoucher = accMainVoucherRespository.qryFinanAccMainVoucher(centerCode, branchCode, accBookType, accBookCode, accMonthTrace.getId().getYearMonthDate());
                if (finanAccMainVoucher!=null) {
                    if (!(voucherNo.length==1 && finanAccMainVoucher.getId().getVoucherNo().equals(voucherNo[0]))) {
                        return InvokeResult.failure("请回退决算凭证后再操作！");
                    }
                } else if (accMonthTrace.getTemp()!=null&&"Y".equals(accMonthTrace.getTemp())) {
                    //如果是Y，则表示已进行决算操作但无需要决算生成凭证的数据，再记账时，需要修改此标志
                    accMonthTrace.setTemp("");
                    accMonthTraceRespository.save(accMonthTrace);
                }
            }

            String result = "";

            for(int i=0;i<voucherNo.length;i++){
                //凭证状态更新
                AccMainVoucherId amid=new AccMainVoucherId();
                amid.setCenterCode(centerCode);//核算单位
                amid.setBranchCode(branchCode);//基层单位
                amid.setAccBookType(accBookType);//账套类型
                amid.setAccBookCode(accBookCode);//账套编码
                amid.setYearMonthDate(dto.getYearMonthDate());//年月
                amid.setVoucherNo(voucherNo[i]);//凭证号
                AccMainVoucher am =voucherRepository.findById(amid).get();

                if(am.getApproveBy()!=null && am.getApproveBy().equals(String.valueOf(CurrentUser.getCurrentUser().getId()))){
                    if (!"".equals(result)){
                        result += "," + am.getId().getVoucherNo();
                    } else {
                        result = am.getId().getVoucherNo();
                    }
                } else {
                    //记账
                    String yearmonth = dto.getYearMonthDate();
                    List<?> asList = voucherRepository.getSource(centerCode, branchCode, accBookType, accBookCode, yearmonth, voucherNo[i]);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                    for(Object object : asList){
                        Map map = new HashMap();
                        map.putAll((Map)object);
                        String itemCode = map.get("item_code").toString();//科目代码
                        String directionIdx = map.get("direction_idx").toString();//科目方向段
                        String directionOther = "";//专项方向段
                        if(map.get("direction_other") != null && !map.get("direction_other").equals("")){
                            directionOther = map.get("direction_other").toString();//专项方向段
                        }

                        BigDecimal debitDest = new BigDecimal("0.00");//本位币借方金额
                        BigDecimal creditDest = new BigDecimal("0.00");//本位币贷方金额
                        BigDecimal debitSource = new BigDecimal("0.00");//原币借方金额
                        BigDecimal creditSource = new BigDecimal("0.00");//原币贷方金额

                        if(map.get("debit_dest") != null && ((BigDecimal)map.get("debit_dest")).compareTo(new BigDecimal("0.00")) != 0){
                            debitDest = (BigDecimal) map.get("debit_dest");//本位币借方金额
                            debitSource = (BigDecimal) map.get("debit_source");//原币借方金额
                        } else {
                            creditDest = (BigDecimal) map.get("credit_dest");//本位币贷方金额
                            creditSource = (BigDecimal) map.get("credit_source");//原币贷方金额
                        }


                        Optional<AccDetailBalance> firstAccDetailBalance= accDetailBalancelList.stream()
                                .filter(a -> directionIdx.equals(a.getId().getDirectionIdx()))
                                .findFirst();


                        if(firstAccDetailBalance.isPresent()){//不等于0说明已经有该科目
                            //科目余额表
                            AccDetailBalance adb = new AccDetailBalance();
                            adb = firstAccDetailBalance.get();

                            //借方+当前值
                            adb.setDebitSource(adb.getDebitSource().add(debitSource));//原币本月借方金额
                            //贷方+当前值
                            adb.setCreditSource(adb.getCreditSource().add(creditSource));//原币本月贷方金额
                            adb.setDebitSourceQuarter(adb.getDebitSourceQuarter().add(new BigDecimal("0.00")));//不填   原币本季借方金额
                            adb.setCreditSourceQuarter(adb.getCreditSourceQuarter().add(new BigDecimal("0.00")));//不填  原币本季贷方金额
                            //借方+当前值
                            adb.setDebitSourceYear(adb.getDebitSourceYear().add(debitSource));//原币本年借方金额
                            //贷方+当前值
                            adb.setCreditSourceYear(adb.getCreditSourceYear().add(creditSource));//原币本年贷方金额
                            adb.setDebitDest(adb.getDebitDest().add(debitDest));//本位币本月借方金额
                            adb.setCreditDest(adb.getCreditDest().add(creditDest));//本位币本月贷方金额
                            adb.setDebitDestQuarter(adb.getDebitDestQuarter().add(new BigDecimal("0.00")));//本位币本季借方金额
                            adb.setCreditDestQuarter(adb.getCreditDestQuarter().add(new BigDecimal("0.00")));//本位币本季贷方金额
                            adb.setDebitDestYear(adb.getDebitDestYear().add(debitDest));//本位币本年借方金额
                            adb.setCreditDestYear(adb.getCreditDestYear().add(creditDest));//本位币本年贷方金额
                            //期末+借-贷
                            adb.setBalanceSource(adb.getBalanceSource().add(debitSource).subtract(creditSource));//原币期末余额
                            //期末+借-贷
                            adb.setBalanceDest(adb.getBalanceDest().add(debitDest).subtract(creditDest));//本位币期末余额
                            adb = null ;

                            //判断专项字段是否为空，为空则不处理专项余额表
                            String directionOtherTemp = directionOther;
                            if(directionOther != null && !directionOther.equals("")){
                                //有专项
                                //判断专项余额表中 该科目下是否存在该专项 有则累加 无则新增

                                Optional<AccArticleBalance> firstAccArticleBalance= accArticleBalanceList.stream()
                                        .filter(a -> directionIdx.equals(a.getId().getDirectionIdx()))
                                        .filter(a -> directionOtherTemp.equals(a.getId().getDirectionOther()))
                                        .findFirst();

                                if(firstAccArticleBalance.isPresent()){

                                    //专项余额表
                                    AccArticleBalance aa = new AccArticleBalance();
                                    aa = firstAccArticleBalance.get();
                                    //大于零说明专项余额表中该科目下有该专项 累加
                                    //借方+当前值
                                    aa.setDebitSource(aa.getDebitSource().add(debitSource));//原币本月借方金额
                                    //贷方+当前值
                                    aa.setCreditSource(aa.getCreditSource().add(creditSource));//原币本月贷方金额

                                    aa.setDebitSourceQuarter(aa.getDebitSourceQuarter().add(new BigDecimal("0.00")));//不填   原币本季借方金额----------------
                                    aa.setCreditSourceQuarter(aa.getCreditSourceQuarter().add(new BigDecimal("0.00")));//不填  原币本季贷方金额----------------

                                    //借方+当前值
                                    aa.setDebitSourceYear(aa.getDebitSourceYear().add(debitSource));//原币本年借方金额
                                    //贷方+当前值
                                    aa.setCreditSourceYear(aa.getCreditSourceYear().add(creditSource));//原币本年贷方金额

                                    aa.setDebitDest(aa.getDebitDest().add(debitDest));//本位币本月借方金额 (值同原币)
                                    aa.setCreditDest(aa.getCreditDest().add(creditDest));//本位币本月贷方金额 (值同原币)

                                    aa.setDebitDestQuarter(aa.getDebitDestQuarter().add(new BigDecimal("0.00")));//本位币本季借方金额-----------------------
                                    aa.setCreditDestQuarter(aa.getCreditDestQuarter().add(new BigDecimal("0.00")));//本位币本季贷方金额----------------------

                                    aa.setDebitDestYear(aa.getDebitDestYear().add(debitDest));//本位币本年借方金额--------
                                    aa.setCreditDestYear(aa.getCreditDestYear().add(creditDest));//本位币本年贷方金额------

                                    //期末+借-贷
                                    aa.setBalanceSource(aa.getBalanceSource().add(debitSource).subtract(creditSource));//原币期末余额------------------------
                                    //期末+借-贷
                                    aa.setBalanceDest(aa.getBalanceDest().add(debitDest.subtract(creditDest)));//本位币期末余额
                                    aa = null ;

                                }else{
                                    //专项余额表中该科目下没有该专项 新增
                                    AccArticleBalanceId aaid = new AccArticleBalanceId();
                                    aaid.setCenterCode(centerCode);//核算单位
                                    aaid.setBranchCode(branchCode);//基层单位
                                    aaid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
                                    aaid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
                                    aaid.setYearMonthDate(dto.getYearMonthDate());//年月
                                    aaid.setItemCode(itemCode);
                                    aaid.setDirectionIdx(directionIdx);
                                    aaid.setDirectionOther(directionOther);//专项方向段

                                    AccArticleBalance aa = new AccArticleBalance(aaid);//专项余额表

                                    aa.setDirectionIdxName(map.get("direction_idx_name").toString());
                                    aa.setCurrency(map.get("currency").toString());

                                    aa.setDebitSource(debitSource);//原币本月借方金额
                                    aa.setCreditSource(creditSource);//原币本月贷方金额

                                    aa.setDebitSourceQuarter(new BigDecimal("0.00"));//不填   原币本季借方金额----------------
                                    aa.setCreditSourceQuarter(new BigDecimal("0.00"));//不填  原币本季贷方金额----------------

                                    aa.setDebitSourceYear(debitSource);//原币本年借方金额
                                    aa.setCreditSourceYear(creditSource);//原币本年贷方金额

                                    aa.setDebitDest(debitDest);//本位币本月借方金额 (值同原币)
                                    aa.setCreditDest(creditDest);//本位币本月贷方金额 (值同原币)

                                    aa.setDebitDestQuarter(new BigDecimal("0.00"));//本位币本季借方金额-----------------------
                                    aa.setCreditDestQuarter(new BigDecimal("0.00"));//本位币本季贷方金额----------------------

                                    aa.setDebitDestYear(debitDest);//本位币本年借方金额--------
                                    aa.setCreditDestYear(creditDest);//本位币本年贷方金额------

                                    aa.setBalanceSource(debitSource.subtract(creditSource));//原币期末余额------------------------
                                    aa.setBalanceBeginSource(new BigDecimal("0.00"));//原币期初余额
                                    aa.setBalanceDest(debitDest.subtract(creditDest));//本位币期末余额
                                    aa.setBalanceBeginDest(new BigDecimal("0.00"));//本位币期初余额
                                    aa.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//创建人
                                    aa.setCreateTime(df.format(new Date()));//创建时间

                                    //科目段、专项段 赋值
                                    aa.setF01(map.get("f01") == null ? null : map.get("f01").toString());
                                    aa.setF02(map.get("f02") == null ? null : map.get("f02").toString());
                                    aa.setF03(map.get("f03") == null ? null : map.get("f03").toString());
                                    aa.setF04(map.get("f04") == null ? null : map.get("f04").toString());
                                    aa.setF05(map.get("f05") == null ? null : map.get("f05").toString());
                                    aa.setF06(map.get("f06") == null ? null : map.get("f06").toString());
                                    aa.setF07(map.get("f07") == null ? null : map.get("f07").toString());
                                    aa.setF08(map.get("f08") == null ? null : map.get("f08").toString());
                                    aa.setF09(map.get("f09") == null ? null : map.get("f09").toString());
                                    aa.setF10(map.get("f10") == null ? null : map.get("f10").toString());
                                    aa.setF11(map.get("f11") == null ? null : map.get("f11").toString());
                                    aa.setF12(map.get("f12") == null ? null : map.get("f12").toString());
                                    aa.setF13(map.get("f13") == null ? null : map.get("f13").toString());
                                    aa.setF14(map.get("f14") == null ? null : map.get("f14").toString());
                                    aa.setF15(map.get("f15") == null ? null : map.get("f15").toString());
                                    aa.setF16(map.get("f16") == null ? null : map.get("f16").toString());
                                    aa.setF17(map.get("f17") == null ? null : map.get("f17").toString());
                                    aa.setF18(map.get("f18") == null ? null : map.get("f18").toString());
                                    aa.setF19(map.get("f19") == null ? null : map.get("f19").toString());
                                    aa.setF20(map.get("f20") == null ? null : map.get("f20").toString());
                                    aa.setF21(map.get("f21") == null ? null : map.get("f21").toString());
                                    aa.setF22(map.get("f22") == null ? null : map.get("f22").toString());
                                    aa.setF23(map.get("f23") == null ? null : map.get("f23").toString());
                                    aa.setF24(map.get("f24") == null ? null : map.get("f24").toString());
                                    aa.setF25(map.get("f25") == null ? null : map.get("f25").toString());
                                    aa.setF26(map.get("f26") == null ? null : map.get("f26").toString());
                                    aa.setF27(map.get("f27") == null ? null : map.get("f27").toString());
                                    aa.setF28(map.get("f28") == null ? null : map.get("f28").toString());
                                    aa.setF29(map.get("f29") == null ? null : map.get("f29").toString());
                                    aa.setF30(map.get("f30") == null ? null : map.get("f30").toString());
                                    aa.setS01(map.get("s01") == null ? null : map.get("s01").toString());
                                    aa.setS02(map.get("s02") == null ? null : map.get("s02").toString());
                                    aa.setS03(map.get("s03") == null ? null : map.get("s03").toString());
                                    aa.setS04(map.get("s04") == null ? null : map.get("s04").toString());
                                    aa.setS05(map.get("s05") == null ? null : map.get("s05").toString());
                                    aa.setS06(map.get("s06") == null ? null : map.get("s06").toString());
                                    aa.setS07(map.get("s07") == null ? null : map.get("s07").toString());
                                    aa.setS08(map.get("s08") == null ? null : map.get("s08").toString());
                                    aa.setS09(map.get("s09") == null ? null : map.get("s09").toString());
                                    aa.setS10(map.get("s10") == null ? null : map.get("s10").toString());
                                    aa.setS11(map.get("s11") == null ? null : map.get("s11").toString());
                                    aa.setS12(map.get("s12") == null ? null : map.get("s12").toString());
                                    aa.setS13(map.get("s13") == null ? null : map.get("s13").toString());
                                    aa.setS14(map.get("s14") == null ? null : map.get("s14").toString());
                                    aa.setS15(map.get("s15") == null ? null : map.get("s15").toString());
                                    aa.setS16(map.get("s16") == null ? null : map.get("s16").toString());
                                    aa.setS17(map.get("s17") == null ? null : map.get("s17").toString());
                                    aa.setS18(map.get("s18") == null ? null : map.get("s18").toString());
                                    aa.setS19(map.get("s19") == null ? null : map.get("s19").toString());
                                    aa.setS20(map.get("s20") == null ? null : map.get("s20").toString());

                                    //专项余额表保存
                                    accArticleBalanceList.add(aa);
                                    aaid = null ;
                                    aa = null;
                                }
                            }

                        }else{
                            //没有该科目直接insert操作
                            //当月明细账余额表
                            //当月明细账余额表
                            AccDetailBalanceId adbid = new AccDetailBalanceId();
                            adbid.setCenterCode(centerCode);//核算单位
                            adbid.setBranchCode(branchCode);//基层单位
                            adbid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
                            adbid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
                            adbid.setYearMonthDate(dto.getYearMonthDate());//年月
                            adbid.setItemCode(itemCode);
                            adbid.setDirectionIdx(directionIdx);

                            AccDetailBalance adb = new AccDetailBalance(adbid);//科目余额表

                            String currency = map.get("currency").toString();//原币别编码
                            adb.setCurrency(currency);

                            adb.setDirectionIdxName(map.get("direction_idx_name").toString());
                            adb.setDirectionOther("");//专项方向段
                            adb.setDebitSource(debitSource);//原币本月借方金额
                            adb.setCreditSource(creditSource);//原币本月贷方金额
                            adb.setDebitSourceQuarter(new BigDecimal("0.00"));//不填   原币本季借方金额
                            adb.setCreditSourceQuarter(new BigDecimal("0.00"));//不填  原币本季贷方金额
                            adb.setDebitSourceYear(debitSource);//原币本年借方金额
                            adb.setCreditSourceYear(creditSource);//原币本年贷方金额
                            adb.setDebitDest(debitDest);//本位币本月借方金额
                            adb.setCreditDest(creditDest);//本位币本月贷方金额
                            adb.setDebitDestQuarter(new BigDecimal("0.00"));//本位币本季借方金额
                            adb.setCreditDestQuarter(new BigDecimal("0.00"));//本位币本季贷方金额
                            adb.setDebitDestYear(debitDest);//本位币本年借方金额
                            adb.setCreditDestYear(creditDest);//本位币本年贷方金额
                            adb.setBalanceSource(debitSource.subtract(creditSource));//原币期末余额
                            adb.setBalanceBeginSource(new BigDecimal("0.00"));//原币期初余额
                            adb.setBalanceDest(debitDest.subtract(creditDest));//本位币期末余额
                            adb.setBalanceBeginDest(new BigDecimal("0.00"));//本位币期初余额

                            adb.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//创建人
                            adb.setCreateTime(df.format(new Date()));//创建时间

                            //科目段、专项段 赋值
                            adb.setF01(map.get("f01") == null ? null : map.get("f01").toString());
                            adb.setF02(map.get("f02") == null ? null : map.get("f02").toString());
                            adb.setF03(map.get("f03") == null ? null : map.get("f03").toString());
                            adb.setF04(map.get("f04") == null ? null : map.get("f04").toString());
                            adb.setF05(map.get("f05") == null ? null : map.get("f05").toString());
                            adb.setF06(map.get("f06") == null ? null : map.get("f06").toString());
                            adb.setF07(map.get("f07") == null ? null : map.get("f07").toString());
                            adb.setF08(map.get("f08") == null ? null : map.get("f08").toString());
                            adb.setF09(map.get("f09") == null ? null : map.get("f09").toString());
                            adb.setF10(map.get("f10") == null ? null : map.get("f10").toString());
                            adb.setF11(map.get("f11") == null ? null : map.get("f11").toString());
                            adb.setF12(map.get("f12") == null ? null : map.get("f12").toString());
                            adb.setF13(map.get("f13") == null ? null : map.get("f13").toString());
                            adb.setF14(map.get("f14") == null ? null : map.get("f14").toString());
                            adb.setF15(map.get("f15") == null ? null : map.get("f15").toString());
                            adb.setF16(map.get("f16") == null ? null : map.get("f16").toString());
                            adb.setF17(map.get("f17") == null ? null : map.get("f17").toString());
                            adb.setF18(map.get("f18") == null ? null : map.get("f18").toString());
                            adb.setF19(map.get("f19") == null ? null : map.get("f19").toString());
                            adb.setF20(map.get("f20") == null ? null : map.get("f20").toString());
                            adb.setF21(map.get("f21") == null ? null : map.get("f21").toString());
                            adb.setF22(map.get("f22") == null ? null : map.get("f22").toString());
                            adb.setF23(map.get("f23") == null ? null : map.get("f23").toString());
                            adb.setF24(map.get("f24") == null ? null : map.get("f24").toString());
                            adb.setF25(map.get("f25") == null ? null : map.get("f25").toString());
                            adb.setF26(map.get("f26") == null ? null : map.get("f26").toString());
                            adb.setF27(map.get("f27") == null ? null : map.get("f27").toString());
                            adb.setF28(map.get("f28") == null ? null : map.get("f28").toString());
                            adb.setF29(map.get("f29") == null ? null : map.get("f29").toString());
                            adb.setF30(map.get("f30") == null ? null : map.get("f30").toString());

                            //当月明细账余额表保存
                            accDetailBalancelList.add(adb);
                            adbid = null;
                            adb = null;
                            //判断专项字段是否为空，为空则不处理专项余额表
                            if(directionOther != null && !directionOther.equals("")){
                                //专项余额表
                                AccArticleBalanceId aaid = new AccArticleBalanceId();
                                aaid.setCenterCode(centerCode);//核算单位
                                aaid.setBranchCode(branchCode);//基层单位
                                aaid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
                                aaid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
                                aaid.setYearMonthDate(dto.getYearMonthDate());//年月
                                aaid.setItemCode(itemCode);
                                aaid.setDirectionIdx(directionIdx);
                                aaid.setDirectionOther(directionOther);
                                AccArticleBalance aa = new AccArticleBalance(aaid);

                                aa.setDirectionIdxName(map.get("direction_idx_name").toString());
                                aa.setCurrency(currency);

                                //应为科目余额表中没有该科目 所有专项表中该科目下一定没有该专项
                                //专项余额表中该科目下没有该专项 新增
                                aa.setDebitSource(debitSource);//原币本月借方金额
                                aa.setCreditSource(creditSource);//原币本月贷方金额

                                aa.setDebitSourceQuarter(new BigDecimal("0.00"));//不填   原币本季借方金额----------------
                                aa.setCreditSourceQuarter(new BigDecimal("0.00"));//不填  原币本季贷方金额----------------

                                aa.setDebitSourceYear(debitSource);//原币本年借方金额
                                aa.setCreditSourceYear(creditSource);//原币本年贷方金额

                                aa.setDebitDest(debitDest);//本位币本月借方金额 (值同原币)
                                aa.setCreditDest(creditDest);//本位币本月贷方金额 (值同原币)

                                aa.setDebitDestQuarter(new BigDecimal("0.00"));//本位币本季借方金额-----------------------
                                aa.setCreditDestQuarter(new BigDecimal("0.00"));//本位币本季贷方金额----------------------

                                aa.setDebitDestYear(debitDest);//本位币本年借方金额--------
                                aa.setCreditDestYear(creditDest);//本位币本年贷方金额------
                                aa.setBalanceSource(debitSource.subtract(creditSource));//原币期末余额------------------------

                                aa.setBalanceBeginSource(new BigDecimal("0.00"));//原币期初余额
                                aa.setBalanceDest(debitDest.subtract(creditDest));//本位币期末余额
                                aa.setBalanceBeginDest(new BigDecimal("0.00"));//本位币期初余额

                                aa.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//创建人
                                aa.setCreateTime(df.format(new Date()));//创建时间

                                //科目段、专项段 赋值
                                aa.setF01(map.get("f01") == null ? null : map.get("f01").toString());
                                aa.setF02(map.get("f02") == null ? null : map.get("f02").toString());
                                aa.setF03(map.get("f03") == null ? null : map.get("f03").toString());
                                aa.setF04(map.get("f04") == null ? null : map.get("f04").toString());
                                aa.setF05(map.get("f05") == null ? null : map.get("f05").toString());
                                aa.setF06(map.get("f06") == null ? null : map.get("f06").toString());
                                aa.setF07(map.get("f07") == null ? null : map.get("f07").toString());
                                aa.setF08(map.get("f08") == null ? null : map.get("f08").toString());
                                aa.setF09(map.get("f09") == null ? null : map.get("f09").toString());
                                aa.setF10(map.get("f10") == null ? null : map.get("f10").toString());
                                aa.setF11(map.get("f11") == null ? null : map.get("f11").toString());
                                aa.setF12(map.get("f12") == null ? null : map.get("f12").toString());
                                aa.setF13(map.get("f13") == null ? null : map.get("f13").toString());
                                aa.setF14(map.get("f14") == null ? null : map.get("f14").toString());
                                aa.setF15(map.get("f15") == null ? null : map.get("f15").toString());
                                aa.setF16(map.get("f16") == null ? null : map.get("f16").toString());
                                aa.setF17(map.get("f17") == null ? null : map.get("f17").toString());
                                aa.setF18(map.get("f18") == null ? null : map.get("f18").toString());
                                aa.setF19(map.get("f19") == null ? null : map.get("f19").toString());
                                aa.setF20(map.get("f20") == null ? null : map.get("f20").toString());
                                aa.setF21(map.get("f21") == null ? null : map.get("f21").toString());
                                aa.setF22(map.get("f22") == null ? null : map.get("f22").toString());
                                aa.setF23(map.get("f23") == null ? null : map.get("f23").toString());
                                aa.setF24(map.get("f24") == null ? null : map.get("f24").toString());
                                aa.setF25(map.get("f25") == null ? null : map.get("f25").toString());
                                aa.setF26(map.get("f26") == null ? null : map.get("f26").toString());
                                aa.setF27(map.get("f27") == null ? null : map.get("f27").toString());
                                aa.setF28(map.get("f28") == null ? null : map.get("f28").toString());
                                aa.setF29(map.get("f29") == null ? null : map.get("f29").toString());
                                aa.setF30(map.get("f30") == null ? null : map.get("f30").toString());
                                aa.setS01(map.get("s01") == null ? null : map.get("s01").toString());
                                aa.setS02(map.get("s02") == null ? null : map.get("s02").toString());
                                aa.setS03(map.get("s03") == null ? null : map.get("s03").toString());
                                aa.setS04(map.get("s04") == null ? null : map.get("s04").toString());
                                aa.setS05(map.get("s05") == null ? null : map.get("s05").toString());
                                aa.setS06(map.get("s06") == null ? null : map.get("s06").toString());
                                aa.setS07(map.get("s07") == null ? null : map.get("s07").toString());
                                aa.setS08(map.get("s08") == null ? null : map.get("s08").toString());
                                aa.setS09(map.get("s09") == null ? null : map.get("s09").toString());
                                aa.setS10(map.get("s10") == null ? null : map.get("s10").toString());
                                aa.setS11(map.get("s11") == null ? null : map.get("s11").toString());
                                aa.setS12(map.get("s12") == null ? null : map.get("s12").toString());
                                aa.setS13(map.get("s13") == null ? null : map.get("s13").toString());
                                aa.setS14(map.get("s14") == null ? null : map.get("s14").toString());
                                aa.setS15(map.get("s15") == null ? null : map.get("s15").toString());
                                aa.setS16(map.get("s16") == null ? null : map.get("s16").toString());
                                aa.setS17(map.get("s17") == null ? null : map.get("s17").toString());
                                aa.setS18(map.get("s18") == null ? null : map.get("s18").toString());
                                aa.setS19(map.get("s19") == null ? null : map.get("s19").toString());
                                aa.setS20(map.get("s20") == null ? null : map.get("s20").toString());

                                accArticleBalanceList.add(aa);
                                aaid = null;
                                aa = null;
                            }
                        }
                    }

                    am.setVoucherFlag("3");//设置为已记账
                    am.setGeneBy(String.valueOf(CurrentUser.getCurrentUser().getId()));
                    String currentDate = df2.format(new Date());
                    if ((currentDate.substring(0,4)+currentDate.substring(5,7)).equals(am.getId().getYearMonthDate())) {
                        am.setGeneDate(currentDate);
                    } else {
                        int year = Integer.parseInt(am.getId().getYearMonthDate().substring(0,4));
                        int month = Integer.parseInt(am.getId().getYearMonthDate().substring(4));
                        am.setGeneDate(fixedassetsCardVoucherService.getLastDayOfYearMonth(year, month));
                    }
                    voucherRepository.save(am);

                    voucherRepository.flush();
                }
            }
            accArticleBalanceRepository.saveAll(accArticleBalanceList);
            accDetailBalanceRepository.saveAll(accDetailBalancelList);

            if (!"".equals(result)) {
                if (result.split(",").length!=voucherNo.length) {
                    return InvokeResult.success("凭证部分记账成功，记账失败凭证："+result+"，原因是：记账人与凭证复核人不能是同一人！");
                } else {
                    return InvokeResult.failure("凭证记账失败，原因是：记账人与凭证复核人不能是同一人！");
                }
            }

            return InvokeResult.success("凭证记账成功！");
        }
    }

    /**
     * 记账
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public InvokeResult accounting2(VoucherDTO dto) {
        synchronized(this){
            String centerCode = CurrentUser.getCurrentLoginManageBranch();
            String branchCode = CurrentUser.getCurrentLoginManageBranch();
            String accBookType = CurrentUser.getCurrentLoginAccountType();
            String accBookCode = CurrentUser.getCurrentLoginAccount();

            String[] voucherNo=dto.getVoucherNo().split(",");
            //先判断本月会计期间是否结转
            AccMonthTrace accMonthTrace = accMonthTraceRespository.findAccMonthTraceByYearMonthDate(centerCode, accBookType, accBookCode, dto.getYearMonthDate());

            if(accMonthTrace.getAccMonthStat().equals("3")){
                return InvokeResult.failure("记账失败，会计期间已结转！");
            }

            //判断上月会计期间是否结转
            String lastYmd = dto.getYearMonthDate();//上月会计日期
            if(lastYmd.contains("JS")){
                lastYmd = lastYmd.substring(0,4)+"12";
            }else if(lastYmd.substring(4).equals("01")){
                lastYmd = (Integer.parseInt(lastYmd.substring(0,4))-1)+"14";
            }else{
                lastYmd = (Integer.parseInt(lastYmd)-1) + "";
            }

            AccMonthTrace lastAM = accMonthTraceRespository.findAccMonthTraceByYearMonthDate(centerCode, accBookType, accBookCode, lastYmd);
            if(lastAM != null){
                if(!lastAM.getAccMonthStat().equals("3") && !lastAM.getAccMonthStat().equals("5")){
                    return InvokeResult.failure("记账失败，上月会计期间未结转！");
                }
            }

            if ("12".equals(accMonthTrace.getId().getYearMonthDate().substring(4))) {
                AccMainVoucher finanAccMainVoucher = accMainVoucherRespository.qryFinanAccMainVoucher(centerCode, branchCode, accBookType, accBookCode, accMonthTrace.getId().getYearMonthDate());
                if (finanAccMainVoucher!=null) {
                    if (!(voucherNo.length==1 && finanAccMainVoucher.getId().getVoucherNo().equals(voucherNo[0]))) {
                        return InvokeResult.failure("请回退决算凭证后再操作！");
                    }
                } else if (accMonthTrace.getTemp()!=null&&"Y".equals(accMonthTrace.getTemp())) {
                    //如果是Y，则表示已进行决算操作但无需要决算生成凭证的数据，再记账时，需要修改此标志
                    accMonthTrace.setTemp("");
                    accMonthTraceRespository.save(accMonthTrace);
                }
            }

            String result = "";

            for(int i=0;i<voucherNo.length;i++){
                //凭证状态更新
                AccMainVoucherId amid=new AccMainVoucherId();
                amid.setCenterCode(centerCode);//核算单位
                amid.setBranchCode(branchCode);//基层单位
                amid.setAccBookType(accBookType);//账套类型
                amid.setAccBookCode(accBookCode);//账套编码
                amid.setYearMonthDate(dto.getYearMonthDate());//年月
                amid.setVoucherNo(voucherNo[i]);//凭证号
                AccMainVoucher am =voucherRepository.findById(amid).get();

                if(am.getApproveBy()!=null && am.getApproveBy().equals(String.valueOf(CurrentUser.getCurrentUser().getId()))){
                    if (!"".equals(result)){
                        result += "," + am.getId().getVoucherNo();
                    } else {
                        result = am.getId().getVoucherNo();
                    }
                } else {
                    //记账
                    //专项余额表
                    AccArticleBalanceId aaid = new AccArticleBalanceId();
                    aaid.setCenterCode(centerCode);//核算单位
                    aaid.setBranchCode(branchCode);//基层单位
                    aaid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
                    aaid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
                    aaid.setYearMonthDate(dto.getYearMonthDate());//年月

                    //当月明细账余额表
                    AccDetailBalanceId adbid = new AccDetailBalanceId();
                    adbid.setCenterCode(centerCode);//核算单位
                    adbid.setBranchCode(branchCode);//基层单位
                    adbid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
                    adbid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
                    adbid.setYearMonthDate(dto.getYearMonthDate());//年月

                    String yearmonth = dto.getYearMonthDate();
                    List<?> asList = voucherRepository.getSource(centerCode, branchCode, accBookType, accBookCode, yearmonth, voucherNo[i]);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                    for(Object object : asList){
                        Map map = new HashMap();
                        map.putAll((Map)object);
                        String itemCode = map.get("item_code").toString();//科目代码
                        String directionIdx = map.get("direction_idx").toString();//科目方向段
                        String directionOther = "";//专项方向段
                        if(map.get("direction_other") != null && !map.get("direction_other").equals("")){
                            directionOther = map.get("direction_other").toString();//专项方向段
                        }

                        //判断科目余额表是否有该科目，如果有则累加，没有则新增
                        List<?> strSqlList = accDetailBalanceRepository.qryAccDetailBalanceByYearMonthDateAndDirectionIdx(centerCode, branchCode,accBookType, accBookCode, dto.getYearMonthDate(), itemCode, directionIdx);

                        BigDecimal debitDest = new BigDecimal("0.00");//本位币借方金额
                        BigDecimal creditDest = new BigDecimal("0.00");//本位币贷方金额
                        BigDecimal debitSource = new BigDecimal("0.00");//原币借方金额
                        BigDecimal creditSource = new BigDecimal("0.00");//原币贷方金额

                        if(map.get("debit_dest") != null && ((BigDecimal)map.get("debit_dest")).compareTo(new BigDecimal("0.00")) != 0){
                            debitDest = (BigDecimal) map.get("debit_dest");//本位币借方金额
                            debitSource = (BigDecimal) map.get("debit_source");//原币借方金额
                        } else {
                            creditDest = (BigDecimal) map.get("credit_dest");//本位币贷方金额
                            creditSource = (BigDecimal) map.get("credit_source");//原币贷方金额
                        }

                        if(strSqlList.size()>0){//不等于0说明已经有该科目
                            adbid.setItemCode(itemCode);
                            adbid.setDirectionIdx(directionIdx);
                            //科目余额表
                            AccDetailBalance adb = accDetailBalanceRepository.findById(adbid).get();

                            //借方+当前值
                            adb.setDebitSource(adb.getDebitSource().add(debitSource));//原币本月借方金额
                            //贷方+当前值
                            adb.setCreditSource(adb.getCreditSource().add(creditSource));//原币本月贷方金额
                            adb.setDebitSourceQuarter(adb.getDebitSourceQuarter().add(new BigDecimal("0.00")));//不填   原币本季借方金额
                            adb.setCreditSourceQuarter(adb.getCreditSourceQuarter().add(new BigDecimal("0.00")));//不填  原币本季贷方金额
                            //借方+当前值
                            adb.setDebitSourceYear(adb.getDebitSourceYear().add(debitSource));//原币本年借方金额
                            //贷方+当前值
                            adb.setCreditSourceYear(adb.getCreditSourceYear().add(creditSource));//原币本年贷方金额
                            adb.setDebitDest(adb.getDebitDest().add(debitDest));//本位币本月借方金额
                            adb.setCreditDest(adb.getCreditDest().add(creditDest));//本位币本月贷方金额
                            adb.setDebitDestQuarter(adb.getDebitDestQuarter().add(new BigDecimal("0.00")));//本位币本季借方金额
                            adb.setCreditDestQuarter(adb.getCreditDestQuarter().add(new BigDecimal("0.00")));//本位币本季贷方金额
                            adb.setDebitDestYear(adb.getDebitDestYear().add(debitDest));//本位币本年借方金额
                            adb.setCreditDestYear(adb.getCreditDestYear().add(creditDest));//本位币本年贷方金额
                            //期末+借-贷
                            adb.setBalanceSource(adb.getBalanceSource().add(debitSource).subtract(creditSource));//原币期末余额
                            //期末+借-贷
                            adb.setBalanceDest(adb.getBalanceDest().add(debitDest).subtract(creditDest));//本位币期末余额

                            //当月明细账余额表保存
                            accDetailBalanceRepository.save(adb);
                            accDetailBalanceRepository.flush();

                            //判断专项字段是否为空，为空则不处理专项余额表
                            if(directionOther != null && !directionOther.equals("")){
                                //有专项
                                //判断专项余额表中 该科目下是否存在该专项 有则累加 无则新增
                                List<?> accSqlList = accArticleBalanceRepository.qryAccArticleBalanceByYearMonthDateAndDirectionIdxAndDirectionOther(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate(), itemCode, directionIdx, directionOther);

                                if(accSqlList.size() > 0){
                                    aaid.setItemCode(itemCode);
                                    aaid.setDirectionIdx(directionIdx);
                                    aaid.setDirectionOther(directionOther);//专项方向段

                                    //专项余额表
                                    AccArticleBalance aa = accArticleBalanceRepository.findById(aaid).get();
                                    //大于零说明专项余额表中该科目下有该专项 累加
                                    //借方+当前值
                                    aa.setDebitSource(aa.getDebitSource().add(debitSource));//原币本月借方金额
                                    //贷方+当前值
                                    aa.setCreditSource(aa.getCreditSource().add(creditSource));//原币本月贷方金额

                                    aa.setDebitSourceQuarter(aa.getDebitSourceQuarter().add(new BigDecimal("0.00")));//不填   原币本季借方金额----------------
                                    aa.setCreditSourceQuarter(aa.getCreditSourceQuarter().add(new BigDecimal("0.00")));//不填  原币本季贷方金额----------------

                                    //借方+当前值
                                    aa.setDebitSourceYear(aa.getDebitSourceYear().add(debitSource));//原币本年借方金额
                                    //贷方+当前值
                                    aa.setCreditSourceYear(aa.getCreditSourceYear().add(creditSource));//原币本年贷方金额

                                    aa.setDebitDest(aa.getDebitDest().add(debitDest));//本位币本月借方金额 (值同原币)
                                    aa.setCreditDest(aa.getCreditDest().add(creditDest));//本位币本月贷方金额 (值同原币)

                                    aa.setDebitDestQuarter(aa.getDebitDestQuarter().add(new BigDecimal("0.00")));//本位币本季借方金额-----------------------
                                    aa.setCreditDestQuarter(aa.getCreditDestQuarter().add(new BigDecimal("0.00")));//本位币本季贷方金额----------------------

                                    aa.setDebitDestYear(aa.getDebitDestYear().add(debitDest));//本位币本年借方金额--------
                                    aa.setCreditDestYear(aa.getCreditDestYear().add(creditDest));//本位币本年贷方金额------

                                    //期末+借-贷
                                    aa.setBalanceSource(aa.getBalanceSource().add(debitSource).subtract(creditSource));//原币期末余额------------------------

                                    //期末+借-贷
                                    aa.setBalanceDest(aa.getBalanceDest().add(debitDest.subtract(creditDest)));//本位币期末余额

                                    //专项余额表保存
                                    accArticleBalanceRepository.save(aa);
                                    accArticleBalanceRepository.flush();
                                }else{
                                    //专项余额表中该科目下没有该专项 新增
                                    aaid.setItemCode(itemCode);
                                    aaid.setDirectionIdx(directionIdx);
                                    aaid.setDirectionOther(directionOther);
                                    AccArticleBalance aa = new AccArticleBalance(aaid);//专项余额表

                                    aa.setDirectionIdxName(map.get("direction_idx_name").toString());
                                    aa.setCurrency(map.get("currency").toString());

                                    aa.setDebitSource(debitSource);//原币本月借方金额
                                    aa.setCreditSource(creditSource);//原币本月贷方金额

                                    aa.setDebitSourceQuarter(new BigDecimal("0.00"));//不填   原币本季借方金额----------------
                                    aa.setCreditSourceQuarter(new BigDecimal("0.00"));//不填  原币本季贷方金额----------------

                                    aa.setDebitSourceYear(debitSource);//原币本年借方金额
                                    aa.setCreditSourceYear(creditSource);//原币本年贷方金额

                                    aa.setDebitDest(debitDest);//本位币本月借方金额 (值同原币)
                                    aa.setCreditDest(creditDest);//本位币本月贷方金额 (值同原币)

                                    aa.setDebitDestQuarter(new BigDecimal("0.00"));//本位币本季借方金额-----------------------
                                    aa.setCreditDestQuarter(new BigDecimal("0.00"));//本位币本季贷方金额----------------------

                                    aa.setDebitDestYear(debitDest);//本位币本年借方金额--------
                                    aa.setCreditDestYear(creditDest);//本位币本年贷方金额------

                                    aa.setBalanceSource(debitSource.subtract(creditSource));//原币期末余额------------------------
                                    aa.setBalanceBeginSource(new BigDecimal("0.00"));//原币期初余额
                                    aa.setBalanceDest(debitDest.subtract(creditDest));//本位币期末余额
                                    aa.setBalanceBeginDest(new BigDecimal("0.00"));//本位币期初余额
                                    aa.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//创建人
                                    aa.setCreateTime(df.format(new Date()));//创建时间

                                    //科目段、专项段 赋值
                                    aa.setF01(map.get("f01") == null ? null : map.get("f01").toString());
                                    aa.setF02(map.get("f02") == null ? null : map.get("f02").toString());
                                    aa.setF03(map.get("f03") == null ? null : map.get("f03").toString());
                                    aa.setF04(map.get("f04") == null ? null : map.get("f04").toString());
                                    aa.setF05(map.get("f05") == null ? null : map.get("f05").toString());
                                    aa.setF06(map.get("f06") == null ? null : map.get("f06").toString());
                                    aa.setF07(map.get("f07") == null ? null : map.get("f07").toString());
                                    aa.setF08(map.get("f08") == null ? null : map.get("f08").toString());
                                    aa.setF09(map.get("f09") == null ? null : map.get("f09").toString());
                                    aa.setF10(map.get("f10") == null ? null : map.get("f10").toString());
                                    aa.setF11(map.get("f11") == null ? null : map.get("f11").toString());
                                    aa.setF12(map.get("f12") == null ? null : map.get("f12").toString());
                                    aa.setF13(map.get("f13") == null ? null : map.get("f13").toString());
                                    aa.setF14(map.get("f14") == null ? null : map.get("f14").toString());
                                    aa.setF15(map.get("f15") == null ? null : map.get("f15").toString());
                                    aa.setF16(map.get("f16") == null ? null : map.get("f16").toString());
                                    aa.setF17(map.get("f17") == null ? null : map.get("f17").toString());
                                    aa.setF18(map.get("f18") == null ? null : map.get("f18").toString());
                                    aa.setF19(map.get("f19") == null ? null : map.get("f19").toString());
                                    aa.setF20(map.get("f20") == null ? null : map.get("f20").toString());
                                    aa.setF21(map.get("f21") == null ? null : map.get("f21").toString());
                                    aa.setF22(map.get("f22") == null ? null : map.get("f22").toString());
                                    aa.setF23(map.get("f23") == null ? null : map.get("f23").toString());
                                    aa.setF24(map.get("f24") == null ? null : map.get("f24").toString());
                                    aa.setF25(map.get("f25") == null ? null : map.get("f25").toString());
                                    aa.setF26(map.get("f26") == null ? null : map.get("f26").toString());
                                    aa.setF27(map.get("f27") == null ? null : map.get("f27").toString());
                                    aa.setF28(map.get("f28") == null ? null : map.get("f28").toString());
                                    aa.setF29(map.get("f29") == null ? null : map.get("f29").toString());
                                    aa.setF30(map.get("f30") == null ? null : map.get("f30").toString());
                                    aa.setS01(map.get("s01") == null ? null : map.get("s01").toString());
                                    aa.setS02(map.get("s02") == null ? null : map.get("s02").toString());
                                    aa.setS03(map.get("s03") == null ? null : map.get("s03").toString());
                                    aa.setS04(map.get("s04") == null ? null : map.get("s04").toString());
                                    aa.setS05(map.get("s05") == null ? null : map.get("s05").toString());
                                    aa.setS06(map.get("s06") == null ? null : map.get("s06").toString());
                                    aa.setS07(map.get("s07") == null ? null : map.get("s07").toString());
                                    aa.setS08(map.get("s08") == null ? null : map.get("s08").toString());
                                    aa.setS09(map.get("s09") == null ? null : map.get("s09").toString());
                                    aa.setS10(map.get("s10") == null ? null : map.get("s10").toString());
                                    aa.setS11(map.get("s11") == null ? null : map.get("s11").toString());
                                    aa.setS12(map.get("s12") == null ? null : map.get("s12").toString());
                                    aa.setS13(map.get("s13") == null ? null : map.get("s13").toString());
                                    aa.setS14(map.get("s14") == null ? null : map.get("s14").toString());
                                    aa.setS15(map.get("s15") == null ? null : map.get("s15").toString());
                                    aa.setS16(map.get("s16") == null ? null : map.get("s16").toString());
                                    aa.setS17(map.get("s17") == null ? null : map.get("s17").toString());
                                    aa.setS18(map.get("s18") == null ? null : map.get("s18").toString());
                                    aa.setS19(map.get("s19") == null ? null : map.get("s19").toString());
                                    aa.setS20(map.get("s20") == null ? null : map.get("s20").toString());

                                    //专项余额表保存
                                    accArticleBalanceRepository.save(aa);
                                    accArticleBalanceRepository.flush();
                                }
                            }

                        }else{
                            //没有该科目直接insert操作
                            //当月明细账余额表
                            adbid.setItemCode(itemCode);
                            adbid.setDirectionIdx(directionIdx);
                            AccDetailBalance adb = new AccDetailBalance(adbid);//科目余额表

                            String currency = map.get("currency").toString();//原币别编码
                            adb.setCurrency(currency);

                            adb.setDirectionIdxName(map.get("direction_idx_name").toString());
                            adb.setDirectionOther("");//专项方向段
                            adb.setDebitSource(debitSource);//原币本月借方金额
                            adb.setCreditSource(creditSource);//原币本月贷方金额
                            adb.setDebitSourceQuarter(new BigDecimal("0.00"));//不填   原币本季借方金额
                            adb.setCreditSourceQuarter(new BigDecimal("0.00"));//不填  原币本季贷方金额
                            adb.setDebitSourceYear(debitSource);//原币本年借方金额
                            adb.setCreditSourceYear(creditSource);//原币本年贷方金额
                            adb.setDebitDest(debitDest);//本位币本月借方金额
                            adb.setCreditDest(creditDest);//本位币本月贷方金额
                            adb.setDebitDestQuarter(new BigDecimal("0.00"));//本位币本季借方金额
                            adb.setCreditDestQuarter(new BigDecimal("0.00"));//本位币本季贷方金额
                            adb.setDebitDestYear(debitDest);//本位币本年借方金额
                            adb.setCreditDestYear(creditDest);//本位币本年贷方金额
                            adb.setBalanceSource(debitSource.subtract(creditSource));//原币期末余额
                            adb.setBalanceBeginSource(new BigDecimal("0.00"));//原币期初余额
                            adb.setBalanceDest(debitDest.subtract(creditDest));//本位币期末余额
                            adb.setBalanceBeginDest(new BigDecimal("0.00"));//本位币期初余额

                            adb.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//创建人
                            adb.setCreateTime(df.format(new Date()));//创建时间

                            //科目段、专项段 赋值
                            adb.setF01(map.get("f01") == null ? null : map.get("f01").toString());
                            adb.setF02(map.get("f02") == null ? null : map.get("f02").toString());
                            adb.setF03(map.get("f03") == null ? null : map.get("f03").toString());
                            adb.setF04(map.get("f04") == null ? null : map.get("f04").toString());
                            adb.setF05(map.get("f05") == null ? null : map.get("f05").toString());
                            adb.setF06(map.get("f06") == null ? null : map.get("f06").toString());
                            adb.setF07(map.get("f07") == null ? null : map.get("f07").toString());
                            adb.setF08(map.get("f08") == null ? null : map.get("f08").toString());
                            adb.setF09(map.get("f09") == null ? null : map.get("f09").toString());
                            adb.setF10(map.get("f10") == null ? null : map.get("f10").toString());
                            adb.setF11(map.get("f11") == null ? null : map.get("f11").toString());
                            adb.setF12(map.get("f12") == null ? null : map.get("f12").toString());
                            adb.setF13(map.get("f13") == null ? null : map.get("f13").toString());
                            adb.setF14(map.get("f14") == null ? null : map.get("f14").toString());
                            adb.setF15(map.get("f15") == null ? null : map.get("f15").toString());
                            adb.setF16(map.get("f16") == null ? null : map.get("f16").toString());
                            adb.setF17(map.get("f17") == null ? null : map.get("f17").toString());
                            adb.setF18(map.get("f18") == null ? null : map.get("f18").toString());
                            adb.setF19(map.get("f19") == null ? null : map.get("f19").toString());
                            adb.setF20(map.get("f20") == null ? null : map.get("f20").toString());
                            adb.setF21(map.get("f21") == null ? null : map.get("f21").toString());
                            adb.setF22(map.get("f22") == null ? null : map.get("f22").toString());
                            adb.setF23(map.get("f23") == null ? null : map.get("f23").toString());
                            adb.setF24(map.get("f24") == null ? null : map.get("f24").toString());
                            adb.setF25(map.get("f25") == null ? null : map.get("f25").toString());
                            adb.setF26(map.get("f26") == null ? null : map.get("f26").toString());
                            adb.setF27(map.get("f27") == null ? null : map.get("f27").toString());
                            adb.setF28(map.get("f28") == null ? null : map.get("f28").toString());
                            adb.setF29(map.get("f29") == null ? null : map.get("f29").toString());
                            adb.setF30(map.get("f30") == null ? null : map.get("f30").toString());

                            //当月明细账余额表保存
                            accDetailBalanceRepository.save(adb);
                            accDetailBalanceRepository.flush();

                            //判断专项字段是否为空，为空则不处理专项余额表
                            if(directionOther != null && !directionOther.equals("")){
                                //专项余额表
                                aaid.setItemCode(itemCode);
                                aaid.setDirectionIdx(directionIdx);
                                aaid.setDirectionOther(directionOther);
                                AccArticleBalance aa = new AccArticleBalance(aaid);

                                aa.setDirectionIdxName(map.get("direction_idx_name").toString());
                                aa.setCurrency(currency);

                                //应为科目余额表中没有该科目 所有专项表中该科目下一定没有该专项
                                //专项余额表中该科目下没有该专项 新增
                                aa.setDebitSource(debitSource);//原币本月借方金额
                                aa.setCreditSource(creditSource);//原币本月贷方金额

                                aa.setDebitSourceQuarter(new BigDecimal("0.00"));//不填   原币本季借方金额----------------
                                aa.setCreditSourceQuarter(new BigDecimal("0.00"));//不填  原币本季贷方金额----------------

                                aa.setDebitSourceYear(debitSource);//原币本年借方金额
                                aa.setCreditSourceYear(creditSource);//原币本年贷方金额

                                aa.setDebitDest(debitDest);//本位币本月借方金额 (值同原币)
                                aa.setCreditDest(creditDest);//本位币本月贷方金额 (值同原币)

                                aa.setDebitDestQuarter(new BigDecimal("0.00"));//本位币本季借方金额-----------------------
                                aa.setCreditDestQuarter(new BigDecimal("0.00"));//本位币本季贷方金额----------------------

                                aa.setDebitDestYear(debitDest);//本位币本年借方金额--------
                                aa.setCreditDestYear(creditDest);//本位币本年贷方金额------
                                aa.setBalanceSource(debitSource.subtract(creditSource));//原币期末余额------------------------

                                aa.setBalanceBeginSource(new BigDecimal("0.00"));//原币期初余额
                                aa.setBalanceDest(debitDest.subtract(creditDest));//本位币期末余额
                                aa.setBalanceBeginDest(new BigDecimal("0.00"));//本位币期初余额

                                aa.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//创建人
                                aa.setCreateTime(df.format(new Date()));//创建时间

                                //科目段、专项段 赋值
                                aa.setF01(map.get("f01") == null ? null : map.get("f01").toString());
                                aa.setF02(map.get("f02") == null ? null : map.get("f02").toString());
                                aa.setF03(map.get("f03") == null ? null : map.get("f03").toString());
                                aa.setF04(map.get("f04") == null ? null : map.get("f04").toString());
                                aa.setF05(map.get("f05") == null ? null : map.get("f05").toString());
                                aa.setF06(map.get("f06") == null ? null : map.get("f06").toString());
                                aa.setF07(map.get("f07") == null ? null : map.get("f07").toString());
                                aa.setF08(map.get("f08") == null ? null : map.get("f08").toString());
                                aa.setF09(map.get("f09") == null ? null : map.get("f09").toString());
                                aa.setF10(map.get("f10") == null ? null : map.get("f10").toString());
                                aa.setF11(map.get("f11") == null ? null : map.get("f11").toString());
                                aa.setF12(map.get("f12") == null ? null : map.get("f12").toString());
                                aa.setF13(map.get("f13") == null ? null : map.get("f13").toString());
                                aa.setF14(map.get("f14") == null ? null : map.get("f14").toString());
                                aa.setF15(map.get("f15") == null ? null : map.get("f15").toString());
                                aa.setF16(map.get("f16") == null ? null : map.get("f16").toString());
                                aa.setF17(map.get("f17") == null ? null : map.get("f17").toString());
                                aa.setF18(map.get("f18") == null ? null : map.get("f18").toString());
                                aa.setF19(map.get("f19") == null ? null : map.get("f19").toString());
                                aa.setF20(map.get("f20") == null ? null : map.get("f20").toString());
                                aa.setF21(map.get("f21") == null ? null : map.get("f21").toString());
                                aa.setF22(map.get("f22") == null ? null : map.get("f22").toString());
                                aa.setF23(map.get("f23") == null ? null : map.get("f23").toString());
                                aa.setF24(map.get("f24") == null ? null : map.get("f24").toString());
                                aa.setF25(map.get("f25") == null ? null : map.get("f25").toString());
                                aa.setF26(map.get("f26") == null ? null : map.get("f26").toString());
                                aa.setF27(map.get("f27") == null ? null : map.get("f27").toString());
                                aa.setF28(map.get("f28") == null ? null : map.get("f28").toString());
                                aa.setF29(map.get("f29") == null ? null : map.get("f29").toString());
                                aa.setF30(map.get("f30") == null ? null : map.get("f30").toString());
                                aa.setS01(map.get("s01") == null ? null : map.get("s01").toString());
                                aa.setS02(map.get("s02") == null ? null : map.get("s02").toString());
                                aa.setS03(map.get("s03") == null ? null : map.get("s03").toString());
                                aa.setS04(map.get("s04") == null ? null : map.get("s04").toString());
                                aa.setS05(map.get("s05") == null ? null : map.get("s05").toString());
                                aa.setS06(map.get("s06") == null ? null : map.get("s06").toString());
                                aa.setS07(map.get("s07") == null ? null : map.get("s07").toString());
                                aa.setS08(map.get("s08") == null ? null : map.get("s08").toString());
                                aa.setS09(map.get("s09") == null ? null : map.get("s09").toString());
                                aa.setS10(map.get("s10") == null ? null : map.get("s10").toString());
                                aa.setS11(map.get("s11") == null ? null : map.get("s11").toString());
                                aa.setS12(map.get("s12") == null ? null : map.get("s12").toString());
                                aa.setS13(map.get("s13") == null ? null : map.get("s13").toString());
                                aa.setS14(map.get("s14") == null ? null : map.get("s14").toString());
                                aa.setS15(map.get("s15") == null ? null : map.get("s15").toString());
                                aa.setS16(map.get("s16") == null ? null : map.get("s16").toString());
                                aa.setS17(map.get("s17") == null ? null : map.get("s17").toString());
                                aa.setS18(map.get("s18") == null ? null : map.get("s18").toString());
                                aa.setS19(map.get("s19") == null ? null : map.get("s19").toString());
                                aa.setS20(map.get("s20") == null ? null : map.get("s20").toString());

                                //专项余额表保存
                                accArticleBalanceRepository.save(aa);
                                accArticleBalanceRepository.flush();
                            }
                        }
                    }

                    am.setVoucherFlag("3");//设置为已记账
                    am.setGeneBy(String.valueOf(CurrentUser.getCurrentUser().getId()));
                    String currentDate = df2.format(new Date());
                    if ((currentDate.substring(0,4)+currentDate.substring(5,7)).equals(am.getId().getYearMonthDate())) {
                        am.setGeneDate(currentDate);
                    } else {
                        int year = Integer.parseInt(am.getId().getYearMonthDate().substring(0,4));
                        int month = Integer.parseInt(am.getId().getYearMonthDate().substring(4));
                        am.setGeneDate(fixedassetsCardVoucherService.getLastDayOfYearMonth(year, month));
                    }
                    voucherRepository.save(am);

                    voucherRepository.flush();
                }
            }

            if (!"".equals(result)) {
                if (result.split(",").length!=voucherNo.length) {
                    return InvokeResult.success("凭证部分记账成功，记账失败凭证："+result+"，原因是：记账人与凭证复核人不能是同一人！");
                } else {
                    return InvokeResult.failure("凭证记账失败，原因是：记账人与凭证复核人不能是同一人！");
                }
            }

            return InvokeResult.success("凭证记账成功！");
        }
    }

    /**
     * 反记账
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult revokeAccounting(VoucherDTO dto) {
        synchronized(this){
            String centerCode = CurrentUser.getCurrentLoginManageBranch();
            String branchCode = CurrentUser.getCurrentLoginManageBranch();
            String accBookType = CurrentUser.getCurrentLoginAccountType();
            String accBookCode = CurrentUser.getCurrentLoginAccount();
            //获取该机构当月全部科目余额数据
            List<AccDetailBalance> accDetailBalancelList = accDetailBalanceRepository.qryAccDetailBalanceByYearMonthDateAndDirectionIdx(centerCode, branchCode,accBookType, accBookCode, dto.getYearMonthDate());
            //获取该机构当月全部专项余额数据
            List<AccArticleBalance> accArticleBalanceList = accArticleBalanceRepository.qryAccArticleBalanceByYearMonthDateAndDirectionIdxAndDirectionOther(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate());


            String[] voucherNo=dto.getVoucherNo().split(",");

            boolean flagZW = true;
            boolean flagGD = true;
            boolean flagWX = true;

            if (voucherNo.length!=0) {
                String yearMonthDate = dto.getYearMonthDate();
                AccMonthTrace accMonthTrace = accMonthTraceRespository.findAccMonthTraceByYearMonthDate(centerCode, accBookType, accBookCode, yearMonthDate);
                if (accMonthTrace != null) {
                    if ("3".equals(accMonthTrace.getAccMonthStat())) {
                        return InvokeResult.failure("反记账失败，当前会计期间已结转");
                    }
                    if ("12".equals(yearMonthDate.substring(4))) {
                        AccMainVoucher finanAccMainVoucher = accMainVoucherRespository.qryFinanAccMainVoucher(centerCode, branchCode, accBookType, accBookCode, yearMonthDate);
                        if (finanAccMainVoucher!=null) {
                            String no = finanAccMainVoucher.getId().getVoucherNo();
                            boolean flag = true;
                            for (String str : voucherNo) {
                                if (no.equals(str)) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                return InvokeResult.failure("请回退决算凭证后再操作！");
                            }
                        } else if (accMonthTrace.getTemp()!=null&&"Y".equals(accMonthTrace.getTemp())) {
                            //如果是Y，则表示已进行决算操作但无需要决算生成凭证的数据，反记账时，需要修改此标志
                            accMonthTrace.setTemp("");
                            accMonthTraceRespository.save(accMonthTrace);
                        }
                    }
                }
            }

            String result = "";

            for(int i=0;i<voucherNo.length;i++){
                //凭证状态更新
                AccMainVoucherId amid=new AccMainVoucherId();
                amid.setCenterCode(centerCode);//核算单位
                amid.setBranchCode(branchCode);//基层单位
                amid.setAccBookType(accBookType);//账套类型
                amid.setAccBookCode(accBookCode);//账套编码
                amid.setYearMonthDate(dto.getYearMonthDate());//年月
                amid.setVoucherNo(voucherNo[i]);//凭证号
                AccMainVoucher am =voucherRepository.findById(amid).get();

                boolean isRevokeAccounting = true;
                if (!(dto.getNeedCheckGeneBy()!=null&&!"N".equals(dto.getNeedCheckGeneBy()))) {
                    if(!am.getGeneBy().equals(String.valueOf(CurrentUser.getCurrentUser().getId()))){
                        isRevokeAccounting = false;
                        if (!"".equals(result)){
                            result += "," + am.getId().getVoucherNo();
                        } else {
                            result = am.getId().getVoucherNo();
                        }
                    }
                }

                if (isRevokeAccounting) {
                    //反记账
                    //专项余额表
                    AccArticleBalanceId aaid = new AccArticleBalanceId();
                    aaid.setCenterCode(centerCode);//核算单位
                    aaid.setBranchCode(branchCode);//基层单位
                    aaid.setAccBookType(accBookType);//账套类型
                    aaid.setAccBookCode(accBookCode);//账套编码
                    aaid.setYearMonthDate(dto.getYearMonthDate());//年月

                    //当月明细账余额表
                    AccDetailBalanceId adbid = new AccDetailBalanceId();
                    adbid.setCenterCode(centerCode);//核算单位
                    adbid.setBranchCode(branchCode);//基层单位
                    adbid.setAccBookType(accBookType);//账套类型
                    adbid.setAccBookCode(accBookCode);//账套编码
                    adbid.setYearMonthDate(dto.getYearMonthDate());//年月

                    List<?> asList = accSubVoucherRespository.getAccSubVoucherByYearMonthDateAndVoucherNo(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate(), voucherNo[i]);
                    for(Object object : asList){
                        AccSubVoucher accSubVoucher = (AccSubVoucher) object;
                        String itemCode = accSubVoucher.getItemCode();//科目代码
                        String directionIdx = accSubVoucher.getDirectionIdx();//科目方向段
                        String directionOther = "";
                        if(accSubVoucher.getDirectionOther() != null && !"".equals(accSubVoucher.getDirectionOther())){
                            directionOther = accSubVoucher.getDirectionOther();//专项方向段
                        }

                        BigDecimal debitDest = new BigDecimal("0.00");//本位币借方金额
                        BigDecimal creditDest = new BigDecimal("0.00");//本位币贷方金额
                        BigDecimal debitSource = new BigDecimal("0.00");//原币借方金额
                        BigDecimal creditSource = new BigDecimal("0.00");//原币贷方金额

                        if(accSubVoucher.getDebitDest() != null && accSubVoucher.getDebitDest().compareTo(new BigDecimal("0.00")) != 0){
                            debitDest = accSubVoucher.getDebitDest();//本位币借方金额
                            debitSource = accSubVoucher.getDebitSource();//原币借方金额
                        } else {
                            creditDest = accSubVoucher.getCreditDest();//本位币贷方金额
                            creditSource = accSubVoucher.getCreditSource();//原币贷方金额
                        }

                        //专项余额表
                        aaid.setItemCode(itemCode);//科目代码
                        aaid.setDirectionIdx(directionIdx);//科目方向段
                        aaid.setDirectionOther(directionOther);//专项方向段

                        //当月明细账余额表
                        adbid.setItemCode(itemCode);//科目代码
                        adbid.setDirectionIdx(directionIdx);//科目方向段

                        String directionOtherTemp = directionOther;
                        if (directionOther!=null&&!"".equals(directionOther)) {
                            Optional<AccArticleBalance> firstAccArticleBalance= accArticleBalanceList.stream()
                                    .filter(a -> directionIdx.equals(a.getId().getDirectionIdx()))
                                    .filter(a -> directionOtherTemp.equals(a.getId().getDirectionOther()))
                                    .findFirst();
                            if(firstAccArticleBalance.isPresent()){
                                //专项余额表
                                AccArticleBalance aa = new AccArticleBalance();
                                aa = firstAccArticleBalance.get();

                                //当前-借
                                aa.setDebitSource(aa.getDebitSource().subtract(debitSource));//原币本月借方金额
                                //当前-贷
                                aa.setCreditSource(aa.getCreditSource().subtract(creditSource));//原币本月贷方金额

                                aa.setDebitSourceQuarter(aa.getDebitSourceQuarter().subtract(new BigDecimal("0.00")));//不填   原币本季借方金额----------------
                                aa.setCreditSourceQuarter(aa.getCreditSourceQuarter().subtract(new BigDecimal("0.00")));//不填  原币本季贷方金额----------------

                                //当前-借
                                aa.setDebitSourceYear(aa.getDebitSourceYear().subtract(debitSource));//原币本年借方金额
                                //当前-贷
                                aa.setCreditSourceYear(aa.getCreditSourceYear().subtract(creditSource));//原币本年贷方金额

                                //当前-借
                                aa.setDebitDest(aa.getDebitDest().subtract(debitDest));//本位币本月借方金额 (值同原币)
                                //当前-贷
                                aa.setCreditDest(aa.getCreditDest().subtract(creditDest));//本位币本月贷方金额 (值同原币)

                                aa.setDebitDestQuarter(aa.getDebitDestQuarter().subtract(new BigDecimal("0.00")));//本位币本季借方金额-----------------------
                                aa.setCreditDestQuarter(aa.getCreditDestQuarter().subtract(new BigDecimal("0.00")));//本位币本季贷方金额----------------------

                                //当前-借
                                aa.setDebitDestYear(aa.getDebitDestYear().subtract(debitDest));//本位币本年借方金额--------
                                //当前-贷
                                aa.setCreditDestYear(aa.getCreditDestYear().subtract(creditDest));//本位币本年贷方金额------

                                //期末-借+贷
                                aa.setBalanceSource(aa.getBalanceSource().subtract(debitSource).add(creditSource));//原币期末余额------------------------

                                //期末-借+贷
                                aa.setBalanceDest(aa.getBalanceDest().subtract(debitDest).add(creditDest));//本位币期末余额

                                aa = null;
                            }else{
                                return InvokeResult.failure("凭证反记账失败，请联系管理员！");
                            }
                        }

                        Optional<AccDetailBalance> firstAccDetailBalance= accDetailBalancelList.stream()
                                .filter(a -> directionIdx.equals(a.getId().getDirectionIdx()))
                                .findFirst();


                        if(firstAccDetailBalance.isPresent()){//不等于0说明已经有该科目
                                //科目余额表
                                AccDetailBalance adb = new AccDetailBalance();
                                adb = firstAccDetailBalance.get();

                                adb.setDebitSource(adb.getDebitSource().subtract(debitSource));//原币本月借方金额
                                adb.setCreditSource(adb.getCreditSource().subtract(creditSource));//原币本月贷方金额
                                adb.setDebitSourceQuarter(adb.getDebitSourceQuarter().subtract(new BigDecimal("0.00")));//不填   原币本季借方金额
                                adb.setCreditSourceQuarter(adb.getCreditSourceQuarter().subtract(new BigDecimal("0.00")));//不填  原币本季贷方金额
                                adb.setDebitSourceYear(adb.getDebitSourceYear().subtract(debitSource));//原币本年借方金额
                                adb.setCreditSourceYear(adb.getCreditSourceYear().subtract(creditSource));//原币本年贷方金额
                                adb.setDebitDest(adb.getDebitDest().subtract(debitDest));//本位币本月借方金额
                                adb.setCreditDest(adb.getCreditDest().subtract(creditDest));//本位币本月贷方金额
                                adb.setDebitDestQuarter(adb.getDebitDestQuarter().subtract(new BigDecimal("0.00")));//本位币本季借方金额
                                adb.setCreditDestQuarter(adb.getCreditDestQuarter().subtract(new BigDecimal("0.00")));//本位币本季贷方金额
                                adb.setDebitDestYear(adb.getDebitDestYear().subtract(debitDest));//本位币本年借方金额
                                adb.setCreditDestYear(adb.getCreditDestYear().subtract(creditDest));//本位币本年贷方金额
                                adb.setBalanceSource(adb.getBalanceSource().subtract(debitSource).add(creditSource));//原币期末余额
                                adb.setBalanceDest(adb.getBalanceDest().subtract(debitDest).add(creditDest));//本位币期末余额
                                adb = null ;
                        }else{
                            return InvokeResult.failure("凭证反记账失败，请联系管理员！");
                        }
                    }

                    am.setVoucherFlag("2");//设置为未记账
                    am.setGeneBy(String.valueOf(CurrentUser.getCurrentUser().getId()));
                    am.setGeneDate(null);//记账日期
                    voucherRepository.save(am);

                    voucherRepository.flush();

                    if (flagZW) {
                        //对账信息表
                        accCheckInfoRespository.updateFlag(am.getId().getYearMonthDate(), CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount(), centerCode);
                        flagZW = false;
                    }
                    if (flagGD && "3".equals(am.getVoucherType()) && "1".equals(am.getGenerateWay())) {
                        //固定资产对账信息回退
                        accGCheckInfoRepository.updateIsCheck(am.getId().getYearMonthDate(), CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount(),CurrentUser.getCurrentLoginManageBranch());
                        flagGD = false;
                    }
                    if (flagWX && "4".equals(am.getVoucherType()) && "1".equals(am.getGenerateWay())) {
                        //无形资产对账信息回退
                        accWCheckInfoRepository.updateIsCheck(am.getId().getYearMonthDate(), CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount(),CurrentUser.getCurrentLoginManageBranch());
                        flagWX = false;
                    }
                    voucherRepository.flush();
                }
            }
            accArticleBalanceRepository.saveAll(accArticleBalanceList);
            accDetailBalanceRepository.saveAll(accDetailBalancelList);
            if (!"".equals(result)) {
                if (result.split(",").length!=voucherNo.length) {
                    return InvokeResult.success("凭证部分反记账成功，反记账失败凭证："+result+"，原因是：反记账人与记账人不一致！");
                } else {
                    return InvokeResult.failure("凭证反记账失败，原因是：反记账人与记账人不一致！");
                }
            }
            return InvokeResult.success("凭证反记账成功！");
        }
    }

    @Override
    public VoucherDTO qryYearMonth() {
        VoucherDTO dto = new VoucherDTO();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT a.year_month_date as yearMonthDate from AccMonthTrace a where 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sql.append(" and a.center_code = ?" + paramsNo);
        params.put(paramsNo, CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append(" and a.acc_book_type = ?" + paramsNo);
        params.put(paramsNo, CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append(" and a.acc_book_code = ?" + paramsNo);
        params.put(paramsNo, CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        sql.append(" and a.acc_month_stat != '3' ORDER BY a.year_month_date asc ");

        List<?> list = voucherRepository.queryBySqlSC(sql.toString(), params);
        if(list != null && list.size() != 0){
            Map map = new HashMap();
            map.putAll((Map) list.get(0));
            dto.setYearMonth((String)map.get("yearMonthDate"));
        }
        return dto;
    }

}
