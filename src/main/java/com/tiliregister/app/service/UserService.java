package com.tiliregister.app.service;

import com.tiliregister.app.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user, String createdByUsername);

    User getUserById(Long id);

    User getUserByUsername(String username);

    User findByEmailAddress(String emailAddress);

    List<User> getAllUsers();

    List<User> getActiveUsers();

    List<User> getInActiveUsers();

    User updateUser(Long id, User user, String updatedByUsername);

    User voidUser(Long id, int voidValue, String voidedByUsername);

    boolean isEmailUnique(String emailAddress, Long excludeUserId);
    boolean isUsernameUnique(String username, Long excludeUserId);

}
