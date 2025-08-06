package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.TillDao;
import com.tiliregister.app.model.*;
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
import java.util.Set;

@Service
public class TillServiceImpl implements TillService {

    private final TillDao tillDao;
    private final UserService userService;
    private final TillFunctionService tillFunctionService;

    @Autowired
    public TillServiceImpl(TillDao tillDao, UserService userService, TillFunctionService tillFunctionService) {
        this.tillDao = tillDao;
        this.userService = userService;
        this.tillFunctionService = tillFunctionService;
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
    @Transactional
    public Till saveTillWithFunctions(TillRequest tillRequest, String createdByUsername) {
        User createdBy = userService.getUserByUsername(createdByUsername);
        if (createdBy == null || createdBy.getVoided() == 1) {
            throw new EntityNotFoundException("Admin not found");
        }
        if (isTillUnique(tillRequest.getTill().getName(), null)) {
            throw new IllegalArgumentException("Till already exist. Duplication denied");
        }
        if (tillRequest.getTillFunctionIds() == null || tillRequest.getTillFunctionIds().isEmpty()) {
            throw new IllegalArgumentException("At least one till function must be provided.");
        }
        LocalDateTime createdAt = LocalDateTime.now();

        Till newTill = tillRequest.getTill();
        newTill.setCreatedBy(createdBy);
        newTill.setCreatedAt(createdAt);

        Set<Long> functionIds = tillRequest.getTillFunctionIds();


        for (Long functionId : functionIds) {
            TillFunction tillFunction = tillFunctionService.getTillFunctionById(functionId);
            TillFunctionMap tillFunctionMap = new TillFunctionMap();
            tillFunctionMap.setTillFunction(tillFunction);
            tillFunctionMap.setTill(newTill);
            tillFunctionMap.setCreatedBy(createdBy);
            tillFunctionMap.setCreatedAt(createdAt);

            newTill.getTillFunctionMaps().add(tillFunctionMap);
        }
        return tillDao.save(newTill);
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
        Till updatedTill = tillDao.findById(id);
        if (updatedTill == null || updatedTill.getVoided() == 1 || updatedBy == null || updatedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Till or Admin not found");
        }
        if (isTillUnique(till.getName(), id)) {
            throw new IllegalArgumentException("Till already exists. Duplication denied");
        }

        updatedTill.setName(till.getName());
        updatedTill.setDescription(till.getDescription());

        updatedTill.setUpdatedBy(updatedBy);
        updatedTill.setUpdatedAt(LocalDateTime.now());

        return tillDao.save(updatedTill);
    }

    @Override
    @Transactional
    public Till updateTillWithFunctions(Long id, TillRequest tillRequest, String updatedByUsername) {
        User updatedBy = userService.getUserByUsername(updatedByUsername);
        if (updatedBy == null || updatedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Admin not found");
        }

        Till existingTill = tillDao.findById(id);
        if (existingTill == null || existingTill.getVoided() == 1) {
            throw new EntityNotFoundException("Till not found or has been voided");
        }

        if (isTillUnique(tillRequest.getTill().getName(), id)) {
            throw new IllegalArgumentException("Till with the same name already exists.");
        }

        // Update main fields
        existingTill.setName(tillRequest.getTill().getName());
        existingTill.setDescription(tillRequest.getTill().getDescription());
        existingTill.setUpdatedBy(updatedBy);
        existingTill.setUpdatedAt(LocalDateTime.now());

        // Clear existing function mappings
        existingTill.getTillFunctionMaps().clear();

        // Add updated mappings
        Set<Long> functionIds = tillRequest.getTillFunctionIds();
        for (Long functionId : functionIds) {
            TillFunction tillFunction = tillFunctionService.getTillFunctionById(functionId);

            TillFunctionMap tillFunctionMap = new TillFunctionMap();
            tillFunctionMap.setTill(existingTill);
            tillFunctionMap.setTillFunction(tillFunction);
            tillFunctionMap.setCreatedBy(updatedBy);
            tillFunctionMap.setCreatedAt(LocalDateTime.now());

            existingTill.getTillFunctionMaps().add(tillFunctionMap);
        }

        return tillDao.save(existingTill);
    }


    @Override
    public Till voidTill(Long id, int voidStatus, String voidByUsername) {
        User voidedBy = userService.getUserByUsername(voidByUsername);
        Till voidedTill = tillDao.findById(id);

        if (voidedTill == null || voidedBy == null || voidedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Till or Admin not found");
        }

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

    @Override
    public List<Till> getTillsByGroupId(Long tiliGroupId) {
        return tillDao.findByTiliGroupId(tiliGroupId);
    }

    @Override
    @Transactional
    public boolean updateFloat(Long id, BigDecimal amount, String performedBy) {
        try {
            Till till = tillDao.findById(id);
            if (till == null) {
                throw new EntityNotFoundException("Till with ID: " + id + " not found");
            }
            till.setCurrentFloat(amount);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update float for till ID " + id, e);
        }
    }

    @Override
    @Transactional
    public boolean updateCash(Long id, BigDecimal amount, String performedBy) {
        try {
            Till till = tillDao.findById(id);
            if (till == null) {
                throw new EntityNotFoundException("Till with ID: " + id + " not found");
            }
            till.setCurrentCashInHand(amount);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update cash in hand for till ID " + id, e);
        }
    }


}
