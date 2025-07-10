package com.tiliregister.app.service;

import com.tiliregister.app.model.UserAuthentication;
import org.springframework.security.core.Authentication;

public interface UserAuthenticationService {
    UserAuthentication saveUserAuthentication(UserAuthentication userAuthentication);
    UserAuthentication getUserAuthenticationById(Long id);
    UserAuthentication getUserAuthenticationByUserId(Long userId);
    boolean updateUserAuthentication(Long id, String rawPassword, String passwordStatus, Authentication authentication);
    boolean changePassword(Long userId, String password);
    int incrementLoginAttempt(Long userId);
    int incrementResetPasswordAttempt(Long userId);
    boolean resetLoginAttempt(Long userId);
    boolean resetResetPasswordAttempt(Long userId);
    boolean authenticateUser(String username, String rawPassword);
    boolean changePasswordProcess(String username, String newPassword);
    boolean isLoginAllowed(Long userId);
}
