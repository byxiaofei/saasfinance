package com.sinosoft.httpclient.service.impl;

import com.alibaba.fastjson.serializer.BigDecimalCodec;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.httpclient.domain.ConfigureManage;
import com.sinosoft.httpclient.domain.PartsVerification;
import com.sinosoft.httpclient.domain.ServiceInvoice;
import com.sinosoft.httpclient.dto.*;
import com.sinosoft.httpclient.repository.ConfigureManageRespository;
import com.sinosoft.httpclient.repository.ServiceInvoiceRespository;
import com.sinosoft.httpclient.repository.TaskSchedulingDetailsInfoRespository;
import com.sinosoft.httpclient.service.ServiceInvoiceService;
import com.sinosoft.httpclient.utils.DtoToEntity;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.service.InterfaceInfoService;
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
public class ServiceInvoiceServiceImpl implements ServiceInvoiceService {

    private Logger logger = LoggerFactory.getLogger(ServiceInvoiceServiceImpl.class);

    @Resource
    ServiceInvoiceRespository respository;

    @Resource
    private VehicleInvoiceServiceImpl vehicleInvoiceServiceImpl;

    // 4s 专有关联信息表。
    @Resource
    private ConfigureManageRespository configureManageRespository;

    @Resource
    private BranchInfoRepository branchInfoRepository;

    @Resource
    private VoucherService voucherService;

    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;

    @Resource
    private SubjectRepository subjectRepository;

    // 任务调度明细表
    @Resource
    private TaskSchedulingDetailsInfoRespository taskSchedulingDetailsInfoRespository;

    @Resource
    private InterfaceInfoService interfaceInfoService;


    @Resource
    private VehicleInvoiceServiceImpl vehicleInvoiceService;

    @Override
    public String getServiceInvoiceService(List<ServiceInvoiceDTO> ServiceInvoiceDTOList,String loadTime) {

        try {
            List<ServiceInvoice> serviceInvoiceList = new ArrayList<>();

            // 拿到解析数据，直接进行解析处理
            List<Map<String, Object>> listResultMaps = new ArrayList<>();
            // 用于存放处理数据的金额。
            Map<String,BigDecimal> mapsAboutAmount = new HashMap<>();
            // 错误日志返回信息。
            StringBuilder errorAllMessage = new StringBuilder();

            String branchInfo = null;
            for (int i = 0 ; i<ServiceInvoiceDTOList.size();i++) {
                ServiceInvoiceDTO serviceInvoiceDTO = ServiceInvoiceDTOList.get(i);
                serviceInvoiceDTO.setBatch(serviceInvoiceDTO.getId());
                List<ServicePartsInvoiceDTO> invoiceVerifyDTOList = serviceInvoiceDTO.getInvoiceServiceParts();
                List<ServiceLaborInvoiceDTO> DnVerifyDTOList = serviceInvoiceDTO.getInvoiceServiceLabors();
                branchInfo = serviceInvoiceDTO.getCompanyNo();
                // 配件 销售汇总总价Parts
                BigDecimal totalPriceParts = new BigDecimal("0.00");
                // 配件 折旧汇总金额
                BigDecimal discountAmountParts = new BigDecimal("0.00");
                // 配件 数量*配件的单位成本汇总金额
                BigDecimal partsUnitCosts = new BigDecimal("0.00");

                // 配件+工时的增值税汇总总额
                BigDecimal vatAmountSum = new BigDecimal("0.00");
                if (!invoiceVerifyDTOList.isEmpty()) {
                    for (ServicePartsInvoiceDTO servicePartsInvoiceDTO : invoiceVerifyDTOList) {
                        ServiceInvoice ServicePartsInvoice = new ServiceInvoice();
                        ServicePartsInvoice = (ServiceInvoice) DtoToEntity.populate(serviceInvoiceDTO, ServicePartsInvoice);
                        ServicePartsInvoice = (ServiceInvoice) DtoToEntity.populate(servicePartsInvoiceDTO, ServicePartsInvoice);
                        serviceInvoiceList.add(ServicePartsInvoice);

                        BigDecimal totalPriceMore = servicePartsInvoiceDTO.getTotalPrice();
                        totalPriceParts = totalPriceParts.add(totalPriceMore);// 销售汇总总价

                        BigDecimal discountAmountMore = servicePartsInvoiceDTO.getDiscountAmount();
                        discountAmountParts = discountAmountParts.add(discountAmountMore);// 折旧汇总金额

                        Integer quantity = servicePartsInvoiceDTO.getQuantity();// 这里修改一下类型。
                        BigDecimal partsUnitCost = servicePartsInvoiceDTO.getPartsUnitCost();// 金额

                        partsUnitCosts = partsUnitCosts.add(new BigDecimal(quantity.toString()).multiply(partsUnitCost));//数量*配件的单位成本

                        BigDecimal vatAmount = servicePartsInvoiceDTO.getVatAmount();
                        vatAmountSum = vatAmountSum.add(vatAmount);

                    }

                }

                //    销售汇总价格Labor
                BigDecimal totalPriceLabor = new BigDecimal("0.00");
                // 折旧汇总j金额
                BigDecimal discountAmountLabor = new BigDecimal("0.00");
                if (!DnVerifyDTOList.isEmpty()) {
                    for (ServiceLaborInvoiceDTO serviceLaborInvoiceDTO : DnVerifyDTOList) {
                        ServiceInvoice ServiceLaborInvoice = new ServiceInvoice();
                        ServiceLaborInvoice = (ServiceInvoice) DtoToEntity.populate(serviceInvoiceDTO, ServiceLaborInvoice);
                        ServiceLaborInvoice = (ServiceInvoice) DtoToEntity.populate(serviceLaborInvoiceDTO, ServiceLaborInvoice);
                        serviceInvoiceList.add(ServiceLaborInvoice);

                        BigDecimal totalPrice = serviceLaborInvoiceDTO.getTotalPrice();
                        totalPriceLabor = totalPriceLabor.add(totalPrice);

                        BigDecimal discountAmount = serviceLaborInvoiceDTO.getDiscountAmount();
                        discountAmountLabor = discountAmountLabor.add(discountAmount);

                        BigDecimal vatAmount = serviceLaborInvoiceDTO.getVatAmount();
                        vatAmountSum = vatAmountSum.add(vatAmount);
                    }
                }

                // 以结账为例：计算的是借方金额
                BigDecimal debitSumAmount = discountAmountLabor.add(discountAmountParts);
                // 以结账为例：计算的是贷方借额
                BigDecimal creditSumAmount = totalPriceLabor.add(totalPriceParts).add(vatAmountSum);
                // 结账
                BigDecimal finalSumAmountForI = debitSumAmount.subtract(creditSumAmount);
                // 退账
                BigDecimal finalSumAmountForC = creditSumAmount.subtract(debitSumAmount);

                mapsAboutAmount.put("finalSumAmountForI",finalSumAmountForI);
                mapsAboutAmount.put("finalSumAmountForC",finalSumAmountForC);
                mapsAboutAmount.put("totalPriceParts",totalPriceParts);
                mapsAboutAmount.put("discountAmountParts",discountAmountParts);
                mapsAboutAmount.put("partsUnitCosts",partsUnitCosts);
                mapsAboutAmount.put("totalPriceLabor",totalPriceLabor);
                mapsAboutAmount.put("discountAmountLabor",discountAmountLabor);
                mapsAboutAmount.put("vatAmountSum",vatAmountSum);

                System.out.println("------- 业务数据在此已经清理完毕！--------");

                StringBuilder errorMsg = new StringBuilder();
                String judgeMsg = judgeInterfaceInfoQuerstion(serviceInvoiceDTO, errorMsg);
                if (!"".equals(judgeMsg)) {
                    logger.error(judgeMsg);
                    errorAllMessage.append("第"+(i+1)+"个的错误问题为：" + judgeMsg);
                    continue;
                }

                String invoicePrintType = serviceInvoiceDTO.getInvoicePrintType();
                String interfaceInfo = "10";
                if("I".equals(invoicePrintType)){
                    String interfaceType = "1";
                    Map<String,Object> stringObjectMap = convertBussinessToAccounting(serviceInvoiceDTO,errorMsg,interfaceInfo,interfaceType,mapsAboutAmount);
                    String resultMsg = (String) stringObjectMap.get("resultMsg");
                    if(!"success".equals(resultMsg)){
                        logger.error(resultMsg);
                        errorAllMessage.append("第"+(i+1)+"个的错误问题为："+judgeMsg);
                        continue;
                    }

                    listResultMaps.add(stringObjectMap);

                }else{
                    String interfaceType = "2";
                    Map<String,Object> stringObjectMap = convertBussinessToAccounting(serviceInvoiceDTO,errorMsg,interfaceInfo,interfaceType,mapsAboutAmount);
                    String resultMsg = (String) stringObjectMap.get("resultMsg");
                    if(!"success".equals(resultMsg)){
                        logger.error(resultMsg);
                        errorAllMessage.append("第"+(i+1)+"个的错误问题为："+judgeMsg);
                        continue;
                    }

                    listResultMaps.add(stringObjectMap);
                }
            }

            System.out.println("---------------《当前时间范围内的正确的数据，已全部保存到List集合中，下面开始保存入库！》------------------");
            // 当前时间范围内解析到的所以数据放入到对应的对接接口表中存放信息。
            for(int i = 0 ; i < listResultMaps.size(); i++ ){
                Map<String, Object> stringObjectMap = listResultMaps.get(i);
                List<VoucherDTO> list2 = (List<VoucherDTO>) stringObjectMap.get("list2");
                List<VoucherDTO> list3 = (List<VoucherDTO>) stringObjectMap.get("list3");
                VoucherDTO dto = (VoucherDTO) stringObjectMap.get("dto");
                String voucherNo = voucherService.saveVoucherForFourS(list2, list3, dto);
                if(!"success".equals(voucherNo)){
                    logger.error(voucherNo);
                    errorAllMessage.append("保存凭证出错");
                }
            }
            System.out.println("--------------------  上述已经对正确的所有数据进行了入库保存！  ----------------------------");

            respository.saveAll(serviceInvoiceList);
            respository.flush();

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


    private String judgeInterfaceInfoQuerstion(ServiceInvoiceDTO serviceInvoiceDTO, StringBuilder errorMsg) {
        if (serviceInvoiceDTO.getCompanyNo() == null || "".equals(serviceInvoiceDTO.getCompanyNo())) {
            errorMsg.append("机构编码不能为空");
        }
        if (serviceInvoiceDTO.getDocDate() == null || "".equals(serviceInvoiceDTO.getDocDate())) {
            errorMsg.append("业务日期不能为空");
        }
        if (serviceInvoiceDTO.getInvoicePrintType() == null || "".equals(serviceInvoiceDTO.getInvoicePrintType())) {
            errorMsg.append("结账类型不能为空");
        }
        if (serviceInvoiceDTO.getInvoiceNo() == null || "".equals(serviceInvoiceDTO.getInvoiceNo())) {
            errorMsg.append("摘要不能为空");
        }
        List<ServiceLaborInvoiceDTO> invoiceServiceLabors = serviceInvoiceDTO.getInvoiceServiceLabors();

        List<ServicePartsInvoiceDTO> invoiceServiceParts = serviceInvoiceDTO.getInvoiceServiceParts();

        for (int i = 0; i < invoiceServiceLabors.size(); i++) {
            if (invoiceServiceLabors.get(i).getTotalPrice().toString() == null || "".equals(invoiceServiceLabors.get(i).getTotalPrice().toString())) {
                errorMsg.append("当前invoiceServiceLabors集合中，第：" + (i + 1) + "个的工时销售总价，不能为空或为0！");
            }
            if (invoiceServiceLabors.get(i).getDiscountAmount().toString() == null || "".equals(invoiceServiceLabors.get(i).getDiscountAmount().toString())) {
                errorMsg.append("当前invoiceServiceLabors集合中，第：" + (i + 1) + "个的工时折旧金额，不能为空或为0！");
            }
            if (invoiceServiceLabors.get(i).getVatAmount().toString() == null || "".equals(invoiceServiceLabors.get(i).getVatAmount().toString())) {
                errorMsg.append("当前invoiceServiceLabors集合中，第：" + (i + 1) + "个的增值税金额，不能为空或为0！");
            }
        }
        for (int j = 0; j < invoiceServiceParts.size(); j++) {
            if (invoiceServiceParts.get(j).getQuantity().toString() == null || "".equals(invoiceServiceParts.get(j).getQuantity().toString())) {
                errorMsg.append("当前invoiceServiceParts集合中，第：" + (j + 1) + "个的数量，不能为空或为0！");
            }
            if (invoiceServiceParts.get(j).getPartsUnitCost().toString() == null || "" .equals(invoiceServiceParts.get(j).getPartsUnitCost().toString())) {
                errorMsg.append("当前invoiceServiceParts集合中，第：" + (j + 1) + "个的金额，不能为0");
            }
            if (invoiceServiceParts.get(j).getTotalPrice().toString() == null || "".equals(invoiceServiceParts.get(j).getTotalPrice().toString())) {
                errorMsg.append("当前invoiceServiceParts集合中，第：" + (j + 1) + "个的配件销售总价，不能为空或为0！");
            }
            if (invoiceServiceParts.get(j).getDiscountAmount().toString() == null || "".equals(invoiceServiceParts.get(j).getDiscountAmount().toString())) {
                errorMsg.append("当前invoiceServiceParts集合中，第：" + (j + 1) + "个的工时折旧金额，不能为空或为0！");
            }
            if (invoiceServiceParts.get(j).getVatAmount().toString() == null || "".equals(invoiceServiceParts.get(j).getVatAmount().toString())) {
                errorMsg.append("当前invoiceServiceParts集合中，第：" + (j + 1) + "个的增值税金额，不能为空或为0！");
            }
        }
        return errorMsg.toString();
    }


    private Map<String,Object> convertBussinessToAccounting(ServiceInvoiceDTO serviceInvoiceDTO,StringBuilder errorMsg,String interfaceInfo,String interfaceType,Map<String,BigDecimal> mapsAboutAmount){
        Map<String,Object> resultMap = new HashMap<>();
        VoucherDTO dto = new VoucherDTO();
        String branchCode = serviceInvoiceDTO.getCompanyNo();// 当前的机构编码
        List<Map<String, Object>> branchMsg = branchInfoRepository.checkExistsComCode(branchCode);
        if(branchMsg.size() <= 0){
            errorMsg.append("机构单位编码信息匹配不正确检查映射关系");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        if(branchMsg.size() > 0){
            Map<String, Object> stringObjectMap = branchMsg.get(0);
            String flagMsg  = (String) stringObjectMap.get("flag");
            if("0".equals(flagMsg)){
                errorMsg.append(branchCode+"机构已经停用");
                resultMap.put("resultMsg",errorMsg.toString());
                return resultMap;
            }
        }

        String operationDate = serviceInvoiceDTO.getDocDate();
        String yearMonthDate = DateUtil.getDateTimeFormatToGeneralLedger(operationDate);
        if(!"success".equals(yearMonthDate)){
            errorMsg.append("制单日期的格式为：yyyy-MM-dd，请重新校验");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        String operationDateReplace = operationDate.replaceAll("-", "");
        // 为会计期间
        String yearMonth = operationDateReplace.substring(0, 6);

        // 需要通过机构找到账套信息，账套类型。来找到对应的凭证号。
        String accbookCode = "";
        String accbookType = "";
        Map<Integer,Object> params = new HashMap<>();
        int paramNo = 1;
        StringBuffer sql1 = new StringBuffer();
        sql1.append("select acc.account_type as accountType , acc.account_code as accountCode   from accountinfo acc inner join branchaccount ba join branchinfo b  on acc.id = ba.account_id  and b.id = ba.branch_id where 1=1 and b.com_code = ?"+paramNo);
        params.put(paramNo,branchCode);
        paramNo++;
        List<?> checkMsg = branchInfoRepository.queryBySqlSC(sql1.toString(), params);
        if(checkMsg.size() <= 0){
            errorMsg.append(branchCode+"机构并无关联账套");
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

        String centerCode = branchCode;// branchCode 与centerCode 相同

        // 这里看是否需要进行会计月度的追加。
        // 根据机构和账套查询当前的最大会计月度，并选择到当前的日期，是否与当
        String monthTrace = vehicleInvoiceService.recursiveCalls(centerCode, accbookType, accbookCode, yearMonth);
        if(!"final".equals(monthTrace)){
            // 如果不是final 就出现了异常了
            errorMsg.append("当前对会计期间的开启存在异常");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }

        // 如果没有问题，校验的同时就生成了凭证号了。 这里把createBy 创建人，设置为001 系统默认了
        VoucherDTO voucherDTO = voucherService.setVoucher1(yearMonth, centerCode, branchCode, accbookCode, accbookType,"001");
        if(voucherDTO.getYearMonth() == null || "".equals(voucherDTO.getYearMonth())){
            errorMsg.append("当前账套信息下没有对应的凭证月");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        // 传过来的年月，需要判断当前月是否已经结转。
        List<?> objects = accMonthTraceRespository.queryAccMonthTraceByChooseMessage(centerCode, yearMonth, accbookType, accbookCode);
        if(objects.size() > 0){
            errorMsg.append("当前月已经进行结转不能再新增凭证");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }

        // 存放到dto中。
        //  凭证号
        dto.setVoucherDate(operationDate);
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
        resultMap.put("dto",dto);

        List<VoucherDTO> list2 = new ArrayList<>();
        List<VoucherDTO> list3 = new ArrayList<>();

        // 这里给1/2 来判断生成那个类型的数据凭证信息。
        // 开始科目代码和专项信息存放整理，方便后续直接保存入库。
        // 之前是通过科目代码找专项一级，在通过专项一级找对应的字段，来拿到对接文档中的数据，并拿到数据再去数据库中比对信息是否存在。
        List<ConfigureManage> configureManages = configureManageRespository.queryConfigureManagesByInterfaceInfoAndInterfaceTypeAndBranchCode(interfaceInfo, interfaceType, branchCode);
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


                if ("1".equals(interfaceType)) {
                    if (i == 0) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(mapsAboutAmount.get("totalPriceLabor").toString());
                    } else if (i == 1) {
                        voucherDTO1.setDebit(mapsAboutAmount.get("finalSumAmountForI").toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i == 2) {
                        voucherDTO1.setDebit(mapsAboutAmount.get("discountAmountLabor").toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i == 3) {
                        voucherDTO1.setDebit(mapsAboutAmount.get("partsUnitCosts").toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i == 4) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(mapsAboutAmount.get("partsUnitCosts").toString());
                    } else if (i == 5) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(mapsAboutAmount.get("totalPriceParts").toString());
                    } else if (i == 6) {
                        voucherDTO1.setDebit(mapsAboutAmount.get("discountAmountParts").toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i == 7) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(mapsAboutAmount.get("vatAmountSum").toString());
                    }
                } else {
                    if (i == 0) {
                        voucherDTO1.setDebit(mapsAboutAmount.get("totalPriceLabor").toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i == 1) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(mapsAboutAmount.get("finalSumAmountForI").toString());
                    } else if (i == 2) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(mapsAboutAmount.get("discountAmountLabor").toString());
                    } else if (i == 3) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(mapsAboutAmount.get("partsUnitCosts").toString());
                    } else if (i == 4) {
                        voucherDTO1.setDebit(mapsAboutAmount.get("partsUnitCosts").toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i == 5) {
                        voucherDTO1.setDebit(mapsAboutAmount.get("totalPriceParts").toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i == 6) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(mapsAboutAmount.get("discountAmountParts").toString());
                    } else if (i == 7) {
                        voucherDTO1.setDebit(mapsAboutAmount.get("vatAmountSum").toString());
                        voucherDTO1.setCredit("0.00");
                    }
                }


                //  当前描述字段无法进行选取的问题。
                voucherDTO1.setRemarkName(serviceInvoiceDTO.getInvoiceNo());
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
        } else {
            errorMsg.append("没有对应的configuremanage表中的管理信息。");
            resultMap.put("resultMsg", errorMsg.toString());
            return resultMap;
        }

        // 以上已经对一条凭证处理校验完毕
        resultMap.put("list2", list2);
        resultMap.put("list3", list3);
        resultMap.put("resultMsg", "success");
        // 返回后开始进行录入凭证
        return resultMap;
    }


}
