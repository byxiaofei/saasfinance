package com.sinosoft.httpclient.service.impl;

import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.httpclient.domain.*;
import com.sinosoft.httpclient.repository.ConfigureManageRespository;
import com.sinosoft.httpclient.repository.PartsInvoiceRespository;
import com.sinosoft.httpclient.service.PartsInvoiceService;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.service.InterfaceInfoService;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.impl.DataDockingServiceImpl;
import com.sinosoft.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.lang.model.type.ArrayType;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PartsInvoiceServiceImpl implements PartsInvoiceService {

    private Logger logger = LoggerFactory.getLogger(DataDockingServiceImpl.class);

    @Resource
    private PartsInvoiceRespository partsInvoiceRespository;

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

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private VehicleInvoiceServiceImpl vehicleInvoiceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String savePartsInvoiceList(List<JsonToPartsInvoice> jsonToPartsInvoicesList,String loadTime) {
        try {
            List<PartsInvoice> partsInvoices = new ArrayList<>();
            List<Map<String,Object>> listResultMaps = new ArrayList<>();
            StringBuilder errorAllMessage = new StringBuilder();
            String branchInfo = null;

            for(int i = 0;i < jsonToPartsInvoicesList.size();i++){
                JsonToPartsInvoice temp = jsonToPartsInvoicesList.get(i);
                PartsInvoice partsInvoice = new PartsInvoice();
                partsInvoice.setCompanyNo(temp.getCompanyNo());
                partsInvoice.setDealerNo(temp.getDealerNo());
                partsInvoice.setDocType(temp.getDocType());
                partsInvoice.setDocNo(temp.getDocNo());
                partsInvoice.setBizType(temp.getBizType());
                partsInvoice.setDocDate(temp.getDocDate());
                partsInvoice.setCustomerName(temp.getCustomerName());
                partsInvoice.setCompanyName(temp.getCompanyName());
                partsInvoice.setFranchise(temp.getFranchise());
                partsInvoice.setOrderNo(temp.getOrderNo());
                partsInvoice.setOperationDate(temp.getOperationDate());
                branchInfo = temp.getCompanyNo();


                BigDecimal sum = new BigDecimal("0.00");
                for(int j = 0;j < temp.getInvoiceParts().size();j++){
                    PartsInvoiceIn temp1= temp.getInvoiceParts().get(j);
                    partsInvoice.setLine(temp1.getLine());
                    partsInvoice.setPartsNo(temp1.getPartsNo());
                    partsInvoice.setDescription(temp1.getDescription());
                    partsInvoice.setPartsAnalysisCode(temp1.getPartsAnalysisCode());
                    partsInvoice.setDepartmentCode(temp1.getDepartmentCode());
                    partsInvoice.setQuantity(temp1.getQuantity());
                    partsInvoice.setPartsUnitCost(temp1.getPartsUnitCost());
                    partsInvoice.setUnitSellingPrice(temp1.getUnitSellingPrice());
                    partsInvoice.setTotalPrice(temp1.getTotalPrice());
                    partsInvoice.setDiscountRate(temp1.getDiscountRate());
                    partsInvoice.setDiscountAmount(temp1.getDiscountAmount());
                    partsInvoice.setContribution(temp1.getContribution());
                    partsInvoice.setNetValue(temp1.getNetValue());
                    partsInvoice.setVatRate(temp1.getVatRate());
                    partsInvoice.setVatAmount(temp1.getVatAmount());
                    partsInvoice.setCustomerTypeNo(temp1.getCustomerTypeNo());

                    partsInvoices.add(partsInvoice);

                }


            }
            //得到业务数据进行业务处理
            for(int a = 0;a < partsInvoices.size(); a++){
                //取值对象
                PartsInvoice partsInvoice = partsInvoices.get(a);
                StringBuilder errorMsg = new StringBuilder();
                //看当前必要信息是否都不为空。
                String judgeMsg = judgeInterfaceInfoQuerstion(partsInvoice, errorMsg);
                if(!"".equals(judgeMsg)){
                   logger.error(judgeMsg);
                    errorAllMessage.append("第"+(a+1)+"Invoice类型错误问题为："+judgeMsg);
                   continue;
                }
                //看当前数据是什么类型
                String docType = partsInvoice.getDocType();
                //I -- 打印账单
                //C -- 退款账单
                String interfaceInfo = "7";
                if("I".equals(docType)){
                    //配件销售分录  interfaceType --->1
                    String interfacetype = "1";
                    Map<String,Object> objectMap = convertBussinessToAccounting(partsInvoice,errorMsg,interfaceInfo,interfacetype);
                    String resultMsg = (String)objectMap.get("resultMsg");
                    if(!"success".equals(resultMsg)){
                        logger.error(resultMsg);
                        return "fail";
                    }

                    listResultMaps.add(objectMap);

                }else{
                    //配件销售分录  interfaceType --->2
                    String interfacetype = "2";
                    Map<String,Object> objectMap = convertBussinessToAccounting(partsInvoice,errorMsg,interfaceInfo,interfacetype);
                    String resultMsg = (String)objectMap.get("resultMsg");
                    if(!"success".equals(resultMsg)){
                        logger.error(resultMsg);
                        return "fail";
                    }

                    listResultMaps.add(objectMap);

                }
            }
            System.out.println("------------->当前时间范围内的数据，已全部保存到凭证表中，入库成功<-------------");
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

            partsInvoiceRespository.saveAll(partsInvoices);
            partsInvoiceRespository.flush();

            System.out.println("--------------------  上述已经对正确的所有数据进行了入库保存！  ----------------------------");

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

    //判断当前必要信息是否为空实现
    private String judgeInterfaceInfoQuerstion(PartsInvoice partsInvoice,StringBuilder errorMsg){
        if(partsInvoice.getCompanyNo() == null || "".equals(partsInvoice.getCompanyNo())){
            errorMsg.append("机构编码不能为空");
        }
        if(partsInvoice.getOperationDate() == null || "".equals(partsInvoice.getOperationDate())){
            errorMsg.append("业务日期不能为空");
        }
        if(partsInvoice.getTotalPrice() == null || "".equals(partsInvoice.getTotalPrice())){
            errorMsg.append("总销售价格不能为空");
        }
        if(partsInvoice.getPartsUnitCost() == null || "".equals(partsInvoice.getPartsUnitCost())){
            errorMsg.append("配件单位成本不能为空");
        }
        if(partsInvoice.getQuantity() == null || "".equals(partsInvoice.getQuantity())){
            errorMsg.append("数量不能为空");
        }
        if(partsInvoice.getDiscountAmount() == null || "".equals(partsInvoice.getDiscountAmount())){
            errorMsg.append("折扣金额不能为空");
        }
        return  errorMsg.toString();
    }

    private Map<String,Object> convertBussinessToAccounting(PartsInvoice partsInvoice, StringBuilder errorMsg, String interfaceInfo, String interfaceType){
        Map<String,Object> resultMap = new HashMap<>();
        VoucherDTO dto = new VoucherDTO();
        String branchCode = partsInvoice.getCompanyNo();//为当前的机构编码
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
        Date operationDate = partsInvoice.getOperationDate();//凭证的日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(operationDate);
        String yearMonthDate = DateUtil.getDateTimeFormatToGeneralLedger(date);
        if(!"success".equals(yearMonthDate)){
            errorMsg.append("制单日期的格式为：yyyy-MM-dd，请重新校验");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        String operationDateReplace = date.replaceAll("-", "");
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


        String monthTrace = vehicleInvoiceService.recursiveCalls(branchCode, accbookType, accbookCode, yearMonth);
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
        dto.setVoucherDate(date);
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
            // 通过接口类型来区分，金额的走向。
            if ("1".equals(interfaceType)) {
                // 不一样的接口+接口类型，这里判断的金额不同。
                if (i == 0) {
                    voucherDTO1.setDebit((partsInvoice.getQuantity().multiply(partsInvoice.getPartsUnitCost())).toString());
                    voucherDTO1.setCredit("0.00");
                } else if (i == 1) {
                    String credit =  (partsInvoice.getTotalPrice().subtract(partsInvoice.getDiscountAmount().add(partsInvoice.getVatAmount()))).toString();
                    voucherDTO1.setDebit(credit);
                    voucherDTO1.setCredit("0.00");
                } else if (i == 2) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit((partsInvoice.getQuantity().multiply(partsInvoice.getPartsUnitCost())).toString());
                } else if (i == 3) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(partsInvoice.getTotalPrice().toString());
                } else if (i == 4) {
                    voucherDTO1.setDebit(partsInvoice.getDiscountAmount().toString());
                    voucherDTO1.setCredit("0.00");
                } else if (i == 5) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(partsInvoice.getVatAmount().toString());
                }
            } else if ("2".equals(interfaceType)) {
                // 不一样的接口+接口类型，这里判断的金额不同。
                if (i == 0) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(partsInvoice.getTotalPrice().toString());
                } else if (i == 1) {                                                                                                    //待修改
                    String credit =  (partsInvoice.getTotalPrice().subtract(partsInvoice.getDiscountAmount().add(partsInvoice.getQuantity()))).toString();
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(credit);
                } else if (i == 2) {
                    voucherDTO1.setDebit((partsInvoice.getPartsUnitCost().multiply(partsInvoice.getQuantity())).toString());
                    voucherDTO1.setCredit("0.00");
                }else if(i == 3){
                    voucherDTO1.setDebit((partsInvoice.getTotalPrice().toString()));
                    voucherDTO1.setCredit("0.00");
                }else if(i == 4){
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(partsInvoice.getDiscountAmount().toString());
                }else if(i == 5){
                    //待完善待修改
                    voucherDTO1.setDebit(partsInvoice.getVatAmount().toString());
                    voucherDTO1.setCredit("0.00");
                }
            }

            voucherDTO1.setRemarkName(partsInvoice.getDescription());
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

    //初版测试方法
    public String savePartsInvoiceListTest(List<JsonToPartsInvoice> jsonToPartsInvoicesList) {
        List<PartsInvoice> partsInvoices = new ArrayList<>();
        for(int i = 0;i < jsonToPartsInvoicesList.size();i++){
            JsonToPartsInvoice temp = jsonToPartsInvoicesList.get(i);
            PartsInvoice partsInvoice = new PartsInvoice();
            partsInvoice.setCompanyNo(temp.getCompanyNo());
            partsInvoice.setDealerNo(temp.getDealerNo());
            partsInvoice.setDocType(temp.getDocType());
            partsInvoice.setDocNo(temp.getDocNo());
            partsInvoice.setBizType(temp.getBizType());
            partsInvoice.setDocDate(temp.getDocDate());
            partsInvoice.setCustomerName(temp.getCustomerName());
            partsInvoice.setCompanyName(temp.getCompanyName());
            partsInvoice.setFranchise(temp.getFranchise());
            partsInvoice.setOrderNo(temp.getOrderNo());
            partsInvoice.setOperationDate(temp.getOperationDate());
            for(int j = 0;j < temp.getInvoiceParts().size();j++){
                PartsInvoiceIn temp1= temp.getInvoiceParts().get(j);
                partsInvoice.setLine(temp1.getLine());
                partsInvoice.setPartsNo(temp1.getPartsNo());
                partsInvoice.setPartsAnalysisCode(temp1.getPartsAnalysisCode());
                partsInvoice.setDepartmentCode(temp1.getDepartmentCode());
                partsInvoice.setQuantity(temp1.getQuantity());
                partsInvoice.setPartsUnitCost(temp1.getPartsUnitCost());
                partsInvoice.setUnitSellingPrice(temp1.getUnitSellingPrice());
                partsInvoice.setTotalPrice(temp1.getTotalPrice());
                partsInvoice.setDiscountRate(temp1.getDiscountRate());
                partsInvoice.setDiscountAmount(temp1.getDiscountAmount());
                partsInvoice.setContribution(temp1.getContribution());
                partsInvoice.setNetValue(temp1.getNetValue());
                partsInvoice.setVatRate(temp1.getVatRate());
                partsInvoice.setVatAmount(temp1.getVatAmount());
                partsInvoice.setCustomerTypeNo(temp1.getCustomerTypeNo());
            }
            partsInvoices.add(partsInvoice);
        }
        partsInvoiceRespository.saveAll(partsInvoices);
        partsInvoiceRespository.flush();
        return "success";
    }
}
