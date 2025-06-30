package com.tiliregister.app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "user_roles")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false) // FK to roles.id
    private Role role;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @JsonSerialize(using = UserReferenceSerializer.class)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }
    @JsonProperty("user")
    public Map<String, Object> getUserSummary() {
        return Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "surname", user.getSurname(),
                "othernames", user.getOthernames()
        );
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonIgnore
    public Role getRole() {
        return role;
    }
    @JsonProperty("role")
    public Map<String, Object> getRoleSummary() {
        if (role == null) return null;
        return Map.of(
                "id", role.getId(),
                "name", role.getName(),
                "description", role.getDescription()
        );

    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", user=" + user +
                ", role=" + role +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}
