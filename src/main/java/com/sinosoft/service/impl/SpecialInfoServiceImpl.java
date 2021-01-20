package com.sinosoft.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.*;
import com.sinosoft.domain.SpecialInfo;
import com.sinosoft.dto.SpecialInfoDTO;
import com.sinosoft.repository.SpecialInfoRepository;
import com.sinosoft.repository.UserInfoRepository;
import com.sinosoft.service.SpecialInfoService;
import com.sinosoft.util.ExcelUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class SpecialInfoServiceImpl implements SpecialInfoService {

    private Logger logger = LoggerFactory.getLogger(SpecialInfoServiceImpl.class);
    @Resource
    private SpecialInfoRepository specialInfoRepository ;
    @Resource
    private UserInfoRepository userInfoRepository;

    @Override
    public Page<?> qrySpecial(int page,int rows,SpecialInfo specialInfo ,int flag) {

        Page<SpecialInfo> result ;
        if(flag == 0){
            //文本框输入查询
            result = specialInfoRepository.findAll(new CusSpecification<SpecialInfo>().and(
                    CusSpecification.Cnd.like("specialCode", specialInfo.getSpecialCode()) ,
                    CusSpecification.Cnd.like("specialName", specialInfo.getSpecialName()),
                    CusSpecification.Cnd.like("endflag",specialInfo.getEndflag()),
                    CusSpecification.Cnd.like("useflag",specialInfo.getUseflag()),
                    CusSpecification.Cnd.like("specialNameP",specialInfo.getSpecialNameP()),
                    CusSpecification.Cnd.like("account",CurrentUser.getCurrentLoginAccount())
            ).asc("specialCode"),new PageRequest((page - 1), rows));
            List<SpecialInfo> listInfo = result.getContent();

            List<SpecialInfoDTO> listDto = new ArrayList<>();
            if(listInfo!=null&&listInfo.size()>0) {
                String superName = "";
                for(SpecialInfo info : listInfo){
                    SpecialInfoDTO dto = SpecialInfoDTO.toDTO(info);
                    String ss = info.getSuperSpecial();
                    if(!"".equals(ss) && ss != null){
                        superName = specialInfoRepository.findById(Long.parseLong(ss)).get().getSpecialCode() ;
                    }

                    int father = superName.length();
                    String child = dto.getSpecialCode().substring(father,dto.getSpecialCode().length());
                    dto.setSpecialCodeFather(superName);
                    dto.setSpecialCodeChild(child);
                    dto.setSuperSpecialName(superName);

                    if (info.getCreateoper()!=null&&!"".equals(info.getCreateoper())) {
                        dto.setCreateoperName(userInfoRepository.findById(Long.valueOf(info.getCreateoper())).get().getUserName());
                    }
                    if (info.getUpdateoper()!=null&&!"".equals(info.getUpdateoper())) {
                        dto.setUpdateoperName(userInfoRepository.findById(Long.valueOf(info.getCreateoper())).get().getUserName());
                    }

                    listDto.add(dto);
                    superName = "";
                }
            }
            Page<?> pagelist = new PageImpl<>(listDto, result.getPageable(), result.getTotalElements());
            return pagelist;

        }else{
            System.out.println("flag = 1");

            StringBuffer sb = new StringBuffer();
            sb.append("select new com.sinosoft.dto.SpecialInfoDTO(t.id,t.specialCode, t.specialName, t.specialNameP, t.endflag, t.account, t.superSpecial, t.createoper, u1.userName as createoperName, t.updateoper, u2.userName as updateoperName, t.createtime, t.updatetime, t.temp, c.codeName as endflagName) from SpecialInfo t left join UserInfo u1 on u1.id = t.createoper left join UserInfo u2 on u2.id = t.updateoper left join CodeSelect c on c.id.codeType = 'endflag' and c.id.codeCode = t.endflag where 1 = 1");

            Map<String,Object> params = new HashMap<>();

            sb.append(" and t.account =: account");
            params.put("account", CurrentUser.getCurrentLoginAccount());

            if(StringUtils.isNotEmpty(specialInfo.getSpecialCode())){
                String [] ar= specialInfo.getSpecialCode().split(",");
                for(int i=0;i<ar.length;i++){
                    if(i==0){
                        if(ar.length==1){
                            sb.append(" and  t.specialCode =:specialCode"+ar[i]);
                            params.put("specialCode"+ar[i],ar[i]  );
                            break;
                        }
                        sb.append(" and ( t.specialCode =:specialCode"+ar[i]);
                        params.put("specialCode"+ar[i],ar[i]  );

                    }
                    if(i>0){
                        if(i==ar.length-1){
                            sb.append(" or t.specialCode =:specialCode"+ar[i]);
                            params.put("specialCode"+ar[i], ar[i]);
                            sb.append(" )");

                        }else {
                            sb.append(" or t.specialCode =:specialCode" + ar[i]);
                            params.put("specialCode" + ar[i], ar[i]);
                        }
                    }
                }
            }

            if(StringUtils.isNotEmpty(specialInfo.getSpecialName())){
                sb.append(" and t.specialName like: specialName");
                params.put("specialName", '%' +specialInfo.getSpecialName()+'%' );
            }
            if(StringUtils.isNotEmpty(specialInfo.getSpecialNameP())){
                sb.append(" and t.specialNameP like: specialNameP");
                params.put("specialNameP", '%' +specialInfo.getSpecialNameP()+'%' );
            }
            if(StringUtils.isNotEmpty(specialInfo.getEndflag())){
                sb.append(" and t.endflag =: endflag");
                params.put("endflag", specialInfo.getEndflag());
            }
            if(StringUtils.isNotEmpty(specialInfo.getUseflag())){
                sb.append(" and t.useflag =: useflag");
                params.put("useflag", specialInfo.getUseflag());
            }
            sb.append(" order by t.specialCode ");
            Page<?> li=  specialInfoRepository.queryByPage(page,rows,sb.toString(),params);
            List<SpecialInfoDTO> listInfo1 = (List<SpecialInfoDTO>) li.getContent();

            List list1=new ArrayList();
            if(listInfo1!=null&&listInfo1.size()>0&&!listInfo1.isEmpty()){
                for (SpecialInfoDTO obj : listInfo1) {

                    String superName="";
                    if(obj.getSuperSpecial() != null && !"".equals(obj.getSuperSpecial())){
                        superName = specialInfoRepository.findById(Long.valueOf(String.valueOf(obj.getSuperSpecial()))).get().getSpecialCode() ;
                    }
                    obj.setSuperSpecialName(superName);
                    list1.add(obj);

                }
            }

            Page<?> pagelist = new PageImpl<>(list1, li.getPageable(), li.getTotalElements());
            return pagelist;

        }
    }


    @Override
    public List<?> qrySuperSpecialList() {
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        return specialInfoRepository.findSuperSpecialList(accBookCode);
    }

    @Override
    public SpecialInfo searchSpecial(long id) {
       // return specialInfoRepository.findSpecialInfoById(id) ;
        return specialInfoRepository.findById(id).get();
    }

    @Override
    public List<?> qrySpecialCode(String value){
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        List resultList=new ArrayList();
        if (value!= null && !"".equals(value)){
            resultList = qrySpecialCodeByValue(value);
        }else {
            //获取所有父菜单为空也就是最外层的父菜单
            List<Map<String, Object>> list = specialInfoRepository.findSuperSpecial(accBookCode);
            //遍历查询父菜单的子菜单
            if (list != null && list.size() >0 && !list.isEmpty()){
                for (Object obj : list) {
                    Map map = new HashMap();
                    map.putAll((Map) obj);
                    List list2=qryChildrenSpecial((Integer) map.get("id"),accBookCode);
                    if(list2!=null&&list2.size()>0){
                        map.put("children",list2);
                        map.put("state","closed");
                    }
                    resultList.add(map);
                }
            }
        }


        return resultList;
    }

    private List<SpecialInfo> qryChildrenSpecial(Integer id,String accBookCode){
        List list1=new ArrayList();
        List<?> list= specialInfoRepository.findChildrenBySuperSpecial(id,accBookCode);
        if(list!=null&&list.size()>0){
            for (Object obj : list) {
                Map map= new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenSpecial((Integer) map.get("id"),accBookCode);
                if(list2!=null&&list2.size()>0){
                    map.put("id",map.get("id"));
                    map.put("text",map.get("text"));
                    map.put("code",map.get("code"));
                    map.put("children",list2);
                    map.put("state","closed");
                }
                map.put("id",map.get("id"));
                map.put("text",map.get("text"));
                map.put("code",map.get("code"));
                list1.add(map);
            }
        }
        return list1;
    }

    //通过输入关键代码去查询
    public List<?> qrySpecialCodeByValue(String value)
    {
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        Set<String> neededIds = new HashSet<>();
        //查询出经过筛选的专项Set
        StringBuffer neededSql = new StringBuffer(" SELECT s.id AS 'id',s.special_code AS 'code', s.special_namep AS 'text' FROM " +
                "specialinfo s WHERE  s.account = ?1  AND s.special_code LIKE ?2 order by s.special_code ");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);
        params.put(2, "%"+value+"%");

        List<?> neededList = specialInfoRepository.queryBySqlSC(neededSql.toString(), params);

        if (neededList!=null&&neededList.size()>0) {
            for(int j=0;j<neededList.size();j++){
                List resultList=new ArrayList();
                Map map = (Map<String, Object>) neededList.get(j);
                neededIds.add(map.get("code").toString());
            }
        }
        List resultList=new ArrayList();
        //查询最外层
        StringBuffer sql = new StringBuffer(" SELECT s.id AS 'id',s.special_code AS 'code', s.special_namep AS 'text' FROM " +
                "specialinfo s WHERE s.account = ?1 AND( s.super_special = '' OR s.super_special IS null ) ORDER BY s.special_code ");

        params = new HashMap<>();
        params.put(1, accBookCode);

        List<?> list = specialInfoRepository.queryBySqlSC(sql.toString(), params);

        for (Object obj : list){
            Map map = new HashMap();
            map.putAll((Map)obj);
            //判断有无下级专项,父级的下一级专项集合
            List childList = qryChildrenSpecialByValue((Integer)map.get("id"),neededIds);
            //当该父级专项无子级时，直接存入父级
            if (childList.size() <= 0 || childList == null){
                if (neededIds.contains(map.get("code").toString())){
                    //如果该父级专项在neededIds中，即符合条件
                    map.put("code",map.get("code"));
                    map.put("text",map.get("text"));
                    resultList.add(map);
                }
            }else {
                //当存在下级专项，且父级专项符合条件，就把下级全部存入父级节点中
                if (neededIds.contains(map.get("code").toString())){
                    List childAll = qryChildSpecial((Integer) map.get("id"));
                    map.put("children",childAll);
                    resultList.add(map);
                }else {
                    //存入下一级专项，而不是全部下级
                    map.put("children",childList);
                    resultList.add(map);
                }
            }
        }

        return resultList;
    }

    private List<SpecialInfo> qryChildrenSpecialByValue(Integer id,Set<String> set){
        List list1 = new ArrayList();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        //通过上一级编码查询下一级专项
        StringBuffer sql = new StringBuffer(" SELECT s.id AS 'id',s.special_code AS 'code', s.special_namep AS 'text' FROM " +
                "specialinfo s WHERE s.account = ?1 AND s.super_special = ?2 ORDER BY s.special_code");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);
        params.put(2, id);

        List<?> list = specialInfoRepository.queryBySqlSC(sql.toString(), params);
        if (list != null && list.size()>0){
            for (Object obj: list){
                Map map = new HashMap();
                map.putAll((Map)obj);
                List list2 = qryChildrenSpecialByValue((Integer) map.get("id"),set);
                if (list2 != null && list2.size()>0){
                    map.put("children",list2);
                }
                String currentId =  map.get("code").toString();
                map.put("code",map.get("code"));
                map.put("text",map.get("text"));

                if (list2 != null && list2.size()>0){
                    list1.add(map);
                }
                if (!(list2!= null && list2.size()>0) && set.contains(currentId)){
                    List list4 = qryChildSpecial((Integer) map.get("id"));
                    map.put("children",list4);
                    list1.add(map);
                }
            }
        }
        return list1;
    }

    private List<SpecialInfo> qryChildSpecial(Integer id){
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        List list1 = new ArrayList();
        StringBuffer sql = new StringBuffer(" SELECT s.id AS 'id',s.special_code AS 'code', s.special_namep AS 'text' FROM " +
                " specialinfo s WHERE s.account = ?1 AND s.super_special = ?2 ORDER BY s.special_code ");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);
        params.put(2, id);

        List<?> list = specialInfoRepository.queryBySqlSC(sql.toString(), params);
        if ( list != null && list.size()>0 ){
            for (Object obj : list){
                Map map = new HashMap();
                map.putAll((Map)obj);
                List list2 =qryChildSpecial((Integer) map.get("id"));
                if (list2 != null && list2.size()>0 ){
                    map.put("code",map.get("code"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                }
                map.put("code",map.get("code"));
                map.put("code",map.get("text"));
                list1.add(map);
            }
        }
        return list1;
    }


    @Override
    public String saveSpecial(SpecialInfoDTO dto) {
            //System.out.println("save:"+dto);
            SpecialInfo specialInfo = new SpecialInfo();
            String str = dto.getSpecialCodeChild();
            for(int i=str.length();--i>=0;){
                int chr=str.charAt(i);
                if(chr<65 || chr>90)
                    return "wrongSpell";
            }
            specialInfo.setSpecialCode(dto.getSpecialCodeFather()+dto.getSpecialCodeChild());
            specialInfo.setSpecialName(dto.getSpecialName());
            specialInfo.setSpecialNameP(dto.getSpecialNameP());
            specialInfo.setEndflag(dto.getEndflag());
           /* specialInfo.setCreateoper(dto.getCreateoper());
            specialInfo.setCreatetime(dto.getCreatetime());*/
            specialInfo.setTemp(dto.getTemp());
            specialInfo.setAccount(CurrentUser.getCurrentLoginAccount());
            specialInfo.setSuperSpecial("");
            //专项存在的情况
            if(specialInfoRepository.findSpecialInfoByCodeAndAccount(specialInfo.getSpecialCode(),CurrentUser.getCurrentLoginAccount()).size() > 0){
                return SPECIAL_ISEXISTE;
            }else{
                //自动获取录入人和系统时间
                specialInfo.setCreateoper(CurrentUser.getCurrentUser().getId() + "");
                specialInfo.setCreatetime(CurrentTime.getCurrentTime());
                specialInfo.setUseflag("1");
                if(specialInfo.getTemp().isEmpty())
                {
                    specialInfo.setTemp(null);
                }
                if(specialInfo.getSpecialCode().isEmpty()){
                    specialInfo.setSpecialCode(null);
                }
                specialInfoRepository.save(specialInfo);
                return "success";
            }

    }

    @Override
    public int countChildNum(long id) {
        return specialInfoRepository.countChildNum(id);
    }

    @Override
    public String saveSpecial(SpecialInfoDTO dto,long id) {
            //System.out.println("save+id:"+dto);
            SpecialInfo specialInfo = new SpecialInfo();
            String str = dto.getSpecialCodeChild();
            for(int i=str.length();--i>=0;){
                int chr=str.charAt(i);
                if(chr<48 || chr>57)
                    return "wrongSpell";
            }
            //专项编码 = 父级专项id对应的编码名称 + 专项数字；
            specialInfo.setSpecialCode(dto.getSpecialCodeFather()+dto.getSpecialCodeChild());
            specialInfo.setSpecialName(dto.getSpecialName());
            specialInfo.setSpecialNameP(dto.getSpecialNameP());
            specialInfo.setEndflag(dto.getEndflag());
            specialInfo.setSuperSpecial(String.valueOf(id));
            specialInfo.setTemp(dto.getTemp());
            specialInfo.setAccount(CurrentUser.getCurrentLoginAccount());
            //专项存在的情况
            if(specialInfoRepository.findSpecialInfoByCodeAndAccount(specialInfo.getSpecialCode(),specialInfo.getAccount()).size() > 0){
                return SPECIAL_ISEXISTE;
            }else{
                //自动获取录入人和系统时间
                specialInfo.setCreateoper(CurrentUser.getCurrentUser().getId() + "");
                specialInfo.setCreatetime(CurrentTime.getCurrentTime());
                specialInfo.setUseflag("1");
                if(specialInfo.getTemp().isEmpty())
                {
                    specialInfo.setTemp(null);
                }

                specialInfoRepository.save(specialInfo);
                return "success";
            }
    }

    @Override
    public String editSpecial(SpecialInfoDTO dto,long id) {
        SpecialInfo s = specialInfoRepository.getOne(id);
        int count = 0;
        count = countChildNum(id);
        if (count!=0){
            return "EXISTE";
        }
        if("0".equals(s.getEndflag())){
            String seg=specialInfoRepository.findSOx(s.getSpecialCode().substring(0,2)+"%");
            if(seg!=null&&!"".equals(seg)){
                List<?> list= getIsNotexist(seg, s.getSpecialCode(), s.getAccount());
                if(list!=null&&list.size()!=0){
                    return ACCSUB_ISEXISTE;
                }
            }
        }

        if (!s.getSpecialCode().equals(dto.getSpecialCodeFather()+dto.getSpecialCodeChild())) {
            String str = dto.getSpecialCodeChild();
            for(int i=str.length();--i>=0;){
                int chr=str.charAt(i);
                if(chr<48 || chr>57)
                    return "wrongSpell";
            }
            if(specialInfoRepository.findSpecialInfoByCodeAndAccount(dto.getSpecialCodeFather()+dto.getSpecialCodeChild(),s.getAccount()).size() > 0){
                return SPECIAL_ISEXISTE;
            }
            s.setSpecialCode(dto.getSpecialCodeFather()+dto.getSpecialCodeChild());
        }
        s.setSpecialName(dto.getSpecialName());
        s.setSpecialNameP(dto.getSpecialNameP());
        //上级专项代码不可编辑
        s.setEndflag(dto.getEndflag());
        if(!dto.getTemp().isEmpty()){
            s.setTemp(dto.getTemp());
        }
        if (dto.getUseflag()!=null&&!"".equals(dto.getUseflag())&&!s.getUseflag().equals(dto.getUseflag())) {
            s.setUseflag(dto.getUseflag());
        }
        s.setUpdateoper(CurrentUser.getCurrentUser().getId()+"");
        s.setUpdatetime(CurrentTime.getCurrentTime());
        specialInfoRepository.save(s);
        return "success";
    }

    private List<?> getIsNotexist(String segmentCol, String specialCode, String accBookCode){
        StringBuffer sql = new StringBuffer();
        sql.append(" select distinct a."+segmentCol);
        sql.append(" from accsubvoucher a where a.acc_book_code = ?1");
        sql.append(" and a." + segmentCol + " = ?2");
        sql.append(" union all ");
        //历史表
        sql.append(" select distinct ah."+segmentCol);
        sql.append(" from accsubvoucherhis ah where ah.acc_book_code = ?1");
        sql.append(" and ah." + segmentCol + " = ?2");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);
        params.put(2, specialCode);

        List<?> list=specialInfoRepository.queryBySql(sql.toString(), params);
        return list;
    }

    @Override
    public String deleteSpecial(long id) {
        SpecialInfo s = specialInfoRepository.getOne(id);
        int count = 0;
        count = countChildNum(id);
        if (count!=0){
            return "EXISTE";
        }
        if("0".equals(s.getEndflag())){
            String seg=specialInfoRepository.findSOx(s.getSpecialCode().substring(0,2)+"%");
            if(seg!=null&&!"".equals(seg)){
                List<?> list= getIsNotexist(seg, s.getSpecialCode(), s.getAccount());
                if(list!=null&&list.size()!=0){
                    return ACCSUB_ISEXISTE;
                }
            }
        }
        specialInfoRepository.deleteById(id);
        return "sucess";
    }


    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                                  String queryConditions, String cols){
         ExcelUtil excelUtil = new ExcelUtil();
        StringBuffer sql=new StringBuffer("select s.id as id, s.createoper as createoper, s.createtime as createtime, " +
                "s.endflag as endflag, s.special_code as specialCode, s.special_name as specialName, s.special_namep as specialNameP, s1.special_code as superSpecialName, s.temp as temp," +
                "s.updateoper as updateoper, s.updatetime as updatetime, s.useflag as useflag,"+
                "s.account as account," +
                "(select c.code_name from codemanage c where c.code_code=s.endflag and c.code_type='endflag') as endflagName " +
                " from specialinfo s left join specialinfo  s1 on s.super_special =s1.id where 1=1 ") ;

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sql.append(" and s.account = ?" + paramsNo);
        params.put(paramsNo, CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        SpecialInfoDTO specialInfoDTO = new SpecialInfoDTO();
        try {
            specialInfoDTO = new ObjectMapper().readValue(queryConditions, SpecialInfoDTO.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        if (specialInfoDTO.getSpecialCode() != null && !"".equals(specialInfoDTO.getSpecialCode())) {
            sql.append(" and s.special_code like ?" + paramsNo);
            params.put(paramsNo, "%"+specialInfoDTO.getSpecialCode()+"%");
            paramsNo++;
        }
        if (specialInfoDTO.getSpecialName() != null && !"".equals(specialInfoDTO.getSpecialName())) {
            sql.append(" and s.special_name like ?" + paramsNo);
            params.put(paramsNo, "%"+specialInfoDTO.getSpecialName()+"%");
            paramsNo++;
        }
        if (specialInfoDTO.getSpecialNameP() != null && !"".equals(specialInfoDTO.getSpecialNameP())) {
            sql.append(" and s.special_namep like ?" + paramsNo);
            params.put(paramsNo, "%"+specialInfoDTO.getSpecialNameP()+"%");
            paramsNo++;
        }
        if (specialInfoDTO.getEndflag() != null && !"".equals(specialInfoDTO.getEndflag())) {
            sql.append(" and s.endflag = ?" + paramsNo);
            params.put(paramsNo, specialInfoDTO.getEndflag());
            paramsNo++;
        }
        if (specialInfoDTO.getUseflag() != null && !"".equals(specialInfoDTO.getUseflag())) {
            sql.append(" and s.useflag = ?" + paramsNo);
            params.put(paramsNo, specialInfoDTO.getUseflag());
            paramsNo++;
        }

        sql.append(" order by s.special_code ");

        try {
            // 根据条件查询导出数据集
            List<?> dataList = specialInfoRepository.queryBySqlSC(sql.toString(), params);
            // 导出
            excelUtil.exportu(request, response, name, cols, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<SpecialInfoDTO> ImportDataFromExcel(InputStream is, String excelFiileName){
        List<SpecialInfoDTO> list = new ArrayList<>();
        try{
            //创建工作簿(输入流，文件名)
            Workbook workbook = this.createWorkbook(is,excelFiileName);
            //创建工作表sheet，第一页（工作簿，工作簿序号）
            Sheet sheet = this.getSheet(workbook,2);//第三页是财产险，第四页是人身险
            //获取sheet中数据的行数
            int rows = sheet.getPhysicalNumberOfRows();
            //获取表头单元格个数
            int cells = sheet.getRow(0).getPhysicalNumberOfCells();
            //利用反射，给JavaBean的属性进行赋值（获取实体类的属性列）
            for (int i = 1;i<rows;i++){
                SpecialInfoDTO dto = new SpecialInfoDTO();
                //获取DTO类的属性列表
                Field[] fields = dto.getClass().getDeclaredFields();

                Row row = sheet.getRow(i);//获取行数据
                int index = 0;//单元格序号
                while(index < cells){
                    Cell cell = row.getCell(index);//获取行中单元格的内容
                    if (null == cell){
                        cell = row.createCell(index);//单元格为空的话就创建一个新的
                    }
                    cell.setCellType(Cell.CELL_TYPE_STRING);//设置单元格类型为String
                    String value = null == cell.getStringCellValue()?"":cell.getStringCellValue();//以String类型获取单元格内容
                    Field field = fields[index];//获取DTO类属性
                    String filedName = field.getName();//实体类属性名称
                    String methodName = "set"+filedName.substring(0,1).toUpperCase()+filedName.substring(1);//获取对应属性的set方法
                    Method setMethod = dto.getClass().getMethod(methodName,new Class[]{String.class});
                    setMethod.invoke(dto,new Object[]{value});//给实体类对象赋值
                    index++;
                }
                if (isHasValues(dto)){//判断对象属性是否有值
                    list.add(dto);

                }

            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return list;
    }
    //生成workbook(文件后缀名为.xls)
    public Workbook createWorkbook(InputStream is,String excelFileName)throws IOException{
        return new HSSFWorkbook(is);
    }
    //根据sheet索引号获取对应的sheet
    private Sheet getSheet(Workbook workbook, int i) {
        return workbook.getSheetAt(0);
    }

    //判断一个对象所有属性是否有值，如果一个属性有值（非空），则返回true
    public boolean isHasValues(Object object){
        Field[] fields = object.getClass().getDeclaredFields();
        boolean flag = false ;
        for (int i= 0;i<fields.length;i++){
            String fieldName = fields[i].getName();
            String methodName = "get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
            Method getMethod;
            try{
                getMethod = object.getClass().getMethod(methodName);
                Object obj = getMethod.invoke(object);
                if(null != obj && !("".equals(obj))){
                    flag = true;
                    break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return flag ;
    }

    public String HasSuperSpecial(List<SpecialInfo> copylist,String superSpecial)throws Exception{

        int flag = 0;
        for (SpecialInfo s : copylist){
            if (superSpecial.equals(s.getSpecialCode())){
                return String.valueOf(s.getId());
            }
            flag += 1;
            if (flag == copylist.size()){
                System.out.println("superSpecial:"+superSpecial);
                throw  new Exception("找不到父级专项");
            }
        }
        return "";
    }
}
