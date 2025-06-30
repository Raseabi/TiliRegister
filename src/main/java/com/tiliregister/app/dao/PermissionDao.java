package com.tiliregister.app.dao;

import com.tiliregister.app.model.Permission;
import java.util.List;

public interface PermissionDao {

    Permission save(Permission permission);
    Permission findById(Long id);
    List<Permission> findAll();
}
