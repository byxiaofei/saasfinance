package com.sinosoft.service.impl.synthesize;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.AccSegmentDefine;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.AccSegmentDefineRespository;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.service.synthesize.CashFlowTableService;
import com.sinosoft.service.synthesize.DetailAccountService;
import com.sinosoft.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CashFlowTableServiceImpl implements CashFlowTableService {
    @Resource
    private VoucherRepository voucherRepository;
    @Resource
    private AccSegmentDefineRespository accSegmentDefineRespository;
    @Value("${MODELPath}")
    private String MODELPath ;
    @Resource
    DetailAccountService detailAccountService;

    /*
        导出操作，为避免重复查询，临时存储校验查询结果，此结果即为导出数据，否则不可用
        key = 用户ID + 下划线 + 当前机构 + 下划线 + 当前核算单位 + 下划线 + 当前登录账套类型 + 下划线 + 当前登录账套编码
     */
    private Map<String, Object> exportDataMap;

    /**
     * 现金流量明细表查询
     * @param dto
     * @return
     */
    @Override
    public List<?> queryCashFlowTable(VoucherDTO dto){
        List centerCode = detailAccountService.getSubBranch();
        List branchCode = centerCode;
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        List<AccSegmentDefine> asd = accSegmentDefineRespository.findByTemp("XJLL");
        String segmentFlag = null;
        String segmentColl = null;
        if (asd!=null && asd.size()>0) {
            segmentFlag = asd.get(0).getSegmentFlag();
            segmentColl = asd.get(0).getSegmentCol();
        } else {
            throw new RuntimeException("未找到现金流量专项的 s 段定义位置！");
        }

        StringBuffer sql = new StringBuffer("");
        sql.append("select t1.center_code as centerCode,t1.year_month_date as yearMonthDate,t1.voucher_date as voucherDate,t1.voucher_no as voucherNo,t2.suffix_no as suffixNo,t2.s06 as specialCode,t3.special_namep as specialName,t2.remark as remark,cast(t2.debit_dest as char) as debit,cast(t2.credit_dest as char) as credit " +
                "from accmainvoucher t1 " +
                "left join accsubvoucher t2 on t1.center_code = t2.center_code and t1.branch_code = t2.branch_code and t1.acc_book_type = t2.acc_book_type and t1.acc_book_code = t2.acc_book_code and t1.year_month_date = t2.year_month_date and t1.voucher_no = t2.voucher_no " +
                "left join specialinfo t3 on t3.special_code like '"+segmentColl+"%' and t2."+segmentFlag+" = t3.special_code and t1.acc_book_code = t3.account " +
                "where 1 = 1 ");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sql.append(" and t1.center_code in (?" + paramsNo +")");
        params.put(paramsNo, centerCode);
        paramsNo++;
        sql.append(" and t1.branch_code in (?" + paramsNo +")");
        params.put(paramsNo, branchCode);
        paramsNo++;
        sql.append(" and t1.acc_book_type = ?" + paramsNo);
        params.put(paramsNo, accBookType);
        paramsNo++;
        sql.append(" and t1.acc_book_code = ?" + paramsNo);//当前用户账套
        params.put(paramsNo, accBookCode);
        paramsNo++;

        if(dto.getYearMonth() != null && !"".equals(dto.getYearMonth())){
            sql.append(" and t1.year_month_date >= ?" + paramsNo);
            params.put(paramsNo, dto.getYearMonth());
            paramsNo++;
        }
        if(dto.getYearMonthDate() != null && !"".equals(dto.getYearMonthDate())){
            String endYearMonth = dto.getYearMonthDate();
            sql.append(" and t1.year_month_date <= ?" + paramsNo);
            params.put(paramsNo, endYearMonth);
            paramsNo++;
        }
        if(dto.getVoucherDateStart() != null && !"".equals(dto.getVoucherDateStart())){
            sql.append(" and t1.voucher_date >= ?" + paramsNo);
            params.put(paramsNo, dto.getVoucherDateStart());
            paramsNo++;
        }
        if(dto.getVoucherDateEnd() != null && !"".equals(dto.getVoucherDateEnd())){
            sql.append(" and t1.voucher_date <= ?" + paramsNo);
            params.put(paramsNo, dto.getVoucherDateEnd());
            paramsNo++;
        }
        if(dto.getVoucherGene() != null && !"".equals(dto.getVoucherGene())){//是否包含未记账凭证
            if("0".equals(dto.getVoucherGene())){//否
                sql.append(" and t1.voucher_flag in ('3') " );
            }else if("1".equals(dto.getVoucherGene())){//是
                sql.append(" and t1.voucher_flag in ('1','2','3') " );
            }
        }

        sql.append(" and (t2."+segmentFlag+"  is not null and t2."+segmentFlag+"  != '')");//现金流量专项

        //历史表查询sql
        String hisSql = sql.toString();
        hisSql = hisSql.replaceAll("accmainvoucher","accmainvoucherhis");
        hisSql = hisSql.replaceAll("accsubvoucher","accsubvoucherhis");
        sql.append(" union all " + hisSql);
        sql.append(" order by specialCode,voucherNo");

        List<?> list = voucherRepository.queryBySqlSC(sql.toString(), params);
        //保存返回结果的集合
        List result = new ArrayList<>();
        if(list != null && !list.isEmpty()){//查询结果不为空
            String specialCode = "";//专项代码
            BigDecimal sumDebit = new BigDecimal(0);//专项借方合计
            BigDecimal sumCredit = new BigDecimal(0);//专项贷方合计
            for(int i = 0; i < list.size(); i++){
                Map<String, String> map = (Map<String, String>) list.get(i);
                BigDecimal debitVal = new BigDecimal(map.get("debit")).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal creditVal = new BigDecimal(map.get("credit")).setScale(2, BigDecimal.ROUND_HALF_UP);
                if(i == 0){
                    specialCode = map.get("specialCode");
                    sumDebit = sumDebit.add(debitVal);
                    sumCredit = sumCredit.add(creditVal);
                    result.add(map);
                }else{
                    if(specialCode.equals(map.get("specialCode"))){//专项代码不变
                        sumDebit = sumDebit.add(debitVal);
                        sumCredit = sumCredit.add(creditVal);
                        result.add(map);
                    }else{//专项代码改变
                        //加入专项合计
                        Map<String, String> sumMap = new HashMap<>();
                        sumMap.put("remark", "合计");
                        sumMap.put("debit", sumDebit.toString());
                        sumMap.put("credit", sumCredit.toString());
                        result.add(sumMap);
                        //初始化专项合计
                        sumDebit = debitVal;
                        sumCredit = creditVal;
                        specialCode = map.get("specialCode");
                        result.add(map);
                    }
                }
                if(i == list.size() - 1) {//最后一条
                    //加入专项合计
                    Map<String, String> sumMap = new HashMap<>();
                    sumMap.put("remark", "合计");
                    sumMap.put("debit", sumDebit.toString());
                    sumMap.put("credit", sumCredit.toString());
                    result.add(sumMap);
                }
            }
        }
        return result;
    }

    @Override
    public String isHasData(VoucherDTO dto) {
        List<?> list = queryCashFlowTable(dto);
        if (list == null || list.size() == 0){
            return "NOTEXIST";
        }

        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String key = CurrentUser.getCurrentUser().getId()+"_"+centerCode+"_"+branchCode+"_"+accBookType+"_"+accBookCode;
        if (exportDataMap==null) {
            exportDataMap = new HashMap<String, Object>();
        }
        exportDataMap.put(key, list);

        return "EXIST";

    }

    @Override
    public void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO dto, String Date1, String Date2) {
        List<?> result;

        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String key = CurrentUser.getCurrentUser().getId()+"_"+centerCode+"_"+branchCode+"_"+accBookType+"_"+accBookCode;
        if (exportDataMap!=null && exportDataMap.get(key)!=null) {
            result = (List) exportDataMap.get(key);
            exportDataMap.put(key, null); // 使用之后便清除，减少内存占用
        } else {
            result = queryCashFlowTable(dto);
        }

        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.exportCashFlow(request,response,result,Date1,Date2, MODELPath);
    }
}
