package com.tiliregister.app.controller;

import com.tiliregister.app.model.Role;
import com.tiliregister.app.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // Spring annotation marking this class as a REST controller
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasAuthority('role:create')")
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role, Authentication authentication) {
        String username = authentication.getName();
        Role savedRole = roleService.saveRole(role, username);
        return ResponseEntity.ok(savedRole);

    }

    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping
    public ResponseEntity<Page<Role>> searchRoles(
            @RequestParam(name = "searchTerm", required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        Page<Role> result = roleService.searchRoles(searchTerm, page, size, sortField, sortOrder);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping("/active")
    public ResponseEntity<List<Role>> getActiveRoles() {
        return ResponseEntity.ok(roleService.getActiveRoles());
    }

    @PreAuthorize("hasAuthority('role:edit')")
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role role, Authentication authentication) {
        String username = authentication.getName();
        Role updatedRole = roleService.updateRole(id, role, username);
        return ResponseEntity.ok(updatedRole);
    }

    @PreAuthorize("hasAuthority('role:delete')")
    @PutMapping("/{id}/void")
    public ResponseEntity<Role> voidRole(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName(); // current logged-in username
        Role role = roleService.voidRole(id, 1, username);
        return ResponseEntity.ok(role);
    }

    @PreAuthorize("hasAuthority('role:delete')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<Role> restoreRole(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName(); // current logged-in username
        Role role = roleService.voidRole(id, 0, username);
        return ResponseEntity.ok(role);
    }
}