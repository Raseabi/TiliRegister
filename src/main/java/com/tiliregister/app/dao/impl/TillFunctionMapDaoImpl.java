package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.TillFunctionMapDao;
import com.tiliregister.app.model.TillFunctionMap;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class TillFunctionMapDaoImpl implements TillFunctionMapDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TillFunctionMap save(TillFunctionMap tillFunctionMap) {
        if (tillFunctionMap.getId() == null) {
            entityManager.persist(tillFunctionMap);
            return tillFunctionMap;
        } else {
            return entityManager.merge(tillFunctionMap);
        }
    }

    @Override
    public TillFunctionMap findById(Long id) {
        return entityManager.find(TillFunctionMap.class, id);
    }

    @Override
    public TillFunctionMap findByTillAndFunctionIds(Long tillId, Long functionId) {
        List<TillFunctionMap> query = entityManager.createQuery(
                        "SELECT tfm FROM TillFunctionMap tfm WHERE tfm.till.id = :tillId AND tfm.tillFunction.id = :functionId", TillFunctionMap.class)
                .setParameter("tillId", tillId)
                .setParameter("tillFunction", functionId)
                .setMaxResults(1)
                .getResultList();
        return query.isEmpty() ? null : query.get(0);

    }

    @Override
    public List<TillFunctionMap> findByTillId(Long tillId) {
        return entityManager.createQuery(
                        "SELECT tfm FROM TillFunctionMap tfm WHERE tfm.till.id = :tillId", TillFunctionMap.class)
                .setParameter("tillId", tillId)
                .getResultList();
    }

    @Override
    public List<TillFunctionMap> findByFunctionId(Long functionId) {
        return entityManager.createQuery(
                        "SELECT tfm FROM TillFunctionMap tfm WHERE tfm.tillFunction.id = :functionId", TillFunctionMap.class)
                .setParameter("tillFunction", functionId)
                .getResultList();
    }

    @Override
    public boolean deleteById(Long id) {
        TillFunctionMap tillFunctionMap = findById(id);

        if(tillFunctionMap != null){
            entityManager.remove(tillFunctionMap);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteByTillAndFunctionIds(Long tillId, Long functionId) {
        TillFunctionMap tillFunctionMap = findByTillAndFunctionIds(tillId, functionId);

        if(tillFunctionMap != null){
            entityManager.remove(tillFunctionMap);
            return true;
        } else {
            return false;
        }
    }
}
