package com.sinosoft.httpclient.service.impl;

import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.httpclient.domain.PartsVerification;
import com.sinosoft.httpclient.dto.DnVerifyDTO;
import com.sinosoft.httpclient.dto.InvoiceVerifyDTO;
import com.sinosoft.httpclient.dto.PartsVerificationDTO;
import com.sinosoft.httpclient.dto.ServiceInvoiceDTO;
import com.sinosoft.httpclient.repository.PartsVerificationRespository;
import com.sinosoft.httpclient.service.AccountingMethod;
import com.sinosoft.httpclient.service.PartsVerificationService;
import com.sinosoft.httpclient.utils.DtoToEntity;
import com.sinosoft.service.VoucherService;
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
public class PartsVerificationServiceImpl implements PartsVerificationService {
    private Logger logger = LoggerFactory.getLogger(PartsVerificationServiceImpl.class);

    @Resource
    PartsVerificationRespository respository;
    @Resource
    private VoucherService voucherService;
    @Resource
    private AccountingMethod accountingMethod;

    @Override
    public String getPartsVerification(List<PartsVerificationDTO> partsVerificationList) {
        StringBuilder errorMsg = new StringBuilder();
        //获取时间区间所有的发票信息
        List<PartsVerification> partsVerificationEntityList = new ArrayList<>();

        for (PartsVerificationDTO partsVerificationDTO : partsVerificationList) {
            // 校验当前业务数据是否满足生账条件。
            String judgeMsg = judgeInterfaceInfoQuerstion(partsVerificationDTO, errorMsg);
            if (!"".equals(judgeMsg)) {
                //TODO 将错误信息保存在错误日志信息表中。
                logger.error(judgeMsg);
                return "fail";
            }
            //账单金额放方向
            BigDecimal variance = new BigDecimal("0.00");
            BigDecimal purchaseInvoiceNet = new BigDecimal("0.00");
            BigDecimal BigDecimal = new BigDecimal("0.00");
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
                    purchaseInvoiceNet.add(invoiceVerify.getPurchaseInvoiceNet());
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
            partsVerificationDTO.setPurchaseInvoiceNet(purchaseInvoiceNet);
            //开始生账
            Map<String, Object> accountingMap = convertBussinessToAccounting(partsVerificationDTO, errorMsg, interfaceInfo, interfaceType);
            String resultMsg1 = (String) accountingMap.get("resultMsg");
            if (!"success".equals(resultMsg1)) {
                logger.error(resultMsg1);
                return "fail";
            }
            List<VoucherDTO> list2 = (List<VoucherDTO>) accountingMap.get("list2");
            List<VoucherDTO> list3 = (List<VoucherDTO>) accountingMap.get("list3");
            VoucherDTO dto1 = (VoucherDTO) accountingMap.get("dto");
            String voucherNo = voucherService.saveVoucherForFourS(list2, list3, dto1);
            if (!"success".equals(voucherNo)) {
                logger.error(voucherNo);
                return "fail";
            }
        }
        respository.saveAll(partsVerificationEntityList);
        respository.flush();

        return "success";
    }

    private String judgeInterfaceInfoQuerstion(PartsVerificationDTO partsVerificationDTO, StringBuilder errorMsg) {
        if (partsVerificationDTO.getCompanyNo() == null || "".equals(partsVerificationDTO.getCompanyNo())) {
            errorMsg.append("机构编码不能为空");
        }
        if (partsVerificationDTO.getVerifyDate() == null || "".equals(partsVerificationDTO.getVerifyDate())) {
            errorMsg.append("业务日期不能为空");
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
        //3.获取生成凭证的分录信息
        Map<String, Object> vocherInformation = accountingMethod.AccountingEntryInformation(resultMap, invoicePrintType, interfaceType, errorMsg, accbookCode);
        vocherInformation.put("dto", dto);
        return vocherInformation;
    }


}
