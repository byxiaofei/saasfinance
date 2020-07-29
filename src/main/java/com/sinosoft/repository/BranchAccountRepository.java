package com.sinosoft.repository;

import com.sinosoft.domain.BranchAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchAccountRepository extends BaseRepository<BranchAccount, Integer> {
    @Query(value = "select b from BranchAccount b where b.accountInfo.id = ?1")
    List<?> findBranchAccountByAccountId(Integer accountId);
}
