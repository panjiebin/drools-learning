package com.pan.drools.metric;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;

import java.util.HashMap;
import java.util.Map;

/**
 * @author panjb
 */
public class AviatorValueExtractor implements ValueExtractor {

    private final AviatorEvaluatorInstance engine = AviatorEvaluator.getInstance();
    private final Map<String, Object> env = new HashMap<>();
    private final static String VAR_LOG = "log";

    @Override
    public String extract(AccessLog accessLog, String expression) {
        Expression exp = engine.compile(expression, true);
        env.put(VAR_LOG, accessLog);
        Object key = exp.execute(env);
        if (key == null) {
            return null;
        }
        return key.toString();
    }
}
