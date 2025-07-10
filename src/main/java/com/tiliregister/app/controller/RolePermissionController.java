package com.tiliregister.app.controller;

import com.tiliregister.app.model.RolePermission;
import com.tiliregister.app.model.RolePermissionRequest;
import com.tiliregister.app.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController  // Spring annotation marking this class as a REST controller
@RequestMapping("/api/role-permissions")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    private static final Logger logger = LoggerFactory.getLogger(RolePermissionController.class);


    @Autowired
    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    //@PreAuthorize("hasAuthority('role:create')")
    @PostMapping
    public ResponseEntity<String> createRolePermission(
            @RequestBody RolePermissionRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        rolePermissionService.assignPermissionsToRole(
                request.getRoleId(),
                request.getPermissionIds(),
                username
        );
        return ResponseEntity.ok("Permissions assigned successfully");
    }

    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping("/{id}")
    public ResponseEntity<RolePermission> getRolePermission(@PathVariable Long id) {
        RolePermission rolePermission = rolePermissionService.getRolePermissionById(id);
        return ResponseEntity.ok(rolePermission);
    }

    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping("/role/{id}")
    public ResponseEntity<List<RolePermission>> getRolePermissionsByRoleId(@PathVariable("id") Long roleId) {
        List<RolePermission> rolePermissions = rolePermissionService.getRolePermissionsByRoleId(roleId);
        return ResponseEntity.ok(rolePermissions);
    }

    @PreAuthorize("hasAuthority('role:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRolePermission(@PathVariable Long id) {
        boolean success = rolePermissionService.deleteRolePermissionById(id);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasAuthority('role:delete')")
    @DeleteMapping("/role/{roleId}/permissions")
    public ResponseEntity<?> removePermissionsFromRole(
            @PathVariable Long roleId,
            @RequestParam Set<Long> permissionIds) {
        boolean success = rolePermissionService.removePermissionsFromRole(roleId, permissionIds);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
