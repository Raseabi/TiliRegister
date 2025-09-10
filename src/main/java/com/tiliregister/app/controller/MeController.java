package com.tiliregister.app.controller;

import com.tiliregister.app.model.ChangePasswordRequest;
import com.tiliregister.app.model.LoginResponse;
import com.tiliregister.app.model.User;
import com.tiliregister.app.model.UserAuthentication;
import com.tiliregister.app.security.CustomUserDetails;
import com.tiliregister.app.service.MeService;
import com.tiliregister.app.service.UserAuthenticationService;
import com.tiliregister.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final MeService meService;

    @Autowired
    public MeController(MeService meService) {
        this.meService = meService;
    }

    @GetMapping
    public ResponseEntity<User> getMe(Authentication authentication){
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = userDetails.getUserId(); // add getUserId() in your CustomUserDetails
        return ResponseEntity.ok(meService.getMeById(userId));
    }

    @PutMapping
    public ResponseEntity<User> updateMe(@RequestBody User user, Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(meService.updateMe(username, user, username));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(meService.changeMePassword(username, changePasswordRequest.getCurrentPassword(), changePasswordRequest.getNewPassword()));
    }
}
