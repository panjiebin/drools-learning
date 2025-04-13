package com.pan.drools;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author panjb
 */
public class MatchedRuleListener extends DefaultAgendaEventListener {

    public final List<String> matchedRules = new ArrayList<>();

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        matchedRules.add(event.getMatch().getRule().getName());
    }
}
