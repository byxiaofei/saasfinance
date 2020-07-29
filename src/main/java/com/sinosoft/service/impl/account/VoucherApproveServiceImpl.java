package com.sinosoft.service.impl.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.account.AccMainVoucher;
import com.sinosoft.domain.account.AccMainVoucherId;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.service.account.VoucherApproveService;
import com.sinosoft.service.fixedassets.FixedassetsCardVoucherService;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VoucherApproveServiceImpl implements VoucherApproveService {
    private Logger logger = LoggerFactory.getLogger(VoucherApproveServiceImpl.class);
    @Resource
    private VoucherRepository voucherRepository ;
    @Resource
    private FixedassetsCardVoucherService fixedassetsCardVoucherService;

    @Override
    public List<?> getApproveListData(VoucherDTO dto) {
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
        sql.append("from AccMainVoucher a where 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

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

        if(dto.getVoucherType()!=null&&!dto.getVoucherType().equals("")){
            sql.append(" and a.voucher_type = ?" + paramsNo);
            params.put(paramsNo, dto.getVoucherType());
            paramsNo++;
        }
        if(dto.getYearMonth()!=null&&!dto.getYearMonth().equals("")){
            sql.append(" and a.year_month_date = ?" + paramsNo);
            params.put(paramsNo, dto.getYearMonth());
            paramsNo++;
        } else {
            return new ArrayList<>();
        }
        if(dto.getVoucherFlag()!=null&&!dto.getVoucherFlag().equals("")){
            sql.append(" and a.voucher_flag = ?" + paramsNo);
            params.put(paramsNo, dto.getVoucherFlag());
            paramsNo++;
        } else {
            sql.append(" and a.voucher_flag in ('1','2')");
        }
        if(dto.getCreateBy()!=null&&!dto.getCreateBy().equals("")){
            sql.append(" and a.create_by in (select u.id from userinfo u where u.user_name like ?" + paramsNo + " ) ");
            params.put(paramsNo, "%"+dto.getCreateBy()+"%");
            paramsNo++;
        }
        if(dto.getVoucherDateStart()!=null&&!dto.getVoucherDateStart().equals("")){
            sql.append(" and a.voucher_date >= ?" + paramsNo);
            params.put(paramsNo, dto.getVoucherDateStart());
            paramsNo++;
        }
        if(dto.getVoucherDateEnd()!=null&&!dto.getVoucherDateEnd().equals("")){
            sql.append(" and a.voucher_date <= ?" + paramsNo);
            params.put(paramsNo, dto.getVoucherDateEnd());
            paramsNo++;
        }
        if (dto.getItemCode1()!=null && !"".equals(dto.getItemCode1())) {
            sql.append(" and (select distinct s.voucher_no from accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no and s.direction_idx like ?" + paramsNo + " and s.debit_dest>'0.00') = a.voucher_no");
            params.put(paramsNo, "%"+dto.getItemCode1()+"%");
            paramsNo++;
        }
        if (dto.getItemCode2()!=null && !"".equals(dto.getItemCode2())) {
            sql.append(" and (select distinct s.voucher_no from accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no and s.direction_idx like ?" + paramsNo + " and s.credit_dest>'0.00') = a.voucher_no");
            params.put(paramsNo, "%"+dto.getItemCode2()+"%");
            paramsNo++;
        }
        if(dto.getRemarkName()!=null&&!dto.getRemarkName().equals("")){
            sql.append(" and (SELECT distinct s.voucher_no FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no and s.remark like ?" + paramsNo + " ) = a.voucher_no ");
            /*sql.append(" and (SELECT s.remark FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no limit 1) like '%"+dto.getRemarkName()+"%' ");*/
            params.put(paramsNo, "%"+dto.getRemarkName()+"%");
            paramsNo++;
        }
        if(dto.getVoucherNoStart()!=null&&!dto.getVoucherNoStart().equals("")){
            String voucherNo="";
            if(dto.getVoucherNoStart().length()!=(10+centerCode.length())){
                int i =  Integer.parseInt(dto.getVoucherNoStart());
                voucherNo = centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", i);
            }else{
                voucherNo = dto.getVoucherNoStart();
            }
            sql.append(" and a.voucher_no >= ?" + paramsNo);
            params.put(paramsNo, voucherNo);
            paramsNo++;
        }
        if(dto.getVoucherNoEnd()!=null&&!dto.getVoucherNoEnd().equals("")){
            String voucherNo = "";
            if(dto.getVoucherNoEnd().length()!=(10+centerCode.length())){
                int i =  Integer.parseInt(dto.getVoucherNoEnd());
                voucherNo = centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", i);
            }else{
                voucherNo = dto.getVoucherNoEnd();
            }
            sql.append(" and a.voucher_no <= ?" + paramsNo);
            params.put(paramsNo, voucherNo);
            paramsNo++;
        }
        if(dto.getMoneyStart()!=null&&!dto.getMoneyStart().equals("")){
            sql.append(" and ((SELECT sum(s.debit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) >= ?" + paramsNo + "' or ");
            sql.append(" (SELECT sum(s.credit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) >= ?" + paramsNo + " )");
            params.put(paramsNo, dto.getMoneyStart());
            paramsNo++;
        }
        if(dto.getMoneyEnd()!=null&&!dto.getMoneyEnd().equals("")){
            sql.append(" and ((SELECT sum(s.debit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) <= ?" + paramsNo + "' or ");
            sql.append(" (SELECT sum(s.credit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) <= ?" + paramsNo + " )");
            params.put(paramsNo, dto.getMoneyEnd());
            paramsNo++;
        }

        //日期 （开始 结束） 科目代码 对方科目代码 凭证号范围 凭证状态 制单人 发生金额（开始 结束 ） 摘要 专项名称全级显示
        sql.append(" ORDER BY a.year_month_date,a.voucher_no ");

        return voucherRepository.queryBySqlSC(sql.toString(), params);
    }

    @Override
    @Transactional
    public InvokeResult reviewPass(VoucherDTO dto) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String result = "";
        String[] voucherNo=dto.getVoucherNo().split(",");
        for(int i=0;i<voucherNo.length;i++){
            AccMainVoucherId amid=new AccMainVoucherId();
            amid.setCenterCode(centerCode);
            amid.setBranchCode(branchCode);
            amid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
            amid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
            amid.setYearMonthDate(dto.getYearMonthDate());//会计月度
            amid.setVoucherNo(voucherNo[i]);
            AccMainVoucher am =voucherRepository.findById(amid).get();
            //如果复核人与制单人是一个人则不允许进行复核操作
            if(am.getCreateBy().equals(CurrentUser.getCurrentUser().getId()+"")){
                if (!"".equals(result)){
                    result += "," + am.getId().getVoucherNo();
                } else {
                    result = am.getId().getVoucherNo();
                }
            } else {
                am.setVoucherFlag("2");//设置为已复核
                am.setApproveBy(CurrentUser.getCurrentUser().getId()+"");//设置审核人
                am.setApproveBranchCode(String.valueOf(CurrentUser.getCurrentUser().getComCode()));//设置审核人所属机构
                //获取当前系统时间，需要判断是否已经超过会计期间的最大日期如果超过了则设置为会计期间的最后一天
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String date = df.format(new Date());
                String currentDate = df.format(new Date());
                if ((currentDate.substring(0,4)+currentDate.substring(5,7)).equals(am.getId().getYearMonthDate())) {
                    am.setApproveDate(currentDate);
                } else {
                    int year = Integer.parseInt(am.getId().getYearMonthDate().substring(0,4));
                    int month = Integer.parseInt(am.getId().getYearMonthDate().substring(4));
                    am.setApproveDate(fixedassetsCardVoucherService.getLastDayOfYearMonth(year, month));
                }
                voucherRepository.save(am);
            }
        }
        if (!"".equals(result)) {
            if (result.split(",").length!=voucherNo.length) {
                return InvokeResult.success("凭证部分复核成功，复核失败凭证："+result+"，原因是：复核人与制单人不能是同一人！");
            } else {
                return InvokeResult.failure("凭证复核失败，原因是：复核人与制单人不能是同一人！");
            }
        }
        return InvokeResult.success("凭证复核成功！");
    }
    /**
     * @description: 两个String类型，按照日期格式对比
     * 				eg:
     *  				dateOne：2015-12-26
     *  				dateTwo：2015-12-26
     * 					dateFormatType: yyyy-MM-dd
     *  				返回类型：-1：dateOne小于dateTwo， 0：dateOne=dateTwo ，1：dateOne大于dateTwo
     * @param dateOne
     * @param dateTwo
     * @param dateFormatType：yyyy-MM-dd / yyyy-MM-dd HH:mm:ss /等
     * @return -1，0，1，100
     */
    public static int compareTime(String dateOne,String dateTwo,String dateFormatType){


        DateFormat df = new SimpleDateFormat(dateFormatType);
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();

        try {
            calendarStart.setTime(df.parse(dateOne));
            calendarEnd.setTime(df.parse(dateTwo));
        } catch (ParseException e) {
            e.printStackTrace();
            return 100;
        }
        int result = calendarStart.compareTo(calendarEnd);
        if(result > 0){
            result = 1;
        }else if(result < 0){
            result = -1;
        }else{
            result = 0 ;
        }
        return result ;
    }

    @Override
    public InvokeResult reviewBack(VoucherDTO dto) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String result = "";
        String[] voucherNo=dto.getVoucherNo().split(",");
        for(int i=0;i<voucherNo.length;i++){
            AccMainVoucherId amid=new AccMainVoucherId();
            amid.setCenterCode(centerCode);
            amid.setBranchCode(branchCode);
            amid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
            amid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
            amid.setYearMonthDate(dto.getYearMonthDate());
            amid.setVoucherNo(voucherNo[i]);
            AccMainVoucher am =voucherRepository.findById(amid).get();
            //如果撤销人与复核人不是一个人则不允许进行撤销操作
            if(!am.getApproveBy().equals(CurrentUser.getCurrentUser().getId()+"")){
                if (!"".equals(result)){
                    result += "," + am.getId().getVoucherNo();
                } else {
                    result = am.getId().getVoucherNo();
                }
            } else {
                am.setVoucherFlag("1");//设置未复核
                voucherRepository.save(am);
            }
        }
        if (!"".equals(result)) {
            if (result.split(",").length!=voucherNo.length) {
                return InvokeResult.success("凭证部分撤销成功，撤销失败凭证："+result+"，原因是：撤销人与复核人不一致！");
            } else {
                return InvokeResult.failure("凭证撤销失败，原因是：撤销人与复核人不一致！");
            }
        }
        return InvokeResult.success("凭证撤销成功！");
    }

    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols) {
        ExcelUtil excelUtil=new ExcelUtil();
        VoucherDTO dto = new VoucherDTO();
        try {
            dto = new ObjectMapper().readValue(queryConditions, VoucherDTO.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        List<?> dataList = getApproveListData(dto);
       // 4. 导出
        excelUtil.exportu(request, response, name, cols, dataList);
    }
}
