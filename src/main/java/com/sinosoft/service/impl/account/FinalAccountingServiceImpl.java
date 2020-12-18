package com.sinosoft.service.impl.account;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.account.*;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.dto.account.AccMonthTraceDTO;
import com.sinosoft.repository.*;
import com.sinosoft.repository.account.*;
import com.sinosoft.repository.fixedassets.AccGCheckInfoRepository;
import com.sinosoft.repository.intangibleassets.AccWCheckInfoRepository;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.account.FinalAccountingService;
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
public class FinalAccountingServiceImpl implements FinalAccountingService{
    private Logger logger = LoggerFactory.getLogger(VoucherApproveServiceImpl.class);
    @Resource
    private AccArticleBalanceRepository accArticleBalanceRepository;
    @Resource
    private AccArticleBalanceHisRespository accArticleBalanceHisRespository;
    @Resource
    private AccDetailBalanceRepository accDetailBalanceRepository;
    @Resource
    private AccDetailBalanceHisRespository accDetailBalanceHisRespository;
    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;
    @Resource
    private VoucherService voucherService;
    @Resource
    private SettlePeriodService settlePeriodService;
    @Resource
    private AccMainVoucherRespository accMainVoucherRespository;
    @Resource
    private AccSubVoucherRespository accSubVoucherRespository;
    @Resource
    private VoucherRepository voucherRepository ;
    @Resource
    private AccVoucherNoRespository accVoucherNoRespository;
    @Resource
    private ProfitLossCarryDownSubjectRepository profitLossCarryDownSubjectRepository;
    @Resource
    private VoucherManageService voucherManageService;
    @Resource
    private UserInfoRepository userInfoRepository;
    @Resource
    private AccGCheckInfoRepository accGCheckInfoRepository;
    @Resource
    private AccWCheckInfoRepository accWCheckInfoRepository;

    //查询所有会计期间
    @Override
    public List<?> getFinalAccountingListData(AccMonthTraceDTO dto) {
        StringBuffer sql = new StringBuffer();
        sql.append("select a.center_code as centerCode, a.year_month_date as yearMonthDate, ");
        sql.append(" '' as accMonthStat,");
        sql.append(" a.acc_book_type as accBookType, ");
        sql.append(" a.acc_book_code as accBookCode, ");
        sql.append(" '' as createBy, a.temp as temp ");
        sql.append(" from accmonthtrace a ");
        sql.append(" where 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sql.append(" and a.acc_book_type = ?" + paramsNo);
        params.put(paramsNo, CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append(" and a.acc_book_code = ?" + paramsNo);
        params.put(paramsNo, CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        sql.append(" and a.center_code = ?" + paramsNo);
        params.put(paramsNo, CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;

        //决算月度在14月
        sql.append(" and a.year_month_date like '%14' ");

        if(dto.getSettlePeriod1() != null && !dto.getSettlePeriod1().equals("")){
            sql.append(" and left(a.year_month_date,4) >= ?" + paramsNo);
            params.put(paramsNo, dto.getSettlePeriod1());
            paramsNo++;
        }
        if(dto.getSettlePeriod2() != null && !dto.getSettlePeriod2().equals("")){
            sql.append(" and left(a.year_month_date,4) <= ?" + paramsNo);
            params.put(paramsNo, dto.getSettlePeriod2());
            paramsNo++;
        }

        sql.append(" ORDER BY a.year_month_date desc");
        List<?> list = accMonthTraceRespository.queryBySqlSC(sql.toString(), params);

        //是否生成决算凭证状态处理
        if (list!=null&&list.size()>0) {
            for (Object o : list) {
                Map map = (Map) o;
                sql.setLength(0);
                sql.append("SELECT a.voucher_no as voucherNo,a.create_by as createBy FROM accmainvoucher a WHERE 1=1 AND a.generate_way = '1' AND a.voucher_type = '1'");
                sql.append(" AND a.center_code = '"+map.get("centerCode")+"'");
                sql.append(" AND a.branch_code = '"+CurrentUser.getCurrentLoginManageBranch()+"'");
                sql.append(" AND a.acc_book_type = '"+map.get("accBookType")+"'");
                sql.append(" AND a.acc_book_code = '"+map.get("accBookCode")+"'");
                sql.append(" AND a.year_month_date = '"+map.get("yearMonthDate")+"'");
                String str = sql.toString();
                str = str.replaceAll("accmainvoucher", "accmainvoucherhis");
                sql.append(" UNION ALL ");
                sql.append(str);
                List<?> list2 = accMonthTraceRespository.queryBySqlSC(sql.toString());
                if (list2!=null&&list2.size()>0) {
                    Map map1 = (Map) list2.get(0);
                    map.put("createBy", userInfoRepository.findById(Long.valueOf((String)map1.get("createBy"))).get().getUserName());
                    map.put("accMonthStat", "Y");
                } else {
                    map.put("accMonthStat", "N");
                }
            }
        }
        return list;
    }

    @Override
    @Transactional
    public InvokeResult finalAccounting(AccMonthTraceDTO dto) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        /*
            1.先判断14月是否结转，再判断有无决算凭证
            2.判断14月份是否全部记账
            3.分别查询出所有14月科目余额不为0、专项科目余额不为0的损益类科目；
                同时查询出损益结转科目设置表数据，再判断这些损益类科目是否均进行了损益结转科目设置
            4.处理决算自动生成凭证基本信息、分录信息
                4.1判断科目是否挂专项，挂了专项则去专项余额表取数，否则科目余额表取数
                4.2处理完损益科目分录后，再处理权益分录
                4.3最后处理凭证基本信息
            5.保存决算自动生成凭证信息
         */

        if (dto.getYearMonthDate()==null || "".equals(dto.getYearMonthDate()) || !dto.getYearMonthDate().endsWith("14")) {
            return InvokeResult.failure("会计期间有误！");
        }

        String year = dto.getYearMonthDate().substring(0, 4);

        // 1.判断月度表信息是否结转。
        List<AccMonthTrace> ymdList = judgeAccMonthTraceMessage(dto,centerCode,branchCode,accBookCode,accBookType);
        if(ymdList!=null&&ymdList.size()!=0){
            if("3".equals(((AccMonthTrace)ymdList.get(0)).getAccMonthStat())){
                return InvokeResult.failure("当前会计期间已结转，不可生成决算凭证");
            }
        }
        // 1.再判断有无决算凭证
        AccMainVoucher finanAccMainVoucher = accMainVoucherRespository.qryFinanAccMainVoucher(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate());
        if (finanAccMainVoucher!=null) {
            return InvokeResult.failure("当前会计期间已生成决算凭证，不可重复生成");
        }
        //判断制单日期即凭证日期必须在当前会计期间内
        String str = dto.getYearMonthDate().substring(4);
        String createTime = dto.getCreateTime().substring(0,4)+dto.getCreateTime().substring(5,7);
        if (!"JS".equals(str) && !"13".equals(str) && !"14".equals(str)) {
            if(!createTime.equals(dto.getYearMonthDate())){
                return InvokeResult.failure("制单日期请选择当前会计期间所在月份！");
            }
        } else {
            if(!createTime.equals(dto.getYearMonthDate().substring(0, 4)+"12")){
                return InvokeResult.failure("制单日期请选择当前会计期间所在月份！");
            }
        }

        //2.判断14月份是否全部记账
        List<AccMainVoucher> accMainVoucherList2 = judgeAccMainVoucherMessage(dto, centerCode, branchCode, accBookCode, accBookType);
        if (accMainVoucherList2!=null&&accMainVoucherList2.size()>0) {
            return InvokeResult.failure("当前会计期间存在未记账凭证");
        }
        //3.分别查询出所有14月科目余额不为0、专项科目余额不为0的 损益类科目；
        //  同时查询出损益结转科目设置表数据，再判断这些损益类科目是否均进行了损益结转科目设置

        //科目余额非0的科目数据集合
        List<AccDetailBalance> subjectBalanceList = queryAccDetailBalanceNonzero(dto,centerCode,branchCode,accBookCode,accBookType);
        //专项科目余额非0的专项科目数据集合
        List<AccArticleBalance> specialBalanceList = queryAccArticleBalanceNonzero(dto,centerCode,branchCode,accBookCode,accBookType);
        //损益结转科目设置数据集合
        List<ProfitLossCarryDownSubject> PLCDSubjectList = queryProfitLossCarryDownSubjectMessage(accBookCode);
        if ((subjectBalanceList!=null&&subjectBalanceList.size()>0) || (specialBalanceList!=null&&specialBalanceList.size()>0)) {

            StringBuffer sql = new StringBuffer();
            sql.append("SELECT a.segment_flag AS segmentFlag,a.segment_col AS segmentCol,a.segment_name AS segmentName FROM accsegmentdefine a");
            List<?> accsegmentdefineList = voucherRepository.queryBySqlSC(sql.toString());
            Map<String, String> accsegmentdefineMap = new HashMap<>();
            if (accsegmentdefineList!=null&&accsegmentdefineList.size()>0) {
                for (Object object : accsegmentdefineList) {
                    Map map = (Map) object;
                    //专项段定义对应位置 key：s字段，value：s字段位置对应的一级专项编码
                    accsegmentdefineMap.put((String) map.get("segmentFlag"), (String) map.get("segmentCol"));
                }
            }

            Map<String, String> PLCDSubjectMap = new HashMap<>();//用于存放损益结转科目设置，方便取数 key：损益科目代码 value：权益科目代码
            Map<String, String> PLCDSubjectMapName = new HashMap<>();//用于存放损益结转科目设置，方便取数 key：权益科目代码 value：权益科目名称
            List<String> useSubjectCode = new ArrayList<>();//已处理过的科目分录（用于排除与 subjectBalanceList 中相同的科目）
            Map<String, BigDecimal> usePLCDSubjectMap = new HashMap<>();//用于暂存结转至的权益科目 key：权益科目代码 value：对应的余额值

            VoucherDTO voucherDTO = new VoucherDTO();//凭证基本信息
            List<VoucherDTO> list1 = new ArrayList<>();//凭证科目分录
            List<VoucherDTO> list2 = new ArrayList<>();//凭证专项分录

            //先处理损益结转科目设置Map集合，当处理凭证分录时若出现 PLCDSubjectMap 中没有的科目，则终止决算处理
            if (PLCDSubjectList!=null&&PLCDSubjectList.size()>0) {
                for (ProfitLossCarryDownSubject p : PLCDSubjectList) {
                    if (p.getRightsInterestsCode()!=null&&!"".equals(p.getRightsInterestsCode())) {
                        //将已设置结转至权益科目的损益科目存于 PLCDSubjectMap 集合中
                        PLCDSubjectMap.put(p.getId().getProfitLossCode(), p.getRightsInterestsCode());
                        //将设置过的权益科目名称存于 PLCDSubjectMapName 集合中
                        PLCDSubjectMapName.put(p.getRightsInterestsCode(), p.getTemp());
                    }
                }

                if (PLCDSubjectMap.size()>0) {
                    //先处理专项科目余额，再处理科目余额
                    if (specialBalanceList!=null&&specialBalanceList.size()>0) {
                        for (AccArticleBalance a : specialBalanceList) {
                            //每一次循环就是一条决算凭证分录
                            BigDecimal balanceDest = a.getBalanceDest();
                            //获取该损益科目对应的权益科目，并对结转至的权益科目进行累计计算
                            boolean flag = setProfitLossCarryDownSubjectBalance(a.getId().getDirectionIdx(), PLCDSubjectMap, usePLCDSubjectMap, balanceDest);
                            if (!flag) {
                                return InvokeResult.failure(a.getId().getDirectionIdx()+":未设置损益结转科目");
                            }
                            //设置决算凭证分录（科目分录+专项分录）
                            setJSVoucher(list1, list2, a, balanceDest, accsegmentdefineMap, year);
                            if (!useSubjectCode.contains(a.getId().getDirectionIdx())) {
                                useSubjectCode.add(a.getId().getDirectionIdx());
                            }
                        }
                        for (AccDetailBalance a : subjectBalanceList) {
                            //每一次循环就是一条决算凭证分录，但要抛出已处理过的科目
                            if (!useSubjectCode.contains(a.getId().getDirectionIdx())) {
                                BigDecimal balanceDest = a.getBalanceDest();
                                //获取该损益科目对应的权益科目，并对结转至的权益科目进行累计计算
                                boolean flag = setProfitLossCarryDownSubjectBalance(a.getId().getDirectionIdx(), PLCDSubjectMap, usePLCDSubjectMap, balanceDest);
                                if (!flag) {
                                    return InvokeResult.failure(a.getId().getDirectionIdx()+":未设置损益结转科目");
                                }
                                //设置决算凭证分录（科目分录+专项分录）
                                setJSVoucher(list1, list2, a, balanceDest, year);
                            }
                        }
                    } else {//无专项科目余额非0的损益科目，那么必定存在科目余额非0的损益科目
                        for (AccDetailBalance a : subjectBalanceList) {
                            //每一次循环就是一条决算凭证分录
                            BigDecimal balanceDest = a.getBalanceDest();
                            //获取该损益科目对应的权益科目，并对结转至的权益科目进行累计计算
                            boolean flag = setProfitLossCarryDownSubjectBalance(a.getId().getDirectionIdx(), PLCDSubjectMap, usePLCDSubjectMap, balanceDest);
                            if (!flag) {
                                return InvokeResult.failure(a.getId().getDirectionIdx()+":未设置损益结转科目");
                            }
                            //设置决算凭证分录（科目分录+专项分录）
                            setJSVoucher(list1, list2, a, balanceDest, year);
                        }
                    }

                    //损益类科目的专项科目余额表、科目余额表数据处理完毕后，再处理权益类科目分录
                    if (usePLCDSubjectMap.size()>0) {
                        //遍历 usePLCDSubjectMap
                        for (Map.Entry<String, BigDecimal> entry : usePLCDSubjectMap.entrySet()) {
                            String key = entry.getKey();
                            BigDecimal value = entry.getValue();
                            String name = PLCDSubjectMapName.get(entry.getKey());
                            setJSVoucher(list1, list2, key, name, value, year);
                        }

                        //最后处理凭证基本信息
                        voucherDTO.setVoucherType("1");//凭证类型为决算凭证
                        voucherDTO.setGenerateWay("1");//凭证记账方式为自动
                        voucherDTO.setYearMonth(dto.getYearMonthDate());
                        //未加上制单日期时
                        //voucherDTO.setVoucherDate(dto.getYearMonthDate().substring(0,4)+"-12-31");
                        //加上制单日期
                        voucherDTO.setVoucherDate(dto.getCreateTime());
                        try {
                            //获取凭证最大号，并设置
                            AccVoucherNoId avn = new AccVoucherNoId();
                            avn.setCenterCode(centerCode);
                            avn.setAccBookType(accBookType);
                            avn.setAccBookCode(accBookCode);
                            avn.setYearMonthDate(voucherDTO.getYearMonth());
                            AccVoucherNo accVoucherNo = accVoucherNoRespository.findById(avn).get();
                            String checkNo = CurrentUser.getCurrentLoginManageBranch()+voucherDTO.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(accVoucherNo.getVoucherNo()));
                            voucherDTO.setVoucherNo(checkNo);
                        } catch (Exception e) {
                            System.out.println(voucherDTO.getYearMonth()+"最大凭证号获取出错！");
                        }
                        //保存决算凭证信息
                        System.out.println("开始保存决算凭证信息");
                        InvokeResult invokeResult = voucherService.saveVoucher(list1, list2, voucherDTO);

                        voucherRepository.flush();
                        AccMonthTraceId amtId = new AccMonthTraceId();
                        amtId.setYearMonthDate(dto.getYearMonthDate());
                        amtId.setCenterCode(centerCode);
                        amtId.setAccBookType(accBookType);
                        amtId.setAccBookCode(accBookCode);
                        AccMonthTrace amt = accMonthTraceRespository.findById(amtId).get();
                        amt.setTemp("");
                        accMonthTraceRespository.save(amt);
                    } else {
                        return InvokeResult.failure("未找到合适的损益结转科目");
                    }
                } else {
                    return InvokeResult.failure("请先设置损益结转科目");
                }
            } else {
                return InvokeResult.failure("请先设置损益结转科目");
            }
        } else {
            AccMonthTraceId amtId = new AccMonthTraceId();
            amtId.setYearMonthDate(dto.getYearMonthDate());
            amtId.setCenterCode(centerCode);
            amtId.setAccBookType(accBookType);
            amtId.setAccBookCode(accBookCode);
            AccMonthTrace amt = accMonthTraceRespository.findById(amtId).get();
            amt.setTemp("Y");//当无生成数据时设置，方便后续使用，如作为在反结转时找不到凭证时的一个依据
            accMonthTraceRespository.save(amt);
            System.out.println("无符合生成决算凭证的数据");
        }
        return InvokeResult.success();
    }

    private boolean setProfitLossCarryDownSubjectBalance(String subjectCode, Map<String, String> PLCDSubjectMap, Map<String, BigDecimal> usePLCDSubjectMap, BigDecimal balanceDest){
        //获取该损益科目对应的权益科目，并对结转至的权益科目进行累计计算
        if (PLCDSubjectMap.containsKey(subjectCode)) {
            String value= PLCDSubjectMap.get(subjectCode);
            if (usePLCDSubjectMap.containsKey(value)) {
                usePLCDSubjectMap.put(value, usePLCDSubjectMap.get(value).add(balanceDest));
            } else {
                usePLCDSubjectMap.put(value, balanceDest);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据专项科目余额表数据设置决算凭证分录（科目分录+专项分录）原始数据
     * @param list1 原始科目分录集合
     * @param list2 原始专项分录集合
     * @param accArticleBalance 专项科目余额表数据
     * @param balanceDest 专项科目余额
     */
    private void setJSVoucher(List<VoucherDTO> list1, List<VoucherDTO> list2, AccArticleBalance accArticleBalance, BigDecimal balanceDest, Map<String, String> accsegmentdefineMap, String year){
        //摘要、科目代码、科目名称、借方/贷方金额、专项信息(一级专项代码、专项代码)
        //开始设置凭证科目分录
        VoucherDTO vdto = new VoucherDTO();
        vdto.setRemarkName(year+"年度决算自动生成凭证");
        vdto.setSubjectCode(accArticleBalance.getId().getDirectionIdx());
        vdto.setSubjectName(accArticleBalance.getDirectionIdxName());
        //余额为正，录到贷方发生额，为负，取绝对值录到借方发生额
        if (balanceDest.compareTo(new BigDecimal("0.00"))>=0) {
            vdto.setCredit(balanceDest.toString());
        } else {
            vdto.setDebit(balanceDest.abs().toString());
        }
        list1.add(vdto);

        //开始设置凭证专项分录
        vdto = new VoucherDTO();
        vdto.setSubjectCode(accArticleBalance.getId().getDirectionIdx());
        vdto.setSubjectName(accArticleBalance.getDirectionIdxName());
        vdto.setSpecialCodeS(accArticleBalance.getId().getDirectionOther());
        String[] specialCodeS = accArticleBalance.getId().getDirectionOther().split(",");
        String specialSuperCodeS = "";
        for (String specialCode : specialCodeS) {
            if (specialCode.equals(accArticleBalance.getS01())) {
                specialSuperCodeS += accsegmentdefineMap.get("s01")+",";
            } else if (specialCode.equals(accArticleBalance.getS02())) {
                specialSuperCodeS += accsegmentdefineMap.get("s02")+",";
            } else if (specialCode.equals(accArticleBalance.getS03())) {
                specialSuperCodeS += accsegmentdefineMap.get("s03")+",";
            } else if (specialCode.equals(accArticleBalance.getS04())) {
                specialSuperCodeS += accsegmentdefineMap.get("s04")+",";
            } else if (specialCode.equals(accArticleBalance.getS05())) {
                specialSuperCodeS += accsegmentdefineMap.get("s05")+",";
            } else if (specialCode.equals(accArticleBalance.getS06())) {
                specialSuperCodeS += accsegmentdefineMap.get("s06")+",";
            } else if (specialCode.equals(accArticleBalance.getS07())) {
                specialSuperCodeS += accsegmentdefineMap.get("s07")+",";
            } else if (specialCode.equals(accArticleBalance.getS08())) {
                specialSuperCodeS += accsegmentdefineMap.get("s08")+",";
            } else if (specialCode.equals(accArticleBalance.getS09())) {
                specialSuperCodeS += accsegmentdefineMap.get("s09")+",";
            } else if (specialCode.equals(accArticleBalance.getS10())) {
                specialSuperCodeS += accsegmentdefineMap.get("s10")+",";
            } else if (specialCode.equals(accArticleBalance.getS11())) {
                specialSuperCodeS += accsegmentdefineMap.get("s11")+",";
            } else if (specialCode.equals(accArticleBalance.getS12())) {
                specialSuperCodeS += accsegmentdefineMap.get("s12")+",";
            } else if (specialCode.equals(accArticleBalance.getS13())) {
                specialSuperCodeS += accsegmentdefineMap.get("s13")+",";
            } else if (specialCode.equals(accArticleBalance.getS14())) {
                specialSuperCodeS += accsegmentdefineMap.get("s14")+",";
            } else if (specialCode.equals(accArticleBalance.getS15())) {
                specialSuperCodeS += accsegmentdefineMap.get("s15")+",";
            } else if (specialCode.equals(accArticleBalance.getS16())) {
                specialSuperCodeS += accsegmentdefineMap.get("s16")+",";
            } else if (specialCode.equals(accArticleBalance.getS17())) {
                specialSuperCodeS += accsegmentdefineMap.get("s17")+",";
            } else if (specialCode.equals(accArticleBalance.getS18())) {
                specialSuperCodeS += accsegmentdefineMap.get("s18")+",";
            } else if (specialCode.equals(accArticleBalance.getS19())) {
                specialSuperCodeS += accsegmentdefineMap.get("s19")+",";
            } else if (specialCode.equals(accArticleBalance.getS20())) {
                specialSuperCodeS += accsegmentdefineMap.get("s20")+",";
            }
        }
        vdto.setSpecialSuperCodeS(specialSuperCodeS.substring(0, specialSuperCodeS.length()-1));
        list2.add(vdto);
    }

    /**
     * 根据科目余额表数据设置决算凭证分录（科目分录+专项分录）原始数据
     * @param list1 原始科目分录集合
     * @param list2 原始专项分录集合
     * @param accDetailBalance 科目余额表数据
     * @param balanceDest 科目余额
     */
    private void setJSVoucher(List<VoucherDTO> list1, List<VoucherDTO> list2, AccDetailBalance accDetailBalance, BigDecimal balanceDest, String year){
        //摘要、科目代码、科目名称、借方/贷方金额
        //开始设置凭证科目分录
        VoucherDTO vdto = new VoucherDTO();
        vdto.setRemarkName(year+"年度决算自动生成凭证");
        vdto.setSubjectCode(accDetailBalance.getId().getDirectionIdx());
        vdto.setSubjectName(accDetailBalance.getDirectionIdxName());
        //余额为正，录到贷方发生额，为负，取绝对值录到借方发生额
        if (balanceDest.compareTo(new BigDecimal("0.00"))>=0) {
            vdto.setCredit(balanceDest.toString());
        } else {
            vdto.setDebit(balanceDest.abs().toString());
        }
        list1.add(vdto);

        //开始设置凭证专项分录
        vdto = new VoucherDTO();
        vdto.setSubjectCode(accDetailBalance.getId().getDirectionIdx());
        vdto.setSubjectName(accDetailBalance.getDirectionIdxName());
        list2.add(vdto);
    }

    /**
     *
     * @param list1 原始科目分录集合
     * @param list2 原始专项分录集合
     * @param subjectCode 科目代码
     * @param subjectName 科目名称
     * @param balance 余额
     */
    private void setJSVoucher(List<VoucherDTO> list1, List<VoucherDTO> list2, String subjectCode, String subjectName, BigDecimal balance, String year){
        //摘要、科目代码、科目名称、借方/贷方金额
        //开始设置凭证科目分录
        VoucherDTO vdto = new VoucherDTO();
        vdto.setRemarkName(year+"年度决算自动生成凭证");
        vdto.setSubjectCode(subjectCode);
        vdto.setSubjectName(subjectName);
        //余额为正，录到借方发生额，为负，取绝对值录到贷方发生额
        if (balance.compareTo(new BigDecimal("0.00"))>=0) {
            vdto.setDebit(balance.toString());
        } else {
            vdto.setCredit(balance.abs().toString());
        }
        list1.add(vdto);

        //开始设置凭证专项分录
        vdto = new VoucherDTO();
        vdto.setSubjectCode(subjectCode);
        vdto.setSubjectName(subjectName);
        list2.add(vdto);
    }

    //反决算
    @Override
    @Transactional
    public InvokeResult unFinalAccounting(AccMonthTraceDTO dto) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        if (dto.getYearMonthDate()==null || "".equals(dto.getYearMonthDate()) || !dto.getYearMonthDate().endsWith("14")) {
            return InvokeResult.failure("会计期间有误！");
        }

        StringBuffer sql = new StringBuffer();
        sql.append(" select * from accmonthtrace a  where 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sql.append(" and a.center_code = ?" + paramsNo);
        params.put(paramsNo, centerCode);
        paramsNo++;
        sql.append(" and a.acc_book_type = ?" + paramsNo);
        params.put(paramsNo, accBookType);
        paramsNo++;
        sql.append(" and a.acc_book_code = ?" + paramsNo);
        params.put(paramsNo, accBookCode);
        paramsNo++;
        sql.append(" and a.year_month_date = ?" + paramsNo);
        params.put(paramsNo, dto.getYearMonthDate());
        paramsNo++;

        List<?> ymdList = accMonthTraceRespository.queryBySql(sql.toString(), params, AccMonthTrace.class);
        if(ymdList!=null&&ymdList.size()!=0){
            AccMonthTrace accMonthTrace = (AccMonthTrace) ymdList.get(0);
            if("3".equals(accMonthTrace.getAccMonthStat())){
                return InvokeResult.failure("当前会计期间已结转，不可操作");
            } else if ("Y".equals(accMonthTrace.getTemp())) {
                return InvokeResult.failure("当前会计期间无可回退决算凭证");
            }
        }

        AccMainVoucher finanAccMainVoucher = accMainVoucherRespository.qryFinanAccMainVoucher(centerCode, branchCode, accBookType, accBookCode, dto.getYearMonthDate());
        if (finanAccMainVoucher!=null) {
            if ("2".equals(finanAccMainVoucher.getVoucherFlag())) {
                return InvokeResult.failure("当前决算凭证已复核，不可操作");
            } else if ("3".equals(finanAccMainVoucher.getVoucherFlag())) {
                return InvokeResult.failure("当前决算凭证已记账，不可操作");
            }

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
            voucherManageService.voucherAnewSortBecauseOccupyOrDel(centerCode, centerCode, accBookType, accBookCode, dto.getYearMonthDate(), finanAccMainVoucher.getId().getVoucherNo(), "del");
            voucherRepository.flush();
            AccMonthTraceId amtId = new AccMonthTraceId();
            amtId.setYearMonthDate(dto.getYearMonthDate());
            amtId.setCenterCode(centerCode);
            amtId.setAccBookType(accBookType);
            amtId.setAccBookCode(accBookCode);
            AccMonthTrace amt = accMonthTraceRespository.findById(amtId).get();
            amt.setTemp("");
            accMonthTraceRespository.save(amt);
        }

        return InvokeResult.success();
    }

    /**
     *  判断月度表信息是否存在
     * @return
     */
    private  List<AccMonthTrace>  judgeAccMonthTraceMessage(AccMonthTraceDTO dto,String centerCode,String branchCode,String accBookCode,String accBookType) {
        StringBuffer sql = new StringBuffer();
        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sql.append(" select * from accmonthtrace a where 1=1");
        sql.append(" and a.center_code = ?" + paramsNo);
        params.put(paramsNo, centerCode);
        paramsNo++;
        sql.append(" and a.acc_book_type = ?" + paramsNo);
        params.put(paramsNo, accBookType);
        paramsNo++;
        sql.append(" and a.acc_book_code = ?" + paramsNo);
        params.put(paramsNo, accBookCode);
        paramsNo++;
        sql.append(" and a.year_month_date = ?" + paramsNo);
        params.put(paramsNo, dto.getYearMonthDate());
        paramsNo++;

        List<?> ymdList = accMonthTraceRespository.queryBySql(sql.toString(), params, AccMonthTrace.class);
        List<AccMonthTrace> ymdListFinal = (List<AccMonthTrace>) ymdList;
        return ymdListFinal;
    }

    /**
     * 判断是否存在决算凭证信息
     * @return
     */
    private List<AccMainVoucher> judgeAccMainVoucherMessage(AccMonthTraceDTO dto,String centerCode,String branchCode,String accBookCode,String accBookType){
        StringBuffer sql = new StringBuffer();
        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sql.append("select * from accmainvoucher a where 1=1");

        sql.append(" and a.center_code = ?" + paramsNo);
        params.put(paramsNo, centerCode);
        paramsNo++;
        sql.append(" and a.branch_code = ?" + paramsNo);
        params.put(paramsNo, branchCode);
        paramsNo++;
        sql.append(" and a.acc_book_type = ?" + paramsNo);
        params.put(paramsNo, accBookType);
        paramsNo++;
        sql.append(" and a.acc_book_code = ?" + paramsNo);
        params.put(paramsNo, accBookCode);
        paramsNo++;
        sql.append(" and a.year_month_date = ?" + paramsNo);
        params.put(paramsNo, dto.getYearMonthDate());
        paramsNo++;

        sql.append(" and a.voucher_flag != '3'");
        List<?> ymdList = accMonthTraceRespository.queryBySql(sql.toString(), params, AccMonthTrace.class);
        List<AccMainVoucher> ymdListFinal = (List<AccMainVoucher>) ymdList;
        return ymdListFinal;
    }

    /**
     * 科目余额表非零的信息
     * @return
     */
    private List<AccDetailBalance> queryAccDetailBalanceNonzero(AccMonthTraceDTO dto,String centerCode,String branchCode,String accBookCode,String accBookType){

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();
        StringBuffer subjectBalanceSql = new StringBuffer("SELECT * FROM accdetailbalance a WHERE 1=1");
        subjectBalanceSql.append(" AND a.center_code = ?" + paramsNo);
        params.put(paramsNo, centerCode);
        paramsNo++;
        subjectBalanceSql.append(" AND a.branch_code = ?" + paramsNo);
        params.put(paramsNo, branchCode);
        paramsNo++;
        subjectBalanceSql.append(" AND a.acc_book_type = ?" + paramsNo);
        params.put(paramsNo, accBookType);
        paramsNo++;
        subjectBalanceSql.append(" AND a.acc_book_code = ?" + paramsNo);
        params.put(paramsNo, accBookCode);
        paramsNo++;
        subjectBalanceSql.append(" AND a.year_month_date = ?" + paramsNo);
        params.put(paramsNo, dto.getYearMonthDate());
        paramsNo++;

        subjectBalanceSql.append(" AND a.balance_dest !=0.00");
        subjectBalanceSql.append(" AND a.direction_idx IN(SELECT CONCAT(s.all_subject,s.subject_code,'/') AS subjectCode FROM subjectinfo s WHERE 1=1");

        subjectBalanceSql.append(" AND s.account = ?" + paramsNo);
        params.put(paramsNo, accBookCode);
        paramsNo++;

        subjectBalanceSql.append(" AND s.subject_type = '4' AND s.end_flag = '0' AND s.useflag = '1' AND (s.special_id IS NULL OR s.special_id = '') ORDER BY subjectCode)");
        List<AccDetailBalance> subjectBalanceList = (List<AccDetailBalance>) accDetailBalanceHisRespository.queryBySql(subjectBalanceSql.toString(), params, AccDetailBalance.class);
        return  subjectBalanceList;
    }

    /**
     * 专项余额表非零的信息
     * @return
     */
    private List<AccArticleBalance> queryAccArticleBalanceNonzero(AccMonthTraceDTO dto,String centerCode,String branchCode,String accBookCode,String accBookType){

        StringBuffer specialBalanceSql = new StringBuffer("SELECT * FROM accarticlebalance a WHERE 1=1");

        int paramsNo2 = 1;
        Map<Integer, Object> params2 = new HashMap<>();

        specialBalanceSql.append(" AND a.center_code = ?" + paramsNo2);
        params2.put(paramsNo2, centerCode);
        paramsNo2++;
        specialBalanceSql.append(" AND a.branch_code = ?" + paramsNo2);
        params2.put(paramsNo2, branchCode);
        paramsNo2++;
        specialBalanceSql.append(" AND a.acc_book_type = ?" + paramsNo2);
        params2.put(paramsNo2, accBookType);
        paramsNo2++;
        specialBalanceSql.append(" AND a.acc_book_code = ?" + paramsNo2);
        params2.put(paramsNo2, accBookCode);
        paramsNo2++;
        specialBalanceSql.append(" AND a.year_month_date = ?" + paramsNo2);
        params2.put(paramsNo2, dto.getYearMonthDate());
        paramsNo2++;

        specialBalanceSql.append(" AND a.balance_dest !=0.00");
        specialBalanceSql.append(" AND a.direction_idx IN(SELECT CONCAT(s.all_subject,s.subject_code,'/') AS subjectCode FROM subjectinfo s WHERE 1=1");

        specialBalanceSql.append(" AND s.account = ?" + paramsNo2);
        params2.put(paramsNo2, accBookCode);
        paramsNo2++;

        specialBalanceSql.append(" AND s.subject_type = '4' AND s.end_flag = '0' AND s.useflag = '1' AND s.special_id IS NOT NULL AND s.special_id != '' ORDER BY subjectCode)");
        List<AccArticleBalance> specialBalanceList = (List<AccArticleBalance>) accArticleBalanceHisRespository.queryBySql(specialBalanceSql.toString(), params2, AccArticleBalance.class);
        return specialBalanceList;
    }

    /**
     * 损益结转科目设置数据集合
     * @param accBookCode
     * @return
     */
    private List<ProfitLossCarryDownSubject> queryProfitLossCarryDownSubjectMessage(String accBookCode) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT p.profit_loss_code,p.account,p.rights_interests_code,p.create_by,p.create_time,p.last_modify_by,p.last_modify_time,s.subject_name AS temp FROM profitlosscarrydownsubject p LEFT JOIN subjectinfo s ON s.account = p.account AND CONCAT(s.all_subject,s.subject_code,'/') = p.rights_interests_code WHERE 1=1");
        int paramsNo3 = 1;
        Map<Integer, Object> params3 = new HashMap<>();
        sql.append(" AND p.account = ?" + paramsNo3);
        params3.put(paramsNo3, accBookCode);
        paramsNo3++;
        List<ProfitLossCarryDownSubject> PLCDSubjectList = (List<ProfitLossCarryDownSubject>) profitLossCarryDownSubjectRepository.queryBySql(sql.toString(), params3, ProfitLossCarryDownSubject.class);
        return PLCDSubjectList;
    }
}
