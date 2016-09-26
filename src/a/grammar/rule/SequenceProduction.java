package a.grammar.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import a.grammar.Context;
import a.grammar.common.Action;
import a.grammar.common.ActionProduceRulename;

public class SequenceProduction<T extends Context> extends Production<T>
{
    private List<Step<T>> steps = new ArrayList<>();
    public SequenceProduction(Rule<T> parent, Object... ruleNames) {
        super(parent);
        for (Object rn : ruleNames)
        {
            steps.add(new RuleStep<T>(this, parent.parent.rule(rn)));
        }
    }
    public SequenceProduction(Rule<T> parent, ActionStep<T> step) {
        super(parent);
        steps.add(step);
    }
    
    public Production<T> then(Object... ruleNames)
    {
        for (Object rn : ruleNames)
        {
            steps.add(new RuleStep<T>(this, parent.parent.rule(rn)));
        }
        return this; 
    }
    
    public Production<T> thenAction(Action<T> act)
    {
        steps.add(new ActionStep<T>(this, act));
        return this; 
    }
    
    public Production<T> thenRunTimeName(ActionProduceRulename<T> act)
    {
        steps.add(new ActionProduceRulenameStep<T>(this, act));
        return this;
    }
    
    @Override
    public void fire(T ctx) {
        for (Step<T> r : getSteps())
        {
            r.fire(ctx);
        }
    }
    @Override
    public String toString() {
        return steps.toString();
    }
    public List<Step<T>> getSteps()
    {
        return Collections.unmodifiableList(steps);
    }
}