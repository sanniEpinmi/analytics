package com.sanni.test.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Analytic {
    private Lock lock = new ReentrantLock();
    private double sum = 0;
    private double max = 0;
    private double min = Double.MAX_VALUE;
    private long count = 0;

    public Analytic() {
    }

    private Analytic(Analytic s) {
        this.sum = s.sum;
        this.max = s.max;
        this.min = s.min;
        this.count = s.count;
    }

    public void updateStatistics(double amount) {
        try{
            lock.lock();
            sum += amount;
            count++;
            min = min < amount ? min : amount;
            max = max > amount ? max : amount;
        }finally {
            lock.unlock();
        }
    }

    public Analytic getStatistics() {
        try{
            lock.lock();
            return new Analytic(this);
        }finally {
            lock.unlock();
        }
    }

    public double getSum() {
        return sum;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }

}
