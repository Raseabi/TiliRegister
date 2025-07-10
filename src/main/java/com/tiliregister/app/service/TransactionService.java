package com.tiliregister.app.service;

import com.tiliregister.app.model.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction saveTransaction(Transaction transaction, String createdByUsername);
    Transaction getTransactionById(Long id);
    List<Transaction> getAllTransactions();
    List<Transaction> getActiveTransactions();
    List<Transaction> getNonActiveTransactions();
    List<Transaction> getTransactionsByTillId(Long tillId);
    List<Transaction> getTransactionsByTillName(String tillName);
    List<Transaction> getTransactionsByFunctionId(Long functionId);
    List<Transaction> getTransactionsByFunctionName(String functionName);
    Transaction updateTransactionOnly(Long id, Transaction transaction, String updatedByUsername);
    Transaction updateTransactionWithReversal(Long id, Transaction transaction, String updatedByUsername);
    Transaction voidTransaction(Long id, int voidStatus, String voidedByUsername);
    boolean isTransactionValid(Transaction transaction, TillService tillService);
    void processTransactionEffects(Transaction transaction, TillService tillService);
    void processTransactionReversalEffects(Transaction transaction, TillService tillService);
}
