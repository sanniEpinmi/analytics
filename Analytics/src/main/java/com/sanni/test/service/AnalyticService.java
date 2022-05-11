package com.sanni.test.service;

import com.sanni.test.model.AnalyticInput;
import com.sanni.test.model.AnalyticOuput;

public interface AnalyticService {
    boolean createTransactin(AnalyticInput request, long timestamp);
    AnalyticOuput getTransactin(long timestamp);
    void clearCache();
}
