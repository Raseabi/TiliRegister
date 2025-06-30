package com.tiliregister.app.dao;

import com.tiliregister.app.model.UserAuthentication;

public interface UserAuthenticationDao {
    UserAuthentication save(UserAuthentication userAuthentication);
    UserAuthentication findById(Long id);
    UserAuthentication findByUserId(Long userId);
}
