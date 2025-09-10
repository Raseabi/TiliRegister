package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.CurrencySettingDao;
import com.tiliregister.app.model.CurrencySetting;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class CurrencySettingDaoImpl implements CurrencySettingDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public CurrencySetting save(CurrencySetting currencySetting) {
        if(currencySetting.getId() == null){
            entityManager.persist(currencySetting);
            return currencySetting;
        } else {
            return entityManager.merge(currencySetting);
        }
    }

    @Override
    public CurrencySetting findById(Long id) {
        return entityManager.find(CurrencySetting.class, id);
    }
}
