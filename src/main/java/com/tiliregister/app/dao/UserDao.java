package com.tiliregister.app.dao;

import com.tiliregister.app.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserDao {
    User save(User user);
    User findById(Long id);
    User findByUsername(String username);
    User findByEmailAddress(String emailAddress);
    List<User> findByVoidStatus(List<Integer> voidStatus);
    boolean isEmailUnique(String emailAddress, Long excludeUserId);
    boolean isUsernameUnique(String username, Long excludeUserId);
    Page<User> searchUsers(String searchToken, int page, int size, String sortField, String sortOrder);
}
