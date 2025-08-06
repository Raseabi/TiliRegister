package com.tiliregister.app.controller;

import com.tiliregister.app.model.Till;
import com.tiliregister.app.model.TillRequest;
import com.tiliregister.app.service.TillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tills")
public class TillController {

    private final TillService tillService;

    @Autowired
    public TillController(TillService tillService) {
        this.tillService = tillService;
    }

    @PreAuthorize("hasAuthority('till:create')")
    @PostMapping
    ResponseEntity<Till> createSimpleTill(@RequestBody Till till, Authentication authentication) {
        String username = authentication.getName();
        Till savedTill = tillService.saveTill(till, username);
        return ResponseEntity.ok(savedTill);
    }

    @PreAuthorize("hasAuthority('till:create')")
    @PostMapping("/with-functions")
    ResponseEntity<Till> createDetailedTill(@RequestBody TillRequest tillRequest, Authentication authentication) {
        String username = authentication.getName();
        Till savedTill = tillService.saveTillWithFunctions(tillRequest, username);
        return ResponseEntity.ok(savedTill);
    }

    @PreAuthorize("hasAuthority('till:view')")
    @GetMapping
    ResponseEntity<List<Till>> getAllTills() {
        List<Till> tills = tillService.getAllTills();
        return ResponseEntity.ok(tills);
    }

    @PreAuthorize("hasAuthority('till:view')")
    @GetMapping("/active")
    ResponseEntity<List<Till>> getActiveTills() {
        List<Till> tills = tillService.getActiveTills();
        return ResponseEntity.ok(tills);
    }

    @PreAuthorize("hasAuthority('till:view')")
    @GetMapping("/non-active")
    ResponseEntity<List<Till>> getInActiveTills() {
        List<Till> tills = tillService.getInActiveTills();
        return ResponseEntity.ok(tills);
    }

    @PreAuthorize("hasAuthority('till:view')")
    @GetMapping("/id/{id}")
    ResponseEntity<Till> getTillById(@PathVariable Long id) {
        Till till = tillService.getTillById(id);
        return ResponseEntity.ok(till);
    }

    @PreAuthorize("hasAuthority('till:view')")
    @GetMapping("/name/{name}")
    ResponseEntity<Till> getTillByName(@PathVariable String name) {
        Till till = tillService.getTillByName(name);
        return ResponseEntity.ok(till);
    }

    @PreAuthorize("hasAuthority('till:view')")
    @GetMapping("/tili-group-id/{tiliGroupId}")
    ResponseEntity<List<Till>> getTillsByGroupId(@PathVariable Long tiliGroupId) {
        return ResponseEntity.ok(tillService.getTillsByGroupId(tiliGroupId));
    }

    @PreAuthorize("hasAuthority('till:edit')")
    @PutMapping("/{id}")
    ResponseEntity<Till> updateTill(@PathVariable Long id, @RequestBody Till till, Authentication authentication) {
        String username = authentication.getName();
        Till updatedTill = tillService.updateTill(id, till, username);
        return ResponseEntity.ok(updatedTill);
    }

    @PreAuthorize("hasAuthority('till:edit')")
    @PutMapping("/{id}/details")
    ResponseEntity<Till> updateTillWithFunctions(@PathVariable Long id, @RequestBody TillRequest tillRequest, Authentication authentication) {
        String username = authentication.getName();
        Till updatedTill = tillService.updateTillWithFunctions(id, tillRequest, username);
        return ResponseEntity.ok(updatedTill);
    }

    @PreAuthorize("hasAuthority('till:delete')")
    @PutMapping("/{id}/void")
    ResponseEntity<Till> voidTill(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Till updatedTill = tillService.voidTill(id, 1, username);
        return ResponseEntity.ok(updatedTill);
    }

    @PreAuthorize("hasAuthority('till:delete')")
    @PutMapping("/{id}/restore")
    ResponseEntity<Till> restoreTill(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Till updatedTill = tillService.voidTill(id, 0, username);
        return ResponseEntity.ok(updatedTill);
    }

    // For updating float
    @PutMapping("/{id}/float")
    public ResponseEntity<?> updateFloat(@PathVariable Long id, @RequestBody Map<String, BigDecimal> payload, Authentication authentication) {
        String username = authentication.getName();
        BigDecimal floatAmount = payload.get("floatAmount");
        return ResponseEntity.ok(tillService.updateFloat(id, floatAmount, username));
    }

    // For updating cash in hand
    @PutMapping("/{id}/cash-in-hand")
    public ResponseEntity<?> updateCashInHand(@PathVariable Long id, @RequestBody Map<String, BigDecimal> payload, Authentication authentication) {
        String username = authentication.getName();
        BigDecimal cashInHandAmount = payload.get("cashInHandAmount");
        return ResponseEntity.ok(tillService.updateCash(id, cashInHandAmount, username));
    }


}
