package com.tiliregister.app.controller;

import com.tiliregister.app.model.Transaction;
import com.tiliregister.app.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;


    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasAuthority('transaction:create')")
    @PostMapping
    ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction, Authentication authentication){
        String username = authentication.getName();
        Transaction savedTransaction = transactionService.saveTransaction(transaction, username);

        return ResponseEntity.ok(savedTransaction);
    }

    @PreAuthorize("hasAuthority('transaction:view')")
    @GetMapping
    ResponseEntity<List<Transaction>> getAllTransactions(){
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @PreAuthorize("hasAuthority('transaction:view')")
    @GetMapping("/active")
    ResponseEntity<List<Transaction>> getActiveTransactions(){
        return ResponseEntity.ok(transactionService.getActiveTransactions());
    }

    @PreAuthorize("hasAuthority('transaction:view')")
    @GetMapping("/non-active")
    ResponseEntity<List<Transaction>> getNonActiveTransactions(){
        return ResponseEntity.ok(transactionService.getNonActiveTransactions());
    }

    @PreAuthorize("hasAuthority('transaction:view')")
    @GetMapping("/{id}")
    ResponseEntity<Transaction> getTransactionById(@PathVariable Long id){
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PreAuthorize("hasAuthority('transaction:view')")
    @GetMapping("/till-id/{id}")
    ResponseEntity<List<Transaction>> getTransactionsByTillId(@PathVariable Long id){
        return ResponseEntity.ok(transactionService.getTransactionsByTillId(id));
    }

    @PreAuthorize("hasAuthority('transaction:view')")
    @GetMapping("/till-name/{name}")
    ResponseEntity<List<Transaction>> getTransactionsByTillName(@PathVariable("name") String tillName){
        return ResponseEntity.ok(transactionService.getTransactionsByTillName(tillName));
    }

    @PreAuthorize("hasAuthority('transaction:view')")
    @GetMapping("/function-id/{id}")
    ResponseEntity<List<Transaction>> getTransactionsByFunctionId(@PathVariable Long id){
        return ResponseEntity.ok(transactionService.getTransactionsByFunctionId(id));
    }

    @PreAuthorize("hasAuthority('transaction:view')")
    @GetMapping("/function-name/{name}")
    ResponseEntity<List<Transaction>> getTransactionsByFunctionName(@PathVariable("name") String functionName){
        return ResponseEntity.ok(transactionService.getTransactionsByFunctionName(functionName));
    }

    @PreAuthorize("hasAuthority('transaction:edit')")
    @PutMapping("/{id}/edit")
    ResponseEntity<Transaction> updateTransactionOnly(@PathVariable Long id, @RequestBody Transaction transaction, Authentication authentication){
        String username = authentication.getName();
        Transaction updatedTransaction = transactionService.updateTransactionOnly(id, transaction, username);

        return ResponseEntity.ok(updatedTransaction);
    }

    @PreAuthorize("hasAuthority('transaction:edit')")
    @PutMapping("/{id}/reverse-update")
    ResponseEntity<Transaction> updateTransactionWithReversal(@PathVariable Long id, @RequestBody Transaction transaction, Authentication authentication){
        String username = authentication.getName();
        Transaction updatedTransaction = transactionService.updateTransactionWithReversal(id, transaction, username);

        return ResponseEntity.ok(updatedTransaction);
    }

    @PreAuthorize("hasAuthority('transaction:delete')")
    @PutMapping("/{id}/void")
    ResponseEntity<Transaction> voidTransaction(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        Transaction voidedTransaction = transactionService.voidTransaction(id, 1, username);

        return ResponseEntity.ok(voidedTransaction);
    }
   @PreAuthorize("hasAuthority('transaction:delete')")
   @PutMapping("/{id}/restore")
   ResponseEntity<Transaction> restoreTransaction(@PathVariable Long id, Authentication authentication){
       String username = authentication.getName();
       Transaction restoredTransaction = transactionService.voidTransaction(id, 0, username);

       return ResponseEntity.ok(restoredTransaction);
   }
}
