package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.TransactionDao;
import com.tiliregister.app.model.TillFunction;
import com.tiliregister.app.model.Transaction;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.TillFunctionService;
import com.tiliregister.app.service.TillService;
import com.tiliregister.app.service.TransactionService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionDao transactionDao;
    private final UserService userService;
    private final TillService tillService;
    private final TillFunctionService tillFunctionService;

    @Autowired
    public TransactionServiceImpl(TransactionDao transactionDao, UserService userService, TillService tillService, TillFunctionService tillFunctionService) {
        this.transactionDao = transactionDao;
        this.userService = userService;
        this.tillService = tillService;
        this.tillFunctionService = tillFunctionService;
    }

    @Override
    @Transactional
    public Transaction saveTransaction(Transaction transaction, String createdByUsername) {
        // Validate creator
        User createdBy = userService.getUserByUsername(createdByUsername);
        if (createdBy == null || createdBy.getVoided() == 1) {
            throw new EntityNotFoundException("Admin not found");
        }

        transaction.setCreatedBy(createdBy);
        transaction.setCreatedAt(LocalDateTime.now());

        if (!isTransactionValid(transaction, tillService)) {
            throw new IllegalStateException("Transaction is not valid");
        }

        processTransactionEffects(transaction, tillService);

        return transactionDao.save(transaction);
    }


    @Override
    public Transaction getTransactionById(Long id) {
        return transactionDao.findById(id);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionDao.findByVoidStatus(List.of(0, 1));
    }

    @Override
    public List<Transaction> getActiveTransactions() {
        return transactionDao.findByVoidStatus(List.of(0));
    }

    @Override
    public List<Transaction> getNonActiveTransactions() {
        return transactionDao.findByVoidStatus(List.of(1));
    }

    @Override
    public Transaction updateTransaction(Long id, Transaction transaction, String updatedByUsername) {
        Transaction updatedTransaction = transactionDao.findById(id);
        User updatedBy = userService.getUserByUsername(updatedByUsername);

        if (updatedTransaction == null || updatedTransaction.getVoided() == 1 || updatedBy == null || updatedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Transaction or Admin not found");
        }
        updatedTransaction.setAmount(transaction.getAmount());
        updatedTransaction.setFloatChange(transaction.getFloatChange());
        updatedTransaction.setCashChange(transaction.getCashChange());
        updatedTransaction.setUpdatedBy(updatedBy);
        updatedTransaction.setUpdatedAt(LocalDateTime.now());

        return transactionDao.save(updatedTransaction);
    }

    @Override
    public Transaction voidTransaction(Long id, int voidStatus, String voidedByUsername) {
        Transaction voidedTransaction = transactionDao.findById(id);
        User voidedBy = userService.getUserByUsername(voidedByUsername);

        if (voidedTransaction == null || voidedTransaction.getVoided() == 1 || voidedBy == null || voidedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Transaction or Admin not found");
        }
        voidedTransaction.setVoided(voidStatus);
        voidedTransaction.setVoidedBy(voidedBy);
        voidedTransaction.setVoidedAt(LocalDateTime.now());

        return transactionDao.save(voidedTransaction);
    }

    @Override
    public boolean isTransactionValid(Transaction transaction, TillService tillService) {
        TillFunction function = transaction.getTillFunction();
        Long tillId = transaction.getTill().getId();
        BigDecimal amount = transaction.getAmount();

        String floatDirection = function.getFloatChangeDirection().toString().toLowerCase();
        String cashDirection = function.getCashChangeDirection().toString().toLowerCase();

        // Validate float deduction
        if (floatDirection.equals("out")) {
            if (!tillService.isAdjustFloatPossible(tillId, amount, "-")) {
                throw new IllegalArgumentException(function.getName() + ": Insufficient Float for this transaction");
            }
        }

        // Validate cash deduction
        if (cashDirection.equals("out")) {
            if (!tillService.isAdjustCashInHandPossible(tillId, amount, "-")) {
                throw new IllegalArgumentException(function.getName() + ": Insufficient Cash in Hand for this transaction");
            }
        }

        return true;
    }
    @Override
    public void processTransactionEffects(Transaction transaction, TillService tillService) {
        TillFunction function = transaction.getTillFunction();
        Long tillId = transaction.getTill().getId();
        BigDecimal amount = transaction.getAmount();

        String floatDirection = function.getFloatChangeDirection().toString().toLowerCase();
        String cashDirection = function.getCashChangeDirection().toString().toLowerCase();

        // Apply float change
        if (floatDirection.equals("in")) {
            tillService.adjustTillFloat(tillId, amount, "+");
        } else if (floatDirection.equals("out")) {
            tillService.adjustTillFloat(tillId, amount, "-");
        }

        // Apply cash change
        if (cashDirection.equals("in")) {
            tillService.adjustTillCashInHand(tillId, amount, "+");
        } else if (cashDirection.equals("out")) {
            tillService.adjustTillCashInHand(tillId, amount, "-");
        }
    }


}
