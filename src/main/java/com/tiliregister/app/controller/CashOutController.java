package com.tiliregister.app.controller;

import com.tiliregister.app.model.CashOut;
import com.tiliregister.app.service.CashOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cash-outs")
public class CashOutController {

    private final CashOutService cashOutService;

    @Autowired
    public CashOutController(CashOutService cashOutService) {
        this.cashOutService = cashOutService;
    }

    @PreAuthorize("hasAuthority('cashout:create')")
    @PostMapping
    ResponseEntity<CashOut> createCashOut(@RequestBody CashOut cashOut, Authentication authentication){
        String username = authentication.getName();
        CashOut savedCashOut = cashOutService.saveCashOut(cashOut, username);
        return ResponseEntity.ok(savedCashOut);
    }

    @PreAuthorize("hasAuthority('cashout:view')")
    @GetMapping
    ResponseEntity<List<CashOut>> getAllCashOuts(){
        return ResponseEntity.ok(cashOutService.getAllCashOuts());
    }

    @PreAuthorize("hasAuthority('cashout:view')")
    @GetMapping("/{id}")
    ResponseEntity<CashOut> getCashOutById(@PathVariable Long id){
        return ResponseEntity.ok(cashOutService.getCashOutById(id));
    }

    @PreAuthorize("hasAuthority('cashout:view')")
    @GetMapping("/active")
    ResponseEntity<List<CashOut>> getActiveCashOuts(){
        return ResponseEntity.ok(cashOutService.getActiveCashOuts());
    }

    @PreAuthorize("hasAuthority('cashout:view')")
    @GetMapping("/till-id/{id}")
    ResponseEntity<List<CashOut>> getCashOutsByTillId(@PathVariable Long id){
        return ResponseEntity.ok(cashOutService.getCashOutsByTillId(id));
    }

    @PreAuthorize("hasAuthority('cashout:view')")
    @GetMapping("/till-name/{name}")
    ResponseEntity<List<CashOut>> getCashOutsByTillName(@PathVariable String name){
        return ResponseEntity.ok(cashOutService.getCashOutsByTillName(name));
    }

    @PreAuthorize("hasAuthority('cashout:edit')")
    @PutMapping("/{id}")
    ResponseEntity<CashOut> updateCashOut(@PathVariable Long id, @RequestBody CashOut cashOut, Authentication authentication){
        String username = authentication.getName();
        CashOut updatedCashOut = cashOutService.updateCashOut(id, cashOut, username);
        return ResponseEntity.ok(updatedCashOut);
    }

    @PreAuthorize("hasAuthority('cashout:delete')")
    @PutMapping("/{id}/void")
    ResponseEntity<CashOut> voidCashOut(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        CashOut voidedCashOut = cashOutService.voidCashOut(id, 1, username);
        return ResponseEntity.ok(voidedCashOut);
    }

    @PreAuthorize("hasAuthority('cashout:delete')")
    @PutMapping("/{id}/restore")
    ResponseEntity<CashOut> restoreCashOut(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        CashOut restoredCashOut = cashOutService.voidCashOut(id, 0, username);
        return ResponseEntity.ok(restoredCashOut);
    }
}
