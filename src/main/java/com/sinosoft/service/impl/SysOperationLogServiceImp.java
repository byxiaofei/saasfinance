package com.sinosoft.service.impl;

import com.sinosoft.domain.SysOperationLog;
import com.sinosoft.domain.UserInfo;
import com.sinosoft.dto.SysOperationLogDTO;
import com.sinosoft.repository.SysOperationLogRepository;
import com.sinosoft.repository.UserInfoRepository;
import com.sinosoft.service.SysOperationLogService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysOperationLogServiceImp implements SysOperationLogService {
    @Resource
    private SysOperationLogRepository sysOperationLogRepository;
    @Resource
    private UserInfoRepository userInfoRepository;

    @Override
    public Page<SysOperationLogDTO> qrySysOperationLog(int page, int rows, SysOperationLogDTO sysOperationLogDTO) {
        if (sysOperationLogDTO.getCreateDateEnd()!=null&&!"".equals(sysOperationLogDTO.getCreateDateEnd())) {
            sysOperationLogDTO.setCreateDateEnd(sysOperationLogDTO.getCreateDateEnd().trim()+" 23:59:59");
        }
        String centreSql = "from sysoperationlog s left join userinfo u on u.id = s.user_id where 1=1";
        StringBuffer sql = new StringBuffer("select s.* " + centreSql);
        StringBuffer querySqlCondition = new StringBuffer();

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        if (sysOperationLogDTO.getUserCode()!=null && !"".equals(sysOperationLogDTO.getUserCode())) {
            querySqlCondition.append(" and u.user_code like ?" + paramsNo);
            params.put(paramsNo, "%"+sysOperationLogDTO.getUserCode()+"%");
            paramsNo++;
        }
        if (sysOperationLogDTO.getUserName()!=null && !"".equals(sysOperationLogDTO.getUserName())) {
            querySqlCondition.append(" and u.user_name like ?" + paramsNo);
            params.put(paramsNo, "%"+sysOperationLogDTO.getUserName()+"%");
            paramsNo++;
        }
        if (sysOperationLogDTO.getOperation()!=null && !"".equals(sysOperationLogDTO.getOperation())) {
            querySqlCondition.append(" and s.operation like ?" + paramsNo);
            params.put(paramsNo, "%"+sysOperationLogDTO.getOperation()+"%");
            paramsNo++;
        }
        if (sysOperationLogDTO.getCreateDateStart()!=null && !"".equals(sysOperationLogDTO.getCreateDateStart())) {
            querySqlCondition.append(" and s.create_date>= ?" + paramsNo);
            params.put(paramsNo, sysOperationLogDTO.getCreateDateStart());
            paramsNo++;
        }
        if (sysOperationLogDTO.getCreateDateEnd()!=null && !"".equals(sysOperationLogDTO.getCreateDateEnd())) {
            querySqlCondition.append(" and s.create_date<= ?" + paramsNo);
            params.put(paramsNo, sysOperationLogDTO.getCreateDateEnd());
            paramsNo++;
        }

        sql.append(querySqlCondition.toString());
        sql.append(" order by s.create_date desc");
        sql.append(" limit " + (page-1)*rows + "," + rows + "");

        List<SysOperationLog> listInfo = (List<SysOperationLog>)sysOperationLogRepository.queryBySql(sql.toString(), params, SysOperationLog.class);
        //设置操作人userCode、userName
        List<SysOperationLogDTO> listDto = new ArrayList<SysOperationLogDTO>();
        if(listInfo!=null&&listInfo.size()>0) {
            for (SysOperationLog info : listInfo) {
                SysOperationLogDTO dto = SysOperationLogDTO.toDTO(info);
                UserInfo userInfo = userInfoRepository.findById(dto.getUserId()).get();
                dto.setUserCode(userInfo.getUserCode());
                dto.setUserName(userInfo.getUserName());
                listDto.add(dto);
            }
        }

        long total = sysOperationLogRepository.queryCountBySql("select count(*) " + centreSql +querySqlCondition.toString(), params);
        Page<SysOperationLogDTO> pageList = new PageImpl<>(listDto, new PageRequest(page-1, rows, null), total);
        return pageList;
    }
}
