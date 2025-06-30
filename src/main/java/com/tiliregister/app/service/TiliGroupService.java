package com.tiliregister.app.service;

import com.tiliregister.app.model.TiliGroup;

import java.util.List;

public interface TiliGroupService {
    TiliGroup saveTiliGroup(TiliGroup tiliGroup, String createdByUsername);
    TiliGroup getTiliGroupById(Long id);
    TiliGroup getTiliGroupByName(String name);
    List<TiliGroup> getAllTiliGroups();
    List<TiliGroup> getActiveTiliGroups();
    List<TiliGroup> getNonActiveTiliGroups();
    TiliGroup updateTiliGroup(Long id, TiliGroup tiliGroup, String updatedByUsername);
    TiliGroup voidTiliGroup(Long id, int voidValue, String voidedByUsername);
    boolean isTiliGroupRegistered(String name, Long id);
}
