package com.tiliregister.app.controller;

import com.tiliregister.app.model.*;
import com.tiliregister.app.security.JwtUtil;
import com.tiliregister.app.service.PasswordResetTokenService;
import com.tiliregister.app.service.UserAuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private final UserAuthenticationService userAuthenticationService;

    @Autowired
    public UserAuthenticationController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        // 1. This checks username + password against UserDetailsService
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()
                )
        );

        // 2. Get the user
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Generate JWT using validated username
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false) // change for production HTTPS
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Lax")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Access token refreshed"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie clearAccessToken = ResponseCookie.from("accessToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();

        ResponseCookie clearRefreshToken = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshToken.toString());

        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("emailAddress");
        return ResponseEntity.ok(userAuthenticationService.isEmailRegistered(email));
    }

    @PostMapping("/send-reset-link")
    public ResponseEntity<?> sendResetLink(@RequestBody Map<String, String> payload) {
        String email = payload.get("emailAddress");
        return ResponseEntity.ok(passwordResetTokenService.resetPasswordLinkProcess(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        try {
            passwordResetTokenService.resetPassword(token, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password reset successful"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to reset password"));
        }
    }

}
