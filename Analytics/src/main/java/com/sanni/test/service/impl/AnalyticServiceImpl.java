package com.sanni.test.service.impl;

import com.sanni.test.wrapper.AnalyticResponse;
import com.sanni.test.cache.AnalyticCache;
import com.sanni.test.model.Analytic;
import com.sanni.test.model.AnalyticInput;
import com.sanni.test.model.AnalyticOuput;
import com.sanni.test.service.AnalyticService;
import com.sanni.test.util.Constants;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticServiceImpl implements AnalyticService {

    @Inject
    private final AnalyticCache<Long, Analytic> cache;

    @Inject
    public AnalyticServiceImpl(AnalyticCache<Long, Analytic> cache) {
        this.cache = cache;
    }

    public boolean createTransactin(AnalyticInput request, long timestamp) {
        long requestTime = request.getTimestamp();
        long delay = timestamp - requestTime;
        if (delay < 0 || delay >= Constants.ONE_MINUTE_IN_MS) {
            return false;
        } else {
            Long key = getKeyFromTimestamp(requestTime);
            Analytic s = cache.get(key);
            if(s == null) {
                synchronized (cache) {
                    s = cache.get(key);
                    if (s == null) {
                        s = new Analytic();
                        cache.put(key, s);
                    }
                }
            }
            s.updateStatistics(request.getAmount());
        }
        return true;
    }

    public AnalyticOuput getTransactin(long timestamp) {
        Map<Long, Analytic> copy = cache.entrySet().parallelStream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getStatistics()));
        return getStatisticsFromCacheCopy(copy, timestamp);
    }

    private AnalyticOuput getStatisticsFromCacheCopy(Map<Long, Analytic> copy, long timestamp) {
        double sum = 0;
        double avg = 0;
        double max = 0;
        double min = Double.MAX_VALUE;
        long count = 0;
        Long key = getKeyFromTimestamp(timestamp);

        for (Map.Entry<Long, Analytic> e : copy.entrySet()) {
            Long eKey = e.getKey();
            Long timeFrame = key - eKey;
            if(timeFrame >= 0 && timeFrame < cache.getCapacity()) {
                Analytic eValue = e.getValue();
                if(eValue.getCount() > 0) {
                    sum += eValue.getSum();
                    min = min < eValue.getMin() ? min : eValue.getMin();
                    max = max > eValue.getMax() ? max : eValue.getMax();
                    count += eValue.getCount();
                }
            }
        }
        if(count == 0) {
            min = 0;
            avg = 0;
        } else {
            avg = sum / count;
        }

        return AnalyticResponse.createStatisticsResponse().withSum(sum).withAvg(avg).withMax(max).withMin(min).withCount(count).build();
    }

    private Long getKeyFromTimestamp(Long timestamp) {
        return (timestamp * cache.getCapacity()) / Constants.ONE_MINUTE_IN_MS;
    }

    public void clearCache() {
        cache.clear();
    }
}
