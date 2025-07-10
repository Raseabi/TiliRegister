package com.tiliregister.app.model;

import java.util.Set;

public class TillFunctionMapRequest {
    private Long tillId;
    private Set<Long> tillFunctionIds;

    public Long getTillId() {
        return tillId;
    }

    public void setTillId(Long tillId) {
        this.tillId = tillId;
    }

    public Set<Long> getTillFunctionIds() {
        return tillFunctionIds;
    }

    public void setTillFunctionIds(Set<Long> tillFunctionIds) {
        this.tillFunctionIds = tillFunctionIds;
    }
}
