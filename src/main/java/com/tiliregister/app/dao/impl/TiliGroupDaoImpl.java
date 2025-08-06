package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.TiliGroupDao;
import com.tiliregister.app.model.TiliGroup;
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

@Repository
@Transactional
public class TiliGroupDaoImpl implements TiliGroupDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TiliGroup save(TiliGroup tiliGroup) {
        if (tiliGroup.getId() == null) {
            entityManager.persist(tiliGroup);
            return tiliGroup;
        } else {
            return entityManager.merge(tiliGroup);
        }
    }

    @Override
    public TiliGroup findById(Long id) {
        return entityManager.find(TiliGroup.class, id);
    }

    @Override
    public TiliGroup findByName(String name) {
        List<TiliGroup> query = entityManager.createQuery(
                        "SELECT tg FROM TiliGroup tg WHERE tg.name = :name", TiliGroup.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();
        return query.isEmpty() ? null : query.get(0);
    }

    @Override
    public List<TiliGroup> findTiliGroupByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery(
                        "SELECT tg FROM TiliGroup tg WHERE tg.voided IN :voided", TiliGroup.class)
                .setParameter("voided", voidStatus)
                .getResultList();
    }

    @Override
    public boolean tiliGroupRegistered(String tiliGroupName, Long excludeTiliGroupId) {
        String jpql = "SELECT COUNT(tg) FROM TiliGroup tg WHERE tg.name = :name";

        if (excludeTiliGroupId != null) {
            jpql += " AND tg.id <> :excludingTiliGroupId";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("name", tiliGroupName);

        if (excludeTiliGroupId != null) {
            query.setParameter("excludingTiliGroupId", excludeTiliGroupId);
        }

        Long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public Page<TiliGroup> searchTiliGroups(String searchToken, int page, int size, String sortField, String sortOrder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // === Main query ===
        CriteriaQuery<TiliGroup> cq = cb.createQuery(TiliGroup.class);
        Root<TiliGroup> tiliGroupRoot = cq.from(TiliGroup.class);

        List<Predicate> predicates = new ArrayList<>();
        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(tiliGroupRoot.get("createdAt").as(String.class)), likeToken),
                    cb.like(cb.lower(tiliGroupRoot.get("name")), likeToken),
                    cb.like(cb.lower(tiliGroupRoot.get("description")), likeToken),
                    cb.like(cb.lower(tiliGroupRoot.get("createdBy").get("surname")), likeToken),
                    cb.like(cb.lower(tiliGroupRoot.get("createdBy").get("othernames")), likeToken)
            ));
        } else {
            predicates.add(cb.equal(tiliGroupRoot.get("voided"), 0));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        if (sortField != null && !sortField.isBlank()) {
            Path<?> path = tiliGroupRoot.get(sortField);
            cq.orderBy("desc".equalsIgnoreCase(sortOrder) ? cb.desc(path) : cb.asc(path));
        }

        TypedQuery<TiliGroup> query = entityManager.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<TiliGroup> tiliGroups = query.getResultList();

        // === Count query ===
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<TiliGroup> countRoot = countQuery.from(TiliGroup.class);
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

        return new PageImpl<>(tiliGroups, PageRequest.of(page, size), totalCount);
    }
}
