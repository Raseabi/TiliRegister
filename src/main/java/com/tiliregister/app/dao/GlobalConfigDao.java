package com.tiliregister.app.dao;

import com.tiliregister.app.model.GlobalConfig;

import java.util.List;

public interface GlobalConfigDao {
    GlobalConfig save(GlobalConfig globalConfig);
    GlobalConfig findById(Long id);
    GlobalConfig findByConfigKey(String configKey);
    List<GlobalConfig> findAll();
}
