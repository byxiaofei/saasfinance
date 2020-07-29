package com.sinosoft.service.impl.intangibleassets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.SpecialInfo;
import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.domain.fixedassets.AccAssetCodeType;
import com.sinosoft.domain.fixedassets.AccAssetCodeTypeId;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetCodeType;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetCodeTypeId;
import com.sinosoft.dto.fixedassets.AccAssetCodeTypeDTO;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetCodeTypeDTO;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetCodeTypeRepository;
import com.sinosoft.service.fixedassets.CategoryCodingService;
import com.sinosoft.service.intangibleassets.IntangibleAssetsService;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 17:14
 */
@Service
public class IntangibleAssetsServiceImpl implements IntangibleAssetsService {
    private Logger logger = LoggerFactory.getLogger(IntangibleAssetsServiceImpl.class);
    @Resource
    private IntangibleAccAssetCodeTypeRepository intangibleAccAssetCodeTypeRepository;
    @Resource
    private CategoryCodingService categoryCodingService;
    @Resource
    private SubjectRepository subjectRepository;
    /**
     * 查询无形资产类别编码信息
     * @param dto
     * @return
     */
    @Override
    public Page<?> qryIntangibleAccAssetCodeTypeList(int page,int rows,IntangibleAccAssetCodeTypeDTO dto) {

        Page<?> result = categoryCodingService.getPage(page,rows,getAssetsList(dto));
        return result;
    }
    @Override
    public List<?> getAssetsList(IntangibleAccAssetCodeTypeDTO dto){
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql = new StringBuffer();
        sql.append("select '31' as ct, a.asset_type as assetType, a.asset_simple_name as assetSimpleName, \n" +
                "a.asset_complex_name as assetComplexName, a.level as level, a.dep_years as depYears,   \n" +
                "(select c.code_name from codemanage c where c.code_type = 'endflag' and c.code_code = a.end_flag) as endFlag, \n" +
                "(select c.code_name from codemanage c where c.code_type = 'deprMethod' and c.code_code = a.dep_type) as depType, \n" +
                " a.acc_book_type as accBookType, \n" +
                "a.acc_book_code as accBookCode, a.code_type as codeType, a.super_code as superCode, \n" +
                "(select c.code_name from codemanage c where c.code_type = 'useFlag' and c.code_code = a.use_flag) as useFlag,  \n" +
                "a.item_code1 as itemCode1, (select special_name from specialinfo where special_code = a.article_code1 and account=a.acc_book_code) as articleCode11, " +
                "a.article_code1 as articleCode1, a.item_code2 as itemCode2, a.article_code2 as articleCode2, \n" +
                "a.item_code3 as itemCode3, a.article_code3 as articleCode3, a.item_code4 as itemCode4, a.article_code4 as articleCode4, \n" +
                "a.item_code5 as itemCode5, a.article_code5 as articleCode5, a.item_code6 as itemCode6, a.article_code6 as articleCode6, \n" +
                "a.create_oper as createOper, a.create_time as createTime, a.update_oper as updateOper, a.update_time as updateTime, \n"+
                "(select count(f.card_code) from intangibleaccassetinfo f where f.acc_book_code=a.acc_book_code and f.asset_type=a.asset_type) as counts, "+
                "(SELECT COUNT(i.asset_type) FROM intangibleaccassetcodetype i WHERE i.super_code = a.asset_type) AS hasNext "+
                "from intangibleaccassetcodetype a where 1=1  ");
        if(dto.getAccBookCode() != null && !dto.getAccBookCode().equals("")){
//            sql.append(" and a.acc_book_code = '"+ dto.getAccBookCode() +"' ");
            sql.append(" and a.acc_book_code = ?"+ paramsNo);
            params.put(paramsNo,dto.getAccBookCode());
            paramsNo++;
        }
        if(dto.getCodeType() != null && !dto.getCodeType().equals("")){
//            sql.append(" and a.code_type = '"+ dto.getCodeType() +"' ");
            sql.append(" and a.code_type = ?"+ paramsNo);
            params.put(paramsNo,dto.getCodeType());
            paramsNo++;
        }
        if(dto.getAssetType() != null && !dto.getAssetType().equals("")){
//            sql.append(" and a.asset_type like '%"+ dto.getAssetType() +"%' ");
            sql.append(" and a.asset_type like ?"+ paramsNo );
            params.put(paramsNo,"%"+dto.getAssetType()+"%");
            paramsNo++;
        }
        if(dto.getAssetSimpleName() != null && !dto.getAssetSimpleName().equals("")){
//            sql.append(" and a.asset_simple_name like '%"+ dto.getAssetSimpleName() +"%' ");
            sql.append(" and a.asset_simple_name like ?"+ paramsNo);
            params.put(paramsNo,"%"+dto.getAssetSimpleName()+"%");
            paramsNo++;
        }
        if(dto.getAssetComplexName() != null && !dto.getAssetComplexName().equals("")){
//            sql.append(" and a.asset_complex_name like '%"+ dto.getAssetComplexName() +"%' ");
            sql.append(" and a.asset_complex_name like ?"+ paramsNo );
            params.put(paramsNo,dto.getAssetComplexName());
            paramsNo++;
        }
        if(dto.getLevel() != null && !dto.getLevel().equals("")){
            sql.append(" and a.level = ?"+ paramsNo );
            params.put(paramsNo,dto.getLevel());
            paramsNo++;
        }
        if(dto.getUseFlag() != null && !dto.getUseFlag().equals("")){
            sql.append(" and a.use_flag = ?"+ paramsNo );
            params.put(paramsNo,dto.getUseFlag());
            paramsNo++;
        }
        return  intangibleAccAssetCodeTypeRepository.queryBySqlSC(sql.toString(),params);
    }
    /**
     * 修改无形资产编码
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult update(IntangibleAccAssetCodeTypeDTO dto) {
            String centerCode = CurrentUser.getCurrentLoginManageBranch();
            String branchCode = CurrentUser.getCurrentLoginManageBranch();
//            //判断当前类别编码是否存在
//            StringBuffer assetTypeSql = new StringBuffer();
//            assetTypeSql.append("select * from IntangibleAccAssetCodeType where asset_type = "+ dto.getAssetType() +"");
//            List<?> assetTypeList = intangibleAccAssetCodeTypeRepository.queryBySqlSC(assetTypeSql.toString());
//            if(assetTypeList.size() > 0){//当前类别编码已存在
//                return InvokeResult.failure("当前类别编码已存在！");
//            }

            //将前台获取到的值通过实体类存入数据库中
            IntangibleAccAssetCodeTypeId iaactid = new IntangibleAccAssetCodeTypeId();
            String accBookType = CurrentUser.getCurrentLoginAccountType();
            iaactid.setAccBookType(accBookType);                                                 //账套类型
            String accBookCode = CurrentUser.getCurrentLoginAccount();
            iaactid.setAccBookCode(accBookCode);                                                 //账套编码
//            String codeType = dto.getCodeType() == null ? null : dto.getCodeType();
            iaactid.setCodeType("31");                                                           //管理类别编码
            String assetType = dto.getAssetType() == null ? null : dto.getAssetType();
            iaactid.setAssetType(assetType);                                                     //无形资产编号
            IntangibleAccAssetCodeType iaact = new IntangibleAccAssetCodeType();
            iaact.setId(iaactid);
            iaact.setAssetSimpleName(dto.getAssetSimpleName() == null ? null : dto.getAssetSimpleName());    //无形资产类别简称
            iaact.setAssetComplexName(dto.getAssetComplexName() == null ? null : dto.getAssetComplexName()); //无形资产类别全称
            iaact.setLevel(dto.getLevel() == null ? null : dto.getLevel());                                  //无形资产类别层级

            //末级标志处理
            String endFlag = null;
            if(dto.getEndFlag().equals("末级")){
                endFlag = "0";
            }else if(dto.getEndFlag().equals("非末级")){
                endFlag = "1";
            }else{
                endFlag = dto.getEndFlag() == null? null : dto.getEndFlag();
            }
            iaact.setEndFlag(endFlag);                                                                       //末级标志
            iaact.setSuperCode(dto.getSuperCode() == null ? null : dto.getSuperCode());                      //父级编码

            if(dto.getEndFlag().equals("末级")||dto.getEndFlag().equals("0")){//末级 正常存储
                iaact.setItemCode1(dto.getItemCode1() == null ? null : dto.getItemCode1());                      //资产科目代码
                iaact.setArticleCode1(dto.getArticleCode1() == null ? null : dto.getArticleCode1());             //资产专项
                iaact.setItemCode2(dto.getItemCode2() == null ? null : dto.getItemCode2());                      //折旧贷方科目代码
                iaact.setArticleCode2(dto.getArticleCode2() == null ? null : dto.getArticleCode2());             //折旧贷方科目专项代码
                iaact.setItemCode3(dto.getItemCode3() == null ? null : dto.getItemCode3());                      //折旧借方科目代码
                iaact.setArticleCode3(dto.getArticleCode3() == null ? null : dto.getArticleCode3());             //折旧借方科目专项代码
                iaact.setItemCode4(dto.getItemCode4() == null ? null : dto.getItemCode4());                      //进项税科目代码
                iaact.setArticleCode4(dto.getArticleCode4() == null ? null : dto.getArticleCode4());             //进项税科目专项代码
                iaact.setItemCode5(dto.getItemCode5() == null ? null : dto.getItemCode5());                      //资产清理科目代码
                iaact.setArticleCode5(dto.getArticleCode5() == null ? null : dto.getArticleCode5());             //资产清理科目专项代码
                iaact.setItemCode6(dto.getItemCode6() == null ? null : dto.getItemCode6());                      //无形资产减值准备科目代码
                iaact.setArticleCode6(dto.getArticleCode6() == null ? null : dto.getArticleCode6());             //无形资产减值准备科目专项代码
                iaact.setDepYears(dto.getDepYears() == null ? null : dto.getDepYears());                         //摊销年限

                //摊销方法处理
                String depType = null;
                if(dto.getDepType().equals("平均年限法")) {
                    depType = "1";
                }else{
                    depType = dto.getDepType() == null ? null : dto.getDepType();
                }
                iaact.setDepType(depType);                                                                       //摊销方法

                                                                                     //使用状态
            }
            //使用状态处理
            String useFlag = null;
            if(dto.getUseFlag().equals("停用")){
                useFlag = "0";
            }else if(dto.getUseFlag().equals("使用")){
                useFlag = "1";
            }else{
                useFlag = dto.getUseFlag() == null ? null : dto.getUseFlag();
            }
            iaact.setUseFlag(useFlag);

            //判断是新增操作还是修改操作
//            StringBuffer sql = new StringBuffer();
//            sql.append("select * from intangibleaccassetcodetype where acc_book_type = '"+ accBookType +"' and acc_book_code = '"+ accBookCode +"' and code_type = '31' and asset_type = '"+ assetType +"'");
//            List<?> list = intangibleAccAssetCodeTypeRepository.queryBySql(sql.toString(),IntangibleAccAssetCodeType.class);
            List<IntangibleAccAssetCodeType> list = intangibleAccAssetCodeTypeRepository.queryIntangibleAccAssetCodeTypeByAccBookTypeAndAccBookCodeAndCodeTypeAndassetType(accBookType,accBookCode,assetType);
//            List<?> list = intangibleAccAssetCodeTypeRepository.qryById(centerCode, branchCode, accBookType, accBookCode, codeType, assetType);
            if(list.size()>0){//list.size()大于0，说明是修改操作，等于0，说明是添加操作
                iaact.setCreateOper(((IntangibleAccAssetCodeType)list.get(0)).getCreateOper());                            //录入人
                iaact.setCreateTime(((IntangibleAccAssetCodeType)list.get(0)).getCreateTime());                            //录入时间
                iaact.setUpdateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));                                  //修改人
                //修改时间格式化
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                iaact.setUpdateTime(df.format(new Date()));                                                      //修改时间
            }else{
                iaact.setCreateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));                       //录入人
                //录入时间格式化
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                iaact.setCreateTime(df.format(new Date()));                                                      //录入时间
                iaact.setUpdateOper(null);                                                                       //修改人
                iaact.setUpdateTime(null);                                                                       //修改时间
            }
            iaact.setTemp(dto.getTemp() == null ? null : dto.getTemp());                                         //备用
            intangibleAccAssetCodeTypeRepository.save(iaact);
            intangibleAccAssetCodeTypeRepository.flush();
            return InvokeResult.success();


    }

    /**
     * 新增无形资产编码
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult add(IntangibleAccAssetCodeTypeDTO dto) {
        //判断当前类别编码是否存在
//        StringBuffer assetTypeSql = new StringBuffer();
//        assetTypeSql.append("select * from IntangibleAccAssetCodeType where acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and asset_type = "+ dto.getAssetType() +"");
//        List<?> assetTypeList = intangibleAccAssetCodeTypeRepository.queryBySqlSC(assetTypeSql.toString());
        List<?> assetTypeList = intangibleAccAssetCodeTypeRepository.queryIntangibleAccAssetCodeTypeByAccBookCodeAndAssetType(CurrentUser.getCurrentLoginAccount(),dto.getAssetType());
        if(assetTypeList.size() > 0){//当前类别编码已存在
            return InvokeResult.failure("当前类别编码已存在！");
        }

            //将前台获取到的值通过实体类存入数据库中
            IntangibleAccAssetCodeTypeId iaactid = new IntangibleAccAssetCodeTypeId();         //基层单位
            String accBookType = CurrentUser.getCurrentLoginAccountType();
            iaactid.setAccBookType(accBookType);                                                 //账套类型
            String accBookCode = CurrentUser.getCurrentLoginAccount();
            iaactid.setAccBookCode(accBookCode);                                                 //账套编码
//            String codeType = dto.getCodeType() == null ? null : dto.getCodeType();
            iaactid.setCodeType("31");                                                           //管理类别编码
            String assetType = dto.getAssetType() == null ? null : dto.getAssetType();
            iaactid.setAssetType(assetType);                                                     //无形资产编号
            IntangibleAccAssetCodeType iaact = new IntangibleAccAssetCodeType();
            iaact.setId(iaactid);
            iaact.setAssetSimpleName(dto.getAssetSimpleName() == null ? null : dto.getAssetSimpleName());    //无形资产类别简称
            iaact.setAssetComplexName(dto.getAssetComplexName() == null ? null : dto.getAssetComplexName()); //无形资产类别全称
            iaact.setLevel(dto.getLevel() == null ? null : dto.getLevel());                                  //无形资产类别层级

            //末级标志处理
            String endFlag = null;
            if(dto.getEndFlag().equals("末级")){
                endFlag = "0";
            }else if(dto.getEndFlag().equals("非末级")){
                endFlag = "1";
            }else{
                endFlag = dto.getEndFlag() == null? null : dto.getEndFlag();
            }
            iaact.setEndFlag(endFlag);                                                                       //末级标志
            iaact.setSuperCode(dto.getSuperCode() == null ? null : dto.getSuperCode());                      //父级编码

            if(dto.getEndFlag().equals("0")){//末级 正常存储
                iaact.setItemCode1(dto.getItemCode1() == null ? null : dto.getItemCode1());                      //资产科目代码
                iaact.setArticleCode1(dto.getArticleCode1() == null ? null : dto.getArticleCode1());             //资产专项
                iaact.setItemCode2(dto.getItemCode2() == null ? null : dto.getItemCode2());                      //折旧贷方科目代码
                iaact.setArticleCode2(dto.getArticleCode2() == null ? null : dto.getArticleCode2());             //折旧贷方科目专项代码
                iaact.setItemCode3(dto.getItemCode3() == null ? null : dto.getItemCode3());                      //折旧借方科目代码
                iaact.setArticleCode3(dto.getArticleCode3() == null ? null : dto.getArticleCode3());             //折旧借方科目专项代码
                iaact.setItemCode4(dto.getItemCode4() == null ? null : dto.getItemCode4());                      //进项税科目代码
                iaact.setArticleCode4(dto.getArticleCode4() == null ? null : dto.getArticleCode4());             //进项税科目专项代码
                iaact.setItemCode5(dto.getItemCode5() == null ? null : dto.getItemCode5());                      //资产清理科目代码
                iaact.setArticleCode5(dto.getArticleCode5() == null ? null : dto.getArticleCode5());             //资产清理科目专项代码
                iaact.setItemCode6(dto.getItemCode6() == null ? null : dto.getItemCode6());                      //无形资产减值准备科目代码
                iaact.setArticleCode6(dto.getArticleCode6() == null ? null : dto.getArticleCode6());             //无形资产减值准备科目专项代码
                iaact.setDepYears(dto.getDepYears() == null ? null : dto.getDepYears());                         //摊销年限

                //摊销方法处理
                String depType = null;
                if(dto.getDepType().equals("平均年限法")) {
                    depType = "1";
                }else{
                    depType = dto.getDepType() == null ? null : dto.getDepType();
                }
                iaact.setDepType(depType);                                                                       //摊销方法

                //使用状态处理
                String useFlag = null;
                if(dto.getUseFlag().equals("停用")){
                    useFlag = "0";
                }else if(dto.getUseFlag().equals("使用")){
                    useFlag = "1";
                }else{
                    useFlag = dto.getUseFlag() == null ? "1" : dto.getUseFlag();
                }
                iaact.setUseFlag(useFlag);                                                                       //使用状态
            }else{
                iaact.setUseFlag("1");
            }


            //判断是新增操作还是修改操作
//            StringBuffer sql = new StringBuffer();
//            sql.append("select * from intangibleaccassetcodetype where  " +
//                    " acc_book_type = '"+ accBookType +"' and acc_book_code = '"+ accBookCode +"' and code_type = '31' and asset_type = '"+ assetType +"'");
//            List<?> list = intangibleAccAssetCodeTypeRepository.queryBySql(sql.toString(),IntangibleAccAssetCodeType.class);
//            List<?> list = intangibleAccAssetCodeTypeRepository.qryById(centerCode, branchCode, accBookType, accBookCode, codeType, assetType);
            List<?> list = intangibleAccAssetCodeTypeRepository.queryIntangibleAccAssetCodeTypeByAccBookTypeAndAccBookCodeAndCodeTypeAndassetType(accBookType,accBookCode,assetType);
            if(list.size()>0){//list.size()大于0，说明是修改操作，等于0，说明是添加操作
                iaact.setCreateOper(((IntangibleAccAssetCodeType)list.get(0)).getCreateOper());                            //录入人
                iaact.setCreateTime(((IntangibleAccAssetCodeType)list.get(0)).getCreateTime());                            //录入时间
                iaact.setUpdateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));                                  //修改人
                //修改时间格式化
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                iaact.setUpdateTime(df.format(new Date()));                                                      //修改时间
            }else{
                iaact.setCreateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));                       //录入人
                //录入时间格式化
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                iaact.setCreateTime(df.format(new Date()));                                                      //录入时间
                iaact.setUpdateOper(null);                                                                       //修改人
                iaact.setUpdateTime(null);                                                                       //修改时间
            }
            iaact.setTemp(dto.getTemp() == null ? null : dto.getTemp());                                         //备用
            intangibleAccAssetCodeTypeRepository.save(iaact);
            return InvokeResult.success();


    }

    /**
     * 新增下级无形资产类别编码
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult addLowerLevel(IntangibleAccAssetCodeTypeDTO dto) {
            //将前台获取到的值通过实体类存入数据库中
            String centerCode = CurrentUser.getCurrentLoginManageBranch();
            String branchCode = CurrentUser.getCurrentLoginManageBranch();
            IntangibleAccAssetCodeTypeId aactid = new IntangibleAccAssetCodeTypeId();             //基层单位
            String accBookType = CurrentUser.getCurrentLoginAccountType();
            aactid.setAccBookType(accBookType);                                                 //账套类型
            String accBookCode = CurrentUser.getCurrentLoginAccount();
            aactid.setAccBookCode(accBookCode);                                                 //账套编码
//            String codeType = dto.getCodeType() == null ? null : dto.getCodeType();
            aactid.setCodeType("31");                                                           //管理类别编码
            String assetType = dto.getAssetType() == null ? null : dto.getAssetType();
            //父级类别编码+当前类别编码
            aactid.setAssetType(dto.getSuperCode()+assetType);//无形资产编号
            IntangibleAccAssetCodeType aact = new IntangibleAccAssetCodeType();
            aact.setId(aactid);
            aact.setAssetSimpleName(dto.getAssetSimpleName() == null ? null : dto.getAssetSimpleName());    //无形资产类别简称
            aact.setAssetComplexName(dto.getAssetComplexName() == null ? null : dto.getAssetComplexName()); //无形资产类别全称
            aact.setLevel(dto.getLevel() == null ? null : dto.getLevel());                                  //无形资产类别层级

            //末级标志处理
            String endFlag = null;
            if(dto.getEndFlag().equals("末级")){
                endFlag = "0";
            }else if(dto.getEndFlag().equals("非末级")){
                endFlag = "1";
            }else{
                endFlag = dto.getEndFlag() == null? null : dto.getEndFlag();
            }
            aact.setEndFlag(endFlag);//末级标志
            aact.setSuperCode(dto.getSuperCode() == null ? null : dto.getSuperCode());                      //父级编码

            if(dto.getEndFlag().equals("末级")||dto.getEndFlag().equals("0")){
                aact.setItemCode1(dto.getItemCode1() == null ? null : dto.getItemCode1());                      //资产科目代码
                aact.setArticleCode1(dto.getArticleCode1() == null ? null : dto.getArticleCode1());             //资产专项
                aact.setItemCode2(dto.getItemCode2() == null ? null : dto.getItemCode2());                      //折旧贷方科目代码
                aact.setArticleCode2(dto.getArticleCode2() == null ? null : dto.getArticleCode2());             //折旧贷方科目专项代码
                aact.setItemCode3(dto.getItemCode3() == null ? null : dto.getItemCode3());                      //折旧借方科目代码
                aact.setArticleCode3(dto.getArticleCode3() == null ? null : dto.getArticleCode3());             //折旧借方科目专项代码
                aact.setItemCode4(dto.getItemCode4() == null ? null : dto.getItemCode4());                      //进项税科目代码
                aact.setArticleCode4(dto.getArticleCode4() == null ? null : dto.getArticleCode4());             //进项税科目专项代码
                aact.setItemCode5(dto.getItemCode5() == null ? null : dto.getItemCode5());                      //资产清理科目代码
                aact.setArticleCode5(dto.getArticleCode5() == null ? null : dto.getArticleCode5());             //资产清理科目专项代码
                aact.setItemCode6(dto.getItemCode6() == null ? null : dto.getItemCode6());                      //无形资产减值准备科目代码
                aact.setArticleCode6(dto.getArticleCode6() == null ? null : dto.getArticleCode6());             //无形资产减值准备科目专项代码
                aact.setDepYears(dto.getDepYears() == null ? null : dto.getDepYears());                         //摊销年限

                //摊销方法
                String depType = null;
                if(dto.getDepType().equals("平均年限法")) {
                    depType = "1";
                }else{
                    depType = dto.getDepType() == null ? null : dto.getDepType();
                }
                aact.setDepType(depType);                                                                       //摊销方法

            }
            //使用状态处理
            String useFlag = null;
            if(dto.getUseFlag().equals("停用")){
                useFlag = "0";
            }else if(dto.getUseFlag().equals("使用")){
                useFlag = "1";
            }else{
                useFlag = dto.getUseFlag() == null||dto.getUseFlag().equals("") ? "1" : dto.getUseFlag();
            }
            aact.setUseFlag(useFlag);                                                                       //使用状态

        //判断是新增操作还是修改操作
//        StringBuffer sql = new StringBuffer();
//        sql.append("select * from intangibleaccassetcodetype where " +
//                "and acc_book_type = '"+ accBookType +"' and acc_book_code = '"+ accBookCode +"' and code_type = '31' and asset_type = '"+ assetType +"'");
//        List<?> list = intangibleAccAssetCodeTypeRepository.queryBySql(sql.toString(),IntangibleAccAssetCodeType.class);
        List<?> list = intangibleAccAssetCodeTypeRepository.queryIntangibleAccAssetCodeTypeByAccBookTypeAndAccBookCodeAndCodeTypeAndassetType(accBookType,accBookCode,assetType);
//            List<?> list = intangibleAccAssetCodeTypeRepository.qryById(centerCode, branchCode, accBookType, accBookCode, codeType, assetType);
            if(list.size()>0){//list.size()大于0，说明是修改操作，等于0，说明是添加操作
                aact.setCreateOper(((IntangibleAccAssetCodeType)list.get(0)).getCreateOper());                            //录入人
                aact.setCreateTime(((IntangibleAccAssetCodeType)list.get(0)).getCreateTime());                            //录入时间
                aact.setUpdateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));                                  //修改人
                //修改时间格式化
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                aact.setUpdateTime(df.format(new Date()));                                                      //修改时间
            }else{
                aact.setCreateOper(String.valueOf(CurrentUser.getCurrentUser().getId()));                       //录入人
                //录入时间格式化
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                aact.setCreateTime(df.format(new Date()));                                                      //录入时间
                aact.setUpdateOper(null);                                                                       //修改人
                aact.setUpdateTime(null);                                                                       //修改时间
            }
            aact.setTemp(dto.getTemp() == null ? null : dto.getTemp());                                         //备用
            intangibleAccAssetCodeTypeRepository.save(aact);
            return InvokeResult.success();

    }

//    /**
//     * 修改无形资产类别编码
//     * @param dto
//     * @return
//     */
//    @Override
//    public InvokeResult update(IntangibleAccAssetCodeTypeDTO dto) {
//        try {
//            //通过Id查询到要修改的数据
//            IntangibleAccAssetCodeTypeId aactid = new IntangibleAccAssetCodeTypeId();
//            aactid.setCenterCode(dto.getCenterCode() == null ? null : dto.getCenterCode());                                                   //核算单位
////            aactid.setBranchCode(dto.getBranchCode() == null ? null : );                                                   //基层单位
//            aactid.setAccBookType(CurrentUser.getCurrentLoginAccountType());                    //账套类型
//            aactid.setAccBookCode(CurrentUser.getCurrentLoginAccount());                        //账套编码
//            aactid.setCodeType(dto.getCodeType() == null ? null : dto.getCodeType());           //管理类别编码
//            aactid.setAssetType(dto.getAssetType() == null ? null : dto.getAssetType());        //无形资产编号
//            return InvokeResult.success();
//        }catch (Exception e){
//            logger.error("无形资产类别编码修改异常", e);
//            return InvokeResult.failure("无形资产编码修改失败，请联系管理员！");
//        }
//    }

    /**
     * 通过科目代码查询科目专项
     * @param id
     * @return
     */
    @Override
    public String getSpecialId(String id) {
        String sprcialId = "";
        try{
            int paramsNo = 1;
            Map<Integer,Object> params = new HashMap<>();
            StringBuffer sql = new StringBuffer();
            sql.append(" select special_code from specialinfo where ");
            sql.append(" id = (select DISTINCT special_id from subjectinfo where 1=1 and end_flag = '0' and all_subject like ?"+paramsNo+" ) ");
            params.put(paramsNo,id+"%");
            paramsNo++;
            List<?> list = intangibleAccAssetCodeTypeRepository.queryBySql(sql.toString(),params, SubjectInfo.class);
            if(list.size()!=0){
                sprcialId = ((SubjectInfo)list.get(0)).getSpecialId();
            }
            return sprcialId;
        }catch (Exception e){
            logger.error("通过科目代码查询科目专项异常", e);
            return sprcialId;
        }

    }

    @Override
    public InvokeResult judgesubject(String itemCodes, String articleCodes) {
        String [] itemcodesArr=itemCodes.split(",");
        String [] articleCodesArr=articleCodes.split(",");
        for(int i=0;i<itemcodesArr.length;i++){
            if(itemcodesArr[i].equals("")){
                continue;
            }
            if(itemcodesArr[i].charAt(itemcodesArr[i].length()-1)!='/'){itemcodesArr[i]=itemcodesArr[i]+"/";}
//            StringBuffer sqls=new StringBuffer(" SELECT * FROM subjectinfo where account='"+CurrentUser.getCurrentLoginAccount()+"' and CONCAT(all_subject,subject_code,'/')='"+itemcodesArr[i]+"'");
//            List<SubjectInfo> subjects= (List<SubjectInfo>)intangibleAccAssetCodeTypeRepository.queryBySql(sqls.toString(),SubjectInfo.class);
            List<SubjectInfo> subjects= subjectRepository.querySubjectInfoByAccountAndAllsubject(CurrentUser.getCurrentLoginAccount(),itemcodesArr[i]);
            if(subjects.size()==0||subjects==null){
                //不存在这个科目
                return  InvokeResult.failure("不存在科目 "+itemcodesArr[i]+"");
            }
            SubjectInfo subjectone=subjects.get(0);
            if(subjectone.getEndFlag().equals("1")){
                //1 非末级
                return  InvokeResult.failure("科目 "+itemcodesArr[i]+"是非末级科目，请填写末级科目");
            }

        }
        for(int i=0;i<articleCodesArr.length;i++){
            if(articleCodesArr[i].equals("") || articleCodesArr[i].equals("undefined")){
                continue;
            }
            int number = 1;
            Map<Integer,Object> maps = new HashMap<>();
            StringBuffer sqls1=new StringBuffer();
            sqls1.append("  select * from specialinfo where account=?"+number);
            maps.put(number,CurrentUser.getCurrentLoginAccount());
            number++;
            sqls1.append(" and special_code=?"+number);
            maps.put(number,articleCodesArr[i]);
            number++;
            List<SpecialInfo> specialInfos= (List<SpecialInfo>)intangibleAccAssetCodeTypeRepository.queryBySql(sqls1.toString(), maps,SpecialInfo.class);
            if(specialInfos.size()==0||specialInfos==null){
                //不存在这个科目
                return  InvokeResult.failure("不存在专项 "+articleCodesArr[i]+"");
            }
            SpecialInfo specialInfo12=specialInfos.get(0);
            if(specialInfo12.getEndflag().equals("1")){
                //1 非末级
                return  InvokeResult.failure("专项 "+articleCodesArr[i]+"是非末级专项，请填写末级专项");
            }

        }

        return InvokeResult.success();

    }

    /**
     * 删除无形资产类别编码
     * @param accBookType   账套类型
     * @param accBookCode   账套编码
     * @param codeType      管理类别编码
     * @param assetType     无形资产类别编码
     * @return
     */
    @Transactional
    @Override
    public InvokeResult delete( String accBookType, String accBookCode, String codeType, String assetType) {

        //通过Id删除无形资产类别编码
        intangibleAccAssetCodeTypeRepository.deleteIntangiBleassetsData( accBookType, accBookCode, codeType, assetType);
        return InvokeResult.success();
    }

    /**
     * 判断该类别是否已被卡片使用
     * @param dto
     * @return
     */
    @Override
    public InvokeResult isUse(IntangibleAccAssetCodeTypeDTO dto) {
        try{
            int paramsNo = 1;
            Map<Integer,Object> params = new HashMap<>();
            StringBuffer sql = new StringBuffer();
            sql.append("select * from IntangibleAccAssetInfo where asset_type = ?"+ paramsNo);
            params.put(paramsNo,dto.getAssetType());
            paramsNo++;
            List<?> list = intangibleAccAssetCodeTypeRepository.queryBySqlSC(sql.toString(),params);
            if(list.size()>0){
                return InvokeResult.failure("该类别编码已被使用，无法删除");
            }else{
                return  InvokeResult.success();
            }
        }catch (Exception e){
            logger.error("操作失败",e);
            return InvokeResult.failure("操作失败，请联系管理员！");
        }
    }

    //查看类别是否有下级
    @Override
    public  String lowerlevel(IntangibleAccAssetCodeTypeDTO dto){

        Integer count=   intangibleAccAssetCodeTypeRepository.lowerlevel(dto.getAccBookType(),dto.getAccBookCode(),dto.getCodeType(),dto.getAssetType());

        return count+"";
    }

    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols) {
        ExcelUtil excelUtil = new ExcelUtil();
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql = new StringBuffer("select '31' as ct, a.asset_type as assetType, a.asset_simple_name as assetSimpleName, \n" +
                "a.asset_complex_name as assetComplexName, a.level as level, a.dep_years as depYears,   \n" +
                "(select c.code_name from codemanage c where c.code_type = 'endflag' and c.code_code = a.end_flag) as endFlag, \n" +
                "(select c.code_name from codemanage c where c.code_type = 'deprMethod' and c.code_code = a.dep_type) as depType, \n" +
                "a.acc_book_type as accBookType, \n" +
                "a.acc_book_code as accBookCode, a.code_type as codeType, a.super_code as superCode, \n" +
                "(select c.code_name from codemanage c where c.code_type = 'useFlag' and c.code_code = a.use_flag) as useFlag,  \n" +
                "a.item_code1 as itemCode1, (select special_name from specialinfo where special_code = a.article_code1) as articleCode1, a.item_code2 as itemCode2, a.article_code2 as articleCode2, \n" +
                "a.item_code3 as itemCode3, a.article_code3 as articleCode3, a.item_code4 as itemCode4, a.article_code4 as articleCode4, \n" +
                "a.item_code5 as itemCode5, a.article_code5 as articleCode5, a.item_code6 as itemCode6, a.article_code6 as articleCode6, \n" +
                "a.create_oper as createOper, a.create_time as createTime, a.update_oper as updateOper, a.update_time as updateTime \n"+
                "from intangibleaccassetcodetype a where 1=1  ");
        IntangibleAccAssetCodeTypeDTO dto = new IntangibleAccAssetCodeTypeDTO();
        try {
            dto = new ObjectMapper().readValue(queryConditions, IntangibleAccAssetCodeTypeDTO.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(dto.getAccBookCode() != null && !dto.getAccBookCode().equals("")){
            sql.append(" and a.acc_book_code = ?"+ paramsNo);
            params.put(paramsNo,dto.getAccBookCode());
            paramsNo++;
        }
        if(dto.getCodeType() != null && !dto.getCodeType().equals("")){
            sql.append(" and a.code_type = ?"+ paramsNo);
            params.put(paramsNo,dto.getCodeType());
            paramsNo++;
        }
        if(dto.getAssetType() != null && !dto.getAssetType().equals("")){
            sql.append(" and a.asset_type like ?"+paramsNo);
            params.put(paramsNo,"%"+dto.getAssetType()+"%");
            paramsNo++;
        }
        if(dto.getAssetSimpleName() != null && !dto.getAssetSimpleName().equals("")){
            sql.append(" and a.asset_simple_name like ?"+paramsNo);
            params.put(paramsNo,"%"+dto.getAssetSimpleName()+"%");
            paramsNo++;
        }
        if(dto.getAssetComplexName() != null && !dto.getAssetComplexName().equals("")){
            sql.append(" and a.asset_complex_name like ?"+paramsNo);
            params.put(paramsNo,"%"+dto.getAssetComplexName()+"%");
            paramsNo++;

        }
        if(dto.getLevel() != null && !dto.getLevel().equals("")){
            sql.append(" and a.level = ?"+paramsNo);
            params.put(paramsNo,dto.getLevel());
            paramsNo++;
        }
        if(dto.getUseFlag() != null && !dto.getUseFlag().equals("")){
            sql.append(" and a.use_flag = ?"+ paramsNo);
            params.put(paramsNo,dto.getUseFlag());
            paramsNo++;
        }
        try {
            // 根据条件查询导出数据集
            List<?> dataList = intangibleAccAssetCodeTypeRepository.queryBySqlSC(sql.toString(),params);
            // 导出
            excelUtil.exportu(request, response, name, cols, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
