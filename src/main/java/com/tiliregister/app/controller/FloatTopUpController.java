package com.tiliregister.app.controller;

import com.tiliregister.app.model.FloatTopUp;
import com.tiliregister.app.service.FloatTopUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/float-top-ups")
public class FloatTopUpController {

    private final FloatTopUpService floatTopUpService;

    @Autowired
    public FloatTopUpController(FloatTopUpService floatTopUpService) {
        this.floatTopUpService = floatTopUpService;
    }

    @PreAuthorize("hasAuthority('float:create')")
    @PostMapping
    ResponseEntity<FloatTopUp> createFloatTopUp(@RequestBody FloatTopUp floatTopUp, Authentication authentication){
        String username = authentication.getName();
        FloatTopUp savedFloatTopUp = floatTopUpService.saveFloatTopUp(floatTopUp, username);
        return ResponseEntity.ok(savedFloatTopUp);
    }

    @PreAuthorize("hasAuthority('float:view')")
    @GetMapping
    ResponseEntity<List<FloatTopUp>> getAllFloatTopUps(){
        return ResponseEntity.ok(floatTopUpService.getAllFloatTopUps());
    }

    @PreAuthorize("hasAuthority('float:view')")
    @GetMapping("/{id}")
    ResponseEntity<FloatTopUp> getFloatTopUpById(@PathVariable Long id){
        return ResponseEntity.ok(floatTopUpService.getFloatTopUpById(id));
    }

    @PreAuthorize("hasAuthority('float:view')")
    @GetMapping("/active")
    ResponseEntity<List<FloatTopUp>> getActiveFloatTopUps(){
        return ResponseEntity.ok(floatTopUpService.getActiveFloatTopUps());
    }

    @PreAuthorize("hasAuthority('float:view')")
    @GetMapping("/till-id/{id}")
    ResponseEntity<List<FloatTopUp>> getFloatTopUpsByTillId(@PathVariable Long id){
        return ResponseEntity.ok(floatTopUpService.getFloatTopUpsByTillId(id));
    }

    @PreAuthorize("hasAuthority('float:view')")
    @GetMapping("/till-name/{name}")
    ResponseEntity<List<FloatTopUp>> getFloatTopUpsByTillName(@PathVariable String name){
        return ResponseEntity.ok(floatTopUpService.getFloatTopUpsByTillName(name));
    }

    @PreAuthorize("hasAuthority('float:edit')")
    @PutMapping("/{id}")
    ResponseEntity<FloatTopUp> updateFloatTopUp(@PathVariable Long id, @RequestBody FloatTopUp floatTopUp, Authentication authentication){
        String username = authentication.getName();
        FloatTopUp updatedFloatTopUp = floatTopUpService.updateFloatTopUp(id, floatTopUp, username);
        return ResponseEntity.ok(updatedFloatTopUp);
    }

    @PreAuthorize("hasAuthority('float:delete')")
    @PutMapping("/{id}/void")
    ResponseEntity<FloatTopUp> voidFloatTopUp(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        FloatTopUp voidedFloatTopUp = floatTopUpService.voidFloatTopUp(id, 1, username);
        return ResponseEntity.ok(voidedFloatTopUp);
    }

    @PreAuthorize("hasAuthority('float:delete')")
    @PutMapping("/{id}/restore")
    ResponseEntity<FloatTopUp> restoreFloatTopUp(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        FloatTopUp restoredFloatTopUp = floatTopUpService.voidFloatTopUp(id, 0, username);
        return ResponseEntity.ok(restoredFloatTopUp);
    }
}
