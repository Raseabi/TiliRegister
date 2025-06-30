package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.PermissionDao;
import com.tiliregister.app.model.Permission;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionDao permissionDao;

    @Autowired
    public PermissionServiceImpl(PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }

    @Override
    public Permission savePermission(Permission permission) {
        return permissionDao.save(permission);
    }

    @Override
    public Permission getPermissionById(Long id) {
        return permissionDao.findById(id);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionDao.findAll();
    }
}
