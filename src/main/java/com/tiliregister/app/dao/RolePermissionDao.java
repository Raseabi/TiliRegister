package com.tiliregister.app.dao;

import com.tiliregister.app.model.RolePermission;

import java.util.List;

public interface RolePermissionDao {
    RolePermission save(RolePermission rolePermission);
    RolePermission findById(Long id);
    RolePermission findByRoleAndPermissionIds(Long roleId, Long permissionId);
    List<RolePermission> findByRoleId(Long roleId);
    List<RolePermission> findByPermissionId(Long permissionId);
    boolean deleteById(Long id);
    boolean deleteByRoleAndPermissionIds(Long roleId, Long permissionId);
}
