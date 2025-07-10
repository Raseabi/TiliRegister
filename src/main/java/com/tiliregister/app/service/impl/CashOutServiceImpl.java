package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.CashOutDao;
import com.tiliregister.app.model.CashOut;
import com.tiliregister.app.model.Till;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.CashOutService;
import com.tiliregister.app.service.TillService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CashOutServiceImpl implements CashOutService {
    private final CashOutDao cashOutDao;
    private final UserService userService;
    private final TillService tillService;

    @Autowired
    public CashOutServiceImpl(CashOutDao cashOutDao, UserService userService, TillService tillService) {
        this.cashOutDao = cashOutDao;
        this.userService = userService;
        this.tillService = tillService;
    }

    @Override
    @Transactional
    public CashOut saveCashOut(CashOut cashOut, String createdByUsername) {
        User createdBy = userService.getUserByUsername(createdByUsername);

        if(createdBy == null || createdBy.getVoided() == 1){
            throw new EntityNotFoundException("Admin not found");
        }
        Till till = tillService.getTillById(cashOut.getTill().getId());

        if(!tillService.isAdjustCashInHandPossible(till.getId(), cashOut.getAmount(), "-")) {
            throw new IllegalArgumentException("Insufficient cash in hand");
        }
        till.setCurrentCashInHand(till.getCurrentCashInHand().subtract(cashOut.getAmount()));
        cashOut.setTill(till);

        cashOut.setCreatedBy(createdBy);
        cashOut.setCreatedAt(LocalDateTime.now());

        return cashOutDao.save(cashOut);
    }

    @Override
    public CashOut getCashOutById(Long id) {
        return cashOutDao.findById(id);
    }

    @Override
    public List<CashOut> getCashOutsByTillId(Long tillId) {
        return cashOutDao.findByTillId(tillId);
    }

    @Override
    public List<CashOut> getCashOutsByTillName(String tillName) {
        return cashOutDao.findByTillName(tillName);
    }

    @Override
    public List<CashOut> getAllCashOuts() {
        return cashOutDao.findByVoidStatus(List.of(0, 1));
    }

    @Override
    public List<CashOut> getActiveCashOuts() {
        return cashOutDao.findByVoidStatus(List.of(0));
    }

    @Override
    public List<CashOut> getVoidedCashOuts() {
        return cashOutDao.findByVoidStatus(List.of(1));
    }

    @Override
    public CashOut updateCashOut(Long id, CashOut cashOut, String updatedByUsername) {
        User updatedBy = userService.getUserByUsername(updatedByUsername);
        CashOut updatedCashOut = cashOutDao.findById(id);

        if(updatedCashOut == null || updatedCashOut.getVoided() == 1 || updatedBy == null || updatedBy.getVoided() == 1){
            throw new EntityNotFoundException("Cash Out or Admin not found");
        }

        updatedCashOut.setAmount(cashOut.getAmount());
        updatedCashOut.setUpdatedBy(updatedBy);
        updatedCashOut.setUpdatedAt(LocalDateTime.now());

        return cashOutDao.save(updatedCashOut);
    }

    @Override
    public CashOut voidCashOut(Long id, int voidStatus, String voidedByUsername) {
        User voidedBy = userService.getUserByUsername(voidedByUsername);
        CashOut voidedCashOut = cashOutDao.findById(id);

        if(voidedCashOut == null || voidedBy == null || voidedBy.getVoided() == 1){
            throw new EntityNotFoundException("Cash Out or Admin not found");
        }

        voidedCashOut.setVoided(voidStatus);
        voidedCashOut.setVoidedBy(voidedBy);
        voidedCashOut.setVoidedAt(LocalDateTime.now());

        return cashOutDao.save(voidedCashOut);
    }
}
