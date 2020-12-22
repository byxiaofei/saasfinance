package com.sinosoft.service.impl.account;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.account.ProfitLossCarryDownSubject;
import com.sinosoft.domain.account.ProfitLossCarryDownSubjectId;
import com.sinosoft.dto.account.ProfitLossCarryDownSubjectDTO;
import com.sinosoft.repository.account.ProfitLossCarryDownSubjectRepository;
import com.sinosoft.service.account.ProfitLossCarryDownSubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProfitLossCarryDownSubjectServiceImpl implements ProfitLossCarryDownSubjectService {
    private Logger logger = LoggerFactory.getLogger(ProfitLossCarryDownSubjectServiceImpl.class);

    @Resource
    private ProfitLossCarryDownSubjectRepository profitLossCarryDownSubjectRepository;

    /**
     * 损益结转科目查询
     * @param rightsInterestsCode
     * @return
     */
    @Override
    public List<?> queryProfitLossCarryDownSubject(String rightsInterestsCode){
        List<?> list = new ArrayList<>();
        StringBuffer sb = new StringBuffer();

        sb.append("select concat(s.all_subject,s.subject_code) as 'subjectCode',s.subject_name as 'subjectName',s.subject_type as 'subjectType' from subjectinfo s where 1=1");
        sb.append(" and s.account = ?1 and s.subject_type in ('3','4') order by subjectCode");
        Map<Integer, Object> p = new HashMap<>();
        p.put(1, CurrentUser.getCurrentLoginAccount());
        List<?> pList = profitLossCarryDownSubjectRepository.queryBySqlSC(sb.toString(), p);
        Map<String, String> subjectNameMap = new HashMap<String, String>();
        if (pList!=null && pList.size()>0) {
            for (Object o : pList) {
                Map<String, String> map = (Map<String, String>) o;
                String key = map.get("subjectCode");
                String value = map.get("subjectName");
                if (!subjectNameMap.containsKey(key)) {
                    if (key.contains("/")) {
                        String key2 = key.substring(0, key.lastIndexOf("/"));
                        if (subjectNameMap.containsKey(key2)) {
                            subjectNameMap.put(key, subjectNameMap.get(key2) + "/" +value);
                        } else {
                            subjectNameMap.put(key, value);
                        }
                    } else {
                        subjectNameMap.put(key, value);
                    }
                }
            }
        }

        sb.setLength(0);
        sb.append("SELECT CONCAT_WS('',s.all_subject,s.subject_code) AS profitLossCode,s.subject_name AS profitLossCodeName,c.code_name AS endFlag,p.rights_interests_code AS rightsInterestsCode,CONCAT_WS('',su.all_subject,su.subject_code,IF((su.subject_name IS NULL OR su.subject_name = ''),'',' '),su.subject_name) AS rightsInterestsCodeName,p.create_by AS createBy,u1.user_name AS createByName,p.create_time AS createTime,p.last_modify_by AS lastModifyBy,u2.user_name AS lastModifyByName,p.last_modify_time AS lastModifyTime FROM subjectinfo s LEFT JOIN codemanage c ON c.code_type = 'endFlag' AND c.code_code = s.end_flag LEFT JOIN profitlosscarrydownsubject p ON p.profit_loss_code = CONCAT_WS('',s.all_subject,s.subject_code,'/') AND p.account = s.account LEFT JOIN subjectinfo su ON CONCAT_WS('',su.all_subject,su.subject_code,'/') = p.rights_interests_code AND su.subject_type = '3' AND su.end_flag = '0' AND su.useflag = '1' AND su.account = p.account LEFT JOIN userinfo u1 ON u1.id = p.create_by LEFT JOIN userinfo u2 ON u2.id = p.last_modify_by WHERE 1=1");
        sb.append(" AND s.subject_type = '4' AND s.end_flag = '0' AND s.useflag = '1'");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sb.append(" AND s.account = ?" + paramsNo);
        params.put(paramsNo, CurrentUser.getCurrentLoginAccount());
        paramsNo++;

        if (rightsInterestsCode!=null && !"".equals(rightsInterestsCode)) {
            if ("NULL".equals(rightsInterestsCode)) {
                sb.append(" AND p.rights_interests_code IS NULL");
            } else {
                sb.append(" AND p.rights_interests_code = ?" + paramsNo);
                params.put(paramsNo, rightsInterestsCode);
                paramsNo++;
            }
        }

        list = profitLossCarryDownSubjectRepository.queryBySqlSC(sb.toString(), params);

        if (list!=null && list.size()>0 && subjectNameMap.size()>0) {
            for (Object o : list) {
                Map<String, Object> map = (Map<String, Object>) o;
                String key = (String) map.get("profitLossCode");
                if (subjectNameMap.containsKey(key)) {
                    map.put("profitLossCodeName", subjectNameMap.get(key));
                }
            }
        }

        return list;
    }

    @Override
    @Transactional   //String profitLossCodes, String rightsInterestsCode
    public void saveProfitLossCarryDownSubject(ProfitLossCarryDownSubjectDTO dto){
        //  判断损益科目是否为空
        if (dto.getProfitLossCode()!=null && !"".equals(dto.getProfitLossCode())) {
            //先查询存不存在，若不存在则按新增处理，否则按修改处理
            ProfitLossCarryDownSubject p = null;
            //  创建损益科目id，参数1：损益科目代码，参数2：账套编码
            ProfitLossCarryDownSubjectId id = new ProfitLossCarryDownSubjectId(dto.getProfitLossCode()+"/", CurrentUser.getCurrentLoginAccount());
            try {
                //  根据联损益科目代码，参数2：账套编码
                p = profitLossCarryDownSubjectRepository.findById(id).get();
            } catch (Exception e) {
                System.out.println("当前编辑的损益结转科目不存在，按新增处理");
            }
            //  如果查出数据
            if (p!=null) {//修改
                //  判断数据库总的本年利润科目和dto传过来的是否一致
                if (p.getRightsInterestsCode().equals(dto.getRightsInterestsCode())) {
                    //  如果一致
                    System.out.println("当前编辑的损益结转科目的本年利润科目与原数据相同，不做修改处理");
                } else {
                    p.setRightsInterestsCode(dto.getRightsInterestsCode());
                    p.setLastModifyBy(CurrentUser.getCurrentUser().getId()+"");
                    p.setLastModifyTime(CurrentTime.getCurrentTime());
                }
            } else {//新增
                p = new ProfitLossCarryDownSubject();
                p.setId(id);
                p.setRightsInterestsCode(dto.getRightsInterestsCode());
                p.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
                p.setCreateTime(CurrentTime.getCurrentTime());
            }
            //  新增
            profitLossCarryDownSubjectRepository.save(p);
        } else {
            //  损益代码不存在
            throw new RuntimeException("损益科目代码不存在！");
        }
    }

    @Override
    @Transactional
    //  profitLossCodes所有需要结转的损益科目代码
    //  rightsInterestsCode转到哪个科目
    public void saveProfitLossCarryDownSubjectAll(String profitLossCodes, String rightsInterestsCode){
        //  判断未分配利润的代码是否为空
        if (rightsInterestsCode!=null && !"".equals(rightsInterestsCode)) {
            if (!"3141/15/00/".equals(rightsInterestsCode)) {
                throw new RuntimeException("本年利润科目代码设置错误！");
            }
        } else {
            //  不为空将3141/15/00/赋值给rightsInterestsCode
            rightsInterestsCode = "3141/15/00/";
        }
        //  给传过来的损益科目代码拼接逗号
        String[] profitLossCode = profitLossCodes.split(",");
        //  创建损益科目DTO
        ProfitLossCarryDownSubjectDTO dto = new ProfitLossCarryDownSubjectDTO();
        //  设置DTO的权益科目代码
        dto.setRightsInterestsCode(rightsInterestsCode);
        //  判断损益科目是否为空
        if (profitLossCode.length>0) {
            //  循环遍历
            for (String s : profitLossCode) {
                dto.setProfitLossCode(s);
                saveProfitLossCarryDownSubject(dto);
            }
        } else {
            throw new RuntimeException("损益科目代码不存在！");
        }
    }

    @Override
    public List<?> codeSelect(String type) {
        List<?> result = new ArrayList<Object>();
        String sql = "";
        if (type == null) {
            logger.error("参数丢失！");
        } else {
            try {
                if ("".equals(type)) {
                    System.out.println("参数type为空串");
                } else if("rightsInterestsCode".equals(type)){
                    result = profitLossCarryDownSubjectRepository.findRightsInterestsCode(CurrentUser.getCurrentLoginAccount());
                } else if("rightsInterestsCodeNotSet".equals(type)){
                    result = profitLossCarryDownSubjectRepository.findRightsInterestsCode(CurrentUser.getCurrentLoginAccount());
                    return addNotSet(result);
                } else if("rightsInterestsCodeAndUnlimited".equals(type)){
                    result = profitLossCarryDownSubjectRepository.findRightsInterestsCode(CurrentUser.getCurrentLoginAccount());
                    return addUnlimited(result);
                }
            } catch (Exception e){
                logger.error("查询数据异常,type="+type, e);
            }
        }
        return result;
    }

    private List<?> addUnlimited(List<?> list) {
        List<Object> resultAll=new ArrayList<Object>();
        resultAll.addAll(list);
        Map map=new HashMap();
        map.put("value", null);
        map.put("text", "-不限-");
        resultAll.add(0,map);
        return resultAll;
    }
    private List<?> addNotSet(List<?> list) {
        List<Object> resultAll=new ArrayList<Object>();
        resultAll.addAll(list);
        Map map=new HashMap();
        map.put("value", "NULL");
        map.put("text", " -空- ");
        resultAll.add(0,map);
        return resultAll;
    }
}
