package com.pan.drools.metric;

/**
 * @author panjb
 */
public interface MergeAble<T> {

    void merge(T other);
}
