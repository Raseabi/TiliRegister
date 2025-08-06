package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.PasswordResetTokenDao;
import com.tiliregister.app.model.PasswordResetToken;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.EmailService;
import com.tiliregister.app.service.PasswordResetTokenService;
import com.tiliregister.app.service.UserAuthenticationService;
import com.tiliregister.app.service.UserService;
import com.tiliregister.app.util.InternetConnectionChecker;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    @Value("${tili.reset-password.base-url}")
    private String resetBaseUrl;
    private final EmailService emailService;
    private final PasswordResetTokenDao passwordResetTokenDao;
    private final UserService userService;
    private final UserAuthenticationService userAuthenticationService;

    @Autowired
    public PasswordResetTokenServiceImpl(EmailService emailService, PasswordResetTokenDao passwordResetTokenDao, UserService userService, UserAuthenticationService userAuthenticationService) {
        this.emailService = emailService;
        this.passwordResetTokenDao = passwordResetTokenDao;
        this.userService = userService;
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    public PasswordResetToken createPasswordResetToken(PasswordResetToken passwordResetToken) {
        return passwordResetTokenDao.save(passwordResetToken);
    }

    @Override
    public PasswordResetToken getPasswordResetTokenById(Long id) {
        return passwordResetTokenDao.findById(id);
    }

    @Override
    public PasswordResetToken getPasswordResetTokenByToken(String token) {
        return passwordResetTokenDao.findByToken(token);
    }

    @Override
    public PasswordResetToken updatePasswordResetToken(String token, String newPassword) {
        PasswordResetToken existingPasswordResetToken = passwordResetTokenDao.findByToken(token);
        existingPasswordResetToken.setUsed(true);
        return passwordResetTokenDao.save(existingPasswordResetToken);
    }


    @Override
    public String generatePasswordResetToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public String generateResetLink(String token) {
        return resetBaseUrl + "?token=" + token;
    }

    @Override
    public boolean sendResetLink(String email, String link) {
        User user = userService.getUserByEmailAddress(email);
        if (InternetConnectionChecker.isInternetAvailable()) {
            emailService.sendEmail(
                    user.getEmailAddress(),
                    "Tili - Password Reset",
                    "Dear " + user.getOthernames() + " " + user.getSurname() + ",\n\n" +
                            "Below is your password reset link.\n\n" +
                            link + "\n\n" +
                            "Click on the link or copy and paste it on your browser\n\n" +
                            "Regards,\n" +
                            "System Admin"
            );
            return true;
        }
        return false;
    }

    @Override
    public boolean resetPasswordLinkProcess(String emailAddress) {
        try {
            String token = generatePasswordResetToken();
            String link = generateResetLink(token);

            User user = userService.getUserByEmailAddress(emailAddress);

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
            resetToken.setUsed(false);

            passwordResetTokenDao.save(resetToken);

            return sendResetLink(emailAddress, link);

        } catch (Exception e) {
            // Consider logging here
            throw new RuntimeException("Failed to process password reset link.");
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenDao.findByToken(token);
        return passwordResetToken != null && !passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now());
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenDao.findByToken(token);

        if (passwordResetToken == null) {
            throw new IllegalArgumentException("Invalid password reset token");
        }

        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token has expired");
        }

        try {
            Long userId = passwordResetToken.getUser().getId();

            userAuthenticationService.changePassword(userId, newPassword, "RESET");
            userAuthenticationService.resetResetPasswordAttempt(userId);
            userAuthenticationService.resetLoginAttempt(userId);

            passwordResetTokenDao.deleteById(passwordResetToken.getId());

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset password for token: " + token, e);
        }
    }

}