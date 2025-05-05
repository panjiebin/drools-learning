package com.pan.drools.metric;

import com.pan.drools.metric.counter.CompositeMetricCounter;
import com.pan.drools.metric.counter.RequestCounter;
import com.pan.drools.metric.counter.SubgroupCounter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author panjb
 */
public class Test {

    public static void main(String[] args) {
        CompositeMetricCounter domainCounter = new CompositeMetricCounter();
        Map<String, SubgroupCounter> subgroupCounters = new HashMap<>();
        subgroupCounters.put("JA3", new SubgroupCounter());
        Map<String, MetricCounter> simpleCounters = new HashMap<>();
        simpleCounters.put("m_1", new RequestCounter());
        domainCounter.setSubgroups(subgroupCounters);
        domainCounter.setSimpleCounters(simpleCounters);

        MetricMeta root = new MetricMeta();
        root.setType(MetricType.COMPOSITE);
        Map<String, MetricMeta> children = new HashMap<>();

        MetricMeta ja3Meta = new MetricMeta();
        ja3Meta.setType(MetricType.COMPOSITE);
        ja3Meta.setKeyExpression("log.ja3");

        Map<String, MetricMeta> ja3Children = new HashMap<>();
        MetricMeta ja3Child1 = new MetricMeta();
        ja3Child1.setItem(MetricItem.REQ);
        ja3Children.put("ja3_m_1", ja3Child1);

        MetricMeta ja3Child2 = new MetricMeta();
        ja3Child2.setItem(MetricItem.DISTINCT);
        ja3Child2.setValueExpression("log.url");
        ja3Children.put("ja3_m_2", ja3Child2);

        ja3Meta.setChildren(ja3Children);

        children.put("JA3", ja3Meta);

        MetricMeta dMeat = new MetricMeta();
        dMeat.setItem(MetricItem.REQ);
        children.put("d_m_1", dMeat);
        root.setChildren(children);

        AccessLog accessLog = new AccessLog();
        accessLog.setDomain("www.baidu.com");
        accessLog.setIp("127.0.0.1");
        accessLog.setJa3("jacjacjdfsddf");
        accessLog.setUrl("/home");
        CompositeMetricCounter c1 = new CompositeMetricCounter();
        CompositeMetricCounter c2 = new CompositeMetricCounter();
        c1.count(accessLog, root);
        c2.count(accessLog, root);
        c1.merge(c2);
        System.out.println("compositeCounter = " + c1);
    }
}
