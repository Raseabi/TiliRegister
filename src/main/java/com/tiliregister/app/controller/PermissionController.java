package com.tiliregister.app.controller;

import com.tiliregister.app.model.Permission;
import com.tiliregister.app.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping
    ResponseEntity<List<Permission>> getAllPermissions(){
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }
    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping("/{id}")
    ResponseEntity<Permission> getPermissionById(@PathVariable Long id){
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }
}