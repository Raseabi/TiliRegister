package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.RolePermissionDao;
import com.tiliregister.app.model.RolePermission;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public class RolePermissionDaoImpl implements RolePermissionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public RolePermission save(RolePermission rolePermission) {
        if (rolePermission.getId() == null) {
            entityManager.persist(rolePermission);
            return rolePermission;
        } else {
            return entityManager.merge(rolePermission);
        }
    }

    @Override
    public RolePermission findById(Long id) {
        return entityManager.find(RolePermission.class, id);
    }

    @Override
    public List<RolePermission> findByRoleId(Long roleId) {
        return entityManager.createQuery(
                        "SELECT rp FROM RolePermission rp WHERE rp.role.id = :roleId"
                        , RolePermission.class)
                .setParameter("roleId", roleId)
                .getResultList();
    }

    @Override
    public RolePermission findByRoleAndPermissionIds(Long roleId, Long permissionId) {
        List<RolePermission> result = entityManager.createQuery(
                        "SELECT rp FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.permission.id = :permissionId",
                        RolePermission.class)
                .setParameter("roleId", roleId)
                .setParameter("permissionId", permissionId)
                .setMaxResults(1)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }


    @Override
    public List<RolePermission> findByPermissionId(Long permissionId) {
        return entityManager.createQuery(
                        "SELECT rp FROM RolePermission rp WHERE rp.permission.id = :permissionId"
                        , RolePermission.class)
                .setParameter("permissionId", permissionId)
                .getResultList();
    }

    @Override
    public boolean deleteById(Long id) {
        RolePermission rolePermission = findById(id);
        if (rolePermission != null) {
            entityManager.remove(rolePermission);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteByRoleAndPermissionIds(Long roleId, Long permissionId) {
        RolePermission rolePermission = findByRoleAndPermissionIds(roleId, permissionId);

        if (rolePermission != null) {
            entityManager.remove(rolePermission);
            return true;
        } else {
            return false;
        }
    }
}
