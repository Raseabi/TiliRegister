package com.tiliregister.app.dao;

import com.tiliregister.app.model.FloatTopUp;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FloatTopUpDao {
    FloatTopUp save(FloatTopUp floatTopUp);
    FloatTopUp findById(Long id);
    List<FloatTopUp> findByTillId(Long tillId);
    List<FloatTopUp> findByTillName(String tillName);
    List<FloatTopUp> findByVoidStatus(List<Integer> voidStatus);
    Page<FloatTopUp> searchFloats(String searchToken, int page, int size, String sortField, String sortOrder, Long tillId);
}
