package com.tiliregister.app.dao;

import com.tiliregister.app.model.User;

public interface MeDao {
    User findMeById(Long id);
    User updateMe(User user);
}
