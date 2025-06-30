package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.RolePermissionDao;
import com.tiliregister.app.model.Permission;
import com.tiliregister.app.model.Role;
import com.tiliregister.app.model.RolePermission;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.PermissionService;
import com.tiliregister.app.service.RolePermissionService;
import com.tiliregister.app.service.RoleService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionDao rolePermissionDao;
    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    @Autowired
    public RolePermissionServiceImpl(RolePermissionDao rolePermissionDao, UserService userService, RoleService roleService, PermissionService permissionService) {
        this.rolePermissionDao = rolePermissionDao;
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Override
    public RolePermission saveRolePermission(RolePermission rolePermission, String createdByUsername) {
        Role role = roleService.getRoleById(rolePermission.getRole().getId());
        Permission permission = permissionService.getPermissionById(rolePermission.getPermission().getId());
        User createdBy = userService.getUserByUsername(createdByUsername);

        if (role == null || permission == null || createdBy == null || createdBy.getVoided() == 1) {
            throw new EntityNotFoundException("Role, Permission or CreatedBy not found");
        }
        // Assign managed entities
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission.setCreatedBy(createdBy);
        rolePermission.setCreatedAt(LocalDateTime.now());

        return rolePermissionDao.save(rolePermission);
    }

    @Override
    public RolePermission getRolePermissionById(Long id) {
        return rolePermissionDao.findById(id);
    }

    @Override
    public RolePermission getRolePermissionByRoleAndPermissionIds(Long roleId, Long permissionId) {
        return rolePermissionDao.findByRoleAndPermissionIds(roleId, permissionId);
    }

    @Override
    public List<RolePermission> getRolePermissionsByRoleId(Long roleId) {
        return rolePermissionDao.findByRoleId(roleId);
    }

    @Override
    public List<RolePermission> getRolePermissionsByPermissionId(Long permissionId) {
        return rolePermissionDao.findByPermissionId(permissionId);
    }

    @Override
    public boolean deleteRolePermissionById(Long id) {
        return rolePermissionDao.deleteById(id);
    }

    @Override
    public boolean deleteRolePermissionByRoleAndPermissionIds(Long roleId, Long permissionId) {
        return rolePermissionDao.deleteByRoleAndPermissionIds(roleId, permissionId);
    }

    @Override
    @Transactional
    public boolean assignPermissionsToRole(Long roleId, Set<Long> permissionIds, String assignedBysername) {
        Role role = roleService.getRoleById(roleId);
        User admin = userService.getUserByUsername(assignedBysername);

        if (role == null || admin == null || admin.getVoided() == 1) {
            throw new EntityNotFoundException("Role or admin not found");
        }

        for (Long permissionId : permissionIds) {
            Permission permission = permissionService.getPermissionById(permissionId);
            if (permission == null) continue; // Skip invalid permissions

            // Check if already mapped (optional to avoid duplicates)
            if (rolePermissionDao.findByRoleAndPermissionIds(role.getId(), permission.getId()) != null) continue;

            RolePermission rp = new RolePermission();
            rp.setRole(role);
            rp.setPermission(permission);
            rp.setCreatedBy(admin);
            rp.setCreatedAt(LocalDateTime.now());

            rolePermissionDao.save(rp);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removePermissionsFromRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleService.getRoleById(roleId);

        if (role == null) {
            throw new EntityNotFoundException("Role or admin not found");
            //return false;
        }

        for (Long permissionId : permissionIds) {
            // Check if already mapped (optional to avoid duplicates)
            RolePermission rolePermission = rolePermissionDao.findByRoleAndPermissionIds(role.getId(), permissionId);
            if (rolePermission == null) {
                throw new EntityNotFoundException("RolePermission not found");
            }

            rolePermissionDao.deleteById(rolePermission.getId());
        }
        return true;
    }
}
