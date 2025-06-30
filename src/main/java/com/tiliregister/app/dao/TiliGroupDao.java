package com.tiliregister.app.dao;

import com.tiliregister.app.model.TiliGroup;

import java.util.List;

public interface TiliGroupDao {
    TiliGroup save(TiliGroup tiliGroup);
    TiliGroup findById(Long id);
    TiliGroup findByName(String name);
    List<TiliGroup> findTiliGroupByVoidStatus(List<Integer> voidStatus);
    boolean tiliGroupRegistered(String tiliGroupName, Long excludeTiliGroupId);
}
