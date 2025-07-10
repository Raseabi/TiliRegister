package com.tiliregister.app.controller;

import com.tiliregister.app.model.GlobalConfig;
import com.tiliregister.app.service.GlobalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/global-config")
public class GlobalConfigController {

    private final GlobalConfigService globalConfigService;

    @Autowired
    public GlobalConfigController(GlobalConfigService globalConfigService) {
        this.globalConfigService = globalConfigService;
    }

    @PreAuthorize("hasAuthority('setting:edit')")
    @PutMapping("/{id}")
    ResponseEntity <GlobalConfig> updateGlobalConfig(@PathVariable Long id, @RequestBody GlobalConfig globalConfig, Authentication authentication){
        String username = authentication.getName();
        GlobalConfig updatedGlobalConfig = globalConfigService.updateGlobalConfig(id, globalConfig, username);
        return ResponseEntity.ok(updatedGlobalConfig);
    }

    @PreAuthorize("hasAuthority('setting:view')")
    @GetMapping
    ResponseEntity <List<GlobalConfig>> getAllGlobalConfigs(){
       return ResponseEntity.ok(globalConfigService.getAllGlobalConfigs());
    }

    @PreAuthorize("hasAuthority('setting:view')")
    @GetMapping("/id/{id}")
    ResponseEntity <GlobalConfig> getGlobalConfigById(@PathVariable Long id){
        return ResponseEntity.ok(globalConfigService.getGlobalConfigById(id));
    }

    @PreAuthorize("hasAuthority('setting:view')")
    @GetMapping("/config-key/{configKey}")
    ResponseEntity <GlobalConfig> getGlobalConfigKey(@PathVariable String configKey){
        return ResponseEntity.ok(globalConfigService.getGlobalConfigByConfigKey(configKey));
    }

}
