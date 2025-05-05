package com.pan.drools.metric;

/**
 * @author panjb
 */
public interface ValueExtractor {

    String extract(AccessLog accessLog, String expression);
}
