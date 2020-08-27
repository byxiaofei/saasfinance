package com.sinosoft.repository;

import com.sinosoft.domain.BranchInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/17 19:07
 * @Description:
 */
@Repository
public interface BranchInfoRepository extends BaseRepository<BranchInfo, Integer> {

    List<?> findByComCode(String comCode);

    @Query(value="select b.id as value, b.comName as text from BranchInfo b where b.level = ?1 and b.superCom = ?2")
    List<Map<String, Object>> findByLevel(String level, String superCom);
    //根据汇总机构id获取获取子机构 非虚拟机构的
    @Query(value="select b.comCode  from BranchInfo b where b.superCom= (SELECT b1.id FROM BranchInfo b1 WHERE b1.comCode = ?1) and b.isVirtual =?2 ")
    List<String> findBySuperCom(String superCom,String isVirtual);
    //根据汇总机构id获取获取子机构
    @Query(value="select b.comCode  from BranchInfo b where b.superCom= (SELECT b1.id FROM BranchInfo b1 WHERE b1.comCode = ?1) ")
    List<String> findBySuperCom(String superCom);
    //获取全部汇总机构 参数值为 1
    @Query(value="select b.comCode  from BranchInfo b where b.level = ?1 ")
    List<String> findByLevel(String level);

    @Query(value = "select b.id as value, b.comName as text from BranchInfo b where b.level = 1 or b.level = 2")
    List<Map<String, Object>> findByLevel();

    @Query(value = "select a.id as value,a.accountName as text from AccountInfo a left join BranchAccount b on b.accountInfo.id = a.id where b.branchInfo.id = ?1")
    List<Map<String, Object>> findAccountByBranchId(Integer id);

    /**
     * 根据机构Id检查机构下是否存在机构
     * @param id
     * @return
     */
    @Query(value = "select b.id from BranchInfo b where b.superCom = ?1")
    List<Map<String, Object>> checkExistsSubordinate(String id);

    /**
     * 检查机构编码是否已存在
     * @param comCode
     * @return
     */
    @Query(value="select id as id,flag as flag from BranchInfo where comCode = ?1")
    List<Map<String, Object>> checkExistsComCode(String comCode);

    @Query(value = "select * from branchinfo b where b.id in (?1) order by id", nativeQuery = true)
    List<BranchInfo> findByIds(String ids);

    /**
     * 根据账套ID查询
     * @param accountId
     * @return
     */
    @Query(value = "select b.id as id,b.comName as text,br.accountInfo.id as accountId from BranchInfo b left join BranchAccount br on b.id = br.branchInfo.id and br.accountInfo.id = ?1 order by b.level,b.comCode")
    List<Map<String, Object>> findBranchTreeByAccountId(Integer accountId);
    /**
     * 根据父机构和账套ID查询
     * @param accountId
     * @return
     */
    @Query(value = "select b.id as id,b.comName as text,br.accountInfo.id as accountId from BranchInfo b left join BranchAccount br on b.id = br.branchInfo.id and br.accountInfo.id = ?1 where b.superCom is null order by b.level,b.id")
    List<Map<String, Object>> findBySuperComIsNullAndAccountId(Integer accountId);
    /**
     * 按层级结构查询非一级机构信息（只查询机构ID(id)、机构名称(text)）
     * @param superCom
     * @param accountId
     * @return
     */
    @Query(value = "select b.id as id,b.comName as text,br.accountInfo.id as accountId from BranchInfo b left join BranchAccount br on b.id = br.branchInfo.id and br.accountInfo.id = ?1 where b.superCom= ?2 order by b.level,b.id")
    List<Map<String, Object>> findByHasBranchAndSuperCom(Integer accountId, Integer superCom);
}
