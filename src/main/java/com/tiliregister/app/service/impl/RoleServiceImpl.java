package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.RoleDao;
import com.tiliregister.app.model.Role;
import com.tiliregister.app.model.Role;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.RoleService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;
    private final UserService userService;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao, @Lazy UserService userService) {
        this.roleDao = roleDao;
        this.userService = userService;
    }

    @Override
    public Role saveRole(Role role, String createdByUsername) {
        User createdBy = userService.getUserByUsername(createdByUsername);

        if (createdBy == null || createdBy.getVoided() == 1) {
            throw new EntityNotFoundException("Creator (admin) not found");
        }
        if (doesRoleExist(role.getName(), null)) {
            throw new IllegalArgumentException("Role already exist");
        }

        role.setCreatedBy(createdBy);
        role.setCreatedAt(LocalDateTime.now());

        return roleDao.save(role);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleDao.findById(id);
    }

    @Override
    public Role getRoleByName(String name) {
        return roleDao.findByName(name);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleDao.findByVoidStatus(List.of(0, 1));
    }

    @Override
    public List<Role> getActiveRoles() {
        return roleDao.findByVoidStatus(List.of(0));
    }

    @Override
    public List<Role> getInActiveRoles() {
        return roleDao.findByVoidStatus(List.of(1));
    }

    @Override
    public Role updateRole(Long id, Role role, String updatedByUsername) {
        Role existingRole = roleDao.findById(id);
        User updatedBy = userService.getUserByUsername(updatedByUsername);

        if (existingRole == null || existingRole.getVoided() == 1 || updatedBy == null || updatedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Role or admin not found");
        }
        if (doesRoleExist(role.getName(), id)) {
            throw new IllegalArgumentException("Role already exist");
        }

        // Only update mutable fields
        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());

        existingRole.setUpdatedBy(updatedBy);
        existingRole.setUpdatedAt(LocalDateTime.now());

        return roleDao.save(existingRole);
    }

    @Override
    public Role voidRole(Long id, int voidValue, String voidedByUsername) {
        User voidedBy = userService.getUserByUsername(voidedByUsername);
        Role role = roleDao.findById(id);

        if (role == null || voidedBy == null || voidedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Role or admin not found");
        }

        role.setVoided(voidValue);
        role.setVoidedBy(voidedBy);
        role.setVoidedAt(LocalDateTime.now());

        return roleDao.save(role);
    }

    @Override
    public boolean doesRoleExist(String roleName, Long excludeRoleId) {
        return roleDao.doesRoleExist(roleName, excludeRoleId);
    }
}
