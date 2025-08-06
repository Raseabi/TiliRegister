package com.tiliregister.app.dao;

import com.tiliregister.app.model.Till;

import java.math.BigDecimal;
import java.util.List;

public interface TillDao {
    Till save(Till till);
    Till findById(Long id);
    Till findByName(String name);
    List<Till> findByVoidStatus(List<Integer> voidStatus);
    List<Till> findByTiliGroupId(Long tiliGroupId);
    boolean isTillUnique(String name, Long excludeTillId);
  }
