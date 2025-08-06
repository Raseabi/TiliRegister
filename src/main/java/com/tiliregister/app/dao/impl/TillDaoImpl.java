package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.TillDao;
import com.tiliregister.app.model.Till;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class TillDaoImpl implements TillDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Till save(Till till) {
        if (till.getId() == null) {
            entityManager.persist(till);
            return till;
        } else {
            return entityManager.merge(till);
        }
    }

    @Override
    public Till findById(Long id) {
        return entityManager.find(Till.class, id);
    }

    @Override
    public Till findByName(String name) {
        List<Till> query = entityManager.createQuery(
                        "SELECT t from Till t WHERE name = :name", Till.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();

        return query.isEmpty() ? null : query.get(0);
    }

    @Override
    public List<Till> findByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery(
                        "SELECT t FROM Till t WHERE voided IN :voidStatus", Till.class)
                .setParameter("voidStatus", voidStatus)
                .getResultList();
    }

    @Override
    public List<Till> findByTiliGroupId(Long tiliGroupId) {
        return entityManager.createQuery("SELECT t FROM Till t WHERE t.tiliGroup.id = :tiliGroupId", Till.class)
                .setParameter("tiliGroupId", tiliGroupId)
                .getResultList();
    }

    @Override
    public boolean isTillUnique(String name, Long excludeTillId) {
        String jpql = "SELECT COUNT(t) FROM Till t WHERE t.name = :name";
        if (excludeTillId != null) {
            jpql += " AND t.id <> :excludeTillId";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("name", name);
        if (excludeTillId != null) {
            query.setParameter("excludeTillId", excludeTillId);
        }

        Long count = query.getSingleResult();
        return count > 0;
    }
}
