package com.sinosoft.service.impl.fixedassets;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.fixedassets.AssetType;
import com.sinosoft.dto.fixedassets.AssetTypeDTO;
import com.sinosoft.repository.fixedassets.AssettypeRepository;
import com.sinosoft.service.fixedassets.AssettypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 固定资产类别编码
 */
@Service
public class AssettypeServiceImpl implements AssettypeService {

    @Resource
    private AssettypeRepository assettypeRepository ;

    @Override
    public Page<AssetTypeDTO> qryAssettype(int page, int rows, AssetType assetType) {
        Page<AssetType> result ;

        List<AssetTypeDTO> dtoList = new ArrayList<>();

         result = assettypeRepository.findAll(new PageRequest((page - 1), rows));
         List<AssetType> list = result.getContent();
         if(list != null && list.size()>0){
             for(AssetType ass:list){
                 AssetTypeDTO dto = AssetTypeDTO.toDTO(ass);
                 dtoList.add(dto);
             }
         }
         Page<AssetTypeDTO> pagelist = new PageImpl<>(dtoList,result.getPageable(),result.getTotalElements());
        return pagelist;
    }

    @Override
    public AssetTypeDTO qryAssettypeById(long id) {
        AssetType ass = assettypeRepository.getOne(id);
        AssetTypeDTO dto = AssetTypeDTO.toDTO(ass);
        return dto;
    }

    @Override
    public List<?> qrySuperCode() {
        List resultList=new ArrayList();
        //获取所有上级编码为空也就是最外层的编码
        List<Map<String, Object>> list = assettypeRepository.findSuperAssettypeCode();
        //遍历查询一级编码的子编码
        for (Object obj : list) {
            Map map = new HashMap();
            map.putAll((Map) obj);
            List list2=qryChildCode((Integer) map.get("id"));
            if(list2!=null){
                map.put("children",list2);
            }
            resultList.add(map);
        }
        return resultList;
    }
    //获取一级编码下的子编码
    public List<AssetType> qryChildCode(Integer id){
        List list1=new ArrayList();
        List<?> list= assettypeRepository.findChildAssettypeCode(id);
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map= new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildCode((Integer) map.get("id"));
                if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                    map.put("id",map.get("id"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                }
                map.put("id",map.get("id"));
                map.put("text",map.get("text"));
                list1.add(map);
            }
        }
        return list1;
    }

    @Override
    public String saveAssettype(AssetType assetType) {
        try{
            System.out.println("assetTypeCode:"+assetType.getAssettypeCode());
            assetType.setCodeType("21");
            assetType.setCreateBy(CurrentUser.getCurrentUser().getId() + "");
            assetType.setCreateTime(CurrentTime.getCurrentTime());
            if(assettypeRepository.findByAssettypeCode(assetType.getAssettypeCode()).size()>0) {
                System.out.println("size:" + assettypeRepository.findByAssettypeCode(assetType.getAssettypeCode()).size());
                return ASSETTYPE_ISEXISTE;
            }
            else{
                assettypeRepository.save(assetType);
                return "success";
            }
        }catch (Exception e){
            return "error";
        }

    }

    @Override
    public String deleteAssettype(long id) {
        try{
            assettypeRepository.deleteById(id);
            return "success";
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

}
