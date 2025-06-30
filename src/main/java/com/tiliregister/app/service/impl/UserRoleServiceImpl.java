package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.UserRoleDao;
import com.tiliregister.app.model.Role;
import com.tiliregister.app.model.User;
import com.tiliregister.app.model.UserRole;
import com.tiliregister.app.service.RoleService;
import com.tiliregister.app.service.UserRoleService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleDao userRoleDao;
    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public UserRoleServiceImpl(UserRoleDao userRoleDao,  @Lazy RoleService roleService, @Lazy UserService userService) {
        this.userRoleDao = userRoleDao;
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    public UserRole saveUserRole(UserRole userRole) {
        return userRoleDao.save(userRole);
    }

    @Override
    public UserRole getUserRoleById(Long id) {
        return userRoleDao.findById(id);
    }

    @Override
    public List<UserRole> getUserRolesByUserId(Long userId) {
        return userRoleDao.findByUserId(userId);
    }

    @Override
    public List<UserRole> getUserRolesByRoleId(Long roleId) {
        return userRoleDao.findByRoleId(roleId);
    }

    @Override
    public UserRole getUserRoleByUserAndRoleIds(Long userId, Long roleId) {
        return userRoleDao.findByUserAndRoleIds(userId, roleId);
    }

    @Override
    public boolean deleteUserRoleById(Long id) {
       return userRoleDao.deleteById(id);
    }

    @Override
    @Transactional
    public boolean assignRolesToUser(Long userId, Set<Long> roleIds, String assignedByUsername) {
        User user = userService.getUserById(userId);
        User assignedBy = userService.getUserByUsername(assignedByUsername);
        if (user == null || assignedBy == null || assignedBy.getVoided() == 1) {
            throw new EntityNotFoundException("User or admin not found");
        }

        for (Long roleId : roleIds) {
            Role role = roleService.getRoleById(roleId);
            if (role == null) {
                throw new EntityNotFoundException("Role with ID " + roleId + " not found");
            }

            if (userRoleDao.findByUserAndRoleIds(userId, role.getId()) != null) continue;

            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRole.setCreatedBy(assignedBy);
            userRole.setCreatedAt(LocalDateTime.now());

             userRoleDao.save(userRole);
        }
         return true;
    }

    @Override
    @Transactional
    public boolean removeRolesAssignedToUser(Long userId, Set<Long> roleIds){
        User user = userService.getUserById(userId);

        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        for (Long roleId : roleIds) {
            UserRole userRole = userRoleDao.findByUserAndRoleIds(userId, roleId);
            if (userRole == null) {
                throw new EntityNotFoundException("UserRole not found");
            }

            userRoleDao.deleteById(userRole.getId());
        }
        return true;
    }



}
