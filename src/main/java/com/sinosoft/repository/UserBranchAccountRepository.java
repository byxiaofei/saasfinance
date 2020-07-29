package com.sinosoft.repository;

import com.sinosoft.domain.UserBranchAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface UserBranchAccountRepository extends BaseRepository<UserBranchAccount, BigInteger> {

    @Query(nativeQuery = true, value = "select u.* from userbranchaccount u where u.user_id = :user_id order by u.branch_id,u.account_id")
    List<UserBranchAccount> findByUserId(@Param("user_id") Long user_id);

    @Query(value = "select u.* from userbranchaccount u where u.user_id = ?1 and u.branch_id = ?2 order by u.account_id", nativeQuery = true)
    List<UserBranchAccount> findByUserIdAndBranchId(Long userId, Integer branchId);
}
