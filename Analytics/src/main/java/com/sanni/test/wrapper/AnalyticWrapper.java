package com.sanni.test.wrapper;

import com.sanni.test.model.AnalyticInput;

public class AnalyticWrapper {

    private AnalyticInput request;

    private AnalyticWrapper() {
        request = new AnalyticInput();
    }

    public static AnalyticWrapper createStatisticsRequest(){
        return new AnalyticWrapper();
    }

    public AnalyticWrapper withAmount(double amount){
        request.setAmount(amount);
        return this;
    }

    public AnalyticWrapper withTimestamp(long timestamp){
        request.setTimestamp(timestamp);
        return this;
    }

    public AnalyticInput build(){
        return request;
    }
}
