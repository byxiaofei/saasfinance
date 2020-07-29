package com.sinosoft.service.impl.fixedassets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.account.AccMonthTrace;
import com.sinosoft.domain.fixedassets.*;
import com.sinosoft.dto.CodeManageDTO;
import com.sinosoft.dto.MenuInfoDTO;
import com.sinosoft.dto.fixedassets.AccAssetCodeTypeDTO;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.fixedassets.AccGCheckInfoDTO;
import com.sinosoft.repository.account.AccMonthRespository;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.repository.fixedassets.AccAssetInfoRepository;
import com.sinosoft.repository.fixedassets.AccDepreRepository;
import com.sinosoft.repository.fixedassets.AccGCheckInfoRepository;
import com.sinosoft.service.fixedassets.CategoryCodingService;
import com.sinosoft.service.fixedassets.FixedassetsCardService;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-29 15:10
 */
@Service
public class FixedassetsCardServiceImpl implements FixedassetsCardService {
    private Logger logger = LoggerFactory.getLogger(FixedassetsCardServiceImpl.class);
    @Resource
    private AccAssetInfoRepository accAssetInfoRepository;
    @Resource
    private AccAssetCodeTypeRepository accAssetCodeTypeRepository;
    @Resource
    private CategoryCodingService categoryCodingService;
    @Resource
    private FixedassetsCardService fixedassetsCardService;
    @Resource
    private AccGCheckInfoRepository accGCheckInfoRepository;
    @Resource
    private AccDepreRepository accDepreRepository;
    /**
     * 新增固定资产卡片
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult add(AccAssetInfoDTO dto) {
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        AccAssetInfo aai = new AccAssetInfo();
        //判断是新增还是修改
//        StringBuffer sql = new StringBuffer();
//        sql.append("select * from accassetinfo a where 1=1 and center_code = '"+ centerCode +"' and branch_code = '"+ branchCode +"' \n" +
//                "and acc_book_type = '"+ accBookType +"' and acc_book_code = '"+ accBookCode +"' and code_type = '21' and card_code = '"+ dto.getCardCode() +"'");
//        List<?> list = accAssetInfoRepository.queryBySql(sql.toString(), AccAssetInfo.class);
        List<?> list = accAssetInfoRepository.queryAccAssetInfoByChooseMessage(centerCode,branchCode,accBookType,accBookCode,dto.getCardCode());
        if(list.size()>0){
            return InvokeResult.failure("卡片编号已存在,请重新操作！");
        }
        /*原来代码是新增和修改写在一起，注释部分为修改部分，现在新增和修改分开处理*/
        /*if(list.size()!=0){//修改
             aai = (AccAssetInfo) list.get(0);

            aai.setAssetType(dto.getAssetType() == null ? null : dto.getAssetType());//固定资产类别编码
            aai.setAssetCode(dto.getAssetCode() == null ? null : dto.getAssetCode());//固定资产编号
            // aai.setEndDepreAmount(dto.getEndDepreAmount() == null? "0" : dto.getEndDepreAmount());//期末累计折旧月份 期初已折旧月份+系统计提折旧月份
            //  aai.setEndDepreMoney(aai.getInitDepreMoney() == null ? new BigDecimal("0") : dto.getEndDepreMoney());//期末累计折旧金额 期初折旧金额+系统计提折旧金额
           // aai.setAssetCode(aai.getAssetCode() ==);//固定资产编号
            aai.setEndDepreAmount(aai.getInitDepreAmount());//期末累计折旧月份 期初已折旧月份+系统计提折旧月份
            aai.setEndDepreMoney(aai.getInitDepreMoney());//期末累计折旧金额 期初折旧金额+系统计提折旧金额
                *//*aai.setCreateOper(((AccAssetInfo)list.get(0)).getCreateOper());//录入人
                aai.setCreateTime(((AccAssetInfo)list.get(0)).getCreateTime());//录入时间*//*
            aai.setUpdateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));//修改人
            //修改时间格式化
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            aai.setUpdateTime(df.format(new Date()));//修改时间
        }else{//新增*/
        //将前台取到的值通过实体类存入数据库中
        AccAssetInfoId aaiid = new AccAssetInfoId();
        aaiid.setCenterCode(centerCode);            //核算单位
        aaiid.setBranchCode(branchCode);            //基层单位
        aaiid.setAccBookType(accBookType);          //账套类型
        aaiid.setAccBookCode(accBookCode);          //账套编码
        aaiid.setCodeType(dto.getCodeType());       //管理类别编码
        aaiid.setCardCode(dto.getCardCode());       //卡片编码
        aai.setId(aaiid);
        aai.setAssetType(dto.getAssetType() == null ? null : dto.getAssetType());//固定资产类别编码
        String assetcodess=fCode(dto.getAssetType());
        aai.setAssetCode(assetcodess == null ? null : assetcodess);//固定资产编号
        aai.setEndDepreAmount(dto.getInitDepreAmount());//期末累计折旧月份 期初已折旧月份+系统计提折旧月份
        aai.setEndDepreMoney(dto.getInitDepreMoney());//期末累计折旧金额 期初折旧金额+系统计提折旧金额
        aai.setCreateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));//录入人
        //录入时间格式化
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        aai.setCreateTime(df.format(new Date()));//录入时间
        aai.setUpdateOper(null);//修改人
        aai.setUpdateTime(null);//修改时间
        // }
        aai.setAssetName(dto.getAssetName() == null ? null : dto.getAssetName());//固定资产名称
        aai.setMetricName(dto.getMetricName() == null ? null : dto.getMetricName());//计量单位
        aai.setManufactor(dto.getManufactor() == null ? null : dto.getManufactor());//建造/制造商
        aai.setSpecification(dto.getSpecification() == null ? null : dto.getSpecification());//规格说明
        aai.setSerialNumber(dto.getSerialNumber() == null ? null : dto.getSerialNumber());//序列号
        //判断启用年月是否在会计期间内
        String date = dto.getUseStartDate() == null ? null : dto.getUseStartDate().substring(0,4)+dto.getUseStartDate().substring(5,7);//2019-01-01
//        StringBuffer dateSql = new StringBuffer();
//            dateSql.append("select year_month_date from AccGCheckInfo t1 where t1.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  t1.acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and t1.year_month_date = '"+ date +"'");
//        List<?> dateList = accAssetInfoRepository.queryBySqlSC(dateSql.toString());
        List<?> dateList =accGCheckInfoRepository.findYearMonthDateByCenterCodeAndAccBookCodeAndYearMonthDate(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),date);
        if(dateList.size()<1){
            return InvokeResult.failure("启用时间必须在当前会计期间内！");
        }
        aai.setUseStartDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//启用年月
        aai.setUnitCode(dto.getUnitCode() == null ? null : dto.getUnitCode());//使用部门
        aai.setQuantity(dto.getQuantity() == null ? null : dto.getQuantity());//数量（面积）
        aai.setSourceFlag(dto.getSourceFlag() == null ? null : dto.getSourceFlag());//来源
        aai.setUseFlag(dto.getUseFlag() == null ? null : dto.getUseFlag());//使用状态
        aai.setOrganization(dto.getOrganization() == null ? null : dto.getOrganization());//存放地点
        aai.setStorageWay(dto.getStorageWay() == null ? null : dto.getStorageWay());//存放方式
        aai.setAssetOriginValue(dto.getAssetOriginValue() == null ? null : dto.getAssetOriginValue());//固定资产原值
        aai.setAssetNetValue(dto.getAssetNetValue() == null ? null : dto.getAssetNetValue());//固定资产净值
        aai.setDepYears(dto.getDepYears() == null ? null : dto.getDepYears());//使用年限
        aai.setDepType(dto.getDepType()!=null&&!"".equals(dto.getDepType())?"1":dto.getDepType());//折旧方法
        aai.setImpairment(dto.getImpairment() == null ? null : dto.getImpairment());//减值准备
        aai.setAddedTax(dto.getAddedTax() == null ? null : dto.getAddedTax());//增值税率
        aai.setSum(dto.getSum() == null ? null : dto.getSum());//金额
        aai.setInputTax(dto.getInputTax() == null ? null : dto.getInputTax());//进项税额
        aai.setPayWay(dto.getPayWay() == null ? null : dto.getPayWay());//付款方式
        aai.setPayCode(dto.getPayCode() == null ? null : dto.getPayCode());//付款专项
        aai.setRemainsRate(dto.getRemainsRate() == null ? new BigDecimal(0) : dto.getRemainsRate());//预计残值率
        aai.setRemainsValue(dto.getRemainsValue() == null ? new BigDecimal(0) : dto.getRemainsValue());//预计残值
        aai.setPredictClearFee(dto.getPredictClearFee() == null ? new BigDecimal(0) : dto.getPredictClearFee());//预计清理费用
        aai.setFormulaCode("");//折旧公式编码 //不知道怎么用，给默认值空
        aai.setInitDepreAmount(dto.getInitDepreAmount() == null ? "0" : dto.getInitDepreAmount());//期初折旧月份
        aai.setInitDepreMoney(dto.getInitDepreMoney() == null ? new BigDecimal(0) : dto.getInitDepreMoney());//期初折旧金额
        aai.setClearFlag((dto.getClearFlag() == null || "".equals(dto.getClearFlag())) ? "0" : dto.getClearFlag());//清理状态
        aai.setClearYearMonth(dto.getClearYearMonth() == null ? null : dto.getClearYearMonth());//清理生效年月
        aai.setClearCode(dto.getClearCode() == null ? null : dto.getClearCode());//清理原因
        aai.setClearfee(dto.getClearfee() == null ? null : dto.getClearfee());//清理费用
        aai.setClearReason(dto.getClearReason() == null ? null : dto.getClearReason());//清理原因说明
        aai.setClearDate(dto.getClearDate() == null ? null : dto.getClearDate());//清理操作日期
        aai.setClearOperatorBranch(dto.getClearOperatorBranch() == null ? null : dto.getClearOperatorBranch());//清理操作员单位
        aai.setClearOperatorCode(dto.getClearOperatorCode() == null ? null : dto.getClearOperatorCode());//清理操作人
        aai.setVoucherNo(dto.getVoucherNo() == null ? null : dto.getVoucherNo());//凭证号
        aai.setDepreFromDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//固定资产折旧起始日期
        aai.setDepreToDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//固定资产折旧至日期
        aai.setDepreFlag(dto.getDepreFlag() == null ? null : dto.getDepreFlag());//折旧计提状态
        aai.setTemp(dto.getTemp() == null ? null : dto.getTemp());//备注
        accAssetInfoRepository.save(aai);//保存
        return InvokeResult.success();

    }
    /**
     * 修改固定资产卡片
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult update(AccAssetInfoDTO dto) {
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();

        //判断启用年月是否在会计期间内
        String date = dto.getUseStartDate() == null ? null : dto.getUseStartDate().substring(0,4)+dto.getUseStartDate().substring(5,7);//2019-01-01
//        StringBuffer dateSql = new StringBuffer();
//        dateSql.append("select year_month_date from AccGCheckInfo t1 where t1.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  t1.acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and t1.year_month_date = '"+ date +"'");
//        List<?> dateList = accAssetInfoRepository.queryBySqlSC(dateSql.toString());
        List<?> dateList = accGCheckInfoRepository.findYearMonthDateByCenterCodeAndAccBookCodeAndYearMonthDate(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount(),date);
        if(dateList.size()<1){
            return InvokeResult.failure("启用时间必须在当前会计期间内！");
        }

        AccAssetInfo aai = new AccAssetInfo();
//        StringBuffer sql = new StringBuffer();
//        sql.append("select * from accassetinfo a where 1=1 and center_code = '"+ centerCode +"' and branch_code = '"+ branchCode +"' \n" +
//                "and acc_book_type = '"+ accBookType +"' and acc_book_code = '"+ accBookCode +"' and code_type = '21' and card_code = '"+ dto.getCardCode() +"'");
//        List<?> list = accAssetInfoRepository.queryBySql(sql.toString(), AccAssetInfo.class);
        List<AccAssetInfo> list = accAssetInfoRepository.queryAccAssetInfoByChooseMessage(centerCode,branchCode,accBookType,accBookCode,dto.getCardCode());
        if(list.size()!=0){//修改
            aai = (AccAssetInfo) list.get(0);
            aai.setAssetType(dto.getAssetType() == null ? null : dto.getAssetType());//固定资产类别编码
            aai.setAssetCode(dto.getAssetCode() == null ? null : dto.getAssetCode());//固定资产编号
            // aai.setEndDepreAmount(dto.getEndDepreAmount() == null? "0" : dto.getEndDepreAmount());//期末累计折旧月份 期初已折旧月份+系统计提折旧月份
            //  aai.setEndDepreMoney(aai.getInitDepreMoney() == null ? new BigDecimal("0") : dto.getEndDepreMoney());//期末累计折旧金额 期初折旧金额+系统计提折旧金额
            // aai.setAssetCode(aai.getAssetCode() ==);//固定资产编号
            aai.setEndDepreAmount(aai.getInitDepreAmount());//期末累计折旧月份 期初已折旧月份+系统计提折旧月份
            aai.setEndDepreMoney(aai.getInitDepreMoney());//期末累计折旧金额 期初折旧金额+系统计提折旧金额
                /*aai.setCreateOper(((AccAssetInfo)list.get(0)).getCreateOper());//录入人
                aai.setCreateTime(((AccAssetInfo)list.get(0)).getCreateTime());//录入时间*/
            aai.setUpdateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));//修改人
            //修改时间格式化
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            aai.setUpdateTime(df.format(new Date()));//修改时间
        }
        aai.setAssetName(dto.getAssetName() == null ? null : dto.getAssetName());//固定资产名称
        aai.setMetricName(dto.getMetricName() == null ? null : dto.getMetricName());//计量单位
        aai.setManufactor(dto.getManufactor() == null ? null : dto.getManufactor());//建造/制造商
        aai.setSpecification(dto.getSpecification() == null ? null : dto.getSpecification());//规格说明
        aai.setSerialNumber(dto.getSerialNumber() == null ? null : dto.getSerialNumber());//序列号
        aai.setUseStartDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//启用年月
        aai.setUnitCode(dto.getUnitCode() == null ? null : dto.getUnitCode());//使用部门
        aai.setQuantity(dto.getQuantity() == null ? null : dto.getQuantity());//数量（面积）
        aai.setSourceFlag(dto.getSourceFlag() == null ? null : dto.getSourceFlag());//来源
        aai.setUseFlag(dto.getUseFlag() == null ? null : dto.getUseFlag());//使用状态
        aai.setOrganization(dto.getOrganization() == null ? null : dto.getOrganization());//存放地点
        aai.setStorageWay(dto.getStorageWay() == null ? null : dto.getStorageWay());//存放方式
        aai.setAssetOriginValue(dto.getAssetOriginValue() == null ? null : dto.getAssetOriginValue());//固定资产原值
        aai.setAssetNetValue(dto.getAssetNetValue() == null ? null : dto.getAssetNetValue());//固定资产净值
        aai.setDepYears(dto.getDepYears() == null ? null : dto.getDepYears());//使用年限
        aai.setDepType(dto.getDepType()!=null&&!"".equals(dto.getDepType())?"1":dto.getDepType());//折旧方法
        aai.setImpairment(dto.getImpairment() == null ? null : dto.getImpairment());//减值准备
        aai.setAddedTax(dto.getAddedTax() == null ? null : dto.getAddedTax());//增值税率
        aai.setSum(dto.getSum() == null ? null : dto.getSum());//金额
        aai.setInputTax(dto.getInputTax() == null ? null : dto.getInputTax());//进项税额
        aai.setPayWay(dto.getPayWay() == null ? null : dto.getPayWay());//付款方式   1002/01/02/01/
        aai.setPayCode(dto.getPayCode() == null ? null : dto.getPayCode());//付款专项
        aai.setRemainsRate(dto.getRemainsRate() == null ? new BigDecimal(0) : dto.getRemainsRate());//预计残值率
        aai.setRemainsValue(dto.getRemainsValue() == null ? new BigDecimal(0) : dto.getRemainsValue());//预计残值
        aai.setPredictClearFee(dto.getPredictClearFee() == null ? new BigDecimal(0) : dto.getPredictClearFee());//预计清理费用
        aai.setFormulaCode("");//折旧公式编码 //不知道怎么用，给默认值空
        aai.setInitDepreAmount(dto.getInitDepreAmount() == null ? "0" : dto.getInitDepreAmount());//期初折旧月份
        aai.setInitDepreMoney(dto.getInitDepreMoney() == null ? new BigDecimal(0) : dto.getInitDepreMoney());//期初折旧金额
        aai.setClearFlag((dto.getClearFlag() == null || "".equals(dto.getClearFlag())) ? "0" : dto.getClearFlag());//清理状态
        aai.setClearYearMonth(dto.getClearYearMonth() == null ? null : dto.getClearYearMonth());//清理生效年月
        aai.setClearCode(dto.getClearCode() == null ? null : dto.getClearCode());//清理原因
        aai.setClearfee(dto.getClearfee() == null ? null : dto.getClearfee());//清理费用
        aai.setClearReason(dto.getClearReason() == null ? null : dto.getClearReason());//清理原因说明
        aai.setClearDate(dto.getClearDate() == null ? null : dto.getClearDate());//清理操作日期
        aai.setClearOperatorBranch(dto.getClearOperatorBranch() == null ? null : dto.getClearOperatorBranch());//清理操作员单位
        aai.setClearOperatorCode(dto.getClearOperatorCode() == null ? null : dto.getClearOperatorCode());//清理操作人
        aai.setVoucherNo(dto.getVoucherNo() == null ? null : dto.getVoucherNo());//凭证号
        aai.setDepreFromDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//固定资产折旧起始日期
        aai.setDepreToDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//固定资产折旧至日期
        aai.setDepreFlag(dto.getDepreFlag() == null ? null : dto.getDepreFlag());//折旧计提状态
        aai.setTemp(dto.getTemp() == null ? null : dto.getTemp());//备注
        accAssetInfoRepository.save(aai);//保存
        return InvokeResult.success();

    }
    /**
     *加载固定资产类别编码中维护的类别
     * @return
     */
    @Override
    public List<?> qryAssetType() {
        List<?> result = accAssetInfoRepository.qryAssetType(CurrentUser.getCurrentLoginAccount());
        return result;
    }

    /**
     * 加载专项中维护的BM类专项
     * @return
     */
    @Override
    public List<?> qryuUnitCode() {
        List<?> result = accAssetInfoRepository.qryuUnitCode(CurrentUser.getCurrentLoginAccount());
        return result;
    }

    /**
     * 通过固定资产编码类别名称查询 使用年限()
     * @param assetType
     * @return
     */
    @Override
    public String getDepYears(String assetType) {
        try{
//            StringBuffer sql = new StringBuffer();
//            sql.append("select * from accassetcodetype a where a.asset_type = '"+ assetType +"'" +
//                    " and  acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"'");
//            List<?> list = accAssetCodeTypeRepository.queryBySql(sql.toString(), AccAssetCodeType.class);
            List<AccAssetCodeType> list = accAssetCodeTypeRepository.queryAccAssetCodeTypeByChooseMessage(assetType,CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount());
            if(list.size()>0){
                return list.get(0).getDepYears().toString();
            }else{
                return "";
            }
        }catch (Exception e){
            logger.error("固定资产使用年限查询异常", e);
            return "";
        }
    }

    /**
     * 通过固定资产编码类别名称查询 折旧方法
     * @param assetType
     * @return
     */
    @Override
    public String getDepType(String assetType) {
        StringBuffer sql = new StringBuffer();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        sql.append("select code_name from codemanage where 1=1 and code_type = 'deprMethod' and code_code =\n" +
                "(select a.dep_type from accassetcodetype a where a.acc_book_code=?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        sql.append(" and a.asset_type =?"+paramsNo+")");
        params.put(paramsNo,assetType);
        paramsNo++;
        List<?> list = accAssetCodeTypeRepository.queryBySql(sql.toString(),params);
        if(list.size()>0){
            return list.get(0).toString();
//            return ((AccAssetCodeType)list.get(0)).getDepYears().toString();
        }else{
            return null;
        }
    }

    /**
     * 获取新增卡片编码
     * @return
     */
    @Override
    public String getNewCardCode() {
        //获取数据库中最大的卡片编码并+1
        String maxCardCode = accAssetInfoRepository.qryMaxCardCode(CurrentUser.getCurrentLoginAccount(),CurrentUser.getCurrentLoginManageBranch());
        if(maxCardCode != null){//数据库不为空
            String newCardCode = String.valueOf(Integer.parseInt(maxCardCode)+1);
            while (newCardCode.length()<5){
                newCardCode = "0"+newCardCode;
            }
            return newCardCode;
        }else{//数据库为空，默认值00001
            return "00001";
        }
    }
    @Override
   public Page<?> qryAccAssetInfo(int page,int rows, AccAssetInfoDTO acc){
       List<?> res=fixedassetsCardService.getAccAssetInfo( acc);
        Page<?> result = categoryCodingService.getPage(page,rows, res);
        return result;
    }
    @Override
    public List<?> getAccAssetInfo(AccAssetInfoDTO acc){
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();

        StringBuffer sql=new StringBuffer();
        sql.append("select f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                " f.asset_type as  assetType, f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName, f.metric_name as metricName,\n" +
                "f.manufactor as manufactor,f.specification as specification,f.serial_number as serialNumber,f.use_start_date as useStartDate,f.quantity,f.source_flag as sourceFlag,"+
                "f.use_flag as useFlag ,f.storage_way as storageWay,f.asset_origin_value as assetOriginValue,f.asset_net_value as assetNetValue, f.dep_years as depYears,f.dep_type as depType,"+
                "f.impairment as impairment, f.added_tax as addedTax,f.sum,f.input_tax as inputTax,f.pay_way as payWay, f.pay_code as payCode,f.remains_value as remainsValue,f.remains_rate as remainsRate," +
                "f.predict_clear_fee as predictClearFee,f.formula_code as formulaCode,f.init_depre_amount as initDepreAmount,f.init_depre_money as initDepreMoney, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "f.clear_flag as clearFlag1 , f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch, f.clear_operator_code as clearOperatorCode,"+
                "f.voucher_no as voucherNo,f.depre_from_date as depreFromDate,f.depre_to_date as depreToDate,f.depre_flag as  depreFlag,f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp,"+
                "(select a.asset_simple_name from accassetcodetype a where  a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetTypeName ,"+
                " f.unit_code as unitCode,f.organization as organization,"+
                "(select o.special_name from specialinfo o where o.id=f.unit_code ) as unitCodeName,"+
                "(select c.code_name from codemanage c where c.code_type='organization' and c.code_code=f.organization ) as organizationName,"+
                " (select c.code_name from codemanage c where c.code_type='deprMethod' and c.code_code=f.dep_type ) as deprMethod,"+
                " (select special_name from specialinfo where f.unit_code = id) as specialName, " +
                "(select i.month_depre_money from  AccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code ORDER BY i.year_month_data desc limit 1 ) as monthDepreMoney   from AccAssetInfo  f where 1=1 " );
//                " and f.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");

        sql.append(" and f.center_code=?"+paramsNo);
        params.put(paramsNo, CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append(" and acc_book_type=?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append(" and acc_book_code=?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        if(acc.getCardCode()!=null&&!acc.getCardCode().equals("")){
            if(acc.getCardCode().length()<5){
                String cs=acc.getCardCode();
                for(int i=0;i<5-acc.getCardCode().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode(cs);
            }
            sql.append(" and f.card_code>=?" + paramsNo );
            params.put(paramsNo,acc.getCardCode());
            paramsNo++;
        }
        if(acc.getCardCode1()!=null&&!acc.getCardCode1().equals("")){
            if(acc.getCardCode1().length()<5){
                String cs=acc.getCardCode1();
                for(int i=0;i<5-acc.getCardCode1().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode1(cs);
            }
            sql.append(" and f.card_code<=?" + paramsNo);
            params.put(paramsNo,acc.getCardCode1());
            paramsNo++;
        }
        if(acc.getAssetCode1()!=null&&!acc.getAssetCode1().equals("")){
            sql.append(" and f.asset_code>=?" + paramsNo);
            params.put(paramsNo,acc.getAssetCode());
            paramsNo++;
        }
        if(acc.getAssetCode2()!=null&&!acc.getAssetCode2().equals("")){
            sql.append(" and f.asset_code<=?" + paramsNo);
            params.put(paramsNo,acc.getAssetCode1());
            paramsNo++;
        }
        if(acc.getUseStartDate()!=null&&!acc.getUseStartDate().equals("")){
            sql.append(" and f.use_start_date>=?"+ paramsNo);
            params.put(paramsNo,acc.getUseStartDate());
            paramsNo++;
        }
        if(acc.getUseStartDate1()!=null&&!acc.getUseStartDate1().equals("")){
            sql.append(" and f.use_start_date<=?"+paramsNo);
            params.put(paramsNo,acc.getUseStartDate1());
            paramsNo++;
        }

        if(acc.getAssetOriginValue()!=null&&!acc.getAssetOriginValue().equals("")){
            sql.append(" and f.asset_origin_value>=?"+paramsNo);
            params.put(paramsNo,acc.getAssetOriginValue());
            paramsNo++;
        }
        if(acc.getAssetOriginValue1()!=null&&!acc.getAssetOriginValue1().equals("")){
            sql.append(" and f.asset_origin_value<=?"+paramsNo);
            params.put(paramsNo,acc.getAssetOriginValue1());
            paramsNo++;
        }
        if(acc.getAssetNetValue()!=null&&!acc.getAssetNetValue().equals("")){
            sql.append(" and f.asset_net_value>=?"+paramsNo);
            params.put(paramsNo,acc.getAssetNetValue());
            paramsNo++;
        }
        if(acc.getAssetNetValue1()!=null&&!acc.getAssetNetValue1().equals("")){
            sql.append(" and f.asset_net_value<=?"+paramsNo);
            params.put(paramsNo,acc.getAssetNetValue1());
            paramsNo++;
        }
        if(acc.getEndDepreMoney()!=null&&!acc.getEndDepreMoney().equals("")){
            sql.append(" and f.end_depre_money>=?"+paramsNo);
            params.put(paramsNo,acc.getEndDepreMoney());
            paramsNo++;
        }
        if(acc.getEndDepreMoney1()!=null&&!acc.getEndDepreMoney1().equals("")){
            sql.append(" and f.end_depre_money<=?"+paramsNo);
            params.put(paramsNo,acc.getEndDepreMoney1());
            paramsNo++;
        }
        if(acc.getDepYears()!=null&&!acc.getDepYears().equals("")){
            sql.append(" and f.dep_years>=?"+paramsNo);
            params.put(paramsNo,acc.getDepYears());
            paramsNo++;
        }
        if(acc.getDepYears1()!=null&&!acc.getDepYears1().equals("")){
            sql.append(" and f.dep_years<=?"+paramsNo);
            params.put(paramsNo,acc.getDepYears1());
            paramsNo++;
        }
        if(acc.getCreateOper()!=null && !acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from userinfo where user_name like ?"+acc.getCreateOper()+")");
            params.put(paramsNo,"%"+acc.getCreateOper()+"%");
            paramsNo++;
        }
        if(acc.getDepreToDate()!=null&&!acc.getDepreToDate().equals("")){
            sql.append(" and f.depre_to_date<=?"+paramsNo);
            params.put(paramsNo,acc.getDepreToDate());
            paramsNo++;
            sql.append(" and f.use_start_date <=?"+paramsNo);
            params.put(paramsNo,acc.getDepreToDate());
            paramsNo++;
        }
        //变更信息
        if(acc.getChangeMessage()!=null&&!acc.getChangeMessage().equals("")){
            if(acc.getChangeMessage().equals("1")){
                //无变更信息 exists
                sql.append(" and not exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }
            if(acc.getChangeMessage().equals("2")){
                //有变更信息
                sql.append(" and  exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
            }
        }
        //使用部门unitCode
        if(acc.getUnitCode()!=null&&!acc.getUnitCode().equals("")){
            sql.append(" and f.unit_code=?"+paramsNo);
            params.put(paramsNo, acc.getUnitCode());
            paramsNo++;
        }
        if(acc.getAssetType()!=null&&!acc.getAssetType().equals("")){
            sql.append(" and f.asset_type like ?"+paramsNo);
            params.put(paramsNo,acc.getAssetType()+"%");
            paramsNo++;
        }
        //sourceFlag
        if(acc.getSourceFlag()!=null&&!acc.getSourceFlag().equals("")){
            sql.append(" and f.source_flag=?"+paramsNo);
            params.put(paramsNo,acc.getSourceFlag());
            paramsNo++;
        }
        if(acc.getStopCard()!=null&&!acc.getStopCard().equals("")){
            if(acc.getStopCard().equals("0")){
                //不包括已停用卡片
                sql.append(" and f.use_flag='1' ");
            }
        }
        if(acc.getCleanCard()!=null&&!acc.getCleanCard().equals("")){
            if(acc.getCleanCard().equals("1")){
                //不包含已清理卡片
                sql.append(" and f.clear_flag='0'");
            }else if(acc.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag='1'");

            }
        }
        if(acc.getIsNotVoucher()!=null&&!acc.getIsNotVoucher().equals("")){
            if(acc.getIsNotVoucher().equals("0")){
                //没有生成凭证
                sql.append(" and (f.voucher_no='' or f.voucher_no  is  null)");
            }else{
                sql.append(" and f.voucher_no !='' and f.voucher_no is not null");

            }
        }
        if(acc.getChangeDate()!=null&&!acc.getChangeDate().equals("")){
            sql.append(" and f.card_code in(select c.card_code from accAssetInfoChange c where  c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_date<=?"+paramsNo+")");
            params.put(paramsNo,acc.getChangeDate());
            paramsNo++;
        }
        if(acc.getChangeType()!=null&&!acc.getChangeType().equals("")){
            sql.append(" and f.card_code in(select c.card_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.change_type=?"+paramsNo+")");
            params.put(paramsNo,acc.getChangeType());
            paramsNo++;
        }
        sql.append(" order by f.card_code asc");
        List<?> res = accAssetInfoRepository.queryBySqlSC(sql.toString(),params);
        return res;
    }
    /**
     * 固定资产卡片删除
     * @param
     * @param
     * @param
     * @param
     * @param
     * @param
     * @return
//     */
//    @Transactional
//    @Override
//    public InvokeResult delete(AccAssetInfoId acc) {
//        try {
//
//                accAssetInfoRepository.delete(acc.getCenterCode(), acc.getBranchCode(), acc.getAccBookType(), acc.getAccBookCode(), acc.getCodeType(), acc.getCardCode());
//
//            return InvokeResult.success();
//        }catch (Exception e){
//            logger.error("固定资产卡片删除异常", e);
//            return InvokeResult.failure("固定资产卡片删除失败，请联系管理员！");
//        }
//    }
    @Transactional
    @Override
    public InvokeResult delete(List<AccAssetInfoId> list) {
        String voucherlist="";
        for (AccAssetInfoId acc : list) {
            AccAssetInfo lists=accAssetInfoRepository.findById(acc).get();
            StringBuffer adSql = new StringBuffer();
//            adSql.append("select * from accdepre a where a.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  a.acc_book_type = '"+ acc.getAccBookType() +"' " +
//                    " and a.acc_book_code = '"+ acc.getAccBookCode() +"' and a.code_type = '"+acc.getCodeType()+"' " +
//                    " and a.asset_code = '"+ lists.getAssetCode() +"'");
//            List<?> adList = accAssetInfoRepository.queryBySql(adSql.toString(), AccDepre.class);
            List<AccDepre> adList = accDepreRepository.queryAccDepreInfo(CurrentUser.getCurrentLoginManageBranch(),acc.getAccBookType(),acc.getAccBookCode(),acc.getCodeType(),lists.getAssetCode(),null);
            if((lists.getVoucherNo()!=null&&!lists.getVoucherNo().equals(""))||adList.size()>0){
                voucherlist=voucherlist+" "+acc.getCardCode();
            }
        }
        if(!voucherlist.equals("")){
            return InvokeResult.failure("编码是'" + voucherlist + "'的卡片已计提过折旧或已经生成凭证，不能删除，请重新操作!");
        }
        for (AccAssetInfoId acc : list) {
            accAssetInfoRepository.delete(acc.getCenterCode(), acc.getBranchCode(), acc.getAccBookType(), acc.getAccBookCode(), acc.getCodeType(), acc.getCardCode());
        }
        accAssetInfoRepository.flush();
        return InvokeResult.success();
    }

    /**
     * 固定资产类别
     * @param value
     * @return
     */
    @Override
    public List<?> qryAssetTypeTree(String value){
        List resultListAll = new ArrayList();
        value=null;
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer assetTypeSql = new StringBuffer("select asset_type as id, asset_simple_name as text, asset_type as mid,end_flag as endFlag from accassetcodetype where level=1" );
//                " and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");
        assetTypeSql.append(" and acc_book_type =?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        assetTypeSql.append(" and acc_book_code =?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        if(value!=null && !("").equals(value)){
            assetTypeSql.append(" and asset_complex_name like ?"+paramsNo);
            params.put(paramsNo,"%"+value+"%");
            paramsNo++;
        }
        List<?> assetTypeList = accAssetCodeTypeRepository.queryBySqlSC(assetTypeSql.toString(),params);
//        List<?> assetTypeList = accAssetCodeTypeRepository.queryAccAssetCodeTypeByAccBookTypeAndAccBookCode(CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount());
        if(assetTypeList!=null&&assetTypeList.size()>0){
            for(Object o : assetTypeList){
                Map assetTypeMap = new HashMap();
                assetTypeMap.putAll((Map) o);
                List list = qryChildrenForTree((String)assetTypeMap.get("id"),value);
                if(!("").equals(value) && value!=null){
                    if(list.size() == 0 && !(assetTypeMap.get("text").toString().contains(value))){
                        continue;
                    }
                }
                //没有下级的非末级去掉
                if(list.size() == 0&&assetTypeMap.get("endFlag").equals("1")){
                    continue;
                }
                if(list.size() != 0){
                    assetTypeMap.put("id",assetTypeMap.get("mid"));
                    assetTypeMap.put("text",assetTypeMap.get("text"));
                    assetTypeMap.put("children",list);
                    assetTypeMap.put("state","closed");

                }else{
                    assetTypeMap.put("id",assetTypeMap.get("mid"));
                    assetTypeMap.put("text",assetTypeMap.get("text"));
                   // assetTypeMap.put("children",null);
                   // assetTypeMap.put("state","closed");

                }
                resultListAll.add(assetTypeMap);
            }
        }
        return resultListAll;
    }

    public List<MenuInfoDTO> qryChildrenForTree(String id, String value){
        List list1 = new ArrayList();
//        StringBuffer sql = new StringBuffer("select asset_type as id, asset_simple_name as text, asset_type as mid,end_flag as endFlag from accassetcodetype where super_code = '"+ id +"'" +
//                " and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");
//        if(value!=null && !("").equals(value)){
//            sql.append(" and asset_complex_name like '%"+value+"%'");
//        }
//        List<?> list =  accAssetCodeTypeRepository.queryBySqlSC(sql.toString());
        List<?> list =  accAssetCodeTypeRepository.queryAccAssetCodeTypeBySuperCodeAndAccBookTypeAndAccBookCode(id,CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount());
        if(list!=null && list.size()>0 && !list.isEmpty()){
            for(Object obj : list){
                Map map = new HashMap();
                map.putAll((Map) obj);

                List list2 = qryChildrenForTree((String)map.get("id"),value);
                //没有下级的非末级去掉
                if(list2.size() == 0&&map.get("endFlag").equals("1")){
                    continue;
                }
                if(list2!=null && list2.size()>0 && !list2.isEmpty()){
                    map.put("id",map.get("mid"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                    map.put("state","closed");
                }else{
                    map.put("id",map.get("mid"));
                    map.put("text",map.get("text"));
                }
                list1.add(map);
            }
        }
        return list1;
    }

    /**
     * 判断当前卡片是否计提过
     * @param dto
     * @return
     */
    @Override
    public InvokeResult depreciation(AccAssetInfoDTO dto) {
        try{
            //判断当前卡片是否进行过计提
//            StringBuffer adSql = new StringBuffer();
//            adSql.append("select * from accdepre a where a.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and a.acc_book_type = '"+ dto.getAccBookType() +"' " +
//                    " and a.acc_book_code = '"+ dto.getAccBookCode() +"' and a.code_type = '"+dto.getCodeType()+"' " +
//                    " and a.asset_code = '"+ dto.getAssetCode() +"'");
//            List<?> adList = accAssetCodeTypeRepository.queryBySql(adSql.toString(), AccDepre.class);
            List<AccDepre> adList = accDepreRepository.queryAccDepreInfo(CurrentUser.getCurrentLoginManageBranch(),dto.getAccBookType(),dto.getAccBookCode(),dto.getCodeType(),dto.getAssetCode(),null);
            if(adList.size() > 0){//大于0 说明计提过折旧
                return InvokeResult.failure("当前卡片已进行过折旧，无法进行修改！");
            }
            return InvokeResult.success();
        }catch (Exception e){
            logger.error("固定资产卡片修改失败",e);
            return InvokeResult.failure("固定资产卡片修改失败，请联系管理员！");
        }
    }

    /**
     * 固定资产编号生成
     * @param assetType
     * @return
     */
    @Override
    public String fCode(String assetType) {
        try{
            int paramsNo = 1 ;
            Map<Integer,Object> params = new HashMap<>();
            StringBuffer sql = new StringBuffer();
            sql.append("select * from AccAssetInfo a where 1=1 " );
//                    "and   a.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and a.asset_type = '"+ assetType +"' and a.asset_code = " +
//                    "(select MAX(asset_code) from AccAssetInfo where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and asset_type = '"+ assetType +"')" +
//                    " and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");
            sql.append(" and  a.center_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append(" and  a.asset_type=?"+paramsNo);
            params.put(paramsNo,assetType);
            paramsNo++;
            sql.append(" and  a.asset_code=(select MAX(asset_code) from AccAssetInfo where center_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append(" and asset_type=?"+paramsNo);
            params.put(paramsNo,assetType);
            paramsNo++;
            sql.append(" and acc_book_type = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
            paramsNo++;
            sql.append(" and acc_book_code=?"+paramsNo+")");
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            List<?> list = accAssetInfoRepository.queryBySql(sql.toString(),params,AccAssetInfo.class);
            String assetCode;//固定资产编号
            if(list.size()>0){//基本信息表中有当前类别
                String assetcode1=((AccAssetInfo)list.get(0)).getAssetCode();
                //获取前
                String assetcode2=assetcode1.substring(0,assetcode1.length()-5);
                //获取后五位字符
                String assetcode3=String.valueOf(Integer.parseInt(assetcode1.substring(assetcode1.length()-5))+1);
                String assetcode4="";
                for(int i=0;i<5-assetcode3.length();i++){
                    assetcode4="0"+assetcode4;
                }
                assetCode = ((AccAssetInfo)list.get(0)).getAssetType()+assetcode4+assetcode3;
            }else{//基本信息表中没有当前类别
                assetCode = assetType + "00001";
            }
            return assetCode;
        }catch (Exception e){
            logger.error("固定资产编号生成异常",e);
            return null;
        }
    }
    /**
     * 固定资产折旧方法
     * @param assetType
     * @return
     */
    @Override
    public String deprMethod(String assetType) {
        try {
            int paramsNo = 1 ;
            Map<Integer,Object> params = new HashMap<>();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT c.code_name FROM AccAssetCodeType a LEFT JOIN codemanage c ON c.temp='折旧方法' WHERE a.dep_type=c.code_code " );
//                    "AND a.asset_type='" + assetType + "' " +
//                    "AND  a.acc_book_code='" + CurrentUser.getCurrentLoginAccount() + "' " +
//                    "AND a.acc_book_type='" + CurrentUser.getCurrentLoginAccountType() + "' ");
            String depType = null;//固定资产折旧方法
            sql.append(" AND a.asset_type= ?"+paramsNo);
            params.put(paramsNo,assetType);
            paramsNo++;
            sql.append(" AND a.acc_book_code=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append(" AND a.acc_book_type=?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
            paramsNo++;
            List<?> list = accAssetInfoRepository.queryBySql(sql.toString(),params);
            for (Object o : list) {
                 depType = (String) o;
            }
            return  depType;
        } catch (Exception e) {
            logger.error("固定资产编号生成异常", e);
            return null;
        }
    }


    /**
     * 固定资产卡片停用
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult stopUse(AccAssetInfoDTO dto) {
            AccAssetInfoId aaiid = new AccAssetInfoId();
            aaiid.setCardCode(dto.getCardCode());
            aaiid.setCodeType(dto.getCodeType());
            aaiid.setAccBookCode(dto.getAccBookCode());
            aaiid.setAccBookType(dto.getAccBookType());
            aaiid.setCenterCode(dto.getCenterCode());
            aaiid.setBranchCode(dto.getBranchCode());
            AccAssetInfo aai = accAssetInfoRepository.findById(aaiid).get();
            aai.setUseFlag("0");//设置为停用
            accAssetInfoRepository.save(aai);
            return InvokeResult.success();
    }

    @Override
   public String getNetSurplusRate(String assetType,String codeType){
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
      return   accAssetInfoRepository.getNetSurplusRate(accBookType,accBookCode,assetType,codeType);
    }


    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols) {
        ExcelUtil excelUtil = new ExcelUtil();
        int paramsNo = 1 ;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql =new StringBuffer();
        sql.append("select f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                " f.asset_type as  assetType, f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName, f.metric_name as metricName,\n" +
                "f.manufactor as manufactor,f.specification as specification,f.serial_number as serialNumber,f.use_start_date as useStartDate,f.quantity,f.source_flag as sourceFlag,"+
                "(select c.code_name from codemanage c where c.code_code=f.use_flag and c.code_type='useFlag') as useFlag ,f.storage_way as storageWay,f.asset_origin_value as assetOriginValue,f.asset_net_value as assetNetValue, f.dep_years as depYears,f.dep_type as depType,"+
                "f.impairment as impairment, f.added_tax as addedTax,f.sum,f.input_tax as inputTax,f.pay_way as payWay, f.pay_code as payCode,f.remains_value as remainsValue,f.remains_rate as remainsRate," +
                "f.predict_clear_fee as predictClearFee,f.formula_code as formulaCode,f.init_depre_amount as initDepreAmount,f.init_depre_money as initDepreMoney, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "f.clear_flag as clearFlag , f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch, f.clear_operator_code as clearOperatorCode,"+
                "f.voucher_no as voucherNo,f.depre_from_date as depreFromDate,f.depre_to_date as depreToDate,f.depre_flag as  depreFlag,f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp,"+
                "(select a.asset_simple_name from accassetcodetype a where  a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetTypeName ,"+
                " f.unit_code as unitCode,"+
                "(select o.special_name from specialinfo o where o.id=f.unit_code ) as unitCodeName,"+
                "(select c.code_name from codemanage c where c.code_type='organization' and c.code_code=f.organization ) as organization,"+
                " (select c.code_name from codemanage c where c.code_type='deprMethod' and c.code_code=f.dep_type ) as deprMethod,"+
                " (select special_name from specialinfo where f.unit_code = id) as specialName, " +
                "(select i.month_depre_money from  AccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code ORDER BY i.year_month_data desc limit 1 ) as monthDepreMoney   from AccAssetInfo  f where 1=1 " );
//                " and center_code = '"+CurrentUser.getCurrentLoginManageBranch()+"' and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");

        sql.append(" and center_code = ?"+ paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append(" and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append(" and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        AccAssetInfoDTO acc = new AccAssetInfoDTO();
        try {
            acc = new ObjectMapper().readValue(queryConditions, AccAssetInfoDTO.class);
        } catch (Exception e){
            e.printStackTrace();
        }

        /*if(acc.getCardCode()!=null&&!acc.getCardCode().equals("")){
            if(acc.getCardCode().length()<5){
                String cs=acc.getCardCode();
                for(int i=0;i<5-acc.getCardCode().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode(cs);
            }
            sql.append(" and f.card_code>='"+acc.getCardCode()+"'");
        }
        if(acc.getCardCode1()!=null&&!acc.getCardCode1().equals("")){
            if(acc.getCardCode1().length()<5){
                String cs=acc.getCardCode1();
                for(int i=0;i<5-acc.getCardCode1().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode1(cs);
            }
            sql.append(" and f.card_code<='"+acc.getCardCode1()+"'");
        }
        if(acc.getAssetCode1()!=null&&!acc.getAssetCode1().equals("")){
            sql.append(" and f.asset_code>='"+acc.getAssetCode1()+"'");
        }
        if(acc.getAssetCode2()!=null&&!acc.getAssetCode2().equals("")){
            sql.append(" and f.asset_code<='"+acc.getAssetCode2()+"'");
        }
        if(acc.getUseStartDate()!=null&&!acc.getUseStartDate().equals("")){
            sql.append(" and f.use_start_date>='"+acc.getUseStartDate()+"'");
        }
        if(acc.getUseStartDate1()!=null&&!acc.getUseStartDate1().equals("")){
            sql.append(" and f.use_start_date<='"+acc.getUseStartDate1()+"'");
        }

        if(acc.getAssetOriginValue()!=null&&!acc.getAssetOriginValue().equals("")){
            sql.append(" and f.asset_origin_value>='"+acc.getAssetOriginValue()+"'");
        }
        if(acc.getAssetOriginValue1()!=null&&!acc.getAssetOriginValue1().equals("")){
            sql.append(" and f.asset_origin_value<='"+acc.getAssetOriginValue1()+"'");
        }
        if(acc.getAssetNetValue()!=null&&!acc.getAssetNetValue().equals("")){
            sql.append(" and f.asset_net_value>='"+acc.getAssetNetValue()+"'");
        }
        if(acc.getAssetNetValue1()!=null&&!acc.getAssetNetValue1().equals("")){
            sql.append(" and f.asset_net_value<='"+acc.getAssetNetValue1()+"'");
        }
        if(acc.getEndDepreMoney()!=null&&!acc.getEndDepreMoney().equals("")){
            sql.append(" and f.end_depre_money>='"+acc.getEndDepreMoney()+"'");
        }
        if(acc.getEndDepreMoney1()!=null&&!acc.getEndDepreMoney1().equals("")){
            sql.append(" and f.end_depre_money<='"+acc.getEndDepreMoney1()+"'");
        }
        if(acc.getDepYears()!=null&&!acc.getDepYears().equals("")){
            sql.append(" and f.dep_years>='"+acc.getDepYears()+"'");
        }
        if(acc.getDepYears1()!=null&&!acc.getDepYears1().equals("")){
            sql.append(" and f.dep_years<='"+acc.getDepYears1()+"'");
        }
        if(acc.getCreateOper()!=null && !acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from roleinfo where role_name like '%"+acc.getCreateOper()+"%')");
        }
        if(acc.getDepreToDate()!=null&&!acc.getDepreToDate().equals("")){
            sql.append(" and f.depre_to_date<='"+acc.getDepreToDate()+"'  and f.use_start_date<='"+acc.getDepreToDate()+"'");
        }
        //变更信息
        if(acc.getChangeMessage()!=null&&!acc.getChangeMessage().equals("")){
            if(acc.getChangeMessage().equals("1")){
                //无变更信息 exists
                sql.append(" and not exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }
            if(acc.getChangeMessage().equals("2")){
                //有变更信息
                sql.append(" and  exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
            }
        }
        //使用部门unitCode
        if(acc.getUnitCode()!=null&&!acc.getUnitCode().equals("")){
            sql.append(" and f.unit_code='"+acc.getUnitCode()+"'");
        }
        if(acc.getAssetType()!=null&&!acc.getAssetType().equals("")){
            sql.append(" and f.asset_type='"+acc.getAssetType()+"'");
        }
        //sourceFlag
        if(acc.getSourceFlag()!=null&&!acc.getSourceFlag().equals("")){
            sql.append(" and f.source_flag='"+acc.getSourceFlag()+"'");
        }
        if(acc.getStopCard()!=null&&!acc.getStopCard().equals("")){
            if(acc.getStopCard().equals("0")){
                //不包括已停用卡片
                sql.append(" and f.use_flag='1' ");
            }
        }
        if(acc.getCleanCard()!=null&&!acc.getCleanCard().equals("")){
            if(acc.getCleanCard().equals("1")){
                //不包含已清理卡片
                sql.append(" and f.clear_flag='0'");
            }else if(acc.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag='1'");

            }
        }*/
        if(acc.getCardCode()!=null&&!acc.getCardCode().equals("")){
            if(acc.getCardCode().length()<5){
                String cs=acc.getCardCode();
                for(int i=0;i<5-acc.getCardCode().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode(cs);
            }
            sql.append(" and f.card_code>=?" + paramsNo );
            params.put(paramsNo,acc.getCardCode());
            paramsNo++;
        }
        if(acc.getCardCode1()!=null&&!acc.getCardCode1().equals("")){
            if(acc.getCardCode1().length()<5){
                String cs=acc.getCardCode1();
                for(int i=0;i<5-acc.getCardCode1().length();i++){
                    cs="0"+cs;
                }
                acc.setCardCode1(cs);
            }
            sql.append(" and f.card_code<=?" + paramsNo);
            params.put(paramsNo,acc.getCardCode1());
            paramsNo++;
        }
        if(acc.getAssetCode1()!=null&&!acc.getAssetCode1().equals("")){
            sql.append(" and f.asset_code>=?" + paramsNo);
            params.put(paramsNo,acc.getAssetCode1());
            paramsNo++;
        }
        if(acc.getAssetCode2()!=null&&!acc.getAssetCode2().equals("")){
            sql.append(" and f.asset_code<=?" + paramsNo);
            params.put(paramsNo,acc.getAssetCode2());
            paramsNo++;
        }
        if(acc.getUseStartDate()!=null&&!acc.getUseStartDate().equals("")){
            sql.append(" and f.use_start_date>=?" + paramsNo);
            params.put(paramsNo,acc.getUseStartDate());
            paramsNo++;
        }
        if(acc.getUseStartDate1()!=null&&!acc.getUseStartDate1().equals("")){
            sql.append(" and f.use_start_date<=?" + paramsNo);
            params.put(paramsNo,acc.getUseStartDate1());
            paramsNo++;
        }

        if(acc.getAssetOriginValue()!=null&&!acc.getAssetOriginValue().equals("")){
            sql.append(" and f.asset_origin_value>=?" + paramsNo);
            params.put(paramsNo,acc.getAssetOriginValue());
            paramsNo++;
        }
        if(acc.getAssetOriginValue1()!=null&&!acc.getAssetOriginValue1().equals("")){
            sql.append(" and f.asset_origin_value<=?" + paramsNo);
            params.put(paramsNo,acc.getAssetOriginValue1());
            paramsNo++;
        }
        if(acc.getAssetNetValue()!=null&&!acc.getAssetNetValue().equals("")){
            sql.append(" and f.asset_net_value>=?" + paramsNo);
            params.put(paramsNo,acc.getAssetNetValue());
            paramsNo++;
        }
        if(acc.getAssetNetValue1()!=null&&!acc.getAssetNetValue1().equals("")){
            sql.append(" and f.asset_net_value<=?" + paramsNo);
            params.put(paramsNo,acc.getAssetNetValue1());
            paramsNo++;
        }
        if(acc.getEndDepreMoney()!=null&&!acc.getEndDepreMoney().equals("")){
            sql.append(" and f.end_depre_money>=?" + paramsNo);
            params.put(paramsNo,acc.getEndDepreMoney());
            paramsNo++;
        }
        if(acc.getEndDepreMoney1()!=null&&!acc.getEndDepreMoney1().equals("")){
            sql.append(" and f.end_depre_money<=?" + paramsNo);
            params.put(paramsNo,acc.getEndDepreMoney1());
            paramsNo++;
        }
        if(acc.getDepYears()!=null&&!acc.getDepYears().equals("")){
            sql.append(" and f.dep_years>=?" + paramsNo);
            params.put(paramsNo,acc.getDepYears());
            paramsNo++;
        }
        if(acc.getDepYears1()!=null&&!acc.getDepYears1().equals("")){
            sql.append(" and f.dep_years<=?" + paramsNo);
            params.put(paramsNo,acc.getDepYears1());
            paramsNo++;
        }
        if(acc.getCreateOper()!=null && !acc.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from userinfo where user_name like ?" + paramsNo+")");
            params.put(paramsNo,"%"+acc.getCreateOper()+"%");
            paramsNo++;
        }
        if(acc.getDepreToDate()!=null&&!acc.getDepreToDate().equals("")){
            sql.append(" and f.depre_to_date<=?"+paramsNo);
            params.put(paramsNo,acc.getDepreToDate());
            paramsNo++;
            sql.append(" and f.use_start_date<=?"+paramsNo);
            params.put(paramsNo,acc.getDepreToDate());
            paramsNo++;
        }
        //变更信息
        if(acc.getChangeMessage()!=null&&!acc.getChangeMessage().equals("")){
            if(acc.getChangeMessage().equals("1")){
                //无变更信息 exists
                sql.append(" and not exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }
            if(acc.getChangeMessage().equals("2")){
                //有变更信息
                sql.append(" and  exists(select c.change_code from accAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
            }
        }
        //使用部门unitCode
        if(acc.getUnitCode()!=null&&!acc.getUnitCode().equals("")){
            sql.append(" and f.unit_code=?" + paramsNo );
            params.put(paramsNo,acc.getUnitCode());
            paramsNo++;
        }
        if(acc.getAssetType()!=null&&!acc.getAssetType().equals("")){
            sql.append(" and f.asset_type like ?"+paramsNo);
            params.put(paramsNo,acc.getAssetType()+"%");
            paramsNo++;
        }
        //sourceFlag
        if(acc.getSourceFlag()!=null&&!acc.getSourceFlag().equals("")){
            sql.append(" and f.source_flag=?" + paramsNo);
            params.put(paramsNo,acc.getSourceFlag());
            paramsNo++;
        }
        if(acc.getStopCard()!=null&&!acc.getStopCard().equals("")){
            if(acc.getStopCard().equals("0")){
                //不包括已停用卡片
                sql.append(" and f.use_flag='1' ");
            }
        }
        if(acc.getCleanCard()!=null&&!acc.getCleanCard().equals("")){
            if(acc.getCleanCard().equals("1")){
                //不包含已清理卡片
                sql.append(" and f.clear_flag='0'");
            }else if(acc.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag='1'");

            }
        }
        try {
            // 根据条件查询导出数据集
            List<?> dataList = accAssetInfoRepository.queryBySqlSC(sql.toString(),params);
            // 导出
            excelUtil.exportu_(request, response, name, cols, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 启用日期校验
     * @param useStartDate
     * @return
     */
    @Override
    public InvokeResult checkDate(String useStartDate) {
        String date = useStartDate.substring(0,4)+useStartDate.substring(5,7);//2019-01-01
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql = new StringBuffer();
        /*sql.append("select * from (select year_month_date from AccGCheckInfo where" +
                " acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' "+
                ") t1 where t1.year_month_date = '"+ date +"'");
        List<?> list = accAssetInfoRepository.queryBySqlSC(sql.toString());*/
        sql.append("SELECT * FROM accmonthtrace a WHERE 1=1");
        sql.append(" AND a.center_code=?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append(" and a.acc_book_type=?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append(" AND a.acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        sql.append(" AND a.year_month_date = ?"+paramsNo);
        params.put(paramsNo,date);
        paramsNo++;
        List<AccMonthTrace> list = (List<AccMonthTrace>) accAssetInfoRepository.queryBySql(sql.toString(),params, AccMonthTrace.class);
        if(list.size()>0){
            if ("3".equals(list.get(0).getAccMonthStat())||"5".equals(list.get(0).getAccMonthStat())) {
                return InvokeResult.failure("启用时间对应的会计期间已结转！");
            }
            //启用时间对应的会计期间是否已计提
//            sql.setLength(0);
//            sql.append("SELECT * FROM accgcheckinfo a WHERE 1=1 AND a.flag !='0'");
//            sql.append(" AND a.center_code = '"+CurrentUser.getCurrentLoginManageBranch()+"'");
//            sql.append(" AND a.acc_book_code = '"+CurrentUser.getCurrentLoginAccount()+"'");
//            sql.append(" AND a.year_month_date = '"+date+"'");
//            List<AccGCheckInfo> list2 = (List<AccGCheckInfo>) accAssetInfoRepository.queryBySql(sql.toString(), AccGCheckInfo.class);
            List<AccGCheckInfo> list2 = accGCheckInfoRepository.findAccgcheckinfoByCenterCodeAndAccBookCodeAndYearMonthDateAndFlag(CurrentUser.getCurrentLoginManageBranch(), CurrentUser.getCurrentLoginAccount(), date);
            if (list!=null&&list2.size()>0) {
                return InvokeResult.failure("启用时间对应的会计期间已折旧计提！");
            }
            return InvokeResult.success();
        }else{
            return InvokeResult.failure("启用时间必须在会计期间内！");
        }
    }
    /**
     * 变动信息查询
     * @param
     * @return
     */
  @Override
    public List<?> getChangeMessage(AccAssetInfoDTO acc) {
        StringBuffer sql = new StringBuffer();
      int paramsNo = 1;
      Map<Integer,Object> params = new HashMap<>();

      sql.append("select f.handle_date,f.change_type as changeType,f.change_reason as changeReason ,f.change_code as changeCode,f.change_date as changeDate,f.change_old_data as changeOldData,\n" +
                "f.change_new_data as changeNewData, (CASE WHEN SUBSTRING_INDEX(f.change_old_data, ':', 1)  LIKE '%#%' THEN '' ELSE SUBSTRING_INDEX(f.change_old_data, ':', 1) END) as changeOldDataRight , " +
                "(select c.code_name from codemanage c where c.code_type='fixChangeType' and c.code_code=f.change_type ) as changeTypeName, " +
                "(CASE WHEN SUBSTRING_INDEX(f.change_new_data, ':', 1)  LIKE '%null%' THEN '' ELSE SUBSTRING_INDEX(f.change_new_data, ':', 1) END) as changeNewDataRight from AccAssetInfoChange f where 1=1 " );
//              " f.center_code='"+acc.getCenterCode()+"' and f.branch_code='" +acc.getBranchCode()+"'"+
//                " and f.acc_book_type='"+acc.getAccBookType()+"' and f.acc_book_code='"+acc.getAccBookCode()+"' and f.code_type='"+acc.getCodeType()+"' and f.card_code='"+acc.getCardCode()+"' ");

      sql.append(" and f.center_code=?"+paramsNo);
      params.put(paramsNo,acc.getCenterCode());
      paramsNo++;
      sql.append(" and f.branch_code=?"+paramsNo);
      params.put(paramsNo,acc.getBranchCode());
      paramsNo++;
      sql.append(" and f.acc_book_type=?"+paramsNo);
      params.put(paramsNo,acc.getAccBookType());
      paramsNo++;
      sql.append(" and f.acc_book_code=?"+paramsNo);
      params.put(paramsNo,acc.getAccBookCode());
      paramsNo++;
      sql.append(" and f.code_type=?"+paramsNo);
      params.put(paramsNo,acc.getCodeType());
      paramsNo++;
      sql.append(" and f.card_code=?"+paramsNo);
      params.put(paramsNo,acc.getCardCode());
      paramsNo++;



      return  accAssetInfoRepository.queryBySqlSC(sql.toString(),params);

    }

    /**
     * 折旧至日期 时间校验
     * @param depreUtilDate
     * @return
     */
    @Override
    public InvokeResult checkDepreToDate(String depreUtilDate) {
        //先获取到 固定资产基本信息表 中的最大折旧至日期
        StringBuffer sql = new StringBuffer();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        sql.append("select * from accassetinfo where 1=1 ");

        sql.append(" and center_code=?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append(" and depre_to_date in (select MAX(depre_to_date) as depre_to_date from accassetinfo)");
        sql.append(" and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append(" and acc_book_code =?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;


        List<?> list = accAssetInfoRepository.queryBySql(sql.toString(), params ,AccAssetInfo.class);
        if(list.size()>0){
            String depreToDate = ((AccAssetInfo)list.get(0)).getDepreToDate().substring(0,4)+((AccAssetInfo)list.get(0)).getDepreToDate().substring(5,7);
            depreUtilDate = depreUtilDate.substring(0,4)+depreUtilDate.substring(5,7);
            //判断当前日期是否 小于等于 最大折旧至日期
            if(Integer.parseInt(depreUtilDate) <= Integer.parseInt(depreToDate)){
                //小于等于
                return InvokeResult.success();
            }else{
                //大于
                return InvokeResult.failure("折旧至日期应小于系统中最大折旧至日期！", depreToDate);
            }
        }
        return InvokeResult.success();
    }

    @Override
    public List<?> getFixedAssetCodeList(String fixedAssetType) {
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
//        StringBuffer sql = new StringBuffer("SELECT ac.asset_code AS 'value', ac.asset_code AS 'text' FROM accassetinfo " +
//                " ac WHERE ac.center_code = '"+centerCode+"' AND ac.branch_code = '"+branchCode+"' AND ac.acc_book_type = '"+accBookType+
//                "' AND ac.acc_book_code = '"+accBookCode+"' AND ac.asset_type = '"+fixedAssetType+"'ORDER BY VALUE ");
//        List<?> list = accAssetInfoRepository.queryBySqlSC(sql.toString());
        List<?> list = accAssetInfoRepository.queryAccAssetInfoByByChooseMessageOrderByValue(centerCode,branchCode,accBookType,accBookCode,fixedAssetType);
        return list;
    }

    @Override
    public List<?> qryBankPayMethodTree(String value) {
        List resultListAll=new ArrayList();
        long start = System.currentTimeMillis();
        // 找到的是四大类(限定为code_code = 1)为 资产
        String subjectTypeSql = "select c.code_code as id,c.code_name as text from codemanage c where c.code_type = 'subjectType' and c.code_code = '1' order by id";
        List<?> subjectTypeList = accAssetInfoRepository.queryBySqlSC(subjectTypeSql);
        if (subjectTypeList != null && subjectTypeList.size() > 0){
            //  查询存在下级的科目id(限定1002科目，all_subject like '1002%')
            int number  = 1;
            Map<Integer,Object> maps = new HashMap<>();
            StringBuffer superSql = new StringBuffer("select distinct s.super_subject as superSubject from subjectinfo s where 1=1 and s.super_subject != '' and s.super_subject is not null and s.all_subject like '1002%'");
            superSql.append(" and s.account = ?"+number);
            maps.put(number,CurrentUser.getCurrentLoginAccount());
            List<?> superList = accAssetInfoRepository.queryBySqlSC(superSql.toString(),maps);
//            List<?> superList = accAssetInfoRepository.queryAccAssetInfoBy(CurrentUser.getCurrentLoginAccount());
            Set<String> superIdSet = new HashSet<>();
            if (superList!=null&&superList.size()>0) {
                for (Object obj: superList) {
                    //	把对应的父类id存到set集合中
                    superIdSet.add(((Map)obj).get("superSubject").toString());
                }
            }

            for(Object o  : subjectTypeList){
                List resultList = new ArrayList();
                Map subjectTypeMap = new HashMap();
                subjectTypeMap.putAll((Map) o);
                int sitsNo = 1;
                Map<Integer,Object> sits = new HashMap<>();
                //  通过四大类类型(subject_type字段) 找出对应4位编码(编订 ：m.subject_code = '1002')
                StringBuffer sql=new StringBuffer(" select cast(m.id as char) as id,m.subject_name as text ,m.subject_code as mid,m.end_flag as endFlag from subjectinfo m " +
                        "where (m.super_subject is null or m.super_subject= '') " );
                sql.append(" and m.subject_type = ?"+sitsNo);
                sits.put(sitsNo,subjectTypeMap.get("id"));
                sitsNo++;
                sql.append(" and m.account =?"+sitsNo);
                sits.put(sitsNo,CurrentUser.getCurrentLoginAccount());
                sitsNo++;
                sql.append(" and m.subject_code = '1002' order by concat(m.all_subject,m.subject_code)");
                List<?> list = accAssetInfoRepository.queryBySqlSC(sql.toString(),sits);
//                List<?> list = accAssetInfoRepository.queryAccAssetInfoBySubjectTypeAndAccount((String) subjectTypeMap.get("id"),CurrentUser.getCurrentLoginAccount());
                for(Object obj : list){
                    Map map = new HashMap();
                    map.putAll((Map) obj);
                    List<?> list2 = null;
                    if(superIdSet.contains(map.get("id").toString())){
                        list2 = qryChildrenForCheck((String) map.get("id"),value,superIdSet);
                    }
                    map.put("id",map.get("mid"));
                    map.put("text",map.get("text"));

                    if(list2 != null && list2.size() != 0){
                        map.put("children",list2);
                        map.put("state","closed");
                        resultList.add(map);
                    }else if ("0".equals(map.get("endFlag"))){
                        //  无子级，但为末级
                        resultList.add(map);
                    }else{
                        //  不需要
                    }

                    if(resultList != null && resultList.size() > 0){
                        subjectTypeMap.put("children",resultList);
                        subjectTypeMap.put("state","closed");
                        resultListAll.add(subjectTypeMap);
                    }
                }
            }
            System.out.println("执行用时时间："+(System.currentTimeMillis()-start) + "ms");
        }
        return resultListAll;
    }

    @Override
    public List<?> qryUnitCodeByUseFlagOne() {
        return accAssetInfoRepository.qryUnitCodeByUseFlagOne(CurrentUser.getCurrentLoginAccount());
    }

    private List<MenuInfoDTO> qryChildrenForCheck(String id,String value,Set<String> superIdSet){
        List list1=new ArrayList();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        //  递归调用 通过父级id，找到对应子级信息，形成list集合
        StringBuffer sql=new StringBuffer();
        sql.append(" select cast(m.id as char) as id,m.subject_name as text,m.subject_code as mid,m.end_flag as endFlag from subjectinfo m where 1=1 ");
        sql.append(" and m.super_subject = ?"+paramsNo);
        params.put(paramsNo,id);
        paramsNo++;
        if(value!=null&&!"".equals(value)){
            sql.append(" and m.subject_name like ?"+paramsNo);
            params.put(paramsNo,"%"+value+"%");
            paramsNo++;
        }
        sql.append(" order by concat(m.all_subject,m.subject_code)  ");
        List<?> list =accAssetInfoRepository.queryBySqlSC(sql.toString(),params);
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map = new HashMap();
                map.putAll((Map) obj);

                List<?> list2 = null;
                if (superIdSet.contains(map.get("id").toString())) {
                    list2 = qryChildrenForCheck((String)map.get("id"),value,superIdSet);
                }
                if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                    map.put("id",map.get("mid"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                    map.put("state","closed");
                    //map.put("checked", map.get("mid")!=null&&!map.get("mid").equals("")?true:false);
                    list1.add(map);
                } else if ("0".equals(map.get("endFlag"))){
                    //无子级，但为末级
                    map.put("id",map.get("mid"));
                    map.put("text",map.get("text"));
                    //map.put("checked", map.get("mid")!=null&&!map.get("mid").equals("")?true:false);
                    list1.add(map);
                } else {
                    //不需要
                }
            }
        }
        return list1;
    }

    /**
     * 固定资产折旧状态修改
     * @param cardCode
     * @param depreFlag
     * @return
     */
    @Transactional
    @Override
    public InvokeResult zjUpdate(String cardCode, String depreFlag) {
            String centerCode = CurrentUser.getCurrentLoginManageBranch();
            String branchCode = CurrentUser.getCurrentLoginManageBranch();
            AccAssetInfoId aaid = new AccAssetInfoId();
            aaid.setCenterCode(centerCode);
            aaid.setBranchCode(branchCode);
            aaid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
            aaid.setAccBookCode(CurrentUser.getCurrentLoginAccount());
            aaid.setCodeType("21");
            aaid.setCardCode(cardCode);
            AccAssetInfo aa = accAssetInfoRepository.findById(aaid).get();
            aa.setDepreFlag(depreFlag);
            aa.setUpdateTime(CurrentTime.getCurrentTime());//修改时间
            aa.setUpdateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));//修改人
            accAssetInfoRepository.save(aa);//保存
            return InvokeResult.success("修改成功！");

    }

    /**
     * 复制操作下 获取资产编号
     * @param assetType
     * @return
     */
    @Override
    public InvokeResult copAssetCode(String assetType) {
        try {
//            StringBuffer sql = new StringBuffer();
            String assetCode = assetType+"00001";
//            sql.append("select MAX(asset_code) from accassetinfo where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  asset_type = '"+ assetType +"' " +
//                    "and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"'");
//            List<?> list = accAssetInfoRepository.queryBySql(sql.toString());
            List<?> list = accAssetInfoRepository.queryAccAssetInfoMAXByChooseMessage(CurrentUser.getCurrentLoginManageBranch(),assetType,CurrentUser.getCurrentLoginAccount(),CurrentUser.getCurrentLoginAccountType());
            if(list.size()>0){
                /*assetCode = String.valueOf(Integer.parseInt(list.get(0).toString())+1);//当前类别下最大资产编号+1*/
                String temp = list.get(0).toString().substring(assetType.length());
                assetCode = assetType + String.format("%05d", Integer.parseInt(temp)+1);
            }
            return InvokeResult.success(assetCode);
        }catch (Exception e){
            logger.error("复制操作下获取资产编号失败",e);
            return InvokeResult.success("资产编号获取失败！");
        }
    }



}
