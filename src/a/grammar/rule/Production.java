package a.grammar.rule;

import a.grammar.Context;
import a.grammar.common.Action;
import a.grammar.common.ActionCount;
import a.grammar.common.ActionProduceRulename;
import a.grammar.common.FixedCount;
import a.grammar.common.FixedWeight;
import a.grammar.util.CalculateBool;
import a.grammar.util.CalculateDouble;
import a.grammar.util.CalculateInt;

public abstract class Production<T extends Context>
{
    Rule<T> parent;
    Weight<T> weight = FixedWeight.DefaultWeight;
    
    public Production(Rule<T> parent) {
        super();
        this.parent = parent;
    }
    
    public Production<T> weight(double weight)
    {
        this.weight = new FixedWeight(this, weight);
        return this;
    }
    
    public Production<T> weight(CalculateDouble<T> wtCalc)
    {
        this.weight = new ActionWeight<T>(this, wtCalc);
        return this;
    }
    
    public Production<T> condition(CalculateBool<T> cond)
    {
        this.weight = new ActionWeight<T>(this, ctx->cond.apply(ctx)?1.:0);
        return this;
    }
    
    public double getWeight(T ctx) {
        return weight.getWeight(ctx);
    }
    
    // delegate for Rule
    public Production<T> produces(Object... ruleNames)
    {
        return parent.produces(ruleNames); 
    }
    public Production<T> producesAction(Action<T> act)
    {
        return parent.producesAction(act); 
    }
    public Production<T> producesRunTimeName(ActionProduceRulename<T> act)
    {
        return parent.producesRunTimeName(act); 
    }
    
    public MultiProduction<T> producesOneOf(Object... ruleNames)
    {
        return parent.producesOneOf(ruleNames); 
    }
    
    public Production<T> fireMultipleTimes(int min, int max)
    {
        parent.fireMultipleTimes(this, new FixedCount<T>(this, min), new FixedCount<T>(this, max));
        return this;
    }
    
    public Production<T> fireMultipleTimes(CalculateInt<T> min, CalculateInt<T> max)
    {
        parent.fireMultipleTimes(this, new ActionCount<T>(this, min), new ActionCount<T>(this, max));
        return this;
    }
    
    public abstract void fire(T ctx);
    
    public abstract Production<T> then(Object... ruleNames);
    public abstract Production<T> thenAction(Action<T> act);
}