package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.RoleDao;
import com.tiliregister.app.model.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public Page<Role> searchRoles(String searchToken, int page, int size, String sortField, String sortOrder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // === Main query ===
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);
        Root<Role> roleRoot = cq.from(Role.class);

        List<Predicate> predicates = new ArrayList<>();
        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(roleRoot.get("createdAt").as(String.class)), likeToken),
                    cb.like(cb.lower(roleRoot.get("name")), likeToken),
                    cb.like(cb.lower(roleRoot.get("description")), likeToken),
                    cb.like(cb.lower(roleRoot.get("createdBy").get("surname")), likeToken),
                    cb.like(cb.lower(roleRoot.get("createdBy").get("othernames")), likeToken)
            ));
        }else {
            predicates.add(cb.equal(roleRoot.get("voided"), 0));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        if (sortField != null && !sortField.isBlank()) {
            Path<?> path = roleRoot.get(sortField);
            cq.orderBy("desc".equalsIgnoreCase(sortOrder) ? cb.desc(path) : cb.asc(path));
        }

        TypedQuery<Role> query = entityManager.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<Role> roles = query.getResultList();

        // === Count query ===
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Role> countRoot = countQuery.from(Role.class);
        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = new ArrayList<>();
        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";
            countPredicates.add(cb.or(
                    cb.like(cb.lower(countRoot.get("createdAt").as(String.class)), likeToken),
                    cb.like(cb.lower(countRoot.get("name")), likeToken),
                    cb.like(cb.lower(countRoot.get("description")), likeToken),
                    cb.like(cb.lower(countRoot.get("createdBy").get("surname")), likeToken),
                    cb.like(cb.lower(countRoot.get("createdBy").get("othernames")), likeToken)
            ));
        } else {
            countPredicates.add(cb.equal(countRoot.get("voided"), 0));
        }

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(roles, PageRequest.of(page, size), totalCount);
    }
}
