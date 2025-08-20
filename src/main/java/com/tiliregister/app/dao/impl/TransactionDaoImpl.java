package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.TransactionDao;
import com.tiliregister.app.model.Transaction;
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
public class TransactionDaoImpl implements TransactionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            entityManager.persist(transaction);
            return transaction;
        } else {
            return entityManager.merge(transaction);
        }
    }

    @Override
    public Transaction findById(Long id) {
        return entityManager.find(Transaction.class, id);
    }

    @Override
    public List<Transaction> findByTillId(Long tillId) {
        return entityManager.createQuery("SELECT tx FROM Transaction tx WHERE tx.till.id = :tillId", Transaction.class)
                .setParameter("tillId", tillId)
                .getResultList();
    }

    @Override
    public List<Transaction> findByTillName(String tillName) {
        return entityManager.createQuery("SELECT tx FROM Transaction tx WHERE tx.till.name = :tillName", Transaction.class)
                .setParameter("tillName", tillName)
                .getResultList();
    }

    @Override
    public List<Transaction> findByFunctionId(Long functionId) {
        return entityManager.createQuery("SELECT tx FROM Transaction tx WHERE tx.tillFunction.id = :functionId", Transaction.class)
                .setParameter("functionId", functionId)
                .getResultList();
    }

    @Override
    public List<Transaction> findByFunctionName(String functionName) {
        return entityManager.createQuery("SELECT tx FROM Transaction tx WHERE tx.tillFunction.name = :functionName", Transaction.class)
                .setParameter("functionName", functionName)
                .getResultList();
    }

    @Override
    public List<Transaction> findByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery("SELECT tx FROM Transaction tx WHERE voided IN :voidStatus", Transaction.class)
                .setParameter("voidStatus", voidStatus)
                .getResultList();
    }

    @Override
    public Page<Transaction> searchTransactions(String searchToken, int page, int size, String sortField, String sortOrder, Long tillId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // === Main query ===
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> root = cq.from(Transaction.class);
        List<Predicate> predicates = new ArrayList<>();

        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("till").get("name")), likeToken),
                    cb.like(cb.lower(root.get("client").get("clientName")), likeToken),
                    cb.like(cb.lower(root.get("client").get("clientNumber")), likeToken),
                    cb.like(cb.lower(cb.concat(root.get("amount").as(String.class), "")), likeToken),
                    cb.like(cb.lower(root.get("createdBy").get("surname")), likeToken),
                    cb.like(cb.lower(root.get("createdBy").get("othernames")), likeToken),
                    cb.like(cb.lower(root.get("createdBy").get("othernames")), likeToken),
                    cb.like(cb.lower(root.get("createdAt").as(String.class)), likeToken)
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

        TypedQuery<Transaction> query = entityManager.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<Transaction> transactions = query.getResultList();

        // === Count query ===
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Transaction> countRoot = countQuery.from(Transaction.class);
        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = new ArrayList<>();

        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";
            countPredicates.add(cb.or(
                    cb.like(cb.lower(countRoot.get("till").get("name")), likeToken),
                    cb.like(cb.lower(countRoot.get("client").get("clientName")), likeToken),
                    cb.like(cb.lower(countRoot.get("client").get("clientNumber")), likeToken),
                    cb.like(cb.lower(cb.concat(countRoot.get("amount").as(String.class), "")), likeToken),
                    cb.like(cb.lower(countRoot.get("createdBy").get("surname")), likeToken),
                    cb.like(cb.lower(countRoot.get("createdBy").get("othernames")), likeToken),
                    cb.like(cb.lower(countRoot.get("createdBy").get("othernames")), likeToken),
                    cb.like(cb.lower(countRoot.get("createdAt").as(String.class)), likeToken)
            ));
        } else {
            countPredicates.add(cb.equal(countRoot.get("voided"), 0));
        }

        if (tillId != null) {
            countPredicates.add(cb.equal(countRoot.get("till").get("id"), tillId));
        }

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(transactions, PageRequest.of(page, size), totalCount);
    }
}
