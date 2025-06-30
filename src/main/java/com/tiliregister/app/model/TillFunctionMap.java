package com.tiliregister.app.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "till_function_map")
public class TillFunctionMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tili_id", nullable = false)
    private Till till;

    @ManyToOne
    @JoinColumn(name = "function_id", nullable = false)
    private TillFunction tillFunction;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @JsonSerialize(using = UserReferenceSerializer.class)
    private User createdBy;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Till getTill() {
        return till;
    }

    public void setTill(Till till) {
        this.till = till;
    }

    public TillFunction getTillFunction() {
        return tillFunction;
    }

    public void setTillFunction(TillFunction tillFunction) {
        this.tillFunction = tillFunction;
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
        return "TillFunctionMap{" +
                "id=" + id +
                ", till=" + till +
                ", tillFunction=" + tillFunction +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}
