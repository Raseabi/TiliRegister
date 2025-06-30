package com.tiliregister.app.dao;

import com.tiliregister.app.model.Role;

import java.util.List;

public interface RoleDao {
    Role save(Role role);
    Role findById(Long id);
    Role findByName(String name);
    List<Role> findByVoidStatus(List<Integer> voidStatus);
    boolean doesRoleExist(String roleName, Long excludeRoleId);
}
