package com.tiliregister.app.service;

import com.tiliregister.app.model.PasswordResetToken;

public interface PasswordResetTokenService {
    PasswordResetToken createPasswordResetToken(PasswordResetToken passwordResetToken);
    PasswordResetToken getPasswordResetTokenById(Long id);
    PasswordResetToken getPasswordResetTokenByToken(String token);
    PasswordResetToken updatePasswordResetToken(String token, String newPassword);
    String generatePasswordResetToken();
    String generateResetLink(String token);
    boolean sendResetLink(String email, String link);
    boolean resetPasswordLinkProcess(String emailAddress);
    boolean isTokenValid(String token);
    boolean resetPassword(String token, String newPassword);
}
