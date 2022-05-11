package com.sanni.test.cache;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AnalyticCache<Long, Statistics> extends LinkedHashMap<Long, Statistics> {

    /**
     * Cache capacity. In multiplication of 6, min value 30 and max value to be 30000 (ms in a minute).
     * Higher cache capacity higher concurrency level. Each cache object have separate lock which allow concurrently modification for each cache object.
     */
    private static final int MIN_CACHE_CAPACITY = 30;
    private static final int MAX_CACHE_CAPACITY = 30000;
    private int capacity = MIN_CACHE_CAPACITY;

    public AnalyticCache(){
        super();
    }

    public AnalyticCache(int capacity){
        super();
        if(capacity < 30){
            capacity = MIN_CACHE_CAPACITY;
        } else if(capacity > MAX_CACHE_CAPACITY){
            capacity = MAX_CACHE_CAPACITY;
        } else if(capacity % 6 != 0){
            int rem = capacity % 6;
            capacity += (6 - rem);
        }
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Long, Statistics> eldest) {
        return this.size() > capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
