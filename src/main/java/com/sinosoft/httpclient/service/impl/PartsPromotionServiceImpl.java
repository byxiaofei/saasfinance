package com.sinosoft.httpclient.service.impl;

import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.httpclient.domain.*;
import com.sinosoft.httpclient.repository.ConfigureManageRespository;
import com.sinosoft.httpclient.repository.PartsPromotionRespository;
import com.sinosoft.httpclient.repository.TaskSchedulingDetailsInfoRespository;
import com.sinosoft.httpclient.service.PartsPromotionService;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.service.InterfaceInfoService;
import com.sinosoft.service.VoucherService;
import com.sinosoft.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PartsPromotionServiceImpl implements PartsPromotionService {


    private Logger logger = LoggerFactory.getLogger(PartsPromotionServiceImpl.class);


    @Resource
    private PartsPromotionRespository partsPromotionRespository;

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
    private VehicleInvoiceServiceImpl VehicleInvoiceServiceImpl;

    @Resource
    private SubjectRepository subjectRepository;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    // 任务调度明细表
    @Resource
    private TaskSchedulingDetailsInfoRespository taskSchedulingDetailsInfoRespository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String savePartsPromotionList(List<JsonToPartsPromotion> jsonToPartsPromotionList,String laodTime) {
        List<PartsPromotion> partsPromotionList = new ArrayList<>();
        // 拿到解析数据，直接进行解析处理。
        for(int i = 0 ;i <jsonToPartsPromotionList.size(); i++){
            JsonToPartsPromotion jsonToPartsPromotion = jsonToPartsPromotionList.get(i);
            PartsPromotion partsPromotion = new PartsPromotion();
            partsPromotion.setCompanyNo(jsonToPartsPromotion.getCompanyNo());
            partsPromotion.setDealerNo(jsonToPartsPromotion.getDealerNo());
            partsPromotion.setTransactionType(jsonToPartsPromotion.getTransactionType());
            partsPromotion.setPromotionDate(jsonToPartsPromotion.getPromotionDate());
            partsPromotion.setOperationDate(jsonToPartsPromotion.getOperationDate());
            List<PromotionParts> promotionParts1 = jsonToPartsPromotion.getPromotionParts();
            StringBuilder errorMsg = new StringBuilder();
            // 看当前必要信息是否都不为空。
            String judgeMsg = judgeInterfaceInfoQuerstion(jsonToPartsPromotion, errorMsg);
            if(!"".equals(judgeMsg)){
                //TODO 将错误信息保存在错误日志信息表中。
                logger.error(judgeMsg);
                return "fail";
            }
            // 用于最后的当前金额的累加。
            BigDecimal finalAmount = new BigDecimal("0.00");
            // 这里塞值的同时，把集合中对象的：数量×金额 = 金额 ——》 在金额累加
            for(int j = 0 ;j <promotionParts1.size(); j++){
                PromotionParts promotionParts = jsonToPartsPromotion.getPromotionParts().get(j);

                partsPromotion.setPartsNo(promotionParts.getPartsNo());
                partsPromotion.setFlag(promotionParts.getFlag());
                partsPromotion.setGenuineFlag(promotionParts.getGenuineFlag());
                partsPromotion.setDescription(promotionParts.getDescription());
                partsPromotion.setPartsAnalysisCode(promotionParts.getPartsAnalysisCode());
                partsPromotion.setQuantity(promotionParts.getQuantity());
                partsPromotion.setPartsUnitCost(promotionParts.getPartsUnitCost());

                BigDecimal quantity = promotionParts.getQuantity();
                BigDecimal partsUnitCost = promotionParts.getPartsUnitCost().setScale(2, RoundingMode.HALF_UP);

                BigDecimal amountTime = quantity.setScale(2, RoundingMode.HALF_UP).multiply(partsUnitCost);
                finalAmount = finalAmount.add(amountTime);

            }
            partsPromotionList.add(partsPromotion);
            System.out.println("以上是对信息的拉取处理，为最后凭证生成后能直接入库信息。");

            // 1. 看当前信息是什么类型的
            String transactionType = jsonToPartsPromotion.getTransactionType();
            String interfaceInfo = "5";
            if("M".equals(transactionType)){
                String interfaceType = "1";
                Map<String,Object> stringObjectMap = convertBussinessToAccounting(jsonToPartsPromotion,errorMsg,interfaceInfo,interfaceType,finalAmount);
                String resultMsg = (String) stringObjectMap.get("resultMsg");
                if (!"success".equals(resultMsg)){
                    logger.error(resultMsg);
                    // 写个方法直接插入扔库里信息。
                    continue;
                }
                List<VoucherDTO> list2 = (List<VoucherDTO>) stringObjectMap.get("list2");
                List<VoucherDTO> list3 = (List<VoucherDTO>) stringObjectMap.get("list3");
                VoucherDTO dto1 = (VoucherDTO) stringObjectMap.get("dto");
                String voucherNo = voucherService.saveVoucherForFourS(list2, list3, dto1);
                if(!"success".equals(voucherNo)){
                    logger.error(voucherNo);
                    continue;
                }
            }else{
                String interfaceType = "2";
                Map<String,Object> stringObjectMap = convertBussinessToAccounting(jsonToPartsPromotion,errorMsg,interfaceInfo,interfaceType,finalAmount);
                String resultMsg = (String) stringObjectMap.get("resultMsg");
                if (!"success".equals(resultMsg)){
                    logger.error(resultMsg);
                    // 写个方法直接插入扔库里信息。
                    continue;
                }
                List<VoucherDTO> list2 = (List<VoucherDTO>) stringObjectMap.get("list2");
                List<VoucherDTO> list3 = (List<VoucherDTO>) stringObjectMap.get("list3");
                VoucherDTO dto1 = (VoucherDTO) stringObjectMap.get("dto");
                String voucherNo = voucherService.saveVoucherForFourS(list2, list3, dto1);
                if(!"success".equals(voucherNo)){
                    logger.error(voucherNo);
                    continue;
                }
            }
        }

        partsPromotionRespository.saveAll(partsPromotionList);
        partsPromotionRespository.flush();

        //  保存日志


        return "success";
    }



    private String judgeInterfaceInfoQuerstion(JsonToPartsPromotion jsonToPartsPromotion,StringBuilder errorMsg){
        if(jsonToPartsPromotion.getCompanyNo() == null || "".equals(jsonToPartsPromotion.getCompanyNo())){
            errorMsg.append("机构编码不能为空");
        }
        if(jsonToPartsPromotion.getOperationDate() == null || "".equals(jsonToPartsPromotion.getOperationDate())){
            errorMsg.append("业务日期不能为空");
        }
        if(jsonToPartsPromotion.getTransactionType() == null || "".equals(jsonToPartsPromotion.getTransactionType())){
            errorMsg.append("结账类型不能为空");
        }
        List<PromotionParts> promotionParts = jsonToPartsPromotion.getPromotionParts();
        for(int i = 0 ; i < promotionParts.size() ;i++ ){
            if(promotionParts.get(i).getQuantity().toString() == null || "".equals(promotionParts.get(i).getQuantity().toString())){
                errorMsg.append("当前集合中，第："+i+1+"的数量，不能为空或为0！");
            }
            if(promotionParts.get(i).getPartsUnitCost().toString() == null || "".equals(promotionParts.get(i).getPartsUnitCost().toString())){
                errorMsg.append("当前集合中，第："+i+1+"的金额，不能为0");
            }
        }
        return errorMsg.toString();
    }

    private Map<String,Object> convertBussinessToAccounting(JsonToPartsPromotion jsonToPartsPromotion, StringBuilder errorMsg, String interfaceInfo, String interfaceType,BigDecimal finalAmount){
        Map<String,Object> resultMap = new HashMap<>();
        VoucherDTO dto = new VoucherDTO();
        String branchCode = jsonToPartsPromotion.getCompanyNo();//为当前的机构编码
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

        String operationDate = jsonToPartsPromotion.getOperationDate();//凭证的日期
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

        // 如果没问题，校验的同时就生成了凭证号了。 这里把createBy 创建人 设置为001 默认系统了
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
        dto.setVoucherType("2");
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
        // TODO  这个地方需要去修改一下问题的所在
        List<ConfigureManage> configureManages = configureManageRespository.queryConfigureManagesByInterfaceInfoAndInterfaceTypeAndBranchCode(interfaceInfo, interfaceType,branchCode);
        // 这里科目信息开始已经有顺序了。直接按照顺序给值即可。 （即为：分录的形式）
        for(int i = 0 ; i < configureManages.size(); i++){
            // 当前这里以为：entry
            VoucherDTO voucherDTO1 = new VoucherDTO();
            VoucherDTO voucherDTO2 = new VoucherDTO();
            // 对科目的校验
            String subjectName = configureManages.get(i).getSubjectName();
            String subjectInfo = configureManages.get(i).getId().getSubjectCode();
            String resultCode = VehicleInvoiceServiceImpl.checkSubjectCodePassMusterBySubjectCodeAll(subjectInfo, accbookCode);
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

            if("1".equals(interfaceType)){
                if(i == 0){
                    voucherDTO1.setDebit(finalAmount.toString());
                    voucherDTO1.setCredit("0.00");
                }else if( i == 1){
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(finalAmount.toString());
                }else if ( i == 2) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(finalAmount.toString());
                }else if ( i == 3){
                    voucherDTO1.setDebit(finalAmount.toString());
                    voucherDTO1.setCredit("0.00");
                }else if ( i == 4){
                    voucherDTO1.setDebit(finalAmount.toString());
                    voucherDTO1.setCredit("0.00");
                }else if ( i == 5){
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(finalAmount.toString());
                }
            }else{
                if(i == 0){
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(finalAmount.toString());
                }else if( i == 1){
                    voucherDTO1.setDebit(finalAmount.toString());
                    voucherDTO1.setCredit("0.00");
                }else if ( i == 2) {
                    voucherDTO1.setDebit(finalAmount.toString());
                    voucherDTO1.setCredit("0.00");
                }else if ( i == 3){
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(finalAmount.toString());
                }else if ( i == 4){
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(finalAmount.toString());
                }else if ( i == 5){
                    voucherDTO1.setDebit(finalAmount.toString());
                    voucherDTO1.setCredit("0.00");
                }
            }

            //  当前描述字段无法进行选取的问题。
//            voucherDTO1.setRemarkName(jsonToPartsPromotion.getDescription());
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
        // 以上已经对一条凭证处理校验完毕
        resultMap.put("list2", list2);
        resultMap.put("list3", list3);
        resultMap.put("resultMsg", "success");
        // 返回后开始进行录入凭证
        return resultMap;
    }


}
