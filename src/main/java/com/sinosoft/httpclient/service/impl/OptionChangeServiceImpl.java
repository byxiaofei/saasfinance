package com.sinosoft.httpclient.service.impl;

import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.httpclient.domain.ConfigureManage;
import com.sinosoft.httpclient.domain.OptionChange;
import com.sinosoft.httpclient.repository.ConfigureManageRespository;
import com.sinosoft.httpclient.repository.OptionChangeRespository;
import com.sinosoft.httpclient.repository.TaskSchedulingDetailsInfoRespository;
import com.sinosoft.httpclient.service.OptionChangeService;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.service.VoucherService;
import com.sinosoft.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class OptionChangeServiceImpl implements OptionChangeService {

    private Logger logger = LoggerFactory.getLogger(OptionChangeServiceImpl.class);

    @Resource
    private OptionChangeRespository optionChangeRespository;

    @Resource
    private BranchInfoRepository branchInfoRepository;

//    @Resource  实体类禁止注入
//    private OptionChange optionChange;

    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;

    @Resource
    private SubjectRepository subjectRepository;

    @Resource
    private VoucherService voucherService;

    @Resource
    private TaskSchedulingDetailsInfoRespository taskSchedulingDetailsInfoRespository;

    // 4s 专有关联信息表。
    @Resource
    private ConfigureManageRespository configureManageRespository;

    @Transactional
    @Override
    public String saveoptionChangeListTest(List<OptionChange> optionChangeList) {
        optionChangeRespository.saveAll(optionChangeList);
        optionChangeRespository.flush();
        return "success";
    }

    @Override
    public String saveoptionChangeList(List<OptionChange> optionChangeList){
        List<Map<String,Object>> listResultMap = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();
        for (int i=0;i<optionChangeList.size();i++){
            OptionChange optionChange = optionChangeList.get(i);
            String judgeMsg =judgeInterfaceInfoQuerstion(optionChange,errorMsg);
            if (!"".equals(judgeMsg)){
                //TODO 将错误信息保存在错误日志信息表中。
                logger.error(judgeMsg);
                errorMsg.append("第i"+1+"的错误问题为："+judgeMsg);
                continue;
            }
            String sourceCode = optionChange.getSourceCode();
            String interfaceInfo ="9";
            if ("P".equals(sourceCode)){
                String interfaceType ="P1";
                Map<String,Object> stringObjectMap =converBussinessToAccounting(optionChange,errorMsg,interfaceInfo,interfaceType);
                String resultMsg =(String)stringObjectMap.get("resultMsg");
                if (!"success".equals(resultMsg)){
                    errorMsg.append("第i"+1+"Invoice类型错误问题为："+judgeMsg);
                    return "fail";
                }
                interfaceType = "P2";
                Map<String,Object> stringObjectMap1 = converBussinessToAccounting(optionChange,errorMsg,interfaceInfo,interfaceType);
                String resultMsg1 = (String) stringObjectMap1.get("resultMsg");
                if (!"success".equals(resultMsg1)){
                    logger.error(resultMsg1);
                    // 写个方法直接插入扔库里信息。
                    return "fail";
                }
                listResultMap.add(stringObjectMap);
                listResultMap.add(stringObjectMap1);
            }else {
                    String interfaceType = "F1";
                    Map<String,Object> stringObjectMap = converBussinessToAccounting(optionChange,errorMsg,interfaceInfo,interfaceType);
                    String resultMsg =(String)stringObjectMap.get("resultMsg");
                    if (!"success".equals(resultMsg)){
                        logger.error(resultMsg);
                        return "fail";
                    }
                interfaceType = "F2";
                Map<String,Object> stringObjectMap1 = converBussinessToAccounting(optionChange,errorMsg,interfaceInfo,interfaceType);
                String resultMsg1 =(String)stringObjectMap1.get("resultMsg");
                if (!"success".equals(resultMsg1)){
                    logger.error(resultMsg1);
                    return "fail";
                }
                listResultMap.add(stringObjectMap);
                listResultMap.add(stringObjectMap1);
            }
        }
        System.out.println("---------------《当前时间范围内的正确的数据，已全部保存到List集合中，下面开始保存入库！》------------------");
        // 当前时间范围内解析到的所以数据放入到对应的对接接口表中存放信息。
        for(int i = 0 ; i < listResultMap.size(); i++ ){
            Map<String, Object> stringObjectMap = listResultMap.get(i);
            List<VoucherDTO> list2 = (List<VoucherDTO>) stringObjectMap.get("list2");
            List<VoucherDTO> list3 = (List<VoucherDTO>) stringObjectMap.get("list3");
            VoucherDTO dto = (VoucherDTO) stringObjectMap.get("dto");
            String voucherNo = voucherService.saveVoucherForFourS(list2, list3, dto);
            if(!"success".equals(voucherNo)){
                logger.error(voucherNo);
                errorMsg.append("保存凭证出错");
            }
        }
        optionChangeRespository.saveAll(optionChangeList);
        optionChangeRespository.flush();
        System.out.println("---------------------当前时间范围内的数据，已经全部保存到对接接口表中---------------------------");
        // TODO  最后接口改造，需要传入--> 起止时间 ，并把起止时间保存到 任务调度明细表中 (taskschedulingdetailsinfo)。
        return "success";
    }

    private String judgeInterfaceInfoQuerstion(OptionChange optionChange,StringBuilder errorMsg){
        if(optionChange.getCompanyNo() == null || "".equals(optionChange.getCompanyNo())){
            errorMsg.append("机构编码不能为空");
        }
        if(optionChange.getOperationDate() == null || "".equals(optionChange.getOperationDate())){
            errorMsg.append("业务日期不能为空");
        }
        if (optionChange.getPrice()==null||"".equals(optionChange.getPrice())){
            errorMsg.append("价格不能为空");
        }
        if (optionChange.getTransactionType()==null||"".equals(optionChange.getTransactionType())){
            errorMsg.append("业务类型不能为空");
        }
        if (optionChange.getSourceCode()==null||"".equals(optionChange.getSourceCode())){
            errorMsg.append("选装件类型不能为空");
        }
        return errorMsg.toString();
    }

    private Map<String,Object> converBussinessToAccounting(OptionChange optionChange,StringBuilder errorMsg,String interfaceInfo,String interfaceType){
        Map<String,Object> resultMap = new HashMap<>();
        VoucherDTO dto = new VoucherDTO();
        String branchCode = optionChange.getCompanyNo(); //当前机构的编码
        List<Map<String,Object>> branchMsg =branchInfoRepository.checkExistsComCode(branchCode);
        if (branchMsg.size()<=0){
            errorMsg.append("机构单位编码信息匹配不正确,请检查映射关系");
            resultMap.put("resultMap",errorMsg.toString());
            return resultMap;
        }
        if (branchMsg.size()>0){
            Map<String, Object> stringObjectMap = branchMsg.get(0);
            String flagMsg  = (String) stringObjectMap.get("flag");
            if("0".equals(flagMsg)){
                errorMsg.append(branchCode+"机构已经停用");
                resultMap.put("resultMsg",errorMsg.toString());
                return resultMap;
            }
        }
        Date opertionDate =optionChange.getOperationDate();  //凭证日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(opertionDate);
        String yearMonthDate = DateUtil.getDateTimeFormatToGeneralLedger(date);
            if (!"success".equals(yearMonthDate)){
                errorMsg.append("制单日期的格式为：yyyy-MM-dd,请重新校验");
                resultMap.put("resultMsg",errorMsg.toString());
                return resultMap;
            }
         String dateReplace = date.replaceAll("-","");
        //会计期间
            String yearMonth =dateReplace.substring(0, 6);

            // 需要通过机构找到账套信息，账套类型。来找到对应的凭证号。
            String accbookCode = "";
            String accbookType = "";
            Map<Integer,Object> params = new HashMap<>();
            int paramNo = 1;
            StringBuffer sql1 = new StringBuffer();
            sql1.append("select acc.account_type as accountType , acc.account_code as accountCode  from accountinfo acc inner join branchaccount ba join branchinfo b  on acc.id = ba.account_id  and b.id = ba.branch_id where 1=1 and b.com_code = ?"+paramNo);
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
//            errorMsg = errorMsg + "当前账套信息下没有对应的凭证月"+",";
                errorMsg.append("当前账套信息下没有对应的凭证月");
                resultMap.put("resultMsg",errorMsg.toString());
                return resultMap;
            }
            // 传过来的年月，需要判断当前月是否已经结转。
            List<?> objects = accMonthTraceRespository.queryAccMonthTraceByChooseMessage(centerCode, yearMonth, accbookType, accbookCode);
            if(objects.size() > 0){
//            errorMsg = errorMsg + "当前月已经进行结转不能再新增凭证"+",";
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

            List<ConfigureManage> configureManages =configureManageRespository.queryConfigureManagesByInterfaceInfoAndInterfaceTypeAndBranchCode(interfaceInfo,interfaceType,branchCode);
            for (int i=0;i<configureManages.size();i++){
                //在这里意为entry的分录信息一样
                VoucherDTO voucherDTO1 =new VoucherDTO();
                VoucherDTO voucherDTO2 = new VoucherDTO();
                //校验科目
                String subjectName = configureManages.get(i).getSubjectName();
                String subjectInfo = configureManages.get(i).getId().getSubjectCode();
                String resultCode = checkSubjectCodePassMusterBySubjectCodeAll(subjectInfo,accbookCode);
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
                if ("P2".equals(interfaceType)){
                        voucherDTO1.setDebit("1956.00");
                        voucherDTO1.setCredit("0.0");
                    }else if ("P1".equals(interfaceType)){
                        voucherDTO1.setDebit("0.0");
                        voucherDTO1.setCredit("1956.00");
                    }
                if ("F1".equals(interfaceType)){
                        voucherDTO1.setDebit("0.0");
                        voucherDTO1.setCredit("1500.00");
                    }else if ("F2".equals(interfaceType)){
                        voucherDTO1.setDebit("1500.00");
                        voucherDTO1.setCredit("0.0");
                    }
                voucherDTO1.setRemarkName(optionChange.getVin());
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
}
