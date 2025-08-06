package com.tiliregister.app.dao;

import com.tiliregister.app.model.CashOut;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CashOutDao {
    CashOut save(CashOut cashOut);
    CashOut findById(Long id);
    List<CashOut> findByTillId(Long tillId);
    List<CashOut> findByTillName(String tillName);
    List<CashOut> findByVoidStatus(List<Integer> voidStatus);
    Page<CashOut> searchCashOuts(String searchToken, int page, int size, String sortField, String sortOrder, Long tillId);
}
