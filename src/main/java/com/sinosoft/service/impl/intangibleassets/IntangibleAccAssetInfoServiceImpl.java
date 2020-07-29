package com.sinosoft.service.impl.intangibleassets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.account.AccMonthTrace;
import com.sinosoft.domain.fixedassets.AccAssetCodeType;
import com.sinosoft.domain.fixedassets.AccAssetInfo;
import com.sinosoft.domain.fixedassets.AccAssetInfoId;
import com.sinosoft.domain.fixedassets.AccDepre;
import com.sinosoft.domain.intangibleassets.*;
import com.sinosoft.dto.MenuInfoDTO;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetCodeTypeRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetInfoRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccDepreRepository;
import com.sinosoft.repository.intangibleassets.IntangibleChangeManageRepository;
import com.sinosoft.service.fixedassets.CategoryCodingService;
import com.sinosoft.service.impl.fixedassets.FixedassetsCardServiceImpl;
import com.sinosoft.service.intangibleassets.IntangibleAccAssetInfoService;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class IntangibleAccAssetInfoServiceImpl implements IntangibleAccAssetInfoService {
    private Logger logger = LoggerFactory.getLogger(IntangibleAccAssetInfoServiceImpl.class);
    @Resource
    private IntangibleAccAssetInfoRepository intangibleAccAssetInfoRepository ;
    @Resource
    private IntangibleAccDepreRepository intangibleAccDepreRepository ;
    @Resource
    private IntangibleAccAssetCodeTypeRepository intangibleAccAssetCodeTypeRepository;
    @Resource
    private IntangibleChangeManageRepository intangibleChangeManageRepository;
    @Resource
    private CategoryCodingService categoryCodingService ;

    @Override
    public Page<?> qryIntangAssetInfo(int page,int rows,IntangibleAccAssetInfoDTO intang) {

        Page<?> result = categoryCodingService.getPage(page,rows,getIntangibleCardList(intang));
        return result;

    }

    @Override
    public List<?> getIntangibleCardList(IntangibleAccAssetInfoDTO intang) {
        StringBuffer sql=new StringBuffer();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        sql.append("select f.depre_from_date as depreFromDate, f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                " f.asset_type as  assetType, f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName,f.depre_to_date as depreToDate, \n" +
                "f.impairment as impairment, f.depre_flag as  depreFlag, f.added_tax as  addedTax, f.input_tax as inputTax, f.sum, f.remains_rate as remainsRate, f.init_depre_amount as initDepreAmount, f.init_depre_money as initDepreMoney,\n" +
                "f.use_flag as useFlag , f.asset_origin_value as assetOriginValue, f.asset_net_value as assetNetValue,\n" +
                "f.dep_type as depType, "+
//                " (select code_name from codemanage c where c.code_type = 'deprMethod' and c.code_code = f.dep_type) as depType, \n"+
                "f.pay_way as payWay, f.pay_code as payCode, f.dep_years as depYears, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "  f.use_start_date as useStartDate,f.voucher_no as voucherNo, f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee," +
                "f.remains_value as remainsValue,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch, f.clear_operator_code as clearOperatorCode," +
                "(select a.asset_simple_name from IntangibleAccAssetCodeType a where  a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetTypeName,"+
                "(select a.special_name from specialinfo  a where a.account=f.acc_book_code and  a.special_code=(select e.article_code1 from intangibleaccassetcodetype e where e.acc_book_code=f.acc_book_code and e.acc_book_type =f.acc_book_type and e.code_type=f.code_type and e.asset_type=f.asset_type)) as articleCode1,"+
                "( select c.change_new_data from IntangibleAccAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.asset_type=f.asset_type and c.handle_date =(select max(j.handle_date) from IntangibleAccAssetInfoChange j where j.center_code=f.center_code and j.branch_code=f.branch_code and j.acc_book_code=f.acc_book_code and j.acc_book_type=f.acc_book_type and c.asset_type=f.asset_type and c.code_type=f.code_type and j.asset_code=f.asset_code and j.card_code=f.card_code)) as changeNewData,"+
                "(f.end_depre_money-f.init_depre_money) as monthDepreMoney ,f.clear_flag as clearFlag, f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp from IntangibleAccAssetInfo  f where 1=1 ");
                //" and center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");
        sql.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        if(intang.getCardCode()!=null&&!intang.getCardCode().equals("")){
            if(intang.getCardCode().length()<5){
                String cs=intang.getCardCode();
                for(int i=0;i<5-intang.getCardCode().length();i++){
                    cs="0"+cs;
                }
                intang.setCardCode(cs);
            }
            sql.append(" and f.card_code>= ?"+paramsNo);
            params.put(paramsNo,intang.getCardCode());
            paramsNo++;
        }
        if(intang.getCardCode1()!=null&&!intang.getCardCode1().equals("")){
            if(intang.getCardCode1().length()<5){
                String cs=intang.getCardCode1();
                for(int i=0;i<5-intang.getCardCode1().length();i++){
                    cs="0"+cs;
                }
                intang.setCardCode1(cs);
            }
            sql.append(" and f.card_code<= ?"+paramsNo);
            params.put(paramsNo,intang.getCardCode1());
            paramsNo++;
        }
        if(intang.getAssetType()!=null&&!intang.getAssetType().equals("")){
            sql.append(" and f.asset_type like ?"+paramsNo);
            params.put(paramsNo,intang.getAssetType()+"%");
            paramsNo++;
        }
        if(intang.getAssetCode()!=null&&!intang.getAssetCode().equals("")){
            sql.append(" and f.asset_code>= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetCode());
            paramsNo++;
        }
        if(intang.getAssetCode1()!=null&&!intang.getAssetCode1().equals("")){
            sql.append(" and f.asset_code<= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetCode1());
            paramsNo++;
        }
        if(intang.getUseStartDate()!=null&&!intang.getUseStartDate().equals("")){
            sql.append(" and f.use_start_date>= ?"+paramsNo);
            params.put(paramsNo,intang.getUseStartDate());
            paramsNo++;
        }
        if(intang.getUseStartDate1()!=null&&!intang.getUseStartDate1().equals("")){
            sql.append(" and f.use_start_date<= ?"+paramsNo);
            params.put(paramsNo,intang.getUseStartDate1());
            paramsNo++;
        }

        if(intang.getAssetOriginValue()!=null&&!intang.getAssetOriginValue().equals("")){
            sql.append(" and f.asset_origin_value>= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetOriginValue());
            paramsNo++;
        }
        if(intang.getAssetOriginValue1()!=null&&!intang.getAssetOriginValue1().equals("")){
            sql.append(" and f.asset_origin_value<= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetOriginValue1());
            paramsNo++;
        }
        if(intang.getAssetNetValue()!=null&&!intang.getAssetNetValue().equals("")){
            sql.append(" and f.asset_net_value>= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetNetValue());
            paramsNo++;
        }
        if(intang.getAssetNetValue1()!=null&&!intang.getAssetNetValue1().equals("")){
            sql.append(" and f.asset_net_value<= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetNetValue1());
            paramsNo++;
        }
        if(intang.getEndDepreMoney()!=null&&!intang.getEndDepreMoney().equals("")){
            sql.append(" and f.end_depre_money>= ?"+paramsNo);
            params.put(paramsNo,intang.getEndDepreMoney());
            paramsNo++;
        }
        if(intang.getEndDepreMoney1()!=null&&!intang.getEndDepreMoney1().equals("")){
            sql.append(" and f.end_depre_money<= ?"+paramsNo);
            params.put(paramsNo,intang.getEndDepreMoney1());
            paramsNo++;
        }
        if(intang.getDepYears()!=null&&!intang.getDepYears().equals("")){
            sql.append(" and f.dep_years>= ?"+paramsNo);
            params.put(paramsNo,intang.getDepYears());
            paramsNo++;
        }
        if(intang.getDepYears1()!=null&&!intang.getDepYears1().equals("")){
            sql.append(" and f.dep_years<= ?"+paramsNo);
            params.put(paramsNo,intang.getDepYears1());
            paramsNo++;
        }
        //摊销至日期---------------------------
        if(intang.getDepreUtilDate()!=null&&!intang.getDepreUtilDate().equals("")){
            sql.append(" and f.depre_to_date<= ?"+paramsNo);
            params.put(paramsNo,intang.getDepreUtilDate());
            paramsNo++;
            sql.append(" and f.use_start_date<= ?"+paramsNo);
            params.put(paramsNo,intang.getDepreUtilDate());
            paramsNo++;
        }
        if(intang.getChangeMessage()!=null&&!intang.getChangeMessage().equals("")){
            if(intang.getChangeMessage().equals("0")){
                //全部 有变更信息判断
            }else if(intang.getChangeMessage().equals("1")){
                //无变更信息 exists
                sql.append(" and not exists(select c.change_code from intangibleaccassetinfochange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }else{
                //有变更信息
                sql.append(" and  exists(select c.change_code from intangibleaccassetinfochange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
            }
        }
        if ((intang.getChangeDate() != null&&!intang.getChangeDate().equals("")) || (intang.getChangeType()!=null&&!intang.getChangeType().equals("")) ){
            sql.append(" and f.card_code in (select c.card_code from intangibleaccassetinfochange c where  c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type ");
            if(intang.getChangeDate()!=null&&!intang.getChangeDate().equals("")){
                sql.append(" and c.change_date<= ?"+paramsNo);
                params.put(paramsNo,intang.getChangeDate());
                paramsNo++;
            }
            if(intang.getChangeType()!=null&&!intang.getChangeType().equals("")){
                sql.append(" and  c.change_type= ?"+paramsNo);
                params.put(paramsNo,intang.getChangeType());
                paramsNo++;
            }
            sql.append(")");
        }

        if(intang.getCreateOper()!=null && !intang.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from roleinfo where role_name like ?"+paramsNo);
            params.put(paramsNo,"%"+intang.getCreateOper()+"%");
            paramsNo++;
        }
        if(intang.getIsNotVoucher()!=null&&!intang.getIsNotVoucher().equals("")){
            if(intang.getIsNotVoucher().equals("0")){
                //没有生成凭证
                sql.append(" and (f.voucher_no= ?"+paramsNo);
                params.put(paramsNo,"");
                paramsNo++;
                sql.append(" or f.voucher_no  is ?" + paramsNo +")");
                params.put(paramsNo,null);
                paramsNo++;
            }else{
                sql.append(" and f.voucher_no != ?"+paramsNo);
                params.put(paramsNo,"");
                paramsNo++;
                sql.append(" and f.voucher_no  is not ?" + paramsNo);
                params.put(paramsNo,null);
                paramsNo++;

            }
        }
        if(intang.getStopCard()!=null&&!intang.getStopCard().equals("")){
            if(intang.getStopCard().equals("0")){
                //不包括已停用卡片
                sql.append(" and f.use_flag= ?" + paramsNo);
                params.put(paramsNo,"1");
                paramsNo++;
            }
        }
        if(intang.getCleanCard()!=null&&!intang.getCleanCard().equals("")){
            if(intang.getCleanCard().equals("1")){
                //不包含已清理卡片
                sql.append(" and f.clear_flag= ?" + paramsNo);
                params.put(paramsNo,"0");
                paramsNo++;
            }else if(intang.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag= ?" + paramsNo);
                params.put(paramsNo,"1");
                paramsNo++;

            }
        }
        if(intang.getUnitCode()!=null&&!intang.getUnitCode().equals("")){
            sql.append(" and f.unit_code = ?"+paramsNo);
            params.put(paramsNo,intang.getUnitCode());
            paramsNo++;
        }
        sql.append(" order by f.card_code asc");
        List<?> res= intangibleAccAssetInfoRepository.queryBySqlSC(sql.toString(),params);
        return res;
    }

    /**
     * 新增无形资产卡片
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult add(IntangibleAccAssetInfoDTO dto) {

        //判断是新增操作 还是修改操作
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        StringBuffer sql = new StringBuffer();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        IntangibleAccAssetInfo aai = new IntangibleAccAssetInfo();

        List<?> list = intangibleAccAssetInfoRepository.queryIntangibleAccAssetInfoByCardCode(centerCode,branchCode,accBookType,accBookCode,"31",dto.getCardCode());
        if(list.size()>0){
            return InvokeResult.failure("卡片编号已存在,请重新操作！");
        }
        /* if(list.size()!=0){//修改
            aai = (IntangibleAccAssetInfo)list.get(0);
            aai.setAssetType(dto.getAssetType() == null ? null : dto.getAssetType());//无形资产类别编码

            aai.setAssetCode(dto.getAssetCode() == null ? null : dto.getAssetCode());//无形资产编号
           *//* aai.setCreateOper(((IntangibleAccAssetInfo)list.get(0)).getCreateOper());//录入人
            aai.setCreateTime(((IntangibleAccAssetInfo)list.get(0)).getCreateTime());//录入时间*//*
            aai.setUpdateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));//修改人
            //修改时间格式化
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            aai.setUpdateTime(df.format(new Date()));//修改时间
        }else{//新增*/
            //将前台取到的值通过实体类存入数据库中
            IntangibleAccAssetInfoId aaiid = new IntangibleAccAssetInfoId();
            aaiid.setCenterCode(centerCode);            //核算单位
            aaiid.setBranchCode(branchCode);            //基层单位
            aaiid.setAccBookType(accBookType);          //账套类型
            aaiid.setAccBookCode(accBookCode);          //账套编码
            aaiid.setCodeType("31");       //管理类别编码
            aaiid.setCardCode(dto.getCardCode());       //卡片编码
            aai.setId(aaiid);
            aai.setAssetType(dto.getAssetType() == null ? null : dto.getAssetType());//无形资产类别编码
            String assetcodess=fCode(dto.getAssetType());
            aai.setAssetCode(assetcodess == null ? null : assetcodess);//无形资产编号
            aai.setCreateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));//录入人
            //录入时间格式化
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            aai.setCreateTime(df.format(new Date()));//录入时间
            aai.setUpdateOper(null);//修改人
            aai.setUpdateTime(null);//修改时间
       // }
            aai.setAssetName(dto.getAssetName() == null ? null : dto.getAssetName());//固定资产名称
//            aai.setMetricName("1");//计量单位
//            aai.setManufactor(dto.getManufactor() == null ? null : dto.getManufactor());//建造/制造商
//            aai.setSpecification(dto.getSpecification() == null ? null : dto.getSpecification());//规格说明
//            aai.setSerialNumber(dto.getSerialNumber() == null ? null : dto.getSerialNumber());//序列号
            //判断启用年月是否在会计期间内
            String date = dto.getUseStartDate() == null ? null : dto.getUseStartDate().substring(0,4)+dto.getUseStartDate().substring(5,7);//2019-01-01
            int paramsNo = 1;
            Map<Integer,Object> params = new HashMap<>();
            StringBuffer dateSql = new StringBuffer();
            dateSql.append("select * from (select year_month_date from AccGCheckInfo where 1=1 " );
            dateSql.append("  and center_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            dateSql.append("  and acc_book_type = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
            paramsNo++;
            dateSql.append("  and acc_book_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            dateSql.append(" ) t1 where t1.year_month_date = ?"+paramsNo);
            params.put(paramsNo,date);
            paramsNo++;
            List<?> dateList = intangibleAccAssetInfoRepository.queryBySqlSC(dateSql.toString(),params);
            if(dateList.size()<1){
                return InvokeResult.failure("开始使用日期必须在当前会计期间内！");
            }
            aai.setUseStartDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//开始使用日期
            aai.setRemainsValue(dto.getRemainsValue() == null ? null : dto.getRemainsValue());//预计残值
            aai.setRemainsRate(dto.getRemainsRate() == null ? null : dto.getRemainsRate());//预计残值率
            aai.setInitDepreAmount(dto.getInitDepreAmount() == null ? "0" : dto.getInitDepreAmount());//期初摊销月份
            aai.setInitDepreMoney(dto.getInitDepreMoney() == null ? new BigDecimal(0) : dto.getInitDepreMoney());//期初摊销金额
            aai.setUnitCode(dto.getUnitCode() == null ? null : dto.getUnitCode());//使用部门
            aai.setUseFlag(dto.getUseFlag() == null ? null : dto.getUseFlag());//使用状态
            aai.setAssetOriginValue(dto.getAssetOriginValue() == null ? new BigDecimal(0) : dto.getAssetOriginValue());//无形资产原值
            aai.setAssetNetValue(dto.getAssetNetValue() == null ? new BigDecimal(0) : dto.getAssetNetValue());//无形资产净值
            aai.setImpairment(dto.getImpairment() == null ? null : dto.getImpairment());//减值准备
            aai.setDepreFlag(dto.getDepreFlag() == null ? null : dto.getDepreFlag());//摊销计提状态
            aai.setAddedTax(dto.getAddedTax() == null ? null : dto.getAddedTax());//增值税率
            aai.setInputTax(dto.getInputTax() == null ? null : dto.getInputTax());//进项税额
            aai.setSum(dto.getSum() == null ? null : dto.getSum());//金额
//            aai.setPayWay("1002/01/02/01/");//付款方式   1002/01/02/01/
            aai.setPayWay(dto.getPayWay() == null ? null : dto.getPayWay());//付款方式
            aai.setPayCode(dto.getPayCode() == null ? null : dto.getPayCode());//付款专项
            aai.setDepYears(dto.getDepYears() == null ? null : dto.getDepYears());//使用年限
            aai.setDepType(dto.getDepType()!=null&&!"".equals(dto.getDepType())?"1":dto.getDepType());//摊销方法
            aai.setEndDepreAmount(aai.getInitDepreAmount());//期末累计摊销月份 期初已折旧月份+系统计提折旧月份
            aai.setEndDepreMoney(aai.getInitDepreMoney());//期末累计摊销金额 期初折旧金额+系统计提折旧金额
            aai.setVoucherNo(dto.getVoucherNo() == null ? null : dto.getVoucherNo());//凭证号
            aai.setDepreFromDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//无形资产折旧起始日期
            aai.setDepreToDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//无形资产折旧至日期
            aai.setClearFlag((dto.getClearFlag() == null||"".equals(dto.getClearFlag())) ? "0" : dto.getClearFlag());//清理状态
            aai.setClearYearMonth(dto.getClearYearMonth() == null ? null : dto.getClearYearMonth());//清理生效年月
            aai.setClearCode(dto.getClearCode() == null ? null : dto.getClearCode());//清理原因
            aai.setClearfee(dto.getClearfee() == null ? null : dto.getClearfee());//清理费用
            aai.setClearReason(dto.getClearReason() == null ? null : dto.getClearReason());//清理原因说明
            aai.setClearDate(dto.getClearDate() == null ? null : dto.getClearDate());//清理操作日期
            aai.setClearOperatorBranch(dto.getClearOperatorBranch() == null ? null : dto.getClearOperatorBranch());//清理操作员单位
            aai.setClearOperatorCode(dto.getClearOperatorCode() == null ? null : dto.getClearOperatorCode());//清理操作人
            aai.setTemp(dto.getTemp() == null ? null : dto.getTemp());//备注
            intangibleAccAssetInfoRepository.save(aai);//保存
            return InvokeResult.success();
    }
    /**
     * 修改无形资产卡片
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult update(IntangibleAccAssetInfoDTO dto) {

        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        //判断启用年月是否在会计期间内
        String date = dto.getUseStartDate() == null ? null : dto.getUseStartDate().substring(0,4)+dto.getUseStartDate().substring(5,7);//2019-01-01
        StringBuffer dateSql = new StringBuffer();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        dateSql.append("select * from (select year_month_date from AccGCheckInfo where 1=1");
        dateSql.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        dateSql.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        dateSql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        dateSql.append(" ) t1 where t1.year_month_date = ?"+paramsNo);
        params.put(paramsNo,date);
        paramsNo++;
        List<?> dateList = intangibleAccAssetInfoRepository.queryBySqlSC(dateSql.toString(),params);
        if(dateList.size()<1){
            return InvokeResult.failure("开始使用日期必须在当前会计期间内！");
        }

        IntangibleAccAssetInfo aai = new IntangibleAccAssetInfo();
        List<?> list = intangibleAccAssetInfoRepository.queryIntangibleAccAssetInfoByCardCode(centerCode,branchCode,accBookType,accBookCode,"31",dto.getCardCode());

        if(list.size()!=0){//修改
            aai = (IntangibleAccAssetInfo)list.get(0);
            aai.setAssetType(dto.getAssetType() == null ? null : dto.getAssetType());//无形资产类别编码

            aai.setAssetCode(dto.getAssetCode() == null ? null : dto.getAssetCode());//无形资产编号
           /* aai.setCreateOper(((IntangibleAccAssetInfo)list.get(0)).getCreateOper());//录入人
            aai.setCreateTime(((IntangibleAccAssetInfo)list.get(0)).getCreateTime());//录入时间*/
            aai.setUpdateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));//修改人
            //修改时间格式化
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            aai.setUpdateTime(df.format(new Date()));//修改时间
        }
        aai.setAssetName(dto.getAssetName() == null ? null : dto.getAssetName());//固定资产名称
//            aai.setMetricName("1");//计量单位
//            aai.setManufactor(dto.getManufactor() == null ? null : dto.getManufactor());//建造/制造商
//            aai.setSpecification(dto.getSpecification() == null ? null : dto.getSpecification());//规格说明
//            aai.setSerialNumber(dto.getSerialNumber() == null ? null : dto.getSerialNumber());//序列号

        aai.setUseStartDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//开始使用日期
        aai.setRemainsValue(dto.getRemainsValue() == null ? null : dto.getRemainsValue());//预计残值
        aai.setRemainsRate(dto.getRemainsRate() == null ? null : dto.getRemainsRate());//预计残值率
        aai.setInitDepreAmount(dto.getInitDepreAmount() == null ? "0" : dto.getInitDepreAmount());//期初摊销月份
        aai.setInitDepreMoney(dto.getInitDepreMoney() == null ? new BigDecimal(0) : dto.getInitDepreMoney());//期初摊销金额
        aai.setUnitCode(dto.getUnitCode() == null ? null : dto.getUnitCode());//使用部门
        aai.setUseFlag(dto.getUseFlag() == null ? null : dto.getUseFlag());//使用状态
        aai.setAssetOriginValue(dto.getAssetOriginValue() == null ? new BigDecimal(0) : dto.getAssetOriginValue());//无形资产原值
        aai.setAssetNetValue(dto.getAssetNetValue() == null ? new BigDecimal(0) : dto.getAssetNetValue());//无形资产净值
        aai.setImpairment(dto.getImpairment() == null ? null : dto.getImpairment());//减值准备
        aai.setDepreFlag(dto.getDepreFlag() == null ? null : dto.getDepreFlag());//摊销计提状态
        aai.setAddedTax(dto.getAddedTax() == null ? null : dto.getAddedTax());//增值税率
        aai.setInputTax(dto.getInputTax() == null ? null : dto.getInputTax());//进项税额
        aai.setSum(dto.getSum() == null ? null : dto.getSum());//金额
//        aai.setPayWay("1002/01/02/01/");//付款方式   1002/01/02/01/
        aai.setPayWay(dto.getPayWay() == null ? null : dto.getPayWay());//付款方式   1002/01/02/01/
        aai.setPayCode(dto.getPayCode() == null ? null : dto.getPayCode());//付款专项
        aai.setDepYears(dto.getDepYears() == null ? null : dto.getDepYears());//使用年限
        aai.setDepType(dto.getDepType()!=null&&!"".equals(dto.getDepType())?"1":dto.getDepType());//摊销方法
        aai.setEndDepreAmount(aai.getInitDepreAmount());//期末累计摊销月份 期初已折旧月份+系统计提折旧月份
        aai.setEndDepreMoney(aai.getInitDepreMoney());//期末累计摊销金额 期初折旧金额+系统计提折旧金额
        aai.setVoucherNo(dto.getVoucherNo() == null ? null : dto.getVoucherNo());//凭证号
        aai.setDepreFromDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//无形资产折旧起始日期
        aai.setDepreToDate(dto.getUseStartDate() == null ? null : dto.getUseStartDate());//无形资产折旧至日期
        aai.setClearFlag((dto.getClearFlag() == null||"".equals(dto.getClearFlag())) ? "0" : dto.getClearFlag());//清理状态
        aai.setClearYearMonth(dto.getClearYearMonth() == null ? null : dto.getClearYearMonth());//清理生效年月
        aai.setClearCode(dto.getClearCode() == null ? null : dto.getClearCode());//清理原因
        aai.setClearfee(dto.getClearfee() == null ? null : dto.getClearfee());//清理费用
        aai.setClearReason(dto.getClearReason() == null ? null : dto.getClearReason());//清理原因说明
        aai.setClearDate(dto.getClearDate() == null ? null : dto.getClearDate());//清理操作日期
        aai.setClearOperatorBranch(dto.getClearOperatorBranch() == null ? null : dto.getClearOperatorBranch());//清理操作员单位
        aai.setClearOperatorCode(dto.getClearOperatorCode() == null ? null : dto.getClearOperatorCode());//清理操作人
        aai.setTemp(dto.getTemp() == null ? null : dto.getTemp());//备注
        intangibleAccAssetInfoRepository.save(aai);//保存
        return InvokeResult.success();

    }
    /**
     *加载固定资产类别编码中维护的类别
     * @return
     */
    @Override
    public List<?> qryAssetType() {
        List<?> result = intangibleAccAssetInfoRepository.qryAssetType(CurrentUser.getCurrentLoginAccount());
        return result;
    }

    /**
     * 获取新增卡片编码
     * @return
     */
    @Override
    public String getNewCardCode() {
        //获取数据库中最大的卡片编码并+1
        String maxCardCode = intangibleAccAssetInfoRepository.qryMaxCardCode(CurrentUser.getCurrentLoginManageBranch(),CurrentUser.getCurrentLoginAccount());
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

    @Transactional
    @Override
    public InvokeResult delete(List<IntangibleAccAssetInfoId> list) {
        String voucherlist="";
        for (IntangibleAccAssetInfoId acc : list) {
            IntangibleAccAssetInfo lists=intangibleAccAssetInfoRepository.findById(acc).get();
            List<?> adList = intangibleAccDepreRepository.queryIntangibleAccDepreByAssetCode(CurrentUser.getCurrentLoginManageBranch(),acc.getAccBookType(),acc.getAccBookCode(),acc.getCodeType(),lists.getAssetCode());
            if((lists.getVoucherNo()!=null&&!lists.getVoucherNo().equals(""))||adList.size()>0){
                voucherlist=voucherlist+" "+acc.getCardCode();
            }
        }
        if(!voucherlist.equals("")){
            return InvokeResult.failure("编码是'" + voucherlist + "'的卡片已计提过摊销或已经生成凭证，不能删除，请重新操作!");
        }
        for (IntangibleAccAssetInfoId inAccAsset : list) {
            intangibleAccAssetInfoRepository.delete(inAccAsset.getCenterCode(), inAccAsset.getBranchCode(), inAccAsset.getAccBookType(), inAccAsset.getAccBookCode(), inAccAsset.getCodeType(), inAccAsset.getCardCode());
        }
        return InvokeResult.success();
    }

    /**
     * 无形资产类别
     * @param value
     * @return
     */
    @Override
    public List<?> qryAssetTypeTree(String value){
        List resultListAll = new ArrayList();
        value=null;
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer assetTypeSql = new StringBuffer("select asset_type as id, asset_simple_name as text, asset_type as mid,end_flag as endFlag from IntangibleAccAssetCodeType where 1=1  " );
        assetTypeSql.append("  and level = ?"+paramsNo);
        params.put(paramsNo,"1");
        paramsNo++;
        assetTypeSql.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        assetTypeSql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        List<?> assetTypeList = intangibleAccAssetInfoRepository.queryBySqlSC(assetTypeSql.toString(),params);
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
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql = new StringBuffer("select asset_type as id, asset_simple_name as text, asset_type as mid,end_flag as endFlag from IntangibleAccAssetCodeType where 1=1 " );
        sql.append("  and super_code = ?"+paramsNo);
        params.put(paramsNo,id);
        paramsNo++;
        sql.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        List<?> list =  intangibleAccAssetInfoRepository.queryBySqlSC(sql.toString(),params);
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
     * 判断当前卡片是否摊销过
     * @param dto
     * @return
     */
    @Override
    public InvokeResult depreciation(IntangibleAccAssetInfoDTO dto) {
        try{
            //判断当前卡片是否进行过摊销
            List<?> adList = intangibleAccDepreRepository.queryIntangibleAccDepreByAssetCode(CurrentUser.getCurrentLoginManageBranch(),dto.getAccBookType(),dto.getAccBookCode(),dto.getCodeType(),dto.getAssetCode());
            if(adList.size() > 0){//大于0 说明计提过摊销
                return InvokeResult.failure("当前卡片已进行过摊销，无法进行修改！");
            }
            return InvokeResult.success();
        }catch (Exception e){
            logger.error("无形资产卡片修改失败",e);
            return InvokeResult.failure("无形资产卡片修改失败，请联系管理员！");
        }
    }

    /**
     * 通过无形资产编码类别名称查询 使用年限()
     * @param assetType
     * @return
     */
    @Override
    public String getDepYears(String assetType) {
        try{
            List<?> list =  intangibleAccAssetCodeTypeRepository.queryByAssetType(assetType,CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount());
            if(list.size()>0){
                return ((IntangibleAccAssetCodeType)list.get(0)).getDepYears().toString();
            }else{
                return "";
            }
        }catch (Exception e){
            logger.error("查询异常", e);
            return "";
        }
    }

    @Override
    public String fCode(String assetType) {
        try{
            StringBuffer sql = new StringBuffer();
            int paramsNo = 1;
            Map<Integer,Object> params = new HashMap<>();
            sql.append("select * from IntangibleAccAssetInfo a where 1=1 " );
            sql.append("  and a.center_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append("  and a.asset_type = ?"+paramsNo);
            params.put(paramsNo,assetType);
            paramsNo++;
            sql.append("  and a.acc_book_type = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
            paramsNo++;
            sql.append("  and a.acc_book_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append("  and a.asset_code = (select MAX(asset_code) from IntangibleAccAssetInfo where center_code= ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append("  and asset_type = ?"+paramsNo);
            params.put(paramsNo,assetType);
            paramsNo++;
            sql.append("  and acc_book_type = ?"+paramsNo);
            params.put(paramsNo, CurrentUser.getCurrentLoginAccountType());
            paramsNo++;
            sql.append("  and acc_book_code = ?"+paramsNo + ")");
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            List<?> list = intangibleAccAssetInfoRepository.queryBySql(sql.toString(),params, IntangibleAccAssetInfo.class);
            String assetCode;//无形资产编号
            if(list.size()>0){//基本信息表中有当前类别
                String assetcode1=((IntangibleAccAssetInfo)list.get(0)).getAssetCode();
                //获取后五位字符
                String assetcode3=String.valueOf(Integer.parseInt(assetcode1.substring(assetcode1.length()-5))+1);
                String assetcode4="";
                for(int i=0;i<5-assetcode3.length();i++){
                    assetcode4="0"+assetcode4;
                }
                assetCode = ((IntangibleAccAssetInfo)list.get(0)).getAssetType()+assetcode4+assetcode3;
            }else{//基本信息表中没有当前类别
                assetCode = assetType + "00001";
            }
            return assetCode;
        }catch (Exception e){
            logger.error("无形资产编号生成异常",e);
            return null;
        }
    }

    /**
     * 根据资产类型、账套编码、账套类型查询摊销方法
     * @param assetType
     * @return
     */
    @Override
    public String deprMethod(String assetType) {
        try{
            StringBuffer sql = new StringBuffer();
            int paramsNo = 1;
            Map<Integer,Object> params = new HashMap<>();
            sql.append("SELECT c.code_name FROM IntangibleAccAssetCodeType i LEFT JOIN codemanage c ON c.temp='折旧方法' WHERE i.dep_type=c.code_code ");
            sql.append("  and i.asset_type = ?"+paramsNo);
            params.put(paramsNo,assetType);
            paramsNo++;
            sql.append("  and i.acc_book_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append("  and i.acc_book_type = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
            paramsNo++;
            String assetCode=null;//无形资产编号
            List<?> list = intangibleAccAssetInfoRepository.queryBySql(sql.toString(),params);
            for (Object o : list) {
                assetCode=(String) o;
            }
            return assetCode;
        }catch (Exception e){
            logger.error("无形资产折旧方法查询异常",e);
            return null;
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
        StringBuffer sql = new StringBuffer();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        sql.append("SELECT * FROM accmonthtrace a WHERE 1=1");
        sql.append("  and a.center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append("  and a.acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append("  and a.acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        sql.append("  and a.year_month_date = ?"+paramsNo);
        params.put(paramsNo,date);
        List<AccMonthTrace> list = (List<AccMonthTrace>) intangibleAccAssetInfoRepository.queryBySql(sql.toString(), params,AccMonthTrace.class);
        if(list.size()>0){
            if ("3".equals(list.get(0).getAccMonthStat())||"5".equals(list.get(0).getAccMonthStat())) {
                return InvokeResult.failure("开始使用日期对应的会计期间已结转！");
            }
            //开始使用日期对应的会计期间是否已计提
            paramsNo = 1;
            params.clear();
            sql.setLength(0);
            sql.append("SELECT * FROM accwcheckinfo a WHERE 1=1 ");
            sql.append("  and a.flag != ?"+paramsNo);
            params.put(paramsNo,"0");
            paramsNo++;
            sql.append("  and a.center_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append("  and a.acc_book_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append("  and a.year_month_date = ?"+paramsNo);
            params.put(paramsNo,date);
            List<AccWCheckInfo> list2 = (List<AccWCheckInfo>) intangibleAccAssetInfoRepository.queryBySql(sql.toString(),params, AccWCheckInfo.class);
            if (list!=null&&list2.size()>0) {
                return InvokeResult.failure("开始使用日期对应的会计期间已计提摊销！");
            }
            return InvokeResult.success();
        }else{
            return InvokeResult.failure("开始使用日期必须在当前会计期间内！");
        }
    }

    /**
     * 资产折旧状态修改
     * @param cardCode
     * @param depreFlag
     * @return
     */
    @Transactional
    @Override
    public InvokeResult zjUpdate(String cardCode, String depreFlag) {
            String centerCode = CurrentUser.getCurrentLoginManageBranch();
            String branchCode = CurrentUser.getCurrentLoginManageBranch();
            IntangibleAccAssetInfoId aaid = new IntangibleAccAssetInfoId();
            aaid.setCenterCode(centerCode);
            aaid.setBranchCode(branchCode);
            aaid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
            aaid.setAccBookCode(CurrentUser.getCurrentLoginAccount());
            aaid.setCodeType("31");
            aaid.setCardCode(cardCode);
            IntangibleAccAssetInfo aa = intangibleAccAssetInfoRepository.findById(aaid).get();
            aa.setDepreFlag(depreFlag);
            aa.setUpdateTime(CurrentTime.getCurrentTime());//修改时间
            aa.setUpdateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));//修改人
            intangibleAccAssetInfoRepository.save(aa);//保存
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
            StringBuffer sql = new StringBuffer();
            int paramsNo = 1;
            Map<Integer,Object> params = new HashMap<>();
            String assetCode = assetType+"00001";
            sql.append("select MAX(asset_code) from intangibleAccAssetInfo where 1=1 ");
            sql.append("  and center_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append("  and asset_type = ?"+paramsNo);
            params.put(paramsNo,assetType);
            paramsNo++;
            sql.append("  and acc_book_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append("  and acc_book_type = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
            paramsNo++;
             List<?> list = intangibleAccAssetInfoRepository.queryBySql(sql.toString(),params);
            if(list.size()>0){
                String temp = list.get(0).toString().substring(assetType.length());
                assetCode = assetType + String.format("%05d", Integer.parseInt(temp)+1);
                //assetCode = String.valueOf(Integer.parseInt(list.get(0).toString())+1);//当前类别下最大资产编号+1
            }
            return InvokeResult.success(assetCode);
        }catch (Exception e){
            logger.error("复制操作下获取资产编号失败",e);
            return InvokeResult.success("资产编号获取失败！");
        }
    }


    /**
     * 无形资产卡片停用
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult stopUse(IntangibleAccAssetInfoDTO dto) {
            IntangibleAccAssetInfoId aaiid = new IntangibleAccAssetInfoId();
            aaiid.setCardCode(dto.getCardCode());
            aaiid.setCodeType(dto.getCodeType());
            aaiid.setAccBookCode(dto.getAccBookCode());
            aaiid.setAccBookType(dto.getAccBookType());
            aaiid.setCenterCode(dto.getCenterCode());
            aaiid.setBranchCode(dto.getBranchCode());
            IntangibleAccAssetInfo aai = intangibleAccAssetInfoRepository.findById(aaiid).get();
            aai.setUseFlag("0");//设置为停用
            intangibleAccAssetInfoRepository.save(aai);
            return InvokeResult.success();

    }

    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols) {
        ExcelUtil excelUtil=new ExcelUtil();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql =new StringBuffer("select f.depre_from_date as depreFromDate, f.center_code as centerCode , f.branch_code as  branchCode, f.acc_book_type as accBookType, f.acc_book_code as accBookCode  , f.code_type as codeType ,"+
                " f.asset_type as  assetType, f.card_code as cardCode, f.asset_code as assetCode, f.asset_name as assetName,f.depre_to_date as depreToDate, " +
                "f.impairment as impairment, f.depre_flag as  depreFlag, f.added_tax as  addedTax, f.input_tax as inputTax, f.sum, f.remains_rate as remainsRate, f.init_depre_amount as initDepreAmount, f.init_depre_money as initDepreMoney," +
                "(select c.code_name from codemanage c where c.code_code=f.use_flag and c.code_type='useFlag') as useFlag , f.asset_origin_value as assetOriginValue, f.asset_net_value as assetNetValue," +
                "f.dep_type as depType, "+
                "f.pay_way as payWay, f.pay_code as payCode, f.dep_years as depYears, f.end_depre_amount as endDepreAmount, f.end_depre_money as endDepreMoney," +
                "  f.use_start_date as useStartDate,f.voucher_no as voucherNo, f.clear_year_month as clearYearMonth, f.clear_code as clearCode, f.clearfee as clearfee," +
                "f.remains_value as remainsValue,f.clear_reason as clearReason, f.clear_date as clearDate, f.clear_operator_branch as clearOperatorBranch, f.clear_operator_code as clearOperatorCode," +
                "(select a.asset_simple_name from IntangibleAccAssetCodeType a where a.acc_book_type=f.acc_book_type and a.acc_book_code=f.acc_book_code and a.code_type=f.code_type and a.asset_type=f.asset_type) as assetTypeName,"+
                "(select a.special_name from specialinfo  a where a.account=f.acc_book_code and  a.special_code=(select e.article_code1 from intangibleaccassetcodetype e where  e.acc_book_code=f.acc_book_code and e.acc_book_type =f.acc_book_type and e.code_type=f.code_type and e.asset_type=f.asset_type)) as articleCode1,"+
                "( select c.change_new_data from IntangibleAccAssetInfoChange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code and c.asset_type=f.asset_type and c.handle_date =(select max(j.handle_date) from IntangibleAccAssetInfoChange j where j.center_code=f.center_code and j.branch_code=f.branch_code and j.acc_book_code=f.acc_book_code and j.acc_book_type=f.acc_book_type and c.asset_type=f.asset_type and c.code_type=f.code_type and j.asset_code=f.asset_code and j.card_code=f.card_code)) as changeNewData,"+
               "(select i.month_depre_money from intangibleAccDepre i where i.center_code=f.center_code and i.branch_code=f.branch_code and i.acc_book_type=f.acc_book_type and  i.acc_book_code=f.acc_book_code and i.asset_type=f.asset_type and i.code_type=f.code_type and f.asset_code=i.asset_code ORDER BY i.year_month_data desc limit 1  ) as monthDepreMoney  ,f.clear_flag as clearFlag, f.create_oper as  createOper, f.create_time as createTime , f.update_oper as  updateOper, f.update_time as  updateTime, f.temp from IntangibleAccAssetInfo  f where 1=1 " );
        sql.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        IntangibleAccAssetInfoDTO intang = new IntangibleAccAssetInfoDTO();
        try {
            intang = new ObjectMapper().readValue(queryConditions, IntangibleAccAssetInfoDTO.class);
        } catch (Exception e){
            e.printStackTrace();
        }

        if(intang.getCardCode()!=null&&!intang.getCardCode().equals("")){
            if(intang.getCardCode().length()<5){
                String cs=intang.getCardCode();
                for(int i=0;i<5-intang.getCardCode().length();i++){
                    cs="0"+cs;
                }
                intang.setCardCode(cs);
            }
            sql.append(" and f.card_code>= ?"+paramsNo);
            params.put(paramsNo,intang.getCardCode());
            paramsNo++;

        }
        if(intang.getCardCode1()!=null&&!intang.getCardCode1().equals("")){
            if(intang.getCardCode1().length()<5){
                String cs=intang.getCardCode1();
                for(int i=0;i<5-intang.getCardCode1().length();i++){
                    cs="0"+cs;
                }
                intang.setCardCode1(cs);
            }
            sql.append(" and f.card_code<= ?"+paramsNo);
            params.put(paramsNo,intang.getCardCode1());
            paramsNo++;
        }
        if(intang.getAssetType()!=null&&!intang.getAssetType().equals("")){
            sql.append(" and f.asset_type like ?"+paramsNo);
            params.put(paramsNo,intang.getAssetType()+"%'");
            paramsNo++;
        }
        if(intang.getAssetCode()!=null&&!intang.getAssetCode().equals("")){
            sql.append(" and f.asset_code>= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetCode());
            paramsNo++;
        }
        if(intang.getAssetCode1()!=null&&!intang.getAssetCode1().equals("")){
            sql.append(" and f.asset_code<= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetCode1());
            paramsNo++;
        }
        if(intang.getUseStartDate()!=null&&!intang.getUseStartDate().equals("")){
            sql.append(" and f.use_start_date>= ?"+paramsNo);
            params.put(paramsNo,intang.getUseStartDate());
            paramsNo++;
        }
        if(intang.getUseStartDate1()!=null&&!intang.getUseStartDate1().equals("")){
            sql.append(" and f.use_start_date<= ?"+paramsNo);
            params.put(paramsNo,intang.getUseStartDate1());
            paramsNo++;
        }

        if(intang.getAssetOriginValue()!=null&&!intang.getAssetOriginValue().equals("")){
            sql.append(" and f.asset_origin_value>= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetOriginValue());
            paramsNo++;
        }
        if(intang.getAssetOriginValue1()!=null&&!intang.getAssetOriginValue1().equals("")){
            sql.append(" and f.asset_origin_value<= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetOriginValue1());
            paramsNo++;
        }
        if(intang.getAssetNetValue()!=null&&!intang.getAssetNetValue().equals("")){
            sql.append(" and f.asset_net_value>= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetNetValue());
            paramsNo++;
        }
        if(intang.getAssetNetValue1()!=null&&!intang.getAssetNetValue1().equals("")){
            sql.append(" and f.asset_net_value<= ?"+paramsNo);
            params.put(paramsNo,intang.getAssetNetValue1());
            paramsNo++;
        }
        if(intang.getEndDepreMoney()!=null&&!intang.getEndDepreMoney().equals("")){
            sql.append(" and f.end_depre_money>= ?"+paramsNo);
            params.put(paramsNo,intang.getEndDepreMoney());
            paramsNo++;
        }
        if(intang.getEndDepreMoney1()!=null&&!intang.getEndDepreMoney1().equals("")){
            sql.append(" and f.end_depre_money<= ?"+paramsNo);
            params.put(paramsNo,intang.getEndDepreMoney1());
            paramsNo++;
        }
        if(intang.getDepYears()!=null&&!intang.getDepYears().equals("")){
            sql.append(" and f.dep_years>= ?"+paramsNo);
            params.put(paramsNo,intang.getDepYears());
            paramsNo++;
        }
        if(intang.getDepYears1()!=null&&!intang.getDepYears1().equals("")){
            sql.append(" and f.dep_years<= ?"+paramsNo);
            params.put(paramsNo,intang.getDepYears1());
            paramsNo++;
        }
        //摊销至日期---------------------------
        if(intang.getDepreUtilDate()!=null&&!intang.getDepreUtilDate().equals("")){
            sql.append(" and f.depre_to_date<= ?"+paramsNo);
            params.put(paramsNo,intang.getDepreUtilDate());
            paramsNo++;
            sql.append(" and f.use_start_date<= ?"+paramsNo);
            params.put(paramsNo,intang.getDepreUtilDate());
            paramsNo++;
        }
        if(intang.getChangeMessage()!=null&&!intang.getChangeMessage().equals("")){
            if(intang.getChangeMessage().equals("0")){
                //全部 有变更信息判断
            }else if(intang.getChangeMessage().equals("1")){
                //无变更信息 exists
                sql.append(" and not exists(select c.change_code from intangibleaccassetinfochange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");

            }else{
                //有变更信息
                sql.append(" and  exists(select c.change_code from intangibleaccassetinfochange c where c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type and c.code_type=f.code_type and c.asset_code=f.asset_code and c.card_code=f.card_code )");
            }
        }
        if ((intang.getChangeDate() != null&&!intang.getChangeDate().equals("")) || (intang.getChangeType()!=null&&!intang.getChangeType().equals("")) ){
            sql.append(" and f.card_code in (select c.card_code from intangibleaccassetinfochange c where  c.center_code=f.center_code and c.branch_code=f.branch_code and c.acc_book_code=f.acc_book_code and c.acc_book_type=f.acc_book_type ");
            if(intang.getChangeDate()!=null&&!intang.getChangeDate().equals("")){
                sql.append(" and c.change_date<= ?"+paramsNo);
                params.put(paramsNo,intang.getChangeDate());
                paramsNo++;
            }
            if(intang.getChangeType()!=null&&!intang.getChangeType().equals("")){
                sql.append(" and  c.change_type= ?"+paramsNo);
                params.put(paramsNo,intang.getChangeType());
                paramsNo++;
            }
            sql.append(")");
        }

        if(intang.getCreateOper()!=null && !intang.getCreateOper().equals("")){
            sql.append(" and f.create_oper in (select id from roleinfo where role_name like  ?"+paramsNo + ")");
            params.put(paramsNo,"%"+intang.getCreateOper()+"%");
            paramsNo++;
        }
        if(intang.getIsNotVoucher()!=null&&!intang.getIsNotVoucher().equals("")){
            if(intang.getIsNotVoucher().equals("0")){
                //没有生成凭证
                sql.append(" and  (f.voucher_no= ?"+paramsNo);
                params.put(paramsNo,"");
                paramsNo++;
                sql.append(" or  f.voucher_no  is ?"+paramsNo +")");
                params.put(paramsNo,null);
                paramsNo++;
            }else{
                sql.append(" and  f.voucher_no != ?"+paramsNo);
                params.put(paramsNo,"");
                paramsNo++;
                sql.append(" and  f.voucher_no is not ?"+paramsNo);
                params.put(paramsNo,null);
                paramsNo++;
            }
        }
        if(intang.getStopCard()!=null&&!intang.getStopCard().equals("")){
            if(intang.getStopCard().equals("0")){
                //不包括已停用卡片
                sql.append(" and f.use_flag= ?"+paramsNo);
                params.put(paramsNo,"1");
                paramsNo++;
            }
        }
        if(intang.getCleanCard()!=null&&!intang.getCleanCard().equals("")){
            if(intang.getCleanCard().equals("1")){
                //不包含已清理卡片
                sql.append(" and f.clear_flag= ?"+paramsNo);
                params.put(paramsNo,"0");
                paramsNo++;
            }else if(intang.getCleanCard().equals("2")){
                //仅显示已清理卡片
                sql.append(" and f.clear_flag= ?"+paramsNo);
                params.put(paramsNo,"1");
            }
        }
        sql.append(" order by f.card_code asc");
        try {
            // 根据条件查询导出数据集
            List<?> dataList = intangibleAccAssetInfoRepository.queryBySqlSC(sql.toString(),params);
            // 导出
            excelUtil.exportu_intangcard(request, response, name, cols, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 变动信息查询
     * @param
     * @return
     */
    @Override
    public List<?> getChangeMessage(IntangibleAccAssetInfoDTO acc) {
        StringBuffer sql = new StringBuffer();
        sql.append("select f.handle_date,f.code_type as codeType, f.change_type as changeType,f.change_reason as changeReason ,f.change_code as changeCode,f.change_date as changeDate,f.change_old_data as changeOldData,"+
              " f.change_new_data as changeNewData, (select c.code_name from codemanage c where c.code_type='changeType' and c.code_code=f.change_type ) as changeTypeName"+
      "  from IntangibleAccAssetInfoChange f  where 1=1 ");
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        sql.append("  and f.center_code = ?"+paramsNo);
        params.put(paramsNo,acc.getCenterCode());
        paramsNo++;
        sql.append("  and f.branch_code = ?"+paramsNo);
        params.put(paramsNo,acc.getBranchCode());
        paramsNo++;
        sql.append("  and f.acc_book_type = ?"+paramsNo);
        params.put(paramsNo,acc.getAccBookType());
        paramsNo++;
        sql.append("  and f.acc_book_code = ?"+paramsNo);
        params.put(paramsNo,acc.getAccBookCode());
        paramsNo++;
        sql.append("  and f.code_type = ?"+paramsNo);
        params.put(paramsNo,acc.getCodeType());
        paramsNo++;
        sql.append("  and f.card_code = ?"+paramsNo + " ORDER BY f.change_code");
        params.put(paramsNo,acc.getCardCode());
        paramsNo++;
        List<?> list=  intangibleAccAssetInfoRepository.queryBySqlSC(sql.toString(),params);
        List list1=new ArrayList();
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map = new HashMap();
                map.putAll((Map) obj);
                String changOldDataName="";
                String changNewDataName="";
                if(map.get("changeType").equals("01")){
                    //类别
                    map.put("changeTypeName","摊销信息变动");
                    //变动前
                    String changeData=String.valueOf(map.get("changeOldData"));
                    String [] arr=changeData.split(",");
                    //变动后
                    String changeData1=String.valueOf(map.get("changeNewData"));
                    String [] arr1=changeData1.split(",");
                    if(!arr[0].equals(arr1[0])){
                        String assetTypeName= intangibleChangeManageRepository.getAssetName(String.valueOf(map.get("accBookType")),String.valueOf(map.get("accBookCode")),String.valueOf(map.get("codeType")),arr[0]);
                        changOldDataName="类别:"+assetTypeName;
                        String assetTypeName1= intangibleChangeManageRepository.getAssetName(String.valueOf(map.get("accBookType")),String.valueOf(map.get("accBookCode")),String.valueOf(map.get("codeType")),arr1[0]);
                        changNewDataName="类别:"+assetTypeName1;
                    }
                    if(!arr[1].equals(arr1[1])){
                        changOldDataName=changOldDataName+" 摊销年限:"+arr[1];
                        changNewDataName=changNewDataName+" 摊销年限:"+arr1[1];
                    }
                    if(!arr[2].equals(arr1[2])){
                        changOldDataName=changOldDataName+" 预计残值率:"+arr[2];
                        changNewDataName=changNewDataName+" 预计残值率:"+arr1[2];
                    }
                }else if(map.get("changeType").equals("02")){
                    //使用状态
                    map.put("changeTypeName","使用状态变动");
                    if(String.valueOf(map.get("changeOldData")).trim().equals("0")){
                        changOldDataName="停用";
                        changNewDataName="使用";
                    }else{
                        changOldDataName="使用";
                        changNewDataName="停用";
                    }

                }else if(map.get("changeType").equals("03")){
                    //清理
                    map.put("changeTypeName","卡片清理");
                    if(String.valueOf(map.get("changeOldData")).trim().equals("0")){
                        changOldDataName="未清理";
                        changNewDataName="已清理";
                    }else{
                        changOldDataName="已清理";
                        changNewDataName="未清理";
                    }
                }
                map.put("changeOldDataName",changOldDataName);
                map.put("changeNewDataName",changNewDataName);
                list1.add(map);

            }
        }
        return list1;

    }

    /**
     * 摊销至日期 时间校验
     * @param depreUtilDate
     * @return
     */
    @Override
    public InvokeResult checkDepreToDate(String depreUtilDate) {
        //先获取到 无形资产基本信息表 中的最大折旧至日期
        StringBuffer sql = new StringBuffer();
        sql.append("select * from IntangibleAccAssetInfo where depre_to_date in (select MAX(depre_to_date) as depre_to_date from IntangibleAccAssetInfo) " );
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        sql.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        List<?> list = intangibleAccAssetInfoRepository.queryBySql(sql.toString(),params, IntangibleAccAssetInfo.class);
        if(list.size()>0){
            String depreToDate = ((IntangibleAccAssetInfo)list.get(0)).getDepreToDate().substring(0,4)+((IntangibleAccAssetInfo)list.get(0)).getDepreToDate().substring(5,7);
            depreUtilDate = depreUtilDate.substring(0,4)+depreUtilDate.substring(5,7);
            //判断当前日期是否 小于等于 最大折旧至日期
            if(Integer.parseInt(depreUtilDate) <= Integer.parseInt(depreToDate)){
                //小于等于
                return InvokeResult.success();
            }else{
                //大于
                return InvokeResult.failure("摊销至日期应小于系统中最大摊销至日期！", depreToDate);
            }
        }
        return InvokeResult.success();
    }

    @Override
    public List<?> getAssetCodeList(String intangAssetType) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        StringBuffer sql = new StringBuffer(" SELECT ib.asset_code AS 'value', ib.asset_code AS 'text' FROM intangibleaccassetinfo " +
                " ib WHERE 1=1  ");

        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        sql.append("  and ib.center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append("  and ib.acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append("  and ib.acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        sql.append("  and ib.asset_type =  ?"+paramsNo +" ORDER BY VALUE");
        params.put(paramsNo,intangAssetType);
        paramsNo++;
        List<?> list = intangibleAccAssetInfoRepository.queryBySqlSC(sql.toString(),params);
        return list;
    }

}
