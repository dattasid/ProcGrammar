package a.grammar.rule;

import a.grammar.Context;
import a.grammar.rule.Rule;

public class MultiRule<T extends Context>
{
    Rule<T>[] rules;

    @SafeVarargs
    public MultiRule(Rule<T>... rules)
    {
        super();
        this.rules = rules;
    }
    
    public MultiRule<T> pushLeaf(String property, Object obj)
    {
        for (Rule<T> r : rules)
        {
            r.pushLeaf(property, obj);
        }
        return this;
    }
    
    public MultiRule<T> pushLeaf(Object obj)
    {
        for (Rule<T> r : rules)
        {
            r.pushLeaf(Rule.LEAFPROP_INDICATOR, obj);
        }
        return this;
    }
}
