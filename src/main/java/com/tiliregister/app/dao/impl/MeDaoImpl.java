package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.MeDao;
import com.tiliregister.app.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class MeDaoImpl implements MeDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User findMeById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User updateMe(User user) {
        return entityManager.merge(user);
    }
}
