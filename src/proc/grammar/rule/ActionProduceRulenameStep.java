package proc.grammar.rule;

import proc.grammar.Context;
import proc.grammar.common.Action;
import proc.grammar.common.ActionProduceRulename;

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