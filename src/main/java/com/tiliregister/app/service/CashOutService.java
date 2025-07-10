package com.tiliregister.app.service;

import com.tiliregister.app.model.CashOut;

import java.util.List;

public interface CashOutService {
    CashOut saveCashOut(CashOut cashOut, String createdByUsername);
    CashOut getCashOutById(Long id);
    List<CashOut> getCashOutsByTillId(Long tillId);
    List<CashOut> getCashOutsByTillName(String tillName);
    List<CashOut> getAllCashOuts();
    List<CashOut> getActiveCashOuts();
    List<CashOut> getVoidedCashOuts();
    CashOut updateCashOut(Long id, CashOut cashOut, String updatedByUsername);
    CashOut voidCashOut(Long id, int voidStatus, String voidedByUsername);
}
