package com.pan.drools.metric.counter;

import cn.hutool.core.util.ReflectUtil;
import com.pan.drools.metric.MetricCounter;
import com.pan.drools.metric.MetricItem;
import com.pan.drools.metric.MetricMeta;
import com.pan.drools.metric.MetricType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author panjb
 */
public class SimpleMetricCounterFactory {

    private final static Map<String, Class<? extends MetricCounter>> COUNTERS = new HashMap<>();


    static {
        COUNTERS.put(MetricItem.REQ.name(), RequestCounter.class);
        COUNTERS.put(MetricItem.DISTINCT.name(), DistinctMetricCounter.class);
    }

    public static MetricCounter create(MetricMeta meta) {
        return ReflectUtil.newInstance(COUNTERS.get(meta.getItem().name()));
    }
}
