package com.sinosoft.repository;


import com.sinosoft.domain.SpecialInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SpecialInfoRepository extends BaseRepository<SpecialInfo,Long>{
    @Query(value = "select s.id,s.special_code,s.special_name,s.special_name_p,s.super_special,s.endflag  from specialinfo s", nativeQuery = true)
    List<Map<String, Object>> findSpecialInfo();

    @Query(value = "select * from specialinfo s where s.account = ?1 and binary s.special_code = ?2", nativeQuery = true)
    SpecialInfo findSpecialInfoBySpecialCode(String account, String specialCode);

    @Query(value = "select s.id,s.special_code,s.special_name,s.special_namep,s.super_special,s.endflag ,s.account,s.useflag,s.temp,s.createoper,s.createtime,s.updateoper,s.updatetime  from specialinfo s where s.id = ?1 ", nativeQuery = true)
    SpecialInfo findSpecialInfoById(int id);

    @Query(value = "select s.id,s.special_code,s.special_name,s.special_namep,s.super_special,s.endflag ,s.account from specialinfo s where s.special_code = ?1 and s.account = ?2", nativeQuery = true)
    List<Map<String, Object>> findSpecialInfoByCodeAndAccount(String specialCode,String account);
   /* @Query(value = "update special s set s.special_code = ?1,s.special_name= ?2,s.special_namep = ?3,s.super_special = ?4,s.endflag = ?5,s.temp = ?6 where s.id = ?7")
    List<Map<String, Object>> updateSpecial(String sc,String sn,String snp,String ss,String ef,String tm,long id);
*/

    @Query(value = "select s.id AS id,s.special_code as code,s.special_namep AS text  from specialinfo s where (s.super_special like \"\" or s.super_special is null) AND s.account = ?1 order by s.id", nativeQuery = true)
    List<Map<String, Object>> findSuperSpecial(String accBookCode);

    /**
     * 按层级结构查询非一级专项信息（只查询菜单ID(id)、菜单名称(text)、菜单URL(url)）
     * @param superMenu
     * @return
     */
    @Query(value = "select s.id as id ,s.special_code as code,s.special_namep AS text from specialinfo s where super_special=?1 AND s.account = ?2 order by s.id", nativeQuery = true)
    List<Map<String, Object>> findChildrenBySuperSpecial(Integer superMenu,String accBookCode);

    /**
     * 获取录入人下拉列表
     * @return
     */
    @Query(value = "select u.id as value,u.user_code as text from userinfo u",nativeQuery = true)
    List<Map<String,Object>> findUser();


    /**
     * 查询所有父级转向的id 和 对应的SpecialCode
     * @return
     */
    @Query(value = "select s.id as VALUE ,s.special_code as text FROM specialinfo s where s.id in(select s.super_special as text FROM specialinfo s where s.super_special not like \"\" and s.account = ?1)",nativeQuery = true)
    List<Map<String,Object>> findSuperSpecialList(String accBookCode);

    /**
     * 通过id查询查询子专项的个数
     * @param id
     * @return
     */
    @Query(value = "select COUNT(*) from specialinfo s where s.super_special = ?",nativeQuery = true)
    int countChildNum(long id);
    /**
     * 编辑或删除时判断凭证使用
     * @param segentCol
     * @return
     */
    @Query(value = "select segment_flag from accsegmentdefine  where segment_col like ?1",nativeQuery = true)
    String findSOx(String segentCol);
    /**
     * 财产险
     * 通过专项代码、账套编码查询专项id
     * @param specialCode
     * @return
     */
    @Query(value = "SELECT s.id FROM specialinfo s WHERE s.special_code = ?1 AND s.account = ?2 ",nativeQuery = true)
    int getSpecialId(String specialCode,String account);

    /**
     *
     * 功能描述: 通过账户编码和专项编码查询专项信息
     *
     */
    @Query(value = "select * from specialinfo where account= ?1 and special_code= ?2 ",nativeQuery = true)
    List<SpecialInfo> querySpecialInfoByAccountAndSpecialCode(String account,String specialCode);


    //先查询所以一级专项
    @Query(value = "SELECT * FROM specialinfo s WHERE 1=1 AND s.endflag='1' AND s.useflag='1' AND (s.super_special IS NULL OR s.super_special='') AND s.account= ?1 ",nativeQuery = true)
    List<?> queryLevelOneSpecial(String accunt);

    //根据专项查询专项存放位置
    @Query(value = " select * from specialinfo where special_code = ?1 ",nativeQuery = true)
    List<?> querySpecialInfoBySpecialCode(String specialCode);

    /**
     *
     * 功能描述:    根据id 查询出special的信息
     *
     */
    @Query(value = "select * from specialinfo where id = ?1 ",nativeQuery = true)
    List<SpecialInfo> querySpecialInfoById(String id);

    /**
     *
     * 功能描述:    通过specialCode查找到出对应的SegmentFlag段位
     *
     */
    @Query(value = "select  acc.segment_flag  from specialinfo s inner join accsegmentdefine acc on s.special_code= acc.segment_col where s.account = ?1 and acc.segment_col = ?2  order by s.id",nativeQuery = true)
    String  querySegmentFlagInfoByAccount(String accBookCode,String specialCode);

    /**
     *
     * 功能描述:   通过specialCode 、super_special 、 account 确定当前S段的专项是否符合。
     *
     */
    @Query(value = "select * from specialinfo where  special_code = ?1 and super_special = ?2 and account = ?3 and useflag = '1'",nativeQuery = true)
    List<SpecialInfo> querySpecialInfoBySpecialCodeAndSuperSpecialAndAccount(String specialCode,String superSpecial,String account);

    /**
     *
     * 功能描述:  通过specialCode 、account 确定superCode信息。
     *
     */
    @Query(value = "select  super_special from specialinfo where special_code = ?1 and  account = ?2",nativeQuery = true)
    String querySpecialInfoBySpecialCodeAndAccount(String specialCode,String account);

    /**
     *
     * 功能描述:    通过账户，拿出对应的一级专项信息，放入map集合中。
     *
     */
    @Query(value = "select id as text, special_code as value from specialinfo where super_special is null and account = ?1 order by account ,id;",nativeQuery = true)
    List<Map<String,Object>> querySpecialInfoOneLevel(String account);

    /**
     *
     * 功能描述：通过部门和往来对象进行模糊查询
     *
     */
    @Query(value = "select special_code from specialinfo where special_code like ?1% AND endflag = '0'",nativeQuery = true)
    List<String> finbySpecialCondLike(String s);
}

