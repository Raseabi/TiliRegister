package com.tiliregister.app.dao;

import com.tiliregister.app.model.CashOut;

import java.util.List;

public interface CashOutDao {
    CashOut save(CashOut cashOut);
    CashOut findById(Long id);
    List<CashOut> findByTillId(Long tillId);
    List<CashOut> findByTillName(String tillName);
    List<CashOut> findByVoidStatus(List<Integer> voidStatus);
}
