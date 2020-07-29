package com.sinosoft.repository;

import com.sinosoft.domain.RoleMenu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/11 15:54
 * @Description:
 */
@Repository
public interface RoleMenuRepository extends BaseRepository<RoleMenu, Integer> {

    @Query(value = "select r from RoleMenu r where r.roleInfo.id = ?1")
    List<?> findByRoleId(Long roleId);
}
