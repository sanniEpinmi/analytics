package com.sanni.test.wrapper;

import com.sanni.test.model.AnalyticOuput;

public class AnalyticResponse {

    private AnalyticOuput response;

    private AnalyticResponse() {
        response = new AnalyticOuput();
    }

    public static AnalyticResponse createStatisticsResponse(){
        return new AnalyticResponse();
    }

    public AnalyticResponse withSum(double sum){
        response.setSum(sum);
        return this;
    }

    public AnalyticResponse withAvg(double avg){
        response.setAvg(avg);
        return this;
    }

    public AnalyticResponse withMax(double max){
        response.setMax(max);
        return this;
    }

    public AnalyticResponse withMin(double min){
        response.setMin(min);
        return this;
    }

    public AnalyticResponse withCount(long count){
        response.setCount(count);
        return this;
    }

    public AnalyticOuput build(){
        return response;
    }
}
