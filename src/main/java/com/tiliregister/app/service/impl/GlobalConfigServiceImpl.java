package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.GlobalConfigDao;
import com.tiliregister.app.model.GlobalConfig;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.GlobalConfigService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GlobalConfigServiceImpl implements GlobalConfigService {

    private final GlobalConfigDao globalConfigDao;
    private final UserService userService;

    @Autowired
    public GlobalConfigServiceImpl(GlobalConfigDao globalConfigDao, UserService userService) {
        this.globalConfigDao = globalConfigDao;
        this.userService = userService;
    }

    @Override
    public GlobalConfig updateGlobalConfig(Long id, GlobalConfig globalConfig, String updatedByUsername) {
        User updatedBy = userService.getUserByUsername(updatedByUsername);

        if(updatedBy == null || updatedBy.getVoided() == 1){
            throw new EntityNotFoundException("Admin not found");
        }
        GlobalConfig existingGlobalConfig = globalConfigDao.findById(id);

        if(existingGlobalConfig == null){
            throw new EntityNotFoundException("Global Configuration not found");
        }

        existingGlobalConfig.setValue(globalConfig.getValue());
        existingGlobalConfig.setChangedBy(updatedBy);
        existingGlobalConfig.setChangedAt(LocalDateTime.now());
        return globalConfigDao.save(existingGlobalConfig);
    }

    @Override
    public GlobalConfig getGlobalConfigById(Long id) {
        return globalConfigDao.findById(id);
    }

    @Override
    public GlobalConfig getGlobalConfigByConfigKey(String configKey) {
        return globalConfigDao.findByConfigKey(configKey);
    }

    @Override
    public List<GlobalConfig> getAllGlobalConfigs() {
        return globalConfigDao.findAll();
    }
}
