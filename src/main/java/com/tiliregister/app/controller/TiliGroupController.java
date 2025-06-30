package com.tiliregister.app.controller;

import com.tiliregister.app.model.TiliGroup;
import com.tiliregister.app.service.TiliGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tili-groups")
public class TiliGroupController {

    private final TiliGroupService tiliGroupService;

    @Autowired
    public TiliGroupController(TiliGroupService tiliGroupService) {
        this.tiliGroupService = tiliGroupService;
    }

    @PostMapping
    public ResponseEntity<TiliGroup> createTiliGroup(@RequestBody TiliGroup tiliGroup, Authentication authentication) {

        String username = authentication.getName();
        TiliGroup savedTiliGroup = tiliGroupService.saveTiliGroup(tiliGroup, username);
        return ResponseEntity.ok(savedTiliGroup);
    }

    @GetMapping
    public ResponseEntity<List<TiliGroup>> getAllTiliGroups() {
        List<TiliGroup> tiliGroups = tiliGroupService.getAllTiliGroups();
        return ResponseEntity.ok(tiliGroups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TiliGroup> getTiliGroup(@PathVariable Long id) {
        TiliGroup tiliGroup = tiliGroupService.getTiliGroupById(id);
        return ResponseEntity.ok(tiliGroup);
    }

    @GetMapping("/active")
    public ResponseEntity<List<TiliGroup>> getActiveTiliGroups() {

        List<TiliGroup> tiliGroups = tiliGroupService.getActiveTiliGroups();
        return ResponseEntity.ok(tiliGroups);
    }

    @GetMapping("/non-active")
    public ResponseEntity<List<TiliGroup>> getNonActiveTiliGroups() {
        List<TiliGroup> tiliGroups = tiliGroupService.getNonActiveTiliGroups();
        return ResponseEntity.ok(tiliGroups);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TiliGroup> updateTiliGroup(@PathVariable Long id, @RequestBody TiliGroup tiliGroup, Authentication authentication) {
        String username = authentication.getName();
        TiliGroup updatedTiliGroup = tiliGroupService.updateTiliGroup(id, tiliGroup, username);
        return ResponseEntity.ok(updatedTiliGroup);

    }

    @PutMapping("/{id}/void")
    public ResponseEntity<TiliGroup> voidTiliGroup(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        TiliGroup updatedTiliGroup = tiliGroupService.voidTiliGroup(id, 1, username);
        return ResponseEntity.ok(updatedTiliGroup);

    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<TiliGroup> restoreTiliGroup(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        TiliGroup updatedTiliGroup = tiliGroupService.voidTiliGroup(id, 0, username);
        return ResponseEntity.ok(updatedTiliGroup);

    }
}
