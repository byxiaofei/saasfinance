package com.sinosoft.repository;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/6 18:39
 * @Description: 自定义BaseRepository接口实现类
 */
public class BaseRepositoryImpl<T, ID extends Serializable>  extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<?> queryBySql(String sql, Class clazz){
        return entityManager.createNativeQuery(sql, clazz).getResultList();
    }

    @Override
    public List<?> queryBySql(String sql, Map<Integer, Object> params, Class clazz){
        Query query = entityManager.createNativeQuery(sql, clazz);

        for (Integer key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }

        return query.getResultList();
    }

    @Override
    public List<?> queryBySql(String sql) {
        return entityManager.createNativeQuery(sql).getResultList();
    }

    @Override
    public List<?> queryBySql(String sql, Map<Integer, Object> params) {
        Query query = entityManager.createNativeQuery(sql);

        for (Integer key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }

        return query.getResultList();
    }

    @Override
    public List<?> queryBySqlS(String sql, Map<String, Object> params) {
        Query query = entityManager.createNativeQuery(sql);

        this.setParameters(query, params);

        return query.getResultList();
    }

    @Override
    public List<?> queryBySqlSC(String sql) {
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.getResultList();
    }

    @Override
    public List<?> queryBySqlSC(String sql, Map<Integer, Object> params) {
        Query query = entityManager.createNativeQuery(sql);

        for (Integer key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }

        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.getResultList();
    }

    public Page<?> queryByPage(int page, int rows,String hql, Map<String, Object> params) {
        Query query = entityManager.createQuery(hql);
        this.setParameters(query, params);
        List<?> list = query.getResultList();
        List<Object> resultList = new ArrayList<>();
        if (rows>=list.size()) {
            resultList.addAll(list);
        } else {
            int starRow = (page-1)*rows;
            int currentRow = starRow;
            for (int i=0;i<rows;i++) {
                if (currentRow<list.size()) { resultList.add(list.get(currentRow)); } else { break; }
                currentRow++;
            }
        }
        Page<?> result = new PageImpl<>(resultList, new PageRequest(page-1, rows), list.size());
        return result;
    }

    @Override
    public Page<?> queryByPageOne(int page, int rows, String hql) {
        Query query = entityManager.createNativeQuery(hql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<?> list = query.getResultList();
        List<Object> resultList = new ArrayList<>();
        if (rows>=list.size()) {
            resultList.addAll(list);
        } else {
            int starRow = (page-1)*rows;
            int currentRow = starRow;
            for (int i=0;i<rows;i++) {
                if (currentRow<list.size()) { resultList.add(list.get(currentRow)); } else { break; }
                currentRow++;
            }
        }
        Page<?> result = new PageImpl<>(resultList, new PageRequest(page-1, rows), list.size());
        return result;
    }

    @Override
    public Page<?> queryByPageOne(int page, int rows, String hql, Map<Integer, Object> params) {
        Query query = entityManager.createNativeQuery(hql);

        for (Integer key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }

        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<?> list = query.getResultList();
        List<Object> resultList = new ArrayList<>();
        if (rows>=list.size()) {
            resultList.addAll(list);
        } else {
            int starRow = (page-1)*rows;
            int currentRow = starRow;
            for (int i=0;i<rows;i++) {
                if (currentRow<list.size()) { resultList.add(list.get(currentRow)); } else { break; }
                currentRow++;
            }
        }
        Page<?> result = new PageImpl<>(resultList, new PageRequest(page-1, rows), list.size());
        return result;
    }

    /**
     * 给hql参数设置值
     * @param query 查询
     * @param params 参数
     */
    private void setParameters(Query query, Map<String,Object> params){
        for(Map.Entry<String,Object> entry:params.entrySet()){
            query.setParameter(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public long queryCountBySql(String sql) {
        return ((BigInteger)entityManager.createNativeQuery(sql).getResultList().get(0)).longValue();
    }

    @Override
    public long queryCountBySql(String sql, Map<Integer, Object> params) {
        Query query = entityManager.createNativeQuery(sql);

        for (Integer key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }
        return ((BigInteger)query.getResultList().get(0)).longValue();
    }

}
