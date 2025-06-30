package com.tiliregister.app.dao;

import com.tiliregister.app.model.UserRole;

import java.util.List;

public interface UserRoleDao {
    UserRole save(UserRole userRole);
    UserRole findById(Long id);
    UserRole findByUserAndRoleIds(Long userId, Long roleId);
    List<UserRole> findByUserId(Long userId);
    List<UserRole> findByRoleId(Long roleId);
    boolean deleteById(Long id);
    boolean deleteByUserAndRoleIds(Long userId, Long roleId);
}
