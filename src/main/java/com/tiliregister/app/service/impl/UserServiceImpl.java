package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.UserDao;
import com.tiliregister.app.model.*;
import com.tiliregister.app.service.EmailService;
import com.tiliregister.app.service.RoleService;
import com.tiliregister.app.service.UserRoleService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleService roleService;
    private final EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleService roleService, EmailService emailService) {
        this.userDao = userDao;
        this.roleService = roleService;
        this.emailService = emailService;
    }

    @Override
    public User saveUser(User user, String createdByUsername) {
        User createdBy = userDao.findByUsername(createdByUsername);
        if (createdBy == null || createdBy.getVoided() == 1) {
            throw new EntityNotFoundException("Creator (admin) not found");
        }
        if (isEmailUnique(user.getEmailAddress().toLowerCase().trim(), null)) {
            throw new IllegalArgumentException("Email address already exists");
        }
        if (isUsernameUnique(user.getUsername(), null)) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setEmailAddress(user.getEmailAddress().toLowerCase().trim());
        user.setCreatedBy(createdBy);
        user.setCreatedAt(LocalDateTime.now());

        // Setup authentication
        String rawPassword = PasswordGenerator.generatePassword(8);

        if (rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        UserAuthentication auth = new UserAuthentication();
        auth.setPassword(passwordEncoder.encode(rawPassword));
        auth.setPasswordStatus("NEW");
        auth.setLoginAttempts(0);
        auth.setResetPasswordAttempts(0);
        auth.setPasswordDateModified(LocalDateTime.now());
        auth.setUser(user);
        user.setUserAuthentication(auth);
        //Send an email
        if (InternetConnectionChecker.isInternetAvailable()) {
            emailService.sendEmail(
                    user.getEmailAddress(),
                    "Welcome to Tili!",
                    "Dear " + user.getOthernames() + " " + user.getSurname() + ",\n\n" +
                            "Welcome! Your registration is now complete.\n\n" +
                            "Below are your credentials:\n\n" +
                            "Username: " + user.getUsername() + "\n" +
                            "Password: " + rawPassword + "\n\n" +
                            "You are strongly recommended to change this password immediately.\n\n" +
                            "Regards,\n" +
                            "System Admin"
            );
        }

        return userDao.save(user); // Cascade will persist UserRole and Auth
    }

    @Override
    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User findByEmailAddress(String emailAddress) {
        return userDao.findByEmailAddress(emailAddress);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findByVoidStatus(List.of(0, 1));
    }

    @Override
    public List<User> getActiveUsers() {
        return userDao.findByVoidStatus(List.of(0));
    }

    @Override
    public List<User> getInActiveUsers() {
        return userDao.findByVoidStatus(List.of(1));
    }

    @Override
    public User updateUser(Long id, User user, String updatedByUsername) {
        User existingUser = userDao.findById(id);
        User updatedBy = userDao.findByUsername(updatedByUsername);

        if (existingUser == null || existingUser.getVoided() == 1 || updatedBy == null || updatedBy.getVoided() == 1) {
            throw new EntityNotFoundException("User (to updated) or Admin not found");
        }
        if (isEmailUnique(user.getEmailAddress(), id)) {
            throw new IllegalArgumentException("Email address already exists");
        }
        if (isUsernameUnique(user.getUsername(), id)) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Only update mutable fields
        existingUser.setUsername(user.getUsername());
        existingUser.setSurname(user.getSurname());
        existingUser.setOthernames(user.getOthernames());
        existingUser.setEmailAddress(user.getEmailAddress().toLowerCase().trim());
        existingUser.setContactNumber(user.getContactNumber());

        existingUser.setUpdatedBy(updatedBy);
        existingUser.setUpdatedAt(LocalDateTime.now());

        return userDao.save(existingUser);
    }

    @Override
    public User voidUser(Long id, int voidValue, String voidedByUsername) {
        User voidedBy = userDao.findByUsername(voidedByUsername);
        User user = userDao.findById(id);

        if (user == null || voidedBy == null || voidedBy.getVoided() == 1) {
            throw new EntityNotFoundException("User or admin not found");
        }

        user.setVoided(voidValue);
        user.setVoidedBy(voidedBy);
        user.setVoidedAt(LocalDateTime.now());

        return userDao.save(user);
    }

    @Override
    public boolean isUsernameUnique(String username, Long excludeUserId) {
        return userDao.isUsernameUnique(username, excludeUserId);
    }

    @Override
    public boolean isEmailUnique(String emailAddress, Long excludeUserId) {
        return userDao.isEmailUnique(emailAddress, excludeUserId);
    }
}
