package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.TillFunctionDao;
import com.tiliregister.app.model.TillFunction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class TillFunctionDaoImpl implements TillFunctionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TillFunction save(TillFunction tillFunction) {
        if (tillFunction.getId() == null) {
            entityManager.persist(tillFunction);
            return tillFunction;
        } else {
            return entityManager.merge(tillFunction);
        }
    }

    @Override
    public TillFunction findById(Long id) {
        return entityManager.find(TillFunction.class, id);
    }

    @Override
    public TillFunction findByName(String name) {
        List<TillFunction> query = entityManager.createQuery(
                        "SELECT tf FROM TillFunction tf WHERE name = :name", TillFunction.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();

        return query.isEmpty() ? null : query.get(0);

    }

    @Override
    public List<TillFunction> findByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery("SELECT tf FROM TillFunction tf WHERE voided IN :voidStatus", TillFunction.class)
                .setParameter("voidStatus", voidStatus)
                .getResultList();
    }

    @Override
    public boolean isTillFunctionUnique(String functionName, Long excludeFunctionId) {
        String jpql = "SELECT COUNT(tf) FROM TillFunction tf WHERE name = :name";
        if(excludeFunctionId != null){
            jpql += " AND tf.id <> :excludeFunctionId";
        }
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("name", functionName);

        if (excludeFunctionId != null) {
            query.setParameter("excludeFunctionId", excludeFunctionId);
        }

        Long count = query.getSingleResult();
        return count > 0;
    }
}
