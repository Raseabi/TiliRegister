package com.tiliregister.app.service;

import com.tiliregister.app.model.CurrencySetting;

public interface CurrencySettingService {
    CurrencySetting updateCurrencySetting(CurrencySetting currencySetting, String updatedByUsername);
    CurrencySetting getCurrencySettingById();
}
