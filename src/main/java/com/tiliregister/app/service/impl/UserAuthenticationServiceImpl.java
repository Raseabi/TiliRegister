package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.UserAuthenticationDao;
import com.tiliregister.app.util.InternetConnectionChecker;
import com.tiliregister.app.model.User;
import com.tiliregister.app.model.UserAuthentication;
import com.tiliregister.app.service.EmailService;
import com.tiliregister.app.service.UserAuthenticationService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Transactional
    public boolean changePassword(Long userId, String password, String passwordStatus) {

        UserAuthentication userAuthentication = userAuthenticationDao.findByUserId(userId);
        if (userAuthentication == null) {
            throw new EntityNotFoundException("User credentials not found");
        }
        userAuthentication.setPassword(passwordEncoder.encode(password));
        userAuthentication.setPasswordStatus(passwordStatus);
        userAuthentication.setPasswordDateModified(LocalDateTime.now());
        userAuthentication.setLoginAttempts(1);
        userAuthentication.setResetPasswordAttempts(1);

        return true;
    }

    @Override
    public boolean changePasswordProcess(String username, String newPassword, String passwordStatus) {
        User user = userService.getUserByUsername(username);

        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be 8+ characters long");
        }
        if (!newPassword.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter, one uppercase letter, and one special character.");
        }
        if (!changePassword(user.getId(), newPassword, passwordStatus)) return false;

        if (InternetConnectionChecker.isInternetAvailable()) {
                sendChangePasswordNotification(user);
        }
        return true;
    }

    private void sendChangePasswordNotification(User user){
        emailService.sendEmail(
                user.getEmailAddress(),
                "Password Changed - Tili System",
                "Dear " + user.getOthernames() + " " + user.getSurname() + ",\n\n" +
                        "Your password was changed successfully. If you did not perform this action, please contact support immediately.\n\n" +
                        "Regards,\nSystem Admin"
        );
    }

    @Override
    public boolean isLoginAllowed(Long userId) {
        return false;
    }

    @Override
    public boolean isEmailRegistered(String emailAddress) {
        User user = userService.getUserByEmailAddress(emailAddress);
        return user != null && user.getVoided() != 1;
    }
}
