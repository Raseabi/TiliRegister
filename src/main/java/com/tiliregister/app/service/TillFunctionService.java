package com.tiliregister.app.service;

import com.tiliregister.app.model.TillFunction;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TillFunctionService {
    TillFunction saveTillFunction(TillFunction tillFunction, String createdByUsername);
    TillFunction getTillFunctionById(Long id);
    TillFunction getTillFunctionByName(String name);
    List<TillFunction> getAllTillFunctions();
    List<TillFunction> getActiveTillFunctions();
    List<TillFunction> getNonActiveTillFunctions();
    TillFunction updateTillFunction(Long id, TillFunction tillFunction, String updatedByUsername);
    TillFunction voidTillFunction(Long id, int voidStatus, String voidByUsername);
    boolean isTillFunctionUnique(String functionName, Long excludeFunctionId);
    Page<TillFunction> searchTillFunctions(String searchToken, int page, int size, String sortField, String sortOrder);
}
