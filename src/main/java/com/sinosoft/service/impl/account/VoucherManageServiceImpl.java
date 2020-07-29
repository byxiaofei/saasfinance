package com.sinosoft.service.impl.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.account.*;
import com.sinosoft.domain.fixedassets.AccAssetInfo;
import com.sinosoft.domain.fixedassets.AccDepre;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfo;
import com.sinosoft.domain.intangibleassets.IntangibleAccDepre;
import com.sinosoft.dto.MenuInfoDTO;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.log.exception.ParameterIsNullException;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.repository.account.AccMainVoucherRespository;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.repository.account.AccSubVoucherRespository;
import com.sinosoft.repository.account.AccVoucherNoRespository;
import com.sinosoft.repository.fixedassets.AccAssetInfoRepository;
import com.sinosoft.repository.fixedassets.AccDepreRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetInfoRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccDepreRepository;
import com.sinosoft.service.account.VoucherManageService;
import com.sinosoft.util.ExcelUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoucherManageServiceImpl implements VoucherManageService {
    @Resource
    private VoucherRepository voucherRepository ;
    @Resource
    private SubjectRepository subjectRepository ;
    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;
    @Resource
    private AccMainVoucherRespository accMainVoucherRespository;
    @Resource
    private AccSubVoucherRespository accSubVoucherRespository;
    @Resource
    private AccAssetInfoRepository accAssetInfoRepository;
    @Resource
    private AccDepreRepository accDepreRepository;
    @Resource
    private IntangibleAccAssetInfoRepository intangibleAccAssetInfoRepository;
    @Resource
    private IntangibleAccDepreRepository intangibleAccDepreRepository;
    @Resource
    private AccVoucherNoRespository accVoucherNoRespository;

    @Override
    public List<?> getApproveListData(VoucherDTO dto) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        StringBuffer sql=new StringBuffer();
        sql.append("select a.voucher_date as voucherDate,a.voucher_no as voucherNo,a.year_month_date as yearMonthDate,a.generate_way as generateWay,a.voucher_type as voucherType,");
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
            /*sql.append("and (SELECT s.remark FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no limit 1) like '%"+dto.getRemarkName()+"%' ");*/
            params.put(paramsNo, "%"+dto.getRemarkName()+"%");
            paramsNo++;
        }
        if(dto.getVoucherNoStart()!=null&&!dto.getVoucherNoStart().equals("")){
            String voucherNo = "";
            if(dto.getVoucherNoStart().length()!=(10+centerCode.length())){
                int i = Integer.parseInt(dto.getVoucherNoStart());
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
                int i = Integer.parseInt(dto.getVoucherNoEnd());
                voucherNo = centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", i);
            }else{
                voucherNo = dto.getVoucherNoEnd();
            }
            sql.append(" and a.voucher_no <= ?" + paramsNo);
            params.put(paramsNo, voucherNo);
            paramsNo++;
        }
        if(dto.getMoneyStart()!=null&&!dto.getMoneyStart().equals("")){
            sql.append(" and ((SELECT sum(s.debit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) >= ?" + paramsNo + " or ");
            sql.append(" (SELECT sum(s.credit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) >= ?" + paramsNo + " )");
            params.put(paramsNo, dto.getMoneyStart());
            paramsNo++;
        }
        if(dto.getMoneyEnd()!=null&&!dto.getMoneyEnd().equals("")){
            sql.append(" and ((SELECT sum(s.debit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) <= ?" + paramsNo + " or ");
            sql.append(" (SELECT sum(s.credit_dest) FROM accsubvoucher s where s.center_code=a.center_code and s.branch_code=a.branch_code and s.acc_book_type=a.acc_book_type and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no GROUP BY s.voucher_no) <= ?" + paramsNo + " )");
            params.put(paramsNo, dto.getMoneyEnd());
            paramsNo++;
        }

        //联查历史表数据
        String hisSql = sql.toString();
        hisSql = hisSql.replaceAll("accsubvoucher","accsubvoucherhis");
        hisSql = hisSql.replaceAll("s\\.","sh\\.");
        hisSql = hisSql.replaceAll(" s "," sh ");
        hisSql = hisSql.replaceAll("AccMainVoucher","AccMainVoucherHis");
        hisSql = hisSql.replaceAll("a\\.","ah\\.");
        hisSql = hisSql.replaceAll(" a "," ah ");

        sql.append(" union all ");
        sql.append(hisSql);

        sql.append(" ORDER BY yearMonthDate,voucherNo ");

        return voucherRepository.queryBySqlSC(sql.toString(), params);
    }

    @Override
    @Transactional
    public String deleteVoucher(String voucherNo, String yearMonth){
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        //删除凭证时，删除的此条凭证及当月凭证号在其后的所有凭证均“未复核”，此条凭证才可被删除
        /*List<AccMainVoucher> mainList = accMainVoucherRespository.findAll(new CusSpecification<>().and(
                CusSpecification.Cnd.eq("id.accBookCode", CurrentUser.getCurrentLoginAccount()),
                CusSpecification.Cnd.eq("id.yearMonthDate", yearMonth),
                CusSpecification.Cnd.gt("id.voucherNo", voucherNo),
                CusSpecification.Cnd.ne("voucherFlag", "1")));*/
        if (!(voucherNo!=null && !"".equals(voucherNo) && !"null".equals(voucherNo)) || !(yearMonth!=null && !"".equals(yearMonth) && !"null".equals(yearMonth))) {
            throw new ParameterIsNullException("参数不能为空");
        }
        List<?> list = voucherRepository.checkAfterTheVoucherExistApprove(accBookCode, yearMonth, voucherNo, centerCode);
        if (list!=null&&list.size()>0) {
            return "exist";
        } else {
            List<AccMainVoucher> mainList = accMainVoucherRespository.findAll(new CusSpecification<>().and(
                    CusSpecification.Cnd.eq("id.centerCode", centerCode),
                    CusSpecification.Cnd.eq("id.branchCode", branchCode),
                    CusSpecification.Cnd.eq("id.accBookType", accBookType),
                    CusSpecification.Cnd.eq("id.accBookCode", accBookCode),
                    CusSpecification.Cnd.eq("id.yearMonthDate", yearMonth),
                    CusSpecification.Cnd.eq("id.voucherNo", voucherNo),
                    CusSpecification.Cnd.eq("voucherFlag", "1")));
            if (mainList!=null&&mainList.size()>0) {
                AccMainVoucher accMainVoucher = mainList.get(0);
                accMainVoucherRespository.delete(accMainVoucher);
                List<AccSubVoucher> subList = accSubVoucherRespository.findAll(new CusSpecification<>().and(
                        CusSpecification.Cnd.eq("id.centerCode", centerCode),
                        CusSpecification.Cnd.eq("id.branchCode", branchCode),
                        CusSpecification.Cnd.eq("id.accBookType", accBookType),
                        CusSpecification.Cnd.eq("id.accBookCode", accBookCode),
                        CusSpecification.Cnd.eq("id.yearMonthDate", yearMonth),
                        CusSpecification.Cnd.eq("id.voucherNo", voucherNo)));
                if (subList!=null&&subList.size()>0) {
                    for (AccSubVoucher accSubVoucher : subList) {
                        accSubVoucherRespository.delete(accSubVoucher);
                    }
                }
                accSubVoucherRespository.flush();

                voucherAnewSortBecauseOccupyOrDel(centerCode, branchCode, accBookType, accBookCode, yearMonth, voucherNo, "del");
                return "";
            } else {
                return "notFindVoucher";
            }
        }
    }

    /**
     * 凭证编号重新排序(由于占用已有凭证号或者删除凭证)
     *
     * 当占用已有凭证操作时必须先调用此方法再保存凭证信息；
     * 当删除操作时必须先执行删除操作再调用此方法。
     *
     * 是否需要基层单位、核算单位、账套类型？
     *
     * @param centerCode 核算单位
     * @param branchCode 基层单位
     * @param accBookType 账套类型
     * @param accBookCode 账套编码
     * @param yearMonth 会计期间
     * @param currentVoucherNo 当前凭证号 eg：180200005 or 18JS00005
     * @param type 操作类型（occupy：录入时占用已有凭证操作，del：删除操作）
     */
    @Override
    @Transactional
    public void voucherAnewSortBecauseOccupyOrDel (String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonth, String currentVoucherNo, String type){
        synchronized (this) {
            if (!(currentVoucherNo!=null && !"".equals(currentVoucherNo) && !"null".equals(currentVoucherNo)) || !(yearMonth!=null && !"".equals(yearMonth) && !"null".equals(yearMonth))) {
                throw new ParameterIsNullException("参数不能为空");
            }
            if (!(type!=null && !"".equals(type) && !"null".equals(type))) {
                throw new ParameterIsNullException("参数不能为空");
            }
            if (accBookCode==null || !"".equals(accBookCode)) {
                accBookCode = CurrentUser.getCurrentLoginAccount();
            }
            List<?> maxList = voucherRepository.getVoucherNoMax(accBookCode, yearMonth, centerCode);
            if (maxList!=null&&maxList.size()>0) {
                //已存在凭证最大号（凭证最大号表中表示的是下一张凭证的号，因此需要减1）
                Integer voucherNoMax = Integer.parseInt((String)((Map)maxList.get(0)).get("voucherNo"))-1;
                //当前操作的凭证号
                Integer operationVoucherNo = Integer.parseInt(currentVoucherNo.substring(centerCode.length()+4));
                //凭证号前缀
                String prefix = centerCode + yearMonth.substring(2,6);

                if ("del".equals(type)) {//删除操作
                    if (voucherNoMax == operationVoucherNo) {
                        //凭证最大号表数据自减1
//                        voucherRepository.voucherMaxNoDecrementOne(centerCode, accBookType, accBookCode, yearMonth, "1");
                        AccVoucherNoId id = new AccVoucherNoId();
                        id.setCenterCode(centerCode);
                        id.setAccBookType(accBookType);
                        id.setAccBookCode(accBookCode);
                        id.setYearMonthDate(yearMonth);
                        AccVoucherNo voucherNo = accVoucherNoRespository.findById(id).get();
                        if (voucherNo.getVoucherNo() != null && Integer.valueOf(voucherNo.getVoucherNo()) > 1){
                            voucherNo.setVoucherNo(String.valueOf(Integer.valueOf(voucherNo.getVoucherNo()) - 1));
                            accVoucherNoRespository.saveAndFlush(voucherNo);
                            accVoucherNoRespository.flush();
                        }
                    } else {
                        /*
                            1.查询自当前凭证号之后的凭证主表、子表的所有凭证信息;
                            2.然后删除主表、子表信息;
                            3.用新的凭证编号替换后再写入数据库
                            4.凭证最大号表数据自减1
                         */
                        List<AccMainVoucher> mainList = accMainVoucherRespository.qryAfterVoucherByYearMonthDateAndVoucherNo(centerCode, branchCode, accBookType, accBookCode, yearMonth, currentVoucherNo);
                        List<AccSubVoucher> subList = accSubVoucherRespository.qryAfterVoucherByYearMonthDateAndVoucherNo(centerCode, branchCode, accBookType, accBookCode, yearMonth, currentVoucherNo);

                        //删除主表、子表信息
                        for (AccMainVoucher mainVoucher: mainList) {
                            accMainVoucherRespository.delete(mainVoucher);
                        }
                        for (AccSubVoucher subVoucher: subList) {
                            accSubVoucherRespository.delete(subVoucher);
                        }

                        voucherRepository.flush();

                        //用新的凭证编号替换后再写入数据库
                        int newOperationVoucherNo = operationVoucherNo;
                        List voucherlists=new ArrayList<>();
                        for (AccMainVoucher mainVoucher: mainList) {
                            AccMainVoucherId id = mainVoucher.getId();
                            //原凭证号
                            String oldVoucherNo=id.getVoucherNo();
                            //新凭证号
                            String newVoucherNo = prefix+String.format("%06d", newOperationVoucherNo);
                            id.setVoucherNo(newVoucherNo);
                            mainVoucher.setId(id);
                            accMainVoucherRespository.save(mainVoucher);
                            accMainVoucherRespository.flush();
                            newOperationVoucherNo++;
                            ///////////////// 新凭证编号 newVoucherNo 原凭证号 oldVoucherNo
                            if(mainVoucher.getVoucherType().equals("3")||mainVoucher.getVoucherType().equals("4")){

                                Map<String,String> vouchermaps=new HashMap<>();
                                vouchermaps.put("newVoucherNo",newVoucherNo);
                                vouchermaps.put("oldVoucherNo",oldVoucherNo);
                                vouchermaps.put("vouchertype",mainVoucher.getVoucherType());
                                voucherlists.add(vouchermaps);

                            }
                        }
                        // 固定无形 凭证修改
                        fixedasetAndIntangVoucher(centerCode, branchCode, accBookType, accBookCode, voucherlists);
                        newOperationVoucherNo = operationVoucherNo;
                        //用于标记子表数据是否属于同一张凭证下的分录信息
                        String oldVoucherNo = "";
                        for (AccSubVoucher subVoucher: subList) {
                            AccSubVoucherId id = subVoucher.getId();
                            if (!"".equals(oldVoucherNo) && !oldVoucherNo.equals(id.getVoucherNo())) {
                                //非空串，说明不是第一条，且与当前凭证号不一致，说明不是同一张凭证分录数据
                                //此时才会自加1
                                newOperationVoucherNo++;
                            }
                            oldVoucherNo = id.getVoucherNo();
                            //新凭证号
                            String newVoucherNo = prefix+String.format("%06d", newOperationVoucherNo);

                            id.setVoucherNo(newVoucherNo);
                            subVoucher.setId(id);
                            accSubVoucherRespository.save(subVoucher);
                            accSubVoucherRespository.flush();
                        }
                        //凭证最大号表数据自减1
//                        voucherRepository.voucherMaxNoDecrementOne(centerCode, CurrentUser.getCurrentLoginAccountType(), accBookCode, yearMonth, "1");
                        AccVoucherNoId id = new AccVoucherNoId();
                        id.setCenterCode(centerCode);
                        id.setAccBookType(CurrentUser.getCurrentLoginAccountType());
                        id.setAccBookCode(accBookCode);
                        id.setYearMonthDate(yearMonth);
                        AccVoucherNo voucherNo = accVoucherNoRespository.findById(id).get();
                        if (voucherNo.getVoucherNo() != null && Integer.valueOf(voucherNo.getVoucherNo()) > 1){
                            voucherNo.setVoucherNo(String.valueOf(Integer.valueOf(voucherNo.getVoucherNo()) - 1));
                            accVoucherNoRespository.saveAndFlush(voucherNo);
                            accVoucherNoRespository.flush();
                        }
                    }
                } else if ("occupy".equals(type)) {//占用已有凭证操作
                    if (voucherNoMax >= operationVoucherNo) {
                        /*
                            1.查询自当前凭证号之后的凭证主表、子表的所有凭证信息（包含当前凭证号）;
                            2.然后删除主表、子表信息;
                            3.用新的凭证编号替换后再写入数据库
                        */
                        List<AccMainVoucher> mainList = accMainVoucherRespository.qrySelfAndAfterVoucherByYearMonthDateAndVoucherNo(centerCode, branchCode, accBookType, accBookCode, yearMonth, currentVoucherNo);
                        List<AccSubVoucher> subList = accSubVoucherRespository.qrySelfAndAfterVoucherByYearMonthDateAndVoucherNo(centerCode, branchCode, accBookType, accBookCode, yearMonth, currentVoucherNo);

                        //删除主表、子表信息
                        for (AccMainVoucher mainVoucher: mainList) {
                            accMainVoucherRespository.delete(mainVoucher);
                        }
                        for (AccSubVoucher subVoucher: subList) {
                            accSubVoucherRespository.delete(subVoucher);
                        }

                        voucherRepository.flush();

                        //用新的凭证编号替换后再写入数据库
                        int newOperationVoucherNo = operationVoucherNo;
                        List voucherlists=new ArrayList<>();
                        for (AccMainVoucher mainVoucher: mainList) {
                            newOperationVoucherNo++;
                            //新凭证号
                            String newVoucherNo = prefix+String.format("%06d", newOperationVoucherNo);
                            AccMainVoucherId id = mainVoucher.getId();
                            //原凭证号
                            String oldVoucherNo=id.getVoucherNo();
                            id.setVoucherNo(newVoucherNo);
                            mainVoucher.setId(id);
                            accMainVoucherRespository.save(mainVoucher);
                            accMainVoucherRespository.flush();
                            ///////////////// 新凭证编号 newVoucherNo 原凭证号 oldVoucherNo
                            if(mainVoucher.getVoucherType().equals("3")||mainVoucher.getVoucherType().equals("4")){
                                Map<String,String> vouchermaps=new HashMap<>();
                                vouchermaps.put("newVoucherNo",newVoucherNo);
                                vouchermaps.put("oldVoucherNo",oldVoucherNo);
                                vouchermaps.put("vouchertype",mainVoucher.getVoucherType());
                                voucherlists.add(vouchermaps);

                            }

                        }
                        // 固定无形 凭证修改
                        fixedasetAndIntangVoucher(centerCode, branchCode, accBookType, accBookCode, voucherlists);
                        newOperationVoucherNo = operationVoucherNo;
                        //用于标记子表数据是否属于同一张凭证下的分录信息
                        String oldVoucherNo = "";
                        for (AccSubVoucher subVoucher: subList) {
                            AccSubVoucherId id = subVoucher.getId();
                            if ("".equals(oldVoucherNo) || (!"".equals(oldVoucherNo) && !oldVoucherNo.equals(id.getVoucherNo()))) {
                                //oldVoucherNo为空串时，说明是第一条数据
                                //或者 oldVoucherNo非空串并且与当前凭证号不一致时，说明不是同一张凭证分录数据
                                //此时才会自增1
                                newOperationVoucherNo++;
                            }
                            oldVoucherNo = id.getVoucherNo();
                            //新凭证号
                            String newVoucherNo = prefix+String.format("%06d", newOperationVoucherNo);
                            id.setVoucherNo(newVoucherNo);
                            subVoucher.setId(id);
                            accSubVoucherRespository.save(subVoucher);
                            accSubVoucherRespository.flush();
                        }
                    } else {
                        System.out.println("占用凭证号超出范围无效：" + currentVoucherNo);
                    }
                } else {
                    System.out.println("参数type错误：" +type);
                }
            }
        }
    }

    /**
     * 占用或删除或重排凭证号后 固定 无形资产中凭证号也要修改
     * @param centerCode
     * @param branchCode
     * @param accBookType
     * @param accBookCode
     * @param voucherlists
     */
    @Override
    @Transactional
    public void fixedasetAndIntangVoucher(String centerCode, String branchCode, String accBookType, String accBookCode, List<Map<String,String>> voucherlists){
        //凭证类型 3 固定自转 4 无形资产自转
        synchronized (this) {
            List<AccAssetInfo> allaccAssetInfoList=new ArrayList<>();
            List<AccDepre> allaccDepreList=new ArrayList<>();
            List<IntangibleAccAssetInfo> allintangibleAccAssetInfoList=new ArrayList<>();
            List<IntangibleAccDepre> allintangibleAccDepreList=new ArrayList<>();
            for(int i=0;i<voucherlists.size();i++){
                String newVoucherNo=voucherlists.get(i).get("newVoucherNo");
                if(newVoucherNo==null){
                    newVoucherNo="";
                }
                String oldVoucherNo=voucherlists.get(i).get("oldVoucherNo");
                String vouchertype=voucherlists.get(i).get("vouchertype");
                if(vouchertype.equals("3")){
                    //固定资产自转
                    //查找所有固定资产基本信息表 替换凭证号
                    StringBuffer sqls = new StringBuffer("select * from accassetinfo where 1=1");

                    int paramsNo = 1;
                    Map<Integer, Object> params = new HashMap<>();

                    sqls.append(" and center_code = ?" + paramsNo);
                    params.put(paramsNo, centerCode);
                    paramsNo++;
                    sqls.append(" and branch_code = ?" + paramsNo);
                    params.put(paramsNo, branchCode);
                    paramsNo++;
                    sqls.append(" and acc_book_type = ?" + paramsNo);
                    params.put(paramsNo, accBookType);
                    paramsNo++;
                    sqls.append(" and acc_book_code = ?" + paramsNo);
                    params.put(paramsNo, accBookCode);
                    paramsNo++;
                    sqls.append(" and voucher_no = ?" + paramsNo);
                    params.put(paramsNo, oldVoucherNo);
                    paramsNo++;

                    List<AccAssetInfo>  accAssetInfoList=(List<AccAssetInfo>) accAssetInfoRepository.queryBySql(sqls.toString(), params, AccAssetInfo.class);
                    if(accAssetInfoList!=null&&accAssetInfoList.size()>0){
                        for(AccAssetInfo assetInfo:accAssetInfoList){
                            accAssetInfoRepository.delete(assetInfo);
                            accAssetInfoRepository.flush();
                            assetInfo.setVoucherNo(newVoucherNo);
                            allaccAssetInfoList.add(assetInfo);
                        }
                    }

                    //查找固定资产折旧表 替换凭证号
                    StringBuffer sqldepre = new StringBuffer("select * from accdepre where 1=1");

                    paramsNo = 1;
                    params = new HashMap<>();

                    sqldepre.append(" and center_code = ?" + paramsNo);
                    params.put(paramsNo, centerCode);
                    paramsNo++;
                    sqldepre.append(" and branch_code = ?" + paramsNo);
                    params.put(paramsNo, branchCode);
                    paramsNo++;
                    sqldepre.append(" and acc_book_type = ?" + paramsNo);
                    params.put(paramsNo, accBookType);
                    paramsNo++;
                    sqldepre.append(" and acc_book_code = ?" + paramsNo);
                    params.put(paramsNo, accBookCode);
                    paramsNo++;
                    sqldepre.append(" and voucher_no = ?" + paramsNo);
                    params.put(paramsNo, oldVoucherNo);
                    paramsNo++;

                    List<AccDepre>  accDepreList=(List<AccDepre>) accDepreRepository.queryBySql(sqldepre.toString(), params, AccDepre.class);
                    if(accDepreList!=null&&accDepreList.size()>0){
                        for(AccDepre accDepre:accDepreList){
                            accDepreRepository.delete(accDepre);
                            accDepreRepository.flush();
                            accDepre.setVoucherNo(newVoucherNo);
                            if(newVoucherNo==null||newVoucherNo==""){accDepre.setVoucherFlag("0");}
                            allaccDepreList.add(accDepre);
                        }
                    }

                }else{
                    //无形
                    //查找无形资产基本信息表 IntangibleAccAssetInfo
                    StringBuffer intangsqls = new StringBuffer("select * from IntangibleAccAssetInfo where 1=1");

                    int paramsNo = 1;
                    Map<Integer, Object> params = new HashMap<>();

                    intangsqls.append(" and center_code = ?" + paramsNo);
                    params.put(paramsNo, centerCode);
                    paramsNo++;
                    intangsqls.append(" and branch_code = ?" + paramsNo);
                    params.put(paramsNo, branchCode);
                    paramsNo++;
                    intangsqls.append(" and acc_book_type = ?" + paramsNo);
                    params.put(paramsNo, accBookType);
                    paramsNo++;
                    intangsqls.append(" and acc_book_code = ?" + paramsNo);
                    params.put(paramsNo, accBookCode);
                    paramsNo++;
                    intangsqls.append(" and voucher_no = ?" + paramsNo);
                    params.put(paramsNo, oldVoucherNo);
                    paramsNo++;

                    List<IntangibleAccAssetInfo>  intangibleAccAssetInfoList=(List<IntangibleAccAssetInfo>) intangibleAccAssetInfoRepository.queryBySql(intangsqls.toString(), params, IntangibleAccAssetInfo.class);
                    if(intangibleAccAssetInfoList!=null&&intangibleAccAssetInfoList.size()>0){
                        for(IntangibleAccAssetInfo intangibleAccAssetInfo:intangibleAccAssetInfoList){
                            intangibleAccAssetInfoRepository.delete(intangibleAccAssetInfo);
                            intangibleAccAssetInfoRepository.flush();
                            intangibleAccAssetInfo.setVoucherNo(newVoucherNo);
                            allintangibleAccAssetInfoList.add(intangibleAccAssetInfo);
                        }
                    }

                    //查找无形资产折旧表
                    StringBuffer sqlaccdepres=new StringBuffer("select * from IntangibleAccDepre where 1=1");

                    paramsNo = 1;
                    params = new HashMap<>();

                    sqlaccdepres.append(" and center_code = ?" + paramsNo);
                    params.put(paramsNo, centerCode);
                    paramsNo++;
                    sqlaccdepres.append(" and branch_code = ?" + paramsNo);
                    params.put(paramsNo, branchCode);
                    paramsNo++;
                    sqlaccdepres.append(" and acc_book_type = ?" + paramsNo);
                    params.put(paramsNo, accBookType);
                    paramsNo++;
                    sqlaccdepres.append(" and acc_book_code = ?" + paramsNo);
                    params.put(paramsNo, accBookCode);
                    paramsNo++;
                    sqlaccdepres.append(" and voucher_no = ?" + paramsNo);
                    params.put(paramsNo, oldVoucherNo);
                    paramsNo++;

                    List<IntangibleAccDepre>  intangibleAccDepreList=(List<IntangibleAccDepre>) intangibleAccDepreRepository.queryBySql(sqlaccdepres.toString(), params, IntangibleAccDepre.class);
                    if(intangibleAccDepreList!=null&&intangibleAccDepreList.size()>0){
                        for(IntangibleAccDepre intangibleAccDepre:intangibleAccDepreList){
                            intangibleAccDepreRepository.delete(intangibleAccDepre);
                            intangibleAccDepreRepository.flush();
                            intangibleAccDepre.setVoucherNo(newVoucherNo);
                            if(newVoucherNo==null||newVoucherNo==""){intangibleAccDepre.setVoucherFlag("0");}
                            allintangibleAccDepreList.add(intangibleAccDepre);
                        }
                    }
                }

            }
            if(allaccAssetInfoList!=null&&allaccAssetInfoList.size()>0){
                for(AccAssetInfo accAssetInfo:allaccAssetInfoList){
                    accAssetInfoRepository.save(accAssetInfo);
                    accAssetInfoRepository.flush();
                }
            }
            if(allaccDepreList!=null&&allaccDepreList.size()>0){
                for(AccDepre accDepre:allaccDepreList){
                    accDepreRepository.save(accDepre);
                    accDepreRepository.flush();
                }
            }
            if(allintangibleAccAssetInfoList!=null&&allintangibleAccAssetInfoList.size()>0){
                for(IntangibleAccAssetInfo intangibleAccAssetInfo:allintangibleAccAssetInfoList){
                    intangibleAccAssetInfoRepository.save(intangibleAccAssetInfo);
                    intangibleAccAssetInfoRepository.flush();
                }
            }
            if(allintangibleAccDepreList!=null&&allintangibleAccDepreList.size()>0){
                for(IntangibleAccDepre intangibleAccDepre:allintangibleAccDepreList){
                    intangibleAccDepreRepository.save(intangibleAccDepre);
                    intangibleAccDepreRepository.flush();
                }
            }
        }
    }

    @Override
    public List<?> qryVoucherCodeForCheck(String code,String direction) {
        List resultListAll=new ArrayList();
        List<Map<String, Object>> subjectTypeList = subjectRepository.findSubjectType();
        for (Object subjectTypeObject : subjectTypeList) {
            Map subjectTypeMap = new HashMap();
            subjectTypeMap.putAll((Map) subjectTypeObject);
            Integer subjectType = Integer.valueOf((String)subjectTypeMap.get("id"));
            List resultList=new ArrayList();
            List<Map<String, Object>> list = voucherRepository.findSuperCode(CurrentUser.getCurrentLoginAccount(),subjectType,code,direction);
            for (Object obj : list) {
                //Map map=(Map) obj;
                Map map = new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenForCheck((String)map.get("id"),code,direction);
                map.put("id",map.get("mid"));
                map.put("text",map.get("text"));
                if(list2!=null){
                    map.put("children",list2);
                }
                resultList.add(map);
            }
            if (resultList!=null&&resultList.size()>0) {
                subjectTypeMap.put("children",resultList);
                resultListAll.add(subjectTypeMap);
            }
        }
        return resultListAll;
    }

    private List<MenuInfoDTO> qryChildrenForCheck(String id, String code,String direction){
        List list1=new ArrayList();
        List<?> list = voucherRepository.findSuperCode1(id,CurrentUser.getCurrentLoginAccount(),code,direction);
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map = new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenForCheck((String)map.get("id"),code,direction);
                if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                    map.put("id",map.get("mid"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                    map.put("state","closed");
                    //map.put("checked", map.get("mid")!=null&&!map.get("mid").equals("")?true:false);
                }else{
                    map.put("id",map.get("mid"));
                    map.put("text",map.get("text"));
                    //map.put("checked", map.get("mid")!=null&&!map.get("mid").equals("")?true:false);
                }
                list1.add(map);
            }
        }
        return list1;
    }

    @Override
    @Transactional
    public InvokeResult rearrangementVoucher(String yearMonth) {
        /*
            当月所有凭证全部未复核的前提下，才可进行凭证的重新排序；
            排序规则：首先按凭证日期从前到后排序，按天进行排序。
            排序顺序为：进账凭证（带银行存款的科目，单张凭证借方发生额合计为正）、出账凭证（带银行存款的科目，单张凭证贷方发生额合计为正）、
                      其他系统自动生成的凭证（固定自转凭证、无形自转凭证、基金收缴系统接口凭证、费控系统接口凭证、决算凭证）、
                      其他手工录入的凭证（记账凭证）、
                      基金账套费用分摊凭证、
                      基金账套损益分摊凭证
         */
        synchronized (this) {
            String centerCode = CurrentUser.getCurrentLoginManageBranch();
            String branchCode = CurrentUser.getCurrentLoginManageBranch();
            String accBookType = CurrentUser.getCurrentLoginAccountType();
            String accBookCode = CurrentUser.getCurrentLoginAccount();
            //先查询会计期间，判断是否允许重排
            AccMonthTrace accMonthTrace = accMonthTraceRespository.findAccMonthTraceByYearMonthDate(centerCode, accBookType, accBookCode, yearMonth);
            if (accMonthTrace != null) {
                String accMonthStat = accMonthTrace.getAccMonthStat();
                if (accMonthStat!=null&&("3".equals(accMonthStat)||"5".equals(accMonthStat))) {
                    return InvokeResult.failure(yearMonth+" 会计期间已结转！");
                } else {
                    //当前重排会计期间内是否存在凭证数据
                    List<?> voucherList = voucherRepository.findMainVoucher(accBookCode, yearMonth, centerCode);
                    if (voucherList==null || voucherList.size()==0) {
                        return InvokeResult.failure(yearMonth+" 会计期间无凭证数据！");
                    } else {
                        //再判断会计期间内凭证是否存在已复核凭证（当月所有凭证全部未复核的前提下，才可进行凭证的重新排序）
                        List<?> voucherApproveList = voucherRepository.findMainVoucherAlreadyApprove(accBookCode, yearMonth, centerCode);
                        if (voucherApproveList!=null&&voucherApproveList.size()>0) {
                            return InvokeResult.failure(yearMonth+" 会计期间存在已复核或已记账的凭证！");
                        } else {
                            //对会计期间内的凭证进行重排
                            /*
                                1.先通过排序处理出排序后的凭证号，
                                2.通过凭证号顺序排序凭证主表、子表数据
                                3.删除凭证主表、子表数据
                                4.插入排序后凭证主表、子表的数据
                             */
                            StringBuffer sql = new StringBuffer();
                            sql.append("select c.code_code as codeCode,c.code_name as codeName from codemanage c where c.code_type = 'voucherType' order by c.order_by");
                            List<?> codemanageList = voucherRepository.queryBySqlSC(sql.toString());

                            List<String> voucherTypeFIELDList = new ArrayList<>();
                            for (int i=0;i<codemanageList.size();i++) {
                                String codeCode = ((Map) codemanageList.get(i)).get("codeCode").toString().trim();
                                voucherTypeFIELDList.add(i, codeCode);
                            }

                            sql.setLength(0);
                            sql.append("SELECT am.year_month_date AS yearMonthDate,am.voucher_date AS voucherDate,am.voucher_no AS voucherNo,(SELECT (SUM(a.debit_dest)-SUM(a.credit_dest)) FROM accsubvoucher a WHERE a.center_code=am.center_code AND a.branch_code=am.branch_code AND a.acc_book_type=am.acc_book_type AND a.acc_book_code = am.acc_book_code AND a.year_month_date = am.year_month_date AND a.voucher_no = am.voucher_no AND a.f01 = '1002') AS voucherBankBalance,am.generate_way AS generateWay,am.voucher_type AS voucherType FROM accmainvoucher am WHERE 1=1");

                            int paramsNo = 1;
                            Map<Integer, Object> params = new HashMap<>();

                            sql.append(" AND am.center_code = ?" + paramsNo);
                            params.put(paramsNo, centerCode);
                            paramsNo++;
                            sql.append(" AND am.branch_code = ?" + paramsNo);
                            params.put(paramsNo, branchCode);
                            paramsNo++;
                            sql.append(" AND am.acc_book_type = ?" + paramsNo);
                            params.put(paramsNo, accBookType);
                            paramsNo++;
                            sql.append(" AND am.acc_book_code = ?" + paramsNo);
                            params.put(paramsNo, accBookCode);
                            paramsNo++;
                            sql.append(" AND am.year_month_date = ?" + paramsNo);
                            params.put(paramsNo, yearMonth);
                            paramsNo++;

                            sql.append(" ORDER BY am.voucher_date,voucherBankBalance DESC");
                            if (voucherTypeFIELDList.size()>0) {
                                sql.append(",FIELD(am.voucher_type, ?" + paramsNo + " )");
                                params.put(paramsNo, voucherTypeFIELDList);
                                paramsNo++;
                            }

                            sql.append(",am.voucher_no");

                            List<?> voucherNoList = voucherRepository.queryBySqlSC(sql.toString(), params);

                            List<String> voucherNoFIELDList = new ArrayList<>();
                            for (int i=0;i<voucherNoList.size();i++) {
                                String voucherNo = ((Map) voucherNoList.get(i)).get("voucherNo").toString().trim();
                                voucherNoFIELDList.add(i, voucherNo);
                            }

                            //通过凭证号顺序排序凭证主表、子表数据
                            sql.setLength(0);
                            sql.append("select * from accmainvoucher a where 1=1");

                            paramsNo = 1;
                            params = new HashMap<>();

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
                            params.put(paramsNo, yearMonth);
                            paramsNo++;
                            sql.append(" order by field(a.voucher_no, ?" + paramsNo + " )");
                            params.put(paramsNo, voucherNoFIELDList);
                            paramsNo++;

                            String sql2 = sql.toString();
                            sql2 = sql2.replaceAll("accmainvoucher", "accsubvoucher");
                            sql2 = sql2 + ",a.suffix_no";
                            List<AccMainVoucher> accMainVoucherList = (List<AccMainVoucher>) voucherRepository.queryBySql(sql.toString(), params, AccMainVoucher.class);
                            List<AccSubVoucher> accSubVoucherList = (List<AccSubVoucher>) voucherRepository.queryBySql(sql2, params, AccSubVoucher.class);
                            //删除凭证主表、子表数据
                            for (AccMainVoucher mainVoucher:accMainVoucherList) {
                                accMainVoucherRespository.delete(mainVoucher);
                                accMainVoucherRespository.flush();
                            }
                            for (AccSubVoucher subVoucher:accSubVoucherList) {
                                accSubVoucherRespository.delete(subVoucher);
                                accSubVoucherRespository.flush();
                            }

                            //插入排序后凭证主表、子表的数据
                            int newVoucherNo = 0;
                            List<Map<String,String>> voucherlists=new ArrayList<>();
                            for (AccMainVoucher accMainVoucher:accMainVoucherList) {
                                AccMainVoucherId id = accMainVoucher.getId();
                                String currentVoucherNo = id.getVoucherNo();
                                newVoucherNo++;
                                id.setVoucherNo(centerCode+yearMonth.substring(2,6)+String.format("%06d", newVoucherNo));
                                accMainVoucher.setId(id);
                                accMainVoucherRespository.save(accMainVoucher);
                                accMainVoucherRespository.flush();

                                //预处理固定无形关联凭证数据
                                Map<String,String> vouchermaps=new HashMap<>();
                                vouchermaps.put("newVoucherNo", id.getVoucherNo());
                                vouchermaps.put("oldVoucherNo", currentVoucherNo);
                                vouchermaps.put("vouchertype", accMainVoucher.getVoucherType());
                                voucherlists.add(vouchermaps);
                            }

                            //处理固定无形关联凭证数据
                            fixedasetAndIntangVoucher(centerCode, branchCode, accBookType, accBookCode, voucherlists);
                            voucherRepository.flush();

                            newVoucherNo = 0;
                            String oldVoucherNo = "";
                            for (AccSubVoucher accSubVoucher:accSubVoucherList) {
                                AccSubVoucherId id = accSubVoucher.getId();
                                String currentVoucherNo = id.getVoucherNo();
                                if (!currentVoucherNo.equals(oldVoucherNo)) {
                                    newVoucherNo++;
                                }
                                oldVoucherNo = currentVoucherNo;
                                id.setVoucherNo(centerCode+yearMonth.substring(2,6)+String.format("%06d", newVoucherNo));
                                accSubVoucher.setId(id);
                                accSubVoucherRespository.save(accSubVoucher);
                                accSubVoucherRespository.flush();
                            }

                            return InvokeResult.success();
                        }
                    }
                }
            } else {
                return InvokeResult.failure(yearMonth+"会计期间不存在！");
            }
        }
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
