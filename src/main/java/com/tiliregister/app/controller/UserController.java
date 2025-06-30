package com.tiliregister.app.controller;

import com.tiliregister.app.model.User;
import com.tiliregister.app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user, Authentication authentication) {
        String username = authentication.getName();
        User savedUser = userService.saveUser(user, username);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user, Authentication authentication) {
            String updatedBy = authentication.getName();
            User updated = userService.updateUser(id, user, updatedBy);
            return ResponseEntity.ok(updated);

    }

    @PutMapping("/{id}/void")
    public ResponseEntity<User> voidUser(@PathVariable Long id, Authentication authentication) {
        String voidedBy = authentication.getName(); // current logged-in username
        User user = userService.voidUser(id, 1, voidedBy);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<User> restoreUser(@PathVariable Long id, Authentication authentication) {
        String restoredBy = authentication.getName(); // current logged-in username
        User user = userService.voidUser(id, 0, restoredBy);
        return ResponseEntity.ok(user);
    }

}
