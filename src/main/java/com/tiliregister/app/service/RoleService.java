package com.tiliregister.app.service;

import com.tiliregister.app.model.Role;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {
    Role saveRole(Role role, String createdByUsername);
    Role getRoleById(Long id);
    Role getRoleByName(String name);
    List<Role> getAllRoles();
    List<Role> getActiveRoles();
    List<Role> getInActiveRoles();
    Role updateRole(Long id, Role role, String updatedByUsername);
    Role voidRole(Long roleId, int voidValue, String voidedByUsername);
    boolean doesRoleExist(String roleName, Long excludeRoleId);
    Page<Role> searchRoles(String searchToken, int page, int size, String sortField, String sortOrder);
}
