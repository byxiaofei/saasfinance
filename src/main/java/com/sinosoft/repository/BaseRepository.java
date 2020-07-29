package com.sinosoft.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * repository 基类，封装自定义查询方法
 *
 * @author hanhuanxing
 * @date 2019/1/6 13:13
 * @Package com.base
 * @Version v1.0
 */

@NoRepositoryBean //该注解表示Spring容器不会创建该对象
public interface BaseRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID>, JpaSpecificationExecutor<T>, JpaRepository<T, ID> {

    List<?> queryBySql(String sql, Class clazz);

    List<?> queryBySql(String sql, Map<Integer, Object> params, Class clazz);

    List<?> queryBySql(String sql);

    List<?> queryBySql(String sql, Map<Integer, Object> params);

    List<?> queryBySqlS(String sql, Map<String, Object> params);

    List<?> queryBySqlSC(String sql);

    List<?> queryBySqlSC(String sql, Map<Integer, Object> params);

    Page<?> queryByPage(int page, int rows,String hql, Map<String, Object> params);

    /**
     *
     * 功能描述:    可用分页查询
     *
     */
    Page<?> queryByPageOne(int page, int rows,String hql);

    Page<?> queryByPageOne(int page, int rows,String hql, Map<Integer, Object> params);
    /**
     * 根据sql条件查询符合条件的总条数
     * @param sql
     * @return
     */
    long queryCountBySql(String sql);

    long queryCountBySql(String sql, Map<Integer, Object> params);

}
