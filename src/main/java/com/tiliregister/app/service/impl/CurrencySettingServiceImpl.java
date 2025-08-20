package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.CurrencySettingDao;
import com.tiliregister.app.model.CurrencySetting;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.CurrencySettingService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CurrencySettingServiceImpl implements CurrencySettingService {

    private final CurrencySettingDao currencySettingDao;
    private final UserService userService;

    @Autowired
    public CurrencySettingServiceImpl(CurrencySettingDao currencySettingDao, UserService userService) {
        this.currencySettingDao = currencySettingDao;
        this.userService = userService;
    }

    @Override
    public CurrencySetting updateCurrencySetting(CurrencySetting currencySetting, String updatedByUsername) {
        User updatedBy = userService.getUserByUsername(updatedByUsername);
        if(updatedBy == null || updatedBy.getVoided() == 1){
            throw new EntityNotFoundException("Admin not found");
        }
        CurrencySetting existingCurrencySetting = getCurrencySettingById();

        existingCurrencySetting.setCurrencyCode(currencySetting.getCurrencyCode());
        existingCurrencySetting.setUpdatedBy(updatedBy);
        existingCurrencySetting.setUpdatedAt(LocalDateTime.now());

        return currencySettingDao.save(existingCurrencySetting);
    }

    @Override
    public CurrencySetting getCurrencySettingById() {
        CurrencySetting setting = currencySettingDao.findById(1L);
        if (setting == null) {
            throw new EntityNotFoundException("Currency setting not found");
        }
        return setting;
    }

}
