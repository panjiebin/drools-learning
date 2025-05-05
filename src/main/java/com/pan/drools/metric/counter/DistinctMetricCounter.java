package com.pan.drools.metric.counter;

import com.pan.drools.metric.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author panjb
 */
public class DistinctMetricCounter implements MetricCounter<DistinctMetricCounter> {

    private Set<String> values = new HashSet<>(32);
    private final ValueExtractor valueExtractor = new AviatorValueExtractor();

    @Override
    public void count(AccessLog accessLog, MetricMeta meta) {
        if (values.size() <= meta.getThreshold()) {
            String value = valueExtractor.extract(accessLog, meta.getValueExpression());
            if (StringUtils.isNotBlank(value)) {
                values.add(value);
            }
        }
    }

    @Override
    public void merge(DistinctMetricCounter other) {

    }

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        this.values = values;
    }
}
