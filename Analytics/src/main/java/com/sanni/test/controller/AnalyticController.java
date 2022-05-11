package com.sanni.test.controller;

import com.sanni.test.model.AnalyticInput;
import com.sanni.test.service.AnalyticService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.time.Instant;

@Controller
public class AnalyticController {

    @Inject
    private AnalyticService statisticsService;

    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    public ResponseEntity getTransaction(){
        long current = Instant.now().toEpochMilli();
        return new ResponseEntity<>(statisticsService.getTransactin(current), HttpStatus.OK);
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public ResponseEntity createTransaction(@RequestBody AnalyticInput request){
        long current = Instant.now().toEpochMilli();
        boolean added = statisticsService.createTransactin(request, current);
        if(added) {
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }


    @RequestMapping(value = "/delete/transactions", method = RequestMethod.DELETE)
    public ResponseEntity deleteTransaction(){
        statisticsService.clearCache();
            return new ResponseEntity(HttpStatus.NO_CONTENT);

    }
}
