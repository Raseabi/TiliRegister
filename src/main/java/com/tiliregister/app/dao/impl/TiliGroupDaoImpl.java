package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.TiliGroupDao;
import com.tiliregister.app.model.TiliGroup;
import com.tiliregister.app.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class TiliGroupDaoImpl implements TiliGroupDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TiliGroup save(TiliGroup tiliGroup) {
        if (tiliGroup.getId() == null) {
            entityManager.persist(tiliGroup);
            return tiliGroup;
        } else {
            return entityManager.merge(tiliGroup);
        }
    }

    @Override
    public TiliGroup findById(Long id) {
        return entityManager.find(TiliGroup.class, id);
    }

    @Override
    public TiliGroup findByName(String name) {
        List<TiliGroup> query = entityManager.createQuery(
                        "SELECT tg FROM TiliGroup tg WHERE tg.name = :name", TiliGroup.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();
        return query.isEmpty() ? null : query.get(0);
    }

    @Override
    public List<TiliGroup> findTiliGroupByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery(
                        "SELECT tg FROM TiliGroup tg WHERE tg.voided IN :voided", TiliGroup.class)
                .setParameter("voided", voidStatus)
                .getResultList();
    }

    @Override
    public boolean tiliGroupRegistered(String tiliGroupName, Long excludeTiliGroupId) {
        String jpql = "SELECT COUNT(tg) FROM TiliGroup tg WHERE tg.name = :name";

        if (excludeTiliGroupId != null) {
            jpql += " AND tg.id <> :excludingTiliGroupId";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("name", tiliGroupName);

        if (excludeTiliGroupId != null) {
            query.setParameter("excludingTiliGroupId", excludeTiliGroupId);
        }

        Long count = query.getSingleResult();
        return count > 0;
    }
}
