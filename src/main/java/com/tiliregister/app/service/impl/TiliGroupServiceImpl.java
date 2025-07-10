package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.TiliGroupDao;
import com.tiliregister.app.dao.UserDao;
import com.tiliregister.app.model.TiliGroup;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.TiliGroupService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TiliGroupServiceImpl implements TiliGroupService {

    private final TiliGroupDao tiliGroupDao;
    private final UserDao userDao;

    @Autowired
    public TiliGroupServiceImpl(TiliGroupDao tiliGroupDao, UserDao userDao) {
        this.tiliGroupDao = tiliGroupDao;
        this.userDao = userDao;
    }

    @Override
    public TiliGroup saveTiliGroup(TiliGroup tiliGroup, String createdByUsername) {
        User createdBy = userDao.findByUsername(createdByUsername);
        if (createdBy == null || createdBy.getVoided() == 1) {
            throw new EntityNotFoundException("Creator (admin) not found");
        }
        if (isTiliGroupRegistered(tiliGroup.getName(), null)) {
            throw new IllegalArgumentException("Tili Group already exist");
        }
        tiliGroup.setCreatedBy(createdBy);
        tiliGroup.setCreatedAt(LocalDateTime.now());

        return tiliGroupDao.save(tiliGroup);
    }

    @Override
    public TiliGroup getTiliGroupById(Long id) {
        return tiliGroupDao.findById(id);
    }

    @Override
    public TiliGroup getTiliGroupByName(String name) {
        return tiliGroupDao.findByName(name);
    }

    @Override
    public List<TiliGroup> getAllTiliGroups() {
        return tiliGroupDao.findTiliGroupByVoidStatus(List.of(0, 1));
    }

    @Override
    public List<TiliGroup> getActiveTiliGroups() {
        return tiliGroupDao.findTiliGroupByVoidStatus(List.of(0));
    }

    @Override
    public List<TiliGroup> getNonActiveTiliGroups() {
        return tiliGroupDao.findTiliGroupByVoidStatus(List.of(1));
    }

    @Override
    public TiliGroup updateTiliGroup(Long id, TiliGroup tiliGroup, String updatedByUsername) {
        TiliGroup existingTiliGroup = tiliGroupDao.findById(id);
        User updatedBy = userDao.findByUsername(updatedByUsername);

        if (existingTiliGroup == null || existingTiliGroup.getVoided() == 1 || updatedBy == null || updatedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Tili Group or Admin not found");
        }
        //Check for duplicates
        if(isTiliGroupRegistered(tiliGroup.getName(), id)){//Tili name already exist
            throw new IllegalArgumentException("Tili Group with this name already exist. Duplication denied");
        }

        existingTiliGroup.setName(tiliGroup.getName());
        existingTiliGroup.setDescription(tiliGroup.getDescription());
        existingTiliGroup.setUpdatedBy(updatedBy);
        existingTiliGroup.setUpdatedAt(LocalDateTime.now());

        return tiliGroupDao.save(existingTiliGroup);
    }

    @Override
    public TiliGroup voidTiliGroup(Long id, int voidValue, String voidedByUsername) {
        TiliGroup tiliGroup = tiliGroupDao.findById(id);
        User voidedBy = userDao.findByUsername(voidedByUsername);

        if (tiliGroup == null || voidedBy == null || voidedBy.getVoided() == 1) {
            throw new EntityNotFoundException("Tili Group or admin not found");
        }

        tiliGroup.setVoided(voidValue);
        tiliGroup.setVoidedBy(voidedBy);
        tiliGroup.setVoidedAt(LocalDateTime.now());

        return tiliGroupDao.save(tiliGroup);
    }

    @Override
    public boolean isTiliGroupRegistered(String tiliGroupName, Long excludeTiliGroupId) {
        return tiliGroupDao.tiliGroupRegistered(tiliGroupName, excludeTiliGroupId);
    }
}
