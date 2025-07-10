package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.UserDao;
import com.tiliregister.app.model.Role;
import com.tiliregister.app.model.User;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@Repository  // Spring annotation to mark this as a data access component
@Transactional
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }

    @Override
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User findByUsername(String username) {
        return entityManager.createQuery(
                        "SELECT DISTINCT u FROM User u " +
                                "LEFT JOIN FETCH u.userRoles ur " +
                                "LEFT JOIN FETCH ur.role r " +
                                "LEFT JOIN FETCH r.rolePermissions rp " +
                                "LEFT JOIN FETCH rp.permission p " +
                                "WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    @Override
    public User findByEmailAddress(String emailAddress) {
        List<User> query = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.emailAddress = :emailAddress", User.class)
                .setParameter("emailAddress", emailAddress)
                .setMaxResults(1)
                .getResultList();

        return query.isEmpty() ? null : query.get(0);
    }

    @Override
    public List<User> findByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.voided IN :voided", User.class)
                .setParameter("voided", voidStatus)
                .getResultList();
    }

    @Override
    public boolean isEmailUnique(String emailAddress, Long excludeUserId) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.emailAddress = :emailAddress";

        if (excludeUserId != null) {
            jpql += " AND u.id <> :excludeUserId";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("emailAddress", emailAddress);

        if (excludeUserId != null) {
            query.setParameter("excludeUserId", excludeUserId);
        }

        Long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public boolean isUsernameUnique(String username, Long excludeUserId) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.username = :username";

        if (excludeUserId != null) {
            jpql += " AND u.id <> :excludeUserId";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("username", username);

        if (excludeUserId != null) {
            query.setParameter("excludeUserId", excludeUserId);
        }

        Long count = query.getSingleResult();
        return count > 0;
    }

}
