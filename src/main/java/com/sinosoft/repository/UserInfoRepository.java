package com.sinosoft.repository;

import com.sinosoft.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoRepository extends  BaseRepository<UserInfo, Long> {
    List<?> findByUserCode(String userCode);
}
