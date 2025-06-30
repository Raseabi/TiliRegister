package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.PermissionDao;
import com.tiliregister.app.model.Permission;
import com.tiliregister.app.model.Permission;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository  // Spring annotation to mark this as a data access component
@Transactional
public class PermissionDaoImpl implements PermissionDao {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Permission save(Permission permission) {
        if (permission.getId() == null) {
            entityManager.persist(permission);
            return permission;
        } else {
            return entityManager.merge(permission);
        }
    }

    @Override
    public Permission findById(Long id) {
        return entityManager.find(Permission.class, id);
    }

    @Override
    public List<Permission> findAll() {
        return entityManager.createQuery("from Permission", Permission.class).getResultList();
    }
}
