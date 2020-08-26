package com.sinosoft.httpclient.service.impl;

import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.httpclient.domain.ConfigureManage;
import com.sinosoft.httpclient.dto.PartsVerificationDTO;
import com.sinosoft.httpclient.dto.ServiceInvoiceDTO;
import com.sinosoft.httpclient.repository.ConfigureManageRespository;
import com.sinosoft.httpclient.service.AccountingMethod;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.service.VoucherService;
import com.sinosoft.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class AccountingMethodServiceImpl implements AccountingMethod {
    private Logger logger = LoggerFactory.getLogger(AccountingMethodServiceImpl.class);

    @Resource
    private BranchInfoRepository branchInfoRepository;

    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;

    @Resource
    private VoucherService voucherService;

    @Resource
    private SubjectRepository subjectRepository;

    @Resource
    private ConfigureManageRespository configureManageRespository;

    @Resource
    private VehicleInvoiceServiceImpl vehicleInvoiceService;

    @Override
    public Map  InformationVerification(String companyNo, String PreparationDate, StringBuilder errorMsg) {
        Map resultMap = new HashMap<>();
        VoucherDTO dto = new VoucherDTO();
        List<Map<String, Object>> branchMsg = branchInfoRepository.checkExistsComCode(companyNo);
        if(branchMsg.size() <= 0){
            errorMsg.append("机构单位编码信息匹配不正确检查映射关系");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        if(branchMsg.size() > 0){
            Map<String, Object> stringObjectMap = branchMsg.get(0);
            String flagMsg  = (String) stringObjectMap.get("flag");
            if("0".equals(flagMsg)){
                errorMsg.append(companyNo+"机构已经停用");
                resultMap.put("resultMsg",errorMsg.toString());
                return resultMap;
            }
        }
        String yearMonthDate = DateUtil.getDateTimeFormatToGeneralLedger(PreparationDate);
        if(!"success".equals(yearMonthDate)){
            errorMsg.append("制单日期的格式为：yyyy-MM-dd，请重新校验");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        String operationDateReplace = PreparationDate.replaceAll("-", "");
        // 为会计期间
        String yearMonth = operationDateReplace.substring(0, 6);
        // 需要通过机构找到账套信息，账套类型。来找到对应的凭证号。
        String accbookCode = "";
        String accbookType = "";
        Map<Integer,Object> params = new HashMap<>();
        int paramNo = 1;
        StringBuffer sql1 = new StringBuffer();
        sql1.append("select acc.account_type as accountType , acc.account_code as accountCode   from accountinfo acc inner join branchaccount ba join branchinfo b  on acc.id = ba.account_id  and b.id = ba.branch_id where 1=1 and b.com_code = ?"+paramNo);
        params.put(paramNo,companyNo);
        paramNo++;
        List<?> checkMsg = branchInfoRepository.queryBySqlSC(sql1.toString(), params);
        if(checkMsg.size() <= 0){
            errorMsg.append(companyNo+"机构并无关联账套");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }else{
            // 这里因为一个机构指能对应一个账套，所以这里只会查询出1条数据
            for(Object o : checkMsg){
                Map maps = new HashMap();
                maps.putAll((Map) o);
                accbookType = maps.get("accountType").toString();
                accbookCode = maps.get("accountCode").toString();
            }
        }

        // 这里看是否需要进行会计月度的追加。
        // 根据机构和账套查询当前的最大会计月度，并选择到当前的日期，是否与当
        String monthTrace = vehicleInvoiceService.recursiveCalls(companyNo, accbookType, accbookCode, yearMonth);
        if(!"final".equals(monthTrace)){
            if("fail".equals(monthTrace)){
                errorMsg.append("不存在当前会计期间");
                resultMap.put("resultMsg",errorMsg.toString());
                return resultMap;
            }else{
                errorMsg.append("当前对会计期间的开启存在异常");
                resultMap.put("resultMsg",errorMsg.toString());
                return resultMap;
            }
        }


        // 如果没问题，校验的同时就生成了凭证号了。 这里把createBy 创建人 设置为001 默认系统了
        VoucherDTO voucherDTO = voucherService.setVoucher1(yearMonth, companyNo, companyNo, accbookCode, accbookType,"001");
        if(voucherDTO.getYearMonth() == null || "".equals(voucherDTO.getYearMonth())){
            errorMsg.append("当前账套信息下没有对应的凭证月");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        // 传过来的年月，需要判断当前月是否已经结转。
        List<?> objects = accMonthTraceRespository.queryAccMonthTraceByChooseMessage(companyNo, yearMonth, accbookType, accbookCode);
        if(objects.size() > 0){
            errorMsg.append("当前月已经进行结转不能再新增凭证");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        // branchCode 与centerCode 相同
        resultMap.put("centerCode",companyNo);
        resultMap.put("branchCode",companyNo);
        resultMap.put("accbookCode",accbookCode);
        resultMap.put("accbookType",accbookType);
        resultMap.put("yearMonth",yearMonth);
        resultMap.put("PreparationDate",PreparationDate);
        resultMap.put("voucherDTO",voucherDTO);
        return resultMap;
    }

    @Override
    public Map<String, Object> AccountingEntryInformation(Map<String,Object> map,String interfaceInfo, String interfaceType, StringBuilder errorMsg, String accbookCode , String branchCode) {
        Map<String,Object> resultMap = new HashMap<>();
        List<VoucherDTO> list2 = new ArrayList<>();
        List<VoucherDTO> list3 = new ArrayList<>();
        PartsVerificationDTO  partsVerification;
        ServiceInvoiceDTO serviceInvoiceDTO;
        // 这里给1/2 来判断生成那个类型的数据凭证信息。
        // 开始科目代码和专项信息存放整理，方便后续直接保存入库。
        // 之前是通过科目代码找专项一级，在通过专项一级找对应的字段，来拿到对接文档中的数据，并拿到数据再去数据库中比对信息是否存在。
        List<ConfigureManage> configureManages = configureManageRespository.queryConfigureManagesByInterfaceInfoAndInterfaceTypeAndBranchCode(interfaceInfo, interfaceType,branchCode);
        // 这里科目信息开始已经有顺序了。直接按照顺序给值即可。 （即为：分录的形式）
        for (int i = 0; i < configureManages.size(); i++) {
            // 当前这里意为：entry的分录信息一样
            VoucherDTO voucherDTO1 = new VoucherDTO();
            VoucherDTO voucherDTO2 = new VoucherDTO();
            // 对科目的校验
            String subjectName = configureManages.get(i).getSubjectName();
            String subjectInfo = configureManages.get(i).getId().getSubjectCode();
            String resultCode = checkSubjectCodePassMusterBySubjectCodeAll(subjectInfo, accbookCode);
            if (resultCode != null && !"".equals(resultCode)) {
                if ("notExist".equals(resultCode)) {
                    errorMsg.append(subjectInfo + "不存在，请重新输入！");
                    resultMap.put("resultMsg", errorMsg.toString());
                    return resultMap;
                }
                if ("notEnd".equals(resultCode)) {
                    errorMsg.append(subjectInfo + "不是末级科目，请重新输入！");
                    resultMap.put("resultMsg", errorMsg.toString());
                    return resultMap;
                }
                if ("notUse".equals(resultCode)) {
                    errorMsg.append(subjectInfo + "已停用，请重新输入！");
                    resultMap.put("resultMsg", errorMsg.toString());
                    return resultMap;
                }
            }
            // 当前配置表中的专项字段为专项信息的末级代码，并非一级。
            // 之前由科目代码找到挂接的一级专项，再由一级专项去找s段，并在s段取出专项末级信息。
            // 当前直接用配置好的专项信息，校验是否启用即可， 不校验配置的专项信息是否符合科目挂接的一级专项。
            String specialInfo = configureManages.get(i).getSpecialCode();
            if (specialInfo != null && !"".equals(specialInfo)) {
                String[] specialInfos = specialInfo.split(",");
                for (int j = 0; j < specialInfos.length; j++) {
                    String specialJudgeInfo = voucherService.checkSpecialCodePassMusterBySpecialCode(specialInfos[j], accbookCode);
                    if (specialJudgeInfo != null && !"".equals(specialJudgeInfo)) {
                        if ("notExist".equals(specialJudgeInfo)) {
                            errorMsg.append("专项：" + specialInfos[j] + " 不存在，请重新输入！");
                            resultMap.put("resultMsg", errorMsg.toString());
                            return resultMap;
                        }
                        if ("notEnd".equals(specialJudgeInfo)) {
                            errorMsg.append(specialInfos[j] + "不是末级专项，请重新输入！");
                            resultMap.put("resultMsg", errorMsg.toString());
                            return resultMap;
                        }
                        if ("notUse".equals(specialJudgeInfo)) {
                            errorMsg.append(specialInfos[j] + "专项已停用，请重新输入！");
                            resultMap.put("resultMsg", errorMsg.toString());
                            return resultMap;
                        }
                    }
                }
            }
            //第四个接口分录信息生成
            if ("4".equals(interfaceInfo)) {
                partsVerification = (PartsVerificationDTO) map.get("accountingEntry");
                //第四个接口只有一个凭证模版
                if (i + 1 == 1) {
                    voucherDTO1.setDebit(partsVerification.getAcceptTotalAmount().toString());
                    voucherDTO1.setCredit("0.00");
                } else if (i + 1 == 2) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(partsVerification.getVariance().toString());
                } else if (i + 1 == 3) {
                    voucherDTO1.setDebit(partsVerification.getPurchaseInvoiceNet().toString());
                    voucherDTO1.setCredit("0.00");
                }
                //摘要信息是校验编号
                voucherDTO1.setRemarkName(partsVerification.getVerifyNo());
            }
            //第10个接口分录信息生成
            if ("10".equals(interfaceInfo)) {
                serviceInvoiceDTO = (ServiceInvoiceDTO) map.get("accountingEntry");

                //售后结账分录
                if ("I".equals(interfaceType)) {
                    if (i + 1 == 1) {
                        //主营收入-工时-MB-机修-零售
                        voucherDTO1.setDebit(serviceInvoiceDTO.getLaborTotalPrice().toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i + 1 == 2) {
                        BigDecimal D = serviceInvoiceDTO.getSumD();
                        BigDecimal C = serviceInvoiceDTO.getSumC();
                        BigDecimal sum = D.subtract(C);
                        //应收账款-集团外-省内
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(sum.toString());
                    } else if (i + 1 == 3) {
                        //主营收入-MB-工时折扣-机修
                        voucherDTO1.setDebit(serviceInvoiceDTO.getLaborDiscountAmount().toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i + 1 == 4) {
                        //主营成本-配件-MB-维修-零售
                        voucherDTO1.setDebit(serviceInvoiceDTO.getTotalpartsUnitCost().toString());
                        voucherDTO1.setCredit("0.00");

                    } else if (i + 1 == 5) {
                        //库存商品-在用配件-配件发出
                        voucherDTO1.setDebit(serviceInvoiceDTO.getStockPrice().toString());
                        voucherDTO1.setCredit("0.00");

                    } else if (i + 1 == 6) {
                        //主营收入-配件-MB-维修-零售
                        voucherDTO1.setDebit(serviceInvoiceDTO.getTotalPrice().toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i + 1 == 7) {
                        voucherDTO1.setDebit(serviceInvoiceDTO.getTotalDiscountAmount().toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i + 1 == 8) {
                        // 应交税金-增值税-销项税额
                        voucherDTO1.setDebit(serviceInvoiceDTO.getTotalVatAmount().toString());
                        voucherDTO1.setCredit("0.00");
                    }

                    //摘要信息是校验编号
                    voucherDTO1.setRemarkName(serviceInvoiceDTO.getOrderNumber());

                } else if ("2".equals(interfaceType)) {

                    if ("I".equals(interfaceType)) {
                        if (i + 1 == 1) {
                            //主营收入-工时-MB-机修-零售
                            voucherDTO1.setDebit(serviceInvoiceDTO.getLaborTotalPrice().toString());
                            voucherDTO1.setCredit("0.00");
                        } else if (i + 1 == 2) {
                            BigDecimal D = serviceInvoiceDTO.getSumD();
                            BigDecimal C = serviceInvoiceDTO.getSumC();
                            BigDecimal sum = D.subtract(C);
                            //应收账款-集团外-省内
                            voucherDTO1.setDebit("0.00");
                            voucherDTO1.setCredit(sum.toString());
                        } else if (i + 1 == 3) {
                            //主营收入-MB-工时折扣-机修
                            voucherDTO1.setDebit(serviceInvoiceDTO.getLaborDiscountAmount().toString());
                            voucherDTO1.setCredit("0.00");
                        } else if (i + 1 == 4) {
                            //主营成本-配件-MB-维修-零售
                            voucherDTO1.setDebit(serviceInvoiceDTO.getTotalpartsUnitCost().toString());
                            voucherDTO1.setCredit("0.00");

                        } else if (i + 1 == 5) {
                            //库存商品-在用配件-配件发出
                            voucherDTO1.setDebit(serviceInvoiceDTO.getStockPrice().toString());
                            voucherDTO1.setCredit("0.00");

                        } else if (i + 1 == 6) {
                            //主营收入-配件-MB-维修-零售
                            voucherDTO1.setDebit(serviceInvoiceDTO.getTotalPrice().toString());
                            voucherDTO1.setCredit("0.00");
                        } else if (i + 1 == 7) {
                            // 应交税金-增值税-销项税额
                            voucherDTO1.setDebit(serviceInvoiceDTO.getTotalVatAmount().toString());
                            voucherDTO1.setCredit("0.00");
                        }

                        //摘要信息是校验编号
                        voucherDTO1.setRemarkName(serviceInvoiceDTO.getOrderNumber());


                    } else if ("3".equals(interfaceType)) {
                        //售后退账分录

                        if (i + 1 == 1) {
                            //主营收入-工时-MB-机修-零售
                            voucherDTO1.setDebit(serviceInvoiceDTO.getLaborTotalPrice().toString());
                            voucherDTO1.setCredit("0.00");
                        } else if (i + 1 == 2) {
                            BigDecimal D = serviceInvoiceDTO.getSumD();
                            BigDecimal C = serviceInvoiceDTO.getSumC();
                            BigDecimal sum = D.subtract(C);
                            //应收账款-集团外-省内
                            voucherDTO1.setDebit("0.00");
                            voucherDTO1.setCredit(sum.toString());
                        } else if (i + 1 == 3) {
                            //主营收入-MB-工时折扣-机修
                            voucherDTO1.setDebit(serviceInvoiceDTO.getLaborDiscountAmount().toString());
                            voucherDTO1.setCredit("0.00");
                        } else if (i + 1 == 4) {
                            //主营成本-配件-MB-维修-零售
                            voucherDTO1.setDebit(serviceInvoiceDTO.getTotalpartsUnitCost().toString());
                            voucherDTO1.setCredit("0.00");

                        } else if (i + 1 == 5) {
                            //库存商品-在用配件-配件发出
                            voucherDTO1.setDebit(serviceInvoiceDTO.getStockPrice().toString());
                            voucherDTO1.setCredit("0.00");

                        } else if (i + 1 == 6) {
                            //主营收入-配件-MB-维修-零售
                            voucherDTO1.setDebit(serviceInvoiceDTO.getTotalPrice().toString());
                            voucherDTO1.setCredit("0.00");
                        } else if (i + 1 == 7) {
                            voucherDTO1.setDebit(serviceInvoiceDTO.getTotalDiscountAmount().toString());
                            voucherDTO1.setCredit("0.00");
                        } else if (i + 1 == 8) {
                            // 应交税金-增值税-销项税额
                            voucherDTO1.setDebit(serviceInvoiceDTO.getTotalVatAmount().toString());
                            voucherDTO1.setCredit("0.00");
                        }
                        //摘要信息是校验编号
                        voucherDTO1.setRemarkName(serviceInvoiceDTO.getOrderNumber());

                    }

                }
                voucherDTO1.setSubjectCode(subjectInfo);
                voucherDTO1.setSubjectName(subjectName);

                voucherDTO2.setSubjectCodeS(subjectInfo);
                voucherDTO2.setSubjectNameS(subjectName);

                // 一级专项集合。专项信息配置一定注意顺序问题。
                String specialSuperCodes = configureManages.get(i).getSpecialSuperCode().trim();
                String specialCode = configureManages.get(i).getSpecialCode();
                voucherDTO2.setSpecialSuperCodeS(specialSuperCodes);
                // 当前 专项信息配置一定注意顺序问题末级、一级一致。
                voucherDTO2.setSpecialCodeS(specialCode);
                list2.add(voucherDTO1);
                list3.add(voucherDTO2);
            }
        }
        // 以上已经对一条凭证处理校验完毕
        resultMap.put("list2", list2);
        resultMap.put("list3", list3);
        resultMap.put("resultMsg", "success");
        // 返回后开始进行录入凭证
        return resultMap;
    }


    public  String checkSubjectCodePassMusterBySubjectCodeAll(String subjectCodeAll,String account){
        if ("/".equals(subjectCodeAll.substring(subjectCodeAll.length()-1))) {
            //最后一个字符是“/”，去掉
            subjectCodeAll = subjectCodeAll.substring(0,subjectCodeAll.length()-1);
        }
        StringBuffer sql=new StringBuffer("select * from subjectinfo s  where s.account = ?1 and concat_ws(\"\",s.all_subject,s.subject_code) = ?2 ");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, account);
        params.put(2, subjectCodeAll);

        List<SubjectInfo> list = (List<SubjectInfo>)subjectRepository.queryBySql(sql.toString(), params, SubjectInfo.class);
        if(list==null||list.size()==0){
            return "notExist";
        }else{
            if("1".equals(list.get(0).getEndFlag())){//0表示末级，1表示非末级
                return "notEnd";
            }
            if("0".equals(list.get(0).getUseflag())){//0表示停用，1表示使用
                return "notUse";
            }
            return "";
        }
    }
    //根据接口不同科目取的账务数据不同


}
