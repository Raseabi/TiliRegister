package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.UserRoleDao;
import com.tiliregister.app.model.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class UserRoleDaoImpl implements UserRoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserRole save(UserRole userRole) {
        if (userRole.getId() == null) {
            this.entityManager.persist(userRole);
            return userRole;
        } else {
            return this.entityManager.merge(userRole);
        }
    }

    @Override
    public UserRole findById(Long id) {
        return entityManager.find(UserRole.class, id);
    }

    @Override
    public UserRole findByUserAndRoleIds(Long userId, Long roleId) {
        List<UserRole> result = entityManager.createQuery(
                        "SELECT ur FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.id = :roleId", UserRole.class)
                .setParameter("userId", userId)
                .setParameter("roleId", roleId)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<UserRole> findByUserId(Long userId) {
        return entityManager.createQuery(
                        " SELECT ur FROM UserRole ur WHERE ur.user.id = :userId", UserRole.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<UserRole> findByRoleId(Long roleId) {
        return entityManager.createQuery(
                        " SELECT ur FROM UserRole ur WHERE ur.role.id = :roleId", UserRole.class)
                .setParameter("roleId", roleId)
                .getResultList();
    }

    @Override
    public boolean deleteById(Long id) {
        UserRole userRole = findById(id);
        if (userRole != null) {
            entityManager.remove(userRole);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteByUserAndRoleIds(Long userId, Long roleId) {
        UserRole userRole = findByUserAndRoleIds(userId, roleId);

        if (userRole != null) {
            entityManager.remove(userRole);
            return true;
        } else {
            return false;
        }
    }
}
