package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.TransactionDao;
import com.tiliregister.app.model.*;
import com.tiliregister.app.service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
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
    private final ClientService clientService;

    @Autowired
    public TransactionServiceImpl(TransactionDao transactionDao, UserService userService, TillService tillService, TillFunctionService tillFunctionService, ClientService clientService) {
        this.transactionDao = transactionDao;
        this.userService = userService;
        this.tillService = tillService;
        this.tillFunctionService = tillFunctionService;
        this.clientService = clientService;
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
        Client client = clientService.getOrCreateClient(
                transaction.getClient().getClientNumber(),
                transaction.getClient().getClientName()
        );
        transaction.setClient(client);
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
    public List<Transaction> getTransactionsByTillId(Long tillId) {
        return transactionDao.findByTillId(tillId);
    }

    @Override
    public List<Transaction> getTransactionsByTillName(String tillName) {
        return transactionDao.findByTillName(tillName);
    }

    @Override
    public List<Transaction> getTransactionsByFunctionId(Long functionId) {
        return transactionDao.findByFunctionId(functionId);
    }

    @Override
    public List<Transaction> getTransactionsByFunctionName(String functionName) {
        return transactionDao.findByFunctionName(functionName);
    }

    @Override
    public Transaction updateTransactionOnly(Long id, Transaction transaction, String updatedByUsername) {
        Transaction updatedTransaction = transactionDao.findById(id);
        User updatedBy = userService.getUserByUsername(updatedByUsername);

        if (updatedTransaction == null || updatedTransaction.getVoided() == 1 || updatedBy == null || updatedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Transaction or Admin not found");
        }
        Till till = tillService.getTillById(transaction.getTill().getId());
        TillFunction tillFunction = tillFunctionService.getTillFunctionById(transaction.getTillFunction().getId());

        String floatDirection = tillFunction.getFloatChangeDirection().toString();
        String cashDirection = tillFunction.getCashChangeDirection().toString();

        BigDecimal floatChange = transactionChangeDirection(transaction.getAmount(), floatDirection);
        BigDecimal cashChange = transactionChangeDirection(transaction.getAmount(), cashDirection);

        updatedTransaction.setTill(till);
        updatedTransaction.setTillFunction(tillFunction);
        updatedTransaction.setAmount(transaction.getAmount());
        updatedTransaction.setFloatChange(floatChange);
        updatedTransaction.setCashChange(cashChange);
        updatedTransaction.setUpdatedBy(updatedBy);
        updatedTransaction.setUpdatedAt(LocalDateTime.now());

        return transactionDao.save(updatedTransaction);
    }

    @Override
    @Transactional
    public Transaction updateTransactionWithReversal(Long id, Transaction transaction, String updatedByUsername) {
        Transaction existingTransaction = transactionDao.findById(id);
        User updatedBy = userService.getUserByUsername(updatedByUsername);

        if (existingTransaction == null || existingTransaction.getVoided() == 1 || updatedBy == null || updatedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Transaction or Admin not found");
        }

        Transaction reversal = new Transaction();
        reversal.setAmount(existingTransaction.getAmount());
        reversal.setTill(existingTransaction.getTill());
        reversal.setTillFunction(existingTransaction.getTillFunction());
        processTransactionReversalEffects(reversal, tillService);

        Till newTill = tillService.getTillById(transaction.getTill().getId());
        TillFunction newFunction = tillFunctionService.getTillFunctionById(transaction.getTillFunction().getId());

        transaction.setTill(newTill);
        transaction.setTillFunction(newFunction);

        if (!isTransactionValid(transaction, tillService)) {
            throw new IllegalArgumentException("Transaction would result in invalid float or cash values.");
        }

        processTransactionEffects(transaction, tillService);

        existingTransaction.setAmount(transaction.getAmount());
        existingTransaction.setTill(transaction.getTill());
        existingTransaction.setTillFunction(transaction.getTillFunction());
        existingTransaction.setFloatChange(transaction.getFloatChange());
        existingTransaction.setCashChange(transaction.getCashChange());
        existingTransaction.setUpdatedBy(updatedBy);
        existingTransaction.setUpdatedAt(LocalDateTime.now());

        return transactionDao.save(existingTransaction);
    }

    @Override
    public Transaction voidTransaction(Long id, int voidStatus, String voidedByUsername) {
        Transaction voidedTransaction = transactionDao.findById(id);
        User voidedBy = userService.getUserByUsername(voidedByUsername);

        if (voidedTransaction == null || voidedBy == null || voidedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Transaction or Admin not found");
        }
        voidedTransaction.setVoided(voidStatus);
        voidedTransaction.setVoidedBy(voidedBy);
        voidedTransaction.setVoidedAt(LocalDateTime.now());

        return transactionDao.save(voidedTransaction);
    }

    @Override
    @Transactional
    public boolean isTransactionValid(Transaction transaction, TillService tillService) {

        TillFunction function = tillFunctionService.getTillFunctionById(transaction.getTillFunction().getId());
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
        TillFunction function = tillFunctionService.getTillFunctionById(transaction.getTillFunction().getId());
        Long tillId = transaction.getTill().getId();
        BigDecimal amount = transaction.getAmount();

        String floatDirection = function.getFloatChangeDirection().toString().toLowerCase();
        String cashDirection = function.getCashChangeDirection().toString().toLowerCase();

        // Apply float change
        switch (floatDirection) {
            case "in" -> {
                tillService.adjustTillFloat(tillId, amount, "+");
                transaction.setFloatChange(transaction.getAmount());
            }
            case "out" -> {
                tillService.adjustTillFloat(tillId, amount, "-");
                transaction.setFloatChange(transaction.getAmount().negate());
            }
            case "none" -> transaction.setFloatChange(BigDecimal.ZERO);
            default -> throw new IllegalArgumentException("Invalid float change direction: " + floatDirection);
        }

        // Apply cash change
        switch (cashDirection) {
            case "in" -> {
                tillService.adjustTillCashInHand(tillId, amount, "+");
                transaction.setCashChange(transaction.getAmount());
            }
            case "out" -> {
                tillService.adjustTillCashInHand(tillId, amount, "-");
                transaction.setCashChange(transaction.getAmount().negate());
            }
            case "none" -> transaction.setCashChange(BigDecimal.ZERO);
            default -> throw new IllegalArgumentException("Invalid cash change direction: " + cashDirection);
        }
    }

    @Override
    public void processTransactionReversalEffects(Transaction transaction, TillService tillService) {
        TillFunction function = transaction.getTillFunction();
        Long tillId = transaction.getTill().getId();
        BigDecimal amount = transaction.getAmount();

        if (function.getFloatChangeDirection() == TillFunctionChangeDirection.in) {
            tillService.adjustTillFloat(tillId, amount, "-");
        } else if (function.getFloatChangeDirection() == TillFunctionChangeDirection.out) {
            tillService.adjustTillFloat(tillId, amount, "+");
        }

        if (function.getCashChangeDirection() == TillFunctionChangeDirection.in) {
            tillService.adjustTillCashInHand(tillId, amount, "-");
        } else if (function.getCashChangeDirection() == TillFunctionChangeDirection.out) {
            tillService.adjustTillCashInHand(tillId, amount, "+");
        }
    }

    @Override
    public Page<Transaction> searchTransactions(String searchToken, int page, int size, String sortField, String sortOrder, Long tillId) {
        return transactionDao.searchTransactions(searchToken, page, size, sortField, sortOrder, tillId);
    }

    private BigDecimal transactionChangeDirection(BigDecimal amount, String changeDirection) {
        return switch (changeDirection.toLowerCase()) {
            case "in" -> amount;
            case "out" -> amount.negate();
            case "none" -> BigDecimal.ZERO;
            default -> throw new IllegalArgumentException("Invalid change direction: " + changeDirection);
        };
    }
}
