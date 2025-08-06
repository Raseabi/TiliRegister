package com.tiliregister.app.service;

import com.tiliregister.app.model.User;

public interface MeService {
    User getMe(String username);
    User updateMe(String username, User user, String updatedByUsername);
    boolean changeMePassword(String username, String currentPassword, String newPassword);
}
