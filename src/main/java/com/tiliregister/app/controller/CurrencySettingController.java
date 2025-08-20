package com.tiliregister.app.controller;

import com.tiliregister.app.model.CurrencySetting;
import com.tiliregister.app.service.CurrencySettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currency-setting")
public class CurrencySettingController {
    private final CurrencySettingService currencySettingService;

    @Autowired
    public CurrencySettingController(CurrencySettingService currencySettingService) {
        this.currencySettingService = currencySettingService;
    }

    @PreAuthorize("hasAuthority('setting:view')")
    @GetMapping
    ResponseEntity<CurrencySetting> getCurrencySettingById(){
        return ResponseEntity.ok(currencySettingService.getCurrencySettingById());
    }

    @PreAuthorize("hasAuthority('setting:edit')")
    @PutMapping
    ResponseEntity<CurrencySetting> updateCurrencySetting(@RequestBody CurrencySetting currencySetting, Authentication authentication){
        String username = authentication.getName();
        CurrencySetting updatedCurrencySetting = currencySettingService.updateCurrencySetting(currencySetting,username);
        return ResponseEntity.ok(updatedCurrencySetting);
    }
}
