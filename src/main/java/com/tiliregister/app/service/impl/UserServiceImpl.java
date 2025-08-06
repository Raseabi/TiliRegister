package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.UserDao;
import com.tiliregister.app.model.*;
import com.tiliregister.app.service.EmailService;
import com.tiliregister.app.service.RoleService;
import com.tiliregister.app.service.UserService;
import com.tiliregister.app.util.InternetConnectionChecker;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public User saveUser(UserRequest userRequest, String createdByUsername) {
        User createdBy = userDao.findByUsername(createdByUsername);
        if (createdBy == null || createdBy.getVoided() == 1) {
            throw new EntityNotFoundException("Creator (admin) not found");
        }

        String emailAddress = userRequest.getUser().getEmailAddress().toLowerCase().trim();
        if (isEmailUnique(emailAddress, null)) {
            throw new IllegalArgumentException("Email address already exists");
        }
        LocalDateTime createdAt = LocalDateTime.now();
        User user = new User();
        user.setEmailAddress(emailAddress);
        user.setUsername(emailAddress);
        user.setSurname(userRequest.getUser().getSurname().trim());
        user.setOthernames(userRequest.getUser().getOthernames().trim());
        user.setContactNumber(userRequest.getUser().getContactNumber());

        user.setCreatedBy(createdBy);
        user.setCreatedAt(createdAt);

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

        //Set up roles
        if (!userRequest.getRoleIds().isEmpty()) {
            Set<UserRole> userRoles = userRequest.getRoleIds().stream()
                    .map(roleId -> {
                        UserRole userRole = new UserRole();
                        Role role = roleService.getRoleById(roleId);
                        userRole.setRole(role);
                        userRole.setUser(user);
                        userRole.setCreatedBy(createdBy);
                        userRole.setCreatedAt(createdAt);

                        return userRole;

                    }).collect(Collectors.toSet());

            user.setUserRoles(userRoles);
        }
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

        return userDao.save(user);
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
    public User getUserByEmailAddress(String emailAddress) {
        return userDao.findByEmailAddress(emailAddress);
    }

    @Override
    public User updateUser(Long id, UserRequest userRequest, String updatedByUsername) {
        User existingUser = userDao.findById(id);
        User updatedBy = userDao.findByUsername(updatedByUsername);

        if (existingUser == null || existingUser.getVoided() == 1) {
            throw new EntityNotFoundException("User not found or has been deactivated");
        }

        if (updatedBy == null || updatedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Admin not found or has been deactivated");
        }

        String newEmail = userRequest.getUser().getEmailAddress().toLowerCase().trim();
        if (isEmailUnique(newEmail, id)) {
            throw new IllegalArgumentException("Email address already exists");
        }

        LocalDateTime updatedAt = LocalDateTime.now();

        // Update fields
        existingUser.setUsername(newEmail); // username = email
        existingUser.setSurname(userRequest.getUser().getSurname().trim());
        existingUser.setOthernames(userRequest.getUser().getOthernames().trim());
        existingUser.setEmailAddress(newEmail);
        existingUser.setContactNumber(userRequest.getUser().getContactNumber().trim());
        existingUser.setUpdatedBy(updatedBy);
        existingUser.setUpdatedAt(updatedAt);

        // Handle roles
        existingUser.getUserRoles().clear();

        if (!userRequest.getRoleIds().isEmpty()) {
            Set<UserRole> userRoles = userRequest.getRoleIds().stream()
                    .map(roleId -> {
                        Role role = roleService.getRoleById(roleId);
                        UserRole userRole = new UserRole();
                        userRole.setUser(existingUser);
                        userRole.setRole(role);
                        userRole.setCreatedBy(updatedBy);
                        userRole.setCreatedAt(updatedAt);
                        return userRole;
                    }).collect(Collectors.toSet());

            existingUser.getUserRoles().addAll(userRoles);
        }

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

    @Override
    public Page<User> searchUsers(String searchToken, int page, int size, String sortField, String sortOrder) {
        return userDao.searchUsers(searchToken, page, size, sortField, sortOrder);
    }
}
