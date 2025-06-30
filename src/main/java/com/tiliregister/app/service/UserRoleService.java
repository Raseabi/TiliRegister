package com.tiliregister.app.service;

import com.tiliregister.app.model.UserRole;

import java.util.List;
import java.util.Set;

public interface UserRoleService {
    UserRole saveUserRole(UserRole userRole);
    UserRole getUserRoleById(Long id);
    List<UserRole> getUserRolesByUserId(Long userId);
    List<UserRole> getUserRolesByRoleId(Long roleId);
    UserRole getUserRoleByUserAndRoleIds(Long userId, Long roleId);
    boolean deleteUserRoleById(Long id);
    boolean assignRolesToUser(Long userId, Set<Long> roleIds, String assignedByUsername);
    boolean removeRolesAssignedToUser(Long userId, Set<Long> roleIds);
}
