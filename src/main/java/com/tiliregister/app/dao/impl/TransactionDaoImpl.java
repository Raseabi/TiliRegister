package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.TransactionDao;
import com.tiliregister.app.model.Transaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class TransactionDaoImpl implements TransactionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Transaction save(Transaction transaction) {
        if(transaction.getId() == null){
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
}
