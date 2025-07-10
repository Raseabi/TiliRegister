package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.GlobalConfigDao;
import com.tiliregister.app.model.GlobalConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class GlobalConfigDaoImpl implements GlobalConfigDao {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public GlobalConfig save(GlobalConfig globalConfig) {
        return entityManager.merge(globalConfig);
    }

    @Override
    public GlobalConfig findById(Long id) {
        return entityManager.find(GlobalConfig.class, id);
    }

    @Override
    public GlobalConfig findByConfigKey(String configKey) {
        List<GlobalConfig> results = entityManager.createQuery(
                        "SELECT gc FROM GlobalConfig gc WHERE gc.configKey = :configKey", GlobalConfig.class)
                .setParameter("configKey", configKey)
                .setMaxResults(1)
                .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<GlobalConfig> findAll() {
        return entityManager.createQuery("from GlobalConfig", GlobalConfig.class)
               .getResultList();
    }
}
