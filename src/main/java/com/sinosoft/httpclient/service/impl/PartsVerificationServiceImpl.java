package com.sinosoft.httpclient.service.impl;

import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.httpclient.domain.ConfigureManage;
import com.sinosoft.httpclient.domain.PartsVerification;
import com.sinosoft.httpclient.dto.DnVerifyDTO;
import com.sinosoft.httpclient.dto.InvoiceVerifyDTO;
import com.sinosoft.httpclient.dto.PartsVerificationDTO;
import com.sinosoft.httpclient.dto.ServiceInvoiceDTO;
import com.sinosoft.httpclient.repository.ConfigureManageRespository;
import com.sinosoft.httpclient.repository.PartsVerificationRespository;
import com.sinosoft.httpclient.service.AccountingMethod;
import com.sinosoft.httpclient.service.PartsVerificationService;
import com.sinosoft.httpclient.utils.DtoToEntity;
import com.sinosoft.service.InterfaceInfoService;
import com.sinosoft.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PartsVerificationServiceImpl implements PartsVerificationService {



    private Logger logger = LoggerFactory.getLogger(PartsVerificationServiceImpl.class);

    @Resource
    PartsVerificationRespository respository;
    @Resource
    private VoucherService voucherService;
    @Resource
    private AccountingMethod accountingMethod;
    @Resource
    private ConfigureManageRespository configureManageRespository;
    @Resource
    private VehicleInvoiceServiceImpl vehicleInvoiceServiceImpl;

    @Resource
    private InterfaceInfoService interfaceInfoService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String getPartsVerification(List<PartsVerificationDTO> partsVerificationList,String loadTime) {
        try {
            StringBuilder errorMsg = new StringBuilder();
            //获取时间区间所有的发票信息
            List<PartsVerification> partsVerificationEntityList = new ArrayList<>();

            // 拿到解析数据，直接进行解析处理
            List<Map<String, Object>> listResultMaps = new ArrayList<>();

            // 错误日志返回信息。
            StringBuilder errorAllMessage = new StringBuilder();
            String branchInfo = null;
            for (int i = 0 ;i < partsVerificationList.size() ;i ++) {
                PartsVerificationDTO partsVerificationDTO = partsVerificationList.get(i);
                // 校验当前业务数据是否满足生账条件。
                String judgeMsg = judgeInterfaceInfoQuerstion(partsVerificationDTO, errorMsg);
                if (!"".equals(judgeMsg)) {
                    logger.error(judgeMsg);
                    errorAllMessage.append("第"+ (i + 1) + "个的错误问题为：" + judgeMsg);
                    continue;
                }
                //账单金额放方向
                String interfaceInfo = "4";
                String interfaceType = "1";
                partsVerificationDTO.setBatch(partsVerificationDTO.getId());
                List<InvoiceVerifyDTO> invoiceVerifyDTOList = partsVerificationDTO.getVerifyInvoices();
                List<DnVerifyDTO> DnVerifyDTOList = partsVerificationDTO.getVerifyDns();
                if (!invoiceVerifyDTOList.isEmpty()) {
                    for (InvoiceVerifyDTO invoiceVerify : invoiceVerifyDTOList) {
                        PartsVerification InvoiceVerify = new PartsVerification();
                        InvoiceVerify = (PartsVerification) DtoToEntity.populate(partsVerificationDTO, InvoiceVerify);
                        InvoiceVerify = (PartsVerification) DtoToEntity.populate(invoiceVerify, InvoiceVerify);
                        partsVerificationEntityList.add(InvoiceVerify);
                    }
                }
                if (!DnVerifyDTOList.isEmpty()) {
                    for (DnVerifyDTO dnVerifyDTO : DnVerifyDTOList) {
                        PartsVerification DnVerify = new PartsVerification();
                        DnVerify = (PartsVerification) DtoToEntity.populate(partsVerificationDTO, DnVerify);
                        DnVerify = (PartsVerification) DtoToEntity.populate(dnVerifyDTO, DnVerify);
                        partsVerificationEntityList.add(DnVerify);
                    }
                }
                //  没有类型需要进行区分。
                Map<String, Object> accountingMap = convertBussinessToAccounting(partsVerificationDTO, errorMsg, interfaceInfo, interfaceType);
                String resultMsg1 = (String) accountingMap.get("resultMsg");
                if (!"success".equals(resultMsg1)) {
                    logger.error(resultMsg1);
                    errorAllMessage.append("第"+(i+1)+"个的错误问题为:"+judgeMsg);
                    continue;
                }

                listResultMaps.add(accountingMap);
            }
            System.out.println("---------------《当前时间范围内的正确的数据，已全部保存到List集合中，下面开始保存入库！》------------------");
            for(int i = 0 ; i < listResultMaps.size(); i ++){
                Map<String, Object> stringObjectMap = listResultMaps.get(i);
                List<VoucherDTO> list2 = (List<VoucherDTO>) stringObjectMap.get("list2");
                List<VoucherDTO> list3 = (List<VoucherDTO>) stringObjectMap.get("list3");
                VoucherDTO dto = (VoucherDTO) stringObjectMap.get("dto");
                String voucherNo = voucherService.saveVoucherForFourS(list2, list3, dto);
                if(!"success".equals(voucherNo)){
                    logger.error(voucherNo);
                    //TODO 保存到对应的数据接口表中。
                    errorAllMessage.append("保存凭证出错");
                }
            }

            System.out.println("--------------------  上述已经对正确的所有数据进行了入库保存！  ----------------------------");
            respository.saveAll(partsVerificationEntityList);
            respository.flush();

            // 保存日志信息
            if("".equals(errorAllMessage.toString())){
                interfaceInfoService.successSave(branchInfo,loadTime,"当前时间段内的数据没有问题，全部入库！");
                return "success";
            }
            interfaceInfoService.failSave(branchInfo,loadTime,"当前时间段内的信息个别信息有问题"+errorAllMessage.toString());
            return "halfsuccess";

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前错误信息为:"+e);
            return "fali";
        }
    }

    private String judgeInterfaceInfoQuerstion(PartsVerificationDTO partsVerificationDTO, StringBuilder errorMsg) {
        if (partsVerificationDTO.getCompanyNo() == null || "".equals(partsVerificationDTO.getCompanyNo())) {
            errorMsg.append("机构编码不能为空");
        }
        if (partsVerificationDTO.getVerifyDate() == null || "".equals(partsVerificationDTO.getVerifyDate())) {
            errorMsg.append("业务日期不能为空");
        }
        if(partsVerificationDTO.getAcceptTotalAmount().toString() == null || "".equals(partsVerificationDTO.getAcceptTotalAmount().toString())){
            errorMsg.append("配件采购总价不能为空");
        }
        List<InvoiceVerifyDTO> verifyInvoices = partsVerificationDTO.getVerifyInvoices();
        for (InvoiceVerifyDTO invoiceVerifyDTO : verifyInvoices){
            if(invoiceVerifyDTO.getPurchaseInvoiceNet().toString() == null || "" .equals(invoiceVerifyDTO.getPurchaseInvoiceNet().toString())){
                errorMsg.append("发票金额不能为空");
            }
        }
        return errorMsg.toString();
    }

    //业务数据转换成账务数据
    private Map<String, Object> convertBussinessToAccounting(PartsVerificationDTO partsVerificationDTO, StringBuilder errorMsg, String invoicePrintType, String interfaceType) {
        Map<String, Object> resultMap = new HashMap<>();
        VoucherDTO dto = new VoucherDTO();
        //1.校验凭证头部信息：机构、账套 、会计期间、制单日期等基本信息
        Map informationVerificationtMap = accountingMethod.InformationVerification(partsVerificationDTO.getCompanyNo(), partsVerificationDTO.getVerifyDate(), errorMsg);
        String yearMonth = (String) informationVerificationtMap.get("yearMonth");
        String centerCode = (String) informationVerificationtMap.get("branchCode");
        String branchCode = (String) informationVerificationtMap.get("branchCode");
        String accbookCode = (String) informationVerificationtMap.get("accbookCode");
        String accbookType = (String) informationVerificationtMap.get("accbookType");
        VoucherDTO voucherDTO = (VoucherDTO) informationVerificationtMap.get("voucherDTO");
        //2.存放凭证头部信息到dto中
        //  凭证号
        dto.setVoucherDate(partsVerificationDTO.getVerifyDate());
        dto.setVoucherNo(voucherDTO.getVoucherNo());
        //  年月
        dto.setYearMonth(yearMonth);
        //  操作人
        dto.setCreateBy("001");
        dto.setAccBookCode(accbookCode);
        dto.setAccBookType(accbookType);
        dto.setBranchCode(branchCode);
        dto.setCenterCode(centerCode);
        //  凭证类型 1 为自动对接的默认类型，类型具体是什么暂定
        dto.setVoucherType("1");
        //  数据来源 1.为外围当前外围系统对接 2 为手工
        dto.setDataSource("1");
        //  凭证录入方式是否为自动（1） 手工（2）
        dto.setGenerateWay("1");
        resultMap.put("dto", dto);
        resultMap.put("accountingEntry", partsVerificationDTO);


        List<VoucherDTO> list2 = new ArrayList<>();
        List<VoucherDTO> list3 = new ArrayList<>();

        List<ConfigureManage> configureManages = configureManageRespository.queryConfigureManagesByInterfaceInfoAndInterfaceTypeAndBranchCode(invoicePrintType, interfaceType,branchCode);
        if (configureManages.size() > 0) {
            for (int i = 0; i < configureManages.size(); i++) {
                // 当前这里以为：entry
                VoucherDTO voucherDTO1 = new VoucherDTO();
                VoucherDTO voucherDTO2 = new VoucherDTO();
                // 对科目的校验
                String subjectName = configureManages.get(i).getSubjectName();
                String subjectInfo = configureManages.get(i).getId().getSubjectCode();
                String resultCode = vehicleInvoiceServiceImpl.checkSubjectCodePassMusterBySubjectCodeAll(subjectInfo, accbookCode);
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

                // 当遇到2121/01/10 时 附属在上面的金额。和其他信息，需要遍历里面的list集合个数来新增分录信息,
                // 加紧开发速度，对于分录信息直接进行了分条复制保存。
                if (i == 0) {
                    voucherDTO1.setDebit(partsVerificationDTO.getAcceptTotalAmount().toString());
                    voucherDTO1.setCredit("0.00");

                    //  当前描述字段无法进行选取的问题。
                    voucherDTO1.setRemarkName("所有配件发票校验的数");
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
                } else if (i == 1) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(partsVerificationDTO.getVariance().toString());

                    //  当前描述字段无法进行选取的问题。
                    voucherDTO1.setRemarkName("所有配件发票校验的数");
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
                } else if (i == 2) {
                    // 这里是2121/01/10的科目信息状态。
                    List<InvoiceVerifyDTO> verifyInvoices = partsVerificationDTO.getVerifyInvoices();
                    for (InvoiceVerifyDTO invoiceVerifyDTO : verifyInvoices) {
                        BigDecimal purchaseInvoiceNet = invoiceVerifyDTO.getPurchaseInvoiceNet();
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(purchaseInvoiceNet.toString());

                        //  当前描述字段无法进行选取的问题。
                        voucherDTO1.setRemarkName("所有配件发票校验的数");
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
            }
        } else {
            errorMsg.append("configuremanage表中无配置映射信息！");
            resultMap.put("resultMsg", errorMsg.toString());
            return resultMap;
        }


        // 以上已经对一条凭证处理校验完毕。
        resultMap.put("list2", list2);
        resultMap.put("list3", list3);
        resultMap.put("resultMsg", "success");
        return resultMap;
    }


}
