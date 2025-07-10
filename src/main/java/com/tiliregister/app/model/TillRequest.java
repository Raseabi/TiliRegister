package com.tiliregister.app.model;

import java.util.Set;

public class TillRequest {
    private Till till;
    private Set<Long> tillFunctionIds;

    public Till getTill() {
        return till;
    }

    public void setTill(Till till) {
        this.till = till;
    }

    public Set<Long> getTillFunctionIds() {
        return tillFunctionIds;
    }

    public void setTillFunctionIds(Set<Long> tillFunctionIds) {
        this.tillFunctionIds = tillFunctionIds;
    }
}
