package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.FloatTopUpDao;
import com.tiliregister.app.model.FloatTopUp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class FloatTopUpDaoImpl implements FloatTopUpDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FloatTopUp save(FloatTopUp floatTopUp) {
        if(floatTopUp.getId() == null){
            entityManager.persist(floatTopUp);
            return floatTopUp;
        } else {
            return entityManager.merge(floatTopUp);
        }
    }

    @Override
    public FloatTopUp findById(Long id) {
        return entityManager.find(FloatTopUp.class, id);
    }

    @Override
    public List<FloatTopUp> findByTillId(Long tillId) {
        return entityManager.createQuery("SELECT ftu FROM FloatTopUp ftu WHERE ftu.till.id = :tillId", FloatTopUp.class)
                .setParameter("tillId", tillId)
                .getResultList();
    }

    @Override
    public List<FloatTopUp> findByTillName(String tillName) {
        return entityManager.createQuery("SELECT ftu FROM FloatTopUp ftu WHERE ftu.till.name = :tillName", FloatTopUp.class)
                .setParameter("tillName", tillName)
                .getResultList();
    }

    @Override
    public List<FloatTopUp> findByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery("SELECT ftu FROM FloatTopUp ftu WHERE ftu.voided IN :voidStatus", FloatTopUp.class)
                .setParameter("voidStatus", voidStatus)
                .getResultList();
    }
}
