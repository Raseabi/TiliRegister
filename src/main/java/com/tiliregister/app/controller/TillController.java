package com.tiliregister.app.controller;

import com.tiliregister.app.model.Till;
import com.tiliregister.app.service.TillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    ResponseEntity<Till> createTill(@RequestBody Till till, Authentication authentication){
        String username = authentication.getName();
        Till savedTill = tillService.saveTill(till, username);
        return ResponseEntity.ok(savedTill);
    }

    @GetMapping
    ResponseEntity<List<Till>> getAllTills(){
        List<Till> tills = tillService.getAllTills();
        return ResponseEntity.ok(tills);
    }
    @GetMapping("/active")
    ResponseEntity<List<Till>> getActiveTills(){
        List<Till> tills = tillService.getActiveTills();
        return ResponseEntity.ok(tills);
    }
    @GetMapping("/nonactive")
    ResponseEntity<List<Till>> getInActiveTills(){
        List<Till> tills = tillService.getInActiveTills();
        return ResponseEntity.ok(tills);
    }

    @GetMapping("/id/{id}")
    ResponseEntity<Till> getTillById(@PathVariable Long id){
        Till till = tillService.getTillById(id);
        return ResponseEntity.ok(till);
    }

    @GetMapping("/name/{name}")
    ResponseEntity<Till> getTillByName(@PathVariable String name){
        Till till = tillService.getTillByName(name);
        return ResponseEntity.ok(till);
    }

    @PutMapping("/{id}")
    ResponseEntity<Till> updateTill(@PathVariable Long id, @RequestBody Till till, Authentication authentication){
        String username = authentication.getName();
        Till updatedTill = tillService.updateTill(id, till, username);
        return ResponseEntity.ok(updatedTill);
    }
    @PutMapping("/{id}/void")
    ResponseEntity<Till> voidTill(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        Till updatedTill = tillService.voidTill(id, 1, username);
        return ResponseEntity.ok(updatedTill);
    }
    @PutMapping("/{id}/restore")
    ResponseEntity<Till> restoreTill(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        Till updatedTill = tillService.voidTill(id, 0, username);
        return ResponseEntity.ok(updatedTill);
    }

}
