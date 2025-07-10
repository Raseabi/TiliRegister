package com.tiliregister.app.service;

import com.tiliregister.app.model.FloatTopUp;

import java.util.List;

public interface FloatTopUpService {
    FloatTopUp saveFloatTopUp(FloatTopUp floatTopUp, String createdByUsername);
    FloatTopUp getFloatTopUpById(Long id);
    List<FloatTopUp> getFloatTopUpsByTillId(Long tillId);
    List<FloatTopUp> getFloatTopUpsByTillName(String tillName);
    List<FloatTopUp> getAllFloatTopUps();
    List<FloatTopUp> getActiveFloatTopUps();
    List<FloatTopUp> getVoidedFloatTopUps();
    FloatTopUp updateFloatTopUp(Long id, FloatTopUp floatTopUp, String updatedByUsername);
    FloatTopUp voidFloatTopUp(Long id, int voidStatus, String voidedByUsername);
}
