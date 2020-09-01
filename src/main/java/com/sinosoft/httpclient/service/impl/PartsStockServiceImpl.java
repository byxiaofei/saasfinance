package com.sinosoft.httpclient.service.impl;

import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.httpclient.domain.*;
import com.sinosoft.httpclient.repository.ConfigureManageRespository;
import com.sinosoft.httpclient.repository.PartsStockRespository;
import com.sinosoft.httpclient.service.PartsStockService;
import com.sinosoft.repository.BranchInfoRepository;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class PartsStockServiceImpl implements PartsStockService {

    private Logger logger = LoggerFactory.getLogger(PartsStockServiceImpl.class);


    @Resource
    private PartsStockRespository partsStockRespository;

    @Resource
    private VoucherService voucherService;
    @Resource
    private BranchInfoRepository branchInfoRepository;
    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;
    @Resource
    private ConfigureManageRespository configureManageRespository;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private VehicleInvoiceServiceImpl vehicleInvoiceService;


    Map<String,Object> resultMap = new HashMap<>();
    VoucherDTO voucherDTO = new VoucherDTO();
    StringBuilder errorMsg = new StringBuilder();

    /**
     * 保存 获取上次调用时间到当前时间，所有配件库存变动相关的数据，包括入库、退库、库存盘点
     * @param jsonTopartsStockList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String savePartsStockListList(List<JsonToPartsStock> jsonTopartsStockList,String loadTime) {
        saveinterface(jsonTopartsStockList);

        // 1 拿到解析数据，直接进行解析处理
        for(JsonToPartsStock jsonToPartsStock : jsonTopartsStockList ){
            errorMsg = new StringBuilder();
            boolean flag = true ;
            for(PartsStockIn partsStockIn: jsonToPartsStock.getStockInParts()){
                // 校验必要信息非空
                String judgeMsg = judgeInterfaceInfoQuerstion(jsonToPartsStock,partsStockIn);
                if (returnErrorInfo(judgeMsg)) {
                    //记录失败日志
                    interfaceInfoService.failSave("system",loadTime,errorMsg.toString());
                    flag = false;
                }
            }

            if(!flag) continue;

            //根据业务类型处理分录信息
            if (manageTransactionType(jsonToPartsStock,jsonToPartsStock.getStockInParts(), Constant.INTERFACEINFO_3)) {
                //记录失败日志
                interfaceInfoService.failSave("system",loadTime,errorMsg.toString());
                continue;
            }

        }

        //保存日志
        return "success";
    }

    private void saveinterface(List<JsonToPartsStock> jsonTopartsStockList) {
        List<PartsStock> partsStocks = new ArrayList<>();
        for(int i=0;i<jsonTopartsStockList.size();i++){
            JsonToPartsStock temp =jsonTopartsStockList.get(i);
            PartsStock partsStock = new PartsStock();
            //转成数据库对象
            partsStock.setCompanyNo(temp.getCompanyNo());
            partsStock.setDealerNo(temp.getDealerNo());
            partsStock.setOperationDate(temp.getOperationDate());
            partsStock.setTransactionType(temp.getTransactionType());
            for(int j=0;j<temp.getStockInParts().size();j++){
                PartsStockIn temp1 =temp.getStockInParts().get(j);

                partsStock.setBusinessDate(temp1.getBusinessDate());
                partsStock.setDescription(temp1.getDescription());
                partsStock.setGenuineFlag(temp1.getGenuineFlag());
                partsStock.setPartsAnalysisCode(temp1.getPartsAnalysisCode());
                partsStock.setPartsNo(temp1.getPartsNo());
                partsStock.setPartsUnitCost(temp1.getPartsUnitCost());
                partsStock.setPoNo(temp1.getPoNo());
                partsStock.setDnNo(temp1.getDnNo());
                partsStock.setQuantity(temp1.getQuantity());
                partsStock.setSupplierDescription(temp1.getSupplierDescription());
                partsStock.setSupplierNo(temp1.getSupplierNo());

            }

            partsStocks.add(partsStock);
        }


        partsStockRespository.saveAll(partsStocks);
        partsStockRespository.flush();
    }

    /**
     * 根据业务类型处理分录信息
     * @param partsStockIns
     * @param interfaceInfo 接口标识  此处为第一个接口
     * @return
     */
    private boolean manageTransactionType(JsonToPartsStock jsonToPartsStock ,List<PartsStockIn> partsStockIns, String interfaceInfo) {
        if(Constant.RECEIVE_3.equals(jsonToPartsStock.getTransactionType())){
            //将接口数据信息转凭证
            Map<String,Object> stringObjectMap = convertBussinessToAccounting(jsonToPartsStock,partsStockIns,interfaceInfo,Constant.INTERFACETYPE_1);
            String resultMsg = (String) stringObjectMap.get("resultMsg");
            if (returnNoSuccess(resultMsg)) return true;
            String voucherNo = saveVoucher(stringObjectMap);
            if (returnNoSuccess(voucherNo)) return true;

        }else if(Constant.UNRECEIVE_3.equals(jsonToPartsStock.getTransactionType())){
            Map<String,Object> stringObjectMap = convertBussinessToAccounting(jsonToPartsStock,partsStockIns,interfaceInfo,Constant.INTERFACETYPE_2);
            String resultMsg = (String) stringObjectMap.get("resultMsg");
            if (returnNoSuccess(resultMsg)) return true;
            String voucherNo = saveVoucher(stringObjectMap);
            if (returnNoSuccess(voucherNo)) return true;
        }
        return false;
    }

    //将接口数据信息转凭证
    private Map<String,Object> convertBussinessToAccounting(JsonToPartsStock jsonToPartsStock ,List<PartsStockIn> partsStockIns,String interfaceInfo,String interfaceType){
        resultMap = new HashMap<>();
        VoucherDTO dto = new VoucherDTO();
        String branchCode = jsonToPartsStock.getCompanyNo();//为当前的机构编码
        String centerCode = branchCode;
        voucherDTO = new VoucherDTO();
        String operationDate = jsonToPartsStock.getOperationDate();//凭证的日期
        String operationDateReplace = operationDate.replaceAll("-", "");
        //会计期间
        String yearMonth = operationDateReplace.substring(0, 6);
        //校验 机构 制单日期 等必要信息校验
        if(!checkInfo(branchCode,operationDate,yearMonth)) return resultMap ;

        dto.setVoucherDate(operationDate);
        dto.setVoucherNo(voucherDTO.getVoucherNo());
        dto.setYearMonth(yearMonth);
        dto.setCreateBy("001");
        dto.setAccBookCode(voucherDTO.getAccBookCode());
        dto.setAccBookType(voucherDTO.getAccBookType());
        dto.setBranchCode(branchCode);
        dto.setCenterCode(centerCode);
        dto.setVoucherType("2");
        dto.setDataSource("1");
        //  凭证录入方式是否为自动（1） 手工（2）
        dto.setGenerateWay("1");
        resultMap.put("dto",dto);

        List<VoucherDTO> list2 = new ArrayList<>();
        List<VoucherDTO> list3 = new ArrayList<>();
        //金额数据赋值
        processData(jsonToPartsStock,partsStockIns, interfaceInfo, interfaceType, branchCode, list2, list3);
        // 以上已经对一条凭证处理校验完毕
        resultMap.put("list2", list2);
        resultMap.put("list3", list3);
        resultMap.put("resultMsg", "success");
        // 返回后开始进行录入凭证
        return resultMap;
    }



    private boolean checkInfo(String branchCode,String operationDate,String yearMonth){
        List<Map<String, Object>> branchMsg = branchInfoRepository.checkExistsComCode(branchCode);
        if(branchMsg.size() <= 0){
            errorMsg.append("机构单位编码信息匹配不正确检查映射关系");
            resultMap.put("resultMsg",errorMsg.toString());
            return false;
        }
        if(branchMsg.size() > 0){
            Map<String, Object> stringObjectMap = branchMsg.get(0);
            String flagMsg  = (String) stringObjectMap.get("flag");
            if("0".equals(flagMsg)){
                errorMsg.append(branchCode+"机构已经停用");
                resultMap.put("resultMsg",errorMsg.toString());
                return false;
            }
        }

        String yearMonthDate = DateUtil.getDateTimeFormatToGeneralLedger(operationDate);
        if(!"success".equals(yearMonthDate)){
            errorMsg.append("制单日期的格式为：yyyy-MM-dd，请重新校验");
            resultMap.put("resultMsg",errorMsg.toString());
            return false;
        }

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
            return false;
        }else{
            // 这里因为一个机构指能对应一个账套，所以这里只会查询出1条数据
            for(Object o : checkMsg){
                Map maps = new HashMap();
                maps.putAll((Map) o);
                accbookType = maps.get("accountType").toString();
                accbookCode = maps.get("accountCode").toString();
            }
        }

        String monthTrace = vehicleInvoiceService.recursiveCalls(branchCode, accbookType, accbookCode, yearMonth);
        if(!"final".equals(monthTrace)){
            if("fail".equals(monthTrace)){
                errorMsg.append("不存在当前会计期间");
                resultMap.put("resultMsg",errorMsg.toString());
                return false;
            }else{
                errorMsg.append("当前对会计期间的开启存在异常");
                resultMap.put("resultMsg",errorMsg.toString());
                return false;
            }
        }

        // 如果没问题，校验的同时就生成了凭证号了。 这里把createBy 创建人 设置为001 默认系统了
        voucherDTO = voucherService.setVoucher1(yearMonth, branchCode, branchCode, accbookCode, accbookType,"001");
        if(voucherDTO.getYearMonth() == null || "".equals(voucherDTO.getYearMonth())){
            errorMsg.append("当前账套信息下没有对应的凭证月");
            resultMap.put("resultMsg",errorMsg.toString());
            return false;
        }
        // 传过来的年月，需要判断当前月是否已经结转。
        List<?> objects = accMonthTraceRespository.queryAccMonthTraceByChooseMessage(branchCode, yearMonth, accbookType, accbookCode);
        if(objects.size() > 0){
            errorMsg.append("当前月已经进行结转不能再新增凭证");
            resultMap.put("resultMsg",errorMsg.toString());
            return false;
        }

        voucherDTO.setAccBookType(accbookType);
        voucherDTO.setAccBookCode(accbookCode);
        return  true;
    }


    /**
     * //金额数据赋值
     * @param partsStockIns
     * @param interfaceInfo
     * @param interfaceType
     * @param branchCode
     * @param list2
     * @param list3
     */
    private void processData(JsonToPartsStock jsonToPartsStock ,List<PartsStockIn> partsStockIns, String interfaceInfo, String interfaceType, String branchCode, List<VoucherDTO> list2, List<VoucherDTO> list3) {
        //获取数据再去数据库中比对信息是否存在。
        List<ConfigureManage> configureManages = configureManageRespository.queryConfigureManagesByInterfaceInfoAndInterfaceTypeAndBranchCode(interfaceInfo, interfaceType,branchCode);
        BigDecimal cost2 =BigDecimal.ZERO;

        for(int count = 0 ;count<partsStockIns.size()+1; count++){  //w p n

            VoucherDTO voucherDTO1 = new VoucherDTO();
            VoucherDTO voucherDTO2 = new VoucherDTO();
            String subjectName = "";
            String subjectInfo = "";
            String specialSuperCodes = "";
            String specialCode = "";
            PartsStockIn partsStockIn ;
            if(count < partsStockIns.size()){
                 partsStockIn = partsStockIns.get(count);
                if(partsStockIn.getPartsAnalysisCode().equals(Constant.PARTSANALYSISCODE_N_3)){
                    subjectName = configureManages.get(Constant.PARTSANALYSISCODE_N_NUM_3).getSubjectName();
                    subjectInfo = configureManages.get(Constant.PARTSANALYSISCODE_N_NUM_3).getId().getSubjectCode();
                    specialSuperCodes = configureManages.get(Constant.PARTSANALYSISCODE_N_NUM_3).getSpecialSuperCode().trim();
                    specialCode = configureManages.get(Constant.PARTSANALYSISCODE_N_NUM_3).getSpecialCode();
                }else if(partsStockIn.getPartsAnalysisCode().equals(Constant.PARTSANALYSISCODE_P_3)){
                    subjectName = configureManages.get(Constant.PARTSANALYSISCODE_P_NUM_3).getSubjectName();
                    subjectInfo = configureManages.get(Constant.PARTSANALYSISCODE_P_NUM_3).getId().getSubjectCode();
                    specialSuperCodes = configureManages.get(Constant.PARTSANALYSISCODE_P_NUM_3).getSpecialSuperCode().trim();
                    specialCode = configureManages.get(Constant.PARTSANALYSISCODE_P_NUM_3).getSpecialCode();
                }else if(partsStockIn.getPartsAnalysisCode().equals(Constant.PARTSANALYSISCODE_W_3)){
                    subjectName = configureManages.get(Constant.PARTSANALYSISCODE_W_NUM_3).getSubjectName();
                    subjectInfo = configureManages.get(Constant.PARTSANALYSISCODE_W_NUM_3).getId().getSubjectCode();
                    specialSuperCodes = configureManages.get(Constant.PARTSANALYSISCODE_W_NUM_3).getSpecialSuperCode().trim();
                    specialCode = configureManages.get(Constant.PARTSANALYSISCODE_W_NUM_3).getSpecialCode();
                }else { //测试是 发现有 W 出现 ，暂时先这么处理
                    subjectName = configureManages.get(Constant.PARTSANALYSISCODE_Z_NUM_3).getSubjectName();
                    subjectInfo = configureManages.get(Constant.PARTSANALYSISCODE_Z_NUM_3).getId().getSubjectCode();
                    specialSuperCodes = configureManages.get(Constant.PARTSANALYSISCODE_Z_NUM_3).getSpecialSuperCode().trim();
                    specialCode = configureManages.get(Constant.PARTSANALYSISCODE_Z_NUM_3).getSpecialCode();
                }
                String cost = new BigDecimal(partsStockIn.getQuantity()).multiply(partsStockIn.getPartsUnitCost()).toString();
                cost2 = cost2.add(new BigDecimal(cost));
                voucherDTO1.setDebit(cost);
                voucherDTO1.setCredit("0.00");
            }else{
                 partsStockIn = partsStockIns.get(0);
                subjectName = configureManages.get(Constant.PARTSANALYSISCODE_5_NUM_3).getSubjectName();
                subjectInfo = configureManages.get(Constant.PARTSANALYSISCODE_5_NUM_3).getId().getSubjectCode();
                specialSuperCodes = configureManages.get(Constant.PARTSANALYSISCODE_5_NUM_3).getSpecialSuperCode().trim();
                specialCode = configureManages.get(Constant.PARTSANALYSISCODE_5_NUM_3).getSpecialCode();
                //循环累加
                voucherDTO1.setDebit("0.00");
                voucherDTO1.setCredit(cost2.toString());
            }

            //TODO 可能需要替换
            voucherDTO1.setRemarkName(partsStockIn.getDnNo() == null ? "Dn号为空，请手工填入！" : partsStockIn.getDnNo());

            voucherDTO1.setSubjectCode(subjectInfo);
            voucherDTO1.setSubjectName(subjectName);

            voucherDTO2.setSubjectCodeS(subjectInfo);
            voucherDTO2.setSubjectNameS(subjectName);


            voucherDTO2.setSpecialSuperCodeS(specialSuperCodes);
            // 当前 专项信息配置一定注意顺序问题末级、一级一致。
            voucherDTO2.setSpecialCodeS(specialCode);
            list2.add(voucherDTO1);
            list3.add(voucherDTO2);
        }

        //类型 2 与类型1金额取反
        if(Constant.INTERFACETYPE_2.equals(interfaceType)) {
            for(VoucherDTO voucherDTO : list2){
                if(new BigDecimal(voucherDTO.getDebit()).compareTo(BigDecimal.ZERO) == 1 ){
                    voucherDTO.setDebit(voucherDTO.getCredit());
                    voucherDTO.setCredit("0.00");
                } else{
                    voucherDTO.setCredit(voucherDTO.getDebit());
                    voucherDTO.setDebit("0.00");
                }
            }
        }

    }



    /**
     * 保存凭证信息
     * @param stringObjectMap
     * @return
     */
    private String saveVoucher(Map<String, Object> stringObjectMap) {
        List<VoucherDTO> list2 = (List<VoucherDTO>) stringObjectMap.get("list2");
        List<VoucherDTO> list3 = (List<VoucherDTO>) stringObjectMap.get("list3");
        VoucherDTO dto1 = (VoucherDTO) stringObjectMap.get("dto");
        return voucherService.saveVoucherForFourS(list2, list3, dto1);
    }

    /**
     * 判断是否为success   是 返回 false 程序继续执行
     * @param resultMsg
     * @return
     */
    private boolean returnNoSuccess(String resultMsg) {
        if (!"success".equals(resultMsg)){
            logger.error(resultMsg);
            // 写个方法直接插入扔库里信息。
            return true;
        }
        return false;
    }

    /**
     * 返回失败原因
     * @param judgeMsg
     * @return
     */
    private boolean returnErrorInfo(String judgeMsg) {
        if(!"".equals(judgeMsg)){
            //TODO 将错误信息保存在错误日志信息表中。
            logger.error(judgeMsg);
            return true;
        }
        return false;
    }



    /**
     * 校验必要信息非空
     * @param jsonToPartsStock
     * @return
     */
    private String judgeInterfaceInfoQuerstion(JsonToPartsStock jsonToPartsStock,PartsStockIn partsStockIn){
        if(jsonToPartsStock.getCompanyNo() == null || "".equals(jsonToPartsStock.getCompanyNo())){
            errorMsg.append("机构编码不能为空");
        }
        if(jsonToPartsStock.getOperationDate() == null || "".equals(jsonToPartsStock.getOperationDate())){
            errorMsg.append("业务日期不能为空");
        }
        if(jsonToPartsStock.getTransactionType() == null || "".equals(jsonToPartsStock.getTransactionType())){
            errorMsg.append("业务类型不能为空|");
        }
        if(partsStockIn.getQuantity() == null ){
            errorMsg.append("入库数量不能为空|");
        }
        // || partsStockIn.getPartsUnitCost().compareTo(BigDecimal.ZERO) == -1
        if(partsStockIn.getPartsUnitCost() == null  ){
            errorMsg.append("配件单位成本不能为空|");
        }
        //dnNo（DN 号）
       /* if(partsStockIn.getDnNo()== null || "".equals(partsStockIn.getDnNo())){
            errorMsg.append("DN号不能为空|");
        }*/

        return  errorMsg.toString();
    }
}
