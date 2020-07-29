package com.sinosoft.repository;

import com.sinosoft.domain.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends BaseRepository<UserRole, Integer> {

    @Query(nativeQuery = true, value = "select u.* from userrole u where u.user_id = :user_id ")
    List<UserRole> findByUserId(@Param("user_id") String user_id);

    @Query(value = "select r.* from userrole r where r.user_id = ?1 and r.branch_id = ?2 and r.account_id = ?3", nativeQuery = true)
    List<UserRole> findByUserIdAndBranchIdAndAccountId(Long userId, Integer branchId, Integer accountId);
}
