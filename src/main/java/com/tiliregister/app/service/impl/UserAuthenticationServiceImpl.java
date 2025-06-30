package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.UserAuthenticationDao;
import com.tiliregister.app.model.UserAuthentication;
import com.tiliregister.app.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final UserAuthenticationDao userAuthenticationDao;

    @Autowired
    public UserAuthenticationServiceImpl(UserAuthenticationDao userAuthenticationDao) {
        this.userAuthenticationDao = userAuthenticationDao;
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
    public boolean updatePassword(String password) {
        return false;
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

}
