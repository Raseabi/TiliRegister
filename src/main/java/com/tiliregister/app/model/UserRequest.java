package com.tiliregister.app.model;

import java.util.Set;

public class UserRequest {
    private User user;
    private Set<Long> roleIds;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
