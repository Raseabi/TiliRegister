package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.RoleDao;
import com.tiliregister.app.model.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Role save(Role role) {
        if (role.getId() == null) {
            entityManager.persist(role);
            return role;
        } else {
            return entityManager.merge(role);
        }
    }

    @Override
    public Role findById(Long id) {
        return entityManager.find(Role.class, id);
    }

    @Override
    public Role findByName(String name) {
        List<Role> result = entityManager.createQuery(
                        "SELECT r FROM Role r WHERE r.name = :name", Role.class)
                .setParameter("name", name)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Role> findByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery(
                        "SELECT r FROM Role r WHERE r.voided IN :voided", Role.class)
                .setParameter("voided", voidStatus)
                .getResultList();
    }

    @Override
    public boolean doesRoleExist(String roleName, Long excludeRoleId) {
        String jpql = "SELECT COUNT(r) FROM Role r WHERE r.name = :name";

        if (excludeRoleId != null) {
            jpql += " AND r.id <> :excludeRoleId";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("name", roleName);

        if (excludeRoleId != null) {
            query.setParameter("excludeRoleId", excludeRoleId);
        }

        Long count = query.getSingleResult();
        return count > 0;
    }

}
