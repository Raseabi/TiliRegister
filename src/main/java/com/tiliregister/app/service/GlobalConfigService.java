package com.tiliregister.app.service;

import com.tiliregister.app.model.GlobalConfig;

import java.util.List;

public interface GlobalConfigService {
    GlobalConfig updateGlobalConfig(Long id, GlobalConfig globalConfig, String updatedByUsername);
    GlobalConfig getGlobalConfigById(Long id);
    GlobalConfig getGlobalConfigByConfigKey(String configKey);
    List<GlobalConfig> getAllGlobalConfigs();
}
