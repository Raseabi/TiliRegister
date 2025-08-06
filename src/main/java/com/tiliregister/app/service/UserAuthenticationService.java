package com.tiliregister.app.service;

import com.tiliregister.app.model.UserAuthentication;
import org.springframework.security.core.Authentication;

public interface UserAuthenticationService {
    UserAuthentication saveUserAuthentication(UserAuthentication userAuthentication);
    UserAuthentication getUserAuthenticationById(Long id);
    UserAuthentication getUserAuthenticationByUserId(Long userId);
    boolean changePassword(Long userId, String password, String passwordStatus);
    int incrementLoginAttempt(Long userId);
    int incrementResetPasswordAttempt(Long userId);
    boolean resetLoginAttempt(Long userId);
    boolean resetResetPasswordAttempt(Long userId);
    boolean authenticateUser(String username, String rawPassword);
    boolean changePasswordProcess(String username, String newPassword, String passwordStatus);
    boolean isLoginAllowed(Long userId);
   boolean isEmailRegistered(String emailAddress);
}
