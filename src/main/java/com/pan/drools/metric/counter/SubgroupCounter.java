package com.pan.drools.metric.counter;

import com.pan.drools.metric.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author panjb
 */
public class SubgroupCounter implements MetricCounter<SubgroupCounter> {

    private Map<String, CompositeMetricCounter> keyCounters = new HashMap<>();
    private final ValueExtractor valueExtractor = new AviatorValueExtractor();

    @Override
    public void count(AccessLog accessLog, MetricMeta meta) {
        String key = valueExtractor.extract(accessLog, meta.getKeyExpression());
        if (key == null) {
            return;
        }
        keyCounters.computeIfAbsent(key, k -> new CompositeMetricCounter())
                .count(accessLog, meta);
    }

    @Override
    public void merge(SubgroupCounter other) {
        for (Map.Entry<String, CompositeMetricCounter> entry : other.getKeyCounters().entrySet()) {
            if (!keyCounters.containsKey(entry.getKey())) {
                keyCounters.put(entry.getKey(), entry.getValue());
                continue;
            }
            keyCounters.get(entry.getKey()).merge(entry.getValue());
        }
    }

    public Map<String, CompositeMetricCounter> getKeyCounters() {
        return keyCounters;
    }

    public void setKeyCounters(Map<String, CompositeMetricCounter> keyCounters) {
        this.keyCounters = keyCounters;
    }
}
