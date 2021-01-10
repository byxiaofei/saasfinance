package com.sinosoft.service.impl.account;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.account.*;
import com.sinosoft.domain.fixedassets.AccGCheckInfo;
import com.sinosoft.domain.fixedassets.AccGCheckInfoId;
import com.sinosoft.domain.intangibleassets.AccWCheckInfo;
import com.sinosoft.domain.intangibleassets.AccWCheckInfoId;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.dto.account.AccMonthTraceDTO;
import com.sinosoft.repository.AccArticleBalanceRepository;
import com.sinosoft.repository.AccDetailBalanceRepository;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.repository.account.*;
import com.sinosoft.repository.fixedassets.AccGCheckInfoRepository;
import com.sinosoft.repository.intangibleassets.AccWCheckInfoRepository;
import com.sinosoft.service.account.SettlePeriodService;
import com.sinosoft.service.account.VoucherAccountingService;
import com.sinosoft.service.account.VoucherManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-12 10:35
 */
@Service
public class SettlePeriodServiceImpl implements SettlePeriodService{
    private Logger logger = LoggerFactory.getLogger(VoucherApproveServiceImpl.class);
    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;
    @Resource
    private AccArticleBalanceRepository accArticleBalanceRepository;
    @Resource
    private AccDetailBalanceRepository accDetailBalanceRepository;
    @Resource
    private AccArticleBalanceHisRespository accArticleBalanceHisRespository;
    @Resource
    private AccDetailBalanceHisRespository accDetailBalanceHisRespository;
    @Resource
    private AccVoucherNoRespository accVoucherNoRespository;
    @Resource
    private AccMainVoucherRespository accMainVoucherRespository;
    @Resource
    private AccMainVoucherHisRespository accMainVoucherHisRespository;
    @Resource
    private AccSubVoucherHisRespository accSubVoucherHisRespository;
    @Resource
    private AccGCheckInfoRepository accGCheckInfoRepository;
    @Resource
    private AccWCheckInfoRepository accWCheckInfoRepository;
    @Resource
    private AccCheckInfoRespository accCheckInfoRespository;
    @Resource
    private VoucherAccountingService voucherAccountingService;
    @Resource
    private AccSubVoucherRespository accSubVoucherRespository;
    @Resource
    private VoucherRepository voucherRepository;
    @Resource
    private VoucherManageService voucherManageService;

    //查询所有会计期间
    @Override
    public List<?> getSettlePderioListData(AccMonthTraceDTO dto) {
        StringBuffer sql = new StringBuffer();
        sql.append("select a.center_code as centerCode, a.year_month_date as yearMonthDate, ");
        sql.append(" (select c.code_name from codemanage c where c.code_type='yearMonthFlag' and c.code_code = a.acc_month_stat) as accMonthStat,");
        sql.append(" a.acc_book_type as accBookType, ");
        sql.append(" a.acc_book_code as accBookCode, ");
        sql.append(" (select u.user_name from userinfo u where u.id = a.create_by) as createByName, a.temp as temp, a.create_by as createBy ");
        sql.append(" from accmonthtrace a where 1=1");

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

        if(dto.getSettlePeriod1() != null && !dto.getSettlePeriod1().equals("")){
            sql.append(" and a.year_month_date >= ?" + paramsNo);
            params.put(paramsNo, dto.getSettlePeriod1());
            paramsNo++;
        }
        if(dto.getSettlePeriod2() != null && !dto.getSettlePeriod2().equals("")){
            sql.append(" and a.year_month_date <= ?" + paramsNo);
            params.put(paramsNo, dto.getSettlePeriod2());
            paramsNo++;
        }

        sql.append(" ORDER BY a.year_month_date desc");

        return accMonthTraceRespository.queryBySqlSC(sql.toString(), params);
    }

    //查询追加信息
    @Override
    public AccMonthTraceDTO addTo() {
        String currentDate = CurrentTime.getCurrentDate();
        String yearMonthDate = currentDate.substring(0,4)+currentDate.substring(5,7);//追加 年月
        AccMonthTrace accMonthTrace = accMonthTraceRespository.findNewestAccMonthTrace(CurrentUser.getCurrentLoginManageBranch(), CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount());

        if(accMonthTrace != null ){
            yearMonthDate = accMonthTrace.getId().getYearMonthDate();
            if(yearMonthDate.substring(4,6).equals("14")){
                /*yearMonthDate = yearMonthDate.substring(0,4) + "JS";*/
                yearMonthDate = (Integer.parseInt(yearMonthDate.substring(0,4))+1)+"01";
            }else if(yearMonthDate.substring(4,6).equals("09") || yearMonthDate.substring(4,6).equals("10") || yearMonthDate.substring(4,6).equals("11") || yearMonthDate.substring(4,6).equals("12") || yearMonthDate.substring(4,6).equals("13")){
                yearMonthDate = yearMonthDate.substring(0,4) + (Integer.parseInt(yearMonthDate.substring(4,6))+1);
            }else if(yearMonthDate.substring(4,6).equals("JS")){
                yearMonthDate = (Integer.parseInt(yearMonthDate.substring(0,4))+1)+"01";
            }else{//1--8
                yearMonthDate = yearMonthDate.substring(0,5) + (Integer.parseInt(yearMonthDate.substring(5,6))+1);
            }
        }
        AccMonthTraceDTO sp = new AccMonthTraceDTO();
        sp.setCenterCode(accMonthTrace.getId().getCenterCode());//核算单位
        sp.setYearMonthDate(yearMonthDate);//凭证年月
        sp.setAccMonthStat("1");//会计月度状态
        sp.setAccBookType(accMonthTrace.getId().getAccBookType());//账套类型
        sp.setAccBookCode(accMonthTrace.getId().getAccBookCode());//账套编码
        sp.setTemp("");//备用

        return sp;
    }

    //保存追加信息
    @Override
    @Transactional
    public InvokeResult save(AccMonthTraceDTO dto) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();

        AccMonthTraceId amId = new AccMonthTraceId();
        amId.setAccBookCode(dto.getAccBookCode());
        amId.setAccBookType(dto.getAccBookType());
        amId.setCenterCode(dto.getCenterCode());
        amId.setYearMonthDate(dto.getYearMonthDate());
        AccMonthTrace am = new AccMonthTrace();
        am.setAccMonthStat(dto.getAccMonthStat());
        am.setTemp(dto.getTemp());

        //修改上一个会计期间状态
        String lastMonthDate = dto.getYearMonthDate();
        if(lastMonthDate.substring(4,6).equals("01")){
            /*lastMonthDate = Integer.parseInt(lastMonthDate.substring(0,4))-1+"JS";*/
            lastMonthDate = Integer.parseInt(lastMonthDate.substring(0,4))-1+"14";
        }else if(lastMonthDate.substring(4,6).equals("JS")){
            lastMonthDate = lastMonthDate.substring(0,4)  + "12";
        }else{
            lastMonthDate = String.valueOf(Integer.parseInt(lastMonthDate)-1);
        }
        amId.setYearMonthDate(lastMonthDate);
        AccMonthTrace accMonthTrace = accMonthTraceRespository.findById(amId).get();
        accMonthTrace.setAccMonthStat("2");
        accMonthTraceRespository.save(accMonthTrace);
        accMonthTraceRespository.findAll();

        //保存追加会计期间
        amId.setYearMonthDate(dto.getYearMonthDate());
        am.setId(amId);

            /*if(amId.getYearMonthDate().contains("JS") || amId.getYearMonthDate().substring(4,6).equals("14")){
                //如果为结算月，修改状态为 未结转
                am.setAccMonthStat("2");
            }else{
                am.setAccMonthStat("1");
            }*/
        am.setAccMonthStat("1");

        accMonthTraceRespository.saveAndFlush(am);

        //判断是否为结算月,是结算月则存储下一年1月会计期间
            /*if(dto.getYearMonthDate().contains("JS") || dto.getYearMonthDate().substring(4,6).equals("14")){
                AccMonthTrace am2 = new AccMonthTrace();
                amId.setYearMonthDate((Integer.parseInt(dto.getYearMonthDate().substring(0,4))+1)+ "01");
                am2.setId(amId);
                am2.setAccMonthStat("1");
                accMonthTraceRespository.saveAndFlush(am2);
            }*/


        //固定资产会计期间追加
        AccGCheckInfoId agid = new AccGCheckInfoId();
        agid.setCenterCode(centerCode);
        agid.setYearMonthDate(dto.getYearMonthDate());
        agid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
        agid.setAccBookCode(CurrentUser.getCurrentLoginAccount());
        AccGCheckInfo ag = new AccGCheckInfo();
        ag.setId(agid);
        ag.setFlag("0");//未计提
        String str = dto.getYearMonthDate().substring(4,6);
        if(!"JS".equals(str) && !"13".equals(str) && !"14".equals(str)){
            accGCheckInfoRepository.save(ag);//不保存JS月
        }/*else if ("14".equals(str)) {
                agid.setYearMonthDate((Integer.parseInt(dto.getYearMonthDate().substring(0,4))+1)+ "01");
                ag.setId(agid);
                accGCheckInfoRepository.save(ag);
            }*/

        //无形资产会计期间追加
        AccWCheckInfoId awid = new AccWCheckInfoId();
        awid.setCenterCode(centerCode);
        awid.setYearMonthDate(dto.getYearMonthDate());
        awid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
        awid.setAccBookCode(CurrentUser.getCurrentLoginAccount());
        AccWCheckInfo aw = new AccWCheckInfo();
        aw.setId(awid);
        aw.setFlag("0");//未计提
        if(!"JS".equals(str) && !"13".equals(str) && !"14".equals(str)){
            accWCheckInfoRepository.save(aw);//不保存JS月
        }/*else if ("14".equals(str)) {
                awid.setYearMonthDate((Integer.parseInt(dto.getYearMonthDate().substring(0,4))+1)+ "01");
                aw.setId(awid);
                accWCheckInfoRepository.save(aw);
            }*/

        //对账信息表追加数据
        AccMonthTraceId newID = new AccMonthTraceId();
        newID.setCenterCode(centerCode);
        newID.setAccBookType(CurrentUser.getCurrentLoginAccountType());
        newID.setAccBookCode(CurrentUser.getCurrentLoginAccount());
        newID.setYearMonthDate(dto.getYearMonthDate());
        AccCheckInfo accCheckInfo = new AccCheckInfo();
        accCheckInfo.setId(newID);
        accCheckInfo.setIsCarry("");
        accCheckInfo.setIsCheck("");
        accCheckInfoRespository.saveAndFlush(accCheckInfo);

            /*if(dto.getYearMonthDate().contains("JS") || "14".equals(str)){
                accCheckInfo = new AccCheckInfo();
                newID.setYearMonthDate((Integer.parseInt(dto.getYearMonthDate().substring(0,4))+1)+ "01");
                accCheckInfo.setId(newID);
                accCheckInfo.setIsCarry("");
                accCheckInfo.setIsCheck("");
                accCheckInfoRespository.saveAndFlush(accCheckInfo);
            }*/

        //保存凭证最大号
        AccVoucherNoId avnid = new AccVoucherNoId();
        avnid.setAccBookCode(CurrentUser.getCurrentLoginAccount());
        avnid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
        avnid.setCenterCode(centerCode);
        avnid.setYearMonthDate(dto.getYearMonthDate());

        AccVoucherNo avn = new AccVoucherNo();
        avn.setId(avnid);
        avn.setMaxFlag("1");//标志
        avn.setVoucherNo("1");//最大凭证号
        accVoucherNoRespository.saveAndFlush(avn);

            /*if(dto.getYearMonthDate().contains("JS") || "14".equals(str)){
                AccVoucherNo avn2 = new AccVoucherNo();
                *//*avnid.setVoucherNo(String.valueOf(Integer.parseInt(avnid.getVoucherNo())+1));//凭证号*//*
                avnid.setYearMonthDate((Integer.parseInt(dto.getYearMonthDate().substring(0,4))+1)+ "01");
                avn2.setId(avnid);
                avn2.setMaxFlag("1");
                avn2.setVoucherNo("1");//最大凭证号
                accVoucherNoRespository.saveAndFlush(avn2);
            }*/

        return InvokeResult.success();
    }

    //结转
    @Transactional
    @Override
    public InvokeResult settle(AccMonthTraceDTO dto) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        //判断上个会计期间是否结转
        String lastYMD = dto.getYearMonthDate();//获取上期会计期间，默认值为当前会计期间
        if(lastYMD.contains("JS")){
            lastYMD = Integer.parseInt(lastYMD.substring(0,4)) + "12";
        }else if(lastYMD.substring(4,6).equals("01")){
            /*lastYMD = Integer.parseInt(lastYMD.substring(0,4))-1 + "JS";*/
            lastYMD = Integer.parseInt(lastYMD.substring(0,4))-1 + "14";
        }else{
            lastYMD = String.valueOf(Integer.parseInt(lastYMD)-1);
        }

        // 判断上个会计期间是否结转，或为未结转状态。
        AccMonthTrace accMonthTrace = accMonthTraceRespository.findAccMonthTraceByYearMonthDate(centerCode, accBookType, accBookCode, lastYMD);
        if(accMonthTrace != null){//如果取到上期  并且上期为未结转 则return
            if(accMonthTrace.getAccMonthStat().equals("1") || accMonthTrace.getAccMonthStat().equals("2")){
                return InvokeResult.failure("会计期间结转失败，上期为未结转状态！");
            }
        }

        //判断当前会计期间是否全部记账，如果未全部记账则给出提示信息
        List<?> voucheraccountingList = accMainVoucherRespository.qryNoAccountVoucherByYearMonthDate(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate());
        if(voucheraccountingList.size()>0){
            return InvokeResult.failure("当前会计期间存在未记账凭证！");
        }

        AccMonthTrace am = accMonthTraceRespository.findAccMonthTraceByYearMonthDate(centerCode, accBookType, accBookCode, dto.getYearMonthDate());
        if (am != null) {
            if ("14".equals(dto.getYearMonthDate().substring(4,6))) {
                //是否生成决算凭证
                AccMainVoucher finanAccMainVoucher = accMainVoucherRespository.qryFinanAccMainVoucher(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate());
                if (finanAccMainVoucher == null) {
                    if (!(am.getTemp()!=null&&"Y".equals(am.getTemp()))) {
                        return InvokeResult.failure("请先生成当年决算凭证");
                    }
                }
            }
        } else {
            return InvokeResult.failure("未发现当前会计期间");
        }

        //判断是否对账平衡，不平衡或未对账不允许结转
        List<AccCheckInfo> accCheckInfoList = accCheckInfoRespository.findAll(new CusSpecification<AccCheckInfo>().and(
                CusSpecification.Cnd.eq("id.centerCode", centerCode),
                CusSpecification.Cnd.eq("id.accBookType", accBookType),
                CusSpecification.Cnd.eq("id.accBookCode", accBookCode),
                CusSpecification.Cnd.eq("id.yearMonthDate", dto.getYearMonthDate()),
                CusSpecification.Cnd.ne("isCheck", "Y")));
        if (accCheckInfoList!=null&&accCheckInfoList.size()>0) {
            return InvokeResult.failure("当前会计期间未对账或对账不平衡！");
        }

        //下个会计期间 年月
        String newYearMonth = null;
        if(dto.getYearMonthDate().substring(4,6).equals("14")){//更新到决算月
            /*newYearMonth = dto.getYearMonthDate().substring(0,4)+"JS";*///下个会计期间日期
            newYearMonth = Integer.parseInt(dto.getYearMonthDate().substring(0, 4))+1+"01";//下个会计期间日期
        }else if(dto.getYearMonthDate().substring(4,6).equals("JS")){
            newYearMonth = Integer.parseInt(dto.getYearMonthDate().substring(0, 4))+1+"01";//下个会计期间日期
        }else{//更新到下个会计期间
            newYearMonth = String.valueOf(Integer.parseInt(dto.getYearMonthDate())+1);//下个会计期间日期
        }

        //将专项余额表数据 备份到 历史专项余额表
        accArticleBalanceRepository.updateHisData(dto.getYearMonthDate(),accBookType, accBookCode, centerCode, branchCode);
//        List<AccArticleBalance> accArticleBalances = accArticleBalanceRepository.qryAccArticleBalanceByYearMonthDate(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate());
//        accArticleBalanceRepository.saveAll(accArticleBalances);
        accArticleBalanceRepository.flush();
        //将余额表年月改为下个会计期间
        accArticleBalanceRepository.updateYearMonth(newYearMonth, dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);
        accArticleBalanceRepository.flush();

        List<?> aaHisList = accArticleBalanceRepository.qryAccArticleBalanceByYearMonthDate(centerCode, branchCode, accBookType, accBookCode, newYearMonth);
        if(aaHisList.size() != 0){
            List<AccArticleBalance> nextAABList = new ArrayList<>();
            for(Object obj : aaHisList){
                //判断是否跨年 如果不跨年则将数据更新到下一个会计期间 如果跨年则生成下年一月数据
                if(!"14".equals(dto.getYearMonthDate().substring(4,6)) && !"JS".equals(dto.getYearMonthDate().substring(4,6))){
                    //未跨年，直接生成下月数据  201901
                    AccArticleBalance nextAAB = (AccArticleBalance) obj;

                    nextAAB.setDebitSource(new BigDecimal("0"));//	原币本月借方金额
                    nextAAB.setDebitDest(new BigDecimal("0"));//	本位币本月借方金额
                    nextAAB.setCreditSource(new BigDecimal("0"));//	原币本月贷方金额
                    nextAAB.setCreditDest(new BigDecimal("0"));//	本位币本月贷方金额

                    nextAAB.setBalanceBeginSource(nextAAB.getBalanceSource());//	原币期初余额
                    nextAAB.setBalanceBeginDest(nextAAB.getBalanceDest());//	本位币期初余额
//                  nextAAB.setBalanceSource(new BigDecimal("0"));//	原币期末余额
//                  nextAAB.setBalanceDest(new BigDecimal("0"));//	本位币期末余额

                    nextAAB.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//创建人
                    nextAAB.setCreateTime(CurrentTime.getCurrentTime());//创建时间

                    nextAABList.add(nextAAB);
//                    accArticleBalanceRepository.save(nextAAB);
//                    accArticleBalanceRepository.flush();

                }else{//跨年，生成下年1月数据
                    AccArticleBalance nextAAB = (AccArticleBalance) obj;

                    nextAAB.setDebitSource(new BigDecimal("0"));//	原币本月借方金额
                    nextAAB.setDebitDest(new BigDecimal("0"));//	本位币本月借方金额
                    nextAAB.setCreditSource(new BigDecimal("0"));//	原币本月贷方金额
                    nextAAB.setCreditDest(new BigDecimal("0"));//	本位币本月贷方金额

                    nextAAB.setDebitDestQuarter(new BigDecimal("0"));
                    nextAAB.setDebitSourceQuarter(new BigDecimal("0"));
                    nextAAB.setCreditDestQuarter(new BigDecimal("0"));
                    nextAAB.setCreditSourceQuarter(new BigDecimal("0"));

                    nextAAB.setDebitSourceYear(new BigDecimal("0"));//debit_source_year	原币本年借方金额
                    nextAAB.setDebitDestYear(new BigDecimal("0"));//debit_dest_year	本位币本年借方金额
                    nextAAB.setCreditSourceYear(new BigDecimal("0"));//credit_source_year	原币本年贷方金额
                    nextAAB.setCreditDestYear(new BigDecimal("0"));//credit_dest_year	本位币本年贷方金额

                    nextAAB.setBalanceBeginSource(nextAAB.getBalanceSource());//	原币期初余额
                    nextAAB.setBalanceBeginDest(nextAAB.getBalanceDest());//	本位币期初余额

//                  nextAAB.setBalanceSource(new BigDecimal("0"));//	原币期末余额
//                  nextAAB.setBalanceDest(new BigDecimal("0"));//	本位币期末余额

                    nextAAB.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//创建人
                    nextAAB.setCreateTime(CurrentTime.getCurrentTime());//创建时间
                    nextAABList.add(nextAAB);
//                    accArticleBalanceRepository.save(nextAAB);
                }
//                accArticleBalanceRepository.flush();
            }
            accArticleBalanceRepository.saveAll(nextAABList);
            accArticleBalanceRepository.flush();
        }

        //将明细账余额表数据 备份 到历史明细账余额表
        accDetailBalanceRepository.updateHisData(dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);
        accDetailBalanceRepository.flush();
//        accDetailBalanceRepository.qryAccDetailBalanceByYearMonthDate(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate());
//        accDetailBalanceRepository.save();
        //将余额表年月改为下个会计期间
        accDetailBalanceRepository.updateYearMonth(newYearMonth, dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);
        accDetailBalanceRepository.flush();

        List<?> adHisList = accDetailBalanceRepository.qryAccDetailBalanceByYearMonthDate(centerCode, branchCode, accBookType, accBookCode, newYearMonth);
        if(adHisList.size() != 0){
            List<AccDetailBalance> nextADBList = new ArrayList<>();
            for(Object obj : adHisList){
                if(!"14".equals(dto.getYearMonthDate().substring(4,6)) && !"JS".equals(dto.getYearMonthDate().substring(4,6))){
                    //未跨年，直接生成下月数据
                    AccDetailBalance nextADB = (AccDetailBalance) obj;

                    nextADB.setDebitSource(new BigDecimal("0"));//	原币本月借方金额
                    nextADB.setDebitDest(new BigDecimal("0"));//	本位币本月借方金额
                    nextADB.setCreditSource(new BigDecimal("0"));//	原币本月贷方金额
                    nextADB.setCreditDest(new BigDecimal("0"));//	本位币本月贷方金额

                    nextADB.setBalanceBeginSource(nextADB.getBalanceSource());//	原币期初余额
                    nextADB.setBalanceBeginDest(nextADB.getBalanceDest());//	本位币期初余额
//                  nextADB.setBalanceSource(new BigDecimal("0"));//	原币期末余额
//                  nextADB.setBalanceDest(new BigDecimal("0"));//	本位币期末余额

                    nextADB.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//创建人
                    nextADB.setCreateTime(CurrentTime.getCurrentTime());//创建时间

                    nextADBList.add(nextADB);
//                    accDetailBalanceRepository.save(nextADB);

                }else{
                    AccDetailBalance nextADB = (AccDetailBalance) obj;

                    nextADB.setDebitSource(new BigDecimal("0"));//	原币本月借方金额
                    nextADB.setDebitDest(new BigDecimal("0"));//	本位币本月借方金额
                    nextADB.setCreditSource(new BigDecimal("0"));//	原币本月贷方金额
                    nextADB.setCreditDest(new BigDecimal("0"));//	本位币本月贷方金额

                    nextADB.setDebitDestQuarter(new BigDecimal("0"));
                    nextADB.setDebitSourceQuarter(new BigDecimal("0"));
                    nextADB.setCreditDestQuarter(new BigDecimal("0"));
                    nextADB.setCreditSourceQuarter(new BigDecimal("0"));

                    nextADB.setDebitSourceYear(new BigDecimal("0"));//debit_source_year	原币本年借方金额
                    nextADB.setDebitDestYear(new BigDecimal("0"));//debit_dest_year	本位币本年借方金额
                    nextADB.setCreditSourceYear(new BigDecimal("0"));//credit_source_year	原币本年贷方金额
                    nextADB.setCreditDestYear(new BigDecimal("0"));//credit_dest_year	本位币本年贷方金额

                    nextADB.setBalanceBeginSource(nextADB.getBalanceSource());//	原币期初余额
                    nextADB.setBalanceBeginDest(nextADB.getBalanceDest());//	本位币期初余额
//                  nextADB.setBalanceSource(new BigDecimal("0"));//	原币期末余额
//                  nextADB.setBalanceDest(new BigDecimal("0"));//	本位币期末余额

                    nextADB.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//创建人
                    nextADB.setCreateTime(CurrentTime.getCurrentTime());//创建时间

                    nextADBList.add(nextADB);
//                    accDetailBalanceRepository.save(nextADB);
                }
//                accDetailBalanceRepository.flush();
            }
            accDetailBalanceRepository.saveAll(nextADBList);
            accDetailBalanceRepository.flush();
        }

        //根据yearMonthDate将凭证主表数据备份到历史表中，并将主表数据删除
        //第一步，根据年月将凭证主表信息备份到历史表中
        accMainVoucherHisRespository.copyToHis(dto.getYearMonthDate(),accBookType, accBookCode, centerCode, branchCode);
//        List<AccMainVoucher> accMainVouchers = accMainVoucherRespository.queryAccMainVoucherByBaseChoose(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate());
//        accMainVoucherHisRespository.saveAll();
        //第二步，根据年月将凭证主表信息删除
        accMainVoucherRespository.deleteMainByYmd(dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);//删除

        //根据yearMonthDate将凭证子表数据备份到历史表中，并将子表数据删除
        //第一步，根据年月将凭证子表信息备份到历史表中
        accSubVoucherHisRespository.copyToHis(dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);

        //第二步，根据年月将凭证子表信息删除
        accMainVoucherRespository.deleteSubByYmd(dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);//删除

        //结账状态修改
        //会计期间状态修改
        am.setAccMonthStat("3");
        am.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//操作人
        am.setCreateTime(CurrentTime.getCurrentTime());//操作时间
        accMonthTraceRespository.save(am);

        //对账信息表修改数据
        AccMonthTraceId checkID = new AccMonthTraceId();
        checkID.setCenterCode(centerCode);
        checkID.setAccBookType(accBookType);
        checkID.setAccBookCode(accBookCode);
        checkID.setYearMonthDate(dto.getYearMonthDate());
        AccCheckInfo a = accCheckInfoRespository.findById(checkID).get();
        a.setIsCarry("Y");//已结转
        accCheckInfoRespository.save(a);

        //获取下一个会计期间，如果下一个会计期间不存在，则生成该会计期间
        //当前会计期间   dto.getYearMonthDate()
        String nextYMD = dto.getYearMonthDate();//下一个会计期间，默认等于当前会计期间
        if(nextYMD.substring(4,6).equals("14")){//如果当前为12月，则生成决算月期间
            /*nextYMD = nextYMD.substring(0,4) + "JS";*/
            nextYMD = (Integer.parseInt(nextYMD.substring(0,4))+1) + "01";
        }else if(nextYMD.substring(4,6).equals("JS")){//如果当前为决算月，则生成下年一月会计期间
            nextYMD = (Integer.parseInt(nextYMD.substring(0,4))+1) + "01";
        }else{//其他月份直接+1
            nextYMD = String.valueOf(Integer.parseInt(dto.getYearMonthDate())+1);
        }

        AccMonthTrace nextAMT = accMonthTraceRespository.findAccMonthTraceByYearMonthDate(centerCode, accBookType, accBookCode, nextYMD);
        if(!(nextAMT!=null)){//下一个会计期间不存在,自动追加下一个会计期间

            AccMonthTraceId amtid = new AccMonthTraceId();
            amtid.setAccBookCode(accBookCode);
            amtid.setAccBookType(accBookType);
            amtid.setCenterCode(centerCode);
            amtid.setYearMonthDate(nextYMD);
            AccMonthTrace amt = new AccMonthTrace();
            amt.setId(amtid);
            amt.setTemp("");
            amt.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
            amt.setCreateTime(CurrentTime.getCurrentTime());
            amt.setAccMonthStat("1");//当前

            /*if (nextYMD.contains("JS") || nextYMD.substring(4,6).equals("14")) {
                amt.setAccMonthStat("2");//未结转
            }*/

            accMonthTraceRespository.saveAndFlush(amt);//保存

            //判断是否为结算月，是决算月则存储下一年1月会计期间
            /*if(nextYMD.contains("JS") || nextYMD.substring(4,6).equals("14")){
                accMonthTraceRespository.addYearMonthDate(accBookCode,accBookType,centerCode,
                        (Integer.parseInt(nextYMD.substring(0,4))+1)+ "01","1",
                        amt.getCreateBy(),amt.getCreateTime(),amt.getTemp());
            }*/

            //资产会计期间不自动追加JS月
            String str = nextYMD.substring(4,6);
            if(!nextYMD.contains("JS") && !"13".equals(str) && !"14".equals(str)){
                //固定资产会计期间追加
                AccGCheckInfoId agid = new AccGCheckInfoId();
                agid.setCenterCode(centerCode);
                agid.setYearMonthDate(nextYMD);
                agid.setAccBookType(accBookType);
                agid.setAccBookCode(accBookCode);
                AccGCheckInfo ag = new AccGCheckInfo();
                ag.setId(agid);
                ag.setFlag("0");//未计提
                accGCheckInfoRepository.save(ag);//不保存JS月

                //无形资产会计期间追加
                AccWCheckInfoId awid = new AccWCheckInfoId();
                awid.setCenterCode(centerCode);
                awid.setYearMonthDate(nextYMD);
                awid.setAccBookType(accBookType);
                awid.setAccBookCode(accBookCode);
                AccWCheckInfo aw = new AccWCheckInfo();
                aw.setId(awid);
                aw.setFlag("0");//未计提
                accWCheckInfoRepository.save(aw);
            }

            //对账信息表追加数据
            AccMonthTraceId newID = new AccMonthTraceId();
            newID.setCenterCode(centerCode);
            newID.setAccBookType(accBookType);
            newID.setAccBookCode(accBookCode);
            newID.setYearMonthDate(nextYMD);
            AccCheckInfo accCheckInfo = new AccCheckInfo();
            accCheckInfo.setId(newID);
            accCheckInfo.setIsCarry("");
            accCheckInfo.setIsCheck("");
            accCheckInfoRespository.saveAndFlush(accCheckInfo);

            /*if(nextYMD.contains("JS") || "14".equals(str)){
                accCheckInfo = new AccCheckInfo();
                newID.setYearMonthDate((Integer.parseInt(nextYMD.substring(0,4))+1)+ "01");
                accCheckInfo.setId(newID);
                accCheckInfo.setIsCarry("");
                accCheckInfo.setIsCheck("");
                accCheckInfoRespository.saveAndFlush(accCheckInfo);
            }*/

            //凭证号最大表数据追加
            AccVoucherNoId avid = new AccVoucherNoId();
            avid.setCenterCode(centerCode);
            avid.setYearMonthDate(nextYMD);
            avid.setAccBookCode(accBookCode);
            avid.setAccBookType(accBookType);
            AccVoucherNo avn = new AccVoucherNo();
            avn.setId(avid);
            avn.setVoucherNo("1");
            avn.setMaxFlag("1");
            accVoucherNoRespository.saveAndFlush(avn);

            /*if(nextYMD.contains("JS") || "14".equals(str)){
                AccVoucherNo avn2 = new AccVoucherNo();
                *//*avnid.setVoucherNo(String.valueOf(Integer.parseInt(avnid.getVoucherNo())+1));//凭证号*//*
                avid.setYearMonthDate((Integer.parseInt(nextYMD.substring(0,4))+1)+ "01");
                avn2.setId(avid);
                avn2.setMaxFlag("1");
                avn2.setVoucherNo("1");//最大凭证号
                accVoucherNoRespository.saveAndFlush(avn2);
            }*/
        }

        return InvokeResult.success();
    }


    //反结转
    @Transactional
    @Override
    public InvokeResult unSettle(AccMonthTraceDTO dto) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        //判断反结转和结转是否为同一人
        AccMonthTraceId amtid = new AccMonthTraceId();
        amtid.setYearMonthDate(dto.getYearMonthDate());
        amtid.setCenterCode(centerCode);
        amtid.setAccBookType(accBookType);
        amtid.setAccBookCode(accBookCode);
        AccMonthTrace amt = accMonthTraceRespository.findById(amtid).get();
        if (!String.valueOf(CurrentUser.getCurrentUser().getId()).equals(amt.getCreateBy())) {
            return InvokeResult.failure("反结转失败，反结转人与结转人不同！");
        }

        //先判断下一个会计期间是否已经结转或决算，是则给出提示信息
        String nextYMD = dto.getYearMonthDate();
        if ("JS".equals(nextYMD.substring(4))) {
            nextYMD = Integer.parseInt(nextYMD.substring(0,4))+1+"01";
        } else if ("14".equals(nextYMD.substring(4))) {
            /*nextYMD = nextYMD.substring(0,4)+"JS";*/
            nextYMD = Integer.parseInt(nextYMD.substring(0,4))+1+"01";
        } else {
            nextYMD = Integer.parseInt(nextYMD)+1+"";
        }

        List<?> list = accMonthTraceRespository.findAccMonthTraceByYearMonthDateAndAccMonthStat(centerCode, accBookType, accBookCode, nextYMD, new String[]{"3","5"});
        if(list!=null&&list.size() != 0){
            if ("JS".equals(nextYMD.substring(4)) || "14".equals(nextYMD.substring(4))) {
                return InvokeResult.failure("当年决算月已经进行了决算操作，请先反决算后再进行操作！");
            } else {
                return InvokeResult.failure("下一个会计期间已结转，请先反结转后再进行操作！");
            }
        }

        //判断当年决算月是否已经决算，已经决算则给出提示信息
        /*if(!dto.getYearMonthDate().contains("JS")){
            StringBuffer jsSql = new StringBuffer();
            String jsStr = dto.getYearMonthDate().substring(0,4)+"JS";
            jsSql.append("select * from accmonthtrace a where 1=1 and a.year_month_date = '"+ jsStr +"'" +
                    "and acc_book_type = '"+ accBookType +"' and acc_book_code = '"+ accBookCode +"' ");
            List<?> list = accMonthTraceRespository.queryBySql(jsSql.toString(), AccMonthTrace.class);
            if(list.size() != 0){//当年会计期间已经决算
                if(((AccMonthTrace)list.get(0)).getAccMonthStat().equals("5")){
                    return InvokeResult.failure("当年决算月已经进行了决算操作，请先反决算后再进行操作！");
                }
            }
        }*/

        //如果是反结转13月，则需要判断14月是否生成决算凭证，如果已生成，则不允许反结转13月
        if ("13".equals(dto.getYearMonthDate().substring(4,6))) {
            String finanYM = dto.getYearMonthDate().substring(0,4) + "14";
            AccMainVoucher finanAccMainVoucher = accMainVoucherRespository.qryFinanAccMainVoucher(centerCode, branchCode, accBookType, accBookCode, finanYM);
            if (finanAccMainVoucher!=null) {
                return InvokeResult.failure(finanYM+"会计月度已生成决算凭证，请回退决算凭证后再操作！");
            }
        }

        //从历史专项余额表 中将数据拿到 专项余额表
        String lastYMD = null;
        if (dto.getYearMonthDate().substring(4, 6).equals("JS")) {
            lastYMD = dto.getYearMonthDate().substring(0, 4) + "12";
        } else if (dto.getYearMonthDate().substring(4, 6).equals("01")) {
            /*lastYMD = dto.getYearMonthDate().substring(0, 4) + "JS";*/
            lastYMD = Integer.valueOf(dto.getYearMonthDate().substring(0, 4))-1 + "14";
        } else {
            lastYMD = String.valueOf(Integer.parseInt(dto.getYearMonthDate()) - 1);
        }

        //清空余额表数据
        accArticleBalanceRepository.clear(accBookType, accBookCode, centerCode, branchCode);
        accArticleBalanceRepository.flush();
        //从历史专项余额表 中将数据拿到 专项余额表
        accArticleBalanceRepository.updateLastData(dto.getYearMonthDate(),accBookType, accBookCode, centerCode, branchCode);
        accArticleBalanceRepository.flush();
        //清空明细账余额表
        accDetailBalanceRepository.clear(accBookType, accBookCode, centerCode, branchCode);
        accDetailBalanceRepository.flush();
        //从历史明细账余额表 中将数据拿到 明细账余额表
        accDetailBalanceRepository.updateLastData(dto.getYearMonthDate(),accBookType, accBookCode, centerCode, branchCode);
        accDetailBalanceRepository.flush();

        //历史专项余额表
        accArticleBalanceHisRespository.deleteDate(dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);
        //历史明细账余额表
        accDetailBalanceHisRespository.deleteDate(dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);
        //将数据从历史主表复制到原主表
        accMainVoucherHisRespository.copyToMain(dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);
        //将历史主表清空
        accMainVoucherHisRespository.deleteMainByYmd(dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);
        accMainVoucherHisRespository.flush();
        //将数据从历史子表复制到原子表
        accSubVoucherHisRespository.copyToSub(dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);
        //将历史子表清空
        accSubVoucherHisRespository.deleteSubByYmd(dto.getYearMonthDate(), accBookType, accBookCode, centerCode, branchCode);
        accSubVoucherHisRespository.flush();
        //主表之后会计期间状态修改
        accMainVoucherRespository.updateFlag(dto.getYearMonthDate(), String.valueOf(CurrentUser.getCurrentUser().getId()),CurrentTime.getCurrentTime(), accBookType, accBookCode, centerCode, branchCode);
        //当前会计期间状态修改
        accMainVoucherRespository.updateDQFlag(dto.getYearMonthDate(), String.valueOf(CurrentUser.getCurrentUser().getId()),CurrentTime.getCurrentTime(), accBookType, accBookCode, centerCode, branchCode);
        //当前会计月度之后状态修改
        accMonthTraceRespository.updateFlag(dto.getYearMonthDate(), String.valueOf(CurrentUser.getCurrentUser().getId()),CurrentTime.getCurrentTime(), accBookType, accBookCode, centerCode);

        //当前会计月度状态修改
        if ("14".equals(dto.getYearMonthDate().substring(4,6))) {
            //决算月度
            accMonthTraceRespository.updateDQFlag2(dto.getYearMonthDate(), String.valueOf(CurrentUser.getCurrentUser().getId()),CurrentTime.getCurrentTime(), accBookType, accBookCode, centerCode);
        } else {
            accMonthTraceRespository.updateDQFlag(dto.getYearMonthDate(), String.valueOf(CurrentUser.getCurrentUser().getId()),CurrentTime.getCurrentTime(), accBookType, accBookCode, centerCode);
        }

        //对账信息表
        accCheckInfoRespository.updateFlag(dto.getYearMonthDate(), accBookType, accBookCode, centerCode);

        //固定资产对账信息回退
        accGCheckInfoRepository.updateIsCheck(dto.getYearMonthDate(), accBookType, accBookCode, centerCode);
        //无形资产对账信息回退
        accWCheckInfoRepository.updateIsCheck(dto.getYearMonthDate(), accBookType, accBookCode, centerCode);

        //如果反的是14月，需要反记账并删除决算凭证
        if ("14".equals(dto.getYearMonthDate().substring(4,6))) {
           /* AccMainVoucher finanAccMainVoucher = accMainVoucherRespository.qryFinanAccMainVoucher(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate());
            if (finanAccMainVoucher!=null) {
                //反记账
                VoucherDTO voucherDTO = new VoucherDTO();
                voucherDTO.setVoucherNo(finanAccMainVoucher.getId().getVoucherNo());
                voucherDTO.setNeedCheckGeneBy("N");
                voucherAccountingService.revokeAccounting(voucherDTO);
                accMainVoucherHisRespository.flush();

                //删除凭证
                List<AccSubVoucher> subList = accSubVoucherRespository.findAll(new CusSpecification<>().and(
                        CusSpecification.Cnd.eq("id.centerCode", centerCode),
                        CusSpecification.Cnd.eq("id.branchCode", branchCode),
                        CusSpecification.Cnd.eq("id.accBookType", accBookType),
                        CusSpecification.Cnd.eq("id.accBookCode", accBookCode),
                        CusSpecification.Cnd.eq("id.yearMonthDate", dto.getYearMonthDate()),
                        CusSpecification.Cnd.eq("id.voucherNo", finanAccMainVoucher.getId().getVoucherNo())));
                //删除子表凭证
                if (subList!=null&&subList.size()>0) {
                    for (AccSubVoucher accSubVoucher : subList) {
                        accSubVoucherRespository.delete(accSubVoucher);
                    }
                }
                accMainVoucherRespository.delete(finanAccMainVoucher);
                voucherRepository.flush();
                voucherManageService.voucherAnewSortBecauseOccupyOrDel(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate(), finanAccMainVoucher.getId().getVoucherNo(), "del");
                voucherRepository.flush();
            }*/
            return InvokeResult.failure("十四月不可以反结转");
        }

        return InvokeResult.success();
    }

    @Override
    public String addToAndSave(String centerCode,String accBookType, String accBookCode) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        String yearMonthDate = currentDate.substring(0,4)+currentDate.substring(5,7);//追加 年月
        AccMonthTrace accMonthTrace = accMonthTraceRespository.findNewestAccMonthTrace(centerCode, accBookType, accBookCode);

        if(accMonthTrace != null ){
            yearMonthDate = accMonthTrace.getId().getYearMonthDate();
            if(yearMonthDate.substring(4,6).equals("14")){
                /*yearMonthDate = yearMonthDate.substring(0,4) + "JS";*/
                yearMonthDate = (Integer.parseInt(yearMonthDate.substring(0,4))+1)+"01";
            }else if(yearMonthDate.substring(4,6).equals("09") || yearMonthDate.substring(4,6).equals("10") || yearMonthDate.substring(4,6).equals("11") || yearMonthDate.substring(4,6).equals("12") || yearMonthDate.substring(4,6).equals("13")){
                yearMonthDate = yearMonthDate.substring(0,4) + (Integer.parseInt(yearMonthDate.substring(4,6))+1);
            }else if(yearMonthDate.substring(4,6).equals("JS")){
                yearMonthDate = (Integer.parseInt(yearMonthDate.substring(0,4))+1)+"01";
            }else{//1--8
                yearMonthDate = yearMonthDate.substring(0,5) + (Integer.parseInt(yearMonthDate.substring(5,6))+1);
            }
        }
        AccMonthTraceDTO dto = new AccMonthTraceDTO();
        dto.setCenterCode(accMonthTrace.getId().getCenterCode());//核算单位
        dto.setYearMonthDate(yearMonthDate);//凭证年月
        dto.setAccMonthStat("1");//会计月度状态
        dto.setAccBookType(accMonthTrace.getId().getAccBookType());//账套类型
        dto.setAccBookCode(accMonthTrace.getId().getAccBookCode());//账套编码
        dto.setTemp("");//备用


        AccMonthTraceId amId = new AccMonthTraceId();
        amId.setAccBookCode(dto.getAccBookCode());
        amId.setAccBookType(dto.getAccBookType());
        amId.setCenterCode(dto.getCenterCode());
        amId.setYearMonthDate(dto.getYearMonthDate());
        AccMonthTrace am = new AccMonthTrace();
        am.setAccMonthStat(dto.getAccMonthStat());
        am.setTemp(dto.getTemp());

        //修改上一个会计期间状态
        String lastMonthDate = dto.getYearMonthDate();
        if(lastMonthDate.substring(4,6).equals("01")){
            /*lastMonthDate = Integer.parseInt(lastMonthDate.substring(0,4))-1+"JS";*/
            lastMonthDate = Integer.parseInt(lastMonthDate.substring(0,4))-1+"14";
        }else if(lastMonthDate.substring(4,6).equals("JS")){
            lastMonthDate = lastMonthDate.substring(0,4)  + "12";
        }else{
            lastMonthDate = String.valueOf(Integer.parseInt(lastMonthDate)-1);
        }
        amId.setYearMonthDate(lastMonthDate);
        AccMonthTrace accMonthTrace1 = accMonthTraceRespository.findById(amId).get();
        accMonthTrace1.setAccMonthStat("2");
        accMonthTraceRespository.saveAndFlush(accMonthTrace1);
        accMonthTraceRespository.findAll();

        //保存追加会计期间
        amId.setYearMonthDate(dto.getYearMonthDate());
        am.setId(amId);

            /*if(amId.getYearMonthDate().contains("JS") || amId.getYearMonthDate().substring(4,6).equals("14")){
                //如果为结算月，修改状态为 未结转
                am.setAccMonthStat("2");
            }else{
                am.setAccMonthStat("1");
            }*/
        am.setAccMonthStat("1");

        accMonthTraceRespository.saveAndFlush(am);

        //判断是否为结算月,是结算月则存储下一年1月会计期间
            /*if(dto.getYearMonthDate().contains("JS") || dto.getYearMonthDate().substring(4,6).equals("14")){
                AccMonthTrace am2 = new AccMonthTrace();
                amId.setYearMonthDate((Integer.parseInt(dto.getYearMonthDate().substring(0,4))+1)+ "01");
                am2.setId(amId);
                am2.setAccMonthStat("1");
                accMonthTraceRespository.saveAndFlush(am2);
            }*/


        //固定资产会计期间追加
        AccGCheckInfoId agid = new AccGCheckInfoId();
        agid.setCenterCode(centerCode);
        agid.setYearMonthDate(dto.getYearMonthDate());
        agid.setAccBookType(accBookType);
        agid.setAccBookCode(accBookCode);
        AccGCheckInfo ag = new AccGCheckInfo();
        ag.setId(agid);
        ag.setFlag("0");//未计提
        String str = dto.getYearMonthDate().substring(4,6);
        if(!"JS".equals(str) && !"13".equals(str) && !"14".equals(str)){
            accGCheckInfoRepository.save(ag);//不保存JS月
        }/*else if ("14".equals(str)) {
                agid.setYearMonthDate((Integer.parseInt(dto.getYearMonthDate().substring(0,4))+1)+ "01");
                ag.setId(agid);
                accGCheckInfoRepository.save(ag);
            }*/

        //无形资产会计期间追加
        AccWCheckInfoId awid = new AccWCheckInfoId();
        awid.setCenterCode(centerCode);
        awid.setYearMonthDate(dto.getYearMonthDate());
        awid.setAccBookType(accBookType);
        awid.setAccBookCode(accBookCode);
        AccWCheckInfo aw = new AccWCheckInfo();
        aw.setId(awid);
        aw.setFlag("0");//未计提
        if(!"JS".equals(str) && !"13".equals(str) && !"14".equals(str)){
            accWCheckInfoRepository.save(aw);//不保存JS月
        }/*else if ("14".equals(str)) {
                awid.setYearMonthDate((Integer.parseInt(dto.getYearMonthDate().substring(0,4))+1)+ "01");
                aw.setId(awid);
                accWCheckInfoRepository.save(aw);
            }*/

        //对账信息表追加数据
        AccMonthTraceId newID = new AccMonthTraceId();
        newID.setCenterCode(centerCode);
        newID.setAccBookType(accBookType);
        newID.setAccBookCode(accBookCode);
        newID.setYearMonthDate(dto.getYearMonthDate());
        AccCheckInfo accCheckInfo = new AccCheckInfo();
        accCheckInfo.setId(newID);
        accCheckInfo.setIsCarry("");
        accCheckInfo.setIsCheck("");
        accCheckInfoRespository.saveAndFlush(accCheckInfo);

            /*if(dto.getYearMonthDate().contains("JS") || "14".equals(str)){
                accCheckInfo = new AccCheckInfo();
                newID.setYearMonthDate((Integer.parseInt(dto.getYearMonthDate().substring(0,4))+1)+ "01");
                accCheckInfo.setId(newID);
                accCheckInfo.setIsCarry("");
                accCheckInfo.setIsCheck("");
                accCheckInfoRespository.saveAndFlush(accCheckInfo);
            }*/

        //保存凭证最大号
        AccVoucherNoId avnid = new AccVoucherNoId();
        avnid.setAccBookCode(accBookCode);
        avnid.setAccBookType(accBookType);
        avnid.setCenterCode(centerCode);
        avnid.setYearMonthDate(dto.getYearMonthDate());

        AccVoucherNo avn = new AccVoucherNo();
        avn.setId(avnid);
        avn.setMaxFlag("1");//标志
        avn.setVoucherNo("1");//最大凭证号
        accVoucherNoRespository.saveAndFlush(avn);

        return  "success";
    }

}
