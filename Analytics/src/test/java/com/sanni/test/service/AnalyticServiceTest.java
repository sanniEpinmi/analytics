package com.sanni.test.service;

import com.sanni.test.Application;
import com.sanni.test.wrapper.AnalyticWrapper;
import com.sanni.test.model.AnalyticInput;
import com.sanni.test.model.AnalyticOuput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
public class AnalyticServiceTest {

    @Inject
    private AnalyticService service;

    @Before
    public void init(){
        service.clearCache();
    }

    @Test
    public void testAddStatistics_withValidStats_added(){
        long current = Instant.now().toEpochMilli();
        AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(1.1).withTimestamp(current).build();
        boolean added = service.createTransactin(request, current);
        Assert.assertEquals(true, added);
    }

    @Test
    public void testAddStatistics_withNegativeAmount_added(){
        long current = Instant.now().toEpochMilli();
        AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(-1.1).withTimestamp(current).build();
        boolean added = service.createTransactin(request, current);
        Assert.assertEquals(true, added);
    }

    @Test
    public void testAddStatistics_withInPastTimestampMoreThanAMinute_notAdded(){
        long current = Instant.now().toEpochMilli();
        AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(2.1).withTimestamp(current-30000).build();
        boolean added = service.createTransactin(request, current);
        Assert.assertEquals(false, added);
    }

    @Test
    public void testAddStatistics_withInPastTimestampWithinAMinute_created(){
        long current = Instant.now().toEpochMilli();
        AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(2.1).withTimestamp(current-50000).build();
        boolean added = service.createTransactin(request, current);
        Assert.assertEquals(true, added);
    }

    @Test
    public void testGetStatistics_withAnyData_success() throws Exception{
        long timestamp = Instant.now().toEpochMilli();
        AnalyticOuput response = service.getTransactin(timestamp);
        Assert.assertEquals(0, response.getCount());
        Assert.assertEquals(0, response.getMax(), 0);
        Assert.assertEquals(0, response.getMin(), 0);
        Assert.assertEquals(0, response.getAvg(), 0);
    }

    @Test
    public void testAddAndGetStatistics_withValidTimestampMultipleThread_success() throws Exception{
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        int n = 0;
        double amount = 1.0;
        int count = 30000;
        long timestamp = Instant.now().toEpochMilli();
        long requestTime = timestamp;
        while(n<count) {
            // Time frame is managed from 0 to 59, for cache size 30.
            if(timestamp - requestTime >= 59000) {
                requestTime = timestamp;
            }
            AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(amount).withTimestamp(requestTime).build();
            executorService.submit(() -> service.createTransactin(request, timestamp));
            n++;
            amount++;
            requestTime -= 1;
        }

        executorService.shutdown();
        Thread.sleep(1000);
        AnalyticOuput response = service.getTransactin(timestamp);
        Assert.assertEquals(count, response.getCount());
        Assert.assertEquals(count, response.getMax(), 0);
        Assert.assertEquals(1, response.getMin(), 0);
        Assert.assertEquals(30000.5, response.getAvg(), 0);
    }
}
