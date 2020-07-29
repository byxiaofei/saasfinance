package com.sinosoft.service.impl;

import com.sinosoft.common.WebServiceResult;
import com.sinosoft.domain.*;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.dto.parsing.Entry;
import com.sinosoft.dto.parsing.VoucherHeader;
import com.sinosoft.dto.parsing.XMLParsingDTO;
import com.sinosoft.repository.*;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.service.DataDockingService;
import com.sinosoft.service.VoucherService;
import com.sinosoft.util.DateUtil;
import com.sinosoft.util.XMLUtil;
import freemarker.template.SimpleDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jws.WebService;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: luodejun
 * @Date: 2020/5/11 11:04
 * @Description:
 */
@WebService(name = "DataDockingService",// 暴露服务名称
        targetNamespace = "http://service.sinosoft.com/", // 命名空间，一般是接口的包名倒叙
        endpointInterface = "com.sinosoft.service.DataDockingService") //终点接口路径
@Service
public class DataDockingServiceImpl implements DataDockingService {

    private static final String CHARSET = "UTF-8";

    private Logger logger = LoggerFactory.getLogger(DataDockingServiceImpl.class);

    @Resource
    private VoucherService voucherService;
    // 接口日志表信息插入
    @Resource
    private InterfaceInfoRespository interfaceInfoRespository;
    // codeManage
    @Resource
    private CodeSelectRepository codeSelectRepository;
    // 账套信息
    @Resource
    private AccountInfoRepository accountInfoRepository;
    // 机构信息
    @Resource
    private BranchInfoRepository branchInfoRepository;
    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;
    @Resource
    private SubjectRepository subjectRepository;
    @Resource
    private SpecialInfoRepository specialInfoRepository;
    @Resource
    private AccSegmentDefineRespository accSegmentDefineRespository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String receiveInformation(String xml) {
        //  1. 传入为我们定义好的文件里面的数据类型。并解析

        VoucherDTO dto = new VoucherDTO();
        StringBuffer errorMsg = new StringBuffer();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String systemDate = sdf.format(date);
        Object o = XMLUtil.convertXmlStrToObject(XMLParsingDTO.class, xml);
        XMLParsingDTO xmlParsingDTO = (XMLParsingDTO) o;
        try {    //  当前少地址路径。
            String fileName = xmlParsingDTO.getVoucherHeader().getFileName();
            int i = fileName.indexOf("_");
            String substringFileName = fileName.substring(0, i);
            if ("FK".equals(substringFileName)) {
                //  费控存放在费控的文件夹下
//                XMLUtil.convertToXml(xmlParsingDTO, "E:\\test\\xml文件解析示例。");
            } else if ("SST".equals(substringFileName)) {
//                int j = fileName.lastIndexOf(".");
//                String substring = fileName.substring(0, j);
//                XMLUtil.convertToXml(xmlParsingDTO, "E:\\test\\xml文件解析示例。");
            }else{
                // 不存。
            }
            //  2.判断解析的数据必要数据是否都存在
            String judgeMsg = judgeXmlMsgQuestion(xmlParsingDTO, errorMsg);
            // 若返回的为"" 即为必要数据全部都有，若不是，将直接返回信息，并插入到接口日志表中
            if (!"".equals(judgeMsg)) {
                // 1. 第一比信息出错直接存库。
                logger.error("当前解析凭证的必要信息不足！");
                logger.error("错误信息为："+judgeMsg);
                InterfaceInfo interfaceInfo = new InterfaceInfo();
                interfaceInfo.setBranchCode(xmlParsingDTO.voucherHeader.getBranchCode());
                interfaceInfo.setResultInfo("fail");
                interfaceInfo.setLoadTime(systemDate);
                String format = sdf.format(System.currentTimeMillis());
                interfaceInfo.setPolicDate(format);
                interfaceInfo.setFileName(xmlParsingDTO.voucherHeader.getFileName());
                // 返回信息，并生成xml文件。
                interfaceInfoRespository.save(interfaceInfo);
                interfaceInfoRespository.flush();
                WebServiceResult failure = WebServiceResult.failure(judgeMsg);
                return XMLUtil.convertToXml(failure);
            }

            // 3.针对入库系统进行需校验一下，看是否与总账系统信息匹配。(由费控那边进行维护)，并把对应信息放入map中
            Map<String, Object> stringObjectMap = compareGeneralLedgerData(xmlParsingDTO, errorMsg);
            // 拿出校验信息。
            String resultMsg = (String) stringObjectMap.get("resultMsg");
            // 只要不是"success" 就是有问题的。直接返回
            if (!"success".equals(resultMsg)){
                logger.error(resultMsg);
                InterfaceInfo interfaceInfo = new InterfaceInfo();
                interfaceInfo.setBranchCode(xmlParsingDTO.voucherHeader.getBranchCode());
                interfaceInfo.setResultInfo("fail");
                interfaceInfo.setLoadTime(systemDate);
                String format = sdf.format(System.currentTimeMillis());
                interfaceInfo.setPolicDate(format);
                interfaceInfo.setFileName(xmlParsingDTO.voucherHeader.getFileName());
                interfaceInfoRespository.save(interfaceInfo);
                interfaceInfoRespository.flush();
                WebServiceResult failure = WebServiceResult.failure(resultMsg);
                // 写个方法直接插入扔库里信息。
                return XMLUtil.convertToXml(failure);
            }
            List<VoucherDTO> list2 = (List<VoucherDTO>) stringObjectMap.get("list2");
            List<VoucherDTO> list3 = (List<VoucherDTO>) stringObjectMap.get("list3");
            VoucherDTO dto1 = (VoucherDTO) stringObjectMap.get("dto");
            //  该保存入库了。
            String voucherNo = voucherService.saveVoucher1(list2, list3, dto1);
            WebServiceResult msg = WebServiceResult.success(voucherNo);
            //  接口日志信息保存成功
            InterfaceInfo interfaceInfo = new InterfaceInfo();
            interfaceInfo.setBranchCode(xmlParsingDTO.voucherHeader.getBranchCode());
            interfaceInfo.setResultInfo("success");
            interfaceInfo.setLoadTime(systemDate);
            String format = sdf.format(System.currentTimeMillis());
            interfaceInfo.setPolicDate(format);
            interfaceInfo.setFileName(xmlParsingDTO.getVoucherHeader().getFileName());
            interfaceInfoRespository.save(interfaceInfo);
            interfaceInfoRespository.flush();
            //  返回的就是当前保存的凭证号。
            return XMLUtil.convertToXml(msg);
        } catch (Exception e) {
            logger.error("凭证对接异常,当前异常凭证文件为："+xmlParsingDTO.getVoucherHeader().getFileName(),e);
            e.printStackTrace();
            WebServiceResult failure = WebServiceResult.failure("凭证对接异常，请联系管理员...");
            return XMLUtil.convertToXml(failure);
        }

    }

    // 看解析的出的必要信息是否存在。
    private String judgeXmlMsgQuestion(XMLParsingDTO xml,StringBuffer errorMsg){
        VoucherHeader voucherHeader = xml.getVoucherHeader();
        // 获取凭证头公司节点
        String company = voucherHeader.getCompany();
        if ("".equals(company) || company == null) {
            errorMsg.append("公司标签不能为空,");
        }
        //  操作人代码
        String createBy = voucherHeader.getCreateBy();
        if ("".equals(createBy) || createBy == null) {
            // 新增方法进行错误信息的插入表
            errorMsg.append("操作人信息不能为空,");
        }
        //  凭证类型
        String voucherType = voucherHeader.getVoucherType();
        if ("".equals(voucherType) || voucherType == null) {
            errorMsg.append("凭证类型不能为空,");
        }
        String voucharDate = voucherHeader.getVoucharDate();
        if ("".equals(voucharDate) || voucharDate == null) {
            errorMsg.append("制单日期不能为空,");
        }
        //  会计期间
        String yearMonth = voucherHeader.getYearMonth();
        if ("".equals(yearMonth) || yearMonth == null) {
            errorMsg.append("会计期间不能为空,");
        }
        //  附件
        String auxNumber = voucherHeader.getAuxNumber();
        if ("".equals(auxNumber) || auxNumber == null) {
            errorMsg.append("附件不能为空,");
        }
        //  凭证提体信息条数
        String entryNumber = voucherHeader.getEntryNumber();
        //  财套类型
        String accbookType = voucherHeader.getAccbookType();
        if ("".equals(accbookType) || accbookType == null) {
            errorMsg.append("账套类型不能为空,");
        }
        //财套编码
        String accbookCode = voucherHeader.getAccbookCode();
        if ("".equals(accbookCode) || accbookCode == null) {
            errorMsg.append("账套编码不能为空,");
        }
        //  业务机构
        String branchCode = voucherHeader.getBranchCode();
        if ("".equals(branchCode) || branchCode == null) {
            errorMsg.append("业务机构不能为空,");
        }
        //  核算中心
        String centerCode = voucherHeader.getCenterCode();
        if ("".equals(centerCode) || centerCode == null) {
            errorMsg.append("核算中心不能为空,");
        }
        //  文件名称
        String fileName = voucherHeader.getFileName();
        if("".equals(fileName) || fileName == null){
            errorMsg.append("文件名称不能为空,");
        }

        String generateWay = voucherHeader.getGenerateWay();
        if("".equals(generateWay) || generateWay == null){
            errorMsg.append("凭证记账方式不能为空,");
        }

        String dataSource = voucherHeader.getDataSource();
        if("".equals(dataSource) || dataSource == null){
            errorMsg.append("数据来源方式不能为空,");
        }

        //  借方总金额
        BigDecimal deditNumber = new BigDecimal("0.00");
        //  贷方总金额
        BigDecimal creditNumber = new BigDecimal("0.00");

        List<Entry> entrys = xml.getVoucherBody().getEntry();
        if(entrys.size() < 2 ){
            errorMsg.append("当前凭证分录条数不足,");
        }
        for(int i = 0 ; i < entrys.size() ; i++){
            //  摘要
            Entry entry = entrys.get(i);
            String remarkName = entry.getRemarkName();
            if ("".equals(remarkName) || remarkName == null) {
                errorMsg.append("第" + (i + 1) + "条摘要不能为空,");
            }
            //  科目代码 需要进行数据库进行校验。
            String subjectCode = entry.getSubjectCode();
            if("".equals(subjectCode) || subjectCode == null){
                errorMsg.append("第" + (i + 1) + "条科目代码不能为空,");
            }
            //  科目名称
            String subjectName = entry.getSubjectName();
            if("".equals(subjectName) || subjectName == null){
                errorMsg.append("第" + (i + 1) + "条科目名称不能为空,");
            }
            //  借方金额
            String debit = entry.getDebit();
            if("".equals(debit) || debit == null){
                errorMsg.append("第" + (i + 1) + "条借方金额不能为空,");
            }
            //  借方金额汇总
            deditNumber = deditNumber.add(new BigDecimal(debit));
            //  贷方金额
            String credit = entry.getCredit();
            if("".equals(credit) || credit == null){
                errorMsg.append("第" + (i + 1) + "条贷方金额不能为空,");
            }
            creditNumber = creditNumber.add(new BigDecimal(credit));
        }
        if(deditNumber.compareTo(creditNumber) != 0){
            errorMsg.append("借贷总金额不相等,");
        }
        return errorMsg.toString();
    }

    //   对数据进行校验。并放入map集合中。
    private Map<String,Object> compareGeneralLedgerData(XMLParsingDTO xml,StringBuffer errorMsg){
        // 验证映射校验信息表。
        Map<String,Object> resultMap = new HashMap<>();
        VoucherDTO dto = new VoucherDTO();
        String createBy  = xml.getVoucherHeader().getCreateBy();
        String accbookCode = xml.getVoucherHeader().getAccbookCode();
        String accbookType = xml.getVoucherHeader().getAccbookType();
        List<Map<String, Object>> accountMsg = accountInfoRepository.checkExistsAccountCode(accbookType, accbookCode);
        if(accountMsg.size() <= 0 ){
            errorMsg.append("账套编码和类型信息匹配不正确检查映射关系");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        //可以用hql语句。
//        List<AccountInfo> byAccountTypeAndAccountCode = (List<AccountInfo>) accountInfoRepository.findByAccountTypeAndAccountCode(accbookType, accbookCode);

        if(accountMsg.size()>0){
            Map<String, Object> stringObjectMap = accountMsg.get(0);
            String useFlagMsg = (String)stringObjectMap.get("useFlag");
            if(useFlagMsg == null ||"0".equals(useFlagMsg)){
//                errorMsg = errorMsg + "当前账套已经停用"+",";
                errorMsg.append("当前账套已经停用");
                resultMap.put("resultMsg",errorMsg.toString());
                return resultMap;
            }
        }
        String branchCode = xml.getVoucherHeader().getBranchCode();
        String centerCode = xml.getVoucherHeader().getCenterCode();
        if(!branchCode.equals(centerCode)){
//            errorMsg = errorMsg + "基层单位和核算单位的信息不一致"+",";
            errorMsg.append("基层单位和核算单位的信息不一致,");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        List<Map<String, Object>> branchMsg = branchInfoRepository.checkExistsComCode(branchCode);
        if(branchMsg.size() <= 0){
//            errorMsg = errorMsg + "机构单位编码信息匹配不正确检查映射关系"+",";
            errorMsg.append("机构单位编码信息匹配不正确检查映射关系");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        if(branchMsg.size() > 0){
            Map<String, Object> stringObjectMap = branchMsg.get(0);
            String flagMsg  = (String) stringObjectMap.get("flag");
            if("0".equals(flagMsg)){
//                errorMsg = errorMsg + branchCode+"机构已经停用"+",";
                errorMsg.append(branchCode+"机构已经停用");
                resultMap.put("resultMsg",errorMsg.toString());
                return resultMap;
            }
        }
        Map<Integer,Object> params = new HashMap<>();
        int paramNo = 1;
        StringBuffer sql1 = new StringBuffer();
        sql1.append("select acc.account_type , acc.account_code ,acc.account_name ,b.com_code , b.com_name  from accountinfo acc inner join branchaccount ba join branchinfo b  on acc.id = ba.account_id  and b.id = ba.branch_id where acc.account_code = ?"+paramNo);
        params.put(paramNo,accbookCode);
        paramNo++;
        sql1.append(" and acc.account_type = ?"+paramNo);
        params.put(paramNo,accbookType);
        paramNo++;
        sql1.append(" and b.com_code = ?"+paramNo);
        params.put(paramNo,branchCode);
        paramNo++;
        List<?> checkMsg = branchInfoRepository.queryBySqlSC(sql1.toString(), params);
        if(checkMsg.size() <= 0){
//            errorMsg = errorMsg + accbookCode+"账套与"+branchCode+"机构并无关联关系"+",";
            errorMsg.append(accbookCode+"账套与"+branchCode+"机构并无关联关系");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }

        //  数据来源校验是否与数据库匹配
        String dataSource = xml.getVoucherHeader().getDataSource();
        List<Map<String, Object>> systemSource = codeSelectRepository.findCodeSelectInfo("systemSource", dataSource);
        if(systemSource.size() <= 0){
//            errorMsg = errorMsg + "数据来源选择有误，并无映射关系"+",";
            errorMsg.append("数据来源选择有误，并无映射关系");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        //  凭证录入方式是否为自动(1)
        String generateWay = xml.getVoucherHeader().getGenerateWay();
        List<Map<String, Object>> generateWay1 = codeSelectRepository.findCodeSelectInfo("generateWay", generateWay);
        if(generateWay1.size() <= 0){
//            errorMsg = errorMsg + "凭证录入方式与数据库方式映射不符"+",";
            errorMsg.append("凭证录入方式与数据库方式映射不符");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }

        //  凭证类型校验是否与数据库匹配
        String voucherType = xml.getVoucherHeader().getVoucherType();
        List<Map<String, Object>> voucherType1 = codeSelectRepository.findCodeSelectInfo("voucherType", voucherType);
        if(voucherType1.size() <= 0){
//            errorMsg = errorMsg + "凭证类型与数据库映射信息不符"+",";
            errorMsg.append("凭证类型与数据库映射信息不符");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }

        String voucharDate = xml.getVoucherHeader().getVoucharDate();// 凭证的日期，是按照实际的记账日期来填写的，与凭证号无关。
        String dateTimeFormatToGeneralLedger = DateUtil.getDateTimeFormatToGeneralLedger(voucharDate);
        if(!"success".equals(dateTimeFormatToGeneralLedger)){
            errorMsg.append("制单日期的格式为：yyyy-MM-dd，请重新校验");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        String yearMonth = xml.getVoucherHeader().getYearMonth();
        String dateTimeFormatToGeneralLedger1 = DateUtil.getDateTimeFormatToGeneralLedger(yearMonth);
        if(!"success".equals(dateTimeFormatToGeneralLedger1)){
            errorMsg.append("会计期间的格式为：yyyyMM，请重新校验");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        List<Map<String, Object>> voucherTypeInfo = codeSelectRepository.findByComTypeAsc("voucherType", voucherType);
        if(voucherTypeInfo.size() <= 0){
//            errorMsg = errorMsg+"没有对应的凭证类型"+",";
            errorMsg.append("没有对应的凭证类型");
            resultMap.put("resultMsg",errorMsg.toString());
            return resultMap;
        }
        // 如果没问题，校验的同时就生成了凭证号了。
        VoucherDTO voucherDTO = voucherService.setVoucher1(yearMonth, centerCode, branchCode, accbookCode, accbookType,createBy);
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

        // 对凭证月的信息必须要保证，制单日期，在凭证月内。
        // 以下是处理方式
        String voucherDateReplace = voucharDate.replaceAll("-","");
        String substringVoucherDateOfMonth = voucherDateReplace.substring(0, 6);
        //  1. 如果会计月和制单日期相同，那么直接塞入制单日期。
        //  2. 如果会计月和制单日期不同，那么取当前系统的日期，若当前系统日期是在会计月中，那么把当前系统日期作为制单日期放入对象，如果不是，取会计月的最后一天做为制单日期放入对象
        if (yearMonth.equals(substringVoucherDateOfMonth)) {
            // 日期
            dto.setVoucherDate(voucharDate);
        }else{
            Date date = new Date();
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
            String systemTime = sdf.format(date);
            String systemTimeYearMonth = sdf1.format(date);
            if(yearMonth.equals(systemTimeYearMonth)){
                // 日期
                dto.setVoucherDate(systemTime);
            }else{
                int year = Integer.parseInt(yearMonth.substring(0, 4));
                int month = Integer.parseInt(yearMonth.substring(4, 6));
                String lastDayOfMonth = DateUtil.getLastDayOfMonth(year, month);
                // 日期
                dto.setVoucherDate(lastDayOfMonth);
            }
        }
        String auxNumber = xml.getVoucherHeader().getAuxNumber();

        // 存放到dto中。
        //  凭证号
        dto.setVoucherNo(voucherDTO.getVoucherNo());
        //  年月
        dto.setYearMonth(yearMonth);

        //  附件
        dto.setAuxNumber(auxNumber);
        //  操作人
        dto.setCreateBy(createBy);
        dto.setAccBookCode(accbookCode);
        dto.setAccBookType(accbookType);
        dto.setBranchCode(branchCode);
        dto.setCenterCode(centerCode);
        //  凭证类型
        dto.setVoucherType(voucherType);
        dto.setDataSource(dataSource);
        dto.setGenerateWay(generateWay);
        resultMap.put("dto",dto);

        List<VoucherDTO> list2 = new ArrayList<>();
        List<VoucherDTO> list3 = new ArrayList<>();

        List<Entry> entrys = xml.getVoucherBody().getEntry();
        // 优化： 把所有的一级专项拿出来，根据账套拿出所有的一级专项。存放到Map集合中。
        // key 为 id ，value 为 一级专项
        Map<String,String> specialMaps = new HashMap<>();
        // key 为 一级专项 ， value 为S段。
        Map<String,String> segmentMaps = new HashMap<>();
        List<Map<String,Object>> maps = specialInfoRepository.querySpecialInfoOneLevel(accbookCode);
        for(Map<String,Object> mapInfo : maps){
            specialMaps.put(String.valueOf(mapInfo.get("text")) , String.valueOf(mapInfo.get("value")));// 当做key
        }
        List<AccSegmentDefine> byTemp = accSegmentDefineRespository.findAll();
        for(AccSegmentDefine accSegmentDefine : byTemp){
            segmentMaps.put(accSegmentDefine.getSegmentCol(),accSegmentDefine.getSegmentFlag());
        }
        // 开始校验 -> 科目代码和专项信息。
        for(Entry entry : entrys){
            VoucherDTO voucherDTO1 = new VoucherDTO();
            VoucherDTO voucherDTO2 = new VoucherDTO();
            String subjectCode = entry.getSubjectCode().substring(0,entry.getSubjectCode().length()-1);
            String resultCode = checkSubjectCodePassMusterBySubjectCodeAll(subjectCode,accbookCode);
            if (resultCode!=null && !"".equals(resultCode)) {
                if ("notExist".equals(resultCode)) {
                    errorMsg.append(entry.getSubjectCode()+"不存在，请重新输入！");
                    resultMap.put("resultMsg",errorMsg.toString());
                    return resultMap;
                }
                if ("notEnd".equals(resultCode)) {
//                    errorMsg = errorMsg + entry.getSubjectCode()+"不是末级科目，请重新输入！";
                    errorMsg.append(entry.getSubjectCode()+"不是末级科目，请重新输入！");
                    resultMap.put("resultMsg",errorMsg.toString());
                    return resultMap;
                }
                if ("notUse".equals(resultCode)) {
//                    errorMsg = errorMsg + entry.getSubjectCode()+"已停用，请重新输入！";
                    errorMsg.append(entry.getSubjectCode()+"已停用，请重新输入！");
                    resultMap.put("resultMsg",errorMsg.toString());
                    return resultMap;
                }
            }
            if((entry.getDebit()==null||"".equals(entry.getDebit())||"0".equals(entry.getDebit()))&&(entry.getCredit()==null||"".equals(entry.getCredit())||"0".equals(entry.getCredit()))){
//                return  "单条分录的借贷方不能同时为0，请重新输入！";
//                errorMsg = errorMsg + "单条分录的借贷方不能同时为0，请重新输入！";
                errorMsg.append("单条分录的借贷方不能同时为0，请重新输入！");
                resultMap.put("resultMsg",errorMsg.toString());
                return resultMap;
            }

            //判断科目是否挂专项，若有则校验专项信息，若无也处理专项信息（sql已经去掉没有专项的科目信息条件得的查询结果）
            StringBuffer sql=new StringBuffer("select * from subjectinfo s  where s.account = ?1 and concat_ws('',s.all_subject,s.subject_code) = ?2 and s.special_id is not null and s.special_id != ''");

            Map<Integer, Object> params1 = new HashMap<>();
            params1.put(1, accbookCode);
            params1.put(2, subjectCode);

            List<SubjectInfo> listBySql = (List<SubjectInfo>)subjectRepository.queryBySql(sql.toString(), params1, SubjectInfo.class);
            // 用于存储总账对应的专项段对应xml分录中信息
            StringBuffer result = new StringBuffer();
            // 一级专项集合
            String specialSuperCodes = "";
            // 例如 1002/01/01/3001  special_id  = '218,231'
            if(listBySql.size() > 0 && listBySql != null){

                SubjectInfo subjectInfo = listBySql.get(0);
                String[] specialIdArr = subjectInfo.getSpecialId().split(",");
                for (int i = 0; i < specialIdArr.length; i++) {
                    // 218,231
                    String specialIds = specialIdArr[i];
                    // 由数据库查询变为Map集合查询
//                    List<SpecialInfo> specialInfos = specialInfoRepository.querySpecialInfoById(specialIds);
                    // speicalCode -> BM、ZJDB、YS
//                    String specialCode = specialInfos.get(0).getSpecialCode();
                    String specialCode = specialMaps.get(specialIds);
                    // 一级专项集合
                    specialSuperCodes = specialSuperCodes + specialCode + ",";
                    //  获取专项段，在通过专项段找到解析信息，S02,S01
//                    String segelment = specialInfoRepository.querySegmentFlagInfoByAccount(accbookCode, specialCode);
                    // 由查询数据库形式修改为map集合中取。
                    String segelment = segmentMaps.get(specialCode);
                    // 拿传输过来的专项信息(这个是看我们系统中专项挂)。
                    if ("s01".equals(segelment)) {
                        if (entry.getS01() == null || "".equals(entry.getS01())) {
//                                return "S01段的专项不能为空";
                            resultMap.put("resultMsg", "S01段的专项不能为空");
                            return resultMap;
                        }
                        // 用专项末级代码和父级id进行查询，看是不是放错了。YS放了BM的,
                        // 由于YS 和 WLDX分级末级的父id 为上级，并非1级， 找末级的上级逐级查找直到superCode为null的id。与 科目代码段截取一级专项id进行比对。
//                        List<SpecialInfo> specialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndSuperSpecialAndAccount(entry.getS01(), specialIds, accbookCode);
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS01(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
//                            errorMsg = errorMsg + accbookCode +"账套下的S01段专项信息不正确";
                            errorMsg.append(accbookCode +"账套下的S01段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String substringS01 = entry.getS01().substring(0,2);
                        if(!"BM".equals(substringS01)){
                            errorMsg.append(accbookCode +"账套下的S01专项段信息不是部门(成本中心)的末级专项");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
//                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
//                        if(!specialIds.equals(oneLevelId)){
////                            errorMsg = errorMsg + accbookCode +"账套下的S01专项段信息不是部门(成本中心)的末级专项";
//                            errorMsg.append(accbookCode +"账套下的S01专项段信息不是部门(成本中心)的末级专项");
//                            resultMap.put("resultMsg",errorMsg.toString());
//                            return resultMap;
//                        }
                        result.append(entry.getS01() + ",");
                    }
                    else if ("s02".equals(segelment)) {
                        if (entry.getS02() == null || "".equals(entry.getS02())) {
//                                return "S02段的专项不能为空";
                            resultMap.put("resultMsg", "S02段的专项不能为空");
                            return resultMap;
                        }
//                        List<SpecialInfo> specialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndSuperSpecialAndAccount(entry.getS02(), specialIds, accbookCode);
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS02(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
//                            errorMsg = errorMsg + accbookCode +"账套下的S02段专项信息不正确";
                            errorMsg.append(accbookCode +"账套下的S02段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String substringS02 = entry.getS02().substring(0, 2);
                        if(!"YS".equals(substringS02)){
                            errorMsg.append(accbookCode +"账套下的S02专项段信息不是预算的末级专项");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
//                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
//                        if(!specialIds.equals(oneLevelId)){
////                            errorMsg = errorMsg + accbookCode +"账套下的S02专项段信息不是预算的末级专项";
//                            errorMsg.append(accbookCode +"账套下的S02专项段信息不是预算的末级专项");
//                            resultMap.put("resultMsg",errorMsg.toString());
//                            return resultMap;
//                        }
                        result.append(entry.getS02() + ",");
                    }
                    else if ("s03".equals(segelment)) {
                        if (entry.getS03() == null || "".equals(entry.getS03())) {
//                                return "S03段的专项不能为空";
                            resultMap.put("resultMsg", "S03段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS03(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
//                            errorMsg = errorMsg + accbookCode +"账套下的S03段专项信息不正确";
                            errorMsg.append(accbookCode +"账套下的S03段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String substringS03 = entry.getS03().substring(0, 2);
                        if(!"CP".equals(substringS03)){
                            errorMsg.append(accbookCode +"账套下的S03专项段信息不是产品的末级专项");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
//                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
//                        if(!specialIds.equals(oneLevelId)){
////                            errorMsg = errorMsg + accbookCode +"账套下的S03专项段信息不是产品的末级专项";
//                            errorMsg.append(accbookCode +"账套下的S03专项段信息不是产品的末级专项");
//                            resultMap.put("resultMsg",errorMsg.toString());
//                            return resultMap;
//                        }
                        result.append(entry.getS03() + ",");

                    }
                    else if ("s04".equals(segelment)) {
                        if (entry.getS04() == null || "".equals(entry.getS04())) {
//                                return "S04段的专项不能为空";
                            resultMap.put("resultMsg", "S04段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS04(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
//                            errorMsg = errorMsg + accbookCode +"账套下的S04段专项信息不正确";
                            errorMsg.append(accbookCode +"账套下的S04段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String substringS04 = entry.getS04().substring(0, 2);
                        if(!"QD".equals(substringS04)){
                            errorMsg.append(accbookCode +"账套下的S04专项段信息不是渠道的末级专项");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
//                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
//                        if(!specialIds.equals(oneLevelId)){
////                            errorMsg = errorMsg + accbookCode +"账套下的S04专项段信息不是渠道的末级专项";
//                            errorMsg.append(accbookCode +"账套下的S04专项段信息不是渠道的末级专项");
//                            resultMap.put("resultMsg",errorMsg.toString());
//                            return resultMap;
//                        }
                        result.append(entry.getS04() + ",");
                    }
                    else if ("s05".equals(segelment)) {
                        if (entry.getS05() == null || "".equals(entry.getS05())) {
//                                return "S05段的专项不能为空";
                            resultMap.put("resultMsg", "S05段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS05(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
//                            errorMsg = errorMsg + accbookCode +"账套下的S05段专项信息不正确";
                            errorMsg.append(accbookCode +"账套下的S05段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String substringS05 = entry.getS05().substring(0, 4);
                        if(!"NBWL".equals(substringS05)){
                            errorMsg.append(accbookCode +"账套下的S05专项段信息不是内部往来的末级专项");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
//                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
//                        if(!specialIds.equals(oneLevelId)){
////                            errorMsg = errorMsg + accbookCode +"账套下的S05专项段信息不是内部往来的末级专项";
//                            errorMsg.append(accbookCode +"账套下的S05专项段信息不是内部往来的末级专项");
//                            resultMap.put("resultMsg",errorMsg.toString());
//                            return resultMap;
//                        }
                        result.append(entry.getS05() + ",");
                    }
                    else if ("s06".equals(segelment)) {
                        if (entry.getS06() == null || "".equals(entry.getS06())) {
//                                return "S06段的专项不能为空";
                            resultMap.put("resultMsg", "S06段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS06(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
//                            errorMsg = errorMsg + accbookCode +"账套下的S06段专项信息不正确";
                            errorMsg.append(accbookCode +"账套下的S06段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String substringS06 = entry.getS06().substring(0, 3);
                        if(!"XJC".equals(substringS06)){
                            errorMsg.append(accbookCode +"账套下的S06专项段信息不是现金流量的末级专项");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
//                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
//                        if(!specialIds.equals(oneLevelId)){
////                            errorMsg = errorMsg + accbookCode +"账套下的S06专项段信息不是现金流量的末级专项";
//                            errorMsg.append(accbookCode +"账套下的S06专项段信息不是现金流量的末级专项");
//                            resultMap.put("resultMsg",errorMsg.toString());
//                            return resultMap;
//                        }
                        result.append(entry.getS06() + ",");
                    }
                    else if ("s07".equals(segelment)) {
                        if (entry.getS07() == null || "".equals(entry.getS07())) {
//                                return "S07段的专项不能为空";
                            resultMap.put("resultMsg", "S07段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS07(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
//                            errorMsg = errorMsg + accbookCode +"账套下的S07段专项信息不正确";
                            errorMsg.append(accbookCode +"账套下的S07段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String substringS07 = entry.getS07().substring(0, 4);
                        if(!"ZJDB".equals(substringS07)){
                            errorMsg.append(accbookCode +"账套下的S07专项段信息不是资金调拨的末级专项");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
//                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
//                        if(!specialIds.equals(oneLevelId)){
////                            errorMsg = errorMsg + accbookCode +"账套下的S07专项段信息不是资金调拨的末级专项";
//                            errorMsg.append(accbookCode +"账套下的S07专项段信息不是资金调拨的末级专项");
//                            resultMap.put("resultMsg",errorMsg.toString());
//                            return resultMap;
//                        }
                        result.append(entry.getS07() + ",");
                    }
                    else if ("s08".equals(segelment)) {
                        if (entry.getS08() == null || "".equals(entry.getS08())) {
//                                return "S08段的专项不能为空";
                            resultMap.put("resultMsg", "S08段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS08(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
//                            errorMsg = errorMsg + accbookCode +"账套下的S08段专项信息不正确";
                            errorMsg.append(accbookCode +"账套下的S08段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String substringS08 = entry.getS08().substring(0, 4);
                        if(!"WLDX".equals(substringS08)){
                            errorMsg.append(accbookCode +"账套下的S08专项段信息不是往来对象的末级专项");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
//                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
//                        if(!specialIds.equals(oneLevelId)){
////                            errorMsg = errorMsg + accbookCode +"账套下的S08专项段信息不是往来对象的末级专项";
//                            errorMsg.append(accbookCode +"账套下的S08专项段信息不是往来对象的末级专项");
//                            resultMap.put("resultMsg",errorMsg.toString());
//                            return resultMap;
//                        }
                        result.append(entry.getS08() + ",");
                    }
                    else if ("s09".equals(segelment)) {
                        if (entry.getS09() == null || "".equals(entry.getS09())) {
//                                return "S09段的专项不能为空";
                            resultMap.put("resultMsg", "S09段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS09(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
//                            errorMsg = errorMsg + accbookCode +"账套下的S09段专项信息不正确";
                            errorMsg.append(accbookCode +"账套下的S09段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String substringS09 = entry.getS09().substring(0, 4);
                        if(!"YWTX".equals(substringS09)){
                            errorMsg.append(accbookCode +"账套下的S09专项段信息不是业务条线的末级专项");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
//                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
//                        if(!specialIds.equals(oneLevelId)){
////                            errorMsg = errorMsg + accbookCode +"账套下的S09专项段信息不是业务条线的末级专项";
//                            errorMsg.append(accbookCode +"账套下的S09专项段信息不是业务条线的末级专项");
//                            resultMap.put("resultMsg",errorMsg.toString());
//                            return resultMap;
//                        }
                        result.append(entry.getS09() + ",");
                    }
                    else if ("s10".equals(segelment)) {
                        if (entry.getS10() == null || "".equals(entry.getS10())) {
                            resultMap.put("resultMsg", "S10段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS10(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
                            errorMsg.append(accbookCode +"账套下的S10段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
                        if(!specialIds.equals(oneLevelId)){
                            errorMsg.append(accbookCode +"账套下的S10专项段信息末级专项存放错误");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        result.append(entry.getS10() + ",");
                    }
                    else if ("s11".equals(segelment)) {
                        if (entry.getS11() == null || "".equals(entry.getS11())) {
                            resultMap.put("resultMsg", "S11段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS11(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
                            errorMsg.append(accbookCode +"账套下的S11段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
                        if(!specialIds.equals(oneLevelId)){
                            errorMsg.append(accbookCode +"账套下的S11专项段信息末级专项存放错误");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        result.append(entry.getS11() + ",");
                    }
                    else if ("s12".equals(segelment)) {
                        if (entry.getS12() == null || "".equals(entry.getS12())) {
                            resultMap.put("resultMsg", "S12段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS12(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
                            errorMsg.append(accbookCode +"账套下的S12段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
                        if(!specialIds.equals(oneLevelId)){
                            errorMsg.append(accbookCode +"账套下的S12专项段信息末级专项存放错误");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        result.append(entry.getS12() + ",");
                    }
                    else if ("s13".equals(segelment)) {
                        if (entry.getS13() == null || "".equals(entry.getS13())) {
                            resultMap.put("resultMsg", "S13段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS13(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
                            errorMsg.append(accbookCode +"账套下的S13段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
                        if(!specialIds.equals(oneLevelId)){
                            errorMsg.append(accbookCode +"账套下的S13专项段信息末级专项存放错误");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        result.append(entry.getS13() + ",");
                    }
                    else if ("s14".equals(segelment)) {
                        if (entry.getS14() == null || "".equals(entry.getS14())) {
                            resultMap.put("resultMsg", "S14段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS14(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
                            errorMsg.append(accbookCode +"账套下的S14段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
                        if(!specialIds.equals(oneLevelId)){
                            errorMsg.append(accbookCode +"账套下的S14专项段信息末级专项存放错误");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        result.append(entry.getS14() + ",");
                    }
                    else if ("s15".equals(segelment)) {
                        if (entry.getS15() == null || "".equals(entry.getS15())) {
                            resultMap.put("resultMsg", "S15段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS15(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
                            errorMsg.append(accbookCode +"账套下的S15段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
                        if(!specialIds.equals(oneLevelId)){
                            errorMsg.append(accbookCode +"账套下的S15专项段信息末级专项存放错误");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        result.append(entry.getS15() + ",");
                    }
                    else if ("s16".equals(segelment)) {
                        if (entry.getS16() == null || "".equals(entry.getS16())) {
                            resultMap.put("resultMsg", "S16段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS16(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
                            errorMsg.append(accbookCode +"账套下的S16段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
                        if(!specialIds.equals(oneLevelId)){
                            errorMsg.append(accbookCode +"账套下的S16专项段信息末级专项存放错误");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        result.append(entry.getS16() + ",");
                    }
                    else if ("s17".equals(segelment)) {
                        if (entry.getS17() == null || "".equals(entry.getS17())) {
                            resultMap.put("resultMsg", "S17段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS17(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
                            errorMsg.append(accbookCode +"账套下的S17段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
                        if(!specialIds.equals(oneLevelId)){
                            errorMsg.append(accbookCode +"账套下的S17专项段信息末级专项存放错误");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        result.append(entry.getS17() + ",");
                    }
                    else if ("s18".equals(segelment)) {
                        if (entry.getS18() == null || "".equals(entry.getS18())) {
                            resultMap.put("resultMsg", "S18段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS18(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
                            errorMsg.append(accbookCode +"账套下的S18段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
                        if(!specialIds.equals(oneLevelId)){
                            errorMsg.append(accbookCode +"账套下的S18专项段信息末级专项存放错误");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        result.append(entry.getS18() + ",");
                    }
                    else if ("s19".equals(segelment)) {
                        if (entry.getS19() == null || "".equals(entry.getS19())) {
                            resultMap.put("resultMsg", "S19段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS19(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
                            errorMsg.append(accbookCode +"账套下的S19段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
                        if(!specialIds.equals(oneLevelId)){
                            errorMsg.append(accbookCode +"账套下的S19专项段信息末级专项存放错误");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        result.append(entry.getS19() + ",");
                    }
                    else if ("s20".equals(segelment)) {
                        if (entry.getS20() == null || "".equals(entry.getS20())) {
                            resultMap.put("resultMsg", "S20段的专项不能为空");
                            return resultMap;
                        }
                        String superSpecialTrueMsg = specialInfoRepository.querySpecialInfoBySpecialCodeAndAccount(entry.getS20(), accbookCode);
                        if("".equals(superSpecialTrueMsg) || superSpecialTrueMsg == null){
                            errorMsg.append(accbookCode +"账套下的S20段专项信息不正确");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        String oneLevelId = recursiveCall(superSpecialTrueMsg);
                        if(!specialIds.equals(oneLevelId)){
                            errorMsg.append(accbookCode +"账套下的S20专项段信息末级专项存放错误");
                            resultMap.put("resultMsg",errorMsg.toString());
                            return resultMap;
                        }
                        result.append(entry.getS20() + ",");
                    }
                }
            } else{
                String subjectCodeInfo = "";
                // 把分录中所有的专项信息拿出来。
                subjectCodeInfo = checkAccountXmlSpecialInfo(subjectCodeInfo,entry);
                if(!"".equals(subjectCodeInfo)){
//                    errorMsg = errorMsg + "当前科目:"+subjectCode+"总账系统下没有挂设专项信息，请重新确认。";
                    errorMsg.append("当前科目:"+subjectCode+"总账系统下没有挂设专项信息，请重新确认。");
                    resultMap.put("resultMsg",errorMsg.toString());
                    return resultMap;
                }
            }

            // 解析出分录中的所有专项段。
            String subjectCodeInfoMation = "";
            // 把分录中所有的专项信息拿出来。
            subjectCodeInfoMation = checkAccountXmlSpecialInfo(subjectCodeInfoMation,entry);
            // xml文件中解析出的所有专项信息。
            String[] subjectCodeInfos = subjectCodeInfoMation.split(",");
            // 根据传入的科目代码，对照总账系统挂接的专项信息，找的xml中对应专项信息。
            String[] systemResultInfos = result.toString().split(",");
            if(subjectCodeInfos.length != systemResultInfos.length){
//                errorMsg = errorMsg + "总账系统:"+subjectCode+"挂接的专项，与当前"+accbookCode+"账套下："+subjectCode+"对应的挂接专项信息不符";
                errorMsg.append("总账系统:"+subjectCode+"科目挂接的专项，与当前传输的"+accbookCode+"账套下："+subjectCode+"科目挂接专项信息不符");
                resultMap.put("resultMsg",errorMsg.toString());
                return resultMap;
            }

            // 由于上述在检验专项段的时候，同时通过superCode,account 和 末级专项信息，校验了信息是否存在，所以不需要下列校验，1. 可能传入的是末级专项，可能会有是末级的情况， 2. 不存在不存在的情况，不存在报异常了。 3.无法保证当前当前末级是否为弃用，
            // 拿具体的专项信息，去专项信息表查询，看是否存在，
            for(String subjectCodeInfo : subjectCodeInfos){
                String specialJudgeInfo = voucherService.checkSpecialCodePassMusterBySpecialCode(subjectCodeInfo,accbookCode);
                if (specialJudgeInfo!=null && !"".equals(specialJudgeInfo)) {
                    if ("notExist".equals(specialJudgeInfo)) {
//                        errorMsg = errorMsg + "专项："+subjectCodeInfo+" 不存在，请重新输入！";
                        errorMsg.append("专项："+subjectCodeInfo+" 不存在，请重新输入！");
                        resultMap.put("resultMsg",errorMsg.toString());
                        return resultMap;
                    }
                    if ("notEnd".equals(specialJudgeInfo)) {
//                        errorMsg = errorMsg + subjectCodeInfo+"不是末级专项，请重新输入！";
                        errorMsg.append(subjectCodeInfo+"不是末级专项，请重新输入！");
                        resultMap.put("resultMsg",errorMsg.toString());
                        return resultMap;
                    }
                    if ("notUse".equals(specialJudgeInfo)) {
//                        errorMsg = errorMsg + subjectCodeInfo+"专项已停用，请重新输入！";
                        errorMsg.append(subjectCodeInfo+"专项已停用，请重新输入！");
                        resultMap.put("resultMsg",errorMsg.toString());
                        return resultMap;
                    }
                }
            }
            voucherDTO1.setCredit(entry.getCredit());
            voucherDTO1.setDebit(entry.getDebit());
            voucherDTO1.setRemarkName(entry.getRemarkName());
            voucherDTO1.setSubjectCode(entry.getSubjectCode());
            voucherDTO1.setSubjectName(entry.getSubjectName());

            voucherDTO2.setSubjectCodeS(entry.getSubjectCode());
            voucherDTO2.setSubjectNameS(entry.getSubjectName());
            // 一级专项集合。
            if(specialSuperCodes.length()>0){
                specialSuperCodes = specialSuperCodes.substring(0,specialSuperCodes.length()-1);
            }
            voucherDTO2.setSpecialSuperCodeS(specialSuperCodes);
            // 这里给的必须是通过科目代码查询出来的挂接专项的结果集，如果是subjectCodeInfoMation 则是S01-S09的文件解析顺序，不符合Direction_other需求
            if(result.length()>0){
                result = result.deleteCharAt(result.length() - 1);
            }
            voucherDTO2.setSpecialCodeS(result.toString());

            list2.add(voucherDTO1);
            list3.add(voucherDTO2);
        }
        // 以上已经对一条凭证处理校验完毕。
        resultMap.put("list2",list2);
        resultMap.put("list3",list3);
        resultMap.put("resultMsg","success");
        // 需要开始进行录入凭证。
        return resultMap;
    }


    private String checkSubjectCodePassMusterBySubjectCodeAll(String subjectCodeAll,String account){
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

    /**
     *
     * 功能描述:    查询xml出来的专项信息，返回对应内容。
     *
     */
    private String checkAccountXmlSpecialInfo(String subjecCodeInfo,Entry entry){
        if(entry.getS01() != null && !"".equals(entry.getS01())){
            subjecCodeInfo = subjecCodeInfo+entry.getS01()+",";
        }
        if(entry.getS02() != null && !"".equals(entry.getS02())){
            subjecCodeInfo = subjecCodeInfo+entry.getS02()+",";
        }
        if(entry.getS03() != null && !"".equals(entry.getS03())){
            subjecCodeInfo = subjecCodeInfo+entry.getS03()+",";
        }
        if(entry.getS04() != null && !"".equals(entry.getS04())){
            subjecCodeInfo = subjecCodeInfo+entry.getS04()+",";
        }
        if(entry.getS05() != null && !"".equals(entry.getS05())){
            subjecCodeInfo = subjecCodeInfo+entry.getS05()+",";
        }
        if(entry.getS06() != null && !"".equals(entry.getS06())){
            subjecCodeInfo = subjecCodeInfo+entry.getS06()+",";
        }
        if(entry.getS07() != null && !"".equals(entry.getS07())){
            subjecCodeInfo = subjecCodeInfo+entry.getS07()+",";
        }
        if(entry.getS08() != null && !"".equals(entry.getS08())){
            subjecCodeInfo = subjecCodeInfo+entry.getS08()+",";
        }
        if(entry.getS09() != null && !"".equals(entry.getS09())){
            subjecCodeInfo = subjecCodeInfo+entry.getS09()+",";
        }
        if(entry.getS10() != null && !"".equals(entry.getS10())){
            subjecCodeInfo = subjecCodeInfo+entry.getS10()+",";
        }
        if(entry.getS11() != null && !"".equals(entry.getS11())){
            subjecCodeInfo = subjecCodeInfo+entry.getS11()+",";
        }
        if(entry.getS12() != null && !"".equals(entry.getS12())){
            subjecCodeInfo = subjecCodeInfo+entry.getS12()+",";
        }
        if(entry.getS13() != null && !"".equals(entry.getS13())){
            subjecCodeInfo = subjecCodeInfo+entry.getS13()+",";
        }
        if(entry.getS14() != null && !"".equals(entry.getS14())){
            subjecCodeInfo = subjecCodeInfo+entry.getS14()+",";
        }
        if(entry.getS15() != null && !"".equals(entry.getS15())){
            subjecCodeInfo = subjecCodeInfo+entry.getS15()+",";
        }
        if(entry.getS16() != null && !"".equals(entry.getS16())){
            subjecCodeInfo = subjecCodeInfo+entry.getS16()+",";
        }
        if(entry.getS17() != null && !"".equals(entry.getS17())){
            subjecCodeInfo = subjecCodeInfo+entry.getS17()+",";
        }
        if(entry.getS18() != null && !"".equals(entry.getS18())){
            subjecCodeInfo = subjecCodeInfo+entry.getS18()+",";
        }
        if(entry.getS19() != null && !"".equals(entry.getS19())){
            subjecCodeInfo = subjecCodeInfo+entry.getS19()+",";
        }
        if(entry.getS20() != null && !"".equals(entry.getS20())){
            subjecCodeInfo = subjecCodeInfo+entry.getS20()+",";
        }
        return  subjecCodeInfo;
    }

    /**
     *
     * 功能描述:    通过末级专项，寻找SuperSpecialCode 一直寻找到 SuperSpecialCode 为空，即当前的对象Id即为一级专项。
     *          主要是因为YS （4）和 WLDX （3） 并不是 二级，
     */
    private String recursiveCall(String superSpecialCode){
        SpecialInfo specialInfo = specialInfoRepository.findById(Long.valueOf(superSpecialCode)).get();
//        System.out.println(specialInfo.getSuperSpecial());//4989
        if(!"".equals(specialInfo.getSuperSpecial())&& specialInfo.getSuperSpecial() != null){
            String superSpecial = specialInfo.getSuperSpecial();
           return recursiveCall(superSpecial);
        }else{
            return  String.valueOf(specialInfo.getId());
        }
    }
}
