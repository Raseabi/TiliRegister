package com.tiliregister.app.controller;

import com.tiliregister.app.model.Till;
import com.tiliregister.app.model.TillRequest;
import com.tiliregister.app.service.TillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    ResponseEntity<Till> createTill(@RequestBody Till till, Authentication authentication){
        String username = authentication.getName();
        Till savedTill = tillService.saveTill(till, username);
        return ResponseEntity.ok(savedTill);
    }

    @PreAuthorize("hasAuthority('till:create')")
    @PostMapping("/details")
    ResponseEntity<Till> createTill(@RequestBody TillRequest tillRequest, Authentication authentication){
        String username = authentication.getName();
        Till savedTill = tillService.saveTillWithFunctions(tillRequest, username);
        return ResponseEntity.ok(savedTill);
    }

    @PreAuthorize("hasAuthority('till:view')")
    @GetMapping
    ResponseEntity<List<Till>> getAllTills(){
        List<Till> tills = tillService.getAllTills();
        return ResponseEntity.ok(tills);
    }

    @PreAuthorize("hasAuthority('till:view')")
    @GetMapping("/active")
    ResponseEntity<List<Till>> getActiveTills(){
        List<Till> tills = tillService.getActiveTills();
        return ResponseEntity.ok(tills);
    }

    @PreAuthorize("hasAuthority('till:view')")
    @GetMapping("/non-active")
    ResponseEntity<List<Till>> getInActiveTills(){
        List<Till> tills = tillService.getInActiveTills();
        return ResponseEntity.ok(tills);
    }

    @PreAuthorize("hasAuthority('till:view')")
    @GetMapping("/id/{id}")
    ResponseEntity<Till> getTillById(@PathVariable Long id){
        Till till = tillService.getTillById(id);
        return ResponseEntity.ok(till);
    }

    @PreAuthorize("hasAuthority('till:view')")
    @GetMapping("/name/{name}")
    ResponseEntity<Till> getTillByName(@PathVariable String name){
        Till till = tillService.getTillByName(name);
        return ResponseEntity.ok(till);
    }

    @PreAuthorize("hasAuthority('till:edit')")
    @PutMapping("/{id}")
    ResponseEntity<Till> updateTill(@PathVariable Long id, @RequestBody Till till, Authentication authentication){
        String username = authentication.getName();
        Till updatedTill = tillService.updateTill(id, till, username);
        return ResponseEntity.ok(updatedTill);
    }

    @PreAuthorize("hasAuthority('till:edit')")
    @PutMapping("/{id}/details")
    ResponseEntity<Till> updateTillWithFunctions(@PathVariable Long id, @RequestBody TillRequest tillRequest, Authentication authentication){
        String username = authentication.getName();
        Till updatedTill = tillService.updateTillWithFunctions(id, tillRequest, username);
        return ResponseEntity.ok(updatedTill);
    }

    @PreAuthorize("hasAuthority('till:delete')")
    @PutMapping("/{id}/void")
    ResponseEntity<Till> voidTill(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        Till updatedTill = tillService.voidTill(id, 1, username);
        return ResponseEntity.ok(updatedTill);
    }

    @PreAuthorize("hasAuthority('till:delete')")
    @PutMapping("/{id}/restore")
    ResponseEntity<Till> restoreTill(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        Till updatedTill = tillService.voidTill(id, 0, username);
        return ResponseEntity.ok(updatedTill);
    }

}
