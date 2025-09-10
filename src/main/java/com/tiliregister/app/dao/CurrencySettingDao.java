package com.tiliregister.app.dao;

import com.tiliregister.app.model.CurrencySetting;

public interface CurrencySettingDao {
    CurrencySetting save(CurrencySetting currencySetting);
    CurrencySetting findById(Long id);
}
