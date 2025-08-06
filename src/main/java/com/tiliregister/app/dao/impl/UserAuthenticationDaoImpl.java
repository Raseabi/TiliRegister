package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.UserAuthenticationDao;
import com.tiliregister.app.model.UserAuthentication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public class UserAuthenticationDaoImpl implements UserAuthenticationDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserAuthentication save(UserAuthentication userAuthentication) {
        if(userAuthentication.getId() == null){
            entityManager.persist(userAuthentication);
            return userAuthentication;
        } else {
            return entityManager.merge(userAuthentication);
        }
    }

    @Override
    public UserAuthentication findById(Long id) {
        return entityManager.find(UserAuthentication.class, id);
    }

    @Override
    public UserAuthentication findByUserId(Long userId) {
        List<UserAuthentication> query = entityManager
                .createQuery("SELECT ua FROM UserAuthentication ua WHERE ua.user.id = :userId", UserAuthentication.class)
                .setParameter("userId", userId)
                .setMaxResults(1)
                .getResultList();

        return query.isEmpty() ? null : query.get(0);
    }
}
