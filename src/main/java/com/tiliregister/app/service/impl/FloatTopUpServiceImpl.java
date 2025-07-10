package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.FloatTopUpDao;
import com.tiliregister.app.model.FloatTopUp;
import com.tiliregister.app.model.Till;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.FloatTopUpService;
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
public class FloatTopUpServiceImpl implements FloatTopUpService {

    private final FloatTopUpDao floatTopUpDao;
    private final UserService userService;
    private final TillService tillService;

    @Autowired
    public FloatTopUpServiceImpl(FloatTopUpDao floatTopUpDao, UserService userService, TillService tillService) {
        this.floatTopUpDao = floatTopUpDao;
        this.userService = userService;
        this.tillService = tillService;
    }

    @Override
    @Transactional
    public FloatTopUp saveFloatTopUp(FloatTopUp floatTopUp, String createdByUsername) {
        User createdBy = userService.getUserByUsername(createdByUsername);

        if(createdBy == null || createdBy.getVoided() == 1){
            throw new EntityNotFoundException("Admin not found");
        }
         Till till = tillService.getTillById(floatTopUp.getTill().getId());

        till.setCurrentFloat(till.getCurrentFloat().add(floatTopUp.getAmount()));

        floatTopUp.setTill(till);

        floatTopUp.setCreatedBy(createdBy);
        floatTopUp.setCreatedAt(LocalDateTime.now());

        return floatTopUpDao.save(floatTopUp);
    }

    @Override
    public FloatTopUp getFloatTopUpById(Long id) {
        return floatTopUpDao.findById(id);
    }

    @Override
    public List<FloatTopUp> getFloatTopUpsByTillId(Long tillId) {
        return floatTopUpDao.findByTillId(tillId);
    }

    @Override
    public List<FloatTopUp> getFloatTopUpsByTillName(String tillName) {
        return floatTopUpDao.findByTillName(tillName);
    }

    @Override
    public List<FloatTopUp> getAllFloatTopUps() {
        return floatTopUpDao.findByVoidStatus(List.of(0, 1));
    }

    @Override
    public List<FloatTopUp> getActiveFloatTopUps() {
        return floatTopUpDao.findByVoidStatus(List.of(0));
    }

    @Override
    public List<FloatTopUp> getVoidedFloatTopUps() {
        return floatTopUpDao.findByVoidStatus(List.of(1));
    }

    @Override
    public FloatTopUp updateFloatTopUp(Long id, FloatTopUp floatTopUp, String updatedByUsername) {
        User updatedBy = userService.getUserByUsername(updatedByUsername);
        FloatTopUp updatedFloatTopUp = floatTopUpDao.findById(id);

        if(updatedFloatTopUp == null || updatedFloatTopUp.getVoided() == 1 || updatedBy == null || updatedBy.getVoided() == 1){
            throw new EntityNotFoundException("Float Top Up or Admin not found");
        }

        updatedFloatTopUp.setAmount(floatTopUp.getAmount());
        updatedFloatTopUp.setUpdatedBy(updatedBy);
        updatedFloatTopUp.setUpdatedAt(LocalDateTime.now());

        return floatTopUpDao.save(updatedFloatTopUp);
    }

    @Override
    public FloatTopUp voidFloatTopUp(Long id, int voidStatus, String voidedByUsername) {
        User voidedBy = userService.getUserByUsername(voidedByUsername);
        FloatTopUp voidedFloatTopUp = floatTopUpDao.findById(id);

        if(voidedFloatTopUp == null || voidedBy == null || voidedBy.getVoided() == 1){
            throw new EntityNotFoundException("Float Top Up or Admin not found");
        }

        voidedFloatTopUp.setVoided(voidStatus);
        voidedFloatTopUp.setVoidedBy(voidedBy);
        voidedFloatTopUp.setVoidedAt(LocalDateTime.now());

        return floatTopUpDao.save(voidedFloatTopUp);
    }
}
