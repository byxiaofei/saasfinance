package com.sinosoft.service.impl.intangibleassets;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.domain.account.*;
import com.sinosoft.domain.fixedassets.*;
import com.sinosoft.domain.intangibleassets.*;
import com.sinosoft.dto.intangibleassets.AccWCheckInfoDTO;
import com.sinosoft.repository.account.AccMainVoucherRespository;
import com.sinosoft.repository.account.AccSubVoucherRespository;
import com.sinosoft.repository.account.AccVoucherNoRespository;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.repository.fixedassets.AccAssetInfoRepository;
import com.sinosoft.repository.fixedassets.AccDepreRepository;
import com.sinosoft.repository.fixedassets.AccGCheckInfoRepository;
import com.sinosoft.repository.intangibleassets.AccWCheckInfoRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetCodeTypeRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetInfoRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccDepreRepository;
import com.sinosoft.service.account.VoucherManageService;
import com.sinosoft.service.fixedassets.FixedassetsCardVoucherService;
import com.sinosoft.service.impl.fixedassets.FixedAssetsDepreciationServiceImpl;
import com.sinosoft.service.intangibleassets.IntangibleAssetsDepreciationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-09 19:28
 */
@Service
public class IntangibleAssetsDepreciationServiceImpl implements IntangibleAssetsDepreciationService {
    private Logger logger = LoggerFactory.getLogger(FixedAssetsDepreciationServiceImpl.class);
    @Value("${voucher.currency}")
    private String currency;
    @Resource
    private VoucherManageService voucherManageService;
    @Resource
    private AccWCheckInfoRepository accWCheckInfoRepository;
    @Resource
    private IntangibleAccAssetInfoRepository intangibleAccAssetInfoRepository;
    @Resource
    private AccDepreRepository accDepreRepository;
    @Resource
    private AccAssetInfoRepository accAssetInfoRepository;
    @Resource
    private AccVoucherNoRespository accVoucherNoRespository;
    @Resource
    private AccMainVoucherRespository accMainVoucherRespository;
    @Resource
    private AccAssetCodeTypeRepository accAssetCodeTypeRepository;
    @Resource
    private AccSubVoucherRespository accSubVoucherRespository;
    @Resource
    private IntangibleAccDepreRepository intangibleAccDepreRepository;
    @Resource
    private IntangibleAccAssetCodeTypeRepository intangibleAccAssetCodeTypeRepository;
    @Resource
    private FixedassetsCardVoucherService fixedassetsCardVoucherService;

    /**
     * 查询所有无形资产会计期间
     *
     * @param dto
     * @return
     */
    @Override
    public List<?> qryAccWCheckInfo(AccWCheckInfoDTO dto) {
        StringBuffer sql = new StringBuffer();
        sql.append("select a.center_code as centerCode, a.year_month_date as yearMonthDate, a.acc_book_type as accBookType, \n" +
                "a.acc_book_code as accBookCode, a.create_by1 as createBy1, a.create_by2 as createBy2, \n" +
                "(select c.code_name from codemanage c where c.code_type = 'GCheckFlag' and c.code_code = a.flag) as flag, \n"+
                "a.create_by3 as createBy3, a.create_by4 as createBy4, a.create_time1 as createTime1, \n" +
                "a.create_time2 as createTime2, a.create_time3 as createTime3, a.create_time4 as createTime4, \n" +
                "a.is_check as isCheck, a.check_by as checkBy, a.check_time as checkTime from AccWCheckInfo a where 1=1 ");
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();

        sql.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        sql.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        if(dto.getYearMonthDateStart()!=null && !dto.getYearMonthDateStart().equals("")){
            sql.append(" and a.year_month_date >=  ?"+paramsNo);
            params.put(paramsNo,dto.getYearMonthDateStart());
            paramsNo++;
        }
        if(dto.getYearMonthDateEnd()!=null && !dto.getYearMonthDateEnd().equals("")){
            sql.append(" and a.year_month_date <=  ?"+paramsNo);
            params.put(paramsNo,dto.getYearMonthDateEnd());
            paramsNo++;
        }
        sql.append(" order by year_month_date desc");
        return accWCheckInfoRepository.queryBySqlSC(sql.toString(),params);
    }

    /**
     * 无形资产摊销计提
     *
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult depreciation(AccWCheckInfoDTO dto) {
        //判断当前会计期间是否结转，如为结转则不允许进行凭证生成操作
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        StringBuffer sql1 = new StringBuffer();
        sql1.append("select * from accmonthtrace where  1=1 ");
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();

        sql1.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql1.append("  and year_month_date = ?"+paramsNo);
        params.put(paramsNo,dto.getYearMonthDate());
        paramsNo++;
        sql1.append("  and acc_month_stat > ?"+paramsNo);
        params.put(paramsNo,2);
        paramsNo++;
        sql1.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        sql1.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        List<?> list1 = accWCheckInfoRepository.queryBySqlSC(sql1.toString(),params);
        if(list1.size()>0){
            return InvokeResult.failure("当前会计期间已结算，不可以进行摊销计提！");
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//格式化日期
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化日期
        //判断上个会计期间是否摊销计提
        String lastYearMonthDate;//201901
        if(dto.getYearMonthDate().substring(4,6).equals("01")){//当前为1月，上个会计期间为上年12月
            lastYearMonthDate = Integer.parseInt(dto.getYearMonthDate().substring(0,4))-1+"12";
        }else{//不为1月，直接减1
            lastYearMonthDate = String.valueOf(Integer.parseInt(dto.getYearMonthDate())-1);
        }
        StringBuffer lastSql = new StringBuffer();
        lastSql.append("select * from accwcheckinfo where  1=1 ");
        paramsNo = 1;
        params.clear();

        lastSql.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        lastSql.append("  and year_month_date = ?"+paramsNo);
        params.put(paramsNo,lastYearMonthDate);
        paramsNo++;
        lastSql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        lastSql.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;

        List<?> lastList = accWCheckInfoRepository.queryBySql(lastSql.toString(),params, AccWCheckInfo.class);
        if(lastList.size()!=0){//size不等于0说明有上个会计期间
            if(((AccWCheckInfo)lastList.get(0)).getFlag().equals("0")){//判断上个会计期间状态 0未折旧
                return InvokeResult.failure("上个会计期间为未摊销状态，当前会计期间无法进行摊销操作！");//上个会计期间为未折旧状态，给出无法折旧的提示
            }
        }
        SimpleDateFormat df2= new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(dto.getYearMonthDate().substring(0,4)));
        cal.set(Calendar.MONTH, Integer.parseInt(dto.getYearMonthDate().substring(4))-1);
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //先从无形资产基本信息表(AccAssetInfo)中获取所有基本信息
        StringBuffer sql = new StringBuffer();
        sql.append("select * from IntangibleAccAssetInfo where  1=1 ");
        paramsNo = 1;
        params.clear();

        sql.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql.append("  and depre_flag = ?"+paramsNo);
        params.put(paramsNo,"Y");
        paramsNo++;
        sql.append("  and end_depre_amount < dep_years and left(REPLACE(use_start_date,'-',''),6) <= ?"+paramsNo);
        params.put(paramsNo,dto.getYearMonthDate());
        paramsNo++;
        sql.append("  and  ( clear_flag != ?"+paramsNo);
        params.put(paramsNo,"1");
        paramsNo++;
        sql.append(" OR ( clear_flag = ?"+paramsNo );
        params.put(paramsNo,"1");
        paramsNo++;
        sql.append(" AND clear_year_month > ?"+paramsNo+"))");
        params.put(paramsNo,df2.format(cal.getTime()));
        paramsNo++;
        sql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        sql.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        List<?> aaiList = accWCheckInfoRepository.queryBySqlSC(sql.toString(),params);
        //判断是否有卡片没有生成卡片凭证，若有则提示
        String cards="";
        String flagcard="1";
        if(aaiList.size()>0) {
            Map aaiMap = new HashMap();
            for (Object obj : aaiList) {
                aaiMap.putAll((Map) obj);
                //判断当前会计期间卡片是否生成凭证
                if (aaiMap.get("voucher_no") == null || aaiMap.get("voucher_no").equals("")) {
                    flagcard="2";
                    cards=cards+" "+aaiMap.get("card_code");
                }
            }
        }
        if(flagcard.equals("2")){
            return InvokeResult.failure("该会计期间下卡片 "+cards+" 没有生成凭证，不能进行计提摊销！");
        }
        int count=0;//如果所有卡片都没有生成凭证则都不能计提折旧
        if(aaiList.size()>0){
            Map aaiMap = new HashMap();
            for(Object obj : aaiList){
                aaiMap.putAll((Map) obj);
                //判断当前会计期间卡片是否生成凭证
                if(aaiMap.get("voucher_no")==null||aaiMap.get("voucher_no").equals("")){
                    //卡片没有凭证生成 不能计提折旧
                    continue;
                }
                count++;
                //判断卡片是否已经清理  若已清理 不折旧 2019-07-19
                String clearFlag=String.valueOf(aaiMap.get("clear_flag"));
                if(clearFlag.equals("1")){
                    //卡片已清理 不计提折旧
                    continue;
                }
                //先判断当前数据是否参与计提折旧
                String yearMonthDate = dto.getYearMonthDate();//当前会计期间
//

                //折旧记录表 ID赋值
                IntangibleAccDepreId adid = new IntangibleAccDepreId();
                adid.setCenterCode(centerCode);//核算单位
                adid.setBranchCode(branchCode);//基层单位
                adid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
                adid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
                adid.setCodeType("31");//管理类别编码 无形资产固定21
                adid.setAssetType(aaiMap.get("asset_type").toString());//无形资产类别编码
                adid.setYearMonthData(dto.getYearMonthDate());//折旧年月
                adid.setAssetCode(aaiMap.get("asset_code").toString());//无形资产编号

                //无形资产基本信息表 ID赋值
                IntangibleAccAssetInfoId aaiid = new IntangibleAccAssetInfoId();
                aaiid.setCenterCode(aaiMap.get("center_code").toString());//核算单位
                aaiid.setBranchCode(aaiMap.get("branch_code").toString());//基层单位
                aaiid.setAccBookType(aaiMap.get("acc_book_type").toString());//账套类型
                aaiid.setAccBookCode(aaiMap.get("acc_book_code").toString());//账套编码
                aaiid.setCodeType(aaiMap.get("code_type").toString());//管理类别编码
                aaiid.setCardCode(aaiMap.get("card_code").toString());//卡片编码

                //判断期初折旧月份是否为0，不为0直接折旧，为0当月不折旧，累计折旧+1
                String useStartdateMonth=String.valueOf(aaiMap.get("use_start_date")).substring(0,4)+String.valueOf(aaiMap.get("use_start_date")).substring(5,7);
                if(aaiMap.get("init_depre_amount").equals("0") && useStartdateMonth.equals(yearMonthDate)){//期初折旧月份为0
//                        IntangibleAccAssetInfo aai = intangibleAccAssetInfoRepository.findById(aaiid).get();
//                        aai.setEndDepreAmount(String.valueOf((Integer.parseInt(aaiMap.get("end_depre_amount").toString())+1)));//期末累计折旧月份+1
                    continue;
                }
                //如果 当前累计折旧==使用年限(月)，则不进行折旧
                if(aaiMap.get("end_depre_amount").equals(aaiMap.get("dep_years"))){
                    continue;
                }

                IntangibleAccDepre ad = new IntangibleAccDepre();
                ad.setId(adid);//联合主键
                ad.setVoucherNo("");//凭证号

                //1)本月计提折旧金额=（原值-预计残值-期末累计折旧额）/【使用年限（月）-期末累计折旧月份】
                BigDecimal assetOriginValue = new BigDecimal(aaiMap.get("asset_origin_value").toString());//无形资产原值
                BigDecimal remainsValue = new BigDecimal(aaiMap.get("remains_value").toString());//预计残值
                BigDecimal endDepreMoney = new BigDecimal(aaiMap.get("end_depre_money").toString());//期末累计折旧金额
                BigDecimal depYears = new BigDecimal(aaiMap.get("dep_years").toString());//使用年限（月）
                BigDecimal endDepreAmount = new BigDecimal(aaiMap.get("end_depre_amount").toString());//期末累计折旧月份


                //判断是否为最后一个折旧月份  累计折旧月份+1 == 使用年限(月)?
                BigDecimal monthDepreMoney;//当月折旧金额
                if(String.valueOf(Integer.parseInt(aaiMap.get("end_depre_amount").toString())+1).equals(aaiMap.get("dep_years"))){//是最后一个折旧月份
                    //3)最后一个折旧月份折旧额=无形资产原值-预计残值-期末累计折旧额
                    monthDepreMoney = assetOriginValue.subtract(remainsValue).subtract(endDepreMoney);//当月折旧金额
                    ad.setMonthDepreMoney(monthDepreMoney);//当月折旧金额
                }else{//不是最后一个折旧月份
                    //1)本月计提折旧金额=（原值-预计残值-期末累计折旧额）/【使用年限（月）-期末累计折旧月份】
                    monthDepreMoney = (assetOriginValue.subtract(remainsValue).subtract(endDepreMoney)).divide((depYears).subtract(endDepreAmount),6,BigDecimal.ROUND_HALF_UP);//当月折旧金额
                    ad.setMonthDepreMoney(monthDepreMoney);//当月折旧金额
                }

                //卡片中存入的累计折旧额 是卡片中的期初累计折旧额+本月折旧额
                BigDecimal allDepreMoney;//累计已折旧金额
                BigDecimal AllDepreQuantity;//累计已折旧量
                if(monthDepreMoney==null||monthDepreMoney.equals("")){
                    allDepreMoney = new BigDecimal("0");
                }else{  // endDepreMoney+monthDepreMoney
                    allDepreMoney =endDepreMoney.add(monthDepreMoney) ;//累计已折旧金额
                }
                if(endDepreAmount==null||endDepreAmount.equals("")){
                    AllDepreQuantity = new BigDecimal("0");
                }else{
                    AllDepreQuantity = endDepreAmount.add(new BigDecimal("1"));//当月累计已折旧量
                }
                ////////////////////////////////////
                //计算折旧记录表种的累计折旧金额 //当月折旧金额+折旧记录表中上月累计已折旧金额
                StringBuffer sqlh=new StringBuffer();
                //计提折旧月份 dto.getYearMonthDate()
                sqlh.append("select * from IntangibleAccDepre where 1=1 ");
                paramsNo = 1;
                params.clear();

                sqlh.append("  and center_code = ?"+paramsNo);
                params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                paramsNo++;
                sqlh.append("  and acc_book_code = ?"+paramsNo);
                params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                paramsNo++;
                sqlh.append("  and asset_type = ?"+paramsNo);
                params.put(paramsNo,aaiMap.get("asset_type"));
                paramsNo++;
                sqlh.append("  and asset_code = ?"+paramsNo);
                params.put(paramsNo,aaiMap.get("asset_code"));
                paramsNo++;
                List<IntangibleAccDepre> accdepress=(List<IntangibleAccDepre>)accDepreRepository.queryBySql(sqlh.toString(),params,IntangibleAccDepre.class);
                if(accdepress==null||accdepress.size()<1){
                    ad.setAllDepreMoney(monthDepreMoney);//累计已折旧金额
                    //当月折旧量+当月累计已折旧量
                    ad.setAllDepreQuantity(new BigDecimal("1"));//累计已折旧量(小时公里月等)

                }else{
                    ad.setAllDepreMoney(monthDepreMoney.add(accdepress.get(0).getAllDepreMoney()));//累计已折旧金额
                    //当月折旧量+当月累计已折旧量
                    ad.setAllDepreQuantity(new BigDecimal("1").add(accdepress.get(0).getAllDepreQuantity()));//累计已折旧量(小时公里月等)

                }
                ad.setMonthDepreQuantity(new BigDecimal("1"));//当月折旧量(小时公里月等)


                ad.setUnitCode("");//当月使用部门  找不到字段
                BigDecimal assetNetValue=new BigDecimal(aaiMap.get("asset_origin_value").toString()).subtract(new BigDecimal(aaiMap.get("impairment").toString())).subtract(allDepreMoney);
                ad.setCurrentNetValue(assetNetValue);//当前净值
                ad.setCurrentDepreMethod(aaiMap.get("dep_type").toString());//当月折旧方法
                ad.setCurrentOriginValue(new BigDecimal(aaiMap.get("asset_origin_value").toString()));//当前原值
                ad.setWorkLoadUnit("");//工作量单位  找不到字段
                ad.setOperatorBranch(null);//操作员单位  //可为空
                ad.setOperatorCode(String.valueOf(CurrentUser.getCurrentUser().getId()));//操作员
                ad.setHandleDate(df1.format(new Date()));//操作日期
                ad.setVoucherFlag("0");//生成凭证标志  未生成
                //ad.setDepreFromDate(null);//无形资产清理日期
                //无形资产折旧至日期
                String currentDate = CurrentTime.getCurrentDate();
                String depreToDates="";
                if ((currentDate.substring(0,4)+currentDate.substring(5,7)).equals(dto.getYearMonthDate())) {
                    depreToDates=currentDate;
                } else {
                    int year = Integer.parseInt(dto.getYearMonthDate().substring(0,4));
                    int month = Integer.parseInt(dto.getYearMonthDate().substring(4));
                    depreToDates=fixedassetsCardVoucherService.getLastDayOfYearMonth(year, month);
                }
                ad.setDepreFromDate(depreToDates);
                intangibleAccDepreRepository.save(ad);//保存到无形资产折旧记录表

                //无形资产基本信息表数据修改
                IntangibleAccAssetInfo aai = intangibleAccAssetInfoRepository.findById(aaiid).get();//通过ID获取当前无形资产基本信息
                //期末累计折旧月份=期末累计折旧月份+系统计提折旧月份
                aai.setEndDepreAmount(new BigDecimal(aaiMap.get("end_depre_amount").toString()).add(new BigDecimal("1")).toString());//期末累计折旧月份+1
                //期末累计折旧金额=期末累计折旧金额+系统计提折旧金额(当月折旧金额)
                aai.setEndDepreMoney(allDepreMoney);//期末累计折旧金额
                //无形资产折旧至日期
                aai.setDepreToDate(depreToDates);
                aai.setAssetNetValue(assetNetValue);
                intangibleAccAssetInfoRepository.save(aai);//保存
            }
            intangibleAccDepreRepository.flush();
            intangibleAccAssetInfoRepository.flush();
            if(count==0){
                return InvokeResult.failure("卡片没有生成凭证，当前会计期间无法进行计提操作！");
            }
        }


            //无形资产会计期间表状态修改
            AccWCheckInfoId agid = new AccWCheckInfoId();
            agid.setCenterCode(dto.getCenterCode());//核算单位
            agid.setYearMonthDate(dto.getYearMonthDate());//凭证年月;
            agid.setAccBookType(dto.getAccBookType());//账套类型
            agid.setAccBookCode(dto.getAccBookCode());//账套编码
            AccWCheckInfo ag = accWCheckInfoRepository.findById(agid).get();//通过ID查找
            ag.setFlag("1");//无形资产折旧状态 已计提
            ag.setCreateBy1(String.valueOf(CurrentUser.getCurrentUser().getId()));//无形资产折旧-计提操作人;
            ag.setCreateTime1(df.format(new Date()));//无形资产折旧-计提时间
            ag.setCreateBy3(null);//无形资产折旧-反计提操作人
            ag.setCreateTime3(null);//无形资产折旧-反计提时间
            accWCheckInfoRepository.save(ag);//保存
            return InvokeResult.success();

    }

    /**
     * 无形资产摊销反处理
     *
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult revokeDepreciation(AccWCheckInfoDTO dto) {

            //判断当前会计期间是否结转，如为结转则不允许进行凭证生成操作
        StringBuffer sql1 = new StringBuffer();
        sql1.append("select * from accmonthtrace where 1=1 ");//已结转
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();

        sql1.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql1.append("  and year_month_date = ?"+paramsNo);
        params.put(paramsNo,dto.getYearMonthDate());
        paramsNo++;
        sql1.append("  and acc_month_stat > ?"+paramsNo);
        params.put(paramsNo, 2 );
        paramsNo++;
        sql1.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        sql1.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType() );
        paramsNo++;
        List<?> list1 = accWCheckInfoRepository.queryBySqlSC(sql1.toString(),params);
            if(list1.size()>0){
                return InvokeResult.failure("当前会计期间已结算，不可以进行摊销反处理！");
            }

            //判断下个会计期间状态是否为 未摊销
            String nextYearMonthDate;//下个会计期间 201901
            if(dto.getYearMonthDate().equals("12")){//如果为12月，则下个会计期间为下年1月
                nextYearMonthDate = Integer.parseInt(dto.getYearMonthDate().substring(0,4))+1+"01";
            }else{//如果不为12月，则下个会计期间直接+1
                nextYearMonthDate = String.valueOf(Integer.parseInt(dto.getYearMonthDate())+1);
            }
            StringBuffer nextSql = new StringBuffer();
            nextSql.append("select * from accwcheckinfo where 1=1 ");
        paramsNo = 1;
        params.clear();

        nextSql.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        nextSql.append("  and year_month_date = ?"+paramsNo);
        params.put(paramsNo,nextYearMonthDate);
        paramsNo++;
        nextSql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        nextSql.append("  and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
            List<?> nextList = accWCheckInfoRepository.queryBySql(nextSql.toString(),params, AccWCheckInfo.class);
            if(nextList.size()!=0){//size不等于0，说明有下个会计期间
                if(!((AccWCheckInfo)nextList.get(0)).getFlag().equals("0")){//不为未折旧状态
                    return InvokeResult.failure("下个会计期间未进行摊销反处理，不允许当前会计期间进行摊销反处理操作！");
                }
            }
            //获取折旧表所有信息
        paramsNo = 1;
        params.clear();
        StringBuffer sqls=new StringBuffer();
        sqls.append("select * from  IntangibleAccDepre where 1=1 ");

        sqls.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sqls.append("  and year_month_data = ?"+paramsNo);
        params.put(paramsNo,dto.getYearMonthDate());
        paramsNo++;
        sqls.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
            List<IntangibleAccDepre> accdepreList=(List<IntangibleAccDepre>)intangibleAccDepreRepository.queryBySql(sqls.toString(),params,IntangibleAccDepre.class);
            if(accdepreList.size()>0){
                for(int i=0;i<accdepreList.size();i++){
                    String assetCode=accdepreList.get(i).getId().getAssetCode();
                    BigDecimal monthDepreMoney=accdepreList.get(i).getMonthDepreMoney();
                    BigDecimal monthDepreQuantity=accdepreList.get(i).getMonthDepreQuantity();
                    StringBuffer asqls=new StringBuffer("select * from IntangibleAccAssetInfo where 1=1 ");
                    paramsNo = 1;
                    params.clear();

                    asqls.append("  and center_code = ?"+paramsNo);
                    params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                    paramsNo++;
                    asqls.append("  and acc_book_code = ?"+paramsNo);
                    params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                    paramsNo++;
                    asqls.append("  and code_type = ?"+paramsNo);
                    params.put(paramsNo,accdepreList.get(i).getId().getCodeType());
                    paramsNo++;
                    asqls.append("  and asset_code = ?"+paramsNo);
                    params.put(paramsNo,assetCode);
                    paramsNo++;

                    List<IntangibleAccAssetInfo> accass=(List<IntangibleAccAssetInfo>) intangibleAccAssetInfoRepository.queryBySql(asqls.toString(),params,IntangibleAccAssetInfo.class);
                    IntangibleAccAssetInfo acca=accass.get(0);
                    String str1 =acca.getDepYears().toString();
                    String str2 =acca.getEndDepreAmount();
                    /*if(accass.size() == 0 && !acca.getInitDepreAmount().equals("0")){//当前会计期间没进行折旧操作，不对累计折旧月份和金额进项修改
//                    if(list.size() == 0){//当前会计期间没进行折旧操作，不对累计折旧月份和金额进项修改
                        continue;
                    }else if(accass.size() == 0 && str1.equals(str2)){
                        continue;
                    }
                    //如果期初为0，期末为1，则不需要修改基本信息表中的累计期末值，但累计折旧月份-1
                    String useStartdateMonth=acca.getUseStartDate().substring(0,4)+acca.getUseStartDate().substring(5,7);
                    if(acca.getInitDepreAmount().equals("0") && useStartdateMonth.equals(dto.getYearMonthDate())){
                        continue;
                    }
                    //如果 期末累计折旧月份==期初折旧月份，则不进行折旧
                    if(acca.getEndDepreAmount().equals(acca.getInitDepreAmount())){
                        continue;
                    }*/
                    //期末累计折旧月份=期末已折旧月份-系统计提折旧月份
                    acca.setEndDepreAmount(new BigDecimal(acca.getEndDepreAmount()).subtract(new BigDecimal("1")).toString());
                    //期末累计折旧金额=期末折旧金额-系统计提折旧金额(当月折旧金额)
                    acca.setEndDepreMoney(acca.getEndDepreMoney().subtract(monthDepreMoney));
                    //将卡片的已折旧至日期改为上一次的折旧操作时的日期，如果在折旧记录中找不到，就设置为卡片的起始折旧日期
                    //获取上一个会计期间 201901
                    String yearMonthDate=dto.getYearMonthDate();
                   /* String yearMonthDateup="";
                    if(yearMonthDate.substring(4).equals("01")){
                        //201901
                        yearMonthDateup=(Integer.parseInt(yearMonthDate.substring(0,4))-1)+"12";
                    }else{
                        yearMonthDateup=(Integer.parseInt(yearMonthDate)-1)+"";
                    }*/
                    paramsNo = 1;
                    params.clear();
                    StringBuffer lastaccde=new StringBuffer("select * from  IntangibleAccDepre where 1=1 ");

                    lastaccde.append("  and center_code = ?"+paramsNo);
                    params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                    paramsNo++;
                    lastaccde.append("  and acc_book_code = ?"+paramsNo);
                    params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                    paramsNo++;
                    lastaccde.append("  and asset_code = ?"+paramsNo + " order by year_month_data desc");
                    params.put(paramsNo,assetCode);
                    List<IntangibleAccDepre> accDeprup= (List<IntangibleAccDepre>)intangibleAccDepreRepository.queryBySql(lastaccde.toString(),params,IntangibleAccDepre.class);
                    if(accDeprup.size()==1){
                        acca.setDepreToDate(acca.getDepreFromDate());
                    }else{
                        //2019-01-12
                        acca.setDepreToDate(accDeprup.get(1).getDepreFromDate());
                    }
                    //净值
                    acca.setAssetNetValue(acca.getAssetNetValue().add(monthDepreMoney));
                    intangibleAccAssetInfoRepository.save(acca);//保存

                    intangibleAccDepreRepository.delete(accdepreList.get(i));

                }
                intangibleAccDepreRepository.flush();
            }

            //无形资产会计期间表状态修改
            AccWCheckInfoId agid = new AccWCheckInfoId();
            agid.setCenterCode(dto.getCenterCode());//核算单位
            agid.setYearMonthDate(dto.getYearMonthDate());//凭证年月;
            agid.setAccBookType(dto.getAccBookType());//账套类型
            agid.setAccBookCode(dto.getAccBookCode());//账套编码
            AccWCheckInfo ag = accWCheckInfoRepository.findById(agid).get();//通过ID查找
            ag.setFlag("0");//无形资产折旧状态 未计提
            ag.setCreateBy1(null);//固定资产折旧-计提操作人;
            ag.setCreateTime1(null);//固定资产折旧-计提时间
            ag.setCreateBy3(String.valueOf(CurrentUser.getCurrentUser().getId()));//固定资产折旧-反计提操作人
            ag.setCreateTime3(CurrentTime.getCurrentTime());//固定资产折旧-反计提时间
            accWCheckInfoRepository.save(ag);//保存

            return InvokeResult.success();

    }

    /**
     * 无形资产折旧管理 凭证生成
     *
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult addVoucher(AccWCheckInfoDTO dto)  throws Exception {

        //先查询所以一级专项
        StringBuffer sql0 = new StringBuffer();
        sql0.append("SELECT * FROM specialinfo s WHERE 1=1 ");
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();

        sql0.append("  and endflag = ?"+paramsNo);
        params.put(paramsNo,"1");
        paramsNo++;
        sql0.append("  and useflag = ?"+paramsNo);
        params.put(paramsNo,"1");
        paramsNo++;
        sql0.append(" AND (s.super_special IS NULL OR s.super_special='')");
        sql0.append("  and s.account = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        List<?> specialList = accAssetCodeTypeRepository.queryBySqlSC(sql0.toString(),params);
        Map specialMap1 = new HashMap();
        Map specialMap2 = new HashMap();
        for (Object specialObj : specialList) {
            specialMap1.putAll((Map)specialObj);
            specialMap2.put(specialMap1.get("id").toString(),specialMap1.get("special_code"));
        }
        //判断当前会计期间是否结转，如为结转则不允许进行凭证生成操作
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        StringBuffer sql1 = new StringBuffer();
        sql1.append("select * from accmonthtrace where 1=1 ");//已结转
        paramsNo = 1;
        params.clear();
        sql1.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sql1.append("  and year_month_date = ?"+paramsNo);
        params.put(paramsNo,dto.getYearMonthDate());
        paramsNo++;
        sql1.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        sql1.append("  and acc_month_stat > ?"+paramsNo);
        params.put(paramsNo,2);
        paramsNo++;
        List<?> list1 = accWCheckInfoRepository.queryBySqlSC(sql1.toString(),params);
        if (list1.size() > 0) {
            return InvokeResult.failure("当前会计期间已结算，不可以进行凭证生成！");
        }
        if (dto.getFlag().equals("未计提") || dto.getFlag().equals("0")) {
            return InvokeResult.failure("当前会计期间未计提摊销，不可以进行凭证生成！");
        }
        //判断上个会计期间是否生成凭证
        String lastYearMonthDate;
        if (dto.getYearMonthDate().substring(4, 6).equals("01")) {//当前为1月，上个会计期间为上年12月
            lastYearMonthDate = Integer.parseInt(dto.getYearMonthDate().substring(0, 4)) - 1 + "12";
        } else {//不为1月，直接减1
            lastYearMonthDate = String.valueOf(Integer.parseInt(dto.getYearMonthDate()) - 1);
        }
        StringBuffer lastSql = new StringBuffer();
        lastSql.append("select * from accwcheckinfo where 1=1 ");
        paramsNo = 1;
        params.clear();

        lastSql.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        lastSql.append("  and year_month_date = ?"+paramsNo);
        params.put(paramsNo,lastYearMonthDate);
        paramsNo++;
        lastSql.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        List<?> lastList = accWCheckInfoRepository.queryBySql(lastSql.toString(),params, AccWCheckInfo.class);
        if (lastList.size() != 0) {//size不等于0说明有上个会计期间
            if (!((AccWCheckInfo) lastList.get(0)).getFlag().equals("3")) {//判断上个会计期间状态 3已生成凭证
                return InvokeResult.failure("上个会计期间为未生成凭证状态，当前会计期间无法进行凭证生成操作！");//上个会计期间为未生成凭证状态，给出提示
            }
        }
        //判断制单日期即凭证日期必须在当前会计期间内
        if(!(dto.getCreateTime2().substring(0,4)+dto.getCreateTime2().substring(5,7)).equals(dto.getYearMonthDate())){
            return InvokeResult.failure("制单日期请选择当前会计期间所在月份！");
        }

        //格式化创建时间
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df1.format(new Date());
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        //-------------------------
        //先从无形资产折旧记录表中获取所有信息
        StringBuffer sqls1 = new StringBuffer();
        sqls1.append("select * from  IntangibleAccDepre where 1=1 ");
        paramsNo = 1;
        params.clear();

        sqls1.append("  and center_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
        paramsNo++;
        sqls1.append("  and year_month_data = ?"+paramsNo);
        params.put(paramsNo,dto.getYearMonthDate());
        paramsNo++;
        sqls1.append("  and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        sqls1.append("  and (voucher_no is NULL or voucher_no='' )");

        List<IntangibleAccDepre> accdepreList = (List<IntangibleAccDepre>) intangibleAccDepreRepository.queryBySql(sqls1.toString(),params, IntangibleAccDepre.class);
        int test1 = 0;
        if (accdepreList.size() > 0) {
            for (IntangibleAccDepre accD : accdepreList) {
                String accBookCode = accD.getId().getAccBookCode();
                String assetCode = accD.getId().getAssetCode();
                StringBuffer sqls2 = new StringBuffer();
                sqls2.append("select * from IntangibleAccAssetInfo where 1=1 ");
                paramsNo = 1;
                params.clear();

                sqls2.append("  and center_code = ?"+paramsNo);
                params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                paramsNo++;
                sqls2.append("  and acc_book_code = ?"+paramsNo);
                params.put(paramsNo,accBookCode);
                paramsNo++;
                sqls2.append("  and asset_code = ?"+paramsNo);
                params.put(paramsNo,assetCode);
                paramsNo++;
                IntangibleAccAssetInfo accAssetInfo = (IntangibleAccAssetInfo) intangibleAccDepreRepository.queryBySql(sqls2.toString(),params, IntangibleAccAssetInfo.class).get(0);
                //-------------------------凭证主表录入开始------------------------------
                AccMainVoucherId amvId = new AccMainVoucherId();
                amvId.setCenterCode(CurrentUser.getCurrentLoginManageBranch());//核算单位
                amvId.setBranchCode(CurrentUser.getCurrentLoginManageBranch());//基层单位
                amvId.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
                amvId.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码

                //从accvoucherno表中获取最大凭证号
                StringBuffer maxVoucherNoSql = new StringBuffer();
                maxVoucherNoSql.append("select * from accvoucherno a where 1=1 ");

                paramsNo = 1;
                params.clear();
                maxVoucherNoSql.append("  and year_month_date = ?"+paramsNo);
                params.put(paramsNo,dto.getYearMonthDate());
                paramsNo++;
                maxVoucherNoSql.append("  and acc_book_code = ?"+paramsNo);
                params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                paramsNo++;

                List<?> maxVoucherNoList = accVoucherNoRespository.queryBySql(maxVoucherNoSql.toString(),params, AccVoucherNo.class);
                //通过年月 生成凭证号
                String maxVoucherNo = ((AccVoucherNo) maxVoucherNoList.get(0)).getVoucherNo();
                int len = 6 - maxVoucherNo.length();
                for (int i = 0; i < len; i++) {//格式：00001
                    maxVoucherNo = "0" + maxVoucherNo;
                }
                maxVoucherNo = centerCode + ((AccVoucherNo) maxVoucherNoList.get(0)).getId().getYearMonthDate().substring(2, 6) + maxVoucherNo;
//                    amvId.setYearMonthDate(((AccVoucherNo)maxVoucherNoList.get(0)).getId().getYearMonthDate());//年月
                amvId.setYearMonthDate(dto.getYearMonthDate());//年月
                amvId.setVoucherNo(maxVoucherNo);//凭证号

                AccMainVoucher amv = new AccMainVoucher();
                amv.setId(amvId);//联合主键
                amv.setVoucherType("4");//凭证类型 3固资自转
                amv.setGenerateWay("1");//录入方式 1自动

                //加上制单日期后
                amv.setVoucherDate(dto.getCreateTime2());

                amv.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//制单人
                amv.setVoucherFlag("1");//生成凭证标志 未复核
                amv.setCreateTime(time);//创建时间
                accMainVoucherRespository.save(amv);//凭证主表信息录入
//-------------------------凭证主表录入结束------------------------------
                    test1++;
//-------------------------凭证子表录入开始------------------------------
                AccSubVoucherId asvId = new AccSubVoucherId();
                asvId.setCenterCode(centerCode);//核算单位
                asvId.setBranchCode(CurrentUser.getCurrentLoginManageBranch());//基层单位
                String accBookType = CurrentUser.getCurrentLoginAccountType();//账套类型
                asvId.setAccBookType(accBookType);//账套类型
                // String accBookCode = CurrentUser.getCurrentLoginAccount();//账套编码
                asvId.setAccBookCode(accBookCode);//账套编码
//                    asvId.setYearMonthDate(((AccVoucherNo)maxVoucherNoList.get(0)).getId().getYearMonthDate());//年月 (生成方式见上方主表)
                asvId.setYearMonthDate(dto.getYearMonthDate());//年月 (生成方式见上方主表)
                asvId.setVoucherNo(maxVoucherNo);//凭证号 (生成方式见上方主表)


                //通过管理类别编码去获取科目代码、科目段、专项段
                StringBuffer sql3 = new StringBuffer();
                sql3.append("select * from IntangibleAccAssetCodeType where 1=1 ");
                paramsNo = 1;
                params.clear();
                sql3.append("  and acc_book_type = ?"+paramsNo);
                params.put(paramsNo,accBookType);
                paramsNo++;
                sql3.append("  and acc_book_code = ?"+paramsNo);
                params.put(paramsNo,accBookCode);
                paramsNo++;
                sql3.append("  and code_type = ?"+paramsNo);
                params.put(paramsNo,accAssetInfo.getId().getCodeType());
                paramsNo++;
                sql3.append("  and asset_type = ?"+paramsNo);
                params.put(paramsNo,accAssetInfo.getAssetType());
                paramsNo++;
                List<?> aactList = intangibleAccAssetCodeTypeRepository.queryBySql(sql3.toString(),params, IntangibleAccAssetCodeType.class);
                //因为一个借方，一个贷方，所以循环执行两次
                for (int i = 0; i < 2; i++) {
                    AccSubVoucher asv = new AccSubVoucher();
                    asv.setId(asvId);//联合主键
                    //先根据主键去 凭证子表中查询，如果信息存在则凭证号+1，若不存在，凭证号为1
                    StringBuffer sql4 = new StringBuffer();
                    sql4.append("select * from accsubvoucher where 1=1 ");
                    paramsNo = 1;
                    params.clear();

                    sql4.append("  and center_code = ?"+paramsNo);
                    params.put(paramsNo,centerCode);
                    paramsNo++;
                    sql4.append("  and acc_book_type = ?"+paramsNo);
                    params.put(paramsNo,accBookType);
                    paramsNo++;
                    sql4.append("  and acc_book_code = ?"+paramsNo);
                    params.put(paramsNo,accBookCode);
                    paramsNo++;
                    sql4.append("  and year_month_date = ?"+paramsNo);
                    params.put(paramsNo,((AccVoucherNo) maxVoucherNoList.get(0)).getId().getYearMonthDate());
                    paramsNo++;
                    sql4.append("  and voucher_no = ?"+paramsNo);
                    params.put(paramsNo,maxVoucherNo);
                    paramsNo++;
                    sql4.append("  and suffix_no= (select MAX(suffix_no) from accsubvoucher where voucher_no = ?" + paramsNo + ")");
                    params.put(paramsNo,maxVoucherNo);
                    paramsNo++;

                    List<?> list = accSubVoucherRespository.queryBySql(sql4.toString(),params, AccSubVoucher.class);
                    if (list.size() != 0) {//存在，需要分录，分类号+1
                        asvId.setSuffixNo(String.valueOf(Integer.parseInt(((AccSubVoucher) list.get(0)).getId().getSuffixNo()) + 1));//凭证分录号
                    } else {//不存在，不需要分录，分类号为1
                        asvId.setSuffixNo("1");//凭证分录号
                    }
                    //当月计提折旧额
//                    String monthDepreMoneyss = String.valueOf(accAssetInfo.getEndDepreMoney().subtract(accAssetInfo.getInitDepreMoney()));
                    BigDecimal monthDepreMoneyss = accD.getMonthDepreMoney();
                    switch (i) {
                        case 0://借方
                            //if (monthDepreMoneyss.equals("0.00")) {
                            if(monthDepreMoneyss.compareTo(BigDecimal.ZERO) == 0){
                                continue;//金额为0，则不记录该分录
                            }
                            String itemCode3 = ((IntangibleAccAssetCodeType) aactList.get(0)).getItemCode3();
                            String[] itemCodeArr = itemCode3.split("/");
                            if (itemCodeArr.length >= 1) {
                                asv.setItemCode(itemCodeArr[0]);//科目代码
                                asv.setF01(itemCodeArr[0]);
                            }
                            if (itemCodeArr.length >= 2) {
                                asv.setF02(itemCodeArr[1]);
                            }
                            if (itemCodeArr.length >= 3) {
                                asv.setF03(itemCodeArr[2]);
                            }
                            if (itemCodeArr.length >= 4) {
                                asv.setF04(itemCodeArr[3]);
                            }
                            if (itemCodeArr.length >= 5) {
                                asv.setF05(itemCodeArr[4]);
                            }
                            if (itemCodeArr.length >= 6) {
                                asv.setF06(itemCodeArr[5]);
                            }
                            if (itemCodeArr.length >= 7) {
                                asv.setF07(itemCodeArr[6]);
                            }
                            if (itemCodeArr.length >= 8) {
                                asv.setF08(itemCodeArr[7]);
                            }
                            if (itemCodeArr.length >= 9) {
                                asv.setF09(itemCodeArr[8]);
                            }
                            if (itemCodeArr.length >= 10) {
                                asv.setF10(itemCodeArr[9]);
                            }
                            if (itemCodeArr.length >= 11) {
                                asv.setF11(itemCodeArr[10]);
                            }
                            if (itemCodeArr.length >= 12) {
                                asv.setF12(itemCodeArr[11]);
                            }
                            if (itemCodeArr.length >= 13) {
                                asv.setF13(itemCodeArr[12]);
                            }
                            if (itemCodeArr.length >= 14) {
                                asv.setF14(itemCodeArr[13]);
                            }
                            if (itemCodeArr.length >= 15) {
                                asv.setF15(itemCodeArr[14]);
                            }
//                                asv.setS01(((IntangibleAccAssetCodeType)aactList.get(0)).getArticleCode3());//
                            String flag = null;
                            StringBuffer sqls6 = new StringBuffer();
                            sqls6.append("SELECT * FROM subjectinfo WHERE 1=1 ");
                            paramsNo = 1;
                            params.clear();
                            sqls6.append("  and account = ?"+paramsNo);
                            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                            paramsNo++;
                            sqls6.append("  and CONCAT(all_subject,subject_code,'/') = ?"+paramsNo);
                            params.put(paramsNo,itemCode3);
                            paramsNo++;
                            List<?> specialCount1 = accSubVoucherRespository.queryBySqlSC(sqls6.toString(),params);
                            for (Object o : specialCount1) {
                                Map specialCountMap = new HashMap();
                                specialCountMap.putAll((Map) o);
                                String[] specialIds = specialCountMap.get("special_id").toString().split(",");
                                if (specialIds.length == 1) {
                                    String specialCode = ((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3();
                                    if (specialCode == null || specialCode.equals("")) {
                                        //科目有专项 类别无专项 出错
                                        throw new Exception("false");
                                    }
                                    flag = qrySegmentFlag(specialCode);//用来判断存放位置
                                    setValue(flag, asv, aactList);
                                    asv.setDirectionOther(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());//专项方向段

                                }
                                if(specialIds.length > 1){
                                    Set<Map.Entry<String,String>> entries = specialMap2.entrySet();
                                    for(Map.Entry<String, String> entry : entries){
                                        String key = entry.getKey();
                                        String value = entry.getValue();
                                        // 213,218
                                        if(specialIds[0].equals(key)&&value.equals("BM")){
                                            StringBuilder sql6 = new StringBuilder();
                                            sql6.append("SELECT (SELECT s.special_code FROM specialinfo s WHERE s.id=a.unit_code) AS specialCode FROM IntangibleAccAssetInfo a  WHERE 1=1 ");
                                            paramsNo = 1;
                                            params.clear();

                                            sql6.append("  and center_code = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                                            paramsNo++;
                                            sql6.append("  and asset_code = ?"+paramsNo);
                                            params.put(paramsNo,assetCode);
                                            paramsNo++;
                                            sql6.append("  and acc_book_code = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                                            paramsNo++;
                                            sql6.append("  and acc_book_type = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
                                            paramsNo++;
                                            List<?> branchList = intangibleAccAssetCodeTypeRepository.queryBySql(sql6.toString(),params);
                                            for (Object code : branchList) {
                                                String branchCode = (String) code;
//                                                String branchCode = "ZJDBN";
                                                flag = qrySegmentFlag(branchCode);//用来判断存放位置
                                                if(flag!=null && !flag.equals("")){
                                                    if(flag.equals("s01")){asv.setS01(branchCode);}else
                                                    if(flag.equals("s02")){asv.setS02(branchCode);}else
                                                    if(flag.equals("s03")){asv.setS03(branchCode);}else
                                                    if(flag.equals("s04")){asv.setS04(branchCode);}else
                                                    if(flag.equals("s05")){asv.setS05(branchCode);}else
                                                    if(flag.equals("s06")){asv.setS06(branchCode);}else
                                                    if(flag.equals("s07")){asv.setS07(branchCode);}else
                                                    if(flag.equals("s08")){asv.setS08(branchCode);}else
                                                    if(flag.equals("s09")){asv.setS09(branchCode);}else
                                                    if(flag.equals("s10")){asv.setS10(branchCode);}else
                                                    if(flag.equals("s11")){asv.setS11(branchCode);}else
                                                    if(flag.equals("s12")){asv.setS12(branchCode);}else
                                                    if(flag.equals("s13")){asv.setS13(branchCode);}else
                                                    if(flag.equals("s14")){asv.setS14(branchCode);}else
                                                    if(flag.equals("s15")){asv.setS15(branchCode);}else
                                                    if(flag.equals("s16")){asv.setS16(branchCode);}else
                                                    if(flag.equals("s17")){asv.setS17(branchCode);}else
                                                    if(flag.equals("s18")){asv.setS18(branchCode);}else
                                                    if(flag.equals("s19")){asv.setS19(branchCode);}else
                                                    if(flag.equals("s20")){asv.setS20(branchCode);}

                                                };
                                                String directionidx=((IntangibleAccAssetCodeType)aactList.get(0)).getItemCode3();
                                                if(directionidx.charAt(directionidx.length()-1)!='/'){directionidx=directionidx+"/";}
                                                asv.setDirectionIdx(directionidx);//科目方向段
//                                                String directionOther=((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3();
                                                String directionOther="YS20002001";
                                                flag = qrySegmentFlag(directionOther);//用来判断存放位置
                                                setValue(flag,asv,aactList,directionOther);

                                                if(directionOther==null||directionOther.equals("")){
                                                    //科目有专项 类别无专项 出错
                                                    throw new Exception("false");
                                                }
                                                if(branchCode==null||branchCode.equals("")){
                                                    //卡片使用部门专项为空，科目不为null
                                                    throw new Exception("unitfalse");
                                                }

                                                String directionOtherValue=branchCode+","+directionOther;
                                                asv.setDirectionOther(directionOtherValue);
                                            };
                                        }
                                        if (specialIds[1].equals(key)&&value.equals("BM")){
                                            StringBuilder sql6 = new StringBuilder();
                                            sql6.append("SELECT (SELECT s.special_code FROM specialinfo s WHERE s.id=a.unit_code) AS specialCode FROM intangibleaccassetinfo a  WHERE 1=1 ");
                                            paramsNo = 1;
                                            params.clear();

                                            sql6.append("  and center_code = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                                            paramsNo++;
                                            sql6.append("  and asset_code = ?"+paramsNo);
                                            params.put(paramsNo,assetCode);
                                            paramsNo++;
                                            sql6.append("  and acc_book_code = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                                            paramsNo++;
                                            sql6.append("  and acc_book_type = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
                                            paramsNo++;
                                            List<?> branchList = intangibleAccAssetCodeTypeRepository.queryBySql(sql6.toString(),params);
                                            for (Object code : branchList) {
                                                String branchCode = (String) code;
//                                                String branchCode = "ZJDBN";
                                                flag = qrySegmentFlag(branchCode);//用来判断存放位置
                                                if(flag!=null && !flag.equals("")){
                                                    if(flag.equals("s01")){asv.setS01(branchCode);}else
                                                    if(flag.equals("s02")){asv.setS02(branchCode);}else
                                                    if(flag.equals("s03")){asv.setS03(branchCode);}else
                                                    if(flag.equals("s04")){asv.setS04(branchCode);}else
                                                    if(flag.equals("s05")){asv.setS05(branchCode);}else
                                                    if(flag.equals("s06")){asv.setS06(branchCode);}else
                                                    if(flag.equals("s07")){asv.setS07(branchCode);}else
                                                    if(flag.equals("s08")){asv.setS08(branchCode);}else
                                                    if(flag.equals("s09")){asv.setS09(branchCode);}else
                                                    if(flag.equals("s10")){asv.setS10(branchCode);}else
                                                    if(flag.equals("s11")){asv.setS11(branchCode);}else
                                                    if(flag.equals("s12")){asv.setS12(branchCode);}else
                                                    if(flag.equals("s13")){asv.setS13(branchCode);}else
                                                    if(flag.equals("s14")){asv.setS14(branchCode);}else
                                                    if(flag.equals("s15")){asv.setS15(branchCode);}else
                                                    if(flag.equals("s16")){asv.setS16(branchCode);}else
                                                    if(flag.equals("s17")){asv.setS17(branchCode);}else
                                                    if(flag.equals("s18")){asv.setS18(branchCode);}else
                                                    if(flag.equals("s19")){asv.setS19(branchCode);}else
                                                    if(flag.equals("s20")){asv.setS20(branchCode);}

                                                };
                                                String directionidx=((IntangibleAccAssetCodeType)aactList.get(0)).getItemCode3();
                                                if(directionidx.charAt(directionidx.length()-1)!='/'){directionidx=directionidx+"/";}
                                                asv.setDirectionIdx(directionidx);//科目方向段
//                                                String directionOther=((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3();
                                                String directionOther = "YS20002001";
                                                flag = qrySegmentFlag(directionOther);//用来判断存放位置
                                                setValue(flag,asv,aactList,directionOther);

                                                if(directionOther==null||directionOther.equals("")){
                                                    //科目有专项 类别无专项 出错
                                                    throw new Exception("false");
                                                }
                                                if(branchCode==null||branchCode.equals("")){
                                                    //卡片使用部门专项为空，科目不为null
                                                    throw new Exception("unitfalse");
                                                }

                                                String directionOtherValue=directionOther+","+branchCode;
                                                asv.setDirectionOther(directionOtherValue);
                                            };
                                        }
                                    }
                                }
                            }

                            String directionidx = ((IntangibleAccAssetCodeType) aactList.get(0)).getItemCode3();
                            if (directionidx.charAt(directionidx.length() - 1) != '/') {
                                directionidx = directionidx + "/";
                            }
                            asv.setDirectionIdx(directionidx);//科目方向段

                            //根据科目代码去获取科目名称
                            StringBuffer directionIdxName = new StringBuffer();
                            String itemCodeStr = "";
                            for (int j = 0; j < itemCodeArr.length; j++) {
                                StringBuffer nameSql = new StringBuffer();
                                itemCodeStr = itemCodeStr + itemCodeArr[j] + "/";
                                nameSql.append("select * from subjectinfo where 1=1 ");
                                paramsNo = 1;
                                params.clear();

                                nameSql.append(" and  CONCAT(all_subject,subject_code,'/')= ?"+paramsNo);
                                params.put(paramsNo,itemCodeStr);
                                paramsNo++;
                                nameSql.append("  and account = ?"+paramsNo);
                                params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                                paramsNo++;
                                List<?> nameList = accAssetCodeTypeRepository.queryBySql(nameSql.toString(),params, SubjectInfo.class);
                                if (j == 0) {//第一次追加科目名称直接追加，非第一次追加在前面加"/"
                                    directionIdxName.append(((SubjectInfo) nameList.get(0)).getSubjectName());
                                } else {
                                    directionIdxName.append("/" + ((SubjectInfo) nameList.get(0)).getSubjectName());

                                }
                            }
                            String directionidxNames = directionIdxName.toString();
                            if (directionidxNames.charAt(directionidxNames.length() - 1) != '/') {
                                directionidxNames = directionidxNames + "/";
                            }
                            asv.setDirectionIdxName(directionidxNames.toString());//科目方向段名称

//                            asv.setDirectionOther(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());//专项方向段
                            //判断科目专项是否相同，合并金额
                            StringBuffer sqls4 = new StringBuffer();
                            sqls4.append("select * from accsubvoucher where 1=1 ");
                            paramsNo = 1;
                            params.clear();

                            sqls4.append("  and acc_book_code = ?"+paramsNo);
                            params.put(paramsNo,accBookCode);
                            paramsNo++;
                            sqls4.append("  and voucher_no = ?"+paramsNo);
                            params.put(paramsNo,maxVoucherNo);
                            paramsNo++;
                            sqls4.append("  and direction_idx = ?"+paramsNo);
                            params.put(paramsNo,asv.getDirectionIdx());
                            paramsNo++;
                            sqls4.append("  and direction_other = ?"+paramsNo);
                            params.put(paramsNo,asv.getDirectionOther());
                            paramsNo++;
                            List<AccSubVoucher> accsubList1 = (List<AccSubVoucher>) accSubVoucherRespository.queryBySql(sqls4.toString(),params, AccSubVoucher.class);
                            if (accsubList1 != null && accsubList1.size() > 0) {
                                AccSubVoucher accsub = accsubList1.get(0);
                                accsub.setDebitSource(accsub.getDebitSource().add(monthDepreMoneyss));//原币借方金额 同一无形资产类别编码下卡片“金额”字段值
                                accsub.setCreditSource(new BigDecimal(0));//原币贷方金额
                                accsub.setDebitDest(accsub.getDebitDest().add(monthDepreMoneyss));//本位币借方金额 值同原币
                                accsub.setCreditDest(new BigDecimal(0));//本位币贷方金额

                                accSubVoucherRespository.save(accsub);//凭证子表信息录入
                                break;
                            }
                            asv.setRemark("提取" + amvId.getYearMonthDate().substring(0, 4) + "年" + amvId.getYearMonthDate().substring(4, 6) + "月无形资产摊销");//摘要 默认置空
                            asv.setCurrency(currency);//原币别编码
                            asv.setExchangeRate(new BigDecimal(0));//当前汇率 默认0
                            asv.setDebitSource(monthDepreMoneyss);//原币借方金额 同一无形资产类别编码下卡片“金额”字段值
                            asv.setCreditSource(new BigDecimal(0));//原币贷方金额
                            asv.setDebitDest(monthDepreMoneyss);//本位币借方金额 值同原币
                            asv.setCreditDest(new BigDecimal(0));//本位币贷方金额
                            asv.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//制单人
                            asv.setCreateTime(time);//创建时间
                            accSubVoucherRespository.save(asv);//凭证子表信息录入
                            break;

                        case 1://贷方
                            if (monthDepreMoneyss.equals("0.00")) {
                                continue;//金额为0不记录该分录
                            }
                            String itemCode2 = ((IntangibleAccAssetCodeType) aactList.get(0)).getItemCode2();
                            String[] itemCodeArr2 = itemCode2.split("/");
                            if (itemCodeArr2.length >= 1) {
                                asv.setItemCode(itemCodeArr2[0]);//科目代码
                                asv.setF01(itemCodeArr2[0]);
                            }
                            if (itemCodeArr2.length >= 2) {
                                asv.setF02(itemCodeArr2[1]);
                            }
                            if (itemCodeArr2.length >= 3) {
                                asv.setF03(itemCodeArr2[2]);
                            }
                            if (itemCodeArr2.length >= 4) {
                                asv.setF04(itemCodeArr2[3]);
                            }
                            if (itemCodeArr2.length >= 5) {
                                asv.setF05(itemCodeArr2[4]);
                            }
                            if (itemCodeArr2.length >= 6) {
                                asv.setF06(itemCodeArr2[5]);
                            }
                            if (itemCodeArr2.length >= 7) {
                                asv.setF07(itemCodeArr2[6]);
                            }
                            if (itemCodeArr2.length >= 8) {
                                asv.setF08(itemCodeArr2[7]);
                            }
                            if (itemCodeArr2.length >= 9) {
                                asv.setF09(itemCodeArr2[8]);
                            }
                            if (itemCodeArr2.length >= 10) {
                                asv.setF10(itemCodeArr2[9]);
                            }
                            if (itemCodeArr2.length >= 11) {
                                asv.setF11(itemCodeArr2[10]);
                            }
                            if (itemCodeArr2.length >= 12) {
                                asv.setF12(itemCodeArr2[11]);
                            }
                            if (itemCodeArr2.length >= 13) {
                                asv.setF13(itemCodeArr2[12]);
                            }
                            if (itemCodeArr2.length >= 14) {
                                asv.setF14(itemCodeArr2[13]);
                            }
                            if (itemCodeArr2.length >= 15) {
                                asv.setF15(itemCodeArr2[14]);
                            }
//                                asv.setS09(((IntangibleAccAssetCodeType)aactList.get(0)).getArticleCode2());//
                            String flag2=null;                                    //根据科目代码去科目信息表查专项个数。
                            StringBuffer sql6 = new StringBuffer();
                            sql6.append("SELECT * FROM subjectinfo WHERE 1=1 ");
                            paramsNo = 1;
                            params.clear();
                            sql6.append(" and account= ?"+paramsNo);
                            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                            paramsNo++;
                            sql6.append(" and CONCAT(all_subject,subject_code,'/')= ?"+paramsNo);
                            params.put(paramsNo,itemCode2);
                            paramsNo++;
                            List<?> specialCount2 = accSubVoucherRespository.queryBySqlSC(sql6.toString(),params);
                            for(Object o : specialCount2){
                                Map specialCountMap = new HashMap();
                                specialCountMap.putAll((Map)o);
                                String[] specialIds = specialCountMap.get("special_id").toString().split(",");
                                Set<Map.Entry<String,String>> entries = specialMap2.entrySet();
                                for(Map.Entry<String, String> entry : entries){
                                    String key = entry.getKey();
                                    String value = entry.getValue();
                                    if(specialIds.length==1&&specialIds[0].equals(key)){
                                        String specialCode = ((IntangibleAccAssetCodeType)aactList.get(0)).getArticleCode2();
                                        if(specialCode==null||specialCode.equals("")){
                                            //科目有专项 类别无专项 出错
                                            throw new Exception("false");
                                        }
                                        flag2 = qrySegmentFlag(specialCode);//用来判断存放位置
                                        setValue1(flag2,asv,aactList);
                                        asv.setDirectionOther(((IntangibleAccAssetCodeType)aactList.get(0)).getArticleCode2());//专项方向段

                                    }

                                    if(specialIds.length>1) {
                                        if (specialIds[0].equals(key)&&value.equals("BM")){
                                            StringBuilder sql7 = new StringBuilder();
                                            sql7.append("SELECT (SELECT s.special_code FROM specialinfo s WHERE s.id=a.unit_code) AS specialCode FROM IntangibleAccAssetInfo a  WHERE 1=1 ");
                                            paramsNo = 1;
                                            params.clear();

                                            sql7.append("  and a.center_code = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                                            paramsNo++;
                                            sql7.append("  and a.asset_code = ?"+paramsNo);
                                            params.put(paramsNo,assetCode);
                                            paramsNo++;
                                            sql7.append("  and a.acc_book_code = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                                            paramsNo++;
                                            sql7.append("  and a.acc_book_type = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
                                            paramsNo++;
                                            List<?> branchList = accSubVoucherRespository.queryBySql(sql7.toString(),params);
                                            for (Object code : branchList) {
                                                String branchCode = (String) code;
//                                                String branchCode = "ZJDBN";
                                                flag2 = qrySegmentFlag(branchCode);//用来判断存放位置
                                                if (flag2 != null && !flag2.equals("")) {
                                                    if (flag2.equals("s01")) {
                                                        asv.setS01(branchCode);
                                                    } else if (flag2.equals("s02")) {
                                                        asv.setS02(branchCode);
                                                    } else if (flag2.equals("s03")) {
                                                        asv.setS03(branchCode);
                                                    } else if (flag2.equals("s04")) {
                                                        asv.setS04(branchCode);
                                                    } else if (flag2.equals("s05")) {
                                                        asv.setS05(branchCode);
                                                    } else if (flag2.equals("s06")) {
                                                        asv.setS06(branchCode);
                                                    } else if (flag2.equals("s07")) {
                                                        asv.setS07(branchCode);
                                                    } else if (flag2.equals("s08")) {
                                                        asv.setS08(branchCode);
                                                    } else if (flag2.equals("s09")) {
                                                        asv.setS09(branchCode);
                                                    } else if (flag2.equals("s10")) {
                                                        asv.setS10(branchCode);
                                                    } else if (flag2.equals("s11")) {
                                                        asv.setS11(branchCode);
                                                    } else if (flag2.equals("s12")) {
                                                        asv.setS12(branchCode);
                                                    } else if (flag2.equals("s13")) {
                                                        asv.setS13(branchCode);
                                                    } else if (flag2.equals("s14")) {
                                                        asv.setS14(branchCode);
                                                    } else if (flag2.equals("s15")) {
                                                        asv.setS15(branchCode);
                                                    } else if (flag2.equals("s16")) {
                                                        asv.setS16(branchCode);
                                                    } else if (flag2.equals("s17")) {
                                                        asv.setS17(branchCode);
                                                    } else if (flag2.equals("s18")) {
                                                        asv.setS18(branchCode);
                                                    } else if (flag2.equals("s19")) {
                                                        asv.setS19(branchCode);
                                                    } else if (flag2.equals("s20")) {
                                                        asv.setS20(branchCode);
                                                    }
                                                }
                                                String directionOther=((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2();
                                                // 17/16 科目信息下没special_id 没有对部门编号的处理。所以不需要
//                                                String directionOther="YS20002001";
                                                flag2 = qrySegmentFlag(directionOther);//用来判断存放位置
                                                setValue1(flag2,asv,aactList);
                                                if(directionOther==null||directionOther.equals("")){
                                                    //科目有专项 类别无专项 出错
                                                    throw new Exception("false");
                                                }
                                                if(branchCode==null||branchCode.equals("")){
                                                    //卡片使用部门专项为空，科目不为null
                                                    throw new Exception("unitfalse");
                                                }
                                                String directionOtherValue=branchCode+","+directionOther;
                                                asv.setDirectionOther(directionOtherValue);
                                            };
                                        }
                                        if (specialIds[1].equals(key)&&value.equals("BM")){
                                            StringBuilder sql7 = new StringBuilder();
                                            sql7.append("SELECT (SELECT s.special_code FROM specialinfo s WHERE s.id=a.unit_code) AS specialCode FROM IntangibleAccAssetInfo a  WHERE 1=1 ");
                                            paramsNo = 1;
                                            params.clear();

                                            sql7.append("  and a.center_code = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                                            paramsNo++;
                                            sql7.append("  and a.asset_code = ?"+paramsNo);
                                            params.put(paramsNo,assetCode);
                                            paramsNo++;
                                            sql7.append("  and a.acc_book_code = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                                            paramsNo++;
                                            sql7.append("  and a.acc_book_type = ?"+paramsNo);
                                            params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
                                            paramsNo++;
                                            List<?> branchList = accSubVoucherRespository.queryBySql(sql7.toString(),params);
                                            for (Object code : branchList) {
                                                String branchCode = (String) code;
//                                                String branchCode = "ZJDBN";
                                                flag2 = qrySegmentFlag(branchCode);//用来判断存放位置
                                                if (flag2 != null && !flag2.equals("")) {
                                                    if (flag2.equals("s01")) {
                                                        asv.setS01(branchCode);
                                                    } else if (flag2.equals("s02")) {
                                                        asv.setS02(branchCode);
                                                    } else if (flag2.equals("s03")) {
                                                        asv.setS03(branchCode);
                                                    } else if (flag2.equals("s04")) {
                                                        asv.setS04(branchCode);
                                                    } else if (flag2.equals("s05")) {
                                                        asv.setS05(branchCode);
                                                    } else if (flag2.equals("s06")) {
                                                        asv.setS06(branchCode);
                                                    } else if (flag2.equals("s07")) {
                                                        asv.setS07(branchCode);
                                                    } else if (flag2.equals("s08")) {
                                                        asv.setS08(branchCode);
                                                    } else if (flag2.equals("s09")) {
                                                        asv.setS09(branchCode);
                                                    } else if (flag2.equals("s10")) {
                                                        asv.setS10(branchCode);
                                                    } else if (flag2.equals("s11")) {
                                                        asv.setS11(branchCode);
                                                    } else if (flag2.equals("s12")) {
                                                        asv.setS12(branchCode);
                                                    } else if (flag2.equals("s13")) {
                                                        asv.setS13(branchCode);
                                                    } else if (flag2.equals("s14")) {
                                                        asv.setS14(branchCode);
                                                    } else if (flag2.equals("s15")) {
                                                        asv.setS15(branchCode);
                                                    } else if (flag2.equals("s16")) {
                                                        asv.setS16(branchCode);
                                                    } else if (flag2.equals("s17")) {
                                                        asv.setS17(branchCode);
                                                    } else if (flag2.equals("s18")) {
                                                        asv.setS18(branchCode);
                                                    } else if (flag2.equals("s19")) {
                                                        asv.setS19(branchCode);
                                                    } else if (flag2.equals("s20")) {
                                                        asv.setS20(branchCode);
                                                    }
                                                }
                                                String directionOther=((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2();
                                                flag2 = qrySegmentFlag(directionOther);//用来判断存放位置
                                                setValue1(flag2,asv,aactList);
                                                if(directionOther==null||directionOther.equals("")){
                                                    //科目有专项 类别无专项 出错
                                                    throw new Exception("false");
                                                }
                                                if(branchCode==null||branchCode.equals("")){
                                                    //卡片使用部门专项为空，科目不为null
                                                    throw new Exception("unitfalse");
                                                }
                                                String directionOtherValue=directionOther+","+branchCode;
                                                asv.setDirectionOther(directionOtherValue);
                                            };
                                        }
                                    }
                                }
                            }

                            String directionidx2 = ((IntangibleAccAssetCodeType) aactList.get(0)).getItemCode2();
                            if (directionidx2.charAt(directionidx2.length() - 1) != '/') {
                                directionidx2 = directionidx2 + "/";
                            }
                            asv.setDirectionIdx(directionidx2);//科目方向段

                            //根据科目代码去获取科目名称
                            StringBuffer directionIdxName2 = new StringBuffer();
                            String itemCodeStr1 = "";
                            for (int j = 0; j < itemCodeArr2.length; j++) {
                                StringBuffer nameSql = new StringBuffer();
                                itemCodeStr1 = itemCodeStr1 + itemCodeArr2[j] + "/";
                                nameSql.append("select * from subjectinfo where  1=1 ");
                                paramsNo = 1;
                                params.clear();

                                nameSql.append("  and CONCAT(all_subject,subject_code,'/') = ?"+paramsNo);
                                params.put(paramsNo,itemCodeStr1);
                                paramsNo++;
                                nameSql.append("  and account = ?"+paramsNo);
                                params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                                paramsNo++;

                                List<?> nameList = accAssetCodeTypeRepository.queryBySql(nameSql.toString(),params, SubjectInfo.class);
                                if (j == 0) {//第一次追加科目名称直接追加，非第一次追加在前面加"/"
                                    directionIdxName2.append(((SubjectInfo) nameList.get(0)).getSubjectName());
                                } else {
                                    directionIdxName2.append("/" + ((SubjectInfo) nameList.get(0)).getSubjectName());

                                }
                            }
                            String directionidxNames2 = directionIdxName2.toString();
                            if (directionidxNames2.charAt(directionidxNames2.length() - 1) != '/') {
                                directionidxNames2 = directionidxNames2 + "/";
                            }
                            asv.setDirectionIdxName(directionidxNames2);//科目方向段名称

//                            asv.setDirectionOther(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());//专项方向段
                            //判断科目专项是否相同，合并金额
                            StringBuffer sqls5 = new StringBuffer();
                            sqls5.append("select * from accsubvoucher where 1=1 ");
                            paramsNo = 1;
                            params.clear();

                            sqls5.append("  and acc_book_code = ?"+paramsNo);
                            params.put(paramsNo,accBookCode);
                            paramsNo++;
                            sqls5.append("  and voucher_no = ?"+paramsNo);
                            params.put(paramsNo,maxVoucherNo);
                            paramsNo++;
                            sqls5.append("  and direction_idx = ?"+paramsNo);
                            params.put(paramsNo,asv.getDirectionIdx());
                            paramsNo++;
                            sqls5.append("  and direction_other = ?"+paramsNo);
                            params.put(paramsNo,asv.getDirectionOther());
                            paramsNo++;
                            List<AccSubVoucher> accsubList2 = (List<AccSubVoucher>) accSubVoucherRespository.queryBySql(sqls5.toString(),params, AccSubVoucher.class);
                            if (accsubList2 != null && accsubList2.size() > 0) {
                                AccSubVoucher accsub = accsubList2.get(0);
                                accsub.setDebitSource(new BigDecimal(0));//原币借方金额
                                accsub.setCreditSource(accsub.getCreditSource().add(monthDepreMoneyss));//原币贷方金额 无形资产计提折旧合计
                                accsub.setDebitDest(new BigDecimal(0));//本位币借方金额
                                accsub.setCreditDest(accsub.getCreditDest().add(monthDepreMoneyss));//本位币贷方金额 无形资产计提折旧合计
                                accSubVoucherRespository.save(accsub);//凭证子表信息录入
                                break;
                            }
                            asv.setRemark("提取" + amvId.getYearMonthDate().substring(0, 4) + "年" + amvId.getYearMonthDate().substring(4, 6) + "月无形资产摊销");//摘要 默认置空
                            asv.setCurrency(currency);//原币别编码
                            asv.setExchangeRate(new BigDecimal(0));//当前汇率 默认0
                            asv.setDebitSource(new BigDecimal(0));//原币借方金额
                            asv.setCreditSource(monthDepreMoneyss);//原币贷方金额 无形资产计提折旧合计
                            asv.setDebitDest(new BigDecimal(0));//本位币借方金额
                            asv.setCreditDest(monthDepreMoneyss);//本位币贷方金额 无形资产计提折旧合计
                            asv.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));//制单人
                            asv.setCreateTime(time);//创建时间
                            accSubVoucherRespository.save(asv);//凭证子表信息录入
                            break;
                    }
                }


//-------------------------凭证子表录入结束------------------------------

                //无形资产折旧记录表 凭证号添加
                IntangibleAccDepreId adid = new IntangibleAccDepreId();
                adid.setCenterCode(centerCode);//核算单位
                adid.setBranchCode(CurrentUser.getCurrentLoginManageBranch());//基层单位
                adid.setAccBookType(CurrentUser.getCurrentLoginAccountType());//账套类型
                adid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
                adid.setCodeType("31");//管理类别编码
                adid.setAssetType(accAssetInfo.getAssetType());//无形资产类别编码
                adid.setYearMonthData(dto.getYearMonthDate());//折旧年月
                adid.setAssetCode(assetCode);//无形资产编号
                IntangibleAccDepre ad = intangibleAccDepreRepository.findById(adid).get();//根据主键查询当前无形资产折旧记录信息
                ad.setVoucherNo(maxVoucherNo);//设置凭证号
                intangibleAccDepreRepository.save(ad);//保存

                }
            }

                AccWCheckInfoId agid = new AccWCheckInfoId();//无形资产会计期间信息获取
                agid.setCenterCode(dto.getCenterCode());//核算单位
                agid.setYearMonthDate(dto.getYearMonthDate());//凭证年月;
                agid.setAccBookType(dto.getAccBookType());//账套类型
                agid.setAccBookCode(dto.getAccBookCode());//账套编码
                AccWCheckInfo ag = accWCheckInfoRepository.findById(agid).get();//通过ID查找
                ag.setFlag("3");//无形资产折旧状态 已生成凭证
                ag.setCreateBy2(String.valueOf(CurrentUser.getCurrentUser().getId()));//无形资产折旧-生成凭证操作人
                ag.setCreateTime2(CurrentTime.getCurrentTime());//无形资产折旧-生成凭证时间
                ag.setCreateBy4(null);//无形资产折旧-凭证回退操作人
                ag.setCreateTime4(null);//无形资产折旧-凭证回退时间
                accWCheckInfoRepository.save(ag);//保存
            if(test1>0){
                //保存成功更新最大会计期间
                accVoucherNoRespository.updateAddVoucherNo(dto.getYearMonthDate(), CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount(),CurrentUser.getCurrentLoginManageBranch());
            }

            return InvokeResult.success();

    }

    /**
     * 无形资产摊销管理 凭证删除
     *
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult revokeVoucher(AccWCheckInfoDTO dto) {

            //判断当前会计期间是否结转，如为结转则不允许进行凭证生成操作
            StringBuffer sql1 = new StringBuffer();
            sql1.append("select * from accmonthtrace where 1=1 ");//已结转
            int paramsNo = 1;
            Map<Integer,Object> params = new HashMap<>();

            sql1.append("  and center_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql1.append("  and acc_book_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql1.append("  and year_month_date = ?"+paramsNo);
            params.put(paramsNo,dto.getYearMonthDate());
            paramsNo++;
            sql1.append("  and acc_month_stat > ?"+paramsNo);
            params.put(paramsNo,2);
            paramsNo++;
            List<?> list1 = accWCheckInfoRepository.queryBySqlSC(sql1.toString(),params);
            if(list1.size()>0){
                return InvokeResult.failure("当前会计期间已结算，不可以进行凭证删除！");
            }

            //判断下个会计期间状态是否为 已生成凭证
            String nextYearMonthDate;//下个会计期间 201901
            if(dto.getYearMonthDate().equals("12")){//如果为12月，则下个会计期间为下年1月
                nextYearMonthDate = Integer.parseInt(dto.getYearMonthDate().substring(0,4))+1+"01";
            }else{//如果不为12月，则下个会计期间直接+1
                nextYearMonthDate = String.valueOf(Integer.parseInt(dto.getYearMonthDate())+1);
            }
            StringBuffer nextSql = new StringBuffer();
            nextSql.append("select * from accwcheckinfo where 1=1");
            paramsNo = 1;
            params.clear();

            nextSql.append("  and center_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            nextSql.append("  and acc_book_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            nextSql.append("  and year_month_date = ?"+paramsNo);
            params.put(paramsNo,nextYearMonthDate);
            paramsNo++;

            List<?> nextList = accWCheckInfoRepository.queryBySql(nextSql.toString(),params, AccWCheckInfo.class);
            if(nextList.size()!=0){//size不等于0，说明有下个会计期间
                if(((AccWCheckInfo)nextList.get(0)).getFlag().equals("3")){//不为未生成凭证状态
                    return InvokeResult.failure("下个会计期间未进行删除凭证处理，不允许当前会计期间进行凭证删除处理操作！");
                }
            }


            //根据凭证号对凭证主表、子表进行回退
            StringBuffer sql = new StringBuffer();
            sql.append("select * from IntangibleAccDepre where 1=1 ");
            paramsNo = 1;
            params.clear();

            sql.append("  and center_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
            paramsNo++;
            sql.append("  and acc_book_code = ?"+paramsNo);
            params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
            paramsNo++;
            sql.append("  and year_month_data = ?"+paramsNo);
            params.put(paramsNo,nextYearMonthDate);
            paramsNo++;
            List<?> adList = intangibleAccDepreRepository.queryBySql(sql.toString(),params, IntangibleAccDepre.class);
            if(adList.size() > 0){
                String voucherNo = ((IntangibleAccDepre)adList.get(0)).getVoucherNo();//凭证号获取

                StringBuffer sqls = new StringBuffer();
                sqls.append("select * from accmainvoucher  where 1=1");
                paramsNo = 1;
                params.clear();

                sqls.append("  and center_code = ?"+paramsNo);
                params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                paramsNo++;
                sqls.append("  and acc_book_code = ?"+paramsNo);
                params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                paramsNo++;
                sqls.append("  and voucher_no = ?"+paramsNo);
                params.put(paramsNo,voucherNo);
                List<?> list = accMainVoucherRespository.queryBySql(sqls.toString(),params, AccMainVoucher.class);
                if(!((AccMainVoucher)list.get(0)).getVoucherFlag().equals("1")) {//非未复核状态的凭证，需要记账回退，撤销复核，才可以执行生帐回退操作
                    return InvokeResult.failure("凭证已复核或记账，无法进行删除操作");

                }

                //当12月反折旧凭证、反摊销凭证时需要判断是否已生成决算凭证
                if ("12".equals(dto.getYearMonthDate().substring(4))) {
                    sql.setLength(0);
                    sql.append("select * from accmainvoucher  where 1=1 ");
                    paramsNo = 1;
                    params.clear();

                    sql.append("  and center_code = ?"+paramsNo);
                    params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                    paramsNo++;
                    sql.append("  and branch_code = ?"+paramsNo);
                    params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
                    paramsNo++;
                    sql.append("  and acc_book_type = ?"+paramsNo);
                    params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
                    paramsNo++;
                    sql.append("  and acc_book_code = ?"+paramsNo);
                    params.put(paramsNo,dto.getYearMonthDate());
                    paramsNo++;
                    sql.append("  and year_month_date = ?"+paramsNo);
                    params.put(paramsNo,voucherNo);
                    paramsNo++;
                    sql.append("  and generate_way = ?"+paramsNo);
                    params.put(paramsNo,"1");
                    paramsNo++;
                    sql.append("  and voucher_type = ?"+paramsNo);
                    params.put(paramsNo,"1");
                    paramsNo++;
                    List<?>  accMainVoucherList= accMainVoucherRespository.queryBySql(sql.toString(),params, AccMainVoucher.class);
                    if (accMainVoucherList!=null&&accMainVoucherList.size()>0) {
                        return InvokeResult.failure("当前会计期间已生成决算凭证，请回退决算凭证后再操作！");
                    }
                }
                //凭证主表信息删除
//                accMainVoucherRespository.delById(voucherNo);
                //凭证子表信息删除
//                accSubVoucherRespository.delById(voucherNo);
                intangibleAccDepreRepository.clearVoucherNo(voucherNo, CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount(),CurrentUser.getCurrentLoginManageBranch());
                //凭证删除
                voucherManageService.deleteVoucher(voucherNo, dto.getYearMonthDate());

//                //无形资产折旧记录表 卡片凭证号清空
//                accDepreRepository.clearVoucherNo(voucherNo, CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount());
//                //无形资产折旧记录表 卡片凭证号清空
//                intangibleAccDepreRepository.clearVoucherNo(voucherNo, CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount());
//
//                //无形资产基本信息表 卡片凭证号清空
//                intangibleAccAssetInfoRepository.clearVoucherNo(voucherNo, CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount());
//                //无形资产基本信息表 卡片凭证号清空
//                accAssetInfoRepository.clearVoucherNo(voucherNo, CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount());

                //最大凭证号回退
//                accVoucherNoRespository.updateSubVoucherNo(dto.getYearMonthDate(), CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount());
            }

            AccWCheckInfoId awid = new AccWCheckInfoId();//无形资产会计期间信息获取
            awid.setCenterCode(dto.getCenterCode());//核算单位
            awid.setYearMonthDate(dto.getYearMonthDate());//凭证年月;
            awid.setAccBookType(dto.getAccBookType());//账套类型
            awid.setAccBookCode(dto.getAccBookCode());//账套编码
            AccWCheckInfo aw = accWCheckInfoRepository.findById(awid).get();//通过ID查找
            if(aw.getFlag().equals("3")){
                aw.setFlag("1");//无形资产折旧状态 已生成凭证
                aw.setCreateBy2(null);//固定资产折旧-生成凭证操作人
                aw.setCreateTime2(null);//固定资产折旧-生成凭证时间
                aw.setCreateBy4(String.valueOf(CurrentUser.getCurrentUser().getId()));//固定资产折旧-凭证回退操作人
                aw.setCreateTime4(CurrentTime.getCurrentTime());//固定资产折旧-凭证回退时间
                accWCheckInfoRepository.save(aw);//保存
            }

//            AccGCheckInfoId agid = new AccGCheckInfoId();//固定资产会计期间信息获取
//            agid.setCenterCode(dto.getCenterCode());//核算单位
//            agid.setYearMonthDate(dto.getYearMonthDate());//凭证年月;
//            agid.setAccBookType(dto.getAccBookType());//账套类型
//            agid.setAccBookCode(dto.getAccBookCode());//账套编码
//            AccGCheckInfo ag = accGCheckInfoRepository.findById(agid).get();//通过ID查找
//            if(ag.getFlag().equals("3")){
//                ag.setFlag("1");//固定资产折旧状态 已生成凭证
//                ag.setCreateBy2(null);//固定资产折旧-生成凭证操作人
//                ag.setCreateTime2(null);//固定资产折旧-生成凭证时间
//                ag.setCreateBy4(String.valueOf(CurrentUser.getCurrentUser().getId()));//固定资产折旧-凭证回退操作人
//                ag.setCreateTime4(CurrentTime.getCurrentTime());//固定资产折旧-凭证回退时间
//                accGCheckInfoRepository.save(ag);//保存
//            }

            return InvokeResult.success();

    }

    /**
     * 日期相加减
     * @param time
     *             时间字符串 yyyy-MM-dd
     * @param num
     *             加的数，-num就是减去
     * @return
     *             减去相应的数量的月份的日期
     */
    public static String yearAddNum(String time, Integer num) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
        try {
            Date date = df.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, num);
            Date newTime = calendar.getTime();
            return df.format(newTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //根据专项查询专项存放位置
    public String qrySegmentFlag(String specialCode) {
        String flag = null;
        StringBuffer siSql = new StringBuffer();
        siSql.append("select * from specialinfo where 1=1");
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();

        siSql.append("  and special_code = ?"+paramsNo);
        params.put(paramsNo,specialCode);
        paramsNo++;
        siSql.append("  and account = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        List<?> aiList = accAssetCodeTypeRepository.queryBySqlSC(siSql.toString(),params);
        if (aiList.size() > 0) {
            Map siMap = new HashMap();
            for (Object siObj : aiList) {
                siMap.putAll((Map) siObj);
                break;
            }
            if (siMap.get("super_special") == null && siMap.get("endflag").equals("1") ||
                    siMap.get("super_special").equals("") && siMap.get("endflag").equals("1")) {
                specialCode = siMap.get("special_code").toString();
                StringBuffer agSql = new StringBuffer();
                agSql.append("select * from accsegmentdefine where 1=1 ");
                paramsNo = 1;
                params.clear();

                agSql.append("  and segment_col = ?"+paramsNo);
                params.put(paramsNo,specialCode);
                List<?> agList = accAssetCodeTypeRepository.queryBySqlSC(agSql.toString(),params);
                if (agList.size() > 0) {
                    Map agMap = new HashMap();
                    for (Object agObj : agList) {
                        agMap.putAll((Map) agObj);
                        flag = agMap.get("segment_flag").toString();
                        break;
                    }
                }
            } else {
                specialCode = siMap.get("super_special").toString();
                for (; ; ) {
                    StringBuffer siSql2 = new StringBuffer();
                    siSql2.append("select * from specialinfo where 1=1 ");
                    paramsNo = 1;
                    params.clear();

                    siSql2.append("  and id = ?"+paramsNo);
                    params.put(paramsNo,specialCode);
                    paramsNo++;
                    siSql2.append("  and account = ?"+paramsNo);
                    params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
                    List<?> aiList2 = accAssetCodeTypeRepository.queryBySqlSC(siSql2.toString(),params);
                    if (aiList2.size() > 0) {
                        Map siMap2 = new HashMap();
                        for (Object siObj2 : aiList2) {
                            siMap2.putAll((Map) siObj2);
                            break;
                        }
                        if (siMap2.get("super_special") == null && siMap2.get("endflag").equals("1") ||
                                siMap2.get("super_special").equals("") && siMap2.get("endflag").equals("1")) {
                            specialCode = siMap2.get("special_code").toString();
                            StringBuffer agSql2 = new StringBuffer();
                            agSql2.append("select * from accsegmentdefine where 1=1 ");
                            paramsNo = 1;
                            params.clear();

                            agSql2.append("  and segment_col = ?"+paramsNo);
                            params.put(paramsNo,specialCode);
                            List<?> agList2 = accAssetCodeTypeRepository.queryBySqlSC(agSql2.toString(),params);
                            if (agList2.size() > 0) {
                                Map agMap2 = new HashMap();
                                for (Object agObj2 : agList2) {
                                    agMap2.putAll((Map) agObj2);
                                    flag = agMap2.get("segment_flag").toString();
                                    break;
                                }
                            }
                            break;
                        } else {
                            specialCode = siMap2.get("super_special").toString();
                        }
                    }

                }
            }
        }
        return flag;
    }

    public void setValue(String flag, AccSubVoucher asv, List<?> aactList) {

        if (flag != null && !flag.equals("")) {
            if (flag.equals("s01")) {
                asv.setS01(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
            } else if (flag.equals("s02")) {
                asv.setS02(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
            } else if (flag.equals("s03")) {
                asv.setS03(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
            } else if (flag.equals("s04")) {
                asv.setS04(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
            } else if (flag.equals("s05")) {
                asv.setS05(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
            } else if (flag.equals("s06")) {
                asv.setS06(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
            } else if (flag.equals("s07")) {
                asv.setS07(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
            } else
//           if(flag.equals("s07")){asv.setS07("ZJDBN");}else
                if (flag.equals("s08")) {
                    asv.setS08(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s09")) {
                    asv.setS09(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s10")) {
                    asv.setS10(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s11")) {
                    asv.setS11(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s12")) {
                    asv.setS12(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s13")) {
                    asv.setS13(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s14")) {
                    asv.setS14(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s15")) {
                    asv.setS15(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s16")) {
                    asv.setS16(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s17")) {
                    asv.setS17(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s18")) {
                    asv.setS18(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s19")) {
                    asv.setS19(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                } else if (flag.equals("s20")) {
                    asv.setS20(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode3());
                }
        }
        String directionidx = ((IntangibleAccAssetCodeType) aactList.get(0)).getItemCode3();
        if (directionidx.charAt(directionidx.length() - 1) != '/') {
            directionidx = directionidx + "/";
        }
        asv.setDirectionIdx(directionidx);//科目方向段
    }

    public void setValue(String flag,AccSubVoucher asv,List<?> aactList,String articleCode){

        if(flag!=null && !flag.equals("")){
            if(flag.equals("s01")){asv.setS01(articleCode);}else
            if(flag.equals("s02")){asv.setS02(articleCode);}else
            if(flag.equals("s03")){asv.setS03(articleCode);}else
            if(flag.equals("s04")){asv.setS04(articleCode);}else
            if(flag.equals("s05")){asv.setS05(articleCode);}else
            if(flag.equals("s06")){asv.setS06(articleCode);}else
            if(flag.equals("s07")){asv.setS07(articleCode);}else
//           if(flag.equals("s07")){asv.setS07("ZJDBN");}else
                if(flag.equals("s08")){asv.setS08(articleCode);}else
                if(flag.equals("s09")){asv.setS09(articleCode);}else
                if(flag.equals("s10")){asv.setS10(articleCode);}else
                if(flag.equals("s11")){asv.setS11(articleCode);}else
                if(flag.equals("s12")){asv.setS12(articleCode);}else
                if(flag.equals("s13")){asv.setS13(articleCode);}else
                if(flag.equals("s14")){asv.setS14(articleCode);}else
                if(flag.equals("s15")){asv.setS15(articleCode);}else
                if(flag.equals("s16")){asv.setS16(articleCode);}else
                if(flag.equals("s17")){asv.setS17(articleCode);}else
                if(flag.equals("s18")){asv.setS18(articleCode);}else
                if(flag.equals("s19")){asv.setS19(articleCode);}else
                if(flag.equals("s20")){asv.setS20(articleCode);}
        }
        String directionidx=((IntangibleAccAssetCodeType)aactList.get(0)).getItemCode3();
        if(directionidx.charAt(directionidx.length()-1)!='/'){directionidx=directionidx+"/";}
        asv.setDirectionIdx(directionidx);//科目方向段
    };

    public void setValue1(String flag2, AccSubVoucher asv, List<?> aactList) {

        if (flag2 != null && !flag2.equals("")) {
            if (flag2.equals("s01")) {
                asv.setS01(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
            } else if (flag2.equals("s02")) {
                asv.setS02(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
            } else if (flag2.equals("s03")) {
                asv.setS03(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
            } else if (flag2.equals("s04")) {
                asv.setS04(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
            } else if (flag2.equals("s05")) {
                asv.setS05(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
            } else if (flag2.equals("s06")) {
                asv.setS06(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
            } else if (flag2.equals("s07")) {
                asv.setS07(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
            } else
//            if(flag2.equals("s07")){asv.setS07("ZJDBN");}else
                if (flag2.equals("s08")) {
                    asv.setS08(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s09")) {
                    asv.setS09(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s10")) {
                    asv.setS10(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s11")) {
                    asv.setS11(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s12")) {
                    asv.setS12(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s13")) {
                    asv.setS13(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s14")) {
                    asv.setS14(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s15")) {
                    asv.setS15(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s16")) {
                    asv.setS16(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s17")) {
                    asv.setS17(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s18")) {
                    asv.setS18(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s19")) {
                    asv.setS19(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                } else if (flag2.equals("s20")) {
                    asv.setS20(((IntangibleAccAssetCodeType) aactList.get(0)).getArticleCode2());
                }
        }
        String directionidx = ((IntangibleAccAssetCodeType) aactList.get(0)).getItemCode2();
        if (directionidx.charAt(directionidx.length() - 1) != '/') {
            directionidx = directionidx + "/";
        }
        asv.setDirectionIdx(directionidx);//科目方向段
    }
}
