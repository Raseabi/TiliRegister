package com.tiliregister.app.controller;

import com.tiliregister.app.model.User;
import com.tiliregister.app.model.UserRequest;
import com.tiliregister.app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController  // Spring annotation marking this class as a REST controller
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('user:create')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest, Authentication authentication) {
        String username = authentication.getName();
        User savedUser = userService.saveUser(userRequest, username);
        return ResponseEntity.ok(savedUser);
    }

    @PreAuthorize("hasAuthority('user:view')")
    @GetMapping
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam(name = "searchTerm", required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "surname") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        Page<User> result = userService.searchUsers(searchTerm, page, size, sortField, sortOrder);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('user:view')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('user:edit')")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest, Authentication authentication) {
        String updatedBy = authentication.getName();
        User updated = userService.updateUser(id, userRequest, updatedBy);
        return ResponseEntity.ok(updated);

    }

    @PreAuthorize("hasAuthority('user:delete')")
    @PutMapping("/{id}/void")
    public ResponseEntity<User> voidUser(@PathVariable Long id, Authentication authentication) {
        String voidedBy = authentication.getName(); // current logged-in username
        User user = userService.voidUser(id, 1, voidedBy);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('user:delete')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<User> restoreUser(@PathVariable Long id, Authentication authentication) {
        String restoredBy = authentication.getName(); // current logged-in username
        User user = userService.voidUser(id, 0, restoredBy);
        return ResponseEntity.ok(user);
    }

}