package com.sinosoft.httpclient.service.impl;

import com.sinosoft.common.WebServiceResult;
import com.sinosoft.domain.InterfaceInfo;
import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.domain.account.AccMonthTrace;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.httpclient.domain.ConfigureManage;
import com.sinosoft.httpclient.domain.VehicleInvoice;
import com.sinosoft.httpclient.repository.ConfigureManageRespository;
import com.sinosoft.httpclient.repository.TaskSchedulingDetailsInfoRespository;
import com.sinosoft.httpclient.repository.VehicleInvoiceRespository;
import com.sinosoft.httpclient.service.VehicleInvoiceService;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.service.InterfaceInfoService;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.account.SettlePeriodService;
import com.sinosoft.service.impl.DataDockingServiceImpl;
import com.sinosoft.util.DateUtil;
import com.sinosoft.util.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VehicleInvoiceServiceImpl implements VehicleInvoiceService {

    private Logger logger = LoggerFactory.getLogger(VehicleInvoiceServiceImpl.class);

    @Resource
    private VehicleInvoiceRespository vehicleInvoiceRespository;

    // 4s 专有关联信息表。
    @Resource
    private ConfigureManageRespository configureManageRespository;

    @Resource
    private BranchInfoRepository branchInfoRepository;

    @Resource
    private VoucherService  voucherService;

    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;

    @Resource
    private SubjectRepository subjectRepository;

    // 任务调度明细表
    @Resource
    private TaskSchedulingDetailsInfoRespository taskSchedulingDetailsInfoRespository;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    // 结算期管理进行的追加
    @Resource
    private SettlePeriodService settlePeriodService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveVehicleInvoiceList(List<VehicleInvoice> vehicleInvoiceList,String loadTime) {
        // 拿到解析数据，直接进行解析处理
        try {
            List<Map<String,Object>> listResultMaps = new ArrayList<>();
            // 错误日志返回信息。
            String branchInfo = null;
            for(int i = 0 ; i < vehicleInvoiceList.size();i++){
                VehicleInvoice vehicleInvoice = vehicleInvoiceList.get(i);
                StringBuilder errorMsg = new StringBuilder();
                branchInfo = vehicleInvoice.getCompanyNo();
                // 看当前必要信息是否都不为空。
                String judgeMsg = judgeInterfaceInfoQuerstion(vehicleInvoice, errorMsg);
                if(!"".equals(judgeMsg)){
                    logger.error("第"+(i+1)+"的错误问题为："+judgeMsg);
                    continue;
                }


                // 1. 看当前信息是什么类型的
                String invoiceType = vehicleInvoice.getInvoiceType();
                String interfaceInfo = "2";
                if("Invoice".equals(invoiceType)){
                    // 结账 找接口2 。类型1/2 (科目与退账相同，金额方向不同)
                    String interfaceType = "1";
                    Map<String,Object> stringObjectMap = convertBussinessToAccounting(vehicleInvoice,errorMsg,interfaceInfo,interfaceType);
                    String resultMsg = (String) stringObjectMap.get("resultMsg");
                    if (!"success".equals(resultMsg)){
                        logger.error("第"+(i+1)+"Invoice类型错误问题为："+resultMsg);
                        continue;
                    }
                    // 变为第二个接口信息
                    interfaceType = "2";
                    Map<String,Object> stringObjectMap1 = convertBussinessToAccounting(vehicleInvoice,errorMsg,interfaceInfo,interfaceType);
                    String resultMsg1 = (String) stringObjectMap1.get("resultMsg");
                    if (!"success".equals(resultMsg1)){
                        logger.error("第"+(i+1)+"Invoice类型错误问题为："+resultMsg1);
                        continue;
                    }

                    // 把两个东西都放入到List集合中
                    listResultMaps.add(stringObjectMap);
                    listResultMaps.add(stringObjectMap1);

                }else{
                    // 退账 找接口2. 类型3/4 (科目与结账相同，金额方向不同)
                    String interfaceType = "3";
                    Map<String,Object> stringObjectMap = convertBussinessToAccounting(vehicleInvoice,errorMsg,interfaceInfo,interfaceType);
                    String resultMsg = (String) stringObjectMap.get("resultMsg");
                    if (!"success".equals(resultMsg)){
                        logger.error("第"+(i+1)+"个Credit类型错误问题为："+resultMsg);
                        // 写个方法直接插入扔库里信息。
                        continue;
                    }
                    // 变为第二个接口信息
                    interfaceType = "4";
                    Map<String,Object> stringObjectMap1 = convertBussinessToAccounting(vehicleInvoice,errorMsg,interfaceInfo,interfaceType);
                    String resultMsg1 = (String) stringObjectMap1.get("resultMsg");
                    if (!"success".equals(resultMsg1)){
                        logger.error("第"+(i+1)+"个Credit类型错误问题为："+resultMsg1);
                        continue;
                    }
                    listResultMaps.add(stringObjectMap);
                    listResultMaps.add(stringObjectMap1);
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
                    logger.error("保存当前"+loadTime+"的"+(i+1)+"数据凭证出错");
                }
            }
            System.out.println("--------------------  上述已经对正确的所有数据进行了入库保存！  ----------------------------");
            vehicleInvoiceRespository.saveAll(vehicleInvoiceList);
            vehicleInvoiceRespository.flush();
            System.out.println("---------------------当前时间范围内的数据，已经全部保存到对接接口表中---------------------------");
            interfaceInfoService.successSave(branchInfo, loadTime, "当前时间段内的数据没有问题，全部入库！");
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前错误信息为:"+e);
            return "fali";
        }
    }

    @Override
    public String saveVehicleInvoiceListTest(List<VehicleInvoice> vehicleInvoiceList) {
        vehicleInvoiceRespository.saveAll(vehicleInvoiceList);
        vehicleInvoiceRespository.flush();
        return "success";
    }


    private String judgeInterfaceInfoQuerstion(VehicleInvoice vehicleInvoice,StringBuilder errorMsg){
        if(vehicleInvoice.getCompanyNo() == null || "".equals(vehicleInvoice.getCompanyNo())){
            errorMsg.append("机构编码不能为空");
        }
        if(vehicleInvoice.getOperationDate() == null || "".equals(vehicleInvoice.getOperationDate())){
            errorMsg.append("业务日期不能为空");
        }
        if(vehicleInvoice.getVehicleCost().toString() == null || "".equals(vehicleInvoice.getVehicleCost().toString())){
            errorMsg.append("车辆成本金额不能为空");
        }
        if(vehicleInvoice.getVehiclePrice().toString() == null || "".equals(vehicleInvoice.getVehiclePrice().toString())){
            errorMsg.append("车辆价格合计金额不能为空");
        }
        if(vehicleInvoice.getVatTax().toString() == null || "".equals(vehicleInvoice.getVatTax().toString())){
            errorMsg.append("增值税金额不能为空");
        }
        if(vehicleInvoice.getInvoiceType() == null || "".equals(vehicleInvoice.getInvoiceType())){
            errorMsg.append("账务类型不能为空");
        }
        return  errorMsg.toString();
    }

    private Map<String,Object> convertBussinessToAccounting(VehicleInvoice vehicleInvoice,StringBuilder errorMsg,String interfaceInfo,String interfaceType){
        Map<String,Object> resultMap = new HashMap<>();
        VoucherDTO dto = new VoucherDTO();
        String branchCode = vehicleInvoice.getCompanyNo();//为当前的机构编码
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
        String operationDate = vehicleInvoice.getOperationDate();//凭证的日期
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
        String monthTrace = recursiveCalls(centerCode, accbookType, accbookCode, yearMonth);
        if(!"final".equals(monthTrace)){
            // 如果不是final 就出现了异常了,
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
        // 这里科目信息开始已经有顺序了。直接按照顺序给值即可。 （即为：分录的形式）
        if (configureManages.size() > 0) {

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
                    if (i + 1 == 1) {
                        voucherDTO1.setDebit(vehicleInvoice.getVehiclePrice().toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i + 1 == 2) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(vehicleInvoice.getVehiclePrice().toString());
                    } else if (i + 1 == 3) {
                        voucherDTO1.setDebit(vehicleInvoice.getVehicleCost().toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i + 1 == 4) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(vehicleInvoice.getVehicleCost().toString());
                    } else if (i + 1 == 5) {
                        voucherDTO1.setDebit("1.00");
                        voucherDTO1.setCredit("0.00");
                    } else if (i + 1 == 6) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit("1.00");
                    } else if (i + 1 == 7) {
                        voucherDTO1.setDebit("1.00");
                        voucherDTO1.setCredit("0.00");
                    } else if (i + 1 == 8) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit("1.00");
                    }
                } else if ("2".equals(interfaceType)) {
                    // 不一样的接口+接口类型，这里判断的金额不同。
                    if (i == 0) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(vehicleInvoice.getVehiclePrice().toString());
                    } else if (i == 1) {
                        // 当前为1131/11/01 金额为求和
                        String sumAmount = vehicleInvoice.getVehiclePrice().add(vehicleInvoice.getVatTax()).toString();
                        voucherDTO1.setDebit(sumAmount);
                        voucherDTO1.setCredit("0.00");
                    } else if (i == 2) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(vehicleInvoice.getVatTax().toString());
                    }
                } else if ("3".equals(interfaceType)) {
                    if (i + 1 == 1) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(vehicleInvoice.getVehiclePrice().toString());
                    } else if (i + 1 == 2) {
                        voucherDTO1.setDebit(vehicleInvoice.getVehiclePrice().toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i + 1 == 3) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(vehicleInvoice.getVehicleCost().toString());
                    } else if (i + 1 == 4) {
                        voucherDTO1.setDebit(vehicleInvoice.getVehicleCost().toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i + 1 == 5) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit("1.00");
                    } else if (i + 1 == 6) {
                        voucherDTO1.setDebit("1.00");
                        voucherDTO1.setCredit("0.00");
                    } else if (i + 1 == 7) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit("1.00");
                    } else if (i + 1 == 8) {
                        voucherDTO1.setDebit("1.00");
                        voucherDTO1.setCredit("0.00");
                    }
                } else if ("4".equals(interfaceType)) {
                    // 不一样的接口+接口类型，这里判断的金额不同。
                    if (i == 0) {
                        voucherDTO1.setDebit(vehicleInvoice.getVehiclePrice().toString());
                        voucherDTO1.setCredit("0.00");
                    } else if (i == 1) {
                        // 当前为1131/11/01 金额为求和
                        String sumAmount = vehicleInvoice.getVehiclePrice().add(vehicleInvoice.getVatTax()).toString();
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setCredit(sumAmount);
                    } else if (i == 2) {
                        voucherDTO1.setDebit("0.00");
                        voucherDTO1.setDebit(vehicleInvoice.getVatTax().toString());
                        voucherDTO1.setCredit("0.00");
                    }
                }

                voucherDTO1.setRemarkName(vehicleInvoice.getVin());
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
        }else{
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


    public String recursiveCalls(String centerCode,String accBookType,String accBookCode,String yearMonth){
        //  查询会计月底第一条
        AccMonthTrace newestAccMonthTrace = accMonthTraceRespository.findNewestAccMonthTrace(centerCode, accBookType, accBookCode);
        AccMonthTrace newestAccMonthTraceASC = accMonthTraceRespository.findNewestAccMonthTraceASC(centerCode, accBookType, accBookCode);
        String yearMonthDateAboutSql = newestAccMonthTrace.getId().getYearMonthDate();
        String yearMonthDateAboutSqlAsc = newestAccMonthTraceASC.getId().getYearMonthDate();
        SimpleDateFormat sdfNew = new SimpleDateFormat("yyyyMM");
        try {
            Date accountDate = sdfNew.parse(yearMonthDateAboutSql);// 账务年月
            Date bussinessDate = sdfNew.parse(yearMonth);// 业务年月
            Date accountDateAsc = sdfNew.parse(yearMonthDateAboutSqlAsc);
            // 业务年月大于账务年月。则需要进行库表新增。其他则不用管。
            if(accountDate.getTime() < bussinessDate.getTime()){
                settlePeriodService.addToAndSave(centerCode, accBookType, accBookCode);
                return recursiveCalls(centerCode,accBookType,accBookCode,yearMonth);
            }else if (accountDateAsc.getTime() > bussinessDate.getTime()){
                return "fail";
            }else{
                return "final";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "error";
        }
    }
}
