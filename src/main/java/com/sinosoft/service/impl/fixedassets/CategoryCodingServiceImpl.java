package com.sinosoft.service.impl.fixedassets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.SpecialInfo;
import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.domain.fixedassets.AccAssetCodeType;
import com.sinosoft.domain.fixedassets.AccAssetCodeTypeId;
import com.sinosoft.dto.fixedassets.AccAssetCodeTypeDTO;
import com.sinosoft.repository.SpecialInfoRepository;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.service.fixedassets.CategoryCodingService;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 17:14
 */
@Service
public class CategoryCodingServiceImpl implements CategoryCodingService {
    private Logger logger = LoggerFactory.getLogger(CategoryCodingServiceImpl.class);
    @Resource
    private AccAssetCodeTypeRepository accAssetCodeTypeRepository;
    @Resource
    private SubjectRepository subjectRepository;
    @Resource
    private SpecialInfoRepository specialInfoRepository;
    /**
     * 查询固定资产类别编码信息
     * @param dto
     * @return
     */
    @Override
    public Page<?> qryAccAssetCodeTypeList(int page, int rows, AccAssetCodeTypeDTO dto) {
        StringBuffer sql = new StringBuffer();
        sql.append("select '21' as ct, a.asset_type as assetType, a.asset_simple_name as assetSimpleName, \n" +
                "a.asset_complex_name as assetComplexName, a.net_surplus_rate as netSurplusRate,  \n" +
                "a.level as level, a.dep_years as depYears, \n" +
                "(select c.code_name from codemanage c where c.code_type = 'endflag' and c.code_code = a.end_flag) as endFlag, \n" +
                "(select c.code_name from codemanage c where c.code_type = 'deprMethod' and c.code_code = a.dep_type) as depType, \n" +
                "a.acc_book_type as accBookType, \n" +
                "a.acc_book_code as accBookCode, a.code_type as codeType, a.super_code as superCode, \n" +
                "(select c.code_name from codemanage c where c.code_type = 'useFlag' and c.code_code = a.use_flag) as useFlag,  \n" +
                "a.item_code1 as itemCode1, (select special_name from specialinfo where special_code = a.article_code1 and account=a.acc_book_code) as articleCode11, " +
                "a.article_code1 as articleCode1, a.item_code2 as itemCode2, a.article_code2 as articleCode2, \n" +
                "a.item_code3 as itemCode3, a.article_code3 as articleCode3, a.item_code4 as itemCode4, a.article_code4 as articleCode4, \n" +
                "a.item_code5 as itemCode5, a.article_code5 as articleCode5, a.item_code6 as itemCode6, a.article_code6 as articleCode6, \n" +
                "a.create_oper as createOper, a.create_time as createTime, a.update_oper as updateOper, a.update_time as updateTime, \n"+
                "(select count(f.card_code) from accassetinfo f where f.acc_book_code=a.acc_book_code and f.asset_type=a.asset_type) as counts, "+
                "(SELECT COUNT(t.asset_type) FROM accassetcodetype t WHERE t.super_code = a.asset_type) AS hasNext "+
                "from accassetcodetype a where 1=1  ");

        int paramsNo = 1 ;
        Map<Integer,Object> params = new HashMap<>();

        sql.append(" and a.acc_book_type =?" + paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append(" and a.acc_book_code =?" + paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        if(dto.getCodeType() != null && !dto.getCodeType().equals("")){
            sql.append(" and a.code_type =?"+ paramsNo);
            params.put(paramsNo,dto.getCodeType());
            paramsNo++;
        }
        if(dto.getAssetType() != null && !dto.getAssetType().equals("")){
            sql.append(" and a.asset_type like ?"+ paramsNo);
            params.put(paramsNo,"%"+dto.getAssetType()+"%");
            paramsNo++;
        }
        if(dto.getAssetSimpleName() != null && !dto.getAssetSimpleName().equals("")){
            sql.append(" and a.asset_simple_name like ?"+ paramsNo);
            params.put(paramsNo,"%"+dto.getAssetSimpleName()+"%");
            paramsNo++;
        }
        if(dto.getAssetComplexName() != null && !dto.getAssetComplexName().equals("")){
            sql.append(" and a.asset_complex_name like ?"+ paramsNo);
            params.put(paramsNo,"%"+dto.getAssetComplexName()+"%");
            paramsNo++;
        }
        if(dto.getLevel() != null && !dto.getLevel().equals("")){
            sql.append(" and a.level = ?"+ paramsNo);
            params.put(paramsNo,dto.getLevel());
            paramsNo++;
        }
        if(dto.getUseFlag() != null && !dto.getUseFlag().equals("")){
            sql.append(" and a.use_flag = ?"+ paramsNo);
            params.put(paramsNo,dto.getUseFlag());
            paramsNo++;
        }

        Page<?> result = getPage(page,rows,accAssetCodeTypeRepository.queryBySqlSC(sql.toString(),params));
        return result;

    }

    public Page<?> getPage(int page,int rows,List<?> list){

        List<Object> pageList = new ArrayList<>();
        //开始截取需要的数据
        //开始位置，下标从0开始（每页开始的位置就是之前页的条数之和）
        int index = (page-1)*rows;
        //数据总条数
        int listSize = list.size();
        if (listSize<=rows) {
            //数据总条数不超过每页显示的行数
            pageList = (List<Object>) list;
        } else {//数据总条数超过每页显示的行数
            int indexEnd = index+rows;//按每页足数显示时，结束位置下标
            if (indexEnd+1>listSize) {
                //当前页的取数范围超过总数据范围
                for (int i=0;i<rows;i++) {
                    if (index>=listSize) {
                        break;
                    } else {
                        pageList.add(list.get(index));
                    }
                    index++;
                }
            } else {//当前页的取数范围未超过总数据范围
                for (int i=0;i<rows;i++) {
                    pageList.add(list.get(index));
                    index++;
                }
            }
        }

        Page<?> res = new PageImpl<>(pageList, new PageRequest(page-1, rows, null), listSize);
        return res;
    }

    /**
     * 修改固定资产编码
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult update(AccAssetCodeTypeDTO dto) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();

//            //判断当前类别编码是否存在
//            StringBuffer assetTypeSql = new StringBuffer();
//            assetTypeSql.append("select * from AccAssetCodeType where asset_type = "+ dto.getAssetType() +"");
//            List<?> assetTypeList = accAssetCodeTypeRepository.queryBySqlSC(assetTypeSql.toString());
//            if(assetTypeList.size() > 0){//当前类别编码已存在
//                return InvokeResult.failure("当前类别编码已存在！");
//            }
        //将前台获取到的值通过实体类存入数据库中
        AccAssetCodeTypeId aactid = new AccAssetCodeTypeId();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        aactid.setAccBookType(accBookType);                                                 //账套类型
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        aactid.setAccBookCode(accBookCode);                                                 //账套编码
//            String codeType = dto.getCodeType() == null ? null : dto.getCodeType();
        aactid.setCodeType("21");                                                           //管理类别编码
        String assetType = dto.getAssetType() == null ? null : dto.getAssetType();
        aactid.setAssetType(assetType);                                                     //固定资产编号
        AccAssetCodeType aact = new AccAssetCodeType();
        aact.setId(aactid);
        aact.setAssetSimpleName(dto.getAssetSimpleName() == null ? null : dto.getAssetSimpleName());    //固定资产类别简称
        aact.setAssetComplexName(dto.getAssetComplexName() == null ? null : dto.getAssetComplexName()); //固定资产类别全称
        aact.setLevel(dto.getLevel() == null ? null : dto.getLevel());                                  //固定资产类别层级

        //末级标志处理
        String endFlag = null;
        if(dto.getEndFlag().equals("末级")){
            endFlag = "0";
        }else if(dto.getEndFlag().equals("非末级")){
            endFlag = "1";
        }else{
            endFlag = dto.getEndFlag() == null? null : dto.getEndFlag();
        }
        aact.setEndFlag(endFlag);                                                                       //末级标志
        aact.setSuperCode(dto.getSuperCode() == null ? null : dto.getSuperCode());                      //父级编码

        if(dto.getEndFlag().equals("末级")||dto.getEndFlag().equals("0")){//末级 正常存储
            //if(!dto.getItemCode1().substring(dto.getItemCode1().length()-1).equals("/")){}
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
            aact.setItemCode6(dto.getItemCode6() == null ? null : dto.getItemCode6());                      //固定资产减值准备科目代码
            aact.setArticleCode6(dto.getArticleCode6() == null ? null : dto.getArticleCode6());             //固定资产减值准备科目专项代码
            aact.setNetSurplusRate(dto.getNetSurplusRate() == null ? null : dto.getNetSurplusRate());       //净残值率
            aact.setDepYears(dto.getDepYears() == null ? null : dto.getDepYears());                         //折旧年限

            //折旧方法处理
            String depType = null;
            if(dto.getDepType().equals("平均年限法")) {
                depType = "1";
            }else{
                depType = dto.getDepType() == null ? null : dto.getDepType();
            }
            aact.setDepType(depType);                                                                       //折旧方法

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
        aact.setUseFlag(useFlag);
        //判断是新增操作还是修改操作
//        StringBuffer sql = new StringBuffer();
//        sql.append("select * from accassetcodetype where acc_book_type = '"+ accBookType +"' and acc_book_code = '"+ accBookCode +"' and code_type = '21' and asset_type = '"+ assetType +"'");
//        List<?> list = accAssetCodeTypeRepository.queryBySql(sql.toString(),AccAssetCodeType.class);
        List<AccAssetCodeType> list = accAssetCodeTypeRepository.checkInsertOrUpdateOprration(accBookType,accBookCode,assetType);
//            List<?> list = accAssetCodeTypeRepository.qryById(centerCode, branchCode, accBookType, accBookCode, codeType, assetType);
        if(list.size()>0){//list.size()大于0，说明是修改操作，等于0，说明是添加操作
            aact.setCreateOper(((AccAssetCodeType)list.get(0)).getCreateOper());                            //录入人
            aact.setCreateTime(((AccAssetCodeType)list.get(0)).getCreateTime());                            //录入时间
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
        accAssetCodeTypeRepository.save(aact);
        return InvokeResult.success();


    }

    /**
     * 新增固定资产编码
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult add(AccAssetCodeTypeDTO dto) {
        //判断当前类别编码是否存在
//        StringBuffer assetTypeSql = new StringBuffer();
//        assetTypeSql.append("select * from AccAssetCodeType where asset_type = '"+ dto.getAssetType() +"'" +
//                " and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");
//        List<?> assetTypeList = accAssetCodeTypeRepository.queryBySqlSC(assetTypeSql.toString());
        List<?> assetTypeList = accAssetCodeTypeRepository.queryCategoryNoByAssetTypeAndAccBookTypeAndAccBookCode(dto.getAssetType(),CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount());
        if(assetTypeList.size() > 0){//当前类别编码已存在
            return InvokeResult.failure("当前类别编码已存在！");
        }

        //将前台获取到的值通过实体类存入数据库中
        AccAssetCodeTypeId aactid = new AccAssetCodeTypeId();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        aactid.setAccBookType(accBookType);                                                 //账套类型
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        aactid.setAccBookCode(accBookCode);                                                 //账套编码
//            String codeType = dto.getCodeType() == null ? null : dto.getCodeType();
        aactid.setCodeType("21");                                                           //管理类别编码
        String assetType = dto.getAssetType() == null ? null : dto.getAssetType();
        aactid.setAssetType(assetType);                                                     //固定资产编号
        AccAssetCodeType aact = new AccAssetCodeType();
        aact.setId(aactid);
        aact.setAssetSimpleName(dto.getAssetSimpleName() == null ? null : dto.getAssetSimpleName());    //固定资产类别简称
        aact.setAssetComplexName(dto.getAssetComplexName() == null ? null : dto.getAssetComplexName()); //固定资产类别全称
        aact.setLevel(dto.getLevel() == null ? null : dto.getLevel());                                  //固定资产类别层级

        //末级标志处理
        String endFlag = null;
        if(dto.getEndFlag().equals("末级")){
            endFlag = "0";
        }else if(dto.getEndFlag().equals("非末级")){
            endFlag = "1";
        }else{
            endFlag = dto.getEndFlag() == null? null : dto.getEndFlag();
        }
        aact.setEndFlag(endFlag);                                                                       //末级标志
        aact.setSuperCode(dto.getSuperCode() == null ? null : dto.getSuperCode());                      //父级编码

        if(dto.getEndFlag().equals("0")){//末级 正常存储
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
            aact.setItemCode6(dto.getItemCode6() == null ? null : dto.getItemCode6());                      //固定资产减值准备科目代码
            aact.setArticleCode6(dto.getArticleCode6() == null ? null : dto.getArticleCode6());             //固定资产减值准备科目专项代码
            aact.setNetSurplusRate(dto.getNetSurplusRate() == null ? null : dto.getNetSurplusRate());       //净残值率
            aact.setDepYears(dto.getDepYears() == null ? null : dto.getDepYears());                         //折旧年限

            //折旧方法处理
            String depType = null;
            if(dto.getDepType().equals("平均年限法")) {
                depType = "1";
            }else{
                depType = dto.getDepType() == null ? null : dto.getDepType();
            }
            aact.setDepType(depType);                                                                       //折旧方法

            //使用状态处理
            String useFlag = null;
            if(dto.getUseFlag().equals("停用")){
                useFlag = "0";
            }else if(dto.getUseFlag().equals("使用")){
                useFlag = "1";
            }else{
                useFlag = dto.getUseFlag() == null ? null : dto.getUseFlag();
            }
            aact.setUseFlag(useFlag);                                                                       //使用状态
        }else{
            aact.setUseFlag("1");
        }

        //判断是新增操作还是修改操作
//        StringBuffer sql = new StringBuffer();
//        sql.append("select * from accassetcodetype where  " +
//                " acc_book_type = '"+ accBookType +"' and acc_book_code = '"+ accBookCode +"' and code_type = '21' and asset_type = '"+ assetType +"'");
//        List<?> list = accAssetCodeTypeRepository.queryBySql(sql.toString(),AccAssetCodeType.class);
        List<AccAssetCodeType> list = accAssetCodeTypeRepository.checkInsertOrUpdateOprration(accBookType,accBookCode,assetType);
//            List<?> list = accAssetCodeTypeRepository.qryById(centerCode, branchCode, accBookType, accBookCode, codeType, assetType);
        if(list.size()>0){//list.size()大于0，说明是修改操作，等于0，说明是添加操作
            aact.setCreateOper(((AccAssetCodeType)list.get(0)).getCreateOper());                            //录入人
            aact.setCreateTime(((AccAssetCodeType)list.get(0)).getCreateTime());                            //录入时间
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
        accAssetCodeTypeRepository.save(aact);
        return InvokeResult.success();


    }

    /**
     * 新增下级固定资产类别编码
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public InvokeResult addLowerLevel(AccAssetCodeTypeDTO dto) {
        //将前台获取到的值通过实体类存入数据库中
        AccAssetCodeTypeId aactid = new AccAssetCodeTypeId();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        aactid.setAccBookType(accBookType);                                                 //账套类型
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        aactid.setAccBookCode(accBookCode);                                                 //账套编码
//            String codeType = dto.getCodeType() == null ? null : dto.getCodeType();
        aactid.setCodeType("21");                                                           //管理类别编码
        String assetType = dto.getAssetType() == null ? null : dto.getAssetType();
        //父级类别编码+当前类别编码
        aactid.setAssetType(dto.getSuperCode()+assetType);//固定资产编号
        AccAssetCodeType aact = new AccAssetCodeType();
        aact.setId(aactid);
        aact.setAssetSimpleName(dto.getAssetSimpleName() == null ? null : dto.getAssetSimpleName());    //固定资产类别简称
        aact.setAssetComplexName(dto.getAssetComplexName() == null ? null : dto.getAssetComplexName()); //固定资产类别全称
        aact.setLevel(dto.getLevel() == null ? null : dto.getLevel());                                  //固定资产类别层级

        //末级标志处理
        String endFlag = null;
        if(dto.getEndFlag().equals("末级")){
            endFlag = "0";
        }else if(dto.getEndFlag().equals("非末级")){
            endFlag = "1";
        }else{
            endFlag = dto.getEndFlag() == null? null : dto.getEndFlag();
        }
        aact.setEndFlag(endFlag);                                                                       //末级标志
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
            aact.setItemCode6(dto.getItemCode6() == null ? null : dto.getItemCode6());                      //固定资产减值准备科目代码
            aact.setArticleCode6(dto.getArticleCode6() == null ? null : dto.getArticleCode6());             //固定资产减值准备科目专项代码
            aact.setNetSurplusRate(dto.getNetSurplusRate() == null ? null : dto.getNetSurplusRate());       //净残值率
            aact.setDepYears(dto.getDepYears() == null ? null : dto.getDepYears());                         //折旧年限

            //折旧方法处理
            String depType = null;
            if(dto.getDepType().equals("平均年限法")) {
                depType = "1";
            }else{
                depType = dto.getDepType() == null ? null : dto.getDepType();
            }
            aact.setDepType(depType);                                                                       //折旧方法

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
//        sql.append("select * from accassetcodetype where  " +
//                "and acc_book_type = '"+ accBookType +"' and acc_book_code = '"+ accBookCode +"' and code_type = '21' and asset_type = '"+ assetType +"'");
//        List<?> list = accAssetCodeTypeRepository.queryBySql(sql.toString(),AccAssetCodeType.class);
        List<AccAssetCodeType> list = accAssetCodeTypeRepository.checkInsertOrUpdateOprration(accBookType,accBookCode,assetType);
//       List<?> list = accAssetCodeTypeRepository.qryById(centerCode, branchCode, accBookType, accBookCode, codeType, assetType);
        if(list.size()>0){//list.size()大于0，说明是修改操作，等于0，说明是添加操作
            aact.setCreateOper(((AccAssetCodeType)list.get(0)).getCreateOper());                            //录入人
            aact.setCreateTime(((AccAssetCodeType)list.get(0)).getCreateTime());                            //录入时间
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
        accAssetCodeTypeRepository.save(aact);
        return InvokeResult.success();

    }


    /**
     * 通过科目代码查询科目专项
     * @param id
     * @return
     */
    @Override
    public String getSpecialId(String id) {
        String sprcialId = "";
        try{
            StringBuffer sql = new StringBuffer();
            int paramNo = 1;
            Map<Integer,Object> params = new HashMap<>();
            sql.append(" select special_code from specialinfo where ");
            sql.append(" id = (select DISTINCT special_id from subjectinfo where 1=1 and end_flag = '0' and all_subject like ?"+paramNo+") ");
            params.put(paramNo,id+"%");
            paramNo++;
            List<?> list = accAssetCodeTypeRepository.queryBySql(sql.toString(),params, SubjectInfo.class);
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
//            List<SubjectInfo> subjects= (List<SubjectInfo>)accAssetCodeTypeRepository.queryBySql(sqls.toString(),SubjectInfo.class);
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
//            StringBuffer sqls1=new StringBuffer("  select * from specialinfo where account='"+CurrentUser.getCurrentLoginAccount()+"' and special_code='"+articleCodesArr[i]+"'");
//            List<SpecialInfo> specialInfos= (List<SpecialInfo>)accAssetCodeTypeRepository.queryBySql(sqls1.toString(), SpecialInfo.class);
            List<SpecialInfo> specialInfos= specialInfoRepository.querySpecialInfoByAccountAndSpecialCode(CurrentUser.getCurrentLoginAccount(),articleCodesArr[i]);
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
     * 删除固定资产类别编码
     * @param centerCode    核算单位
     * @param branchCode    基层单位
     * @param accBookType   账套类型
     * @param accBookCode   账套编码
     * @param codeType      管理类别编码
     * @param assetType     固定资产类别编码
     * @return
     */
    @Transactional
    @Override
    public InvokeResult delete(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String assetType) {

        //通过Id删除固定资产类别编码
        accAssetCodeTypeRepository.deleteCategoryCodingData( accBookType, accBookCode, codeType, assetType);
        return InvokeResult.success();
    }

    /**
     * 判断该类别是否已被卡片使用
     * @param dto
     * @return
     */
    @Override
    public InvokeResult isUse(AccAssetCodeTypeDTO dto) {
        try{
//            StringBuffer sql = new StringBuffer("select * from AccAssetInfo where center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and asset_type = '"+ dto.getAssetType() +"'" +
//                    " and acc_book_type = '"+ CurrentUser.getCurrentLoginAccountType() +"' and acc_book_code = '"+ CurrentUser.getCurrentLoginAccount() +"' ");
//            List<?> list = accAssetCodeTypeRepository.queryBySqlSC(sql.toString());
            List<?> list = accAssetCodeTypeRepository.queryAccAssetInfoByCenterCodeAndAssetTypeAndAccBookTypeAndAccBookCode(CurrentUser.getCurrentLoginManageBranch(),dto.getAssetType(),CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount());
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

    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols) {
        ExcelUtil excelUtil = new ExcelUtil();
        StringBuffer sql = new StringBuffer("select '21' as ct, a.asset_type as assetType, a.asset_simple_name as assetSimpleName," +
                "a.asset_complex_name as assetComplexName, a.net_surplus_rate as netSurplusRate," +
                "a.level as level, a.dep_years as depYears," +
                "(select c.code_name from codemanage c where c.code_type = 'endflag' and c.code_code = a.end_flag) as endFlag," +
                "(select c.code_name from codemanage c where c.code_type = 'deprMethod' and c.code_code = a.dep_type) as depType," +
                " a.acc_book_type as accBookType," +
                "a.acc_book_code as accBookCode, a.code_type as codeType, a.super_code as superCode," +
                "(select c.code_name from codemanage c where c.code_type = 'useFlag' and c.code_code = a.use_flag) as useFlag," +
                "a.item_code1 as itemCode1, (select special_name from specialinfo where special_code = a.article_code1 and account=a.acc_book_code) as articleCode1, a.item_code2 as itemCode2, a.article_code2 as articleCode2," +
                "a.item_code3 as itemCode3, a.article_code3 as articleCode3, a.item_code4 as itemCode4, a.article_code4 as articleCode4," +
                "a.item_code5 as itemCode5, a.article_code5 as articleCode5, a.item_code6 as itemCode6, a.article_code6 as articleCode6," +
                "a.create_oper as createOper, a.create_time as createTime, a.update_oper as updateOper, a.update_time as updateTime   "+
                "from accassetcodetype a where 1=1  ");

        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        sql.append(" and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append(" and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        AccAssetCodeTypeDTO dto =new AccAssetCodeTypeDTO();
        try {
            dto = new ObjectMapper().readValue(queryConditions, AccAssetCodeTypeDTO.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(dto.getCodeType() != null && !dto.getCodeType().equals("")){
            sql.append(" and a.code_type = ?"+ paramsNo );
            params.put(paramsNo,dto.getCodeType());
            paramsNo++;
        }
        if(dto.getAssetType() != null && !dto.getAssetType().equals("")){
            sql.append(" and a.asset_type like ?"+ paramsNo);
            params.put(paramsNo,"%"+dto.getAssetType()+"%");
            paramsNo++;
        }
        if(dto.getAssetSimpleName() != null && !dto.getAssetSimpleName().equals("")){
            sql.append(" and a.asset_simple_name like ?"+ paramsNo);
            params.put(paramsNo,"%"+dto.getAssetSimpleName()+"%");
            paramsNo++;
        }
        if(dto.getAssetComplexName() != null && !dto.getAssetComplexName().equals("")){
            sql.append(" and a.asset_complex_name like ?"+ paramsNo);
            params.put(paramsNo,"%"+dto.getAssetComplexName()+"%");
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

        try {
            // 根据条件查询导出数据集
            List<?> dataList = accAssetCodeTypeRepository.queryBySqlSC(sql.toString(),params);
            // 导出
            excelUtil.exportu(request, response, name, cols, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //查看类别是否有下级
    @Override
    public  String lowerlevel(AccAssetCodeTypeDTO dto){

        Integer count=   accAssetCodeTypeRepository.lowerlevel(dto.getAccBookType(),dto.getAccBookCode(),dto.getCodeType(),dto.getAssetType());

        return count+"";
    }
    @Override
    public List<?> getAccAssetCodeTypeList( AccAssetCodeTypeDTO dto) {
        StringBuffer sql = new StringBuffer();
        sql.append("select '21' as ct, a.asset_type as assetType, a.asset_simple_name as assetSimpleName, \n" +
                "a.asset_complex_name as assetComplexName, a.net_surplus_rate as netSurplusRate,  \n" +
                "a.level as level, a.dep_years as depYears, \n" +
                "(select c.code_name from codemanage c where c.code_type = 'endflag' and c.code_code = a.end_flag) as endFlag, \n" +
                "(select c.code_name from codemanage c where c.code_type = 'deprMethod' and c.code_code = a.dep_type) as depType, \n" +
                "a.acc_book_type as accBookType, \n" +
                "a.acc_book_code as accBookCode, a.code_type as codeType, a.super_code as superCode, \n" +
                "(select c.code_name from codemanage c where c.code_type = 'useFlag' and c.code_code = a.use_flag) as useFlag,  \n" +
                "a.item_code1 as itemCode1, (select special_name from specialinfo where special_code = a.article_code1 and account=a.acc_book_code) as articleCode11, " +
                "a.article_code1 as articleCode1, a.item_code2 as itemCode2, a.article_code2 as articleCode2, \n" +
                "a.item_code3 as itemCode3, a.article_code3 as articleCode3, a.item_code4 as itemCode4, a.article_code4 as articleCode4, \n" +
                "a.item_code5 as itemCode5, a.article_code5 as articleCode5, a.item_code6 as itemCode6, a.article_code6 as articleCode6, \n" +
                "a.create_oper as createOper, a.create_time as createTime, a.update_oper as updateOper, a.update_time as updateTime, \n"+
                "(select count(f.card_code) from accassetinfo f where f.acc_book_code=a.acc_book_code and f.asset_type=a.asset_type) as counts, "+
                "(SELECT COUNT(t.asset_type) FROM accassetcodetype t WHERE t.super_code = a.asset_type) AS hasNext "+
                "from accassetcodetype a where 1=1  ");

        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        sql.append(" and acc_book_type = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccountType());
        paramsNo++;
        sql.append(" and acc_book_code = ?"+paramsNo);
        params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
        paramsNo++;


        if(dto.getCodeType() != null && !dto.getCodeType().equals("")){
            sql.append(" and a.code_type = ?"+ paramsNo );
            params.put(paramsNo,dto.getCodeType());
            paramsNo++;
        }
        if(dto.getAssetType() != null && !dto.getAssetType().equals("")){
            sql.append(" and a.asset_type like ?"+ paramsNo);
            params.put(paramsNo,"%"+dto.getAssetType()+"%");
            paramsNo++;
        }
        if(dto.getAssetSimpleName() != null && !dto.getAssetSimpleName().equals("")){
            sql.append(" and a.asset_simple_name like ?"+ paramsNo);
            params.put(paramsNo,"%"+dto.getAssetSimpleName()+"%");
            paramsNo++;
        }
        if(dto.getAssetComplexName() != null && !dto.getAssetComplexName().equals("")){
            sql.append(" and a.asset_complex_name like ?"+ paramsNo);
            params.put(paramsNo,"%"+dto.getAssetComplexName()+"%");
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

        List<?> result = accAssetCodeTypeRepository.queryBySqlSC(sql.toString(),params);
        return result;

    }
}
