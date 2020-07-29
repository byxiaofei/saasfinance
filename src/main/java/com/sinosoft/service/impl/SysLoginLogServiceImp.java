package com.sinosoft.service.impl;

import com.sinosoft.domain.SysLoginLog;
import com.sinosoft.dto.SysLoginLogDTO;
import com.sinosoft.repository.AccountInfoRepository;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.SysLoginLogRepository;
import com.sinosoft.repository.UserInfoRepository;
import com.sinosoft.service.SysLoginLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SysLoginLogServiceImp implements SysLoginLogService {
    @Resource
    private SysLoginLogRepository sysLoginLogRepository;
    @Resource
    private UserInfoRepository userInfoRepository;
    @Resource
    private BranchInfoRepository branchInfoRepository;
    @Resource
    private AccountInfoRepository accountInfoRepository;

    @Override
    public Page<SysLoginLogDTO> qrySysLoginLog(int page, int rows, SysLoginLogDTO sysLoginLogDTO) {
        if (sysLoginLogDTO.getLoginDateEnd()!=null&&!"".equals(sysLoginLogDTO.getLoginDateEnd())) {
            sysLoginLogDTO.setLoginDateEnd(sysLoginLogDTO.getLoginDateEnd().trim()+" 23:59:59");
        }
        String centreSql = "from sysloginlog s left join userinfo u on u.id = s.user_id where 1=1";
        StringBuffer sql = new StringBuffer("select s.* " + centreSql);
        StringBuffer querySqlCondition = new StringBuffer();

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        if (sysLoginLogDTO.getLoginDateStart()!=null && !"".equals(sysLoginLogDTO.getLoginDateStart())) {
            querySqlCondition.append(" and s.login_date>= ?" + paramsNo);
            params.put(paramsNo, sysLoginLogDTO.getLoginDateStart());
            paramsNo++;
        }
        if (sysLoginLogDTO.getLoginDateEnd()!=null && !"".equals(sysLoginLogDTO.getLoginDateEnd())) {
            querySqlCondition.append(" and s.login_date<= ?" + paramsNo);
            params.put(paramsNo, sysLoginLogDTO.getLoginDateEnd());
            paramsNo++;
        }
        if (sysLoginLogDTO.getUserCode()!=null && !"".equals(sysLoginLogDTO.getUserCode())) {
            querySqlCondition.append(" and u.user_code like ?" + paramsNo);
            params.put(paramsNo, "%"+sysLoginLogDTO.getUserCode()+"%");
            paramsNo++;
        }
        if (sysLoginLogDTO.getUserName()!=null && !"".equals(sysLoginLogDTO.getUserName())) {
            querySqlCondition.append(" and u.user_name like ?" + paramsNo);
            params.put(paramsNo, "%"+sysLoginLogDTO.getUserName()+"%");
            paramsNo++;
        }

        sql.append(querySqlCondition.toString());
        sql.append(" order by s.login_date desc");
        sql.append(" limit " + (page-1)*rows + "," + rows + "");

        List<SysLoginLog> listInfo = (List<SysLoginLog>)sysLoginLogRepository.queryBySql(sql.toString(), params, SysLoginLog.class);
        //设置用户名称、机构名称、账套名称
        List<SysLoginLogDTO> listDto = new ArrayList<SysLoginLogDTO>();
        if(listInfo!=null&&listInfo.size()>0) {
            for (SysLoginLog info : listInfo) {
                SysLoginLogDTO dto = SysLoginLogDTO.toDTO(info);
                long userId = info.getUserId();
                String comId = info.getComId();
                String accountId = info.getAccountId();
                String name = "";
                if (userId!=0) {
                    name = userInfoRepository.findById(userId).get().getUserName();
                    dto.setUserName(name);
                }
                if (comId!=null&&!"".equals(comId)) {
                    name = branchInfoRepository.findById(Integer.valueOf(comId)).get().getComName();
                    dto.setComName(name);
                }
                if (accountId!=null&&!"".equals(accountId)) {
                    name = accountInfoRepository.findById(Integer.valueOf(accountId)).get().getAccountName();
                    dto.setAccountName(name);
                }
                listDto.add(dto);
            }
        }
        long total = sysLoginLogRepository.queryCountBySql("select count(*) " + centreSql +querySqlCondition.toString(), params);
        Page<SysLoginLogDTO> pageList = new PageImpl<>(listDto, new PageRequest(page-1, rows, null), total);
        return pageList;
    }
}
