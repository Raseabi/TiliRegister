package com.tiliregister.app.service;

import com.tiliregister.app.model.TillFunctionMap;

import java.util.List;
import java.util.Set;

public interface TillFunctionMapService {
    TillFunctionMap saveTillFunctionMap(TillFunctionMap tillFunctionMap);
    TillFunctionMap getTillFunctionById(Long id);
    TillFunctionMap getTillFunctionByTillAndFunctionIds(Long tillId, Long functionId);
    List<TillFunctionMap> getTillFunctionByTillId(Long tillId);
    List<TillFunctionMap> getTillFunctionByFunctionId(Long functionId);
    boolean assignFunctionsToTill(Long tillId, Set<Long> functionIds, String assignedByUsername);
    boolean removeFunctionsFromTill(Long tillId, Set<Long> functionIds);
}
