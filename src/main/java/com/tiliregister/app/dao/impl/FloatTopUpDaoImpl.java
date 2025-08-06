package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.FloatTopUpDao;
import com.tiliregister.app.model.FloatTopUp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class FloatTopUpDaoImpl implements FloatTopUpDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FloatTopUp save(FloatTopUp floatTopUp) {
        if(floatTopUp.getId() == null){
            entityManager.persist(floatTopUp);
            return floatTopUp;
        } else {
            return entityManager.merge(floatTopUp);
        }
    }

    @Override
    public FloatTopUp findById(Long id) {
        return entityManager.find(FloatTopUp.class, id);
    }

    @Override
    public List<FloatTopUp> findByTillId(Long tillId) {
        return entityManager.createQuery("SELECT ftu FROM FloatTopUp ftu WHERE ftu.till.id = :tillId", FloatTopUp.class)
                .setParameter("tillId", tillId)
                .getResultList();
    }

    @Override
    public List<FloatTopUp> findByTillName(String tillName) {
        return entityManager.createQuery("SELECT ftu FROM FloatTopUp ftu WHERE ftu.till.name = :tillName", FloatTopUp.class)
                .setParameter("tillName", tillName)
                .getResultList();
    }

    @Override
    public List<FloatTopUp> findByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery("SELECT ftu FROM FloatTopUp ftu WHERE ftu.voided IN :voidStatus", FloatTopUp.class)
                .setParameter("voidStatus", voidStatus)
                .getResultList();
    }

    @Override
    public Page<FloatTopUp> searchFloats(String searchToken, int page, int size, String sortField, String sortOrder, Long tillId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // === Main query ===
        CriteriaQuery<FloatTopUp> cq = cb.createQuery(FloatTopUp.class);
        Root<FloatTopUp> root = cq.from(FloatTopUp.class);
        List<Predicate> predicates = new ArrayList<>();

        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("createdAt").as(String.class)), likeToken),
                    cb.like(cb.lower(root.get("till").get("name")), likeToken),
                    cb.like(cb.lower(cb.concat(root.get("amount").as(String.class), "")), likeToken ),
                    cb.like(cb.lower(root.get("createdBy").get("surname")), likeToken),
                    cb.like(cb.lower(root.get("createdBy").get("othernames")), likeToken)
            ));
        } else {
            predicates.add(cb.equal(root.get("voided"), 0));
        }

        if (tillId != null) {
            predicates.add(cb.equal(root.get("till").get("id"), tillId));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        if (sortField != null && !sortField.isBlank()) {
            Path<?> path = root.get(sortField);
            cq.orderBy("desc".equalsIgnoreCase(sortOrder) ? cb.desc(path) : cb.asc(path));
        }

        TypedQuery<FloatTopUp> query = entityManager.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<FloatTopUp> floatTopUps = query.getResultList();

        // === Count query ===
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<FloatTopUp> countRoot = countQuery.from(FloatTopUp.class);
        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = new ArrayList<>();

        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";
            countPredicates.add(cb.or(
                    cb.like(cb.lower(countRoot.get("createdAt").as(String.class)), likeToken),
                    cb.like(cb.lower(countRoot.get("till").get("name")), likeToken),
                    cb.like(cb.lower(cb.concat(countRoot.get("amount").as(String.class), "")), likeToken ),
                    cb.like(cb.lower(countRoot.get("createdBy").get("surname")), likeToken),
                    cb.like(cb.lower(countRoot.get("createdBy").get("othernames")), likeToken)
            ));
        } else {
            countPredicates.add(cb.equal(countRoot.get("voided"), 0));
        }

        if (tillId != null) {
            countPredicates.add(cb.equal(countRoot.get("till").get("id"), tillId));
        }

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(floatTopUps, PageRequest.of(page, size), totalCount);
    }
}
