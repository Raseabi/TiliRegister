package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.TillDao;
import com.tiliregister.app.model.Till;
import com.tiliregister.app.model.TillFunction;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.TillFunctionService;
import com.tiliregister.app.service.TillService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TillServiceImpl implements TillService {

    private final TillDao tillDao;
    private final UserService userService;

    @Autowired
    public TillServiceImpl(TillDao tillDao, UserService userService) {
        this.tillDao = tillDao;
        this.userService = userService;
    }

    @Override
    public Till saveTill(Till till, String createdByUsername) {
        User createdBy = userService.getUserByUsername(createdByUsername);
        if (createdBy == null || createdBy.getVoided() == 1) {
            throw new EntityNotFoundException("Admin not found");
        }
        if (isTillUnique(till.getName(), null)) {
            throw new IllegalArgumentException("Till already exist. Duplication denied");
        }
        till.setCreatedBy(createdBy);
        till.setCreatedAt(LocalDateTime.now());

        return tillDao.save(till);
    }

    @Override
    public Till getTillById(Long id) {
        return tillDao.findById(id);
    }

    @Override
    public Till getTillByName(String name) {
        return tillDao.findByName(name);
    }

    @Override
    public List<Till> getAllTills() {
        return tillDao.findByVoidStatus(List.of(0, 1));
    }

    @Override
    public List<Till> getActiveTills() {
        return tillDao.findByVoidStatus(List.of(0));
    }

    @Override
    public List<Till> getInActiveTills() {
        return tillDao.findByVoidStatus(List.of(1));
    }

    @Override
    public Till updateTill(Long id, Till till, String updatedByUsername) {
        User updatedBy = userService.getUserByUsername(updatedByUsername);
        if (updatedBy == null || updatedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Admin not found");
        }
        if (isTillUnique(till.getName(), id)) {
            throw new IllegalArgumentException("Till already exists. Duplication denied");
        }
        Till updatedTill = tillDao.findById(id);
        updatedTill.setName(till.getName());
        updatedTill.setDescription(till.getDescription());
        updatedTill.setCurrentFloat(till.getCurrentFloat());
        updatedTill.setCurrentCashInHand(till.getCurrentCashInHand());

        updatedTill.setUpdatedBy(updatedBy);
        updatedTill.setUpdatedAt(LocalDateTime.now());

        return tillDao.save(updatedTill);
    }

    @Override
    public Till voidTill(Long id, int voidStatus, String voidByUsername) {
        User voidedBy = userService.getUserByUsername(voidByUsername);
        if (voidedBy == null || voidedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Admin not found");
        }
        Till voidedTill = tillDao.findById(id);
        voidedTill.setVoided(voidStatus);
        voidedTill.setVoidedBy(voidedBy);
        voidedTill.setVoidedAt(LocalDateTime.now());

        return tillDao.save(voidedTill);
    }

    @Override
    public boolean isTillUnique(String name, Long excludeTillId) {
        return tillDao.isTillUnique(name, excludeTillId);
    }

    @Override
    @Transactional
    public BigDecimal adjustTillFloat(Long tillId, BigDecimal amount, String adjustSign) {
        Till till = tillDao.findById(tillId);

        BigDecimal currentFloat = till.getCurrentFloat();
        BigDecimal newFloat = adjustSign.equals("+")
                ? currentFloat.add(amount)
                : currentFloat.subtract(amount);

        till.setCurrentFloat(newFloat);
        tillDao.save(till);

        return newFloat;
    }


    @Override
    @Transactional
    public BigDecimal adjustTillCashInHand(Long tillId, BigDecimal amount, String adjustSign) {
        Till till = tillDao.findById(tillId);

        BigDecimal currentCashInHand = till.getCurrentCashInHand();
        BigDecimal newCashInHand = adjustSign.equals("+")
                ? currentCashInHand.add(amount)
                : currentCashInHand.subtract(amount);

        till.setCurrentCashInHand(newCashInHand);
        tillDao.save(till);

        return newCashInHand;
    }

    @Override
    public boolean isAdjustFloatPossible(Long tillId, BigDecimal amount, String adjustSign) {
        Till till = tillDao.findById(tillId);
        if (adjustSign.equalsIgnoreCase("-")) {
            return till.getCurrentFloat().subtract(amount).compareTo(BigDecimal.ZERO) >= 0;
        } else {
            return true;
        }

    }

    @Override
    public boolean isAdjustCashInHandPossible(Long tillId, BigDecimal amount, String adjustSign) {
        Till till = tillDao.findById(tillId);
        if (adjustSign.equalsIgnoreCase("-")) {
            return till.getCurrentCashInHand().subtract(amount).compareTo(BigDecimal.ZERO) >= 0;
        } else {
            return true;
        }
    }
}
