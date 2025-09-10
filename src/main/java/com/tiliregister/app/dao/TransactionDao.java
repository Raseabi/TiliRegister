package com.tiliregister.app.dao;

import com.tiliregister.app.model.Transaction;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionDao {
    Transaction save(Transaction transaction);
    Transaction findById(Long id);
    List<Transaction> findByTillId(Long tillId);
    List<Transaction> findByTillName(String tillName);
    List<Transaction> findByFunctionId(Long functionId);
    List<Transaction> findByFunctionName(String functionName);
    List<Transaction> findByVoidStatus(List<Integer> voidStatus);
    Page<Transaction> searchTransactions(String searchToken, int page, int size, String sortField, String sortOrder, Long tillId);
}
