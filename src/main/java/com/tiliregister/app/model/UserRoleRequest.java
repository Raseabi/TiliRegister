package com.tiliregister.app.model;

import java.util.Set;

public class UserRoleRequest {
    private Long userId;
    private Set<Long> roleIds;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
