package com.sinosoft.repository;

import com.sinosoft.domain.RoleInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Repository
public interface RoleInfoRepository extends  BaseRepository<RoleInfo, Long> {

    List<?> findByRoleCode(String roleCode);

    @Query(value = "select r.id as id, r.role_name as text, ur.id as rid from roleinfo r left join userbranchaccount uba on uba.id = ?1 left join userrole ur on ur.role_id = r.id and ur.user_id = uba.user_id and ur.branch_id = uba.branch_id and ur.account_id = uba.account_id order by r.id", nativeQuery = true)
    List<Map<String, Object>> findByUserBAId(BigInteger userBAId);
}
