package com.tiliregister.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<UserRole> userRoles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="created_by", nullable = false)
    @JsonSerialize(using = UserReferenceSerializer.class)
    private User createdBy;

    @Column(name="created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="updated_by", nullable = true)
    @JsonSerialize(using = UserReferenceSerializer.class)
    private User updatedBy;

    @Column(name="updated_at", nullable = true, columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int voided;

    @ManyToOne
    @JoinColumn(name="voided_by", nullable = true)
    @JsonSerialize(using = UserReferenceSerializer.class)
    private User voidedBy;

    @Column(name="voided_at", nullable = true, columnDefinition = "TIMESTAMP")
    private LocalDateTime voidedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
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

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getVoided() {
        return voided;
    }

    public void setVoided(int voided) {
        this.voided = voided;
    }

    public User getVoidedBy() {
        return voidedBy;
    }

    public void setVoidedBy(User voidedBy) {
        this.voidedBy = voidedBy;
    }

    public LocalDateTime getVoidedAt() {
        return voidedAt;
    }

    public void setVoidedAt(LocalDateTime voidedAt) {
        this.voidedAt = voidedAt;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", userRoles=" + userRoles +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", updatedBy=" + updatedBy +
                ", updatedAt=" + updatedAt +
                ", voided=" + voided +
                ", voidedBy=" + voidedBy +
                ", voidedAt=" + voidedAt +
                '}';
    }
}
