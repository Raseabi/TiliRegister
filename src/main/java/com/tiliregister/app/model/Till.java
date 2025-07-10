package com.tiliregister.app.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tills")
public class Till {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tili_group_id", nullable = false)
    @JsonSerialize(using = TiliGroupSerializer.class)
    private TiliGroup tiliGroup;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(name = "current_float", nullable = false)
    private BigDecimal currentFloat;

    @Column(name = "current_cash_in_hand", nullable = false)
    private BigDecimal currentCashInHand;

    @OneToMany(mappedBy = "till", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CashOut> cashOuts = new ArrayList<>();

    @OneToMany(mappedBy = "till", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FloatTopUp> floatTopUps = new ArrayList<>();

    @OneToMany(mappedBy = "till", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TillFunctionMap> tillFunctionMaps = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @JsonSerialize(using = UserSerializer.class)
    private User createdBy;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "updated_by", nullable = true)
    @JsonSerialize(using = UserSerializer.class)
    private User updatedBy;

    @Column(name = "updated_at", nullable = true, columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int voided;

    @ManyToOne
    @JoinColumn(name = "voided_by", nullable = true)
    @JsonSerialize(using = UserSerializer.class)
    private User voidedBy;

    @Column(name = "voided_at", nullable = true, columnDefinition = "TIMESTAMP")
    private LocalDateTime voidedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TiliGroup getTiliGroup() {
        return tiliGroup;
    }

    public void setTiliGroup(TiliGroup tiliGroup) {
        this.tiliGroup = tiliGroup;
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

    public BigDecimal getCurrentFloat() {
        return currentFloat;
    }

    public void setCurrentFloat(BigDecimal currentFloat) {
        this.currentFloat = currentFloat;
    }

    public BigDecimal getCurrentCashInHand() {
        return currentCashInHand;
    }

    public void setCurrentCashInHand(BigDecimal currentCashInHand) {
        this.currentCashInHand = currentCashInHand;
    }

    public List<CashOut> getCashOuts() {
        return cashOuts;
    }

    public void setCashOuts(List<CashOut> cashOuts) {
        this.cashOuts = cashOuts;
    }

    public List<FloatTopUp> getFloatTopUps() {
        return floatTopUps;
    }

    public void setFloatTopUps(List<FloatTopUp> floatTopUps) {
        this.floatTopUps = floatTopUps;
    }

    public List<TillFunctionMap> getTillFunctionMaps() {
        return tillFunctionMaps;
    }

    public void setTillFunctionMaps(List<TillFunctionMap> tillFunctionMaps) {
        this.tillFunctionMaps = tillFunctionMaps;
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
        return "Till{" +
                "id=" + id +
                ", tiliGroup=" + tiliGroup +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", currentFloat=" + currentFloat +
                ", currentCashInHand=" + currentCashInHand +
                ", cashOuts=" + cashOuts +
                ", floatTopUps=" + floatTopUps +
                ", tillFunctionMaps=" + tillFunctionMaps +
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
