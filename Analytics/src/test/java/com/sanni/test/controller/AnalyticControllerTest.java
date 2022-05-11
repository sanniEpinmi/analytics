package com.sanni.test.controller;

import com.sanni.test.Application;
import com.sanni.test.wrapper.AnalyticWrapper;
import com.sanni.test.model.AnalyticInput;
import com.sanni.test.model.AnalyticOuput;
import com.sanni.test.service.AnalyticService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
public class AnalyticControllerTest {

    @Inject
    private AnalyticController controller;

    @Inject
    private AnalyticService service;

    @Before
    public void init(){
        service.clearCache();
    }

    @Test
    public void testAnalytic_withValidStats_creationData(){
        AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(1.1).withTimestamp(Instant.now().toEpochMilli()).build();
        ResponseEntity responseEntity = controller.createTransaction(request);
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testAnalytic_withNegativeAmount_creationdata(){
        AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(-1.1).withTimestamp(Instant.now().toEpochMilli()).build();
        ResponseEntity responseEntity = controller.createTransaction(request);
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testAnalytic_withInPastTimestampMoreThanAMinute_noContent(){
        AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(2.1).withTimestamp(Instant.now().toEpochMilli()-30000).build();
        ResponseEntity responseEntity = controller.createTransaction(request);
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void testAnalytic_withInPastTimestampWithinAMinute_created(){
        AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(2.1).withTimestamp(Instant.now().toEpochMilli()-50000).build();
        ResponseEntity responseEntity = controller.createTransaction(request);
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testAddAndGetAnalytic_withInValidTimestampWithinAMinuteWithSameTime_success() throws Exception{
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int n = 0;
        double amount = 21.0;
        int count = 80000;
        while(n<count) {
            AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(amount).withTimestamp(Instant.now().toEpochMilli()).build();
            executorService.submit(() -> controller.createTransaction(request));
            n++;
            amount += 1;
        }

        executorService.shutdown();
        Thread.sleep(1000);
        ResponseEntity response = controller.getTransaction();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(count, ((AnalyticOuput)response.getBody()).getCount());
    }

    @Test
    public void testAddAndGetAnalytic_withInValidTimestampWithinAMinuteWithDifferentTime_success() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int n = 0;
        double amount = 21.0;
        int count = 80000;
        long timestamp = Instant.now().toEpochMilli();
        while(n<count) {
            AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(amount).withTimestamp(timestamp).build();
            executorService.submit(() -> controller.createTransaction(request));
            n++;
            amount += 1;
            timestamp -= 1;
        }

        executorService.shutdown();
        Thread.sleep(1000);
        ResponseEntity response = controller.getTransaction();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(count, ((AnalyticOuput)response.getBody()).getCount());
    }

    @Test
    public void testAddAndGetAnalytic_withInValidAndOutdatedTimestamp_success() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int n = 0;
        double amount = 21.0;
        int count = 500;
        long timestamp = Instant.now().toEpochMilli();
        while(n<count) {
            AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(amount).withTimestamp(timestamp).build();
            executorService.submit(() -> controller.createTransaction(request));
            n++;
            amount += 1;
            timestamp -= 1;
        }

        Thread.sleep(1000);
        timestamp -= 30000;
        n = 0;
        while(n<count) {
            AnalyticInput request = AnalyticWrapper.createStatisticsRequest().withAmount(amount).withTimestamp(timestamp).build();
            executorService.submit(() -> controller.createTransaction(request));
            n++;
            amount += 1;
            timestamp -= 30000;
        }

        executorService.shutdown();
        Thread.sleep(1000);
        ResponseEntity response = controller.getTransaction();
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(count, ((AnalyticOuput)response.getBody()).getCount());
    }

}
