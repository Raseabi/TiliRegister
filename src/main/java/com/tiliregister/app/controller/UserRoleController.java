package com.tiliregister.app.controller;

import com.tiliregister.app.model.UserRole;
import com.tiliregister.app.model.UserRoleRequest;
import com.tiliregister.app.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {

    private  final UserRoleService userRoleService;

    @Autowired
    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping
    public ResponseEntity<String> createUserRole(
            @RequestBody UserRoleRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        userRoleService.assignRolesToUser(
                request.getUserId(),
                request.getRoleIds(),
                username
        );
        return ResponseEntity.ok("Roles assigned successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRole> getUserRole(@PathVariable Long id) {
        UserRole userRole = userRoleService.getUserRoleById(id);
        return ResponseEntity.ok(userRole);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<UserRole>> getUserRolesByUserId(@PathVariable("id") Long userId) {
        List<UserRole> userRoles = userRoleService.getUserRolesByUserId(userId);
        return ResponseEntity.ok(userRoles);
    }
    @GetMapping("/role/{id}")
    public ResponseEntity<List<UserRole>> getUserRolesByRoleId(@PathVariable("id") Long roleId) {
        List<UserRole> userRoles = userRoleService.getUserRolesByRoleId(roleId);
        return ResponseEntity.ok(userRoles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserRole(@PathVariable Long id) {
        boolean success = userRoleService.deleteUserRoleById(id);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/user/{userId}/roles")
    public ResponseEntity<?> removeRolesFromUser(
            @PathVariable Long userId,
            @RequestParam Set<Long> roleIds) {
        boolean success = userRoleService.removeRolesAssignedToUser(userId, roleIds);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
