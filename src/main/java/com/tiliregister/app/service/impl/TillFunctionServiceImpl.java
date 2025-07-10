package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.TillFunctionDao;
import com.tiliregister.app.model.TillFunction;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.TillFunctionService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TillFunctionServiceImpl implements TillFunctionService {

    private final TillFunctionDao tillFunctionDao;
    private final UserService userService;

    @Autowired
    public TillFunctionServiceImpl(TillFunctionDao tillFunctionDao, UserService userService) {
        this.tillFunctionDao = tillFunctionDao;
        this.userService = userService;
    }

    @Override
    public TillFunction saveTillFunction(TillFunction tillFunction, String createdByUsername) {
        User createdBy = userService.getUserByUsername(createdByUsername);

        if(createdBy == null || createdBy.getVoided() == 1){
            throw new EntityNotFoundException("Admin not found");
        }
        if(isTillFunctionUnique(tillFunction.getName(), null)){
            throw new IllegalArgumentException("Function already exists");
        }

        tillFunction.setCreatedBy(createdBy);
        tillFunction.setCreatedAt(LocalDateTime.now());

        return tillFunctionDao.save(tillFunction);
    }

    @Override
    public TillFunction getTillFunctionById(Long id) {
        return tillFunctionDao.findById(id);
    }

    @Override
    public TillFunction getTillFunctionByName(String name) {
        return tillFunctionDao.findByName(name);
    }

    @Override
    public List<TillFunction> getAllTillFunctions() {
        return tillFunctionDao.findByVoidStatus(List.of(0, 1));
    }

    @Override
    public List<TillFunction> getActiveTillFunctions() {
        return tillFunctionDao.findByVoidStatus(List.of(0));
    }

    @Override
    public List<TillFunction> getNonActiveTillFunctions() {
        return tillFunctionDao.findByVoidStatus(List.of(1));
    }

    @Override
    public TillFunction updateTillFunction(Long id, TillFunction tillFunction, String updatedByUsername) {
        TillFunction updatedTillFunction = tillFunctionDao.findById(id);
        User updatedBy = userService.getUserByUsername(updatedByUsername);

        if(tillFunction == null || tillFunction.getVoided() == 1 || updatedBy == null || updatedBy.getVoided() == 1){
            throw  new EntityNotFoundException("Function or Admin not found");
        }
        if(isTillFunctionUnique(tillFunction.getName(), id)){
            throw new IllegalArgumentException("Function already exists");
        }

        System.out.println("Cash Change Direction **** = " + tillFunction.getCashChangeDirection());

        if (tillFunction.getCashChangeDirection() == null) {
            throw new IllegalArgumentException("Cash Change Direction must not be null");
        }

        updatedTillFunction.setName(tillFunction.getName());
        updatedTillFunction.setFloatChangeDirection(tillFunction.getFloatChangeDirection());
        updatedTillFunction.setCashChangeDirection(tillFunction.getCashChangeDirection());
        updatedTillFunction.setDeletable(tillFunction.getDeletable());
        updatedTillFunction.setDescription(tillFunction.getDescription());

        updatedTillFunction.setUpdatedBy(updatedBy);
        updatedTillFunction.setUpdatedAt(LocalDateTime.now());

        return tillFunctionDao.save(updatedTillFunction);
    }

    @Override
    public TillFunction voidTillFunction(Long id, int voidStatus, String voidByUsername) {
        TillFunction voidedTillFunction = tillFunctionDao.findById(id);
        User voidedBy = userService.getUserByUsername(voidByUsername);

        if(voidedTillFunction == null || voidedBy == null || voidedBy.getVoided() == 1){
            throw  new EntityNotFoundException("Function or Admin not found");
        }

        voidedTillFunction.setVoided(voidStatus);
        voidedTillFunction.setVoidedBy(voidedBy);
        voidedTillFunction.setVoidedAt(LocalDateTime.now());

        return tillFunctionDao.save(voidedTillFunction);
    }

    @Override
    public boolean isTillFunctionUnique(String functionName, Long excludeFunctionId) {
        return tillFunctionDao.isTillFunctionUnique(functionName, excludeFunctionId);
    }
}
