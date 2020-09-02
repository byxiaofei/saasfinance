package com.sinosoft.service.impl.account;

import com.sinosoft.common.Constant;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.account.VoucherPrintService;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoucherPrintServiceImpl implements VoucherPrintService {
    private Logger logger = LoggerFactory.getLogger(VoucherPrintServiceImpl.class);
    @Resource
    private VoucherRepository voucherRepository;
    @Resource
    private VoucherService voucherService;

    @Override
    public List<?> queryVoucherPrintList(VoucherDTO dto){
        List<Object> result = new ArrayList<Object>();//每一条代表需要打印的一个凭证号的凭证信息
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        //查询打印凭证的子表、主表综合信息
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ac.year_month_date AS yearMonthDate,ac.voucher_no voucherNo,am.voucher_date AS voucherDate,ac.suffix_no AS suffixNo,ac.remark AS remark,ac.direction_idx_name AS directionIdxName,ac.direction_other AS directionOther,CAST(IFNULL(ac.debit_dest,0.00) AS CHAR) AS debitDest,CAST(IFNULL(ac.credit_dest,0.00) AS CHAR) AS creditDest,");
        sb.append(" CAST(IFNULL((SELECT SUM(ac1.debit_dest) FROM accsubvoucher ac1 WHERE ac1.center_code = ac.center_code AND ac1.branch_code = ac.branch_code AND ac1.acc_book_type = ac.acc_book_type AND ac1.acc_book_code = ac.acc_book_code AND ac1.year_month_date = ac.year_month_date AND ac1.voucher_no = ac.voucher_no GROUP BY ac1.voucher_no),0.00) AS CHAR) AS debitSum,");
        sb.append(" CAST(IFNULL((SELECT SUM(ac2.credit_dest) FROM accsubvoucher ac2 WHERE ac2.center_code = ac.center_code AND ac2.branch_code = ac.branch_code AND ac2.acc_book_type = ac.acc_book_type AND ac2.acc_book_code = ac.acc_book_code AND ac2.year_month_date = ac.year_month_date AND ac2.voucher_no = ac.voucher_no GROUP BY ac2.voucher_no),0.00) AS CHAR) AS creditSum,");
        sb.append(" (SELECT u1.user_name FROM userinfo u1 WHERE u1.id = am.gene_by) AS geneByName,(SELECT u2.user_name FROM userinfo u2 WHERE u2.id = am.approve_by) AS approveByName,(SELECT u3.user_name FROM userinfo u3 WHERE u3.id = am.create_by) AS createByName");
        sb.append(" , am.center_code AS centerCode");
        sb.append(" FROM accsubvoucher ac LEFT JOIN accmainvoucher am ON am.center_code = ac.center_code AND am.branch_code = ac.branch_code AND am.acc_book_type = ac.acc_book_type AND am.acc_book_code = ac.acc_book_code AND am.year_month_date = ac.year_month_date AND am.voucher_no = ac.voucher_no WHERE 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sb.append(" AND ac.center_code = ?" + paramsNo);
        params.put(paramsNo, centerCode);
        paramsNo++;
        sb.append(" AND ac.branch_code = ?" + paramsNo);
        params.put(paramsNo, branchCode);
        paramsNo++;
        sb.append(" AND ac.acc_book_type = ?" + paramsNo);
        params.put(paramsNo, accBookType);
        paramsNo++;
        sb.append(" AND ac.acc_book_code = ?" + paramsNo);
        params.put(paramsNo, accBookCode);
        paramsNo++;

        if (dto.getCopyType()!=null && "4".equals(dto.getCopyType())) { //为4时是凭证批量打印（凭证打印界面）
            if (dto.getYearMonth()!=null&&!"".equals(dto.getYearMonth())) {
                sb.append(" AND ac.year_month_date >= ?" + paramsNo);
                params.put(paramsNo, dto.getYearMonth());
                paramsNo++;
            }
            if (dto.getYearMonthDate()!=null&&!"".equals(dto.getYearMonthDate())) {
                sb.append(" AND ac.year_month_date <= ?" + paramsNo);
                params.put(paramsNo, dto.getYearMonthDate());
                paramsNo++;
            }
            if (dto.getVoucherNoStart()!=null&&!"".equals(dto.getVoucherNoStart())) {
                sb.append(" AND ac.voucher_no >= ?" + paramsNo);
                params.put(paramsNo, centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(dto.getVoucherNoStart())));
                paramsNo++;
            }
            if (dto.getVoucherNoEnd()!=null&&!"".equals(dto.getVoucherNoEnd())) {
                sb.append(" AND ac.voucher_no <= ?" + paramsNo);
                params.put(paramsNo, centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(dto.getVoucherNoEnd())));
                paramsNo++;
            }
            //制单人
            if (dto.getCreateBy()!=null&&!"".equals(dto.getCreateBy())) {
                sb.append(" AND am.create_by IN(SELECT u.id FROM userinfo u WHERE u.user_name LIKE ?" + paramsNo + " )");
                params.put(paramsNo, "%"+dto.getCreateBy()+"%");
                paramsNo++;
            }
            //挑选凭证号
            if (dto.getVoucherNo()!=null&&!"".equals(dto.getVoucherNo())) {
                String[] nos = dto.getVoucherNo().split(",");
                List<String> voucherNoList = new ArrayList<>();
                for (int i=0;i<nos.length;i++) {
                    String no = nos[i];
                    if (no.length()!=(10+centerCode.length())) {
                        no = centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(no));
                    }
                    voucherNoList.add(i, no);
                }
                if (voucherNoList.size()>0) {
                    sb.append(" AND ac.voucher_no in ( ?" + paramsNo + " )");
                    params.put(paramsNo, voucherNoList);
                    paramsNo++;
                }
            }
            if(dto.getVoucherDateStart()!=null&&!dto.getVoucherDateStart().equals("")){
                sb.append(" and am.voucher_date >= ?" + paramsNo);
                params.put(paramsNo, dto.getVoucherDateStart());
                paramsNo++;
            }
            if(dto.getVoucherDateEnd()!=null&&!dto.getVoucherDateEnd().equals("")){
                sb.append(" and am.voucher_date <= ?" + paramsNo);
                params.put(paramsNo, dto.getVoucherDateEnd());
                paramsNo++;
            }
        } else {
            if (dto.getYearMonth()!=null&&!"".equals(dto.getYearMonth())) {
                sb.append(" AND ac.year_month_date = ?" + paramsNo);
                params.put(paramsNo, dto.getYearMonth());
                paramsNo++;
            }
            if (dto.getVoucherNo()!=null&&!"".equals(dto.getVoucherNo())) {
                sb.append(" AND ac.voucher_no = ?" + paramsNo);
                params.put(paramsNo, dto.getVoucherNo());
                paramsNo++;
            }
        }

        //联查历史表
        String sql = sb.toString();
        sql =sql.replaceAll("accsubvoucher","accsubvoucherhis");
        sql =sql.replaceAll("ac\\.","ach\\.");
        sql =sql.replaceAll(" ac "," ach ");
        sql =sql.replaceAll("ac1\\.","ach1\\.");
        sql =sql.replaceAll(" ac1 "," ach1 ");
        sql =sql.replaceAll("ac2\\.","ach2\\.");
        sql =sql.replaceAll(" ac2 "," ach2 ");
        sql =sql.replaceAll("accmainvoucher","accmainvoucherhis");
        sql =sql.replaceAll("am\\.","amh\\.");
        sql =sql.replaceAll(" am "," amh ");

        sb.append(" UNION ALL ");
        sb.append(sql);
        sb.append(" ORDER BY yearMonthDate,voucherNo,suffixNo");

        List<?> list = voucherRepository.queryBySqlSC(sb.toString(), params);
        if (list!=null && list.size()>0) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            List<Object> suffixList = new ArrayList<>();
            String vym = "";//会计期间
            String vno = "";//凭证号，用于判断是否为同一张凭证
            for (int i=0;i<list.size();i++) {
                Map map = (Map) list.get(i);
                String currentVYM = (String) map.get("yearMonthDate");
                String currentVNO = (String) map.get("voucherNo");

                if ("".equals(vym) && "".equals(vno)) {
                    //第一张凭证的第一条分录
                    vym = currentVYM;
                    vno = currentVNO;

                    //凭证日期，制单日期、凭证号、借方合计、贷方合计、记账人、复核人、制单人
                    setBasicMap(resultMap, map, currentVNO);

                    //设置摘要、科目名称(专项)、借方金额、贷方金额
                    Map<String, Object> newMap = new HashMap<String, Object>();
                    setSuffixMap(newMap, map, dto.getSpecialNameP());

                    suffixList.add(newMap);

                } else if ((!"".equals(vym)&&!"".equals(vno)) && !(vno.equals(currentVNO))){
                    vym = currentVYM;
                    vno = currentVNO;
                    //与上一条凭证号不同，非同一张凭证
                    //将上一个 resultMap 添加到 result 中
                    resultMap.put("suffix",suffixList);
                    result.add(resultMap);

                    resultMap = new HashMap<String, Object>();
                    suffixList = new ArrayList<>();

                    //凭证日期，制单日期、凭证号、借方合计、贷方合计、记账人、复核人、制单人
                    setBasicMap(resultMap, map, currentVNO);

                    //设置摘要、科目名称(专项)、借方金额、贷方金额
                    Map<String, Object> newMap = new HashMap<String, Object>();
                    setSuffixMap(newMap, map, dto.getSpecialNameP());

                    suffixList.add(newMap);

                } else {
                    vym = currentVYM;
                    vno = currentVNO;
                    //同一张凭证
                    Map<String, Object> newMap = new HashMap<String, Object>();
                    setSuffixMap(newMap, map, dto.getSpecialNameP());

                    suffixList.add(newMap);
                }
                if (i==list.size()-1) {
                    if (suffixList.size()>0) {
                        resultMap.put("suffix",suffixList);
                        result.add(resultMap);
                    }
                }
            }
        }
        return result;
    }


    private void setBasicMap(Map map1, Map map2, String currentVNO){
        map1.put("voucherDate",map2.get("voucherDate"));//凭证日期，制单日期
        map1.put("voucherNo",currentVNO);//凭证号
        String debitSum = (String) map2.get("debitSum");//借方合计
        String creditSum = (String) map2.get("creditSum");//贷方合计
        if (debitSum!=null && !"".equals(debitSum) && !"0.00".equals(debitSum)) {
            map1.put("debitSum",debitSum);
        } else {
            map1.put("debitSum","");
        }
        if (creditSum!=null && !"".equals(creditSum) && !"0.00".equals(creditSum)) {
            map1.put("creditSum",creditSum);
        } else {
            map1.put("creditSum","");
        }
        String geneByName = (String) map2.get("geneByName");
        String approveByName = (String) map2.get("approveByName");
        String createByName = (String) map2.get("createByName");
        map1.put("geneByName",(geneByName!=null)?geneByName:"");//记账人
        map1.put("approveByName",(approveByName!=null)?approveByName:"");//复核人
        map1.put("createByName",(createByName!=null)?createByName:"");//制单人
        /*map1.put("centerCode",(String) map2.get("centerCode"));*/
        /*map1.put("centerCode",CurrentUser.getCurrentAccountName());*/
        map1.put("centerCode","");
    }

    private void setSuffixMap(Map map1, Map map2, String specialNameP){

        map1.put("remark",map2.get("remark"));//设置摘要

        String directionOther = (String) map2.get("directionOther");
        String directionIdxName = (String) map2.get("directionIdxName");
        if (directionIdxName!=null && !"".equals(directionIdxName) && "/".equals(directionIdxName.substring(directionIdxName.length()-1))) {
            //去除最后一个“/”
            directionIdxName = directionIdxName.substring(0, directionIdxName.length()-1);
        }
        if (directionOther!=null && !"".equals(directionOther)) {
            String[] directionOthers = directionOther.split(",");
            String directionOtherAll = "";
            for (String str : directionOthers) {
                VoucherDTO dto = voucherService.getSpecialDateBySpecialCode(str);
                if (dto!=null && !"".equals(dto.getSpecialName())) {
                    if (specialNameP!=null&&"1".equals(specialNameP)) {
                        //专项全称全级显示
                        directionOtherAll += str + " " + dto.getSpecialNameP() + ",";
                    } else {
                        directionOtherAll += str + " " + dto.getSpecialName() + ",";
                    }
                }
            }
            if (!"".equals(directionOtherAll)) {
                directionIdxName += "("+ directionOtherAll.substring(0, directionOtherAll.length()-1) +")";
            }
        }
        map1.put("directionIdxName",directionIdxName);//科目名称(专项)
        map1.put("debitDest",map2.get("debitDest"));//借方金额
        map1.put("creditDest",map2.get("creditDest"));//贷方金额
        String debitDest = (String) map2.get("debitDest");//借方金额
        String creditDest = (String) map2.get("creditDest");//贷方金额
        if (debitDest!=null && !"".equals(debitDest) && !"0.00".equals(debitDest)) {
            map1.put("debitDest",debitDest);
        } else {
            map1.put("debitDest","");
        }
        if (creditDest!=null && !"".equals(creditDest) && !"0.00".equals(creditDest)) {
            map1.put("creditDest",creditDest);
        } else {
            map1.put("creditDest","");
        }
    }


    @Override
    public void voucherExport(HttpServletRequest request, HttpServletResponse response, VoucherDTO voucherDTO) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, branchCode);
        params.put(3, accBookType);
        params.put(4, accBookCode);
        params.put(5, voucherDTO.getVoucherNo());
        params.put(6, voucherDTO.getYearMonthDate());

        StringBuffer sql1 = new StringBuffer(" SELECT am.voucher_no AS 'voucherNo', am.year_month_date AS 'yearMonthDate'," +
                " am.aux_number AS 'auxNumber', am.voucher_date AS 'voucherDate', u1.user_name AS 'createBy', c.code_name AS 'voucherFlag'," +
                " u2.user_name AS 'approveBy', u3.user_name AS 'geneBy' FROM accmainvoucher am LEFT JOIN userinfo u1 ON u1.id = am.create_by " +
                " LEFT JOIN userinfo u2 ON u2.id = am.approve_by LEFT JOIN userinfo u3 ON u3.id = am.gene_by" +
                " LEFT JOIN codemanage c ON c.code_type = 'voucherFlag' AND c.code_code = am.voucher_flag WHERE am.center_code = ?1" +
                " AND am.branch_code = ?2 AND am.acc_book_type = ?3 AND am.acc_book_code = ?4" +
                " AND am.voucher_no = ?5 AND am.year_month_date = ?6"  +
                " union all  SELECT am.voucher_no AS 'voucherNo', am.year_month_date AS 'yearMonthDate'," +
                " am.aux_number AS 'auxNumber', am.voucher_date AS 'voucherDate', u1.user_name AS 'createBy', c.code_name AS 'voucherFlag'," +
                " u2.user_name AS 'approveBy', u3.user_name AS 'geneBy' FROM accmainvoucherhis am LEFT JOIN userinfo u1 ON u1.id = am.create_by " +
                " LEFT JOIN userinfo u2 ON u2.id = am.approve_by  LEFT JOIN userinfo u3 ON u3.id = am.gene_by" +
                " LEFT JOIN codemanage c ON c.code_type = 'voucherFlag' AND c.code_code = am.voucher_flag WHERE am.center_code = ?1" +
                " AND am.branch_code = ?2 AND am.acc_book_type = ?3 AND am.acc_book_code = ?4" +
                " AND am.voucher_no = ?5 AND am.year_month_date = ?6");

        List<?> list1 = voucherRepository.queryBySqlSC(sql1.toString(), params);

        StringBuffer sql2 = new StringBuffer(" SELECT ab.flag AS 'tagCode', ab.remark AS 'remark'," +
                " ab.direction_idx AS 'subjectCode', ab.direction_idx_name AS 'subjectName', ab.debit_dest AS 'debitDest'," +
                " ab.credit_dest AS 'creditDest' FROM accsubvoucher ab WHERE ab.center_code = ?1 AND ab.branch_code = ?2" +
                " AND ab.acc_book_type = ?3 AND ab.acc_book_code = ?4 AND ab.voucher_no = ?5" +
                " AND ab.year_month_date = ?6"  +
                " union all SELECT ab.flag AS 'tagCode', ab.remark AS 'remark'," +
                " ab.direction_idx AS 'subjectCode', ab.direction_idx_name AS 'subjectName', ab.debit_dest AS 'debitDest'," +
                " ab.credit_dest AS 'creditDest' FROM accsubvoucherhis ab WHERE ab.center_code = ?1 AND ab.branch_code = ?2" +
                " AND ab.acc_book_type = ?3 AND ab.acc_book_code = ?4 AND ab.voucher_no = ?5" +
                " AND ab.year_month_date = ?6");
        List<?> list2 = voucherRepository.queryBySqlSC(sql2.toString(), params);

        StringBuffer sql3 = new StringBuffer("SELECT ab.direction_idx AS 'subjectCode'," +
                " ab.direction_idx_name AS 'subjectName', ab.direction_other AS 'specialCode', ab.d01 AS 'unitPrice', ab.d02 AS 'num', " +
                " ( SELECT code_name FROM codemanage WHERE code_type = 'settlementType' AND code_code = ab.d03 ) AS 'settlementType'," +
                " ab.d04 AS 'settlementCode', ab.d05 AS 'settlementDate', ab.check_no AS 'checkNo', ab.invoice_no AS 'invoiceNo' FROM accsubvoucher ab WHERE ab.center_code = ?1" +
                " AND ab.branch_code = ?2 AND ab.acc_book_type = ?3 AND ab.acc_book_code = ?4" +
                " AND ab.voucher_no = ?5 AND ab.year_month_date = ?6"  +
                " union all SELECT ab.direction_idx AS 'subjectCode'," +
                " ab.direction_idx_name AS 'subjectName', ab.direction_other AS 'specialCode', ab.d01 AS 'unitPrice', ab.d02 AS 'num', " +
                " ( SELECT code_name FROM codemanage WHERE code_type = 'settlementType' AND code_code = ab.d03 ) AS 'settlementType'," +
                " ab.d04 AS 'settlementCode', ab.d05 AS 'settlementDate', ab.check_no AS 'checkNo', ab.invoice_no AS 'invoiceNo' FROM accsubvoucherhis ab WHERE ab.center_code = ?1" +
                " AND ab.branch_code = ?2 AND ab.acc_book_type = ?3 AND ab.acc_book_code = ?4" +
                " AND ab.voucher_no = ?5 AND ab.year_month_date = ?6");
        List<?> list3  = voucherRepository.queryBySqlSC(sql3.toString(), params) ;

        Map<String,Object> result = new HashMap<>();
        result.putAll((Map<String,Object>)list1.get(0));

        //查询专项简称
        for (int i=0;i<list3.size();i++){

            Map<String,Object> map = (Map<String,Object>) list3.get(i);

            String specialName = "";
            String specialMessage = "";

            String specialCode = (String) map.get("specialCode");
            if (specialCode != null && !"".equals(specialCode)) {
                if (specialCode.contains(",")){
                    //如果包含“，”
                    String []str = specialCode.split(",");
                    for (int j=0;j<str.length;j++){
                        specialName =voucherRepository.getSpecialName(accBookCode,str[j]);
                        specialMessage += (str[j] +" "+ specialName +" ");//专项信息
                    }
                }else {
                    specialName = voucherRepository.getSpecialName(accBookCode,specialCode);
                    specialMessage = specialCode+" "+specialName;
                }
            }else {
                specialMessage= "无关联专项";
            }

            String settlementType = (String) map.get("settlementType");
            String settlementCode = (String) map.get("settlementCode");
            String settlementDate = (String) map.get("settlementDate");
            if ( settlementCode != null && !"".equals(settlementCode) ){
                settlementType = " 结算类型：" + settlementType+ " ";
                settlementCode = " 结算单号：" + settlementCode+ " ";
                settlementDate = " 结算日期：" + settlementDate+ " ";

                specialMessage += (settlementType+settlementCode+settlementDate);

            }

            String unitPrice = (String) map.get("unitPrice");
            String num = (String) map.get("num");
            if (unitPrice != null && !"".equals(unitPrice) ){
                unitPrice = " 单价：" + unitPrice + " ";
                num = " 数量：" + num + " ";
                specialMessage +=(unitPrice+num);
            }

            String checkNo = (String) map.get("checkNo");
            if ( checkNo != null && !"".equals(checkNo) ){
                checkNo = " 支票号：" + checkNo + " ";

                specialMessage += checkNo;
            }

            String invoiceNo = (String) map.get("invoiceNo");
            if ( invoiceNo != null && !"".equals(invoiceNo)){
                invoiceNo = " 发票号：" + invoiceNo+ " ";

                specialMessage += invoiceNo;
            }

            ((Map<String, Object>) list3.get(i)).put("specialMessage",specialMessage);
        }

        Map data1 = new HashMap();
        data1.put("data1",list2);
        result.putAll(data1);

        Map data2 = new HashMap();
        data2.put("data2",list3);
        result.putAll(data2);


        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.exportVoucher(request,response,result, Constant.MODELPATH);
    }

    @Override
    public void voucherExportAboutDetails(HttpServletRequest request, HttpServletResponse response, List<VoucherDTO> voucherDTO) {

        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        // 把信息都存放好，去进行导出。
        List<Map<String,Object>> resultListAboutMaps = new ArrayList<>();

        // 取的是每一张凭证号和会计期间。
        for(VoucherDTO voucherDTOMsg : voucherDTO){

            Map<Integer, Object> params = new HashMap<>();
            params.put(1, centerCode);
            params.put(2, branchCode);
            params.put(3, accBookType);
            params.put(4, accBookCode);
            params.put(5, voucherDTOMsg.getVoucherNo());
            params.put(6, voucherDTOMsg.getYearMonthDate());

            StringBuffer sql1 = new StringBuffer(" SELECT am.voucher_no AS 'voucherNo', am.year_month_date AS 'yearMonthDate'," +
                    " am.aux_number AS 'auxNumber', am.voucher_date AS 'voucherDate', u1.user_name AS 'createBy', c.code_name AS 'voucherFlag'," +
                    " u2.user_name AS 'approveBy', u3.user_name AS 'geneBy' FROM accmainvoucher am LEFT JOIN userinfo u1 ON u1.id = am.create_by " +
                    " LEFT JOIN userinfo u2 ON u2.id = am.approve_by LEFT JOIN userinfo u3 ON u3.id = am.gene_by" +
                    " LEFT JOIN codemanage c ON c.code_type = 'voucherFlag' AND c.code_code = am.voucher_flag WHERE am.center_code = ?1" +
                    " AND am.branch_code = ?2 AND am.acc_book_type = ?3 AND am.acc_book_code = ?4" +
                    " AND am.voucher_no = ?5 AND am.year_month_date = ?6"  +
                    " union all  SELECT am.voucher_no AS 'voucherNo', am.year_month_date AS 'yearMonthDate'," +
                    " am.aux_number AS 'auxNumber', am.voucher_date AS 'voucherDate', u1.user_name AS 'createBy', c.code_name AS 'voucherFlag'," +
                    " u2.user_name AS 'approveBy', u3.user_name AS 'geneBy' FROM accmainvoucherhis am LEFT JOIN userinfo u1 ON u1.id = am.create_by " +
                    " LEFT JOIN userinfo u2 ON u2.id = am.approve_by  LEFT JOIN userinfo u3 ON u3.id = am.gene_by" +
                    " LEFT JOIN codemanage c ON c.code_type = 'voucherFlag' AND c.code_code = am.voucher_flag WHERE am.center_code = ?1" +
                    " AND am.branch_code = ?2 AND am.acc_book_type = ?3 AND am.acc_book_code = ?4" +
                    " AND am.voucher_no = ?5 AND am.year_month_date = ?6");

            List<?> list1 = voucherRepository.queryBySqlSC(sql1.toString(), params);

//            StringBuffer sql2 = new StringBuffer(" SELECT ab.flag AS 'tagCode', ab.remark AS 'remark'," +
//                    " ab.direction_idx AS 'subjectCode', ab.direction_idx_name AS 'subjectName', ab.debit_dest AS 'debitDest'," +
//                    " ab.credit_dest AS 'creditDest' FROM accsubvoucher ab WHERE ab.center_code = ?1 AND ab.branch_code = ?2" +
//                    " AND ab.acc_book_type = ?3 AND ab.acc_book_code = ?4 AND ab.voucher_no = ?5" +
//                    " AND ab.year_month_date = ?6"  +
//                    " union all SELECT ab.flag AS 'tagCode', ab.remark AS 'remark'," +
//                    " ab.direction_idx AS 'subjectCode', ab.direction_idx_name AS 'subjectName', ab.debit_dest AS 'debitDest'," +
//                    " ab.credit_dest AS 'creditDest' FROM accsubvoucherhis ab WHERE ab.center_code = ?1 AND ab.branch_code = ?2" +
//                    " AND ab.acc_book_type = ?3 AND ab.acc_book_code = ?4 AND ab.voucher_no = ?5" +
//                    " AND ab.year_month_date = ?6");
//            List<?> list2 = voucherRepository.queryBySqlSC(sql2.toString(), params);


            StringBuffer sql3 = new StringBuffer("SELECT ab.flag AS 'tagCode',ab.remark AS 'remark'," +
                    "  ab.debit_dest AS 'debitDest', ab.credit_dest AS 'creditDest' ," +
                    "  ab.direction_idx AS 'subjectCode', ab.direction_idx_name AS 'subjectName', ab.direction_other AS 'specialCode'," +
                    "  ab.d01 AS 'unitPrice',ab.d02 AS 'num'," +
                    "  (SELECT  code_name  FROM codemanage " +
                    "  WHERE code_type = 'settlementType' " +
                    "    AND code_code = ab.d03) AS 'settlementType'," +
                    "  ab.d04 AS 'settlementCode'," +
                    "  ab.d05 AS 'settlementDate'," +
                    "  ab.check_no AS 'checkNo'," +
                    "  ab.invoice_no AS 'invoiceNo' " +
                    "FROM" +
                    "  accsubvoucher ab " +
                    "WHERE ab.center_code = ?1 " +
                    "  AND ab.branch_code = ?2 " +
                    "  AND ab.acc_book_type = ?3 " +
                    "  AND ab.acc_book_code = ?4 " +
                    "  AND ab.voucher_no = ?5 " +
                    "  AND ab.year_month_date = ?6 " +
                    "UNION " +
                    "ALL " +
                    "SELECT " +
                    "  ab.flag AS 'tagCode'," +
                    "  ab.remark AS 'remark'," +
                    "  ab.debit_dest AS 'debitDest'," +
                    "  ab.credit_dest AS 'creditDest' ," +
                    "  ab.direction_idx AS 'subjectCode'," +
                    "  ab.direction_idx_name AS 'subjectName'," +
                    "  ab.direction_other AS 'specialCode'," +
                    "  ab.d01 AS 'unitPrice'," +
                    "  ab.d02 AS 'num'," +
                    "  (SELECT " +
                    "    code_name " +
                    "  FROM " +
                    "    codemanage " +
                    "  WHERE code_type = 'settlementType' " +
                    "    AND code_code = ab.d03) AS 'settlementType'," +
                    "  ab.d04 AS 'settlementCode'," +
                    "  ab.d05 AS 'settlementDate'," +
                    "  ab.check_no AS 'checkNo'," +
                    "  ab.invoice_no AS 'invoiceNo' " +
                    "FROM " +
                    "  accsubvoucherhis ab " +
                    "WHERE ab.center_code = ?1 " +
                    "  AND ab.branch_code = ?2 " +
                    "  AND ab.acc_book_type = ?3 " +
                    "  AND ab.acc_book_code = ?4 " +
                    "  AND ab.voucher_no = ?5 " +
                    "  AND ab.year_month_date = ?6 ");


            List<?> list3 = voucherRepository.queryBySqlSC(sql3.toString(),params);
            Map<String,Object> result = new HashMap<>();
            result.putAll((Map<String,Object>)list1.get(0));


            //查询专项简称
            for (int i=0;i<list3.size();i++){

                Map<String,Object> map = (Map<String,Object>) list3.get(i);

                String specialName = "";
                String specialMessage = "";

                String specialCode = (String) map.get("specialCode");
                if (specialCode != null && !"".equals(specialCode)) {
                    if (specialCode.contains(",")){
                        //如果包含“，”
                        String []str = specialCode.split(",");
                        for (int j=0;j<str.length;j++){
                            specialName =voucherRepository.getSpecialName(accBookCode,str[j]);
                            specialMessage += (" "+ specialName +" ");//专项信息
                        }
                    }else {
                        specialName = voucherRepository.getSpecialName(accBookCode,specialCode);
                        specialMessage = " "+specialName;
                    }
                }else {
                    specialMessage= "无关联专项";
                }

                String settlementType = (String) map.get("settlementType");
                String settlementCode = (String) map.get("settlementCode");
                String settlementDate = (String) map.get("settlementDate");
                if ( settlementCode != null && !"".equals(settlementCode) ){
                    settlementType = " 结算类型：" + settlementType+ " ";
                    settlementCode = " 结算单号：" + settlementCode+ " ";
                    settlementDate = " 结算日期：" + settlementDate+ " ";

                    specialMessage += (settlementType+settlementCode+settlementDate);

                }

                String unitPrice = (String) map.get("unitPrice");
                String num = (String) map.get("num");
                if (unitPrice != null && !"".equals(unitPrice) ){
                    unitPrice = " 单价：" + unitPrice + " ";
                    num = " 数量：" + num + " ";
                    specialMessage +=(unitPrice+num);
                }

                String checkNo = (String) map.get("checkNo");
                if ( checkNo != null && !"".equals(checkNo) ){
                    checkNo = " 支票号：" + checkNo + " ";

                    specialMessage += checkNo;
                }

                String invoiceNo = (String) map.get("invoiceNo");
                if ( invoiceNo != null && !"".equals(invoiceNo)){
                    invoiceNo = " 发票号：" + invoiceNo+ " ";

                    specialMessage += invoiceNo;
                }

                ((Map<String, Object>) list3.get(i)).put("specialMessage",specialMessage);
            }


            Map data2 = new HashMap();
            data2.put("data2",list3);
            result.putAll(data2);
            // 把上述存放好的结果result 放入到list集合中。为了批量写入到excel 表格中。
            resultListAboutMaps.add(result);
        }

        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.exportVoucherAboutDetails(request,response,resultListAboutMaps,Constant.MODELPATH);
    }
}
