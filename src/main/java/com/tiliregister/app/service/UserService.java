package com.tiliregister.app.service;

import com.tiliregister.app.model.User;
import com.tiliregister.app.model.UserRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    User saveUser(UserRequest userRequest, String createdByUsername);

    User getUserById(Long id);

    User getUserByUsername(String username);

    User getUserByEmailAddress(String emailAddress);

    User updateUser(Long id, UserRequest userRequest, String updatedByUsername);

    User voidUser(Long id, int voidValue, String voidedByUsername);

    boolean isEmailUnique(String emailAddress, Long excludeUserId);

    boolean isUsernameUnique(String username, Long excludeUserId);

    Page<User> searchUsers(String searchToken, int page, int size, String sortField, String sortOrder);
}
