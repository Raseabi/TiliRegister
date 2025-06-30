package com.tiliregister.app.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "till_id", nullable = false)
    private Till till;

    @ManyToOne
    @JoinColumn(name = "function_id", nullable = false)
    private TillFunction tillFunction;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "float_change", nullable = false)
    private BigDecimal floatChange;

    @Column(name = "cash_change", nullable = false)
    private BigDecimal cashChange;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @JsonSerialize(using = UserReferenceSerializer.class)
    private User createdBy;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "updated_by", nullable = true)
    @JsonSerialize(using = UserReferenceSerializer.class)
    private User updatedBy;

    @Column(name = "updated_at", nullable = true, columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int voided;

    @ManyToOne
    @JoinColumn(name = "voided_by", nullable = true)
    @JsonSerialize(using = UserReferenceSerializer.class)
    private User voidedBy;

    @Column(name = "voided_at", nullable = true, columnDefinition = "TIMESTAMP")
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

    public TillFunction getTillFunction() {
        return tillFunction;
    }

    public void setTillFunction(TillFunction tillFunction) {
        this.tillFunction = tillFunction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFloatChange() {
        return floatChange;
    }

    public void setFloatChange(BigDecimal floatChange) {
        this.floatChange = floatChange;
    }

    public BigDecimal getCashChange() {
        return cashChange;
    }

    public void setCashChange(BigDecimal cashChange) {
        this.cashChange = cashChange;
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
        return "Transaction{" +
                "id=" + id +
                ", till=" + till +
                ", tillFunction=" + tillFunction +
                ", amount=" + amount +
                ", floatChange=" + floatChange +
                ", cashChange=" + cashChange +
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
