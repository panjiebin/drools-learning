package com.pan.drools.metric.counter;

import com.pan.drools.metric.AccessLog;
import com.pan.drools.metric.MetricCounter;
import com.pan.drools.metric.MetricMeta;

/**
 * @author panjb
 */
public class RequestCounter implements MetricCounter<RequestCounter> {
    private long count;

    @Override
    public void count(AccessLog accessLog, MetricMeta meta) {
        this.count++;
    }

    @Override
    public void merge(RequestCounter other) {
        this.count += other.count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
