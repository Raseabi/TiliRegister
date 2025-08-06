package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.TillFunctionDao;
import com.tiliregister.app.model.TillFunction;
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
public class TillFunctionDaoImpl implements TillFunctionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TillFunction save(TillFunction tillFunction) {
        if (tillFunction.getId() == null) {
            entityManager.persist(tillFunction);
            return tillFunction;
        } else {
            return entityManager.merge(tillFunction);
        }
    }

    @Override
    public TillFunction findById(Long id) {
        return entityManager.find(TillFunction.class, id);
    }

    @Override
    public TillFunction findByName(String name) {
        List<TillFunction> query = entityManager.createQuery(
                        "SELECT tf FROM TillFunction tf WHERE name = :name", TillFunction.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();

        return query.isEmpty() ? null : query.get(0);

    }

    @Override
    public List<TillFunction> findByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery("SELECT tf FROM TillFunction tf WHERE voided IN :voidStatus", TillFunction.class)
                .setParameter("voidStatus", voidStatus)
                .getResultList();
    }

    @Override
    public boolean isTillFunctionUnique(String functionName, Long excludeFunctionId) {
        String jpql = "SELECT COUNT(tf) FROM TillFunction tf WHERE name = :name";
        if(excludeFunctionId != null){
            jpql += " AND tf.id <> :excludeFunctionId";
        }
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("name", functionName);

        if (excludeFunctionId != null) {
            query.setParameter("excludeFunctionId", excludeFunctionId);
        }

        Long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public Page<TillFunction> searchTillFunctions(String searchToken, int page, int size, String sortField, String sortOrder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // === Main query ===
        CriteriaQuery<TillFunction> cq = cb.createQuery(TillFunction.class);
        Root<TillFunction> tillFunctionRoot = cq.from(TillFunction.class);

        List<Predicate> predicates = new ArrayList<>();
        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(tillFunctionRoot.get("createdAt").as(String.class)), likeToken),
                    cb.like(cb.lower(tillFunctionRoot.get("name")), likeToken),
                    cb.like(cb.lower(tillFunctionRoot.get("description")), likeToken),
                    cb.like(cb.lower(tillFunctionRoot.get("createdBy").get("surname")), likeToken),
                    cb.like(cb.lower(tillFunctionRoot.get("createdBy").get("othernames")), likeToken)
            ));
        }else {
            predicates.add(cb.equal(tillFunctionRoot.get("voided"), 0));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        if (sortField != null && !sortField.isBlank()) {
            Path<?> path = tillFunctionRoot.get(sortField);
            cq.orderBy("desc".equalsIgnoreCase(sortOrder) ? cb.desc(path) : cb.asc(path));
        }

        TypedQuery<TillFunction> query = entityManager.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<TillFunction> tillFunctions = query.getResultList();

        // === Count query ===
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<TillFunction> countRoot = countQuery.from(TillFunction.class);
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

        return new PageImpl<>(tillFunctions, PageRequest.of(page, size), totalCount);

    }
}
