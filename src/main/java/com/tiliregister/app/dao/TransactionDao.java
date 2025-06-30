package com.tiliregister.app.dao;

import com.tiliregister.app.model.Transaction;

import java.util.List;

public interface TransactionDao {
    Transaction save(Transaction transaction);
    Transaction findById(Long id);
    List<Transaction> findByTillId(Long tillId);
    List<Transaction> findByFunctionId(Long functionId);
    List<Transaction> findByVoidStatus(List<Integer> voidStatus);

}
