package com.sinosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/6 18:39
 * @Description: 自定义工厂类
 */
public class BaseRepositoryFactoryBean<T extends JpaRepository<S, ID>, S,ID extends Serializable>
    extends JpaRepositoryFactoryBean<T, S, ID> {

    public BaseRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new BaseRepositoryFactory(em);
    }

    //创建一个内部类，该类不用在外部访问
    private static class BaseRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {


        public BaseRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
        }

        @Override
        @SuppressWarnings({"unchecked"})
        protected SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager){
            return new BaseRepositoryImpl<T, ID>((Class<T>) information.getDomainType(), entityManager);
        }


        //设置具体的实现类的class
        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return BaseRepositoryImpl.class;
        }
    }
}
