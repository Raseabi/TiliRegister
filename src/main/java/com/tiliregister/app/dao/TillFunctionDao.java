package com.tiliregister.app.dao;

import com.tiliregister.app.model.TillFunction;

import java.util.List;

public interface TillFunctionDao {
    TillFunction save(TillFunction tillFunction);
    TillFunction findById(Long id);
    TillFunction findByName(String name);
    List<TillFunction> findByVoidStatus(List<Integer> voidStatus);
    boolean isTillFunctionUnique(String functionName, Long excludeFunctionId);
}
