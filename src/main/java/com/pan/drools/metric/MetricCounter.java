package com.pan.drools.metric;

import java.io.Serializable;

/**
 * @author panjb
 */
public interface MetricCounter<T> extends MergeAble<T>, Serializable {

    void count(AccessLog accessLog, MetricMeta meta);

}
