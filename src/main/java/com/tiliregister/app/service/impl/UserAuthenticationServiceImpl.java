package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.UserAuthenticationDao;
import com.tiliregister.app.model.InternetConnectionChecker;
import com.tiliregister.app.model.User;
import com.tiliregister.app.model.UserAuthentication;
import com.tiliregister.app.service.EmailService;
import com.tiliregister.app.service.UserAuthenticationService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final UserAuthenticationDao userAuthenticationDao;
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserAuthenticationServiceImpl(UserAuthenticationDao userAuthenticationDao, UserService userService, EmailService emailService) {
        this.userAuthenticationDao = userAuthenticationDao;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public UserAuthentication saveUserAuthentication(UserAuthentication userAuthentication) {
        return userAuthenticationDao.save(userAuthentication);
    }

    @Override
    public UserAuthentication getUserAuthenticationById(Long id) {
        return userAuthenticationDao.findById(id);
    }

    @Override
    public UserAuthentication getUserAuthenticationByUserId(Long userId) {
        return userAuthenticationDao.findByUserId(userId);
    }

    @Override
    public boolean updateUserAuthentication(Long id, String rawPassword, String passwordStatus, Authentication authentication) {
        UserAuthentication userAuth = userAuthenticationDao.findById(id);
        if (userAuth == null) {
            throw new EntityNotFoundException("User credentials not found");
        }
        try {
            userAuth.setPassword(passwordEncoder.encode(rawPassword));
            userAuth.setLoginAttempts(1);
            userAuth.setResetPasswordAttempts(1);
            userAuth.setPasswordStatus(passwordStatus);
            userAuth.setPasswordDateModified(LocalDateTime.now());

            userAuthenticationDao.save(userAuth);

            //Send an email
            if (InternetConnectionChecker.isInternetAvailable()) {
                emailService.sendEmail(
                        userAuth.getUser().getEmailAddress(),
                        passwordStatus.equalsIgnoreCase("Reset") ? "Password reset notification" : "Password update notification",
                        "Dear " + userAuth.getUser().getOthernames() + " " + userAuth.getUser().getSurname() + ",\n\n" +
                                "Welcome! This message notifies you of the change this your login credentials.\n\n" +
                                "Below are your credentials:\n\n" +
                                "Username: " + userAuth.getUser().getUsername() + "\n" +
                                "Password: " + rawPassword + "\n\n" +
                                "Status: " + passwordStatus + "\n\n" +
                                "You are strongly recommended to keep your login details private.\n\n" +
                                "Regards,\n" +
                                "System Admin"
                );
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        if (!password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
            return false;
        }
        UserAuthentication userAuthentication = userAuthenticationDao.findByUserId(userId);
        if (userAuthentication == null) {
            throw new EntityNotFoundException("User not found");
        }
        userAuthentication.setPassword(passwordEncoder.encode(password));

        return true; // JPA auto-flushes due to @Transactional
    }


    @Override
    public int incrementLoginAttempt(Long userId) {
        UserAuthentication userAuthentication = userAuthenticationDao.findByUserId(userId);

        userAuthentication.setLoginAttempts((userAuthentication.getLoginAttempts() + 1));
        userAuthenticationDao.save(userAuthentication);

        return userAuthentication.getLoginAttempts();
    }

    @Override
    public int incrementResetPasswordAttempt(Long userId) {
        UserAuthentication userAuthentication = userAuthenticationDao.findByUserId(userId);

        userAuthentication.setResetPasswordAttempts((userAuthentication.getResetPasswordAttempts() + 1));
        userAuthenticationDao.save(userAuthentication);

        return userAuthentication.getResetPasswordAttempts();
    }

    @Override
    public boolean resetLoginAttempt(Long userId) {
        try {
            UserAuthentication userAuthentication = userAuthenticationDao.findByUserId(userId);
            userAuthentication.setLoginAttempts(1);
            userAuthenticationDao.save(userAuthentication);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean resetResetPasswordAttempt(Long userId) {
        try {
            UserAuthentication userAuthentication = userAuthenticationDao.findByUserId(userId);
            userAuthentication.setResetPasswordAttempts(1);
            userAuthenticationDao.save(userAuthentication);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean authenticateUser(String username, String rawPassword) {
        User user = userService.getUserByUsername(username);

        if (user == null || user.getVoided() == 1) {
            throw new EntityNotFoundException("User not found or inactive");
        }

        UserAuthentication userAuth = user.getUserAuthentication();

        if (userAuth == null) {
            throw new EntityNotFoundException("Authentication credentials not found");
        }

        return passwordEncoder.matches(rawPassword, userAuth.getPassword());
    }

    @Override
    public boolean changePasswordProcess(String username, String newPassword) {
        User user = userService.getUserByUsername(username);
        if (!InternetConnectionChecker.isInternetAvailable()) return false;

        if (!changePassword(user.getId(), newPassword)) return false;

        emailService.sendEmail(
                user.getEmailAddress(),
                "Password Changed - Tili System",
                "Dear " + user.getOthernames() + " " + user.getSurname() + ",\n\n" +
                        "Your password was changed successfully. If you did not perform this action, please contact support immediately.\n\n" +
                        "Regards,\nSystem Admin"
        );
        return true;
    }

    @Override
    public boolean isLoginAllowed(Long userId) {
        return false;
    }


}
