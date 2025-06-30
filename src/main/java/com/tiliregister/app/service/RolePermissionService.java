package com.tiliregister.app.service;

import com.tiliregister.app.model.RolePermission;

import java.util.List;
import java.util.Set;

public interface RolePermissionService {
    RolePermission saveRolePermission(RolePermission rolePermission, String createdByUsername);
    RolePermission getRolePermissionById(Long id);
    RolePermission getRolePermissionByRoleAndPermissionIds(Long roleId, Long permissionId);
    List<RolePermission> getRolePermissionsByRoleId(Long roleId);
    List<RolePermission> getRolePermissionsByPermissionId(Long permissionId);
    boolean deleteRolePermissionById(Long id);
    boolean deleteRolePermissionByRoleAndPermissionIds(Long roleId, Long permissionId);
    boolean assignPermissionsToRole(Long roleId, Set<Long> permissionIds, String assignedByUsername);
    boolean removePermissionsFromRole(Long roleId, Set<Long> permissionIds);
}
