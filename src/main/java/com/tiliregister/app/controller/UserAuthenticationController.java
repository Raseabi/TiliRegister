package com.tiliregister.app.controller;

import com.tiliregister.app.model.*;
import com.tiliregister.app.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private final UserAuthenticationService userAuthenticationService;

    @Autowired
    public UserAuthenticationController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.generateToken(authentication.getName());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        boolean isAuthenticated = userAuthenticationService.authenticateUser(
                changePasswordRequest.getUsername(),
                changePasswordRequest.getOldPassword());

        if (isAuthenticated) {
            boolean isPasswordChanged = userAuthenticationService.changePasswordProcess(changePasswordRequest.getUsername(), changePasswordRequest.getNewPassword());

            if (isPasswordChanged) {
                return ResponseEntity.ok("Password change successful");
            } else {
                return ResponseEntity.ok("Password not changed");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, "Change password: Invalid credentials"));
        }
    }

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        return ResponseEntity.ok(auth.getName());
    }

    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuth(Authentication auth) {
        return ResponseEntity.ok(auth.getAuthorities());
    }
}
