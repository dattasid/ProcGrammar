package a.grammar.rule;

import a.grammar.Context;
import a.grammar.common.Action;
import a.grammar.common.ActionProduceRulename;

public class ActionProduceRulenameStep<T extends Context> extends Step<T>
{
    ActionProduceRulename<T> act;
    public ActionProduceRulenameStep(Production<T> parent,
    		ActionProduceRulename<T> act) {
    	super(parent);
        this.act = act;
    }
    
    
    @Override
    public void fire(T ctx) 
    {
        Object name = act.act(ctx);
        parent.parent.parent.rule(name).fire(ctx);
    }
}