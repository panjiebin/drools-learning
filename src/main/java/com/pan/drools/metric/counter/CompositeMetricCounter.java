package com.pan.drools.metric.counter;

import com.pan.drools.metric.AccessLog;
import com.pan.drools.metric.MetricCounter;
import com.pan.drools.metric.MetricMeta;
import com.pan.drools.metric.MetricType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author panjb
 */
public class CompositeMetricCounter implements MetricCounter<CompositeMetricCounter> {

    private Map<String, SubgroupCounter> subgroups = new HashMap<>();
    private Map<String, MetricCounter> simpleCounters = new HashMap<>();

    @Override
    public void count(AccessLog accessLog, MetricMeta meta) {
        Map<String, MetricMeta> children = meta.getChildren();
        for (Map.Entry<String, MetricMeta> entry : children.entrySet()) {
            MetricMeta childMeta = entry.getValue();
            if (MetricType.COMPOSITE == childMeta.getType()) {
                subgroups.computeIfAbsent(entry.getKey(), k -> new SubgroupCounter()).count(accessLog, childMeta);
            } else {
                simpleCounters.computeIfAbsent(entry.getKey(), k -> SimpleMetricCounterFactory.create(childMeta))
                        .count(accessLog, childMeta);
            }
        }
    }

    @Override
    public void merge(CompositeMetricCounter other) {
        Map<String, SubgroupCounter> otherSubgroups = other.getSubgroups();
        if (!otherSubgroups.isEmpty()) {
            for (Map.Entry<String, SubgroupCounter> entry : otherSubgroups.entrySet()) {
                if (this.subgroups.containsKey(entry.getKey())) {
                    this.subgroups.get(entry.getKey()).merge(entry.getValue());
                } else {
                    this.subgroups.put(entry.getKey(), entry.getValue());
                }
            }
        }
        Map<String, MetricCounter> otherSimpleCounters = other.getSimpleCounters();
        if (!otherSimpleCounters.isEmpty()) {
            for (Map.Entry<String, MetricCounter> entry : otherSimpleCounters.entrySet()) {
                if (this.simpleCounters.containsKey(entry.getKey())) {
                    this.simpleCounters.get(entry.getKey()).merge(entry.getValue());
                } else {

                }
            }
        }
    }


    public Map<String, SubgroupCounter> getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(Map<String, SubgroupCounter> subgroups) {
        this.subgroups = subgroups;
    }

    public Map<String, MetricCounter> getSimpleCounters() {
        return simpleCounters;
    }

    public void setSimpleCounters(Map<String, MetricCounter> simpleCounters) {
        this.simpleCounters = simpleCounters;
    }
}
