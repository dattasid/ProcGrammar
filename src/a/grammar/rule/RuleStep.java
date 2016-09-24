package a.grammar.rule;

import a.grammar.Context;

public class RuleStep<T extends Context> extends Step<T> 
{
    public final Rule<T> rule;

    public RuleStep(Production parent, Rule<T> rule)
    {
        super(parent);
        this.rule = rule;
    }
    
//    public RuleStep(RuleName ruleName)
//    {
//        super();
//        this.rule = rule(ruleName);
//    }
    
    @Override
    public void fire(T ctx)
    {
        rule.fire(ctx);
    }
    
    @Override
    public String toString()
    {
        return rule.toString();
    }
}