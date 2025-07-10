package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.TillFunctionMapDao;
import com.tiliregister.app.model.Till;
import com.tiliregister.app.model.TillFunction;
import com.tiliregister.app.model.TillFunctionMap;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.TillFunctionMapService;
import com.tiliregister.app.service.TillFunctionService;
import com.tiliregister.app.service.TillService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class TillFunctionMapServiceImpl implements TillFunctionMapService {

    private final TillFunctionMapDao tillFunctionMapDao;
    private final UserService userService;
    private final TillService tillService;
    private final TillFunctionService tillFunctionService;

    @Autowired
    public TillFunctionMapServiceImpl(TillFunctionMapDao tillFunctionMapDao, UserService userService, TillService tillService, TillFunctionService tillFunctionService) {
        this.tillFunctionMapDao = tillFunctionMapDao;
        this.userService = userService;
        this.tillService = tillService;
        this.tillFunctionService = tillFunctionService;
    }

    @Override
    public TillFunctionMap saveTillFunctionMap(TillFunctionMap tillFunctionMap) {
        return tillFunctionMapDao.save(tillFunctionMap);
    }

    @Override
    public TillFunctionMap getTillFunctionById(Long id) {
        return tillFunctionMapDao.findById(id);
    }

    @Override
    public TillFunctionMap getTillFunctionByTillAndFunctionIds(Long tillId, Long functionId) {
        return tillFunctionMapDao.findByTillAndFunctionIds(tillId, functionId);
    }

    @Override
    public List<TillFunctionMap> getTillFunctionByTillId(Long tillId) {
        return tillFunctionMapDao.findByTillId(tillId);
    }

    @Override
    public List<TillFunctionMap> getTillFunctionByFunctionId(Long functionId) {
        return tillFunctionMapDao.findByFunctionId(functionId);
    }

    @Override
    @Transactional
    public boolean assignFunctionsToTill(Long tillId, Set<Long> functionIds, String assignedByUsername) {
        Till till = tillService.getTillById(tillId);
        User assignedBy = userService.getUserByUsername(assignedByUsername);

        if(till == null || assignedBy == null || assignedBy.getVoided() == 1){
            throw new EntityNotFoundException("Till or Admin not found");
        }
        for(Long functionId : functionIds){
            TillFunction function = tillFunctionService.getTillFunctionById(functionId);

            if(function == null) continue;

            if(getTillFunctionByTillAndFunctionIds(tillId, functionId) != null) continue;

            TillFunctionMap tillFunctionMap = new TillFunctionMap();
            tillFunctionMap.setTill(till);
            tillFunctionMap.setTillFunction(function);
            tillFunctionMap.setCreatedBy(assignedBy);
            tillFunctionMap.setCreatedAt(LocalDateTime.now());

            tillFunctionMapDao.save(tillFunctionMap);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeFunctionsFromTill(Long tillId, Set<Long> functionIds) {
       TillFunction tillFunction = tillFunctionService.getTillFunctionById(tillId);

       if(tillFunction == null){
           throw  new EntityNotFoundException("Till Function not found");
       }
       for(Long functionId : functionIds){
           TillFunctionMap tillFunctionMap = tillFunctionMapDao.findByTillAndFunctionIds(tillId, functionId);

           if(tillFunctionMap == null){
               throw new EntityNotFoundException("Till function map not found");
           }
           tillFunctionMapDao.deleteById(tillFunctionMap.getId());
       }
       return true;
    }

    @Override
    public boolean deleteTillFunctionById(Long id) {
        return tillFunctionMapDao.deleteById(id);
    }
}
