package com.tiliregister.app.controller;

import com.tiliregister.app.model.Role;
import com.tiliregister.app.model.Role;
import com.tiliregister.app.model.TiliGroup;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role, Authentication authentication) {
        String username = authentication.getName();
        Role savedRole = roleService.saveRole(role, username);
        return ResponseEntity.ok(savedRole);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role role, Authentication authentication) {
        String username = authentication.getName();
        Role updatedRole = roleService.updateRole(id, role, username);
        return ResponseEntity.ok(updatedRole);
    }

    @PutMapping("/{id}/void")
    public ResponseEntity<Role> voidRole(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName(); // current logged-in username
        Role role = roleService.voidRole(id, 1, username);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Role> restoreRole(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName(); // current logged-in username
        Role role = roleService.voidRole(id, 0, username);
        return ResponseEntity.ok(role);
    }
}
