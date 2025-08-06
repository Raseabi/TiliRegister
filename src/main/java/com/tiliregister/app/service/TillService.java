package com.tiliregister.app.service;

import com.tiliregister.app.model.Till;
import com.tiliregister.app.model.TillRequest;

import java.math.BigDecimal;
import java.util.List;

public interface TillService {
    Till saveTill(Till till, String createdByUsername);
    Till saveTillWithFunctions(TillRequest tillRequest, String createdByUsername);
    Till getTillById(Long id);
    Till getTillByName(String name);
    List<Till> getAllTills();
    List<Till> getActiveTills();
    List<Till> getInActiveTills();
    Till updateTill(Long id, Till till, String updatedByUsername);
    Till updateTillWithFunctions(Long id, TillRequest tillRequest, String updatedByUsername);
    Till voidTill(Long id, int voidStatus, String voidByUsername);
    boolean isTillUnique(String name, Long excludeTillId);
    BigDecimal adjustTillFloat(Long tillId, BigDecimal amount, String adjustSign);
    BigDecimal adjustTillCashInHand(Long tillId, BigDecimal amount, String adjustSign);
    boolean isAdjustFloatPossible(Long tillId, BigDecimal amount, String adjustSign);
    boolean isAdjustCashInHandPossible(Long tillId, BigDecimal amount, String adjustSign);
    List<Till> getTillsByGroupId(Long tiliGroupId);
    boolean updateFloat(Long id, BigDecimal amount, String performedBy);
    boolean updateCash(Long id, BigDecimal amount, String performedBy);
}
