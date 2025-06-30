package com.tiliregister.app.dao;

import com.tiliregister.app.model.TillFunctionMap;

import java.util.List;

public interface TillFunctionMapDao {
    TillFunctionMap save(TillFunctionMap tillFunctionMap);
    TillFunctionMap findById(Long id);
    TillFunctionMap findByTillAndFunctionIds(Long tillId, Long functionId);
    List<TillFunctionMap> findByTillId(Long tillId);
    List<TillFunctionMap> findByFunctionId(Long functionId);
    boolean deleteById(Long id);
    boolean deleteByTillAndFunctionIds(Long tillId, Long functionId);
}
