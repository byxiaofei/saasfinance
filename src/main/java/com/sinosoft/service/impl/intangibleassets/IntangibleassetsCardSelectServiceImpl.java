package com.sinosoft.service.impl.intangibleassets;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.fixedassets.AccDepre;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;
import com.sinosoft.repository.fixedassets.AccAssetCodeTypeRepository;
import com.sinosoft.repository.fixedassets.AccAssetInfoRepository;
import com.sinosoft.repository.fixedassets.AccAssetSelectRepository;
import com.sinosoft.repository.intangibleassets.IntangibleAccAssetSelectRepository;
//import com.sinosoft.service.fixedassets.fixedassetsselect.FixedassetsCardSelectService;
import com.sinosoft.service.intangibleassets.IntangibleassetsCardSelectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @authorzst
 * @Description
 * @create 2019-03-29 15:10
 * 无形资产 查询
 */
@Service
public class IntangibleassetsCardSelectServiceImpl implements IntangibleassetsCardSelectService {
    private Logger logger = LoggerFactory.getLogger(IntangibleassetsCardSelectServiceImpl.class);
    @Resource
    private IntangibleAccAssetSelectRepository intangibleAccAssetSelectRepository;

    /*
    * 固定资产卡片查询 台账查询
    * */
    @Override
    public List<?> getAssetDDepre(IntangibleAccAssetInfoDTO acc){
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
       List<?> list= intangibleAccAssetSelectRepository.getIntangibleAccDepre(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getAssetType(),String.valueOf(acc.getAssetCode()));
        List<Map<String,Object>> assetValue=intangibleAccAssetSelectRepository.getAccAssetValue(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getCardCode());
        Map<String,Object> info=assetValue.get(0);
          List list1=new ArrayList();
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map = new HashMap();
                map.putAll((Map) obj);
             /*   //本月累计折旧月数和累计金额
               Map<String,BigDecimal> count= intangibleAccAssetSelectRepository.getCount(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getAssetType(),String.valueOf(acc.getAssetCode()),String.valueOf(map.get("year_month_data")));

                //上月折旧日期
                String month=String.valueOf(map.get("year_month_data"));
                if(month.substring(4).equals("01")){
                    month=(Integer.parseInt(month.substring(0,4))-1)+"12";
                }else{
                    if(Integer.parseInt(month.substring(4))>10){
                        month=month.substring(0,4)+(Integer.parseInt(month.substring(4))-1);
                    }else{
                        month=month.substring(0,4)+"0"+(Integer.parseInt(month.substring(5))-1);
                    }
                }
                //上月累计折旧月数和累计金额
                Map<String,BigDecimal> countEnd= intangibleAccAssetSelectRepository.getCount(centerCode,branchCode,accBookType,accBookCode,acc.getCodeType(),acc.getAssetType(),String.valueOf(acc.getAssetCode()),month);
                if(countEnd.get("allDepreQuantity")==null){
                    countEnd.put("allDepreQuantity",new BigDecimal("0"));
                }
                if(countEnd.get("allDepreMoney")==null){
                    countEnd.put("allDepreMoney",new BigDecimal("0"));
                }
                if(count.get("allDepreQuantity")==null){
                    count.put("allDepreQuantity",new BigDecimal("0"));
                }
                if(count.get("allDepreMoney")==null){
                    count.put("allDepreMoney",new BigDecimal("0"));
                }*/
                //本月计提月数
                map.put("thisMonthDrepre", map.get("month_depre_quantity")==null?"0":map.get("month_depre_quantity"));
                //本月计提折旧金额
                map.put("month_depre_money", map.get("month_depre_money")==null?"0.00":map.get("month_depre_money"));

                //期末累计折旧月份
                map.put("endDepreAmount",map.get("all_depre_quantity")==null?"0":map.get("all_depre_quantity"));
                map.put("assetOriginValue",info.get("assetOriginValue")==null?"0.00":info.get("assetOriginValue"));
                map.put("assetNetValue",map.get("current_net_value")==null?"0.00":map.get("current_net_value"));
                map.put("impairment",info.get("impairment")==null?"0.00":info.get("impairment"));
                BigDecimal endDepreMoney=new BigDecimal(String.valueOf(map.get("all_depre_money"))).add(new BigDecimal(String.valueOf(info.get("initDepreMoney"))));
                map.put("endDepreMoney",endDepreMoney);
                list1.add(map);

            }
        }
        return list1;
    }



}
