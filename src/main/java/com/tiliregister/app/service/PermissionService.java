package com.tiliregister.app.service;

import com.tiliregister.app.model.Permission;

import java.util.List;

public interface PermissionService {
    Permission savePermission(Permission permission);
    Permission getPermissionById(Long id);
    List<Permission> getAllPermissions();
}
