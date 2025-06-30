package com.tiliregister.app.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_outs")
public class CashOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "till_id", nullable = false)
    private Till till;

    @Column(nullable = false)
    private BigDecimal amount;

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

    public Till getTill() {
        return till;
    }

    public void setTill(Till till) {
        this.till = till;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
        return "CashOut{" +
                "id=" + id +
                ", till=" + till +
                ", amount=" + amount +
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
