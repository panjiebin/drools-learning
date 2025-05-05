package com.pan.drools.metric;

import java.util.Map;

/**
 * @author panjb
 */
public class MetricMeta {
    private String keyExpression;
    private String valueExpression;
    private MetricType type = MetricType.SIMPLE;
    private MetricItem item;
    private Map<String, MetricMeta> children;
    private CompareOperator operator;
    private double threshold;

    public String getKeyExpression() {
        return keyExpression;
    }

    public void setKeyExpression(String keyExpression) {
        this.keyExpression = keyExpression;
    }

    public String getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(String valueExpression) {
        this.valueExpression = valueExpression;
    }

    public MetricType getType() {
        return type;
    }

    public void setType(MetricType type) {
        this.type = type;
    }

    public MetricItem getItem() {
        return item;
    }

    public void setItem(MetricItem item) {
        this.item = item;
    }

    public Map<String, MetricMeta> getChildren() {
        return children;
    }

    public void setChildren(Map<String, MetricMeta> children) {
        this.children = children;
    }

    public CompareOperator getOperator() {
        return operator;
    }

    public void setOperator(CompareOperator operator) {
        this.operator = operator;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
