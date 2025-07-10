package com.tiliregister.app.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "global_config")
public class GlobalConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", unique = true, nullable = false)
    private String configKey;

    @Column(nullable = false)
    private String value;

    private String description;

    @ManyToOne
    @JoinColumn(name = "changed_by", nullable = true)
    @JsonSerialize(using = UserSerializer.class)
    private User changedBy;

    @Column(name = "changed_at", nullable = true, columnDefinition = "TIMESTAMP")
    private LocalDateTime changedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    @Override
    public String toString() {
        return "GlobalConfig{" +
                "id=" + id +
                ", configKey='" + configKey + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                ", changedBy=" + changedBy +
                ", changedAt=" + changedAt +
                '}';
    }
}
