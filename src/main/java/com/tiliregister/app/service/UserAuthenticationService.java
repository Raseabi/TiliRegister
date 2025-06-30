package com.tiliregister.app.service;

import com.tiliregister.app.model.UserAuthentication;

public interface UserAuthenticationService {
    UserAuthentication saveUserAuthentication(UserAuthentication userAuthentication);
    UserAuthentication getUserAuthenticationById(Long id);
    UserAuthentication getUserAuthenticationByUserId(Long userId);
    boolean updatePassword(String password);
    int incrementLoginAttempt(Long userId);
    int incrementResetPasswordAttempt(Long userId);
    boolean resetLoginAttempt(Long userId);
    boolean resetResetPasswordAttempt(Long userId);
}
