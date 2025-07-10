package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.CashOutDao;
import com.tiliregister.app.model.CashOut;
import com.tiliregister.app.model.FloatTopUp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class CashOutDaoImpl implements CashOutDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public CashOut save(CashOut cashOut) {
        if(cashOut.getId() == null){
            entityManager.persist(cashOut);
            return cashOut;
        } else {
            return entityManager.merge(cashOut);
        }
    }

    @Override
    public CashOut findById(Long id) {
        return entityManager.find(CashOut.class, id);
    }

    @Override
    public List<CashOut> findByTillId(Long tillId) {
        return entityManager.createQuery("SELECT co FROM CashOut co WHERE co.till.id = :tillId", CashOut.class)
                .setParameter("tillId", tillId)
                .getResultList();
    }

    @Override
    public List<CashOut> findByTillName(String tillName) {
        return entityManager.createQuery("SELECT co FROM CashOut co WHERE co.till.name = :tillName", CashOut.class)
                .setParameter("tillName", tillName)
                .getResultList();
    }

    @Override
    public List<CashOut> findByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery("SELECT co FROM CashOut co WHERE co.voided IN :voidStatus", CashOut.class)
                .setParameter("voidStatus", voidStatus)
                .getResultList();
    }
}
