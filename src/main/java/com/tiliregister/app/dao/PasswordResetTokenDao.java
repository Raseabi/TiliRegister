package com.tiliregister.app.dao;

import com.tiliregister.app.model.PasswordResetToken;

public interface PasswordResetTokenDao {
    PasswordResetToken save(PasswordResetToken passwordResetToken);
    PasswordResetToken findById(Long id);
    PasswordResetToken findByToken(String token);
    boolean deleteById(Long id);
}
