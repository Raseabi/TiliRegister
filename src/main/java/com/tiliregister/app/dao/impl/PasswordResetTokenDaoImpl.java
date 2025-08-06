package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.PasswordResetTokenDao;
import com.tiliregister.app.model.PasswordResetToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class PasswordResetTokenDaoImpl implements PasswordResetTokenDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PasswordResetToken save(PasswordResetToken passwordResetToken) {
        if(passwordResetToken.getId() == null){
            entityManager.persist(passwordResetToken);
            return passwordResetToken;
        } else {
            return entityManager.merge(passwordResetToken);
        }
    }

    @Override
    public PasswordResetToken findById(Long id) {
        return entityManager.find(PasswordResetToken.class, id);
    }

    @Override
    public PasswordResetToken findByToken(String token) {
         List<PasswordResetToken> query = entityManager.createQuery("SELECT prt FROM PasswordResetToken prt WHERE prt.token = :token", PasswordResetToken.class)
                .setParameter("token", token)
                 .setMaxResults(1)
                .getResultList();

        return query.isEmpty() ? null : query.get(0);
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            PasswordResetToken token = findById(id);
            if (token == null) {
                return false;
            }
            entityManager.remove(entityManager.contains(token) ? token : entityManager.merge(token));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete password reset token with ID " + id, e);
        }
    }
}
