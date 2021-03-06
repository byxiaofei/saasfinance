package com.sinosoft.httpclient.service.impl;

import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.httpclient.domain.ConfigureManage;
import com.sinosoft.httpclient.domain.Constant;
import com.sinosoft.httpclient.domain.VehicleInvoice;
import com.sinosoft.httpclient.domain.VehicleStock;
import com.sinosoft.httpclient.repository.ConfigureManageRespository;
import com.sinosoft.httpclient.repository.VehicleStockRespository;
import com.sinosoft.httpclient.service.VehicleStockService;
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
public class VehicleStockServiceImpl implements VehicleStockService {


    private Logger logger = LoggerFactory.getLogger(VehicleStockServiceImpl.class);

    @Resource
    private VehicleStockRespository vehicleStockRespository;

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
     * 保存车辆库存变动接口返回数据
     * @param vehicleStockList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String savevehicleStockList(List<VehicleStock> vehicleStockList,String loadTime) {
        //保存接口数据
        saveinterface(vehicleStockList);
        // 1 拿到解析数据，直接进行解析处理
        for(VehicleStock vehicleStock: vehicleStockList ){
             errorMsg = new StringBuilder();
            // 校验必要信息非空
            String judgeMsg = judgeInterfaceInfoQuerstion(vehicleStock);
            //失败结束，返回fail
            if (returnErrorInfo(judgeMsg)) {
                //记录失败日志
                interfaceInfoService.failSave("system",loadTime,errorMsg.toString());
                continue;
            }

            //根据业务类型处理分录信息
            if (manageTransactionType(vehicleStock, Constant.INTERFACEINFO_1)) {
                //记录失败日志
                interfaceInfoService.failSave("system",loadTime,errorMsg.toString());
                continue;
            }
        }

        //保存日志
        return "success";
    }

    /**
     * 根据业务类型处理分录信息
     * @param vehicleStock
     * @param interfaceInfo 接口标识  此处为第一个接口
     * @return
     */
    private boolean manageTransactionType(VehicleStock vehicleStock, String interfaceInfo) {
        if(Constant.STOCK_IN_1.equals(vehicleStock.getTransactionType())){
            //将接口数据信息转凭证
            Map<String,Object> stringObjectMap = convertBussinessToAccounting(vehicleStock,interfaceInfo,Constant.INTERFACETYPE_1);
            String resultMsg = (String) stringObjectMap.get("resultMsg");
            if (returnNoSuccess(resultMsg)) return true;
            String voucherNo = saveVoucher(stringObjectMap);
            if (returnNoSuccess(voucherNo)) return true;

        }else if(Constant.STOCK_OUT_1.equals(vehicleStock.getTransactionType())){
            Map<String,Object> stringObjectMap = convertBussinessToAccounting(vehicleStock,interfaceInfo,Constant.INTERFACETYPE_2);
            String resultMsg = (String) stringObjectMap.get("resultMsg");
            if (returnNoSuccess(resultMsg)) return true;
            String voucherNo = saveVoucher(stringObjectMap);
            if (returnNoSuccess(voucherNo)) return true;
        }else if(Constant.COST_CHANGE_1.equals(vehicleStock.getTransactionType())){
            Map<String,Object> stringObjectMap = convertBussinessToAccounting(vehicleStock,interfaceInfo,Constant.INTERFACETYPE_3);
            String resultMsg = (String) stringObjectMap.get("resultMsg");
            if (returnNoSuccess(resultMsg)) return true;
            String voucherNo = saveVoucher(stringObjectMap);
            if (returnNoSuccess(voucherNo)) return true;
        }
        return false;
    }

    //将接口数据信息转凭证
    private Map<String,Object> convertBussinessToAccounting(VehicleStock vehicleStock,String interfaceInfo,String interfaceType){
        resultMap = new HashMap<>();
        VoucherDTO dto = new VoucherDTO();
        String branchCode = vehicleStock.getCompanyNo();//为当前的机构编码
        String centerCode = branchCode;
        voucherDTO = new VoucherDTO();
        String operationDate = vehicleStock.getOperationDate();//凭证的日期
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
        processData(vehicleStock, interfaceInfo, interfaceType, branchCode, list2, list3);
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
     * @param vehicleStock
     * @param interfaceInfo
     * @param interfaceType
     * @param branchCode
     * @param list2
     * @param list3
     */
    private void processData(VehicleStock vehicleStock, String interfaceInfo, String interfaceType, String branchCode, List<VoucherDTO> list2, List<VoucherDTO> list3) {
        //获取数据再去数据库中比对信息是否存在。
        List<ConfigureManage> configureManages = configureManageRespository.queryConfigureManagesByInterfaceInfoAndInterfaceTypeAndBranchCode(interfaceInfo, interfaceType,branchCode);
        // 这里科目信息开始已经有顺序了。
        for (int i = 0; i < configureManages.size(); i++) {
            // 当前这里意为：entry的分录信息一样
            VoucherDTO voucherDTO1 = new VoucherDTO();
            VoucherDTO voucherDTO2 = new VoucherDTO();
            // 对科目的校验
            String subjectName = configureManages.get(i).getSubjectName();
            String subjectInfo = configureManages.get(i).getId().getSubjectCode();
            String cost = vehicleStock.getVehicleCurrentCost().toString();
            // 通过接口类型来区分，金额的走向。
            if (Constant.INTERFACETYPE_1.equals(interfaceType)) {
                // 不一样的接口+接口类型，这里判断的金额不同。
                if (i + 1 == 1) {
                    voucherDTO1.setDebit(cost);
                    voucherDTO1.setCredit("0.00");
                } else if (i + 1 == 2) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(cost);
                } else if (i + 1 == 3) {
                    voucherDTO1.setDebit("1.00");
                    voucherDTO1.setCredit("0.00");
                } else if (i + 1 == 4) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit("1.00");
                } else if (i + 1 == 5) {
                    voucherDTO1.setDebit("1.00");
                    voucherDTO1.setCredit("0.00");
                } else if (i + 1 == 6) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit("1.00");
                }
            } else if (Constant.INTERFACETYPE_2.equals(interfaceType)) {
                // 不一样的接口+接口类型，这里判断的金额不同。
                if (i == 0) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit(cost);
                } else if (i == 1) {
                    voucherDTO1.setDebit(cost);
                    voucherDTO1.setCredit("0.00");
                } else if (i + 1 == 2) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit("1.00");
                } else if (i + 1 == 3) {
                    voucherDTO1.setDebit("1.00");
                    voucherDTO1.setCredit("0.00");
                } else if (i + 1 == 4) {
                    voucherDTO1.setDebit("0.00");
                    voucherDTO1.setCredit("1.00");
                } else if (i + 1 == 5) {
                    voucherDTO1.setDebit("1.00");
                    voucherDTO1.setCredit("0.00");
                }
            }else if (Constant.INTERFACETYPE_3.equals(interfaceType)){
                //变动成本大于 0 增加车辆成本
               if(vehicleStock.getVehicleCostChange().compareTo(BigDecimal.ZERO) == 1){
                   if (i == 0) {
                       voucherDTO1.setDebit(cost);
                       voucherDTO1.setCredit("0.00");
                   } else if (i == 1) {
                       voucherDTO1.setDebit("0.00");
                       voucherDTO1.setCredit(cost);
                   }
               }else{
                   if (i == 0) {
                       voucherDTO1.setDebit("0.00");
                       voucherDTO1.setCredit(cost);
                   } else if (i == 1) {
                       voucherDTO1.setDebit(cost);
                       voucherDTO1.setCredit("0.00");
                   }
               }

            }

            //vin（美版底盘号）如果为空commissionNo 生产号
            if(vehicleStock.getVin()==null || vehicleStock.getVin().equals("")){
                voucherDTO1.setRemarkName(vehicleStock.getCommissionNo());
            }else{
                voucherDTO1.setRemarkName(vehicleStock.getVin());
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
            logger.error(judgeMsg);
            return true;
        }
        return false;
    }

    /**
     * 保存接口数据
     * @param vehicleStockList
     */
    private void saveinterface(List<VehicleStock> vehicleStockList) {
        vehicleStockRespository.saveAll(vehicleStockList);
        vehicleStockRespository.flush();
    }

    /**
     * 校验必要信息非空
     * @param vehicleStock
     * @return
     */
    private String judgeInterfaceInfoQuerstion(VehicleStock vehicleStock){
        if(vehicleStock.getCompanyNo() == null || "".equals(vehicleStock.getCompanyNo())){
            errorMsg.append("机构编码不能为空");
        }
        if(vehicleStock.getOperationDate() == null || "".equals(vehicleStock.getOperationDate())){
            errorMsg.append("业务日期不能为空");
        }
        if(vehicleStock.getVehicleCurrentCost() == null || "".equals(vehicleStock.getVehicleCurrentCost())){
            errorMsg.append("车辆最新成本不能为空");
        }
        //vin（美版底盘号）如果为空 commissionNo 生产号
        if(vehicleStock.getCommissionNo() == null || "".equals(vehicleStock.getCommissionNo())){
            errorMsg.append("生产号不能为空");
        }
        if(vehicleStock.getTransactionType() == null || "".equals(vehicleStock.getTransactionType())){
            errorMsg.append("业务类型不能为空");
        }
        return  errorMsg.toString();
    }
}
